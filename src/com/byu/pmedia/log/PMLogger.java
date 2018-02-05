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

/**
 * PMLogger
 * Provides a simple logger to the rest of the DataCenter. Handles debug, info, warn, and error messages, and can write
 * them to file or print them to stdout, depending on configuration in the settings. The default is to log them to
 * stdout.
 * TODO: implement file logging and appending
 * TODO: implement creation of new file when old exceeds max size
 *
 * @author Braden Hitchcock
 */
public class PMLogger {

    /* The prefix generated at the start of each message */
    private final LogPrefix prefix;

    /* The singleton instance of the logger, providing access to all classes in the application */
    private static PMLogger singleton;

    /**
     * Default constructor. Creates a new instance with a prefix of '=pm='
     */
    public PMLogger(){
        this.prefix = new LogPrefix("=pm=");
    }

    /**
     * Provides access to the singleton instance of the logger
     *
     * @return An instance of the PMLogger
     */
    public static PMLogger getInstance(){
        if(singleton == null){
            singleton = new PMLogger();
        }
        return singleton;
    }

    /**
     * Logs an info message
     *
     * @param message The message to log
     */
    public void info(String message){
        logOut("INFO", message);
    }
    /**
     * Logs a debug message
     *
     * @param message The message to log
     */
    public void debug(String message){
        logOut("DEBUG", message);
    }

    /**
     * Logs a warning message
     *
     * @param message The message to log
     */
    public void warn(String message){
        logErr("WARN", message);
    }

    /**
     * Logs an error message
     *
     * @param message The message to log
     */
    public void error(String message){
        logErr("ERROR", message);
    }

    /**
     * Logs the message to stdout or to the out file
     *
     * @param logLevel The log level to append to the prefix
     * @param message The message to log
     */
    private void logOut(String logLevel, String message){
        System.out.println(this.prefix.get(logLevel) + message);
    }

    /**
     * Logs the message to stderr or the error file
     * @param logLevel The log level to append to the prefix
     * @param message The message to log
     */
    private void logErr(String logLevel, String message){
        System.err.println(this.prefix.get(logLevel) + message);
    }
}
