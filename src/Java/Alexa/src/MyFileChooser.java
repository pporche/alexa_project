package Alexa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;


public class MyFileChooser extends JFrame {
   public static final int WINDOW_WIDTH = 300;
   public static final int WINDOW_HEIGHT = 150;
   // ......

   JFileChooser chooseFile = new JFileChooser();


   // private variables of UI components
   // ......

   /** Constructor to setup the UI components */
   public MyFileChooser() {
      Container cp = this.getContentPane();
      // Content-pane sets layout
      cp.setLayout(new FlowLayout());
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit when close button clicked
      setTitle("......"); // "this" JFrame sets title
      setSize(WINDOW_WIDTH, WINDOW_HEIGHT);  // or pack() the components
      setVisible(true);   // show it
      cp.add(chooseFile);
   }




	public String chooseFile() {
      chooseFile.setCurrentDirectory(new java.io.File("."));
      chooseFile.setDialogTitle("Choose a directory");
      //chooseFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      chooseFile.setAcceptAllFileFilterUsed(false);
      chooseFile.showOpenDialog(MyFileChooser.this);
      return (""+chooseFile.getSelectedFile());
   }

}