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

import com.byu.pmedia.model.StillFaceModel;
import javafx.concurrent.Task;

import java.util.logging.Logger;

/**
 * StillFaceSyncTask
 * Implementation of the IStillFaceTask interface. Wraps the execution of syncing the internal data models with the
 * latest data in the database. This task is executed on a separate thread from the GUI.
 *
 * @author Braden Hitchcock
 */
public class StillFaceSyncTask implements IStillFaceTask {

    /* Grab an instance of the logger */
    private final static Logger logger =Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /* Implementation of the callback provided by the developer */
    private StillFaceTaskCallback callback;

    public StillFaceSyncTask(StillFaceTaskCallback callback){
        this.callback = callback;
    }

    /**
     * Executes the task. Attempts to refresh the internal data model with the information in the database. If it fails,
     * the failed() method will be called and the developer's provided onFail() method will be triggered. If it
     * succeeds, the succeeded() method of the Task class will be called and the developer's onSuccess() method will
     * be subsequently triggered.
     */
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

    /**
     * Attempts to synchronize the internal data model with the data in the database. On successfully doing so, it will
     * restore the model to the startup state, meaning that edits will be lost and the visible data will be cleared.
     *
     * @throws Exception If an error occurs, this exception triggers the Task's failed() method, which will in turn call
     *                   the onFail() method provided by the developer
     */
    private void synchronize() throws Exception{
        logger.info("Synchronizing with database");
        boolean success = StillFaceModel.getInstance().refresh();
        if(!success){
            logger.warning("Could not refresh the model");
            throw new Exception("Failed to refresh in memory data. See log for more details.");
        }
    }
}
