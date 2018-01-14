package com.byu.pmedia.model;

import com.googlecode.cqengine.attribute.Attribute;
import java.sql.Date;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

public class StillFaceImportData {

    private int importID = 0;
    private String filename;
    private int year;
    private int familyID;
    private int participantNumber;
    private StillFaceTag tag;
    private String alias;
    private Date date;

    /**
     * The following variables are defined for use with the CQEngine IndexedCollections. This allows us to
     * create extremely fast indexing capabilities and cache data in memory for use. Data is only cached if
     * the model.cache configuration option is set to 'true'
     */
    public static final Attribute<StillFaceImportData, Integer> IMPORT_ID =
            attribute("importID", StillFaceImportData::getImportID);
    public static final Attribute<StillFaceImportData, String> FILENAME =
            attribute("filename", StillFaceImportData::getFilename);
    public static final Attribute<StillFaceImportData, Integer> YEAR =
            attribute("year", StillFaceImportData::getYear);
    public static final Attribute<StillFaceImportData, Integer> FAMILY_ID =
            attribute("familyID", StillFaceImportData::getFamilyID);
    public static final Attribute<StillFaceImportData, Integer> PARTICIPANT_ID =
            attribute("participantNumber", StillFaceImportData::getParticipantNumber);
    public static final Attribute<StillFaceImportData, StillFaceTag> TAG =
            attribute("tag", StillFaceImportData::getTag);
    public static final Attribute<StillFaceImportData, String> ALIAS =
            attribute("alias", StillFaceImportData::getAlias);
    public static final Attribute<StillFaceImportData, Date> DATE =
            attribute("date", StillFaceImportData::getDate);

    public StillFaceImportData(int importID, String filename, int year, int familyID, int participantNumber,
                               StillFaceTag tag, String alias, Date date) {
        this.importID = importID;
        this.filename = filename;
        this.year = year;
        this.familyID = familyID;
        this.participantNumber = participantNumber;
        this.tag = tag;
        this.alias = alias;
        this.date = date;
    }

    public StillFaceImportData(String filename, int year, int familyID, int participantNumber,
                               StillFaceTag tag, String alias, Date date) {
        this.filename = filename;
        this.year = year;
        this.familyID = familyID;
        this.participantNumber = participantNumber;
        this.tag = tag;
        this.alias = alias;
        this.date = date;
    }

    public int getImportID() {
        return importID;
    }

    public String getFilename() {
        return filename;
    }

    public int getYear() {
        return year;
    }

    public int getFamilyID() {
        return familyID;
    }

    public int getParticipantNumber() {
        return participantNumber;
    }

    public String getAlias() {
        return alias;
    }

    public Date getDate() {
        return date;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setFamilyID(int familyID) {
        this.familyID = familyID;
    }

    public void setParticipantNumber(int participantNumber) {
        this.participantNumber = participantNumber;
    }

    public StillFaceTag getTag() {
        return tag;
    }

    public void setTag(StillFaceTag tag) {
        this.tag = tag;
    }
}
