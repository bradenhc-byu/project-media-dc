package com.byu.pmedia.model;

import java.util.ArrayList;

public class StillFaceVideoData {

    ArrayList<StillFaceCodeData> data = new ArrayList<>();
    private String delimiter;

    public StillFaceVideoData(){
        this.delimiter = ",";
    }

    public void addCodeData(StillFaceCodeData cs){
        this.data.add(cs);
    }

    public ArrayList<StillFaceCodeData> getData(){
        return this.data;
    }

    public void clear(){
        this.data.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(StillFaceCodeData stamp : this.data){
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
