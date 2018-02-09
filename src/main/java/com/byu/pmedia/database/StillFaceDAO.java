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
import com.byu.pmedia.log.PMLoggerInitializer;
import com.byu.pmedia.model.*;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.index.radix.RadixTreeIndex;
import com.googlecode.cqengine.query.Query;

import java.sql.*;
import java.util.logging.Logger;

import static com.googlecode.cqengine.query.QueryFactory.*;

/**
 * StillFaceDAO
 * Database Access Object that provides access to the database for the rest of the program. This class can handle
 * connections to multiple types of databases, but the primary database used in this application is the
 * Apache Derby SQL database. Queries for the various requests are built inside the StillFaceQueryBuilder class, and
 * this class has an instance of a query builder.
 *
 * @author Braden Hitchcock
 */
public class StillFaceDAO {

    /* Grab an instance of the logger */
    private final static Logger logger =Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /* The database connection tied to this DAO */
    private IDatabaseConnection databaseConnection;

    /* The query builder this DAO uses to get data from and make changes to the database */
    private StillFaceQueryBuilder queryBuilder = new StillFaceQueryBuilder();

    /* Flag to keep track of wheter the connection to the database is 'locked', meaning that it cannot be closed.
     * This is useful because it allows the DAO to make several calls to the database without having to establish
     * and close connections every time. */
    private boolean connectionLocked = false;

    /**
     * Static method that constructs an instance of this object based on the configuration provided by the
     * StillFaceConfig singleton instance. Useful for initializing DAO's inside of controllers or other external
     * classes that only need temporary access to the DAO
     *
     * @return A new instance of the StillFaceDAO class constructed from configuration
     */
    public static StillFaceDAO generateFromConfig(){
        DatabaseMode mode = DatabaseMode.valueOf(StillFaceConfig.getInstance().getAsString("database.mode"));
        String host = StillFaceConfig.getInstance().getAsString("database.host");
        String port = StillFaceConfig.getInstance().getAsString("database.port");
        String dbname = StillFaceConfig.getInstance().getAsString("database.name");
        String user = StillFaceConfig.getInstance().getAsString("database.user");
        String password = StillFaceConfig.getInstance().getAsString("database.password");
        String filepath = StillFaceConfig.getInstance().getAsString("database.filepath");
        if(mode == null){
            logger.severe("Unable to generate DAO: config mode is null");
            return null;
        }
        switch (mode){
            case DERBY:
                DerbyDatabaseConnection derbyDatabaseConnection;

                if(!user.equals("") && !password.equals("")){
                    derbyDatabaseConnection = new DerbyDatabaseConnection(host, port, dbname, user, password);
                }
                else{
                    derbyDatabaseConnection = new DerbyDatabaseConnection(host, port, dbname);
                }
                return new StillFaceDAO(derbyDatabaseConnection);

            case AZURE:
                return new StillFaceDAO(new AzureDatabaseConnection(host, port, dbname, user, password));

                default:
                    logger.severe("Unable to generate DAO: unknown database mode");
                    return null;
        }
    }

    /**
     * Constructs a new instance of this class with the provided implementation of the IDatabaseConnection interface
     *
     * @param databaseConnection An implementation of the IDatabaseConnection interface that this instance will use to
     *                           connect to the database
     */
    public StillFaceDAO(IDatabaseConnection databaseConnection){
        this.databaseConnection = databaseConnection;
    }


    /**
     * Insert new import data into the database
     *
     * @param data A populated StillFaceImportData object
     *
     * @return The generated key of the successfully inserted data if successful. Otherwise -1.
     */
    public int insertImportData(StillFaceImport data){
        // Create the query
        String query = this.queryBuilder.buildInsertImport(data);

        // Execute it
        try{
            this.openConnection();
            PreparedStatement statement = this.databaseConnection.getConnection()
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            int generatedKey = -1;
            if(resultSet.next()){
                generatedKey = resultSet.getInt(1);
            }
            this.closeConnection();
            return generatedKey;
        }
        catch(SQLException e){
            logger.severe("Unable to insert import data: " + e.getMessage() + "\n" + query);
            return -1;
        }
    }

    /**
     * Retrieves import data from the database and returns it as a map containing StillFaceImportData
     *
     * @param importID The integer ID of the desired data. If 0, all data will be returned.
     *
     * @return A map of StillFaceImportData objects with their respective import IDs as the key
     */
    public IndexedCollection<StillFaceImport> getImportData(int importID){
        // Create the query
        String query = this.queryBuilder.buildSelectImportData(importID);

        // Initialize the map
        IndexedCollection<StillFaceImport> importDataCollection = new ConcurrentIndexedCollection<>();
        importDataCollection.addIndex(NavigableIndex.onAttribute(StillFaceImport.IMPORT_ID));
        importDataCollection.addIndex(RadixTreeIndex.onAttribute(StillFaceImport.FILENAME));
        importDataCollection.addIndex(NavigableIndex.onAttribute(StillFaceImport.YEAR));
        importDataCollection.addIndex(NavigableIndex.onAttribute(StillFaceImport.FAMILY_ID));
        importDataCollection.addIndex(NavigableIndex.onAttribute(StillFaceImport.PARTICIPANT_ID));
        importDataCollection.addIndex(HashIndex.onAttribute(StillFaceImport.TAG));
        importDataCollection.addIndex(RadixTreeIndex.onAttribute(StillFaceImport.ALIAS));
        importDataCollection.addIndex(HashIndex.onAttribute(StillFaceImport.DATE));

        // Get the data
        try {
            //Execute the query
            this.openConnection();
            Statement statement = this.databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            // Iterate over the results and populate the map with StillFaceImportData objects
            while(resultSet.next()){
                int iid = resultSet.getInt("iid");
                String filename = resultSet.getString("filename");
                int year = resultSet.getInt("syear");
                int familyID = resultSet.getInt("fid");
                int participantNumber = resultSet.getInt("pid");
                int tid = resultSet.getInt("tid");
                String tValue = resultSet.getString("value");
                String alias = resultSet.getString("alias");
                if(alias.equals("")){
                    alias = "none";
                }
                Date date = resultSet.getDate("date");
                importDataCollection.add(new StillFaceImport(iid, filename, year, familyID, participantNumber,
                        new StillFaceTag(tid, tValue), alias, date));
            }
            this.closeConnection();
            return importDataCollection;
        }
        catch (SQLException e){
            logger.severe("Could not get import data: " + e.getMessage());
            return null;
        }


    }

    /**
     * Updates existing import data information in the database
     *
     * @param data Populated StillFaceImportData object with the new values
     *
     * @return True if the update is successful. False otherwise.
     */
    public boolean updateImportData(StillFaceImport data){
        // Create the query
        String query = this.queryBuilder.buildUpdateImport(data);

        // Execute the query
        try{
            this.openConnection();
            PreparedStatement statement = this.databaseConnection.getConnection().prepareStatement(query);
            statement.executeUpdate();
            this.closeConnection();
            return true;
        }
        catch(SQLException e){
            logger.severe("Unable to update import data: " + e.getMessage());
            return false;
        }
    }

    /**
     * If a transaction goes wrong int he database, this method can be called to clean up any stray data and import
     * entries associated with the import ID
     *
     * @param importID The ID associated with entries to remove from the database
     */
    public boolean cleanImportData(int importID){
        String queryI = this.queryBuilder.buildDeleteImport(importID);
        String queryD = this.queryBuilder.buildDeleteCodeDataFromImport(importID);
        // Execute the query
        try{
            this.openConnection();
            PreparedStatement statement = this.databaseConnection.getConnection().prepareStatement(queryD);
            statement.executeUpdate();
            statement = this.databaseConnection.getConnection().prepareStatement(queryI);
            statement.executeUpdate();
            this.closeConnection();
            return true;
        }
        catch(SQLException e){
            logger.severe("Unable to update import data: " + e.getMessage());
            return false;
        }
    }


    /**
     * Inserts new code data into the database from a populated StillFaceCodeData object
     *
     * @param data The populated data object
     *
     * @return The newly generated key if successful. -1 otherwise.
     */
    public int insertCodeData(StillFaceData data){
        // Verify the code (cid) is valid. If not create a new entry
        boolean exists = false;
        Query<StillFaceCode> codeQuery = equal(StillFaceCode.NAME, data.getCode().getName());
        StillFaceModel.getInstance().lockDatabaseConnection();
        for(StillFaceCode code : StillFaceModel.getInstance().getCodeCollection().retrieve(codeQuery)){
            exists = true;
            data.getCode().setCodeID(code.getCodeID());
            break;
        }
        if(!exists){
            // Insert the new value into the DB
            int key = insertNewCode(data.getCode());
            if(key < 0){
                logger.severe("Failed to create new code entry for unknown code");
                return -1;
            }
            data.getCode().setCodeID(key);
        }
        StillFaceModel.getInstance().unlockDatabaseConnection();

        // Create the query
        String query = this.queryBuilder.buildInsertData(data);

        // Execute the query
        try{
            this.openConnection();
            PreparedStatement statement = this.databaseConnection.getConnection()
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            int generatedKey = -1;
            while(resultSet.next()){
                generatedKey = resultSet.getInt(1);
            }
            this.closeConnection();
            return generatedKey;
        }
        catch(SQLException e){
            logger.severe("Unable to insert code data: " + e.getMessage());
            return -1;
        }
    }


    /**
     * Retrieves coded video data for Still face based on the import associated with the data. If 0 is provided, all
     * available code data is returned.
     *
     * @param importID The id of the import whose data we want to retrieve
     *
     * @return A map of integer data ID, StillFaceCodeData object pairs if the query succeeds. Null otherwise.
     */
    public IndexedCollection<StillFaceData> getCodeDataFromImport(int importID){
        // Create the query
        String query = this.queryBuilder.buildSelectCodeDataFromImport(importID);

        // Prepare an indexed collection of StillFaceCodeData
        IndexedCollection<StillFaceData> dataCollection = new ConcurrentIndexedCollection<>();
        dataCollection.addIndex(NavigableIndex.onAttribute(StillFaceData.DATA_ID));
        dataCollection.addIndex(NavigableIndex.onAttribute(StillFaceData.IMPORT_ID));
        dataCollection.addIndex(NavigableIndex.onAttribute(StillFaceData.TIME));
        dataCollection.addIndex(NavigableIndex.onAttribute(StillFaceData.DURATION));
        dataCollection.addIndex(HashIndex.onAttribute(StillFaceData.CODE));
        dataCollection.addIndex(RadixTreeIndex.onAttribute(StillFaceData.COMMENT));

        // Execute the query
        try{
            this.openConnection();
            Statement statement = this.databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                int dataID = resultSet.getInt("did");
                int iid = resultSet.getInt("iid");
                int time = resultSet.getInt("time");
                int duration = resultSet.getInt("duration");
                int codeID = resultSet.getInt("cid");
                String comment = resultSet.getString("comment");
                String codeName = resultSet.getString("name");
                int codeDelimiter = resultSet.getInt("delimiter");
                dataCollection.add(new StillFaceData(dataID, iid, time, duration,
                        new StillFaceCode(codeID, codeName, codeDelimiter), comment));
            }
            this.closeConnection();
            return dataCollection;
        }
        catch(SQLException e){
            logger.severe("Unable to retrieve code data: " + e.getMessage());
            return null;
        }
    }


    /**
     * Retrieves coded video data for entries that share a common family id. If 0 is provided, all available code data
     * is returned.
     *
     * @param familyID The family ID
     *
     * @return A Map of integer data ID, StillFaceCodeData object pairs if successful. Null otherwise.
     */
    public IndexedCollection<StillFaceData> getCodeDataFromFamilyID(int familyID){
        // Create the query
        String query = this.queryBuilder.buildSelectCodeDataFromFamilyID(familyID);

        // Prepare an indexed collection of StillFaceCodeData
        IndexedCollection<StillFaceData> dataCollection = new ConcurrentIndexedCollection<>();
        dataCollection.addIndex(NavigableIndex.onAttribute(StillFaceData.DATA_ID));
        dataCollection.addIndex(NavigableIndex.onAttribute(StillFaceData.IMPORT_ID));
        dataCollection.addIndex(NavigableIndex.onAttribute(StillFaceData.TIME));
        dataCollection.addIndex(NavigableIndex.onAttribute(StillFaceData.DURATION));
        dataCollection.addIndex(HashIndex.onAttribute(StillFaceData.CODE));
        dataCollection.addIndex(RadixTreeIndex.onAttribute(StillFaceData.COMMENT));

        // Execute the query
        try{
            this.openConnection();
            Statement statement = this.databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                int dataID = resultSet.getInt("did");
                int iid = resultSet.getInt("iid");
                int time = resultSet.getInt("time");
                int duration = resultSet.getInt("duration");
                int codeID = resultSet.getInt("cid");
                String comment = resultSet.getString("comment");
                String codeName = resultSet.getString("name");
                int codeDelimiter = resultSet.getInt("delimiter");
                dataCollection.add(new StillFaceData(dataID, iid, time, duration,
                        new StillFaceCode(codeID, codeName, codeDelimiter), comment));
            }
            this.closeConnection();
            return dataCollection;
        }
        catch(SQLException e){
            logger.severe("Unable to retrieve code data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates existing coded video data in the database
     *
     * @param data The data entry to update in the database
     *
     * @return True if the update is successful. False otherwise.
     */
    public boolean updateCodeData(StillFaceData data){
        // Create the query
        String query = this.queryBuilder.buildUpdateCodeData(data);

        // Execute the query
        try{
            this.openConnection();
            PreparedStatement statement = this.databaseConnection.getConnection().prepareStatement(query);
            statement.executeUpdate();
            this.closeConnection();
            return true;
        }
        catch(SQLException e){
            logger.severe("Unable to update code data: " + e.getMessage());
            return false;
        }
    }


    /**
     * Creates a new entry for a code type in the database
     *
     * @param code The new code to insert
     *
     * @return The generated key of the new entry if successful. -1 otherwise.
     */
    public int insertNewCode(StillFaceCode code){
        // Create the query
        String query = this.queryBuilder.buildInsertCode(code);

        // Execute the query
        try{
            this.openConnection();
            PreparedStatement statement = this.databaseConnection.getConnection()
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            int generatedKey = -1;
            while(resultSet.next()){
                generatedKey = resultSet.getInt(1);
            }
            this.closeConnection();
            return generatedKey;
        }
        catch(SQLException e){
            logger.severe("Unable to insert new code: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Retrieves information about the code with the provided codeID.
     *
     * @param codeID The id of the code to retrieve. If 0 is provided, all available codes are returned.
     *
     * @return A populated map of integer code ID, StillFaceCode object pairs if successful. Null otherwise.
     */
    public IndexedCollection<StillFaceCode> getCode(int codeID){
        // Create the query
        String query = this.queryBuilder.buildSelectCode(codeID);

        // Prepare the map
        IndexedCollection<StillFaceCode> codeCollection = new ConcurrentIndexedCollection<>();
        codeCollection.addIndex(NavigableIndex.onAttribute(StillFaceCode.CODE_ID));
        codeCollection.addIndex(RadixTreeIndex.onAttribute(StillFaceCode.NAME));

        // Execute the query
        try{
            this.openConnection();
            Statement statement = this.databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                int cid = resultSet.getInt("cid");
                String name = resultSet.getString("name");
                int delimiter = resultSet.getInt("delimiter");
                codeCollection.add(new StillFaceCode(cid, name, delimiter));
            }
            this.closeConnection();
            return codeCollection;
        }
        catch(SQLException e){
            logger.severe("Unable to retrieve code: " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates existing information about an available code for StillFace
     *
     * @param code A StillFaceCode object populated with the new information
     *
     * @return True if successful, false otherwise.
     */
    public boolean updateExistingCode(StillFaceCode code){
        // Create the query
        String query = this.queryBuilder.buildUpdateCode(code);

        // Execute the query
        try{
            this.openConnection();
            PreparedStatement statement = this.databaseConnection.getConnection().prepareStatement(query);
            statement.executeUpdate();
            this.closeConnection();
            return true;
        }
        catch(SQLException e){
            logger.severe("Unable to update code: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes an existing code entry in the database
     *
     * @param code The code entry to delete
     *
     * @return True if successful. False otherwise.
     */
    public boolean deleteExistingCode(StillFaceCode code){
        // Create the query
        String query = this.queryBuilder.buildDeleteCode(code);

        // Execute the query
        try{
            this.openConnection();
            PreparedStatement statement = this.databaseConnection.getConnection().prepareStatement(query);
            statement.execute();
            this.closeConnection();
            return true;
        }
        catch(SQLException e){
            logger.severe("Unable to delete code: " + e.getMessage());
            return false;
        }
    }


    /**
     * Creates a new entry for tag information in the database
     *
     * @param tag The tag information to insert. If 0, all tags will be returned.
     *
     * @return The new entry's generated key if successful. -1 otherwise.
     */
    public int insertNewTag(StillFaceTag tag){
        // Create the query
        String query = this.queryBuilder.buildInsertTag(tag);

        // Execute the query
        try{
            this.openConnection();
            PreparedStatement statement = this.databaseConnection.getConnection()
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.execute();
            ResultSet resultSet = statement.getGeneratedKeys();
            int generatedKey = -1;
            while(resultSet.next()){
                generatedKey = resultSet.getInt(1);
            }
            this.closeConnection();
            return generatedKey;
        }
        catch(SQLException e){
            logger.severe("Unable to insert new tag: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Retrieves tag information from the database
     *
     * @param tagID The id of the tag to get information about. If 0, all tag information will be returned.
     *
     * @return A map of integer tag ID, StillFaceTag object pairs if successful. Null otherwise.
     */
    public IndexedCollection<StillFaceTag> getTag(int tagID){
        // Create the query
        String query = this.queryBuilder.buildSelectTag(tagID);

        // Prepare the map
        IndexedCollection<StillFaceTag> tagCollection = new ConcurrentIndexedCollection<>();
        tagCollection.addIndex(NavigableIndex.onAttribute(StillFaceTag.TAG_ID));
        tagCollection.addIndex(RadixTreeIndex.onAttribute(StillFaceTag.TAG_VALUE));

        // Execute the query
        try{
            this.openConnection();
            Statement statement = this.databaseConnection.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                int tid = resultSet.getInt("tid");
                String value = resultSet.getString("value");
                tagCollection.add(new StillFaceTag(tid, value));
            }
            this.closeConnection();
            return tagCollection;
        }
        catch(SQLException e){
            logger.severe("Unable to retrieve tag information: " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates existing tag information in the database
     *
     * @param tag The populated tag object to provide updates
     *
     * @return True if successful, False otherwise.
     */
    public boolean updateExistingTag(StillFaceTag tag){
        // Create the query
        String query = this.queryBuilder.buildUpdateTag(tag);

        // Execute the query
        try{
            this.openConnection();
            PreparedStatement statement = this.databaseConnection.getConnection().prepareStatement(query);
            statement.executeUpdate();
            this.closeConnection();
            return true;
        }
        catch(SQLException e){
            logger.severe("Unable to update tag: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes an existing tag entry in the database
     *
     * @param tag The tag entry to delete
     *
     * @return True if successful. False otherwise.
     */
    public boolean deleteExistingTag(StillFaceTag tag){
        // Create the query
        String query = this.queryBuilder.buildDeleteTag(tag);

        // Execute the query
        try{
            this.openConnection();
            PreparedStatement statement = this.databaseConnection.getConnection().prepareStatement(query);
            statement.execute();
            this.closeConnection();
            return true;
        }
        catch(SQLException e){
            logger.severe("Unable to delete tag: " + e.getMessage());
            return false;
        }
    }

    public boolean createTables(DatabaseMode mode){
        // Initialize all the queries
        String createImportTableQuery = this.queryBuilder.buildCreateSFImportTable(mode);
        String createDataTableQuery = this.queryBuilder.buildCreateSFDataTable(mode);
        String createCodeTableQuery = this.queryBuilder.buildCreateSFCodesTable(mode);
        String createTagTableQuery = this.queryBuilder.buildCreateSFTagsTable(mode);

        // Execute the queries
        try{
            this.openConnection();
            Statement statement = this.databaseConnection.getConnection().createStatement();
            statement.executeUpdate(createImportTableQuery);
            statement.executeUpdate(createDataTableQuery);
            statement.executeUpdate(createCodeTableQuery);
            statement.executeUpdate(createTagTableQuery);
            this.closeConnection();
            return true;
        }
        catch(SQLException e){
            logger.severe("Unable to create database table: " + e.getMessage());
            return false;
        }
    }

    public boolean dropTables(){
        // Initialize all the queries
        String dropImportTableQuery = this.queryBuilder.buildDropSFImportTable();
        String dropDataTableQuery = this.queryBuilder.buildDropSFDataTable();
        //String dropCodeTableQuery = this.queryBuilder.buildDropSFCodesTable();
        //String dropTagTableQuery = this.queryBuilder.buildDropSFTagsTable();

        // Execute the queries
        try{
            this.openConnection();
            Statement statement = this.databaseConnection.getConnection().createStatement();
            statement.executeUpdate(dropImportTableQuery);
            statement.executeUpdate(dropDataTableQuery);
            //statement.executeUpdate(dropCodeTableQuery);
            //statement.executeUpdate(dropTagTableQuery);
            this.closeConnection();
            return true;
        }
        catch(SQLException e){
            logger.severe("Unable to drop database table: " + e.getMessage());
            return false;
        }
    }


    /**
     * Opens a database connection to this DAO's associated database. If unsuccessful, it will throw an SQLException
     *
     * @throws SQLException
     */
    public void openConnection() throws SQLException{
        if(!this.databaseConnection.connectionIsEstablished()){
            try{
                this.databaseConnection.establish();
            }
            catch(SQLException e){
                logger.severe("DAO unable to open database connection");
                throw e;
            }
        }
    }

    /**
     * Closes a database connection to this DAO's associated database. If unsuccessful, it will throw an SQLException
     *
     * @throws SQLException
     */
    public void closeConnection() throws SQLException{
        if(this.databaseConnection.connectionIsEstablished() && !this.connectionLocked){
            try{
                this.databaseConnection.close();
            }
            catch(SQLException e){
                logger.severe("DAO unable to close database connection");
                throw e;
            }
        }
    }

    /**
     * Prevents the database connection for this DAO to be closed once established. This means that calls to this
     * object's closeConnection() method will do nothing.
     */
    public void lockConnection(){
        this.connectionLocked = true;
    }

    /**
     * Unlocks the database connection and allows it to be closed after being established. This means that calls to
     * this object's closeConnection() method will attempt to close the database connection.
     */
    public void unlockConnection(){
        this.connectionLocked = false;
    }

    public boolean isDatabaseInitialized(){
        // Set up the queries
        String checkImportTable = "SELECT COUNT(*) FROM sf_imports";
        String checkDataTable = "SELECT COUNT(*) FROM sf_data";
        String checkCodesTable = "SELECT COUNT(*) FROM sf_codes";
        String checkTagsTable = "SELECT COUNT(*) FROM sf_tags";

        try{
            this.openConnection();
            this.databaseConnection.getConnection().createStatement().executeQuery(checkImportTable);
            this.databaseConnection.getConnection().createStatement().executeQuery(checkDataTable);
            this.databaseConnection.getConnection().createStatement().executeQuery(checkCodesTable);
            this.databaseConnection.getConnection().createStatement().executeQuery(checkTagsTable);
            this.closeConnection();
            return true;
        }
        catch (SQLException e){
            logger.severe("Caught exception checking database status: " + e.getMessage());
            return false;
        }
    }


}
