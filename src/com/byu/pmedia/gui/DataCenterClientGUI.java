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

    public DataCenterClientGUI() {

        importDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String filename = chooseFile();
                if(filename != null){
                    importFile(filename);
                }
            }
        });
    }

    private String chooseFile(){
        JFileChooser fc = new JFileChooser();
        int returnval = fc.showOpenDialog(null);
        if(returnval == JFileChooser.APPROVE_OPTION){
            File selectedFile = fc.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return null;
    }

    private void importFile(String filename){
        CodedVideoData videoData = new CodedVideoData();
        boolean success = new CodedVideoCSVParser().parseFromCSVIntoCodedVideoData(filename, videoData);
        if(success){
            this.dataDispalyTextArea.setText(videoData.toString());
        }
    }

    private static void displayGUI(){
        JFrame frame = new JFrame("ProjectMEDIA Data Center");
        frame.setContentPane(new DataCenterClientGUI().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
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
