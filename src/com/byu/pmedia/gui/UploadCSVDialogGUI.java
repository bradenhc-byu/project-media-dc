package com.byu.pmedia.gui;

import com.byu.pmedia.model.StillFaceVideoData;
import com.byu.pmedia.parser.CodedVideoCSVParser;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadCSVDialogGUI extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton fileChooserButton;
    private JTextField chosenFileNameTextField;
    private JTextField yearTextField;
    private JTextField familyIDTextField;
    private JTextField participantNumTextField;
    private JLabel yearLabel;
    private JLabel familyIDLabel;
    private JLabel participantNumLabel;

    private String chosenFile;
    private DataCenterClientGUI parent;

    public UploadCSVDialogGUI(DataCenterClientGUI parent) {
        this.parent = parent;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.setEnabled(false);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Handle Choose File button
        fileChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                chosenFile = onChooseFile();
                if(chosenFile != null) {
                    buttonOK.setEnabled(true);
                    chosenFileNameTextField.setText(getFilenameFromPath(chosenFile));
                    extractFileNameData(chosenFile);
                }
            }
        });
    }

    private void onOK() {
        // add your code here
        importFile(this.chosenFile);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    @Override
    public void dispose(){
        this.chosenFile = null;
        this.buttonOK.setEnabled(false);
        this.chosenFileNameTextField.setText(null);
        this.yearTextField.setText(null);
        this.familyIDTextField.setText(null);
        this.participantNumTextField.setText(null);
        super.dispose();
    }

    private String onChooseFile(){
        JFileChooser fc = new JFileChooser();
        int returnval = fc.showOpenDialog(null);
        if(returnval == JFileChooser.APPROVE_OPTION){
            File selectedFile = fc.getSelectedFile();
            return selectedFile.getAbsolutePath();
        }
        return null;
    }

    private void importFile(String filename){
        if(filename == null){
            return;
        }
        StillFaceVideoData videoData = new StillFaceVideoData();
        boolean success = new CodedVideoCSVParser().parseFromCSVIntoCodedVideoData(filename, videoData);
        if(success){
            //this.dataDispalyTextArea.setText(videoData.toPrettyString());
        }
    }

    private String getFilenameFromPath(String filepath){
        String[] parts = filepath.split(File.separator);
        String filename = parts[parts.length - 1];
        return filename;
    }

    private void extractFileNameData(String filepath){
        String filename = getFilenameFromPath(filepath);
        Pattern p = Pattern.compile("[0-9]-[0-9]+-[0-9]+ SF [a-zA-z ]+-evts");
        if(p.matcher(filename).find()){
            p = Pattern.compile("\\d+");
            Matcher m = p.matcher(filename);
            int count = 0;
            while(m.find()){
                switch(count){
                    case 0:
                        yearTextField.setText(m.group(0));
                        count++;
                        break;

                    case 1:
                        familyIDTextField.setText(m.group(0));
                        count++;
                        break;

                    case 2:
                        participantNumTextField.setText(m.group(0));
                        count++;
                        break;

                    default:
                        break;
                }
            }
        }
    }
}
