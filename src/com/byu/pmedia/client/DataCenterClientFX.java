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
