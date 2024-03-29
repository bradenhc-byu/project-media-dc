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
package com.byu.pmedia.model;

import com.googlecode.cqengine.attribute.Attribute;
import java.sql.Date;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

/**
 * StillFaceImport
 * Provides the data structure for holding information from an import entry in the database.
 *
 * @author Braden Hitchcock
 */
public class StillFaceImport {

    /* The member variables representing the schema of the database table. An additional argument (pid) is provided
     * to construct the whole PID of the entry from the import given its components. */
    private int importID = 0;
    private String filename;
    private int year;
    private int familyID;
    private int participantNumber;
    private StillFaceTag tag;
    private String alias;
    private Date date;
    private String pid;

    /*
     * The following variables are defined for use with the CQEngine IndexedCollections. This allows us to
     * create extremely fast indexing capabilities and cache data in memory for use. Data is only cached if
     * the model.cache configuration option is set to 'true'
     */
    public static final Attribute<StillFaceImport, Integer> IMPORT_ID =
            attribute("importID", StillFaceImport::getImportID);
    public static final Attribute<StillFaceImport, String> FILENAME =
            attribute("filename", StillFaceImport::getFilename);
    public static final Attribute<StillFaceImport, Integer> YEAR =
            attribute("year", StillFaceImport::getYear);
    public static final Attribute<StillFaceImport, Integer> FAMILY_ID =
            attribute("familyID", StillFaceImport::getFamilyID);
    public static final Attribute<StillFaceImport, Integer> PARTICIPANT_ID =
            attribute("participantNumber", StillFaceImport::getParticipantNumber);
    public static final Attribute<StillFaceImport, StillFaceTag> TAG =
            attribute("tag", StillFaceImport::getTag);
    public static final Attribute<StillFaceImport, String> ALIAS =
            attribute("alias", StillFaceImport::getAlias);
    public static final Attribute<StillFaceImport, Date> DATE =
            attribute("date", StillFaceImport::getDate);
    public static final Attribute<StillFaceImport, String> PID =
            attribute("pid", StillFaceImport::getPid);

    public StillFaceImport(int importID, String filename, int year, int familyID, int participantNumber,
                           StillFaceTag tag, String alias, Date date) {
        this.importID = importID;
        this.filename = filename;
        this.year = year;
        this.familyID = familyID;
        this.participantNumber = participantNumber;
        this.tag = tag;
        this.alias = alias;
        this.date = date;
        this.pid = String.format("%d-%03d-%02d", this.year, this.familyID, this.participantNumber);
    }

    public StillFaceImport(String filename, int year, int familyID, int participantNumber,
                           StillFaceTag tag, String alias, Date date) {
        this.filename = filename;
        this.year = year;
        this.familyID = familyID;
        this.participantNumber = participantNumber;
        this.tag = tag;
        this.alias = alias;
        this.date = date;
        this.pid = String.format("%d-%d-%d", this.year, this.familyID, this.participantNumber);
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

    public String getPid() { return this.pid; }

    public void setPid(String pid) { this.pid = pid;}

    public StillFaceTag getTag() {
        return tag;
    }

    public void setTag(StillFaceTag tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o){
        if(o == null || o.getClass() != this.getClass()) return false;
        StillFaceImport sfImport = (StillFaceImport)o;
        if(sfImport.getImportID() != this.getImportID()) return false;
        return true;
    }

    @Override
    public String toString(){
        return String.format("%s : %s - %s [%s]", pid, tag, alias,date);
    }
}
