package com.byu.pmedia.tasks;

import com.byu.pmedia.model.StillFaceData;
import com.byu.pmedia.model.StillFaceVideoData;
import com.byu.pmedia.parser.StillFaceCSVParser;
import javafx.concurrent.Task;

import java.util.List;

public class StillFaceExportTask implements IStillFaceTask {

    private List<StillFaceData> data;
    private String filepath;
    private StillFaceTaskCallback callback;

    public StillFaceExportTask(List<StillFaceData> data, String filepath, StillFaceTaskCallback callback){
        this.data = data;
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
                new StillFaceVideoData(this.data), this.filepath);
        if(!success){
            throw new Exception("Failed to export data. See log for more information.");
        }
    }
}
