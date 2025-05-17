package view ; 

import java.awt.*; 

public class MainGui { 
    private Frame mainFrame;
    private Panel controlPanel;

    public MainGui() {
        mainFrame = new Frame("Main GUI");
        controlPanel = new Panel();
        mainFrame.add(controlPanel);
        mainFrame.setSize(400, 400);
        mainFrame.setVisible(true);
    }

    public void makeUI() {
        // Implementasi pembuatan UI
    }
}