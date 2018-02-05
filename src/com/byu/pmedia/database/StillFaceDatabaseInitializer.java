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
package com.byu.pmedia.database;

import com.byu.pmedia.config.StillFaceConfig;

/**
 * StillFaceDatabaseInitializer
 * Provides a wrapper for the functionality that checks to make sure the required database structure exists on the
 * connection specified in configuration. If the structure does not exist, this will create that structure.
 *
 * @author Braden Hitchcock
 */
public class StillFaceDatabaseInitializer {

    /* The internal database access object that is used to verify the structure of the database on the connection
     * and initialize the database*/
    private StillFaceDAO dao;

    /**
     * Default constructor. Creates a new instance of this class.
     */
    public StillFaceDatabaseInitializer(){ }

    /**
     * Given a specific mode, initializes the database structure using a connection established with that mode.
     *
     * @param mode The DatabaseMode type to initialize
     * @return True if the initialization was successful. False otherwise.
     */
    public boolean initialize(DatabaseMode mode){
        switch(mode){
            case DERBY:
                return initializeDerbyDatabase();
            case AZURE:
                return initializeAzureDatabase();
            default:
                return false;
        }

    }

    /**
     * When the database mode is DERBY, this method will use the configuration file to attempt to create a
     * DerbyDatabaseConnection instance and verify the required database structure
     *
     * @return True if the initialization is successful, false otherwise.
     */
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

    /**
     * When the database mode is AZURE, this method will use the configuration file to attempt to create a
     * AzureDatabaseConnection instance and verify the required database structure
     *
     * @return True if the initialization is successful, false otherwise.
     */
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

    /**
     * Provides access to the DAO used to initialize the database structure
     *
     * @return A StillFaceDAO instance that matches configuration if it has been created, null otherwise.
     */
    public StillFaceDAO getDAO(){
        return this.dao;
    }
}
