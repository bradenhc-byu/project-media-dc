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
package com.byu.pmedia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * StillFaceVideoData
 * Represents all of the StillFaceData that are associated with a single video. This makes wrapping the data as a list
 * easier and provides the means to import and export data to and from CSV files.
 *
 * @author Braden Hitchcock
 */
public class StillFaceVideoData {

    /* The internal member variables */
    List<StillFaceData> data = new ArrayList<>();
    private String delimiter;

    public StillFaceVideoData(List<StillFaceData> data){
        this.data = data;
        this.delimiter = ",";
    }

    public StillFaceVideoData(){
        this.delimiter = ",";
    }

    public void addCodeData(StillFaceData cs){
        this.data.add(cs);
    }

    public List<StillFaceData> getData(){
        return this.data;
    }

    public void clear(){
        this.data.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(StillFaceData stamp : this.data){
            sb.append(stamp.getTime());
            sb.append(this.delimiter);
            sb.append(stamp.getDuration());
            sb.append(this.delimiter);
            sb.append(stamp.getCode().getName());
            sb.append(this.delimiter);
            sb.append(stamp.getComment());
            sb.append("\n");
        }
        return sb.toString();
    }
}
