package com.byu.pmedia.controller;

import com.byu.pmedia.database.StillFaceDAO;
import com.byu.pmedia.model.*;
import com.byu.pmedia.parser.CodedVideoCSVParser;
import com.byu.pmedia.view.DataCenterClientGUI;
import com.byu.pmedia.view.DataCenterSplashScreen;

import javax.swing.*;

public class DataCenterController {

    private StillFaceDAO dao;
    private DataCenterClientGUI gui;

    public DataCenterController(StillFaceDAO dao, DataCenterClientGUI gui){
        this.dao = dao;
        this.gui = gui;
    }

    public void importCSVData(StillFaceImportData importData){
        StillFaceVideoData videoData = new StillFaceVideoData();
        CodedVideoCSVParser parser = new CodedVideoCSVParser();
        parser.parseFromCSVIntoCodedVideoData(importData.getFilename(), videoData);
        this.dao.insertImportData(importData);
        for(StillFaceCodeData data : videoData.getData()){
            this.dao.insertCodeData(data);
        }
        StillFaceModel.getInstance().refreshImportData();
        StillFaceModel.getInstance().refreshVideoData();
        this.gui.update();
    }

    public void save(){

    }
}
