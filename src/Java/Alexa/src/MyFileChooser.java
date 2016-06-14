package Alexa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;


public class MyFileChooser extends JFrame {
    public static final int WINDOW_WIDTH = 300;
    public static final int WINDOW_HEIGHT = 150;

    JFileChooser chooseFile = new JFileChooser();

    /** Constructor to setup the UI components */
    public MyFileChooser() {
        Container cp = this.getContentPane();
        cp.setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Choose a csv file");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setVisible(false);
        cp.add(chooseFile);
    }


    public String chooseFile() {
        chooseFile.setCurrentDirectory(new java.io.File("."));
        chooseFile.setDialogTitle("Choose a file");
        chooseFile.setAcceptAllFileFilterUsed(false);
        FileFilter filter = new FileNameExtensionFilter("CSV file", "csv");
        chooseFile.addChoosableFileFilter(filter);
        chooseFile.showOpenDialog(MyFileChooser.this);
        return (""+chooseFile.getSelectedFile());
    }

}