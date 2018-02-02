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
package com.byu.pmedia.config;

import com.byu.pmedia.log.PMLogger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * StillFaceConfig
 * Holds configuration information for the DataCenter. This class must be initialized with a .config file containing
 * key,value pairs where the key and value are separated by a colon (:). New pairs are separated by newline (\n).
 * Comments can be written into the file using the pound sign (#).
 * <p>
 * NOTE: The config file provided could possibly be overwritten if the user decides to save configuration. In doing so,
 *       the original order of pairs will not necessarily be conserved, and comments will be removed. This simple
 *       class is meant to control configuration mainly from the user's GUI interface.
 *
 * @author Braden Hitchcock
 */
public class StillFaceConfig {

    /* The default path to the config file. From the applicatoin root, it looks for an etc/ directory where the
    * config file(s) should be located. */
    private String configPath = "." + File.separator + "etc" + File.separator;

    /* The map holding configuration values. They are all stored as strings and can be converted to a selection of
    * primitive values via appropriate functions below. */
    private Map<String, String> configuration = new HashMap<>();

    /* The file that is used to initialize this instance of the StillFaceConfig object */
    private String filename;

    /* Singleton instance of this class that all other classes in the DataCenter can use */
    private static StillFaceConfig singleton;

    /**
     * Gets the singleton instance of this class.
     *
     * @return The singleton instance of the StillFaceConfig class
     */
    public static StillFaceConfig getInstance(){
        if(singleton == null){
            singleton = new StillFaceConfig();
        }
        return singleton;
    }

    /**
     * Initializes this instance of the config object with key value pairs from the provided config file. The
     * configuration (.config) file needs to be formatted with keys and values being separated by a colon (:) and
     * pairs separated by newlines (\n). Comments use the pound sign (#). An example is provided below:
     * <p>
     * # This is my comment
     * myNumber: 32
     * myString: This is my string value
     * myBoolean: true
     *
     * @param filename The name of the file to load from the default etc/ directory
     * @return True if the initialization succeeds. False otherwise.
     */
    public boolean initialize(String filename){
        try{
            this.filename = filename;
            BufferedReader br = new BufferedReader(new FileReader(this.configPath + filename));
            String line;
            while((line = br.readLine()) != null){
                if(line.startsWith("#")) continue;
                int index = line.indexOf(":");
                String key = line.substring(0, index).trim();
                String value = line.substring(index+1).trim();
                this.configuration.put(key, value);
            }
            br.close();
            return true;
        }
        catch(IOException e){
            PMLogger.getInstance().error("Unable to initialize configuration: " + e.getMessage());
            return false;
        }
    }

    /**
     * Takes the values stored in the configuration map and writes them back to the configuration file used to
     * initialize this instance. This will overwrite the file, so comments will be lost and the original order of
     * the pairs may not be preserved.
     *
     * @return True if the configurations are saved successfully. False otherwise.
     */
    public boolean save(){
        PMLogger.getInstance().info("Saving configuration to file");
        if(this.filename == null || this.filename.equals("")){
            PMLogger.getInstance().warn("Failed to save configuration. Invalid filename: " + filename);
            return false;
        }
        StringBuilder line = new StringBuilder();
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.configPath + this.filename));
            for(String key : configuration.keySet()){
                line.append(key).append(": ").append(configuration.get(key));
                bw.write(line.toString());
                bw.newLine();
                line.setLength(0);
                line.trimToSize();
            }
            bw.close();
            PMLogger.getInstance().info("Configuration successfully saved");
            return true;

        }
        catch(IOException e){
            PMLogger.getInstance().error("Unable to save configuration: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sets a configuration value in the map with an integer value. If the key does not already exist in the map, it
     * will be created.
     *
     * @param key The key to the configuration value
     * @param value The new value of the configuration
     */
    public void setWithInt(String key, int value){
        this.configuration.put(key, Integer.toString(value));
    }

    /**
     * Gets a configuration value in the map, returning it as an integer value. If the configuration value is not in
     * the map, or it is not an integer and the method fails, it will return -1.
     *
     * @param key The name of the configuration value to look up
     * @return An integer representation of the configuration value
     */
    public int getAsInt(String key){
        int value = -1;
        try{
            if(!this.configuration.containsKey(key)){
                PMLogger.getInstance().error("Failed to get configuration: key not in map");
                return value;
            }
            value = Integer.parseInt(this.configuration.get(key));
            return value;
        }
        catch(NumberFormatException e){
            PMLogger.getInstance().error("Failed to get integer configuration: " + e.getMessage());
            return value;
        }
    }

    /**
     * Sets a configuration value using the provided string. Since value in the map are stored as a string, this does
     * not have any error checking. (i.e. if the original value was an integer and the developer attempts to write the
     * string "my config value" to that key, it will succeed)
     *
     * @param key The name of the configuration value to set
     * @param value The new value of the configuration
     */
    public void setWithString(String key, String value){
        this.configuration.put(key, value);
    }

    /**
     * Gets a configuration value as a string.
     *
     * @param key The name of the configuration value to look up
     * @return The string value if the key exists. If it doesn't, it will return the empty string.
     */
    public String getAsString(String key){
        return this.configuration.getOrDefault(key, "");
    }

    /**
     * Sets the configuration value with the key to the provided boolean value. If the configuration value does not
     * exist in the map, it will be created.
     *
     * @param key The name of the configuration value to update or create.
     * @param value The new value of the configuration
     */
    public void setWithBoolean(String key, boolean value){
        String sValue = (value) ? "true" : "false";
        this.configuration.put(key, sValue);
    }

    /**
     * Get a configuration value as a boolean. If it is not formatted properly, then the method will default to false.
     *
     * @param key The name of the configuration value to lookup
     * @return True of the value is in the map and formatted correctly. False otherwise.
     */
    public boolean getAsBoolean(String key){
        String value = this.configuration.getOrDefault(key, "false");
        if(value.equals("true")){
            return true;
        }
        else if(value.equals("false")){
            return false;
        }
        else{
            PMLogger.getInstance().error("Unable to get config boolean value: " + value);
            return false;
        }
    }

    /**
     * @return The filename used to initialize this instance
     */
    public String getFilename() { return this.filename; }
}
