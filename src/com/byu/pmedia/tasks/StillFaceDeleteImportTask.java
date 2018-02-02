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

public class StillFaceDeleteImportTask implements IStillFaceTask {

    private StillFaceImport importData;
    private StillFaceDAO dao;
    private StillFaceTaskCallback callback;

    public StillFaceDeleteImportTask(StillFaceImport importData, StillFaceTaskCallback callback){
        this.importData = importData;
        this.dao = StillFaceDAO.generateFromConfig();
        this.callback = callback;
    }

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