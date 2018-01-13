package com.byu.pmedia.model;

public class StillFaceCodeData {

    private int dataID = 0;
    private int importID = 0;
    private int time;
    private int duration;
    private StillFaceCode code;
    private String comment;

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

    public int getImportID() {
        return importID;
    }
}
