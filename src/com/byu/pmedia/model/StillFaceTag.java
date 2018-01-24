package com.byu.pmedia.model;

import com.googlecode.cqengine.attribute.Attribute;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

public class StillFaceTag {

    private SimpleIntegerProperty tagID = new SimpleIntegerProperty();
    private SimpleStringProperty tagValue = new SimpleStringProperty();

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
        this.tagID.set(tagID);
        this.setTagValue(tagValue);
    }

    public void setTagValue(String tagValue) {
        this.tagValue.set(tagValue);
    }

    public int getTagID() {
        return tagID.get();
    }

    public String getTagValue() {
        return tagValue.get();
    }

    @Override
    public String toString(){
        return this.getTagValue();
    }

    @Override
    public boolean equals(Object o){
        if(o == null || !o.getClass().equals(StillFaceTag.class)) return false;
        StillFaceTag t = (StillFaceTag)o;
        if(!t.getTagValue().equals(this.getTagValue())
                || t.getTagID() != this.getTagID()){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        return tagValue.get().hashCode();
    }
}
