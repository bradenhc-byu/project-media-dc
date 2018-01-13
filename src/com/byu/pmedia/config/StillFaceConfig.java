package com.byu.pmedia.config;

import com.byu.pmedia.log.PMLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StillFaceConfig {

    private String configPath = "." + File.separator + "etc" + File.separator;
    private Map<String, String> configuration = new HashMap<>();

    private static StillFaceConfig singleton;

    public static StillFaceConfig getInstance(){
        if(singleton == null){
            singleton = new StillFaceConfig();
        }
        return singleton;
    }

    public boolean initialize(String filename){
        try{
            BufferedReader br = new BufferedReader(new FileReader(this.configPath + filename));
            String line;
            while((line = br.readLine()) != null){
                if(line.startsWith("#")) continue;
                int index = line.indexOf(":");
                String key = line.substring(0, index).trim();
                String value = line.substring(index+1).trim();
                this.configuration.put(key, value);
            }
            return true;
        }
        catch(IOException e){
            PMLogger.getInstance().error("Unable to initialize configuration: " + e.getMessage());
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
        try {
            return value.equals("true") || Integer.parseInt(value) > 0;
        }
        catch(NumberFormatException e){
            PMLogger.getInstance().warn("Config request format error: " + e.getMessage());
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
