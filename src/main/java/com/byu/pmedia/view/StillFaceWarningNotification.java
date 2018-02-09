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

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * StillFaceWarningNotification
 * Provides a simple wrapper around displaying a JavaFX warning dialog to the user.
 *
 * @author Braden Hitchcock
 */
public class StillFaceWarningNotification implements IStillFaceNotification {

    /* The JavaFX alert to show */
    private Alert alert = new Alert(Alert.AlertType.WARNING);

    /**
     * Constructs a new warning dialog
     *
     * @param message The message to show to the user
     */
    public StillFaceWarningNotification(String message){
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        ((Stage)alert.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
    }

    /**
     * Displays the warning dialog to the user. Halts execution of the current thread until the user closes the
     * dialog.
     */
    @Override
    public void show() {
        alert.showAndWait();
    }
}