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
import com.byu.pmedia.model.StillFaceData;
import com.byu.pmedia.model.StillFaceModel;
import javafx.concurrent.Task;

import java.util.Map;

/**
 * StillFaceSaveTask
 * Implementation of the IStillFaceTask interface. Wraps the execution of saving changes made to the internal data of the
 * DataCenter to the database. This task is executed on a separate thread from the GUI.
 *
 * @author Braden Hitchcock
 */
public class StillFaceSaveTask implements IStillFaceTask {

    /* An implementation of the callback methods provided by the developer */
    private StillFaceTaskCallback callback;
    /* The database access object used in saving the data */
    private StillFaceDAO dao;

    public StillFaceSaveTask(StillFaceTaskCallback callback) {
        this.callback = callback;
        this.dao = StillFaceDAO.generateFromConfig();
    }

    /**
     * Executes the task. Attempts to save data from the map holding edits in the StillFaceModel. If it fails, it
     * will call the developer's provided onSuccess() method. If it fails, it will call the developer's provided
     * onFail() method.
     */
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

    /**
     * Takes the updated data in the map in the StillFaceModel and writes the changes to the database.
     *
     * @throws Exception If an error occurs, this exception will trigger the Thread's failed() method, in turn calling
     *                   the developer's implementation of the onFail() method.
     */
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
