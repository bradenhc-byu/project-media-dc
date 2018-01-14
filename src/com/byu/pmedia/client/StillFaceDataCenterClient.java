package com.byu.pmedia.client;

import com.byu.pmedia.view.DataCenterClientGUI;
import com.byu.pmedia.view.DataCenterSplashScreen;

import javax.swing.*;

public class StillFaceDataCenterClient {

    private static void initialize(){
        DataCenterSplashScreen splashScreen = new DataCenterSplashScreen();
    }

    private static void displayGUI(){
        JFrame frame = new JFrame("ProjectMEDIA Data Center");
        frame.setContentPane(new DataCenterClientGUI().getMainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                initialize();
                displayGUI();
            }
        });
    }
}
