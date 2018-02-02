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
package com.byu.pmedia.controller;

import com.byu.pmedia.model.*;
import com.byu.pmedia.tasks.StillFaceImportTask;
import com.byu.pmedia.tasks.StillFaceTaskCallback;
import com.byu.pmedia.view.StillFaceErrorNotification;
import com.byu.pmedia.view.StillFaceWarningNotification;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ImportController
 * Provides for initialization for and controls the GUI responsible for importing CSV files of StillFace data into the
 * database.
 *
 * @author Braden Hitchcock
 */
public class ImportController implements Initializable {

    /* Below are the GUI elements for this controller. Their purposes should be self-documenting in their names. */
    @FXML private Button buttonChooseFile;
    @FXML private TextField textFieldChosenFile;
    @FXML private Label labelYear;
    @FXML private Label labelFamilyID;
    @FXML private Label labelParticipantID;
    @FXML private TextField textFieldYear;
    @FXML private TextField textFieldFamilyID;
    @FXML private TextField textFieldParticipantID;
    @FXML private Label labelTag;
    @FXML private ChoiceBox choiceBoxTag;
    @FXML private Button buttonImport;
    @FXML private Button buttonCancel;
    @FXML private Label labelAlias;
    @FXML private TextField textFieldAlias;
    @FXML private Label labelMessage;
    @FXML private ProgressIndicator progressIndicator;

    /* A pop-up file chooser */
    private FileChooser fileChooser = new FileChooser();

    /* Keeps track of what file is chosen by the user from the file chooser */
    private File chosenFile;

    /**
     * Initializes the GUI components of the view associated with this controller
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not
     *            known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not
     *                       localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressIndicator.setVisible(false);
        buttonImport.setDisable(true);
    }

    /**
     * Listener triggered when the controller detects an action from the user on the 'Choose File' button. Opens
     * a file chooser and allows the user to select a file to import into the database.
     *
     * @param actionEvent The event detected by the controller
     */
    @FXML
    private void onChooseFile(ActionEvent actionEvent) {
        this.fileChooser.setTitle("Open Resource File");
        this.fileChooser.setInitialDirectory(new File("."));
        this.chosenFile = fileChooser.showOpenDialog(null);
        if(this.chosenFile != null){
            this.textFieldChosenFile.setText(chosenFile.getName());
            extractFileNameData(this.chosenFile.getName());
            this.choiceBoxTag.setItems(FXCollections.observableArrayList(StillFaceModel.getTagList()));
            this.buttonImport.setDisable(false);

        }
    }

    /**
     * Listener triggered when the controller detects an action from the user on the 'Cancel' button. Close the
     * GUI associated with this controller.
     *
     * @param actionEvent The event detected by the controller
     */
    public void onCancel(ActionEvent actionEvent) {
        close();
    }

    /**
     * Listener triggered when the controller detects an action from the user on the 'Import' button. Takes the
     * file chosen by the user and other relevant information from the GUI and imports the file contents into
     * the database.
     *
     * @param actionEvent The event detected by the controller
     */
    public void onImport(ActionEvent actionEvent) {
        // Verify the user has selected a tag
        if(this.choiceBoxTag.getValue() == null){
            new StillFaceWarningNotification("Please select a tag for this import.").show();
            return;
        }
        // Verify the user has enterd an alias for the import
        if(this.textFieldAlias.getText().equals("")){
            new StillFaceWarningNotification("Please enter an alias for this import.").show();
            return;
        }
        // Perform the import
        doImport();
    }

    /**
     * Takes the name of the file chosen by the user and attempts to extract useful information from the filename.
     * This information is associated with the data when it is imported into the database.
     *
     * @param filename The name of the file selected by the user from the file chooser
     */
    private void extractFileNameData(String filename){
        // Attempt to find a PID in the filename (00-000-00)
        Pattern p = Pattern.compile("[0-9]-[0-9]+-[0-9]+");
        if(p.matcher(filename).find()){
            // Since it matches, extract each of the digits from the pid and set the corresponding input fields
            // with that information
            p = Pattern.compile("\\d+");
            Matcher m = p.matcher(filename);
            int count = 0;
            while(m.find()){
                switch(count){
                    case 0:
                        this.textFieldYear.setText(m.group(0));
                        count++;
                        break;

                    case 1:
                        this.textFieldFamilyID.setText(m.group(0));
                        count++;
                        break;

                    case 2:
                        this.textFieldParticipantID.setText(m.group(0));
                        count++;
                        break;

                    default:
                        break;
                }
            }
        }
    }

    /**
     * Starts up a StillFaceImportTask that will take the file selected by the user and relevant information and
     * create a new import entry in the database and import all the data from that file
     */
    private void doImport(){
        // Disable some GUI elements so this can't happen more than once at a time
        buttonImport.setDisable(true);
        // Let the user know the program is working
        progressIndicator.setVisible(true);
        // Create an empty StillFaceImport object with the information
        StillFaceImport importData = new StillFaceImport(
                chosenFile.getName(),
                Integer.parseInt(textFieldYear.getText()),
                Integer.parseInt(textFieldFamilyID.getText()),
                Integer.parseInt(textFieldParticipantID.getText()),
                (StillFaceTag)choiceBoxTag.getValue(),
                textFieldAlias.getText(),
                new Date(System.currentTimeMillis()));
        labelMessage.setText("Importing data...");
        // Create the new task
        new StillFaceImportTask(chosenFile, importData, new StillFaceTaskCallback() {
            @Override
            public void onSuccess() {
                StillFaceModel.getInstance().notifyObservers();
                close();
            }
            @Override
            public void onFail(Throwable exception) {
                new StillFaceErrorNotification("An error occured while trying to import the data: " +
                        exception.getMessage()
                ).show();
            }
        }).execute();
    }

    /**
     * Close the GUI window associated with this controller
     */
    private void close(){
        ((Stage)(this.buttonCancel.getScene().getWindow())).close();
    }
}
