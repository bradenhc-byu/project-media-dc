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
 * IStillFaceTask
 * Interface that all tasks used in the DataCenter should implement. Essential provides the ability for the
 * command pattern to be utilized, although in this current version that pattern is not implemented.
 *
 * @author Braden Hitchcock
 */
public interface IStillFaceTask {

    /**
     * When called, executes the code inside the task that implements this interface
     */
    void execute();
}
