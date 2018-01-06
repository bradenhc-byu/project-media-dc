package com.byu.pmedia.model;

import java.util.ArrayList;

public class CodedVideoData {

    ArrayList<CodeStamp> data = new ArrayList<>();
    private String delimiter;

    public CodedVideoData(){
        this.delimiter = ",";
    }

    public void addCodeStamp(CodeStamp cs){
        this.data.add(cs);
    }

    public ArrayList<CodeStamp> getData(){
        return this.data;
    }

    public void clear(){
        this.data.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(CodeStamp stamp : this.data){
            sb.append(stamp.getTime());
            sb.append(this.delimiter);
            sb.append(stamp.getDuration());
            sb.append(this.delimiter);
            sb.append(stamp.getType().getName());
            sb.append(this.delimiter);
            sb.append(stamp.getComment());
            sb.append("\n");
        }
        return sb.toString();
    }
}
