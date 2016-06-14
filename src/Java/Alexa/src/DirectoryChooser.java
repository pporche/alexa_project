package Alexa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;


public class DirectoryChooser extends JFrame {
    public static final int WINDOW_WIDTH = 300;
    public static final int WINDOW_HEIGHT = 150;

    JFileChooser chooseDirectory = new JFileChooser();


    /** Constructor to setup the UI components */
    public DirectoryChooser() {
        Container cp = this.getContentPane();
        cp.setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Choose a directory");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setVisible(false);
        cp.add(chooseDirectory);
    }




    public String chooseDirectory() {
        chooseDirectory.setCurrentDirectory(new java.io.File("."));
        chooseDirectory.setDialogTitle("Choose a directory");
        chooseDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooseDirectory.setAcceptAllFileFilterUsed(false);
        chooseDirectory.showOpenDialog(DirectoryChooser.this);
        return (""+chooseDirectory.getSelectedFile());
    }

}