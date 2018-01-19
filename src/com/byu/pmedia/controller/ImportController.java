package com.byu.pmedia.controller;

import com.byu.pmedia.database.StillFaceDAO;
import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.model.StillFaceCodeData;
import com.byu.pmedia.model.StillFaceImportData;
import com.byu.pmedia.model.StillFaceTag;
import com.byu.pmedia.model.StillFaceVideoData;
import com.byu.pmedia.parser.CodedVideoCSVParser;
import com.googlecode.cqengine.query.Query;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportController {
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

    private FileChooser fileChooser = new FileChooser();
    private File chosenFile;
    private StillFaceDAO dao;



    public void onChooseFile(ActionEvent actionEvent) {
        this.fileChooser.setTitle("Open Resource File");
        this.fileChooser.setInitialDirectory(new File("."));
        this.chosenFile = fileChooser.showOpenDialog(null);
        if(this.chosenFile != null){
            this.textFieldChosenFile.setText(chosenFile.getName());
            extractFileNameData(this.chosenFile.getName());
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        ((Stage)(this.buttonCancel.getScene().getWindow())).close();
    }

    public void onImport(ActionEvent actionEvent) {
        if(this.choiceBoxTag.getValue() == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete Submission");
            alert.setContentText("Please select a tag for this import");
            alert.showAndWait();
            return;
        }
        this.labelMessage.setText("Preparing import...");
        StillFaceImportData importData = new StillFaceImportData(
                this.chosenFile.getName(),
                Integer.parseInt(this.textFieldYear.getText()),
                Integer.parseInt(this.textFieldFamilyID.getText()),
                Integer.parseInt(this.textFieldParticipantID.getText()),
                (StillFaceTag)this.choiceBoxTag.getValue(),
                this.textFieldAlias.getText(),
                new Date(System.currentTimeMillis()));
        this.labelMessage.setText("Importing data...");
        StillFaceVideoData videoData = new StillFaceVideoData();
        new CodedVideoCSVParser().parseFromCSVIntoCodedVideoData(this.chosenFile.getAbsolutePath(), videoData);
        this.dao.lockConnection();
        int key = this.dao.insertImportData(importData);
        if(key > 0){
            for(StillFaceCodeData data : videoData.getData()){
                data.setImportID(key);
                int dKey = this.dao.insertCodeData(data);
                if(dKey < 0){
                    PMLogger.getInstance().error("Unable to insert code data");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Import Failed");
                    alert.setContentText("Unable to insert imported data into the database. Check your connection and the log files and try again.");
                    alert.showAndWait();
                    this.dao.cleanImportData(key);
                }
            }
        }
        else{
            PMLogger.getInstance().error("Unable to insert data into database");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Import Failed");
            alert.setContentText("Unable to insert imported data into the database. Check your connection and the log files and try again.");
            alert.showAndWait();
        }
        this.dao.unlockConnection();
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
}
