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
package com.byu.pmedia.database;

/**
 * DatabaseMode
 * Enumerated type for describing the different kinds of available databases the DataCenter can support. Types added
 * here will be available for selection from the Settings GUI
 */
public enum DatabaseMode {

    DERBY,
    AZURE;

    /**
     * Converts the enumerated type machine name to a more user-friendly name
     * @return A string representing the pretty-printed, user-friendly type name
     */
    public String toPrettyString(){
        switch(this){
            case DERBY: return "Apache Derby";
            case AZURE: return "Microsoft Azure";
            default: return "Unknown DB Type";
        }
    }

}
