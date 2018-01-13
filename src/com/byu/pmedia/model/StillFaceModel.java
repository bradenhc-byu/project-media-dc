package com.byu.pmedia.model;

import com.byu.pmedia.config.StillFaceConfig;
import com.byu.pmedia.database.StillFaceDAO;
import com.byu.pmedia.log.PMLogger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StillFaceModel {

    private StillFaceDAO dao;

    private Map<Integer, StillFaceCode> stillFaceCodeMap = new HashMap<>();
    private Map<Integer, StillFaceTag> stillFaceTagMap = new HashMap<>();
    private Map<Integer, StillFaceImportData> stillFaceImportDataMap = new HashMap<>();
    private Map<Integer, StillFaceCodeData> stillFaceCodeDataMap = new HashMap<>();

    private static StillFaceModel singleton;

    public static StillFaceModel getInstance(){
        if(singleton == null){
            singleton = new StillFaceModel();
        }
        return singleton;
    }

    public boolean initialize(StillFaceDAO dao){
        try {
            this.dao = dao;
            this.dao.lockConnection();
            stillFaceCodeMap = this.dao.getCode(0);
            stillFaceTagMap = this.dao.getTag(0);
            stillFaceImportDataMap = this.dao.getImportData(0);
            if (StillFaceConfig.getInstance().getAsBoolean("model.cache")) {
                stillFaceCodeDataMap = this.dao.getCodeDataFromImport(0);
            }
            this.dao.unlockConnection();
            this.dao.closeConnection();
        }
        catch (SQLException e){
            PMLogger.getInstance().error("Error initializing model: " + e.getMessage());
            return false;
        }
        return true;
    }



}
