package com.byu.pmedia.tasks;

import com.byu.pmedia.database.StillFaceDAO;
import com.byu.pmedia.model.StillFaceData;
import com.byu.pmedia.model.StillFaceModel;
import javafx.concurrent.Task;

import java.util.Map;

public class StillFaceSaveTask implements IStillFaceTask {

    private StillFaceTaskCallback callback;
    private StillFaceDAO dao;

    public StillFaceSaveTask(StillFaceTaskCallback callback) {
        this.callback = callback;
        this.dao = StillFaceDAO.generateFromConfig();
    }

    @Override
    public void execute() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                saveData();
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

    private void saveData() throws Exception {
        Map<Integer, StillFaceData> dataMap = StillFaceModel.getInstance().getEditedDataMap();
        if(StillFaceModel.getInstance().getEditedDataMap().size() != 0){
            for(int key : dataMap.keySet()){
                boolean success = dao.updateCodeData(dataMap.get(key));
                if(!success){
                    throw new Exception("Failed to update data in database. See log for more details.");
                }
            }
            StillFaceModel.getInstance().clearEdits();
            StillFaceModel.getInstance().refreshCodeData();
        }
    }
}
