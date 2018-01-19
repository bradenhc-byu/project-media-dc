package com.byu.pmedia.controller;

import com.byu.pmedia.database.StillFaceDAO;
import com.byu.pmedia.log.PMLogger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.IOException;

public class DataCenterController extends Application{

    public ListView listViewExplorer;
    public Button buttonImportCSVData;
    public Button buttonSettings;
    public Button buttonExportToCSV;
    public Button buttonPlot;
    public Label labelDataTitle;
    public TilePane tilePaneSummary;
    public TableView tableData;

    private StillFaceDAO dao;

    @Override
    public void init(){

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.dao = StillFaceDAO.generateFromConfig();
    }

    @Override
    public void stop(){

    }

    public void onImportCSVData(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/com/byu/pmedia/view/stillfaceimportgui.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Import CSV");
            stage.setScene(scene);
            stage.showAndWait();
        }
        catch(IOException e){
            PMLogger.getInstance().error("Unable to open import dialogue: " + e.getMessage());
        }
    }

    public void onSettings(ActionEvent actionEvent) {

    }

    public void onSync(ActionEvent actionEvent) {

    }
}
