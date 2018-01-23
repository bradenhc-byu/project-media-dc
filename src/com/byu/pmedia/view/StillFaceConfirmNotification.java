package com.byu.pmedia.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Optional;

public class StillFaceConfirmNotification implements IStillFaceNotification {

    private ButtonType confirmButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
    private ButtonType cancelButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
    private Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", confirmButton, cancelButton);
    private ConfirmAction callback;

    public StillFaceConfirmNotification(String message, ConfirmAction action){
        alert.setTitle("Confirmation Required");
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        ((Stage)alert.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
        this.callback = action;
    }

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
