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
package com.byu.pmedia.view;

/**
 * IStillFaceNotification
 * Provides the methods that must be implemented by all notifications originating from the application
 *
 * @author Braden Hitchcock
 */
public interface IStillFaceNotification {

    /**
     * Displays the notification for the user to see
     */
    void show();

}
