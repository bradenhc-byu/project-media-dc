package com.byu.pmedia.config;

import com.byu.pmedia.log.PMLogger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StillFaceConfig {

    private String configPath = "." + File.separator + "etc" + File.separator;
    private Map<String, String> configuration = new HashMap<>();
    private String filename;

    private static StillFaceConfig singleton;

    public static StillFaceConfig getInstance(){
        if(singleton == null){
            singleton = new StillFaceConfig();
        }
        return singleton;
    }

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

    public void setWithInt(String key, int value){
        this.configuration.put(key, Integer.toString(value));
    }

    public int getAsInt(String key){
        return Integer.parseInt(this.configuration.get(key));
    }

    public void setWithString(String key, String value){
        this.configuration.put(key, value);
    }

    public String getAsString(String key){
        return this.configuration.get(key);
    }

    public void setWithBoolean(String key, boolean value){
        String sValue = (value) ? "true" : "false";
        this.configuration.put(key, sValue);
    }

    public boolean getAsBoolean(String key){
        String value = this.configuration.get(key);
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

    //public void setWithEnum(String key, IConfigEnum e){
    //    this.configuration.put(key, e.toString());
    //}

    //public IConfigEnum getAsEnum(String key){
    //    return ConfigEnumManager.getInstance().lookup(this.configuration.get(key));
    //}

}
