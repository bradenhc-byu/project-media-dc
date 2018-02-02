package com.byu.pmedia.tasks;

import com.byu.pmedia.model.*;
import com.byu.pmedia.parser.StillFaceCSVParser;
import javafx.concurrent.Task;

import java.util.*;


public class StillFaceExportTask implements IStillFaceTask {

    private String filepath;
    private String summaryFilepath;
    private StillFaceTaskCallback callback;

    public StillFaceExportTask(String filepath, StillFaceTaskCallback callback){
        this.filepath = filepath;
        this.summaryFilepath = filepath.replaceAll(".csv", "_code_summary.csv");
        this.callback = callback;
    }

    @Override
    public void execute() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                exportData();
                return null;
            }

            @Override
            protected void succeeded(){
                callback.onSuccess();
                super.succeeded();
            }

            @Override
            protected void failed(){
                callback.onFail(this.getException());
                super.failed();
            }
        };
        new Thread(task).start();
    }

    private void exportData() throws Exception{
        // Set up some maps to hold summary data
        Map<String, Integer> mostCommonCodeFirst = new HashMap<>();
        Map<String, Integer> mostCommonCodeSecond= new HashMap<>();
        Map<String, Integer> mostCommonCodeThird = new HashMap<>();

        StillFaceCode delim1 = null;
        StillFaceCode delim2 = null;

        for(StillFaceCode c : StillFaceModel.getCodeList()){
            if(c.getDelimiterIndex() == 1){
                delim1 = c;
            }
            else if(c.getDelimiterIndex() == 2){
                delim2 = c;
            }
        }

        // Collect the summary data
        int delimiterIndex = 0;
        for(StillFaceData data : StillFaceModel.getInstance().getVisibleDataList()){
            // Code stats
            String codeName = data.getCode().getName();
            if(data.getCode().getDelimiterIndex() > delimiterIndex){
                delimiterIndex = data.getCode().getDelimiterIndex();
            }
            // Get the common code counts for each video segment
            switch(delimiterIndex){
                case 0:
                    if(mostCommonCodeFirst.containsKey(codeName)){
                        int count = mostCommonCodeFirst.get(codeName);
                        mostCommonCodeFirst.put(codeName, count + 1);
                    }
                    else{
                        mostCommonCodeFirst.put(codeName, 1);
                    }
                    break;

                case 1:
                    if(mostCommonCodeSecond.containsKey(codeName)){
                        int count = mostCommonCodeSecond.get(codeName);
                        mostCommonCodeSecond.put(codeName, count + 1);
                    }
                    else{
                        mostCommonCodeSecond.put(codeName, 1);
                    }
                    break;

                case 2:
                    if(mostCommonCodeThird.containsKey(codeName)){
                        int count = mostCommonCodeThird.get(codeName);
                        mostCommonCodeThird.put(codeName, count + 1);
                    }
                    else{
                        mostCommonCodeThird.put(codeName, 1);
                    }
                    break;


            }

        }
        List<StillFaceCodeCount> firstCounts = new ArrayList<>();
        List<StillFaceCodeCount> secondCounts = new ArrayList<>();
        List<StillFaceCodeCount> thirdCounts = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : mostCommonCodeFirst.entrySet()){
            firstCounts.add(new StillFaceCodeCount(entry.getKey(), entry.getValue()));
        }
        for(Map.Entry<String, Integer> entry : mostCommonCodeSecond.entrySet()){
            secondCounts.add(new StillFaceCodeCount(entry.getKey(), entry.getValue()));
        }
        for(Map.Entry<String, Integer> entry : mostCommonCodeThird.entrySet()){
            thirdCounts.add(new StillFaceCodeCount(entry.getKey(), entry.getValue()));
        }
        Collections.sort(firstCounts);
        Collections.sort(secondCounts);
        Collections.sort(thirdCounts);
        StillFaceCSVParser parser = new StillFaceCSVParser();
        List<List<StillFaceCodeCount>> summaryList = new ArrayList<>();
        summaryList.add(firstCounts);
        summaryList.add(secondCounts);
        summaryList.add(thirdCounts);
        boolean success = parser.serializeToCSVFromCodedVideoData(
                new StillFaceVideoData(StillFaceModel.getInstance().getVisibleDataList()), this.filepath)
                && parser.serializeSummaryToCSVFromMaps(delim1, delim2, summaryList, this.summaryFilepath);
        if(!success){
            throw new Exception("Failed to export data. See log for more information.");
        }
    }
}
