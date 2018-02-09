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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * StillFaceConfirmNotification
 * Wraps the display of a JavaFX confirm dialog. Also provides simple implementation of the user pressing
 * 'OK' or 'Cancel'
 *
 * @author Braden Hitchcock
 */
public class StillFaceConfirmNotification implements IStillFaceNotification {

    /* New buttons to show in the dialog */
    private ButtonType confirmButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
    private ButtonType cancelButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

    /* The JavaFX alert */
    private Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", confirmButton, cancelButton);

    /* The callback implementation provided by the developer */
    private ConfirmAction callback;

    /**
     * Constructs a new instance of a confirmation dialog
     *
     * @param message The message text to show in the confirmation dialog
     * @param action An implementation of the ConfirmAction callback to respond to the user choice
     */
    public StillFaceConfirmNotification(String message, ConfirmAction action){
        alert.setTitle("Confirmation Required");
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        ((Stage)alert.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
        this.callback = action;
    }

    /**
     * Displays the message while halting the currently executing thread. Waits for a response before starting the
     * thread up again.
     */
    @Override
    public void show() {
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == confirmButton){
            this.callback.onOK();
        }
        else{
            this.callback.onCancel();
        }
    }
}
