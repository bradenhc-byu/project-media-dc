package com.byu.pmedia.database;

import com.byu.pmedia.model.StillFaceCode;
import com.byu.pmedia.model.StillFaceCodeData;
import com.byu.pmedia.model.StillFaceImportData;
import com.byu.pmedia.model.StillFaceTag;

public class StillFaceQueryBuilder {

    public StillFaceQueryBuilder(){

    }

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
        return "SELECT * " +
                "FROM sf_imports " +
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
                "FROM sf_data d " +
                "INNER JOIN sf_codes c ON d.cid = c.cid " +
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
                "FROM sf_data d " +
                "INNER JOIN sf_imports i ON i.iid = d.iid " +
                "INNER JOIN sf_codes c ON d.cid = c.cid " +
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
                "FROM sf_codes " +
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
                "FROM sf_tags " +
                "WHERE " + tagIDCondition;
    }

    //
    // INSERT statements
    //

    public String buildInsertImport(StillFaceImportData importData){
        return "INSERT INTO sf_imports " +
                "(filename, year, fid, pid, alias) " +
                "VALUES(\"" + importData.getFilename() + "\", " + importData.getYear() + ", " +
                importData.getFamilyID() + ", " + importData.getParticipantNumber() + ", \"" +
                importData.getAlias() + "\")";
    }

    public String buildInsertCodeData(StillFaceCodeData data){
        return "INSERT INTO sf_data " +
                "(iid, time, duration, cid, comment) " +
                "VALUES(" + data.getImportID() + ", " + data.getTime() + ", " + data.getDuration() + ", " +
                data.getCode().getCodeID() + ", \"" + data.getComment() + "\")";
    }

    public String buildInsertNewCode(StillFaceCode code){
        return "INSERT INTO sf_codes " +
                "(name) " +
                "VALUES(\"" + code.getName() + "\")";
    }

    public String buildInsertNewTag(StillFaceTag tag){
        return "INSERT INTO sf_tags " +
                "(value) " +
                "VALUES(\"" + tag.getTagValue() + "\")";
    }

    //
    // UPDATE statements
    //

    public String buildUpdateImport(StillFaceImportData importData){
        return "UPDATE sf_imports " +
                "SET " +
                "year = " + importData.getYear() + ", " +
                "fid = " + importData.getFamilyID() + ", " +
                "pid = " + importData.getParticipantNumber() + ", " +
                "alias = \"" + importData.getAlias() + "\" " +
                "WHERE iid = " + importData.getImportID();
    }

    public String buildUpdateCodeData(StillFaceCodeData data){
        return "UPDATE sf_data " +
                "SET " +
                "time = " + data.getTime() + ", " +
                "duration = " + data.getDuration() + ", " +
                "cid = " + data.getCode().getCodeID() + ", " +
                "comment = \"" + data.getComment() + "\" " +
                "WHERE did = " + data.getDataID();
    }

    public String buildUpdateExistingCode(StillFaceCode code){
        return "UPDATE sf_codes " +
                "SET " +
                "name = \"" + code.getName() + "\" " +
                "WHERE cid = " + code.getCodeID();
    }

    public String buildUpdateExistingTag(StillFaceTag tag){
        return "UPDATE sf_tags " +
                "SET " +
                "value = \"" + tag.getTagValue() + "\" " +
                "WHERE tid = " + tag.getTagID();
    }

    //
    // DELETE statements
    //

    public String buildDeleteCode(StillFaceCode code){
        return "DELETE FROM sf_codes WHERE cid = " + code.getCodeID();
    }

    public String buildDeleteTag(StillFaceTag tag){
        return "DELETE FROM sf_tags WHERE tid = " + tag.getTagID();
    }

    //
    // CREATE TABLE statements
    //

    public String buildCreateSFImportTable(DatabaseMode mode){
        String autoIncrement = getAutoIncrementSyntax(mode);
        return "CREATE TABLE sf_imports\n" +
                "(\n" +
                "    iid INT PRIMARY KEY NOT NULL " + autoIncrement + ",\n" +
                "    filename VARCHAR(200) NOT NULL,\n" +
                "    year INT NOT NULL,\n" +
                "    fid INT NOT NULL,\n" +
                "    pid INT NOT NULL,\n" +
                "    alias VARCHAR(200),\n" +
                "    date DATE NOT NULL\n" +
                ")";
    }

    public String buildCreateSFDataTable(DatabaseMode mode){
        String autoIncrement = getAutoIncrementSyntax(mode);
        return "CREATE TABLE sf_data\n" +
                "(\n" +
                "    did INT PRIMARY KEY NOT NULL " + autoIncrement + ",\n" +
                "    iid INT NOT NULL,\n" +
                "    time INT NOT NULL,\n" +
                "    duration INT NOT NULL,\n" +
                "    pid INT NOT NULL,\n" +
                "    cid INT NOT NULL,\n" +
                "    comment VARCHAR(500)\n" +
                ")";
    }

    public String buildCreateSFCodesTable(DatabaseMode mode){
        String autoIncrement = getAutoIncrementSyntax(mode);
        return "CREATE TABLE sf_codes\n" +
                "(\n" +
                "    cid INT PRIMARY KEY NOT NULL " + autoIncrement + ",\n" +
                "    name VARCHAR(200) NOT NULL\n"+
                ")";
    }

    public String buildCreateSFTagsTable(DatabaseMode mode){
        String autoIncrement = getAutoIncrementSyntax(mode);
        return "CREATE TABLE sf_tags\n" +
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
}
