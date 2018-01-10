package com.byu.pmedia.model;

import java.sql.Date;

public class StillFaceImportData {

    private int importID = 0;
    private String filename;
    private int year;
    private int familyID;
    private int participantNumber;
    private String alias;
    private Date date;

    public StillFaceImportData(int importID, String filename, int year, int familyID, int participantNumber, String alias, Date date) {
        this.importID = importID;
        this.filename = filename;
        this.year = year;
        this.familyID = familyID;
        this.participantNumber = participantNumber;
        this.alias = alias;
        this.date = date;
    }

    public StillFaceImportData(String filename, int year, int familyID, int participantNumber, String alias, Date date) {
        this.filename = filename;
        this.year = year;
        this.familyID = familyID;
        this.participantNumber = participantNumber;
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
}
