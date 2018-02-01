package com.byu.pmedia.model;

import com.googlecode.cqengine.attribute.Attribute;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

public class StillFaceCode {

    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleIntegerProperty codeID = new SimpleIntegerProperty();
    private SimpleIntegerProperty delimiterIndex = new SimpleIntegerProperty();

    /**
     * The following variables are defined for use with the CQEngine IndexedCollections. This allows us to
     * create extremely fast indexing capabilities and cache data in memory for use. Data is only cached if
     * the model.cache configuration option is set to 'true'
     */
    public static final Attribute<StillFaceCode, Integer> CODE_ID =
            attribute("codeID", StillFaceCode::getCodeID);
    public static final Attribute<StillFaceCode, String> NAME =
            attribute("name", StillFaceCode::getName);
    public static final Attribute<StillFaceCode, Integer> DELIMITER_INDEX =
            attribute("delimiterIndex", StillFaceCode::getDelimiterIndex);

    public StillFaceCode(String name){
        this.setName(name);
    }

    public StillFaceCode(int codeID, String name, int delimiterIndex){
        this.setCodeID(codeID);
        this.setName(name);
        this.setDelimiterIndex(delimiterIndex);
    }

    public void setName(String name){
        this.name.set(name);
    }

    public String getName(){
        return this.name.get();
    }

    public void setCodeID(int codeID) { this.codeID.set(codeID); }

    public int getCodeID() { return this.codeID.get(); }

    public void setDelimiterIndex(int delimiter) { this.delimiterIndex.set(delimiter); }

    public int getDelimiterIndex() { return this.delimiterIndex.get(); }

    @Override
    public String toString(){
        return name.get();
    }

    @Override
    public boolean equals(Object o){
        if(o == null || !o.getClass().equals(StillFaceCode.class)) return false;
        StillFaceCode c = (StillFaceCode)o;
        if(!c.getName().equals(this.getName())
                || c.getCodeID() != this.getCodeID()){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode(){
        return name.get().hashCode();
    }
}
