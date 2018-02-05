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

/**
 * StillFaceTaskCallback
 * Provides an interface the developer can implement to control what should happen after a task has succeeded or
 * failed.
 *
 * @author Braden Hitchcock
 */
public interface StillFaceTaskCallback {

    /**
     * When the JavaFX Task has completed, this method will be called when a StillFaceTaskCallback is passed to
     * the appropriate StillFaceTask object.
     */
    void onSuccess();

    /**
     * When the JavaFX Task fails (i.e. and exception is caught from inside), this method will be called and the
     * task will terminate.
     * @param exception The exception caught by the task
     */
    void onFail(Throwable exception);

}
