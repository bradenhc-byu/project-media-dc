package com.byu.pmedia.config;

import java.util.HashMap;
import java.util.Map;

public class ConfigEnumManager {

    private Map<String, IConfigEnum> enums = new HashMap<>();

    private static ConfigEnumManager singleton;

    public static ConfigEnumManager getInstance(){
        if(singleton == null){
            singleton = new ConfigEnumManager();
        }
        return singleton;
    }

    public void register(IConfigEnum e){
        this.enums.put(e.toString(), e);
    }

    public IConfigEnum lookup(String key){
        return this.enums.get(key);
    }

}
