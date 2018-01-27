package com.byu.pmedia.database;

import com.byu.pmedia.model.StillFaceCode;
import com.byu.pmedia.model.StillFaceData;
import com.byu.pmedia.model.StillFaceImport;
import com.byu.pmedia.model.StillFaceTag;


public class StillFaceQueryBuilder {

    private final String IMPORT_TABLE_NAME = "sf_imports";
    private final String DATA_TABLE_NAME = "sf_data";
    private final String CODES_TABLE_NAME = "sf_codes";
    private final String TAGS_TABLE_NAME = "sf_tags";

    //
    // SELECT statements
    //

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

    public String buildSelectCodeDataFromImport(int importID){
        String importIDCondition;
        if(importID == 0){
            importIDCondition = "iid <> 0";
        }
        else{
            importIDCondition = "iid = " + importID;
        }
        return "SELECT d.*, c.name " +
                "FROM " + DATA_TABLE_NAME + " d " +
                "INNER JOIN " + CODES_TABLE_NAME + " c ON c.cid = d.cid " +
                "WHERE " + importIDCondition;
    }

    public String buildSelectCodeDataFromFamilyID(int familyID){
        String familyIDCondition;
        if(familyID == 0){
            familyIDCondition = "i.fid <> 0";
        }
        else{
            familyIDCondition = "i.fid = " + familyID;
        }
        return "SELECT d.*, c.name " +
                "FROM " + DATA_TABLE_NAME + " d " +
                "INNER JOIN " + IMPORT_TABLE_NAME + " i ON i.iid = d.iid " +
                "INNER JOIN " + CODES_TABLE_NAME + " c ON d.cid = c.cid " +
                "WHERE " + familyIDCondition;
    }

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

    //
    // INSERT statements
    //

    public String buildInsertImport(StillFaceImport importData){
        return "INSERT INTO " + IMPORT_TABLE_NAME + " " +
                "(filename, syear, fid, pid, tid, alias, date) " +
                "VALUES('" + importData.getFilename() + "', " + importData.getYear() + ", " +
                importData.getFamilyID() + ", " + importData.getParticipantNumber() + ", " +
                importData.getTag().getTagID() + ", '" + importData.getAlias() + "', '" +
                importData.getDate().toString() + "')";
    }

    public String buildInsertCodeData(StillFaceData data){
        return "INSERT INTO " + DATA_TABLE_NAME + " " +
                "(iid, time, duration, cid, comment) " +
                "VALUES(" + data.getImportID() + ", " + data.getTime() + ", " + data.getDuration() + ", " +
                data.getCode().getCodeID() + ", '" + data.getComment() + "')";
    }

    public String buildInsertNewCode(StillFaceCode code){
        return "INSERT INTO " + CODES_TABLE_NAME + " " +
                "(name) " +
                "VALUES('" + code.getName() + "')";
    }

    public String buildInsertNewTag(StillFaceTag tag){
        return "INSERT INTO " + TAGS_TABLE_NAME + " " +
                "(value) " +
                "VALUES('" + tag.getTagValue() + "')";
    }

    //
    // UPDATE statements
    //

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

    public String buildUpdateCodeData(StillFaceData data){
        return "UPDATE " + DATA_TABLE_NAME + " " +
                "SET " +
                "time = " + data.getTime() + ", " +
                "duration = " + data.getDuration() + ", " +
                "cid = " + data.getCode().getCodeID() + ", " +
                "comment = '" + data.getComment() + "' " +
                "WHERE did = " + data.getDataID();
    }

    public String buildUpdateExistingCode(StillFaceCode code){
        return "UPDATE " + CODES_TABLE_NAME + " " +
                "SET " +
                "name = '" + code.getName() + "' " +
                "WHERE cid = " + code.getCodeID();
    }

    public String buildUpdateExistingTag(StillFaceTag tag){
        return "UPDATE " + TAGS_TABLE_NAME + " " +
                "SET " +
                "value = '" + tag.getTagValue() + "' " +
                "WHERE tid = " + tag.getTagID();
    }

    //
    // DELETE statements
    //

    public String buildDeleteImport(int importID) { return "DELETE FROM " + IMPORT_TABLE_NAME + " WHERE iid = " + importID; }

    public String buildDeleteCodeDataFromImport(int importID) { return "DELETE FROM " + DATA_TABLE_NAME + " WHERE iid = " + importID; }

    public String buildDeleteCode(StillFaceCode code){
        return "DELETE FROM " + CODES_TABLE_NAME + " WHERE cid = " + code.getCodeID();
    }

    public String buildDeleteTag(StillFaceTag tag){
        return "DELETE FROM " + TAGS_TABLE_NAME + " WHERE tid = " + tag.getTagID();
    }

    //
    // CREATE TABLE statements
    //

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

    public String buildCreateSFCodesTable(DatabaseMode mode){
        String autoIncrement = getAutoIncrementSyntax(mode);
        return "CREATE TABLE " + CODES_TABLE_NAME + "\n" +
                "(\n" +
                "    cid INT PRIMARY KEY NOT NULL " + autoIncrement + ",\n" +
                "    name VARCHAR(200) NOT NULL\n"+
                ")";
    }

    public String buildCreateSFTagsTable(DatabaseMode mode){
        String autoIncrement = getAutoIncrementSyntax(mode);
        return "CREATE TABLE " + TAGS_TABLE_NAME + "\n" +
                "(\n" +
                "    tid INT PRIMARY KEY NOT NULL " + autoIncrement + ",\n" +
                "    value VARCHAR(200) NOT NULL\n"+
                ")";
    }

    public String getAutoIncrementSyntax(DatabaseMode mode){
        switch (mode){
            case AZURE:
                return "IDENTITY(1,1)";

            case DERBY:
                return "GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)";

            case HSQLDB:
                return "IDENTITY";

            default: return "AUTO INCREMENT";
        }
    }

    //
    // DROP TABLE statements
    //

    public String buildDropSFImportTable(){
        return "DROP TABLE " + IMPORT_TABLE_NAME + "";
    }

    public String buildDropSFDataTable(){
        return "DROP TABLE " + DATA_TABLE_NAME + "";
    }

    public String buildDropSFCodesTable(){
        return "DROP TABLE " + CODES_TABLE_NAME + "";
    }

    public String buildDropSFTagsTable(){
        return "DROP TABLE " + TAGS_TABLE_NAME + "";
    }
}
