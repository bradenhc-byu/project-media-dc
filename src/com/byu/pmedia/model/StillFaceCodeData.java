package com.byu.pmedia.model;

import com.googlecode.cqengine.attribute.Attribute;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

public class StillFaceCodeData {

    private int dataID = 0;
    private int importID = 0;
    private int time;
    private int duration;
    private StillFaceCode code;
    private String comment;

    /**
     * The following variables are defined for use with the CQEngine IndexedCollections. This allows us to
     * create extremely fast indexing capabilities and cache data in memory for use. Data is only cached if
     * the model.cache configuration option is set to 'true'
     */
    public static final Attribute<StillFaceCodeData, Integer> DATA_ID =
            attribute("dataID", StillFaceCodeData::getDataID);
    public static final Attribute<StillFaceCodeData, Integer> IMPORT_ID =
            attribute("importID", StillFaceCodeData::getImportID);
    public static final Attribute<StillFaceCodeData, Integer> TIME =
            attribute("time", StillFaceCodeData::getTime);
    public static final Attribute<StillFaceCodeData, Integer> DURATION =
            attribute("duration", StillFaceCodeData::getDuration);
    public static final Attribute<StillFaceCodeData, StillFaceCode> CODE =
            attribute("code", StillFaceCodeData::getCode);
    public static final Attribute<StillFaceCodeData, String> COMMENT =
            attribute("comment", StillFaceCodeData::getComment);

    public StillFaceCodeData(int importID, int time, int duration, StillFaceCode code, String comment){
        this.importID = importID;
        this.setTime(time);
        this.setDuration(duration);
        this.setCode(code);
        this.setComment(comment);
    }

    public StillFaceCodeData(int dataID, int importID, int time, int duration, StillFaceCode code, String comment){
        this.dataID = dataID;
        this.importID = importID;
        this.setTime(time);
        this.setDuration(duration);
        this.setCode(code);
        this.setComment(comment);
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public StillFaceCode getCode() {
        return code;
    }

    public void setCode(StillFaceCode code){
        this.code = code;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getDataID() {
        return dataID;
    }

    public void setImportID(int importID) { this.importID = importID; }

    public int getImportID() {
        return importID;
    }
}
