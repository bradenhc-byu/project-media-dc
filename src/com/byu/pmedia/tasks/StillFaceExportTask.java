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
package com.byu.pmedia.tasks;

import com.byu.pmedia.model.*;
import com.byu.pmedia.parser.StillFaceCSVParser;
import javafx.concurrent.Task;

import java.util.*;

/**
 * StillFaceExportTask
 * Implementation of the IStillFaceTask interface. Wraps the execution of exporting the visible data on the GUI taken
 * from the database into a CSV file. This task is executed on a separate thread from the GUI.
 *
 * @author Braden Hitchcock
 */
public class StillFaceExportTask implements IStillFaceTask {

    /* The full path (filename included) to write the file to */
    private String filepath;
    /* The path to the file where summary data will be written. Same directory as the filepath. */
    private String summaryFilepath;
    /* Callback method provided by developer to be executed on success or fail of the task */
    private StillFaceTaskCallback callback;

    public StillFaceExportTask(String filepath, StillFaceTaskCallback callback){
        this.filepath = filepath;
        this.summaryFilepath = filepath.replaceAll(".csv", "_code_summary.csv");
        this.callback = callback;
    }

    /**
     * Executes the task. Attempts to export the file data on a separate thread.
     */
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

    /**
     * Where the export actually happens. Using the visible data list from the StillFaceModel, it gets summary data
     * and then writes the visible data to a CSV file, followed by writing the summary data.
     *
     * @throws Exception If the write fails, this makes sure that the fail triggers the onFail() callback method
     *                   provided by the developer
     */
    private void exportData() throws Exception{
        // Set up some maps to hold summary data
        Map<String, Integer> mostCommonCodeFirst = new HashMap<>();
        Map<String, Integer> mostCommonCodeSecond= new HashMap<>();
        Map<String, Integer> mostCommonCodeThird = new HashMap<>();

        // Get the delimiters
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
        // Count everything up
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
        // Sort the counts
        Collections.sort(firstCounts);
        Collections.sort(secondCounts);
        Collections.sort(thirdCounts);
        // Write them to a file
        StillFaceCSVParser parser = new StillFaceCSVParser();
        List<List<StillFaceCodeCount>> summaryList = new ArrayList<>();
        summaryList.add(firstCounts);
        summaryList.add(secondCounts);
        summaryList.add(thirdCounts);
        boolean success = parser.serializeToCSVFromCodedVideoData(
                new StillFaceVideoData(StillFaceModel.getInstance().getVisibleDataList()), this.filepath)
                && parser.serializeSummaryToCSVFromLists(delim1, delim2, summaryList, this.summaryFilepath);
        if(!success){
            throw new Exception("Failed to export data. See log for more information.");
        }
    }
}
