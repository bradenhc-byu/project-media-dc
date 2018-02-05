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
package com.byu.pmedia.parser;

import com.byu.pmedia.model.StillFaceCode;
import com.byu.pmedia.model.StillFaceCodeCount;
import com.byu.pmedia.model.StillFaceData;
import com.byu.pmedia.model.StillFaceVideoData;

import java.io.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * StillFaceCSVParser
 * Provides the ability to parse data from properly formatted CSV files into the data structures of the StillFaceModel.
 * This also provides the ability to write data to an external file as an entire video or as summary data.
 *
 * @author Braden Hitchcock
 */
public class StillFaceCSVParser {

    /* Grab an instance of the logger */
    private final static Logger logger =Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /* The delimiter used in reading and writing CSV files. Defaults to a comma */
    private String delimeter;

    public StillFaceCSVParser(){
        this.delimeter = ",";
    }

    /**
     * Reads properly formatted data from a CSV file into StillFaceData objects and creates a list of the data that
     * can be used to write to the database.
     *
     * @param filename The file to read
     * @param videoData The StillFaceVideoData object to populate with the data from the file
     *
     * @return True if successful, false otherwise
     */
    public boolean parseFromCSVIntoCodedVideoData(String filename, StillFaceVideoData videoData){

        logger.fine("Parsing data from " + filename + " into CodedVideoDataObject");

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
                    logger.fine("Detected possible CSV header, skipping...");
                }

            }

            br.close();
        }
        catch(FileNotFoundException e){
            logger.severe("Could not read file, file not found: " + filename);
            return false;
        }
        catch(IOException e){
            logger.severe("Caught IOException: " + e.getMessage());
            return false;
        }

        logger.fine("Parsing complete");

        return true;
    }

    /**
     * Takes data from the provided StillFaceVideoData object and writes it to a CSV file.
     *
     * @param videoData The object containing information to write
     * @param filename The name of the file to write to
     * @return True if successful, false otherwise
     */
    public boolean serializeToCSVFromCodedVideoData(StillFaceVideoData videoData, String filename){

        logger.fine("Serialize StillFaceVideoData to CSV file: " + filename);

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
            logger.fine("Serialization complete");
            return true;
        }
        catch(FileNotFoundException e){
            logger.severe("Could not write to file, file not found: " + filename);
            return false;
        }
        catch(IOException e){
            logger.severe("Caught IOException: " + e.getMessage());
            return false;
        }
    }

    /**
     * Given a list of summary information from a set of data objects, this will write those to a CSV file with the code
     * delimiters provided used to separate the sections of information
     *
     * @param delim1 The first StillFaceCode object acting as a delimiter used in the summary data
     * @param delim2 The second StillFaceCode object acting as a delimiter used in the summary data
     * @param summary A list of the three summary lists to write to the file. These list contain StillFaceCodeCount
     *                objects
     * @param filename The name of the file to write to
     * @return True if the serialization succeeds. False otherwise.
     */
    public boolean serializeSummaryToCSVFromLists(StillFaceCode delim1, StillFaceCode delim2,
                                                  List<List<StillFaceCodeCount>> summary, String filename){
        logger.fine("Serializing maps to summary file...");
        if(summary.size() != 3){
            logger.warning("Incorrect number of summaries in list. Should be 3");
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
            logger.fine("Finished serializing summary data to file");
            return true;

        }
        catch(FileNotFoundException e){
            logger.severe("Could not write to file, file not found: " + filename);
            return false;
        }
        catch(IOException e){
            logger.severe("Caught IOException: " + e.getMessage());
            return false;
        }
    }
}
