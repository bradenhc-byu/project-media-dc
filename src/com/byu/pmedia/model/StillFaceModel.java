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

    /**
     * Provides access to an indexed collection of import data entries from the database. If the model is cached, these
     * will be in-memory. Otherwise the model will query the database to get the information.
     * <p>
     * The IndexedCollection objects are a part of the CQEngine implementation on GitHub. They provide extremely
     * fast, SQL-like access to internal data structures.
     *
     * @return An IndexedCollection of StillFaceImport objects representing the entries in the database
     */
    public IndexedCollection<StillFaceImport> getImportDataCollection() {
        if(this.initialized) {
            return (this.cached) ? importDataCollection : this.dao.getImportData(0);
        }
        return null;
    }

    /**
     * Provides access to an indexed collection of video data entries from the database. If the model is cached, these
     * will be in-memory. Otherwise the model will query the database to get the information.
     * <p>
     * The IndexedCollection objects are a part of the CQEngine implementation on GitHub. They provide extremely
     * fast, SQL-like access to internal data structures.
     *
     * @return An IndexedCollection of StillFaceData objects representing the entries in the database
     */
    public IndexedCollection<StillFaceData> getDataCollection() {
        if(this.initialized) {
            return (this.cached) ? dataCollection : this.dao.getCodeDataFromImport(0);
        }
        return null;
    }

    /**
     * Provides access to an indexed collection of code data entries from the database. If the model is cached, these
     * will be in-memory. Otherwise the model will query the database to get the information.
     * <p>
     * The IndexedCollection objects are a part of the CQEngine implementation on GitHub. They provide extremely
     * fast, SQL-like access to internal data structures.
     *
     * @return An IndexedCollection of StillFaceCode objects representing the entries in the database
     */
    public IndexedCollection<StillFaceCode> getCodeCollection() {
        if(this.initialized) {
            return (this.cached) ? codeCollection : this.dao.getCode(0);
        }
        return null;
    }

    /**
     * Provides access to an indexed collection of tag data entries from the database. If the model is cached, these
     * will be in-memory. Otherwise the model will query the database to get the information.
     * <p>
     * The IndexedCollection objects are a part of the CQEngine implementation on GitHub. They provide extremely
     * fast, SQL-like access to internal data structures.
     *
     * @return An IndexedCollection of StillFaceTag objects representing the entries in the database
     */
    public IndexedCollection<StillFaceTag> getTagCollection() {
        if(this.initialized){
            return (this.cached) ? tagCollection : this.dao.getTag(0);
        }
        return null;
    }

    /**
     * Provides access to a list of all the code entries in the database. This provides easy access for populating
     * ChoiceBox objects and other lists in the GUI.
     *
     * @return A list of StillFaceCode objects representing all the available codes in the database
     */
    public static List<StillFaceCode> getCodeList(){ return codeList; }

    /**
     * Provides access to a list of all the tag entries in the database. This provides easy access for populating
     * ChoiceBox objects and other lists in the GUI.
     *
     * @return A list of StillFaceTag objects representing all the available tags in the database
     */
    public static List<StillFaceTag> getTagList() { return tagList; }

    /**
     * Provides access to the list of data visible on the GUI. This list is composed of StillFaceData objects based on
     * a specific import or on a search query and is used in exporting the data to a file.
     *
     * @return A list of the StillFaceData objects visible on the GUI
     */
    public List<StillFaceData> getVisibleDataList(){ return visibleDataList; }

    /**
     * Sets the visible data to the provided list
     *
     * @param data A list of StillFaceData objects that are visible from the GUI
     */
    public void setVisibleData(List<StillFaceData> data){
        visibleDataList = data;
        setChanged();
    }

    /**
     * Provides access to the currently selected import in the GUI
     *
     * @return A StillFaceImport object representing the selected import from the list in the GUI
     */
    public StillFaceImport getVisibleImport() {
        return visibleImport;
    }

    /**
     * Sets the currently selected/visible import on the GUI
     *
     * @param importData The StillFaceImport object representing the visible and selected object in the GUI
     */
    public void setVisibleImport(StillFaceImport importData){
        this.visibleImport = importData;
        setChanged();
    }

    /**
     * Provides access to all the unsaved edits made by the user
     *
     * @return A map of edited StillFaceData objects with their ids being the key
     */
    public Map<Integer, StillFaceData> getEditedDataMap(){ return editedDataMap; }

    /**
     * Adds or updates an edited StillFaceData object in the map that is tracking the changes.
     *
     * @param data The newly updated object to add
     */
    public void addEditedData(StillFaceData data){
        editedDataMap.put(data.getDataID(), data);
        setChanged();
    }

    /**
     * After the edits have been saved, or the user discards the changes with a sync request, this method helps clear
     * the edits that were in the map.
     */
    public void clearEdits(){
        editedDataMap.clear();
        setChanged();
    }

    /**
     * Contacts the database and re-populates the internal cache of import data. If the data in not cached, nothing
     * happens since the data is obtained by communicating with the database directly.
     *
     * @return True if the refresh succeeds or the data is not cached, false otherwise
     */
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

    /**
     * Contacts the database and re-populates the internal cache of video data. If the data in not cached, nothing
     * happens since the data is obtained by communicating with the database directly.
     *
     * @return True if the refresh succeeds or the data is not cached, false otherwise
     */
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

    /**
     * Contacts the database and re-populates the internal cache of code data. If the data in not cached, nothing
     * happens since the data is obtained by communicating with the database directly.
     *
     * @return True if the refresh succeeds or the data is not cached, false otherwise
     */
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

    /**
     * Contacts the database and re-populates the internal cache of tag data. If the data in not cached, nothing
     * happens since the data is obtained by communicating with the database directly.
     *
     * @return True if the refresh succeeds or the data is not cached, false otherwise
     */
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

    /**
     * Contacts the database and re-populates the internal caches of all data in the model. If the data in not cached,
     * nothing happens since the data is obtained by communicating with the database directly.
     *
     * @return True if the refresh succeeds or the data is not cached, false otherwise
     */
    public boolean refresh(){
        this.editedDataMap.clear();
        this.visibleImport = null;
        this.visibleDataList.clear();
        setChanged();
        return refreshImportData() && refreshCodeData() && refreshCodes() && refreshTags();
    }

    /**
     * Updates the statically available code list with the latest data from the database
     */
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

    /**
     * Updates the statically available tag list with the latest data from the database
     */
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

    /**
     * Locks the connection used by the internal DAO of the model. This helps reduce the number of times the model has
     * to connect to the database when making consecutive requests.
     */
    public void lockDatabaseConnection(){
        this.dao.lockConnection();
    }

    /**
     * Unlocks the connection used by the internal DAO of the model.
     */
    public void unlockDatabaseConnection(){
        this.dao.unlockConnection();
    }
}
