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

/**
 * DataCenterController
 * Provides initialization for JavaFX elements as part of the main DataCenter GUI, as well as the program's responses
 * to user interaction with the GUI.
 *
 * @author Braden Hitchcock
 */
public class DataCenterController implements Initializable {

    /* These variables are linked to an FXML stylesheet to generate the view for which this
    * controller is used.
    * When adding new elements, be sure to use the @FXML attribute with a private variable definition to follow
    * good coding standards and practices. */
    @FXML private Button buttonGetResults;
    @FXML private ChoiceBox<StillFaceTag> choiceBoxTag;
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

    /**
     * Initializes the controller and sets properties for the view elements when the FXMLLoader.load() method
     * is called. This method implements the Initializable interface method.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initializeDataTable();

        initializeSearchTab();

        // Setup miscellaneous FXML element properties
        tilePaneSummary.setHgap(10);
        tilePaneSummary.setVgap(10);
        tilePaneSummary.setTileAlignment(Pos.CENTER_LEFT);
        tilePaneSummary.setOrientation(Orientation.VERTICAL);

        buttonSaveChanges.setDisable(true);
        buttonExportToCSV.setDisable(true);

        // Setup miscellaneous listeners
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

    /**
     * Defines properties for FXML elements that are a part of the table displaying StillFaceData from the database.
     */
    private void initializeDataTable(){
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
                StillFaceModel.getInstance().addEditedData(data);
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
                StillFaceModel.getInstance().addEditedData(data);
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
                StillFaceModel.getInstance().addEditedData(data);
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
                StillFaceModel.getInstance().addEditedData(data);
                buttonSaveChanges.setDisable(false);
            }
        });

        tableData.setEditable(true);
        tableData.getColumns().addAll(tableColumnID, tableColumnTime, tableColumnDuration, tableColumnCode,
                tableColumnComment);
    }

    /**
     * Defines properties and attributes for FXML elements associated with the search functionality that allows the
     * user to search for StillFaceData elements that share specified properties and values.
     */
    private void initializeSearchTab(){
        // Format the text fields so that they will only accept numbers as input
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

        choiceBoxTag.setItems(FXCollections.observableList(StillFaceModel.getTagList()));
    }

    /**
     * Listener triggered when the 'Import' button detects an action from the user. This method will open a new window
     * that allows the user to select a data file to import and define metadata associated with the import.
     *
     * @param actionEvent The event detected by the listener
     */
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

    /**
     * Listener triggered when the 'Settings' button detects an action from the user. This method will open a new window
     * that allows the user to change program settings and configuration.
     *
     * @param actionEvent The event detected by the listener
     */
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

    /**
     * Listener triggered when the 'Sync' button detects and action from the user. If changes made by the user have
     * not been saved, a dialog will appear informing the user that proceeding will cause all unsaved changes to be
     * lost, and then ask the user if they want to proceed. If the user desires to proceed, the data internal to the
     * model will be updated with the latest information from the database, and the GUI view will be reset.
     *
     * @param actionEvent The event detected by the listener
     */
    @FXML
    private void onSync(ActionEvent actionEvent) {
        if(StillFaceModel.getInstance().getEditedDataMap().size() > 0){
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

    /**
     * Refreshes the internal data model with the latest data from the database and clears any changes the user
     * has not saved into the database.
     */
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

    /**
     * Listener triggered when the 'Save Changes' button detects and action from the user. It will attempt to update
     * the database with any changes the user has made to the in-memory data of the model.
     *
     * @param actionEvent The event detected by the listener
     */
    @FXML
    private void onSaveChanges(ActionEvent actionEvent) {
        new StillFaceSaveTask(new StillFaceTaskCallback() {
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

    /**
     * Listener triggered when the 'Export' button detects an action from the user. It will attempt to export the
     * presently visible data to a CSV file that the user selects.
     *
     * @param actionEvent The event detected by the listener
     */
    @FXML
    private void onExport(ActionEvent actionEvent) {
        Optional<String> result = showExportDialog();
        result.ifPresent(exportFile -> {
            new StillFaceExportTask(exportFile, new StillFaceTaskCallback() {
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

    /**
     * Listener triggered when the 'Get Results' query button detects an action from the user. It will take the
     * parameters entered by the user and search the in-memory data for StillFaceData that matches the query (if the
     * data is cached), or query the database to get the results (if the data is not cached).
     *
     * @param actionEvent The event detected by the listener
     */
    @FXML
    private void onGetResults(ActionEvent actionEvent) {
        updateQueryData();
    }

    /**
     * Updates the visible list of imports in the GUI with the latest entries from the database. NOTE: this does not
     * sync with the database unless the program is not in cache mode.
     */
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

    /**
     * Restores the GUI view to the way it was when the user first opened the program
     */
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

    /**
     * Updates the data visible in the GUI data table when an import is selected from the list view.
     *
     * @param importID The ID of the import that was selected by the user
     */
    private void updateData(int importID){

        List<StillFaceData> dataList = StillFaceModel.getInstance().getVisibleDataList();

        PMLogger.getInstance().info("Filling table with import data. Import ID: " + importID);

        dataList.clear();

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
            dataList.add(data);

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

        PMLogger.getInstance().info("Table data size: " + StillFaceModel.getInstance().getVisibleDataList().size());

        ObservableList<StillFaceData> data = FXCollections.observableArrayList(dataList);

        tableData.setItems(data);

        // Put in some summary data
        Map.Entry<String, Integer> maxEntry = null;
        for(Map.Entry<String, Integer> entry : mostCommonCode.entrySet()){
            if(maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0){
                maxEntry = entry;
            }
        }
        tilePaneSummary.getChildren().clear();
        tilePaneSummary.getChildren().add(new Text("Number of codes: " + dataList.size()));
        tilePaneSummary.getChildren().add(new Text("Codes used: " + mostCommonCode.keySet().size()));
        String mostCommon = (maxEntry != null && maxEntry.getKey() != null) ? maxEntry.getKey() : "";
        tilePaneSummary.getChildren().add(new Text("Most common code: " + mostCommon ));
        tilePaneSummary.getChildren().add(new Text("Total duration (sec): " + dataList.get(dataList.size() - 1).getTime()/1000));

        buttonExportToCSV.setDisable(false);

    }

    /**
     * Updates the visible data in the data table based on query parameters specified by the user.
     */
    private void updateQueryData(){
        List<StillFaceData> dataList = StillFaceModel.getInstance().getVisibleDataList();
        PMLogger.getInstance().info("Performing search");
        dataList.clear();
        Query<StillFaceImport> importQuery = not(equal(StillFaceImport.IMPORT_ID, 0));
        if(checkBoxYear.isSelected()){
            importQuery = and(importQuery, equal(StillFaceImport.YEAR, Integer.parseInt(textFieldYear.getText())));
        }
        if(checkBoxFamilyID.isSelected()){
            importQuery = and(importQuery, equal(StillFaceImport.FAMILY_ID, Integer.parseInt(textFieldFamilyID.getText())));
        }
        if(checkBoxParticipantID.isSelected()){
            importQuery = and(importQuery, equal(StillFaceImport.PARTICIPANT_ID, Integer.parseInt(textFieldParticipantID.getText())));
        }
        if(checkBoxTag.isSelected()){
            importQuery = and(importQuery, equal(StillFaceImport.TAG, choiceBoxTag.getValue()));
        }
        Query<StillFaceData> dataQuery = equal(StillFaceData.DATA_ID, Integer.MAX_VALUE);
        for(StillFaceImport i : StillFaceModel.getInstance().getImportDataCollection().retrieve(importQuery)){
            dataQuery = or(dataQuery, equal(StillFaceData.IMPORT_ID, i.getImportID()));
        }
        // Set up some maps to hold summary data
        Map<String, Integer> mostCommonCode = new HashMap<>();
        for(StillFaceData d : StillFaceModel.getInstance().getDataCollection().retrieve(dataQuery,
                queryOptions(orderBy(ascending(StillFaceData.DATA_ID))))){
            dataList.add(d);
            // Code stats
            String codeName = d.getCode().getName();
            if(mostCommonCode.containsKey(codeName)){
                int count = mostCommonCode.get(codeName);
                mostCommonCode.put(codeName, count + 1);
            }
            else{
                mostCommonCode.put(codeName, 1);
            }
        }
        PMLogger.getInstance().info("Table data size: " + dataList.size());

        ObservableList<StillFaceData> data = FXCollections.observableArrayList(dataList);

        tableData.setItems(data);

        // Put in some summary data
        Map.Entry<String, Integer> maxEntry = null;
        for(Map.Entry<String, Integer> entry : mostCommonCode.entrySet()){
            if(maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0){
                maxEntry = entry;
            }
        }

        labelDataTitle.setText("Search Results");

        tilePaneSummary.getChildren().clear();
        tilePaneSummary.getChildren().add(new Text("Number of codes: " + dataList.size()));
        tilePaneSummary.getChildren().add(new Text("Codes used: " + mostCommonCode.keySet().size()));
        String mostCommon = (maxEntry != null && maxEntry.getKey() != null) ? maxEntry.getKey() : "";
        tilePaneSummary.getChildren().add(new Text("Most common code: " + mostCommon ));

        buttonExportToCSV.setDisable(false);
        labelImportDate.setText("");
        labelParticipantID.setText("");
        labelYear.setText("");
        labelTag.setText("");
    }

    /**
     * Clears any changes made by the user. The changes will not be saved.
     */
    private void clearEdits(){
        StillFaceModel.getInstance().getEditedDataMap().clear();
        PMLogger.getInstance().info("Edits cleared");
    }

    /**
     * Creates and shows a dialog that allows the user to select what directory they want to export the visible data
     * to and provide a name for the file.
     *
     * @return An Optional object that contains the full filepath to the file the data should be saved to if the
     *         user has correctly filled out the dialog, otherwise it is null if the user cancels the export
     */
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
