package com.byu.pmedia.view;

import com.byu.pmedia.config.StillFaceConfig;
import com.byu.pmedia.database.DatabaseMode;
import com.byu.pmedia.database.StillFaceDatabaseInitializer;
import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.model.StillFaceModel;
import com.byu.pmedia.util.ErrorCode;

import javax.swing.*;
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
        StillFaceConfig.getInstance().initialize("projectmedia.datacenter.config");
        renderSplashFrame(g, 300, "Establishing database connection...");
        splash.update();
        DatabaseMode mode = DatabaseMode.valueOf(StillFaceConfig.getInstance().getAsString("database.mode"));
        StillFaceDatabaseInitializer initializer = new StillFaceDatabaseInitializer();
        boolean success = initializer.initialize(mode);
        if(!success){
            JOptionPane.showMessageDialog(null, "We encountered an error while trying to establish the" +
                    "database connection. Please check your connection and database settings and try again.");
            splash.close();
            System.exit(ErrorCode.DB_CONNECT_FAILURE);
        }
        //renderSplashFrame(g, 440, "Loading data and initializing model...");
        //splash.update();
        //StillFaceModel.getInstance().initialize(initializer.getDAO());
        renderSplashFrame(g, 600, "Loading GUI Components...");
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

}
