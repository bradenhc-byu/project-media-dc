package com.byu.pmedia.model;

import com.googlecode.cqengine.attribute.Attribute;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

public class StillFaceCode {

    private String name;
    private int codeID = 0;

    /**
     * The following variables are defined for use with the CQEngine IndexedCollections. This allows us to
     * create extremely fast indexing capabilities and cache data in memory for use. Data is only cached if
     * the model.cache configuration option is set to 'true'
     */
    public static final Attribute<StillFaceCode, Integer> CODE_ID =
            attribute("codeID", StillFaceCode::getCodeID);
    public static final Attribute<StillFaceCode, String> NAME =
            attribute("name", StillFaceCode::getName);

    public StillFaceCode(String name){
        this.setName(name);
    }

    public StillFaceCode(int codeID, String name){
        this.codeID = codeID;
        this.setName(name);
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getCodeID() { return this.codeID; }
}
