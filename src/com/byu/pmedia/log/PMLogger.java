package com.byu.pmedia.log;

public class PMLogger {

    private final LogPrefix prefix;
    private static PMLogger singleton;

    public PMLogger(){
        this.prefix = new LogPrefix("=pm=");
    }

    public static PMLogger getInstance(){
        if(singleton == null){
            singleton = new PMLogger();
        }
        return singleton;
    }

    public void info(String message){
        logOut("INFO", message);
    }

    public void debug(String message){
        logOut("DEBUG", message);
    }

    public void warn(String message){
        logErr("WARN", message);
    }

    public void error(String message){
        logErr("ERROR", message);
    }

    private void logOut(String logLevel, String message){
        System.out.println(this.prefix.get(logLevel) + message);
    }

    private void logErr(String logLevel, String message){
        System.err.println(this.prefix.get(logLevel) + message);
    }

}
