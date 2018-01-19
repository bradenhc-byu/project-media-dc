package com.byu.pmedia.model;

import com.googlecode.cqengine.attribute.Attribute;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

public class StillFaceTag {

    private int tagID = 0;
    private String tagValue;

    /**
     * The following variables are defined for use with the CQEngine IndexedCollections. This allows us to
     * create extremely fast indexing capabilities and cache data in memory for use. Data is only cached if
     * the model.cache configuration option is set to 'true'
     */
    public static final Attribute<StillFaceTag, Integer> TAG_ID =
            attribute("tagID", StillFaceTag::getTagID);
    public static final Attribute<StillFaceTag, String> TAG_VALUE =
            attribute("tagValue", StillFaceTag::getTagValue);

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

    @Override
    public String toString(){
        return this.getTagValue();
    }
}
