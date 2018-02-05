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
 * ConfirmAction
 * This is a callback interface. The user must specify the functions below when making a choice so as to
 * determine the actions to take after a Confirmation Notification
 *
 * @author Braden Hitchcock
 */
public interface ConfirmAction {
    /**
     * Function to be run if the user presses the OK button
     */
    void onOK();

    /**
     * Function to be run if the user presses the Cancel button
     */
    void onCancel();
}
