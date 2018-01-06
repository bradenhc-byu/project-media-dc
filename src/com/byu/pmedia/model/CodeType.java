package com.byu.pmedia.model;

public class CodeType {

    private String name;

    public CodeType(String name){
        this.setName(name);
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
