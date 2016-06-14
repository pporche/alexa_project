package Alexa;

import java.io.*;
import java.util.*;

public class Testing {

    public static void main(String[] args) throws Exception {

        Console console = System.console();

        String accessKey = console.readLine("Enter access key:");
        String secretKey = console.readLine("Enter secret key:");
        String nameOfCsvFile = console.readLine("Enter csv input:");

        UrlInfo urlInfo = new UrlInfo();
        HashMap<String, InfosAlexa> hmInitial = urlInfo.deserializePrecedent();
        HashMap<String, String> hmEntering = urlInfo.readCsv(nameOfCsvFile);
        HashMap<String, InfosAlexa> hmComplete = urlInfo.getInfosFromAlexa(hmEntering, hmInitial, accessKey, secretKey);
        urlInfo.serializePrecedent(hmComplete);

        String csvOutput = console.readLine("Done! where to save the output:");
        urlInfo.writeCsv(hmComplete, csvOutput);
    }
}