package com.byu.pmedia.view;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class StillFaceWarningNotification implements IStillFaceNotification {

    private Alert alert = new Alert(Alert.AlertType.WARNING);

    public StillFaceWarningNotification(String message){
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        ((Stage)alert.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
    }

    @Override
    public void show() {
        alert.showAndWait();
    }
}
