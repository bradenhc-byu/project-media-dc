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

import com.byu.pmedia.model.StillFaceCode;
import com.byu.pmedia.model.StillFaceData;
import com.byu.pmedia.model.StillFaceImport;
import com.byu.pmedia.model.StillFaceTag;

import java.util.logging.Logger;

/**
 * StillFaceQueryBuilder
 * Responsible for building the SQL queries used to access and modify information in the database. Given different
 * database types (SQL, MySQL, etc.) these queries are able to adapt to the connection and constructed syntactically
 * correct queries for any of the available database connection types.
 *
 * @author Braden Hitchcock
 */
public class StillFaceQueryBuilder {

    /* Grab an instance of the logger */
    private final static Logger logger =Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /* Constants defining the table names in the database structure */
    private final String IMPORT_TABLE_NAME = "sf_imports";
    private final String DATA_TABLE_NAME = "sf_data";
    private final String CODES_TABLE_NAME = "sf_codes";
    private final String TAGS_TABLE_NAME = "sf_tags";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // SELECT statements
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a query that returns import data from the database based on the provided import ID. If the developer
     * wishes to get all import data entries, simply pass 0 in as a parameter
     *
     * @param importID The id of the entry to query. If 0, will return all entries
     * @return A string representing the query to be used to access import data in the database
     */
    public String buildSelectImportData(int importID){
        String importIDCondition;
        if(importID == 0){
            importIDCondition = "iid <> 0";
        }
        else{
            importIDCondition = "iid = " + importID;
        }
        return "SELECT i.*, t.value " +
                "FROM " + IMPORT_TABLE_NAME + " i " +
                "INNER JOIN " + TAGS_TABLE_NAME + " t ON t.tid = i.tid " +
                "WHERE " + importIDCondition;
    }

    /**
     * Creates a query that returns video data from the database based on the provided import ID. If the developer
     * wishes to get all data entries, simply pass 0 in as a parameter
     *
     * @param importID The id of the entry to query. If 0, will return all entries
     * @return A string representing the query to be used to access video data in the database
     */
    public String buildSelectCodeDataFromImport(int importID){
        String importIDCondition;
        if(importID == 0){
            importIDCondition = "iid <> 0";
        }
        else{
            importIDCondition = "iid = " + importID;
        }
        return "SELECT d.*, c.name, c.delimiter " +
                "FROM " + DATA_TABLE_NAME + " d " +
                "INNER JOIN " + CODES_TABLE_NAME + " c ON c.cid = d.cid " +
                "WHERE " + importIDCondition;
    }

    /**
     * Creates a query that returns video data from the database based on the provided import ID. If the developer
     * wishes to get all import data entries, simply pass 0 in as a parameter
     *
     * @param familyID The fid of the entry to query. If 0, will return all entries
     * @return A string representing the query to be used to access video data in the database
     */
    public String buildSelectCodeDataFromFamilyID(int familyID){
        String familyIDCondition;
        if(familyID == 0){
            familyIDCondition = "i.fid <> 0";
        }
        else{
            familyIDCondition = "i.fid = " + familyID;
        }
        return "SELECT d.*, c.name, c.delimiter " +
                "FROM " + DATA_TABLE_NAME + " d " +
                "INNER JOIN " + IMPORT_TABLE_NAME + " i ON i.iid = d.iid " +
                "INNER JOIN " + CODES_TABLE_NAME + " c ON d.cid = c.cid " +
                "WHERE " + familyIDCondition;
    }

    /**
     * Creates a query that returns code data from the database based on the provided code id. If the developer wishes
     * to get all codes in the database, simply pass 0 as a parameter.
     *
     * @param codeID The id of the code to access. If 0, will return all codes
     * @return A string representing the query to be used to access codes in the database
     */
    public String buildSelectCode(int codeID){
        String codeIDCondition;
        if(codeID == 0){
            codeIDCondition = "cid <> 0";
        }
        else{
            codeIDCondition = "cid = " + codeID;
        }
        return "SELECT * " +
                "FROM " + CODES_TABLE_NAME + " " +
                "WHERE " + codeIDCondition;
    }

    /**
     * Creates a query that returns tag data from the database based on the provided code id. If the developer wishes
     * to get all tags in the database, simply pass 0 as a parameter.
     *
     * @param tagID The id of the tag to access. If 0, will return all tags
     * @return A string representing the query to be used to access tags in the database
     */
    public String buildSelectTag(int tagID){
        String tagIDCondition;
        if(tagID == 0){
            tagIDCondition = "tid <> 0";
        }
        else{
            tagIDCondition = "tid = " + tagID;
        }
        return "SELECT * " +
                "FROM " + TAGS_TABLE_NAME + " " +
                "WHERE " + tagIDCondition;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INSERT statements
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a query that can be used to insert a new entry of import data into the database.
     *
     * @param importData The import data object to enter into the database
     * @return A string representing an INSERT query that puts the provided object in the database
     */
    public String buildInsertImport(StillFaceImport importData){
        return "INSERT INTO " + IMPORT_TABLE_NAME + " " +
                "(filename, syear, fid, pid, tid, alias, date) " +
                "VALUES('" + importData.getFilename() + "', " + importData.getYear() + ", " +
                importData.getFamilyID() + ", " + importData.getParticipantNumber() + ", " +
                importData.getTag().getTagID() + ", '" + importData.getAlias() + "', '" +
                importData.getDate().toString() + "')";
    }

    /**
     * Creates a query that can be used to insert a new entry of video data into the database.
     *
     * @param data The data object to enter into the database
     * @return A string representing an INSERT query that puts the provided object in the database
     */
    public String buildInsertData(StillFaceData data){
        return "INSERT INTO " + DATA_TABLE_NAME + " " +
                "(iid, time, duration, cid, comment) " +
                "VALUES(" + data.getImportID() + ", " + data.getTime() + ", " + data.getDuration() + ", " +
                data.getCode().getCodeID() + ", '" + data.getComment() + "')";
    }

    /**
     * Creates a query that can be used to insert a new entry of code data into the database.
     *
     * @param code The code data object to enter into the database
     * @return A string representing an INSERT query that puts the provided object in the database
     */
    public String buildInsertCode(StillFaceCode code){
        return "INSERT INTO " + CODES_TABLE_NAME + " " +
                "(name, delimiter) " +
                "VALUES('" + code.getName() + "', " + code.getDelimiterIndex() + ")";
    }

    /**
     * Creates a query that can be used to insert a new entry of tag data into the database.
     *
     * @param tag The tag data object to enter into the database
     * @return A string representing an INSERT query that puts the provided object in the database
     */
    public String buildInsertTag(StillFaceTag tag){
        return "INSERT INTO " + TAGS_TABLE_NAME + " " +
                "(value) " +
                "VALUES('" + tag.getTagValue() + "')";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // UPDATE statements
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a query that can be used to update an existing entry of import data in the database.
     *
     * @param importData The import data object to update into the database
     * @return A string representing an UPDATE query that puts the provided object in the database
     */
    public String buildUpdateImport(StillFaceImport importData){
        return "UPDATE " + IMPORT_TABLE_NAME + " " +
                "SET " +
                "syear = " + importData.getYear() + ", " +
                "fid = " + importData.getFamilyID() + ", " +
                "pid = " + importData.getParticipantNumber() + ", " +
                "tid = " + importData.getTag().getTagID() + ", " +
                "alias = '" + importData.getAlias() + "' " +
                "WHERE iid = " + importData.getImportID();
    }

    /**
     * Creates a query that can be used to update an existing entry of video data in the database.
     *
     * @param data The video data object to update into the database
     * @return A string representing an UPDATE query that puts the provided object in the database
     */
    public String buildUpdateCodeData(StillFaceData data){
        return "UPDATE " + DATA_TABLE_NAME + " " +
                "SET " +
                "time = " + data.getTime() + ", " +
                "duration = " + data.getDuration() + ", " +
                "cid = " + data.getCode().getCodeID() + ", " +
                "comment = '" + data.getComment() + "' " +
                "WHERE did = " + data.getDataID();
    }

    /**
     * Creates a query that can be used to update an existing entry of code data in the database.
     *
     * @param code The code data object to update into the database
     * @return A string representing an UPDATE query that puts the provided object in the database
     */
    public String buildUpdateCode(StillFaceCode code){
        return "UPDATE " + CODES_TABLE_NAME + " " +
                "SET " +
                "name = '" + code.getName() + "', " +
                "delimiter = " + code.getDelimiterIndex() + " " +
                "WHERE cid = " + code.getCodeID();
    }

    /**
     * Creates a query that can be used to update an existing entry of tag data in the database.
     *
     * @param tag The tag data object to update into the database
     * @return A string representing an UPDATE query that puts the provided object in the database
     */
    public String buildUpdateTag(StillFaceTag tag){
        return "UPDATE " + TAGS_TABLE_NAME + " " +
                "SET " +
                "value = '" + tag.getTagValue() + "' " +
                "WHERE tid = " + tag.getTagID();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // DELETE statements
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a query that will delete the entry with the provided ID.
     *
     * @param importID The id of the import entry to delete. Cannot pass 0.
     * @return A string representing the DELETE statement to remove the entry from the database
     */
    public String buildDeleteImport(int importID) { return "DELETE FROM " + IMPORT_TABLE_NAME + " WHERE iid = " + importID; }

    /**
     * Creates a query that will delete the entry/entries with the provided ID.
     *
     * @param importID The id of the import entry associated with the code entries to delete. Cannot pass 0.
     * @return A string representing the DELETE statement to remove the entry from the database
     */
    public String buildDeleteCodeDataFromImport(int importID) { return "DELETE FROM " + DATA_TABLE_NAME + " WHERE iid = " + importID; }

    /**
     * Creates a query that will delete the entry with the provided ID.
     *
     * @param code The code object with the id of the code entry to delete. Cannot pass 0.
     * @return A string representing the DELETE statement to remove the entry from the database
     */
    public String buildDeleteCode(StillFaceCode code){
        return "DELETE FROM " + CODES_TABLE_NAME + " WHERE cid = " + code.getCodeID();
    }

    /**
     * Creates a query that will delete the entry with the provided ID.
     *
     * @param tag The tag object with the id of the import entry to delete. Cannot pass 0.
     * @return A string representing the DELETE statement to remove the entry from the database
     */
    public String buildDeleteTag(StillFaceTag tag){
        return "DELETE FROM " + TAGS_TABLE_NAME + " WHERE tid = " + tag.getTagID();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CREATE TABLE statements
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a query that will create a new table for import data in the database
     *
     * @param mode The mode of the type of database table to create
     * @return A string representing a CREATE TABLE statement
     */
    public String buildCreateSFImportTable(DatabaseMode mode){
        String autoIncrement = getAutoIncrementSyntax(mode);
        return "CREATE TABLE " + IMPORT_TABLE_NAME + "\n" +
                "(\n" +
                "    iid INT PRIMARY KEY NOT NULL " + autoIncrement + ",\n" +
                "    filename VARCHAR(200) NOT NULL,\n" +
                "    syear INT NOT NULL,\n" +
                "    fid INT NOT NULL,\n" +
                "    pid INT NOT NULL,\n" +
                "    tid INT NOT NULL,\n" +
                "    alias VARCHAR(200),\n" +
                "    date DATE NOT NULL\n" +
                ")";
    }

    /**
     * Creates a query that will create a new table for video data in the database
     *
     * @param mode The mode of the type of database table to create
     * @return A string representing a CREATE TABLE statement
     */
    public String buildCreateSFDataTable(DatabaseMode mode){
        String autoIncrement = getAutoIncrementSyntax(mode);
        return "CREATE TABLE " + DATA_TABLE_NAME + "\n" +
                "(\n" +
                "    did INT PRIMARY KEY NOT NULL " + autoIncrement + ",\n" +
                "    iid INT NOT NULL,\n" +
                "    time INT NOT NULL,\n" +
                "    duration INT NOT NULL,\n" +
                "    cid INT NOT NULL,\n" +
                "    comment VARCHAR(500)\n" +
                ")";
    }

    /**
     * Creates a query that will create a new table for code data in the database
     *
     * @param mode The mode of the type of database table to create
     * @return A string representing a CREATE TABLE statement
     */
    public String buildCreateSFCodesTable(DatabaseMode mode){
        String autoIncrement = getAutoIncrementSyntax(mode);
        return "CREATE TABLE " + CODES_TABLE_NAME + "\n" +
                "(\n" +
                "    cid INT PRIMARY KEY NOT NULL " + autoIncrement + ",\n" +
                "    name VARCHAR(200) NOT NULL,\n" +
                "    delimiter INT NOT NULL\n" +
                ")";
    }

    /**
     * Creates a query that will create a new table for tag data in the database
     *
     * @param mode The mode of the type of database table to create
     * @return A string representing a CREATE TABLE statement
     */
    public String buildCreateSFTagsTable(DatabaseMode mode){
        String autoIncrement = getAutoIncrementSyntax(mode);
        return "CREATE TABLE " + TAGS_TABLE_NAME + "\n" +
                "(\n" +
                "    tid INT PRIMARY KEY NOT NULL " + autoIncrement + ",\n" +
                "    value VARCHAR(200) NOT NULL\n"+
                ")";
    }

    /**
     * Given a database mode, this will return a String using the correct syntax for creating an auto-increment
     * schema in a database table
     *
     * @param mode The DatabaseMode the syntax must conform to
     * @return A correctly formatted, auto-increment string
     */
    private String getAutoIncrementSyntax(DatabaseMode mode){
        switch (mode){
            case AZURE:
                return "IDENTITY(1,1)";

            case DERBY:
                return "GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)";

            default: return "AUTO INCREMENT";
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // DROP TABLE statements
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a query to drop the import table in the database
     * @return A string representation of a DROP TABLE query
     */
    public String buildDropSFImportTable(){
        return "DROP TABLE " + IMPORT_TABLE_NAME + "";
    }

    /**
     * Creates a query to drop the data table in the database
     * @return A string representation of a DROP TABLE query
     */
    public String buildDropSFDataTable(){
        return "DROP TABLE " + DATA_TABLE_NAME + "";
    }

    /**
     * Creates a query to drop the code table in the database
     * @return A string representation of a DROP TABLE query
     */
    public String buildDropSFCodesTable(){
        return "DROP TABLE " + CODES_TABLE_NAME + "";
    }

    /**
     * Creates a query to drop the tag table in the database
     * @return A string representation of a DROP TABLE query
     */
    public String buildDropSFTagsTable(){
        return "DROP TABLE " + TAGS_TABLE_NAME + "";
    }
}
