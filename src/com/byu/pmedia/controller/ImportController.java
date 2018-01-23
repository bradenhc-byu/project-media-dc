package com.byu.pmedia.controller;

import com.byu.pmedia.database.StillFaceDAO;
import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.model.*;
import com.byu.pmedia.parser.CodedVideoCSVParser;
import com.byu.pmedia.view.StillFaceWarningNotification;
import com.googlecode.cqengine.query.Query;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.googlecode.cqengine.query.QueryFactory.*;



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
            Query<StillFaceTag> query = not(equal(StillFaceTag.TAG_ID, 0));
            ArrayList<StillFaceTag> choiceBoxValues = new ArrayList<>();
            for(StillFaceTag tag : StillFaceModel.getInstance().getTagCollection().retrieve(query)){
                choiceBoxValues.add(tag);
            }
            this.choiceBoxTag.setItems(FXCollections.observableArrayList(choiceBoxValues));
            this.buttonImport.setDisable(false);

        }
    }

    public void onCancel(ActionEvent actionEvent) {
        ((Stage)(this.buttonCancel.getScene().getWindow())).close();
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
        Task importTask = new Task<Void>(){
            @Override
            protected Void call() throws Exception{
                updateMessage("Preparing import...");
                StillFaceImportData importData = new StillFaceImportData(
                        chosenFile.getName(),
                        Integer.parseInt(textFieldYear.getText()),
                        Integer.parseInt(textFieldFamilyID.getText()),
                        Integer.parseInt(textFieldParticipantID.getText()),
                        (StillFaceTag)choiceBoxTag.getValue(),
                        textFieldAlias.getText(),
                        new Date(System.currentTimeMillis()));
                updateMessage("Importing data...");
                StillFaceVideoData videoData = new StillFaceVideoData();
                new CodedVideoCSVParser().parseFromCSVIntoCodedVideoData(chosenFile.getAbsolutePath(), videoData);
                dao.lockConnection();
                int key = dao.insertImportData(importData);
                if(key > 0){
                    for(StillFaceCodeData data : videoData.getData()){
                        data.setImportID(key);
                        int dKey = dao.insertCodeData(data);
                        if(dKey < 0){
                            PMLogger.getInstance().error("Unable to insert code data");
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Import Failed");
                            alert.setContentText("Unable to insert imported data into the database. Check your connection and the log files and try again.");
                            alert.showAndWait();
                            dao.cleanImportData(key);
                            break;
                        }
                    }
                    StillFaceModel.getInstance().refreshImportData();
                    StillFaceModel.getInstance().refreshVideoData();
                }
                else{
                    PMLogger.getInstance().error("Unable to insert data into database");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Import Failed");
                    alert.setContentText("Unable to insert imported data into the database. Check your connection and the log files and try again.");
                    alert.showAndWait();
                }
                dao.unlockConnection();
                return null;
            }
        };
        importTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                ((Stage)(buttonImport.getScene().getWindow())).close();
            }
        });

        // Bind the progress
        labelMessage.textProperty().bind(importTask.messageProperty());

        new Thread(importTask).start();
    }
}
