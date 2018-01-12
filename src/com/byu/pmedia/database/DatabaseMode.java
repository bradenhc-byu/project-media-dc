package com.byu.pmedia.database;

import com.byu.pmedia.config.ConfigEnumManager;
import com.byu.pmedia.config.IConfigEnum;

public enum DatabaseMode implements IConfigEnum {

    DERBY,
    AZURE,
    HSQLDB;

    /**
     * This static block must be executed to add the string values of the enums to the
     * Configuration enum manager
     */
    static{
        for(DatabaseMode mode : DatabaseMode.values()){
            ConfigEnumManager.getInstance().register(mode);
        }
    }

    @Override
    public String toPrettyString(){
        switch(this){
            case DERBY: return "Apache Derby";
            case AZURE: return "Microsoft Azure";
            case HSQLDB: return "HyperSQL Database";
            default: return "Unknown DB Type";
        }
    }

}
