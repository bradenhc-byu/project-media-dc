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

import com.byu.pmedia.model.StillFaceModel;
import com.byu.pmedia.tasks.StillFaceSaveTask;
import com.byu.pmedia.tasks.StillFaceSyncTask;
import com.byu.pmedia.tasks.StillFaceTaskCallback;
import com.byu.pmedia.view.DataCenterSplashScreen;
import com.byu.pmedia.view.StillFaceErrorNotification;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class DataCenterClientFX extends Application {

    private final KeyCombination keyCombinationSave = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);

    @Override
    public void start(Stage stage) throws Exception {
        // Show splash screen while initializing
        DataCenterSplashScreen splashScreen = new DataCenterSplashScreen();

        // Display the main gui
        Parent root = FXMLLoader.load(getClass().getResource("/com/byu/pmedia/view/stillfacefxgui.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("ProjectMEDIA Data Center");
        stage.setScene(scene);

        // Set up some keyboard shortcuts
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode() == KeyCode.F5){
                    new StillFaceSyncTask(new StillFaceTaskCallback() {
                        @Override
                        public void onSuccess() {
                            StillFaceModel.getInstance().notifyObservers();
                        }

                        @Override
                        public void onFail(Throwable exception) {
                            new StillFaceErrorNotification("An error occurred while synchronizing with the database: " +
                                    exception.getMessage()).show();
                        }
                    }).execute();
                    keyEvent.consume();
                }
            }
        });
        scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyCombinationSave.match(keyEvent)){
                    new StillFaceSaveTask(new StillFaceTaskCallback() {
                        @Override
                        public void onSuccess() {
                            StillFaceModel.getInstance().notifyObservers();
                        }

                        @Override
                        public void onFail(Throwable exception) {
                            new StillFaceErrorNotification("An error occurred while saving data to the database: " +
                                    exception.getMessage()).show();
                        }
                    }).execute();
                    keyEvent.consume();
                }
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
