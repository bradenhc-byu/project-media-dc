package com.byu.pmedia.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public JPanel getMainPanel() {
        return panelMain;
    }

}
