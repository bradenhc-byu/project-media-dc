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
package com.byu.pmedia.view;

import com.byu.pmedia.config.StillFaceConfig;
import com.byu.pmedia.database.DatabaseMode;
import com.byu.pmedia.database.StillFaceDatabaseInitializer;
import com.byu.pmedia.log.PMLogger;
import com.byu.pmedia.model.StillFaceModel;

import java.awt.*;

/**
 * DataCenterSplashScreen
 * Uses Java Swing to show a small graphic and a progress bar while the application establishes a connection with the
 * database and loads data into the internal model. The constructor of the Splash Screen attempts to make connections
 * and configure the application. There are no other public methods available.
 *
 * @author Braden Hitchcock
 */
public class DataCenterSplashScreen {

    /**
     * Updates the visible splash screen frame with the latest progress bar length and text
     *
     * @param g The graphics object representing the frame
     * @param progress The progress of the initialization
     * @param message The message to display below the progress bar
     */
    private void renderSplashFrame(Graphics2D g, int progress, String message){
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, 640, 400);
        g.setPaintMode();
        g.setColor(new Color(0xCC, 0xCC, 0xCC));
        g.setFont(new Font("Arial Black", Font.PLAIN, 12));
        g.drawString(message, 10, 380);
        g.fillRect(2, 300, progress, 6);
    }

    /**
     * Constructor. After creating a new instance of the DataCenterSplashScreen, it will initialize and configure
     * the running application. This should be called before opening the JavaFX GUI from the main client.
     */
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
        fakeWait(600);
        splash.close();
    }

    /**
     * For aesthetic purposes, provides a small delay so the user can see what is going on with the initialization
     *
     * @param milli Number of milliseconds to delay
     */
    private void fakeWait(int milli){
        try{
            Thread.sleep(milli);
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * If an error occurs, display an error notification with a message and then exits the application.
     *
     * @param message The message to display inside the notification
     */
    private void exitOnError(String message){
        new StillFaceErrorNotification(message).show();
        System.exit(1);
    }

}
