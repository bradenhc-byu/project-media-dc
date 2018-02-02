package com.byu.pmedia.parser;

import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.model.StillFaceCode;
import com.byu.pmedia.model.StillFaceCodeCount;
import com.byu.pmedia.model.StillFaceData;
import com.byu.pmedia.model.StillFaceVideoData;

import java.io.*;
import java.util.List;
import java.util.Map;

public class StillFaceCSVParser {

    private String delimeter;

    public StillFaceCSVParser(){
        this.delimeter = ",";
    }

    public boolean parseFromCSVIntoCodedVideoData(String filename, StillFaceVideoData videoData){

        PMLogger.getInstance().debug("Parsing data from " + filename + " into CodedVideoDataObject");

        try{
            // Create a new buffered reader to read the CSV file
            BufferedReader br = new BufferedReader(new FileReader(filename));


            // Clear any existing data from the StillFaceVideoData object. Placing this line after creating the
            // buffered reader ensures that the provided CSV file exists
            videoData.clear();

            // Begin parsing from the CSV file
            String line;
            while((line = br.readLine()) != null){

                // Get the data from the line
                String[] data = line.split(this.delimeter);

                // Strip away the header contents from the CSV file (if it exists)
                try{
                    videoData.addCodeData(new StillFaceData(0, Integer.parseInt(data[0]),
                            Integer.parseInt(data[1]),
                            new StillFaceCode(data[2]),
                            data[3]));
                }
                catch(NumberFormatException e){
                    PMLogger.getInstance().debug("Detected possible CSV header, skipping...");
                }

            }

            br.close();
        }
        catch(FileNotFoundException e){
            PMLogger.getInstance().error("Could not read file, file not found: " + filename);
            return false;
        }
        catch(IOException e){
            PMLogger.getInstance().error("Caught IOException: " + e.getMessage());
            return false;
        }

        PMLogger.getInstance().debug("Parsing complete");

        return true;
    }

    public boolean serializeToCSVFromCodedVideoData(StillFaceVideoData videoData, String filename){

        PMLogger.getInstance().debug("Serialize StillFaceVideoData to CSV file: " + filename);

        try{

            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

            for(StillFaceData stamp : videoData.getData()){
                StringBuilder sb = new StringBuilder();
                sb.append(stamp.getTime());
                sb.append(this.delimeter);
                sb.append(stamp.getDuration());
                sb.append(this.delimeter);
                sb.append(stamp.getCode().getName());
                sb.append(this.delimeter);
                sb.append(stamp.getComment());
                sb.append("\n");

                bw.write(sb.toString());
            }

            bw.close();
            PMLogger.getInstance().debug("Serialization complete");
            return true;
        }
        catch(FileNotFoundException e){
            PMLogger.getInstance().error("Could not write to file, file not found: " + filename);
            return false;
        }
        catch(IOException e){
            PMLogger.getInstance().error("Caught IOException: " + e.getMessage());
            return false;
        }
    }

    public boolean serializeSummaryToCSVFromMaps(StillFaceCode delim1, StillFaceCode delim2,
                                                 List<List<StillFaceCodeCount>> summary, String filename){
        PMLogger.getInstance().debug("Serializing maps to summary file...");
        if(summary.size() != 3){
            PMLogger.getInstance().warn("Incorrect number of summaries in list. Should be 3");
            return false;
        }
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

            bw.write("Before " + delim1.getName() + " code name, count\n");
            for(StillFaceCodeCount count : summary.get(0)) {
                bw.write(count.getName() + this.delimeter + count.getCount() + "\n");
            }
            bw.write("After " + delim1.getName() + " code name, count\n");
            for(StillFaceCodeCount count : summary.get(1)) {
                bw.write(count.getName() + this.delimeter + count.getCount() + "\n");
            }
            bw.write("After " + delim2.getName() + " code name, count\n");
            for(StillFaceCodeCount count : summary.get(2)) {
                bw.write(count.getName() + this.delimeter + count.getCount() + "\n");
            }

            bw.close();
            PMLogger.getInstance().debug("Finished serializing summary data to file");
            return true;

        }
        catch(FileNotFoundException e){
            PMLogger.getInstance().error("Could not write to file, file not found: " + filename);
            return false;
        }
        catch(IOException e){
            PMLogger.getInstance().error("Caught IOException: " + e.getMessage());
            return false;
        }
    }

}
