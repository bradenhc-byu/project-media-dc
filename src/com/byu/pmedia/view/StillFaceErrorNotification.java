package com.byu.pmedia.view;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class StillFaceErrorNotification implements IStillFaceNotification {

    private Alert alert = new Alert(Alert.AlertType.ERROR);

    public StillFaceErrorNotification(String message){
        alert.setContentText(message);
        alert.setTitle("Datacenter encountered an error");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        ((Stage)alert.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
    }

    @Override
    public void show() {
        alert.showAndWait();
    }
}
