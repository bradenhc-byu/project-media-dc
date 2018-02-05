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
package com.byu.pmedia.tasks;

import com.byu.pmedia.database.StillFaceDAO;
import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.model.StillFaceData;
import com.byu.pmedia.model.StillFaceImport;
import com.byu.pmedia.model.StillFaceModel;
import com.byu.pmedia.model.StillFaceVideoData;
import com.byu.pmedia.parser.StillFaceCSVParser;
import javafx.concurrent.Task;

import java.io.File;

/**
 * StillFaceImportTask
 * Implementation of the IStillFaceTask interface. Wraps the execution of importing new still face data from a CSV file
 * into the database. This task is executed on a separate thread from the GUI.
 *
 * @author Braden Hitchcock
 */
public class StillFaceImportTask implements IStillFaceTask {

    /* The file to import data from (CSV) */
    private File importFile;
    /* The import data object containing information about he new import entry */
    private StillFaceImport importData;
    /* The database access object used to modify the database */
    private StillFaceDAO dao;
    /* Callback functionality provided by the developer */
    private StillFaceTaskCallback callback;

    public StillFaceImportTask(File importFile, StillFaceImport importData, StillFaceTaskCallback callback){
        this.importFile = importFile;
        this.importData = importData;
        this.dao = StillFaceDAO.generateFromConfig();
        this.callback = callback;
    }

    /**
     * Execute the task. Attempts to import new data into the database from a CSV file
     */
    @Override
    public void execute() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                onImportFromFile();
                return null;
            }

            @Override
            protected void succeeded() {
                callback.onSuccess();
                super.succeeded();
            }

            @Override
            protected void failed() {
                callback.onFail(this.getException());
                super.failed();
            }
        };
        new Thread(task).start();
    }

    /**
     * Where the import happens. Takes the data from a CSV file and populates a list of StillFaceData objects. Then
     * it will attempt to first write the import data to the database. After successfully inserting a new import
     * entry, it will use the generated key to populate the import id field of the StillFaceData objects and then
     * write them to the database. If there is an error encountered during the process, it will rollback the changes
     * to the state of the database before the attempt to import occurred.
     *
     * @throws Exception If an error occurs, this will trigger the failed() method in the thread to be called and the
     *                   developer's provided onFail() implementation to be called
     */
    private void onImportFromFile() throws Exception {
        PMLogger.getInstance().debug("Performing import task...");
        StillFaceVideoData videoData = new StillFaceVideoData();
        new StillFaceCSVParser().parseFromCSVIntoCodedVideoData(importFile.getAbsolutePath(), videoData);
        dao.lockConnection();
        int key = dao.insertImportData(importData);
        if(key > 0){
            PMLogger.getInstance().debug("Import entry created, importing data...");
            for(StillFaceData data : videoData.getData()){
                data.setImportID(key);
                int dKey = dao.insertCodeData(data);
                if(dKey < 0){
                    dao.cleanImportData(key);
                    throw new Exception("Import data failed while importing file");
                }
            }
            StillFaceModel.getInstance().refreshImportData();
            StillFaceModel.getInstance().refreshCodeData();
            StillFaceModel.getInstance().refreshCodes();
            PMLogger.getInstance().debug("Import task completed");
        }
        else{
            PMLogger.getInstance().error("Unable to insert data into database");
            throw new Exception("Import failed while loading file information");
        }
        dao.unlockConnection();
    }
}
