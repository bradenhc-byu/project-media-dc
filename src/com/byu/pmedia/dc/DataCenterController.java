package com.byu.pmedia.dc;

import com.byu.pmedia.gui.DataCenterClientGUI;

import javax.swing.*;

public class DataCenterController {

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
                displayGUI();
            }
        });
    }
}
