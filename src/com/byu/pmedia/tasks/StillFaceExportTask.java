package com.byu.pmedia.tasks;

import com.byu.pmedia.model.StillFaceModel;
import com.byu.pmedia.model.StillFaceVideoData;
import com.byu.pmedia.parser.StillFaceCSVParser;
import javafx.concurrent.Task;


public class StillFaceExportTask implements IStillFaceTask {

    private String filepath;
    private StillFaceTaskCallback callback;

    public StillFaceExportTask(String filepath, StillFaceTaskCallback callback){
        this.filepath = filepath;
        this.callback = callback;
    }

    @Override
    public void execute() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                exportData();
                return null;
            }

            @Override
            protected void succeeded(){
                callback.onSuccess();
                super.succeeded();
            }

            @Override
            protected void failed(){
                callback.onFail(this.getException());
                super.failed();
            }
        };
        new Thread(task).start();
    }

    private void exportData() throws Exception{
        boolean success = new StillFaceCSVParser().serializeToCSVFromCodedVideoData(
                new StillFaceVideoData(StillFaceModel.getInstance().getVisibleDataList()), this.filepath);
        if(!success){
            throw new Exception("Failed to export data. See log for more information.");
        }
    }
}
