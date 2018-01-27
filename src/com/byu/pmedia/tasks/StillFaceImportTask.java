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

public class StillFaceImportTask implements IStillFaceTask {

    private File importFile;
    private StillFaceImport importData;
    private StillFaceDAO dao;
    private StillFaceTaskCallback callback;

    public StillFaceImportTask(File importFile, StillFaceImport importData, StillFaceTaskCallback callback){
        this.importFile = importFile;
        this.importData = importData;
        this.dao = StillFaceDAO.generateFromConfig();
        this.callback = callback;
    }

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

    private void onImportFromFile() throws Exception {
        StillFaceVideoData videoData = new StillFaceVideoData();
        new StillFaceCSVParser().parseFromCSVIntoCodedVideoData(importFile.getAbsolutePath(), videoData);
        dao.lockConnection();
        int key = dao.insertImportData(importData);
        if(key > 0){
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
        }
        else{
            PMLogger.getInstance().error("Unable to insert data into database");
            throw new Exception("Import failed while loading file information");
        }
        dao.unlockConnection();
    }
}
