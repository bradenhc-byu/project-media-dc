package com.byu.pmedia.model;

import java.util.ArrayList;
import java.util.List;

public class StillFaceVideoData {

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
