package com.byu.pmedia.controller;

import com.byu.pmedia.database.StillFaceDAO;
import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.model.StillFaceCode;
import com.byu.pmedia.model.StillFaceCodeData;
import com.byu.pmedia.model.StillFaceImportData;
import com.byu.pmedia.model.StillFaceModel;
import com.byu.pmedia.util.NumericTextFieldTableCell;
import com.byu.pmedia.view.ConfirmAction;
import com.byu.pmedia.view.StillFaceConfirmNotification;
import com.googlecode.cqengine.query.Query;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;
import java.util.function.UnaryOperator;

import static com.googlecode.cqengine.query.QueryFactory.*;

public class DataCenterController implements Initializable {


    @FXML private Button buttonSaveChanges;
    @FXML private Button buttonSynch;
    @FXML private ListView listViewExplorer;
    @FXML private Button buttonImportCSVData;
    @FXML private Button buttonSettings;
    @FXML private Button buttonExportToCSV;
    @FXML private Button buttonPlot;
    @FXML private Label labelDataTitle;
    @FXML private TilePane tilePaneSummary;
    @FXML private TableView tableData;

    // TODO: Initialize the table columns here, so that they can be accessed throughout the class

    private StillFaceDAO dao;
    private Map<Integer, StillFaceCodeData> editedDataMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the DAO
        this.dao = StillFaceDAO.generateFromConfig();

        // Set up a number formatted table cell
        NumberFormat format = NumberFormat.getIntegerInstance();
        UnaryOperator<TextFormatter.Change> filter = c -> {
            if (c.isContentChange()) {
                ParsePosition parsePosition = new ParsePosition(0);
                // NumberFormat evaluates the beginning of the text
                format.parse(c.getControlNewText(), parsePosition);
                if (parsePosition.getIndex() == 0 ||
                        parsePosition.getIndex() < c.getControlNewText().length()) {
                    // reject parsing the complete text failed
                    return null;
                }
            }
            return c;
        };

        // Initialize GUI Elements
        TableColumn entryCol = new TableColumn("ID");
        entryCol.prefWidthProperty().bind(tableData.widthProperty().multiply(0.10));
        entryCol.setCellValueFactory(new PropertyValueFactory<StillFaceCodeData, Integer>("dataID"));

        TableColumn timeCol = new TableColumn("Time");
        timeCol.prefWidthProperty().bind(tableData.widthProperty().multiply(0.10));
        timeCol.setCellValueFactory(new PropertyValueFactory<StillFaceCodeData, Integer>("time"));
        timeCol.setCellFactory(c -> new NumericTextFieldTableCell<>(
                // note: each cell needs its own formatter
                // see comment by @SurprisedCoconut
                new TextFormatter<Integer>(
                        // note: should use local-aware converter instead of core!
                        new IntegerStringConverter(), 0,
                        filter)));
        timeCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<StillFaceCodeData, Integer>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<StillFaceCodeData, Integer> cellEditEvent) {
                StillFaceCodeData data = cellEditEvent.getTableView().getItems()
                        .get(cellEditEvent.getTablePosition().getRow());
                data.setTime(cellEditEvent.getNewValue());
                editedDataMap.put(data.getDataID(), data);
                buttonSaveChanges.setDisable(false);
            }
        });

        TableColumn durationCol = new TableColumn("Duration");
        durationCol.prefWidthProperty().bind(tableData.widthProperty().multiply(0.10));
        durationCol.setCellValueFactory(new PropertyValueFactory<StillFaceCodeData, Integer>("duration"));
        durationCol.setCellFactory(c -> new NumericTextFieldTableCell<>(
                // note: each cell needs its own formatter
                // see comment by @SurprisedCoconut
                new TextFormatter<Integer>(
                        // note: should use local-aware converter instead of core!
                        new IntegerStringConverter(), 0,
                        filter)));
        durationCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<StillFaceCodeData, Integer>>(){
            @Override
            public void handle(TableColumn.CellEditEvent<StillFaceCodeData, Integer> cellEditEvent){
                StillFaceCodeData data = cellEditEvent.getTableView().getItems()
                        .get(cellEditEvent.getTablePosition().getRow());
                data.setDuration(cellEditEvent.getNewValue());
                editedDataMap.put(data.getDataID(), data);
                buttonSaveChanges.setDisable(false);
            }
        });

        TableColumn codeCol = new TableColumn("Code");
        codeCol.prefWidthProperty().bind(tableData.widthProperty().multiply(0.20));
        codeCol.setCellValueFactory(new PropertyValueFactory<StillFaceCodeData, StillFaceCode>("code"));
        ObservableList<StillFaceCode> observableCodeList = FXCollections.observableArrayList(StillFaceModel.getCodeList());
        codeCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(observableCodeList));
        codeCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<StillFaceCodeData, StillFaceCode>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<StillFaceCodeData, StillFaceCode> cellEditEvent) {
                StillFaceCodeData data = cellEditEvent.getTableView().getItems()
                        .get(cellEditEvent.getTablePosition().getRow());
                data.setCode(cellEditEvent.getNewValue());
                editedDataMap.put(data.getDataID(), data);
                buttonSaveChanges.setDisable(false);
            }
        });

        TableColumn commentCol = new TableColumn("Comment");
        commentCol.prefWidthProperty().bind(tableData.widthProperty().multiply(0.50));
        commentCol.setCellValueFactory(new PropertyValueFactory<StillFaceCodeData, String>("comment"));
        commentCol.setCellFactory(TextFieldTableCell.forTableColumn());
        commentCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<StillFaceCodeData, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<StillFaceCodeData, String> cellEditEvent) {
                StillFaceCodeData data = cellEditEvent.getTableView().getItems()
                        .get(cellEditEvent.getTablePosition().getRow());
                data.setComment(cellEditEvent.getNewValue());
                editedDataMap.put(data.getDataID(), data);
                buttonSaveChanges.setDisable(false);
            }
        });

        tableData.setEditable(true);
        tableData.getColumns().addAll(entryCol, timeCol, durationCol, codeCol, commentCol);

        tilePaneSummary.setHgap(10);
        tilePaneSummary.setVgap(10);
        tilePaneSummary.setTileAlignment(Pos.CENTER_LEFT);
        tilePaneSummary.setOrientation(Orientation.VERTICAL);

        buttonSaveChanges.setDisable(true);

        // Setup the listeners
        listViewExplorer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<StillFaceImportData>() {
            @Override
            public void changed(ObservableValue<? extends StillFaceImportData> observable,
                                StillFaceImportData oldValue, StillFaceImportData newValue) {
                if(newValue != null){
                    updateData(newValue.getImportID());
                }

            }
        });
        updateImports();
    }

    @FXML
    private void onImportCSVData(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/com/byu/pmedia/view/stillfaceimportgui.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Import CSV");
            stage.setScene(scene);
            stage.showAndWait();
            updateImports();
        }
        catch(IOException e){
            PMLogger.getInstance().error("Unable to open import dialogue: " + e.getMessage());
        }
    }

    @FXML
    private void onSettings(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/com/byu/pmedia/view/stillfacesettings.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Data Center Settings");
            stage.setScene(scene);
            stage.showAndWait();
            StillFaceModel.getInstance().refresh();
        }
        catch(IOException e){
            PMLogger.getInstance().error("Unable to open settings dialogue: " + e.getMessage());
        }
    }

    @FXML
    private void onSync(ActionEvent actionEvent) {
        if(editedDataMap.size() > 0){
            new StillFaceConfirmNotification("Syncing with the database will discard any unsaved changes." +
                    "You have unsaved changes. Do you want to continue?", new ConfirmAction() {
                @Override
                public void onOK() {
                    sync();
                }

                @Override
                public void onCancel() {

                }
            }).show();
        }
        else {
            sync();
        }
        if(editedDataMap.size() == 0) resetView();
    }

    private void sync(){
        PMLogger.getInstance().info("Synchronizing with database");
        StillFaceModel.getInstance().refresh();
        clearEdits();
    }

    @FXML
    private void onSaveChanges(ActionEvent actionEvent) {
        if(editedDataMap.size() != 0){
            for(int key : editedDataMap.keySet()){
                this.dao.updateCodeData(editedDataMap.get(key));
            }
            editedDataMap.clear();
            StillFaceModel.getInstance().refreshCodeData();
        }
        buttonSaveChanges.setDisable(true);
    }

    private void updateImports(){
        // Get the import data
        Query<StillFaceImportData> importDataQuery = not(equal(StillFaceImportData.IMPORT_ID, 0));
        List<StillFaceImportData> importDataList = new ArrayList<>();
        for (StillFaceImportData data : StillFaceModel.getInstance().getImportDataCollection().retrieve(importDataQuery,
                queryOptions(orderBy(ascending(StillFaceImportData.DATE), ascending(StillFaceImportData.IMPORT_ID))))) {
            importDataList.add(data);
        }
        listViewExplorer.setItems(FXCollections.observableArrayList(importDataList));
    }

    private void resetView(){
        tableData.setItems(null);
        labelDataTitle.setText("");
        tilePaneSummary.getChildren().clear();
        updateImports();
    }

    private void updateData(int importID){

        PMLogger.getInstance().info("Filling table with import data. Import ID: " + importID);

        Query<StillFaceImportData> importDataQuery = equal(StillFaceImportData.IMPORT_ID, importID);
        for(StillFaceImportData data : StillFaceModel.getInstance().getImportDataCollection().retrieve(importDataQuery)){
            if(!data.getAlias().equals("N/A") || !data.getAlias().equals("")){
                labelDataTitle.setText(data.getAlias() + ": " + data.getFilename());
            }
            else{
                labelDataTitle.setText(data.getFilename());
            }
        }

        // Set up some maps to hold summary data
        Map<String, Integer> mostCommonCode = new HashMap<>();

        Query<StillFaceCodeData> codeDataQuery = equal(StillFaceCodeData.IMPORT_ID, importID);
        List<StillFaceCodeData> codeDataList = new ArrayList<>();
        for(StillFaceCodeData data : StillFaceModel.getInstance().getDataCollection().retrieve(codeDataQuery,
                queryOptions(orderBy(ascending(StillFaceCodeData.TIME), ascending(StillFaceCodeData.DATA_ID))))){
            codeDataList.add(data);

            // Code stats
            String codeName = data.getCode().getName();
            if(mostCommonCode.containsKey(codeName)){
                int count = mostCommonCode.get(codeName);
                mostCommonCode.put(codeName, count + 1);
            }
            else{
                mostCommonCode.put(codeName, 1);
            }
        }

        PMLogger.getInstance().info("Table data size: " + codeDataList.size());

        ObservableList<StillFaceCodeData> data = FXCollections.observableArrayList(codeDataList);

        tableData.setItems(data);

        // Put in some summary data
        Map.Entry<String, Integer> maxEntry = null;
        for(Map.Entry<String, Integer> entry : mostCommonCode.entrySet()){
            if(maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0){
                maxEntry = entry;
            }
        }
        tilePaneSummary.getChildren().clear();
        tilePaneSummary.getChildren().add(new Text("Number of codes: " + codeDataList.size()));
        tilePaneSummary.getChildren().add(new Text("Codes used: " + mostCommonCode.keySet().size()));
        tilePaneSummary.getChildren().add(new Text("Most common code: " + maxEntry.getKey()));
        tilePaneSummary.getChildren().add(new Text("Total duration (sec): " + codeDataList.get(codeDataList.size() - 1).getTime()/1000));

    }

    private void clearEdits(){
        editedDataMap.clear();
        PMLogger.getInstance().info("Edits cleared");
    }

}
