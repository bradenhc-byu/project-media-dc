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
package com.byu.pmedia.log;

import java.sql.Timestamp;
import java.util.Date;

/**
 * LogPrefix
 * Provides a wrapper that can construct the prefix to put in front of a log message
 *
 * @author Braden Hitchcock
 */
public class LogPrefix {

    /* The generally formatted prefix */
    private String generalPrefix;

    /**
     * Constructor that takes a general prefix to prepend to all messages
     *
     * @param generalPrefix general prefix to prepend to all messages
     */
    public LogPrefix(String generalPrefix){
        this.generalPrefix = generalPrefix;
    }

    /**
     * Constructs the prefix to put in front of a log message
     *
     * @param level A string representation of the log level to use in the prefix
     * @return A string representing the log prefix
     */
    public String get(String level){
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        return this.generalPrefix + " " + timestamp.toString() + " [" + level + "]: ";
    }
}
