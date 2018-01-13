package com.byu.pmedia.database;

import com.byu.pmedia.config.StillFaceConfig;

public class StillFaceDatabaseInitializer {


    private StillFaceDAO dao;

    public StillFaceDatabaseInitializer(){

    }

    public boolean initialize(DatabaseMode mode){
        switch(mode){
            case DERBY:
                return initializeDerbyDatabase();
            case AZURE:
                return initializeAzureDatabase();
            case HSQLDB:
                return initializeHsqlDatabase();
            default:
                return false;
        }

    }

    private boolean initializeDerbyDatabase(){
        String host = StillFaceConfig.getInstance().getAsString("database.host");
        String port = StillFaceConfig.getInstance().getAsString("database.port");
        String dbname = StillFaceConfig.getInstance().getAsString("database.name");
        String user = StillFaceConfig.getInstance().getAsString("database.user");
        String password = StillFaceConfig.getInstance().getAsString("database.password");

        DerbyDatabaseConnection derbyDatabaseConnection;

        if(!user.equals("") && !password.equals("")){
            derbyDatabaseConnection = new DerbyDatabaseConnection(host, port, dbname, user, password);
        }
        else{
            derbyDatabaseConnection = new DerbyDatabaseConnection(host, port, dbname);
        }
        this.dao = new StillFaceDAO(derbyDatabaseConnection);

        return this.dao.isDatabaseInitialized() || this.dao.createTables(DatabaseMode.DERBY);
    }

    private boolean initializeAzureDatabase(){
        String host = StillFaceConfig.getInstance().getAsString("database.host");
        String port = StillFaceConfig.getInstance().getAsString("database.port");
        String dbname = StillFaceConfig.getInstance().getAsString("database.name");
        String user = StillFaceConfig.getInstance().getAsString("database.user");
        String password = StillFaceConfig.getInstance().getAsString("database.password");

        AzureDatabaseConnection azureDatabaseConnection;

        if(!user.equals("") && !password.equals("")){
            azureDatabaseConnection = new AzureDatabaseConnection(host, port, dbname, user, password);
            this.dao = new StillFaceDAO(azureDatabaseConnection);
            return this.dao.isDatabaseInitialized() || this.dao.createTables(DatabaseMode.AZURE);
        }
        else{
            return false;
        }
    }

    private boolean initializeHsqlDatabase(){
        String filepath = StillFaceConfig.getInstance().getAsString("database.filepath");
        String dbname = StillFaceConfig.getInstance().getAsString("database.name");
        String user = StillFaceConfig.getInstance().getAsString("database.user");
        String password = StillFaceConfig.getInstance().getAsString("database.password");

        HsqlDatabaseConnection hsqlDatabaseConnection;

        if(!user.equals("") && !password.equals("")){
            hsqlDatabaseConnection = new HsqlDatabaseConnection(filepath, dbname, user, password);
        }
        else{
            hsqlDatabaseConnection = new HsqlDatabaseConnection(filepath, dbname);
        }
        this.dao = new StillFaceDAO(hsqlDatabaseConnection);
        return this.dao.isDatabaseInitialized() || this.dao.createTables(DatabaseMode.HSQLDB);
    }

    public StillFaceDAO getDAO(){
        return this.dao;
    }




}
