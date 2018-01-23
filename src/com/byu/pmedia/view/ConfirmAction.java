package com.byu.pmedia.view;

/**
 * This is a callback interface. The user must specify the functions below when making a choice so as to
 * determine the actions to take after a Confirmation Notification
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
