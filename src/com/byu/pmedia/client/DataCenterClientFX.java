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

import com.byu.pmedia.log.PMLoggerInitializer;
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

/**
 * DataCenterClientFX
 * Main application for the ProjectMEDIA Data Center. This class creates a splash screen for the user while it
 * initializes a connection with the database, the internal model, and GUI elements. It loads the main GUI from a
 * FXML file and assigns it to the root stage. It also sets up keyboard shortcuts that execute certain tasks from the
 * main GUI.
 *
 * @author Braden Hitchcock
 */
public class DataCenterClientFX extends Application {

    /* Key combinations can be initialized here.*/
    /* This key combination is for Ctrl+s (saving the edited data) */
    private final KeyCombination keyCombinationSave = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);

    /**
     * Overrides the default implementation of a JavaFX Application's start method. This is where all initialization
     * happens and the main GUI is loaded by the FXMLLoader.
     *
     * @param stage The primary JavaFX stage
     * @throws Exception If an error occurs during initialization, it can throw an exception to be handled by JavaFX
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Initialize the logger
        PMLoggerInitializer.setup();

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

        // Display the main GUI
        stage.show();
    }

    /**
     * Main method. Simply calls launch to kick-off the JavaFX Application
     *
     * @param args A string array of arguments passed to the application, usually from the command line
     */
    public static void main(String[] args) {
        launch(args);
    }
}
