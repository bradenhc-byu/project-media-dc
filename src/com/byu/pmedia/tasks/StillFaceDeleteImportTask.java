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
 * StillFaceDeleteImportTask
 * Implementation of the IStillFaceTask interface. Wraps the execution of deleting data associated with a single
 * import from the database into a single task. This task is executed on a separate thread from the GUI.
 *
 * @author Braden Hitchcock
 */
public class StillFaceDeleteImportTask implements IStillFaceTask {

    /* The import data that has the ID reference to delete */
    private StillFaceImport importData;
    /* The database access object used to delete the import */
    private StillFaceDAO dao;
    /* The callback object whose implementation is provided by the user */
    private StillFaceTaskCallback callback;

    public StillFaceDeleteImportTask(StillFaceImport importData, StillFaceTaskCallback callback){
        this.importData = importData;
        this.dao = StillFaceDAO.generateFromConfig();
        this.callback = callback;
    }

    /**
     * Executes the task, attempting to delete anything relating to the id of the StillFaceImport object passed in the
     * constructor
     */
    @Override
    public void execute() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                onDeleteImport();
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
     * Where the deletion actually happens. Attempts to delete all data entries with an iid matching the StillFaceImport
     * object passed in to the constructor.
     *
     * @throws Exception If the deletion fails, throw an exception so that the thread will execute the onFail() method
     *                   from the callback provided by the developer
     */
    private void onDeleteImport() throws Exception {
        PMLogger.getInstance().debug("Executing deleting import task...");
        dao.lockConnection();
        boolean success = dao.cleanImportData(importData.getImportID());
        if(success){
            PMLogger.getInstance().debug("Delete import task completed...");
            StillFaceModel.getInstance().refresh();
        }
        else{
            PMLogger.getInstance().error("Unable to delete import data from database");
            throw new Exception("Import failed while loading file information");
        }
        dao.unlockConnection();
    }
}