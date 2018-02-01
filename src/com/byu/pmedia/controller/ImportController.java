package com.byu.pmedia.controller;

import com.byu.pmedia.database.StillFaceDAO;
import com.byu.pmedia.model.*;
import com.byu.pmedia.tasks.StillFaceImportTask;
import com.byu.pmedia.tasks.StillFaceTaskCallback;
import com.byu.pmedia.view.StillFaceErrorNotification;
import com.byu.pmedia.view.StillFaceWarningNotification;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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



public class ImportController implements Initializable {
    public Button buttonChooseFile;
    public TextField textFieldChosenFile;
    public Label labelYear;
    public Label labelFamilyID;
    public Label labelParticipantID;
    public TextField textFieldYear;
    public TextField textFieldFamilyID;
    public TextField textFieldParticipantID;
    public Label labelTag;
    public ChoiceBox choiceBoxTag;
    public Button buttonImport;
    public Button buttonCancel;
    public Label labelAlias;
    public TextField textFieldAlias;
    public Label labelMessage;
    public ProgressIndicator progressIndicator;

    private FileChooser fileChooser = new FileChooser();
    private File chosenFile;
    private StillFaceDAO dao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.dao = StillFaceDAO.generateFromConfig();
        progressIndicator.setVisible(false);
        buttonImport.setDisable(true);
    }

    public void onChooseFile(ActionEvent actionEvent) {
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

    public void onCancel(ActionEvent actionEvent) {
        close();
    }

    public void onImport(ActionEvent actionEvent) {
        if(this.choiceBoxTag.getValue() == null){
            new StillFaceWarningNotification("Please select a tag for this import.").show();
            return;
        }
        if(this.textFieldAlias.getText().equals("")){
            new StillFaceWarningNotification("Please enter an alias for this import.").show();
            return;
        }
        doImport();
    }

    private void extractFileNameData(String filename){
        Pattern p = Pattern.compile("[0-9]-[0-9]+-[0-9]+ SF [a-zA-z ]+-evts");
        if(p.matcher(filename).find()){
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

    private void doImport(){
        buttonImport.setDisable(true);
        progressIndicator.setVisible(true);
        StillFaceImport importData = new StillFaceImport(
                chosenFile.getName(),
                Integer.parseInt(textFieldYear.getText()),
                Integer.parseInt(textFieldFamilyID.getText()),
                Integer.parseInt(textFieldParticipantID.getText()),
                (StillFaceTag)choiceBoxTag.getValue(),
                textFieldAlias.getText(),
                new Date(System.currentTimeMillis()));
        labelMessage.setText("Importing data...");
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

    private void close(){
        ((Stage)(this.buttonCancel.getScene().getWindow())).close();
    }
}
