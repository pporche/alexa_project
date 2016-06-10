package Alexa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;


public class DirectoryChooser extends JFrame {
   public static final int WINDOW_WIDTH = 300;
   public static final int WINDOW_HEIGHT = 150;
   // ......

   JFileChooser chooseDirectory = new JFileChooser();


   // private variables of UI components
   // ......

   /** Constructor to setup the UI components */
   public DirectoryChooser() {
      Container cp = this.getContentPane();
      // Content-pane sets layout
      cp.setLayout(new FlowLayout());
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit when close button clicked
      setTitle("......"); // "this" JFrame sets title
      setSize(WINDOW_WIDTH, WINDOW_HEIGHT);  // or pack() the components
      setVisible(true);   // show it
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