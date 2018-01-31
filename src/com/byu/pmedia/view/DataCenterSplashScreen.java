package com.byu.pmedia.view;

import com.byu.pmedia.config.StillFaceConfig;
import com.byu.pmedia.database.DatabaseMode;
import com.byu.pmedia.database.StillFaceDatabaseInitializer;
import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.model.StillFaceModel;

import java.awt.*;

public class DataCenterSplashScreen {

    private void renderSplashFrame(Graphics2D g, int progress, String message){
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, 640, 400);
        g.setPaintMode();
        g.setColor(new Color(0xCC, 0xCC, 0xCC));
        g.setFont(new Font("Arial Black", Font.PLAIN, 12));
        g.drawString(message, 10, 380);
        g.fillRect(2, 300, progress, 6);
    }

    public DataCenterSplashScreen(){
        final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            PMLogger.getInstance().warn("SplashScreen.getSplashScreen() returned null");
            return;
        }
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            System.out.println("g is null");
            return;
        }
        renderSplashFrame(g, 1, "Getting ready...");
        splash.update();
        fakeWait(1500);
        renderSplashFrame(g, 100, "Loading configuration...");
        splash.update();
        boolean success = StillFaceConfig.getInstance().initialize("projectmedia.datacenter.config");
        if(!success){
            String message = "We encountered an error while trying to initialize configuration the database connection. " +
                    "Please check your connection and database settings and try again.";
            exitOnError(message);
        }
        renderSplashFrame(g, 300, "Establishing database connection...");
        splash.update();
        DatabaseMode mode = DatabaseMode.valueOf(StillFaceConfig.getInstance().getAsString("database.mode"));
        StillFaceDatabaseInitializer initializer = new StillFaceDatabaseInitializer();
        success = initializer.initialize(mode);
        if(!success){
            String message = "We encountered an error while trying to establish the database connection. " +
                    "Please check your connection and database settings and try again.";
            exitOnError(message);
        }
        renderSplashFrame(g, 440, "Loading data and initializing model...");
        splash.update();
        success = StillFaceModel.getInstance().initialize(initializer.getDAO());
        if(!success){
            String message = "We encountered an error while trying to initialize the internal model. " +
                    "Please check the log messages try again.";
            exitOnError(message);
        }
        renderSplashFrame(g, 580, "Loading GUI Components...");
        splash.update();
        fakeWait(900);
        splash.close();
    }

    private void fakeWait(int milli){
        try{
            Thread.sleep(milli);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private void exitOnError(String message){
        new StillFaceErrorNotification(message).show();
        System.exit(1);
    }

}
