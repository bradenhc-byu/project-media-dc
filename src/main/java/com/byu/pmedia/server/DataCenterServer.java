package com.byu.pmedia.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class DataCenterServer {

    /* Grab an instance of the logger */
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private DataCenterServer(){

    }

    public void start(){
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"java", "-jar", "lib/derbyrun.jar",
                    "server","start"});
            InputStream in = process.getInputStream();
            InputStream err = process.getErrorStream();
            while(process.isAlive()){
                if(in.available() != 0){
                    byte b[]=new byte[in.available()];
                    in.read(b,0,b.length);
                    logger.info(new String(b));
                }
                if(err.available() != 0){
                    byte b[]=new byte[err.available()];
                    err.read(b,0,b.length);
                    logger.severe(new String(b));
                }
                waitForASecond();
            }
        }
        catch(IOException e){
            System.err.println("Failed to start server process");
        }
    }

    private void waitForASecond(){
        try{
            Thread.sleep(1000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DataCenterServer().start();
    }

}
