package com.byu.pmedia.model;

public class StillFaceTag {

    private int tagID = 0;
    private String tagValue;

    public StillFaceTag(String tagValue){
        this.setTagValue(tagValue);
    }

    public StillFaceTag(int tagID, String tagValue){
        this.tagID = tagID;
        this.setTagValue(tagValue);
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public int getTagID() {
        return tagID;
    }

    public String getTagValue() {
        return tagValue;
    }
}
