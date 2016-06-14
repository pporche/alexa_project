package Alexa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.util.*;


public class Display extends JFrame {
    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_HEIGHT = 550;
    private static String precedentKeys = "data/keys.ser";

    JFileChooser chooseFile = new JFileChooser();
    JFileChooser chooseDirectory = new JFileChooser();

    JTextField saveDirectoryField = new JTextField();
    JTextField openFileField = new JTextField();
    JTextField accessKeyField = new JTextField();
    JTextField secretKeyField = new JTextField();

    JLabel title = new JLabel();
    JLabel labelSearchDirectory = new JLabel();
    JLabel labelOpenFile = new JLabel();
    JLabel labelAccessKey = new JLabel();
    JLabel labelSecretKey = new JLabel();


    JButton searchDirectoryButton = new JButton("Browse");
    JButton openFileButton = new JButton("Browse");
    JButton validationButton = new JButton("Go!");

    JCheckBox rememberKeysCheckBox = new JCheckBox("Remember my keys", true);

    public Display() throws Exception {
        Container cp = this.getContentPane();

        cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

        final String[] keys = this.deserializeKeys();
        if(keys[0]!=null&&keys[1]!=null) {
            accessKeyField.setText(keys[0]);
            secretKeyField.setText(keys[1]);
        }


        labelSearchDirectory.setText("Directory for the output:");
        labelOpenFile.setText("CSV file to analyse:");
        labelAccessKey.setText("Access key:");
        labelSecretKey.setText("Secret key:");
        title.setText("Alexa URL Informations collector");

        saveDirectoryField.setMinimumSize(new Dimension(200, 20));
        openFileField.setMinimumSize(new Dimension(200, 20));
        accessKeyField.setMinimumSize(new Dimension(100, 20));
        secretKeyField.setMinimumSize(new Dimension(100, 20));
        validationButton.setMinimumSize(new Dimension(100, 25));

        saveDirectoryField.setMaximumSize(new Dimension(600, 20));
        openFileField.setMaximumSize(new Dimension(600, 20));
        accessKeyField.setMaximumSize(new Dimension(500, 20));
        secretKeyField.setMaximumSize(new Dimension(500, 20));
        validationButton.setMaximumSize(new Dimension(400, 25));


        cp.add(Box.createRigidArea(new Dimension(0,30)));
        cp.add(title);
        cp.add(Box.createRigidArea(new Dimension(0,50)));

        cp.add(labelAccessKey);
        cp.add(accessKeyField);
        accessKeyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        cp.add(Box.createRigidArea(new Dimension(0,30)));

        cp.add(labelSecretKey);
        cp.add(secretKeyField);
        secretKeyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        cp.add(rememberKeysCheckBox);

        cp.add(Box.createRigidArea(new Dimension(0,30)));

        cp.add(labelSearchDirectory);
        cp.add(saveDirectoryField);
        saveDirectoryField.setAlignmentX(Component.LEFT_ALIGNMENT);
        cp.add(searchDirectoryButton);
        searchDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DirectoryChooser dc = new DirectoryChooser();
                String directory = dc.chooseDirectory();
                saveDirectoryField.setText(directory);
                dc.dispose();
            }
        });
        cp.add(Box.createRigidArea(new Dimension(10,30)));

        cp.add(labelOpenFile);
        cp.add(openFileField);
        openFileField.setAlignmentX(Component.LEFT_ALIGNMENT);
        cp.add(openFileButton);
        openFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MyFileChooser fc = new MyFileChooser();
                String file = fc.chooseFile();
                openFileField.setText(file);
                fc.dispose();
            }
        });

        cp.add(Box.createRigidArea(new Dimension(10,40)));


        cp.add(validationButton);
        validationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String accessKey = accessKeyField.getText();
                    String secretKey = secretKeyField.getText();

                    if (rememberKeysCheckBox.isSelected()) {
                        keys[0] = accessKey;
                        keys[1] = secretKey;
                        serializeKeys(keys);
                    }

                    String saveDirectory = saveDirectoryField.getText();
                    String openFile = openFileField.getText();
                    UrlInfo urlInfo = new UrlInfo();
                    HashMap<String, InfosAlexa> hmInitial = urlInfo.deserializePrecedent();
                    HashMap<String, String> hmEntering = urlInfo.readCsv(openFile);
                    HashMap<String, InfosAlexa> hmComplete = urlInfo.getInfosFromAlexa(hmEntering, hmInitial, accessKey, secretKey);
                    urlInfo.serializePrecedent(hmComplete);
                    urlInfo.writeCsv(hmComplete, saveDirectory+"/alexaOutput.csv");
                    dispose();
                } catch (Exception exception) {
                    exception.printStackTrace(System.err);
                }
            }
        });

        cp.add(Box.createRigidArea(new Dimension(10,30)));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Alexa URL Informations collector"); 
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setVisible(true);
    }


    private String[] deserializeKeys() throws Exception {
        String[] array = new String[2];
        File yourFile = new File(precedentKeys);
        if(yourFile.exists()) {
            try
            {
                FileInputStream fis = new FileInputStream(precedentKeys);
                ObjectInputStream ois = new ObjectInputStream(fis);
                array = (String[]) ois.readObject();
                ois.close();
                fis.close();
            } catch(IOException ioe)
            {
                ioe.printStackTrace();
            } catch(ClassNotFoundException c)
            {
                System.out.println("Class not found");
                c.printStackTrace();
            }
        }
        return array;
    }


    private void serializeKeys(String[] keys) throws Exception {
        File yourFile = new File(precedentKeys);
        if(!yourFile.exists()) {
            yourFile.createNewFile();
        } 
        try {
            FileOutputStream fos = new FileOutputStream(precedentKeys);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(keys);
            fos.flush();
            oos.close();
            fos.close();
        } catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        new Display();
    }
}
