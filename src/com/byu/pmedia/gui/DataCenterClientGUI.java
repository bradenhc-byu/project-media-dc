package com.byu.pmedia.gui;

import com.byu.pmedia.model.CodedVideoData;
import com.byu.pmedia.parser.CodedVideoCSVParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class DataCenterClientGUI {

    private JPanel panelMain;
    private JButton importDataButton;
    private JButton button2;
    private JButton button3;
    private JTextArea dataDispalyTextArea;
    private JLabel welcomeLabel;
    private JScrollPane dataDisplayScrollPane;

    private UploadCSVDialogGUI uploadDialog;

    public DataCenterClientGUI() {

        this.uploadDialog = new UploadCSVDialogGUI(this);

        importDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                uploadDialog.pack();
                uploadDialog.setLocationRelativeTo(null);
                uploadDialog.setTitle("Upload CSV File");
                uploadDialog.setVisible(true);
            }
        });
    }

    private static void displayGUI(){
        JFrame frame = new JFrame("ProjectMEDIA Data Center");
        frame.setContentPane(new DataCenterClientGUI().panelMain);
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
