package com.byu.pmedia.tasks;

import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.model.StillFaceModel;
import javafx.concurrent.Task;

public class StillFaceSyncTask implements IStillFaceTask {

    private StillFaceTaskCallback callback;

    public StillFaceSyncTask(StillFaceTaskCallback callback){
        this.callback = callback;
    }

    @Override
    public void execute() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                synchronize();
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

    private void synchronize() throws Exception{
        PMLogger.getInstance().info("Synchronizing with database");
        boolean success = StillFaceModel.getInstance().refresh();
        if(!success){
            PMLogger.getInstance().error("Could not refresh the model");
            throw new Exception("Failed to refresh in memory data. See log for more details.");
        }
    }
}
