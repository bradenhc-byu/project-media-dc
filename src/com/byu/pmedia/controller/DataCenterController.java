package com.byu.pmedia.controller;

import com.byu.pmedia.database.StillFaceDAO;
import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.model.*;
import com.byu.pmedia.tasks.StillFaceExportTask;
import com.byu.pmedia.tasks.StillFaceSaveTask;
import com.byu.pmedia.tasks.StillFaceSyncTask;
import com.byu.pmedia.tasks.StillFaceTaskCallback;
import com.byu.pmedia.util.NumericTextFieldTableCell;
import com.byu.pmedia.view.ConfirmAction;
import com.byu.pmedia.view.StillFaceConfirmNotification;
import com.byu.pmedia.view.StillFaceErrorNotification;
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
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;
import java.util.function.UnaryOperator;

import static com.googlecode.cqengine.query.QueryFactory.*;

public class DataCenterController implements Initializable {


    @FXML private Button buttonGetResults;
    @FXML private ChoiceBox choiceBoxTag;
    @FXML private CheckBox checkBoxFamilyID;
    @FXML private CheckBox checkBoxTag;
    @FXML private TextField textFieldFamilyID;
    @FXML private TextField textFieldParticipantID;
    @FXML private CheckBox checkBoxParticipantID;
    @FXML private TextField textFieldYear;
    @FXML private CheckBox checkBoxYear;
    @FXML private AnchorPane anchorPaneMain;
    @FXML private Tab tabImports;
    @FXML private Tab tabQuery;
    @FXML private Label labelTag;
    @FXML private Label labelYear;
    @FXML private Label labelParticipantID;
    @FXML private Label labelImportDate;
    @FXML private Tab tabPlot;
    @FXML private Tab tabData;
    @FXML private Button buttonSaveChanges;
    @FXML private Button buttonSynch;
    @FXML private ListView listViewExplorer;
    @FXML private Button buttonImportCSVData;
    @FXML private Button buttonSettings;
    @FXML private Button buttonExportToCSV;
    @FXML private Label labelDataTitle;
    @FXML private TilePane tilePaneSummary;
    @FXML private TableView tableData;

    // Initialize the table columns here, so that they can be accessed throughout the class
    private TableColumn tableColumnID = new TableColumn("ID");
    private TableColumn tableColumnTime = new TableColumn("Time");
    private TableColumn tableColumnDuration = new TableColumn("Duration");
    private TableColumn tableColumnCode = new TableColumn("Code");
    private TableColumn tableColumnComment = new TableColumn("Comment");

    private StillFaceDAO dao;
    private Map<Integer, StillFaceData> editedDataMap = new HashMap<>();
    private List<StillFaceData> visibleDataList = new ArrayList<>();

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
        tableColumnID.prefWidthProperty().bind(tableData.widthProperty().multiply(0.10));
        tableColumnID.setCellValueFactory(new PropertyValueFactory<StillFaceData, Integer>("dataID"));

        tableColumnTime.prefWidthProperty().bind(tableData.widthProperty().multiply(0.10));
        tableColumnTime.setCellValueFactory(new PropertyValueFactory<StillFaceData, Integer>("time"));
        tableColumnTime.setCellFactory(c -> new NumericTextFieldTableCell<>(
                // note: each cell needs its own formatter
                // see comment by @SurprisedCoconut
                new TextFormatter<Integer>(
                        // note: should use local-aware converter instead of core!
                        new IntegerStringConverter(), 0,
                        filter)));
        tableColumnTime.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<StillFaceData, Integer>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<StillFaceData, Integer> cellEditEvent) {
                StillFaceData data = cellEditEvent.getTableView().getItems()
                        .get(cellEditEvent.getTablePosition().getRow());
                data.setTime(cellEditEvent.getNewValue());
                editedDataMap.put(data.getDataID(), data);
                buttonSaveChanges.setDisable(false);
            }
        });

        tableColumnDuration.prefWidthProperty().bind(tableData.widthProperty().multiply(0.10));
        tableColumnDuration.setCellValueFactory(new PropertyValueFactory<StillFaceData, Integer>("duration"));
        tableColumnDuration.setCellFactory(c -> new NumericTextFieldTableCell<>(
                // note: each cell needs its own formatter
                // see comment by @SurprisedCoconut
                new TextFormatter<Integer>(
                        // note: should use local-aware converter instead of core!
                        new IntegerStringConverter(), 0,
                        filter)));
        tableColumnDuration.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<StillFaceData, Integer>>(){
            @Override
            public void handle(TableColumn.CellEditEvent<StillFaceData, Integer> cellEditEvent){
                StillFaceData data = cellEditEvent.getTableView().getItems()
                        .get(cellEditEvent.getTablePosition().getRow());
                data.setDuration(cellEditEvent.getNewValue());
                editedDataMap.put(data.getDataID(), data);
                buttonSaveChanges.setDisable(false);
            }
        });

        tableColumnCode.prefWidthProperty().bind(tableData.widthProperty().multiply(0.20));
        tableColumnCode.setCellValueFactory(new PropertyValueFactory<StillFaceData, StillFaceCode>("code"));
        ObservableList<StillFaceCode> observableCodeList = FXCollections.observableArrayList(StillFaceModel.getCodeList());
        tableColumnCode.setCellFactory(ChoiceBoxTableCell.forTableColumn(observableCodeList));
        tableColumnCode.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<StillFaceData, StillFaceCode>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<StillFaceData, StillFaceCode> cellEditEvent) {
                StillFaceData data = cellEditEvent.getTableView().getItems()
                        .get(cellEditEvent.getTablePosition().getRow());
                data.setCode(cellEditEvent.getNewValue());
                editedDataMap.put(data.getDataID(), data);
                buttonSaveChanges.setDisable(false);
            }
        });

        tableColumnComment.prefWidthProperty().bind(tableData.widthProperty().multiply(0.5));
        tableColumnComment.setCellValueFactory(new PropertyValueFactory<StillFaceData, String>("comment"));
        tableColumnComment.setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumnComment.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<StillFaceData, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<StillFaceData, String> cellEditEvent) {
                StillFaceData data = cellEditEvent.getTableView().getItems()
                        .get(cellEditEvent.getTablePosition().getRow());
                data.setComment(cellEditEvent.getNewValue());
                editedDataMap.put(data.getDataID(), data);
                buttonSaveChanges.setDisable(false);
            }
        });

        tableData.setEditable(true);
        tableData.getColumns().addAll(tableColumnID, tableColumnTime, tableColumnDuration, tableColumnCode,
                tableColumnComment);

        tilePaneSummary.setHgap(10);
        tilePaneSummary.setVgap(10);
        tilePaneSummary.setTileAlignment(Pos.CENTER_LEFT);
        tilePaneSummary.setOrientation(Orientation.VERTICAL);

        buttonSaveChanges.setDisable(true);
        buttonExportToCSV.setDisable(true);

        textFieldYear.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    textFieldYear.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        textFieldFamilyID.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    textFieldFamilyID.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        textFieldParticipantID.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    textFieldParticipantID.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        // Setup the listeners
        listViewExplorer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<StillFaceImport>() {
            @Override
            public void changed(ObservableValue<? extends StillFaceImport> observable,
                                StillFaceImport oldValue, StillFaceImport newValue) {
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
            ObservableList<StillFaceCode> observableCodeList = FXCollections.observableArrayList(StillFaceModel.getCodeList());
            tableColumnCode.setCellFactory(ChoiceBoxTableCell.forTableColumn(observableCodeList));
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
    }

    private void sync(){
        new StillFaceSyncTask(new StillFaceTaskCallback() {
            @Override
            public void onSuccess() {
                clearEdits();
                resetView();
            }

            @Override
            public void onFail(Throwable exception) {
                new StillFaceErrorNotification("An error occurred while synchronizing with the database: " +
                        exception.getMessage()).show();
            }
        }).execute();
    }

    @FXML
    private void onSaveChanges(ActionEvent actionEvent) {
        new StillFaceSaveTask(editedDataMap, new StillFaceTaskCallback() {
            @Override
            public void onSuccess() {
                buttonSaveChanges.setDisable(true);
            }

            @Override
            public void onFail(Throwable exception) {
                new StillFaceErrorNotification("An error occurred while trying to save changes: "
                        + exception.getMessage()).show();
            }
        }).execute();
    }

    @FXML
    private void onExport(ActionEvent actionEvent) {
        Optional<String> result = showExportDialog();
        result.ifPresent(exportFile -> {
            new StillFaceExportTask(visibleDataList, exportFile, new StillFaceTaskCallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail(Throwable exception) {
                    new StillFaceErrorNotification("An error has occured: " + exception.getMessage()).show();
                }
            }).execute();
        });
    }

    @FXML
    private void onGetResults(ActionEvent actionEvent) {

    }

    private void updateImports(){
        // Get the import data
        Query<StillFaceImport> importDataQuery = not(equal(StillFaceImport.IMPORT_ID, 0));
        List<StillFaceImport> importDataList = new ArrayList<>();
        for (StillFaceImport data : StillFaceModel.getInstance().getImportDataCollection().retrieve(importDataQuery,
                queryOptions(orderBy(ascending(StillFaceImport.DATE), ascending(StillFaceImport.IMPORT_ID))))) {
            importDataList.add(data);
        }
        listViewExplorer.setItems(FXCollections.observableArrayList(importDataList));
    }

    private void resetView(){
        tableData.setItems(null);
        labelDataTitle.setText("");
        labelYear.setText("Year:");
        labelTag.setText("Tag:");
        labelParticipantID.setText("Participant ID:");
        labelImportDate.setText("Import Date:");
        tilePaneSummary.getChildren().clear();
        buttonExportToCSV.setDisable(true);
        buttonSaveChanges.setDisable(true);
        updateImports();
    }

    private void updateData(int importID){

        PMLogger.getInstance().info("Filling table with import data. Import ID: " + importID);

        visibleDataList.clear();

        Query<StillFaceImport> importDataQuery = equal(StillFaceImport.IMPORT_ID, importID);
        for(StillFaceImport data : StillFaceModel.getInstance().getImportDataCollection().retrieve(importDataQuery)){
            labelDataTitle.setText(data.getAlias());
            labelYear.setText("Year: " + data.getYear());
            labelTag.setText("Tag: " + data.getTag().getTagValue());
            labelParticipantID.setText("Participant ID: " + data.getParticipantNumber());
            labelImportDate.setText("Import Date: " + data.getDate());
        }

        // Set up some maps to hold summary data
        Map<String, Integer> mostCommonCode = new HashMap<>();

        Query<StillFaceData> codeDataQuery = equal(StillFaceData.IMPORT_ID, importID);
        for(StillFaceData data : StillFaceModel.getInstance().getDataCollection().retrieve(codeDataQuery,
                queryOptions(orderBy(ascending(StillFaceData.TIME), ascending(StillFaceData.DATA_ID))))){
            visibleDataList.add(data);

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

        PMLogger.getInstance().info("Table data size: " + visibleDataList.size());

        ObservableList<StillFaceData> data = FXCollections.observableArrayList(visibleDataList);

        tableData.setItems(data);

        // Put in some summary data
        Map.Entry<String, Integer> maxEntry = null;
        for(Map.Entry<String, Integer> entry : mostCommonCode.entrySet()){
            if(maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0){
                maxEntry = entry;
            }
        }
        tilePaneSummary.getChildren().clear();
        tilePaneSummary.getChildren().add(new Text("Number of codes: " + visibleDataList.size()));
        tilePaneSummary.getChildren().add(new Text("Codes used: " + mostCommonCode.keySet().size()));
        tilePaneSummary.getChildren().add(new Text("Most common code: " + maxEntry.getKey()));
        tilePaneSummary.getChildren().add(new Text("Total duration (sec): " + visibleDataList.get(visibleDataList.size() - 1).getTime()/1000));

        buttonExportToCSV.setDisable(false);

    }

    private void updateQueryData(){
        PMLogger.getInstance().info("Performing search");

    }

    private void clearEdits(){
        editedDataMap.clear();
        PMLogger.getInstance().info("Edits cleared");
    }

    private Optional<String> showExportDialog(){
        // Create the custom dialog.
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Export File");
        dialog.setHeaderText("Select a directory to export the data to.");


        // Set the button types.
        ButtonType exportButtonType = new ButtonType("Export", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(exportButtonType, ButtonType.CANCEL);

        // Create the layout grid
        GridPane grid = new GridPane();
        grid.getColumnConstraints().add(new ColumnConstraints(150));
        grid.getColumnConstraints().add(new ColumnConstraints(400));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Label labelExportLocation = new Label();

        Button buttonDirectoryChooser = new Button();
        buttonDirectoryChooser.setText("Choose Directory");
        buttonDirectoryChooser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                File chosenFile =
                        directoryChooser.showDialog(buttonDirectoryChooser.getScene().getWindow());

                if(chosenFile == null){
                    labelExportLocation.setText("");
                }else{
                    labelExportLocation.setText(chosenFile.getAbsolutePath());
                }
            }
        });

        TextField textFieldFileName = new TextField();
        textFieldFileName.setPromptText("Enter file name");

        grid.add(buttonDirectoryChooser, 0, 0);
        grid.add(labelExportLocation, 1, 0);
        grid.add(new Label("Name of File:"), 0, 1);
        grid.add(textFieldFileName, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node exportButton = dialog.getDialogPane().lookupButton(exportButtonType);
        exportButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        textFieldFileName.textProperty().addListener((observable, oldValue, newValue) -> {
            exportButton.setDisable(newValue.trim().isEmpty() || labelExportLocation.getText().isEmpty());
        });
        labelExportLocation.textProperty().addListener((observable, oldValue, newValue) -> {
            exportButton.setDisable(newValue.trim().isEmpty() || textFieldFileName.getText().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);


        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == exportButtonType &&
                    !textFieldFileName.getText().isEmpty() &&
                    !labelExportLocation.getText().isEmpty()) {
                return labelExportLocation.getText() + File.separator +  textFieldFileName.getText() + ".csv";
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
