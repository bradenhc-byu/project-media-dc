/*
 * ---------------------------------------------------------------------------------------------------------------------
 *                            Brigham Young University - Project MEDIA StillFace DataCenter
 * ---------------------------------------------------------------------------------------------------------------------
 * The contents of this file contribute to the ProjectMEDIA DataCenter for managing and analyzing data obtained from the
 * results of StillFace observational experiments.
 *
 * This code is free, open-source software. You may distribute or modify the code, but Brigham Young University or any
 * parties involved in the development and production of this code as downloaded from the remote repository are not
 * responsible for any repercussions that come as a result of the modifications.
 */
package com.byu.pmedia.model;

import com.byu.pmedia.config.StillFaceConfig;
import com.byu.pmedia.database.StillFaceDAO;
import com.byu.pmedia.log.PMLogger;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.Query;

import java.sql.SQLException;
import java.util.*;

import static com.googlecode.cqengine.query.QueryFactory.*;

/**
 * StillFaceModel
 * Data structure for keeping track of database data and user edits in memory. The This model is populated from the
 * Project MEDIA database and, depending on user caching configuration, can hold all of the data in the database or
 * only the data requested by the user.
 *
 * @author Braden Hitchcock
 */
public class StillFaceModel extends Observable {

    private StillFaceDAO dao;               // Database Access Object for the model
    private boolean cached;                 // If true, the sf_data table is cached in memory to this object at initialization
                                            // and retrieving data from the model will not refer to the database server
    private boolean initialized = false;    // Flag to check if the model has been initialized

    /* Collections that hold data. They are CQEngine IndexedCollections, allowing for extremely fast data querying
     * and manipulation (SQL-like interactions with in-memory data structures. */
    private IndexedCollection<StillFaceImport> importDataCollection = new ConcurrentIndexedCollection<>();
    private IndexedCollection<StillFaceData> dataCollection = new ConcurrentIndexedCollection<>();
    private IndexedCollection<StillFaceCode> codeCollection = new ConcurrentIndexedCollection<>();
    private IndexedCollection<StillFaceTag> tagCollection = new ConcurrentIndexedCollection<>();

    /* These static lists contain the Codes and Tags used by the program and allow lists of the available codes and
     * tags to be produced easily */
    private static List<StillFaceCode> codeList = new ArrayList<>();
    private static List<StillFaceTag> tagList = new ArrayList<>();

    /* This map contains edits made to StillFace data by the user, providing a way to track what changes are made and
     * update the database when the user saves their changes. Placing the map here in the model allows it to be
     * accessible from multiple places in the program via the singleton pattern, meaning that we can register
     * keyboard shortcuts (such as Ctrl+s) that can perform actions on it. */
    private Map<Integer, StillFaceData> editedDataMap = new HashMap<>();

    /* This map contains the data that is currently visible to the user as an array. Useful in allowing the data to
     * be exported as a CSV file or be plotted when the user switches to plot mode. */
    private List<StillFaceData> visibleDataList = new ArrayList<>();

    /* This StillFaceImport object holds information about the currently visible import in the data table */
    private StillFaceImport visibleImport = null;

    // The SINGLETON instance of the model
    private static StillFaceModel singleton;

    /**
     * Static method that will return the singleton instance of the data model. If the instance has not been created
     * yet, it will create the instance before returning a reference to it.
     *
     * @return A reference to the StillFaceModel singleton instance
     */
    public static StillFaceModel getInstance(){
        if(singleton == null){
            singleton = new StillFaceModel();
        }
        return singleton;
    }

    /**
     * Populates the internal data structures of the model using information from the database.
     *
     * @param dao The data access object uses to access the database
     *
     * @return True if the initialization was successful, false otherwise
     */
    public boolean initialize(StillFaceDAO dao){
        try {
            this.dao = dao;
            this.cached = StillFaceConfig.getInstance().getAsBoolean("model.cache");
            if(this.cached){
                this.dao.lockConnection();
                this.importDataCollection = this.dao.getImportData(0);
                this.codeCollection = this.dao.getCode(0);
                this.tagCollection = this.dao.getTag(0);
                this.dataCollection = this.dao.getCodeDataFromImport(0);
                this.dao.unlockConnection();
                this.dao.closeConnection();
            }
            this.initialized = true;
            populateCodeList();
            populateTagList();
            return true;
        }
        catch (SQLException e){
            PMLogger.getInstance().error("Error initializing model: " + e.getMessage());
            return false;
        }
    }

    public IndexedCollection<StillFaceImport> getImportDataCollection() {
        if(this.initialized) {
            return (this.cached) ? importDataCollection : this.dao.getImportData(0);
        }
        return null;
    }

    public IndexedCollection<StillFaceData> getDataCollection() {
        if(this.initialized) {
            return (this.cached) ? dataCollection : this.dao.getCodeDataFromImport(0);
        }
        return null;
    }

    public IndexedCollection<StillFaceCode> getCodeCollection() {
        if(this.initialized) {
            return (this.cached) ? codeCollection : this.dao.getCode(0);
        }
        return null;
    }

    public IndexedCollection<StillFaceTag> getTagCollection() {
        if(this.initialized){
            return (this.cached) ? tagCollection : this.dao.getTag(0);
        }
        return null;
    }

    public static List<StillFaceCode> getCodeList(){ return codeList; }

    public static List<StillFaceTag> getTagList() { return tagList; }

    public List<StillFaceData> getVisibleDataList(){ return visibleDataList; }

    public void setVisibleData(List<StillFaceData> data){
        visibleDataList = data;
        setChanged();
    }

    public StillFaceImport getVisibleImport() {
        return visibleImport;
    }

    public void setVisibleImport(StillFaceImport importData){
        this.visibleImport = importData;
        setChanged();
    }

    public Map<Integer, StillFaceData> getEditedDataMap(){ return editedDataMap; }

    public void addEditedData(StillFaceData data){
        editedDataMap.put(data.getDataID(), data);
        setChanged();
    }

    public void clearEdits(){
        editedDataMap.clear();
        setChanged();
    }

    public boolean refreshImportData(){
        if(this.initialized && this.cached){
            IndexedCollection<StillFaceImport> tmpCollection = this.dao.getImportData(0);
            if(tmpCollection != null) {
                this.importDataCollection = tmpCollection;
                setChanged();
                return true;
            }
        }
        if(!this.initialized || this.cached) PMLogger.getInstance().warn("Failed to refresh imports");
        return !this.cached;
    }

    public boolean refreshCodeData(){
        if(this.initialized && this.cached){
            IndexedCollection<StillFaceData> tmpCollection = this.dao.getCodeDataFromImport(0);
            if(tmpCollection != null){
                this.dataCollection = tmpCollection;
                setChanged();
                return true;
            }

        }
        if(!this.initialized || this.cached) PMLogger.getInstance().warn("Failed to refresh data");
        return !this.cached;
    }

    public boolean refreshCodes(){
        if(this.initialized && this.cached){
            IndexedCollection<StillFaceCode> tmpCollection = this.dao.getCode(0);
            if(tmpCollection != null) {
                this.codeCollection = tmpCollection;
                populateCodeList();
                setChanged();
                return true;
            }
        }
        if(!this.initialized || this.cached) PMLogger.getInstance().warn("Failed to refresh codes");
        return !this.cached;
    }

    public boolean refreshTags(){
        if(this.initialized && this.cached){
            IndexedCollection<StillFaceTag> tmpCollection = this.dao.getTag(0);
            if(tmpCollection != null){
                this.tagCollection = tmpCollection;
                populateTagList();
                setChanged();
                return true;
            }
        }
        if(!this.initialized || this.cached) PMLogger.getInstance().warn("Failed to refresh tags");
        return !this.cached;
    }

    public boolean refresh(){
        this.editedDataMap.clear();
        this.visibleImport = null;
        this.visibleDataList.clear();
        setChanged();
        return refreshImportData() && refreshCodeData() && refreshCodes() && refreshTags();
    }

    private void populateCodeList(){
        if(initialized) {
            codeList.clear();
            IndexedCollection<StillFaceCode> codeCollection = (this.cached) ? this.codeCollection : this.dao.getCode(0);
            Query<StillFaceCode> query = not(equal(StillFaceCode.CODE_ID, 0));
            for (StillFaceCode c : codeCollection.retrieve(query,
                    queryOptions(orderBy(ascending(StillFaceCode.NAME))))) {
                codeList.add(c);
            }
            setChanged();
        }
    }

    private void populateTagList(){
        if(initialized) {
            tagList.clear();
            IndexedCollection<StillFaceTag> tagCollection = (this.cached) ? this.tagCollection : this.dao.getTag(0);
            Query<StillFaceTag> query = not(equal(StillFaceTag.TAG_ID, 0));
            for (StillFaceTag t : tagCollection.retrieve(query,
                    queryOptions(orderBy(ascending(StillFaceTag.TAG_VALUE))))) {
                tagList.add(t);
            }
            setChanged();
        }
    }

    public void lockDatabaseConnection(){
        this.dao.lockConnection();
    }

    public void unlockDatabaseConnection(){
        this.dao.unlockConnection();
    }
}
