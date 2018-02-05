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
 * StillFaceErrorNotification
 * Provides a simple wrapper around a JavaFX error notification for simple and easy incorporation into the DataCenter.
 *
 * @author Braden Hitchcock
 */
public class StillFaceErrorNotification implements IStillFaceNotification {

    /* The JavaFX alert object */
    private Alert alert = new Alert(Alert.AlertType.ERROR);

    /**
     * Constructs a new instance of the error dialog
     *
     * @param message The error message to show to the user
     */
    public StillFaceErrorNotification(String message){
        alert.setContentText(message);
        alert.setTitle("Datacenter encountered an error");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        ((Stage)alert.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
    }

    /**
     * Displays the dialog and halts the current execution of the thread. Waits for the user to close the dialog
     * before resuming execution.
     */
    @Override
    public void show() {
        alert.showAndWait();
    }
}
