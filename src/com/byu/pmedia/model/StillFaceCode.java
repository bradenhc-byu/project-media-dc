package com.byu.pmedia.model;

public class StillFaceCode {

    private String name;
    private int codeID = 0;

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
