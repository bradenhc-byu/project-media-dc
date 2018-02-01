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

import com.byu.pmedia.config.StillFaceConfig;
import com.byu.pmedia.database.DatabaseMode;
import com.byu.pmedia.database.StillFaceDAO;
import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.model.*;
import com.byu.pmedia.view.ConfirmAction;
import com.byu.pmedia.view.StillFaceConfirmNotification;
import com.byu.pmedia.view.StillFaceErrorNotification;
import com.byu.pmedia.view.StillFaceWarningNotification;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

import static com.googlecode.cqengine.query.QueryFactory.*;

public class SettingsController implements Initializable, Observer {

    @FXML private ChoiceBox choiceBoxFirstCode;
    @FXML private ChoiceBox choiceBoxSecondCode;
    @FXML private Button buttonDeleteCode;
    @FXML private Button buttonDeleteTag;
    @FXML private TabPane tabPaneSettings;
    @FXML private Tab tabCodes;
    @FXML private Tab tabTags;
    @FXML private Tab tabGeneral;
    @FXML private TableView tableViewCodes;
    @FXML private Button buttonAddCode;
    @FXML private TableView tableViewTags;
    @FXML private Button buttonAddTag;
    @FXML private TextField textFieldDBHost;
    @FXML private Label labelDBMode;
    @FXML private Label labelDBHost;
    @FXML private Label labelDBPort;
    @FXML private TextField textFieldDBPort;
    @FXML private ChoiceBox choiceBoxDBMode;
    @FXML private Label labelDBName;
    @FXML private TextField textFieldDBName;
    @FXML private TextField textFieldDBUsername;
    @FXML private Label labelDBUsername;
    @FXML private Label labelDBPassword;
    @FXML private Button buttonSave;
    @FXML private Button buttonCancel;
    @FXML private Label labelDataCache;
    @FXML private PasswordField textFieldDBPassword;
    @FXML private CheckBox checkboxDataCache;

    private boolean changesMade = false;
    private StillFaceDAO dao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.dao = StillFaceDAO.generateFromConfig();

        // Set available modes
        choiceBoxDBMode.setItems(FXCollections.observableArrayList(DatabaseMode.DERBY.toPrettyString(),
                DatabaseMode.AZURE.toPrettyString(), DatabaseMode.HSQLDB.toPrettyString() ));

        textFieldDBHost.setText(StillFaceConfig.getInstance().getAsString("database.host"));
        textFieldDBPort.setText(StillFaceConfig.getInstance().getAsString("database.port"));
        textFieldDBName.setText(StillFaceConfig.getInstance().getAsString("database.name"));
        textFieldDBUsername.setText(StillFaceConfig.getInstance().getAsString("database.user"));
        textFieldDBPassword.setText(StillFaceConfig.getInstance().getAsString("database.password"));
        String selected = DatabaseMode.valueOf(StillFaceConfig.getInstance().getAsString("database.mode")).toPrettyString();
        choiceBoxDBMode.getSelectionModel().select(selected);
        if(StillFaceConfig.getInstance().getAsBoolean("model.cache")){
            checkboxDataCache.setSelected(true);
        }
        else{
            checkboxDataCache.setSelected(false);
        }

        buttonDeleteCode.setDisable(true);
        buttonDeleteTag.setDisable(true);

        // Set listeners
        choiceBoxDBMode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                changesMade = true;
            }
        });
        textFieldDBHost.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                changesMade = true;
            }
        });
        textFieldDBPort.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                changesMade = true;
            }
        });
        textFieldDBName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                changesMade = true;
            }
        });
        textFieldDBUsername.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                changesMade = true;
            }
        });
        textFieldDBPassword.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                changesMade = true;
            }
        });
        checkboxDataCache.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                changesMade = true;
            }
        });

        // Establish the table factors and listeners
        TableColumn codeCol = new TableColumn("Code Name");
        codeCol.prefWidthProperty().bind(tableViewCodes.widthProperty());
        codeCol.setCellValueFactory(new PropertyValueFactory<StillFaceCode, String>("name"));
        codeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        codeCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<StillFaceCode, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<StillFaceCode, String> cellEditEvent) {
                StillFaceCode code = cellEditEvent.getTableView().getItems()
                        .get(cellEditEvent.getTablePosition().getRow());
                code.setName(cellEditEvent.getNewValue());
                dao.updateExistingCode(code);
            }
        });

        TableColumn tagCol = new TableColumn("Tag Name");
        tagCol.prefWidthProperty().bind(tableViewCodes.widthProperty());
        tagCol.setCellValueFactory(new PropertyValueFactory<StillFaceTag, String>("tagValue"));
        tagCol.setCellFactory(TextFieldTableCell.forTableColumn());
        tagCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<StillFaceTag, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<StillFaceTag, String> cellEditEvent) {
                StillFaceTag tag = cellEditEvent.getTableView().getItems()
                        .get(cellEditEvent.getTablePosition().getRow());
                tag.setTagValue(cellEditEvent.getNewValue());
                dao.updateExistingTag(tag);
            }
        });

        tableViewCodes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                buttonDeleteCode.setDisable(false);
            }
        });
        tableViewTags.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                buttonDeleteTag.setDisable(false);
            }
        });

        tableViewCodes.getColumns().add(codeCol);
        tableViewTags.getColumns().add(tagCol);

        tabPaneSettings.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observableValue, Tab tab, Tab t1) {
                if(t1 == tabCodes){
                    updateCodes();
                    buttonDeleteTag.setDisable(true);
                }
                if(t1 == tabTags){
                    updateTags();
                    buttonDeleteCode.setDisable(true);
                }
            }
        });

        // Now setup the code delimiter choice boxes
        choiceBoxFirstCode.setItems(FXCollections.observableList(StillFaceModel.getCodeList()));
        StillFaceCode c = retrieveCodeWithIndex(1);
        choiceBoxFirstCode.getSelectionModel().select(c);
        choiceBoxFirstCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<StillFaceCode>() {
            @Override
            public void changed(ObservableValue observableValue, StillFaceCode oldCode, StillFaceCode newCode) {
                if(newCode != null){
                    setCodeDelimiter(newCode, 1);
                }
            }
        });
        choiceBoxSecondCode.setItems(FXCollections.observableList(StillFaceModel.getCodeList()));
        c = retrieveCodeWithIndex(2);
        choiceBoxSecondCode.getSelectionModel().select(c);
        choiceBoxSecondCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<StillFaceCode>() {
            @Override
            public void changed(ObservableValue observableValue, StillFaceCode oldCode, StillFaceCode newCode) {
                if(newCode != null){
                    setCodeDelimiter(newCode, 2);
                }
            }
        });

        // Everything has been initialized, register as an observer
        StillFaceModel.getInstance().addObserver(this);
    }

    @FXML
    private void onSaveAction(ActionEvent actionEvent) {
        if(changesMade) {
            // Save the general configuration first
            DatabaseMode mode = DatabaseMode.valueOf(StillFaceConfig.getInstance().getAsString("database.mode"));
            for (DatabaseMode m : DatabaseMode.values()) {
                if (m.toPrettyString().equals(choiceBoxDBMode.getSelectionModel().getSelectedItem().toString())) {
                    mode = m;
                    break;
                }
            }
            StillFaceConfig.getInstance().setWithString("database.mode", mode.toString());
            StillFaceConfig.getInstance().setWithString("database.host", textFieldDBHost.getText());
            StillFaceConfig.getInstance().setWithString("database.port", textFieldDBPort.getText());
            StillFaceConfig.getInstance().setWithString("database.name", textFieldDBName.getText());
            StillFaceConfig.getInstance().setWithString("database.user", textFieldDBUsername.getText());
            StillFaceConfig.getInstance().setWithString("database.password", textFieldDBPassword.getText());
            StillFaceConfig.getInstance().setWithBoolean("model.cache", checkboxDataCache.isSelected());
            boolean success = StillFaceConfig.getInstance().save();
            if (success) {
                String message = "Changes have been made to the application configuration. " +
                        "Do you wish to restart the program?";
                new StillFaceConfirmNotification(message, new ConfirmAction() {
                    @Override
                    public void onOK() {
                        restart();
                    }

                    @Override
                    public void onCancel() {
                        close();
                    }
                }).show();
            }
        }
        close();
    }

    @FXML
    private void onCancelAction(ActionEvent actionEvent) {
        close();
    }

    private void close(){
        ((Stage)buttonCancel.getScene().getWindow()).close();
    }

    private void restart(){
        close();
    }

    @FXML
    private void onAddCode(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Code");
        dialog.setHeaderText("Enter the code name below");
        dialog.setContentText("Code Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            StillFaceCode code = new StillFaceCode(name);
            int key = dao.insertNewCode(code);
            if(key < 0){
                String message = "An error has occurred in connecting with the database. We were unable to save the new" +
                        "code.";
                new StillFaceErrorNotification(message).show();
                PMLogger.getInstance().error("Could not add code");
            }
            else{
                StillFaceModel.getInstance().refreshCodes();
            }
        });
        StillFaceModel.getInstance().notifyObservers();
    }

    @FXML
    private void onAddTag(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Tag");
        dialog.setHeaderText("Enter the tag name below");
        dialog.setContentText("Tag Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            StillFaceTag tag = new StillFaceTag(name);
            int key = dao.insertNewTag(tag);
            if(key < 0){
                String message = "An error has occurred in connecting with the database. We were unable to save the" +
                        "new tag.";
                new StillFaceErrorNotification(message).show();
                PMLogger.getInstance().error("Could not add tag");
            }
            else{
                StillFaceModel.getInstance().refreshTags();
            }
        });
        StillFaceModel.getInstance().notifyObservers();
    }

    @FXML
    private void onDeleteCode(ActionEvent actionEvent) {
        StillFaceCode codeToDelete = (StillFaceCode)tableViewCodes.getSelectionModel().getSelectedItem();
        // If this code is the selected delimiter, don't allow the user to delete it
        if(codeToDelete.equals(choiceBoxFirstCode.getSelectionModel().getSelectedItem())
                || codeToDelete.equals(choiceBoxSecondCode.getSelectionModel().getSelectedItem())){
            String message = "The code you have selected to delete is currently one of the selected delimiter codes. " +
                    "Please replace this code with a new delimiter before deleting it.";
            new StillFaceWarningNotification(message).show();
            return;
        }
        // If there are data entries that have this code, make the user select another code to replace them with
        Query<StillFaceData> dataQuery = equal(StillFaceData.CODE, codeToDelete);
        ResultSet<StillFaceData> resultCodeData = StillFaceModel.getInstance().getDataCollection().retrieve(dataQuery);
        if(resultCodeData.size() > 0){
            Query<StillFaceCode> codeQuery = not(equal(StillFaceCode.CODE_ID, codeToDelete.getCodeID()));
            List<StillFaceCode> codes = new ArrayList<>();
            for(StillFaceCode c : StillFaceModel.getInstance().getCodeCollection().retrieve(codeQuery,
                    queryOptions(orderBy(descending(StillFaceCode.NAME))))){
                codes.add(c);
            }
            ChoiceDialog<StillFaceCode> dialog = new ChoiceDialog<>(codes.get(0), codes);
            dialog.setTitle("Replace Codes");
            dialog.setHeaderText("Attempt to delete a code that is currently in use");
            dialog.setContentText("Some data in the database uses the code you are trying to delete. Please select" +
                    " a code to use in place of '" + codeToDelete.getName() + "'");
            dialog.setWidth(200);
            Optional<StillFaceCode> result = dialog.showAndWait();
            result.ifPresent(replacementCode -> {
                // Update all the code entries
                dao.lockConnection();
                for(StillFaceData dataEntry : resultCodeData){
                    dataEntry.setCode(replacementCode);
                    dao.updateCodeData(dataEntry);
                }
                dao.unlockConnection();
                StillFaceModel.getInstance().refreshCodeData();
            });
        }
        // Delete the old code
        dao.deleteExistingCode(codeToDelete);
        StillFaceModel.getInstance().refreshCodes();
        StillFaceModel.getInstance().notifyObservers();
    }

    @FXML
    private void onDeleteTag(ActionEvent actionEvent) {
        // Get the selected value
        StillFaceTag tagToDelete = (StillFaceTag) tableViewTags.getSelectionModel().getSelectedItem();

        // If there are import entries that have this tag, make the user select another code to replace them with
        Query<StillFaceImport> importQuery = equal(StillFaceImport.TAG, tagToDelete);
        ResultSet<StillFaceImport> resultImportData =
                StillFaceModel.getInstance().getImportDataCollection().retrieve(importQuery);
        if(resultImportData.size() > 0){
            Query<StillFaceTag> tagQuery = not(equal(StillFaceTag.TAG_ID, tagToDelete.getTagID()));
            List<StillFaceTag> tags = new ArrayList<>();
            for(StillFaceTag t : StillFaceModel.getInstance().getTagCollection().retrieve(tagQuery,
                    queryOptions(orderBy(descending(StillFaceTag.TAG_VALUE))))){
                tags.add(t);
            }
            ChoiceDialog<StillFaceTag> dialog = new ChoiceDialog<>(tags.get(0), tags);
            dialog.setTitle("Replace Tag");
            dialog.setHeaderText("Attempt to delete a tag that is currently in use");
            dialog.setContentText("Some imports in the database use the tag you are trying to delete. Please select" +
                    " a tag to use in place of '" + tagToDelete.getTagValue() + "'");
            dialog.setWidth(200);
            Optional<StillFaceTag> result = dialog.showAndWait();
            result.ifPresent(replacementTag -> {
                // Update all the code entries
                dao.lockConnection();
                for(StillFaceImport importEntry : resultImportData){
                    importEntry.setTag(replacementTag);
                    dao.updateImportData(importEntry);
                }
                dao.unlockConnection();
                StillFaceModel.getInstance().refreshImportData();
            });
        }
        // Delete the old code
        dao.deleteExistingTag(tagToDelete);
        StillFaceModel.getInstance().refreshTags();
        StillFaceModel.getInstance().notifyObservers();
    }


    @Override
    public void update(Observable observable, Object o) {
        if(observable == StillFaceModel.getInstance()){
            updateCodes();
            updateTags();
            buttonDeleteTag.setDisable(true);
            buttonDeleteCode.setDisable(true);
        }
    }

    private void updateCodes(){
        tableViewCodes.setItems(FXCollections.observableArrayList(StillFaceModel.getCodeList()));
    }

    private void updateTags(){
        tableViewTags.setItems(FXCollections.observableArrayList(StillFaceModel.getTagList()));
    }

    private void setCodeDelimiter(StillFaceCode code, int index){
        StillFaceCode c = retrieveCodeWithIndex(index);
        if(c != null){
            c.setDelimiterIndex(0);
            dao.updateExistingCode(c);
        }
        code.setDelimiterIndex(index);
        dao.updateExistingCode(code);
        StillFaceModel.getInstance().refreshCodes();
        StillFaceModel.getInstance().notifyObservers();
    }

    private StillFaceCode retrieveCodeWithIndex(int index){
        Query<StillFaceCode> query = equal(StillFaceCode.DELIMITER_INDEX, index);
        StillFaceCode code = null;
        for(StillFaceCode c : StillFaceModel.getInstance().getCodeCollection().retrieve(query)){
            code = c;
        }
        return code;
    }
}
