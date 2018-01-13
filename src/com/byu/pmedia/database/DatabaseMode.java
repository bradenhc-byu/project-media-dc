package com.byu.pmedia.database;

public enum DatabaseMode {

    DERBY,
    AZURE,
    HSQLDB;

    public String toPrettyString(){
        switch(this){
            case DERBY: return "Apache Derby";
            case AZURE: return "Microsoft Azure";
            case HSQLDB: return "HyperSQL Database";
            default: return "Unknown DB Type";
        }
    }

}
