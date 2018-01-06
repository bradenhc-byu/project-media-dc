package com.byu.pmedia.parser;

import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.model.CodeStamp;
import com.byu.pmedia.model.CodedVideoData;

import java.io.*;

public class CodedVideoCSVParser {

    private String delimeter;

    public CodedVideoCSVParser(){
        this.delimeter = ",";
    }

    public boolean parseFromCSVIntoCodedVideoData(String filename, CodedVideoData videoData){

        PMLogger.getInstance().debug("Parsing data from " + filename + " into CodedVideoDataObject");

        try{
            // Create a new buffered reader to read the CSV file
            BufferedReader br = new BufferedReader(new FileReader(filename));


            // Clear any existing data from the CodedVideoData object. Placing this line after creating the
            // buffered reader ensures that the provided CSV file exists
            videoData.clear();

            // Begin parsing from the CSV file
            String line;
            while((line = br.readLine()) != null){

                // Get the data from the line
                String[] data = line.split(this.delimeter);

                // Strip away the header contents from the CSV file (if it exists)
                try{
                    videoData.addCodeStamp(new CodeStamp(Integer.parseInt(data[0]),
                            Integer.parseInt(data[1]),
                            data[2]));
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

    public boolean serializeToCSVFromCodedVideoData(CodedVideoData videoData, String filename){

        PMLogger.getInstance().debug("Serialize CodedVideoData to CSV file: " + filename);

        try{

            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));

            for(CodeStamp stamp : videoData.getData()){
                StringBuilder sb = new StringBuilder();
                sb.append(stamp.getTime());
                sb.append(this.delimeter);
                sb.append(stamp.getDuration());
                sb.append(this.delimeter);
                sb.append(stamp.getType().getName());
                sb.append(this.delimeter);
                sb.append(stamp.getComment());
                sb.append("\n");

                bw.write(sb.toString());
            }

            bw.close();

        }
        catch(FileNotFoundException e){
            PMLogger.getInstance().error("Could not write to file, file not found: " + filename);
            return false;
        }
        catch(IOException e){
            PMLogger.getInstance().error("Caught IOException: " + e.getMessage());
            return false;
        }

        PMLogger.getInstance().debug("Serialization complete");

        return true;
    }

}
