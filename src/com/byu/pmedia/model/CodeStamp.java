package com.byu.pmedia.model;

public class CodeStamp {

    private int time;
    private int duration;
    private CodeType type;
    private String comment;

    public CodeStamp(int time, int duration, String name){
        this.setTime(time);
        this.setDuration(duration);
        this.setType(name);
    }

    public CodeStamp(int time, int duration, String name, String comment){
        this.setTime(time);
        this.setDuration(duration);
        this.setType(name);
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

    public CodeType getType() {
        return type;
    }

    public void setType(String name){
        this.type = new CodeType(name);
    }

    public void setType(CodeType type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
