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
package com.byu.pmedia.client;

import com.byu.pmedia.view.DataCenterSplashScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DataCenterClientFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Show splash screen while initializing
        DataCenterSplashScreen splashScreen = new DataCenterSplashScreen();

        // Display the main gui
        Parent root = FXMLLoader.load(getClass().getResource("/com/byu/pmedia/view/stillfacefxgui.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("ProjectMEDIA Data Center");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
