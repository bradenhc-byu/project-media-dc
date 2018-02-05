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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

/**
 * StillFaceTag
 * Represents the internal data structure that holds tag data from entries in the database.
 *
 * @author Braden Hitchcock
 */
public class StillFaceTag {

    /*
     * These variables are for the JavaFX Table object in the GUI. The table cells are populated using a CellFactory,
     * and in order to do this we need to have special variable types
     */
    private SimpleIntegerProperty tagID = new SimpleIntegerProperty();
    private SimpleStringProperty tagValue = new SimpleStringProperty();

    /*
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
