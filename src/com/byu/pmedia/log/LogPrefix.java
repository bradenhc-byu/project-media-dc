package com.byu.pmedia.log;

import java.sql.Timestamp;
import java.util.Date;

public class LogPrefix {

    private String generalPrefix;

    public LogPrefix(String generalPrefix){
        this.generalPrefix = generalPrefix;
    }

    public String get(String level){
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        return this.generalPrefix + " " + timestamp.toString() + " [" + level + "]: ";
    }
}
