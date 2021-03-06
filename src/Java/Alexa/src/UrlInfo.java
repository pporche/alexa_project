package Alexa;

import javax.xml.bind.DatatypeConverter;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.w3c.dom.NodeList;
import org.w3c.dom.*;
import org.xml.sax.*; 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.stream.*;
import javax.xml.transform.*;

/**
 * Makes a request to the Alexa Web Information Service UrlInfo action.
 */
public class UrlInfo {

    private static final String ACTION_NAME = "UrlInfo";
    private static final String RESPONSE_GROUP_NAME = "Rank,RankByCountry";
    private static final String SERVICE_HOST = "awis.amazonaws.com";
    private static final String AWS_BASE_URL = "http://" + SERVICE_HOST + "/?";
    private static final String HASH_ALGORITHM = "HmacSHA256";
    private static final String DATEFORMAT_AWS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final Integer NUMBER_OF_DAYS = 30;

    private static String precedentData = "data/precedent.ser";

    private String accessKeyId;
    private String secretAccessKey;
    private String site;

    public UrlInfo(String accessKeyId, String secretAccessKey, String site) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.site = site;
    }

    public UrlInfo() {
        this.accessKeyId = "";
        this.secretAccessKey = "";
        this.site = "";
    }

    /**
     * Generates a timestamp for use with AWS request signing
     *
     * @param date current date
     * @return timestamp
     */
    protected static String getTimestampFromLocalTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATEFORMAT_AWS);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }

    /**
     * Computes RFC 2104-compliant HMAC signature.
     *
     * @param data The data to be signed.
     * @return The base64-encoded RFC 2104-compliant HMAC signature.
     * @throws java.security.SignatureException
     *          when signature generation fails
     */
    protected String generateSignature(String data) throws java.security.SignatureException {
        String result;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(
                secretAccessKey.getBytes(), HASH_ALGORITHM);
            Mac mac = Mac.getInstance(HASH_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = DatatypeConverter.printBase64Binary(rawHmac);
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    /**
     * Serializes the Hashmap containing the already computed data.
     *
     * @param hmap The hashmap to be serialized.
     */
    public static void serializePrecedent(HashMap<String, InfosAlexa> hmap) throws Exception {
        File yourFile = new File(precedentData);
        if(!yourFile.exists()) {
            yourFile.createNewFile();
        } 
        try {
            FileOutputStream fos = new FileOutputStream(precedentData);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(hmap);
            fos.flush();
            oos.close();
            fos.close();
        } catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /**
     * Deserializes the HashMap containing the previously computed data.
     *
     * @return The HashMap containing the previously computed data.
     *
     */
    public static HashMap<String, InfosAlexa> deserializePrecedent() throws Exception {
        HashMap<String, InfosAlexa> hmap = new HashMap<String, InfosAlexa>();
        File yourFile = new File(precedentData);
        if(yourFile.exists()) {
            try
            {
                FileInputStream fis = new FileInputStream(precedentData);
                ObjectInputStream ois = new ObjectInputStream(fis);
                hmap = (HashMap<String,InfosAlexa>) ois.readObject();
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
        return hmap;
    }

/**
     * Puts the data in the entry .csv file into a HashMap.
     *
     * @param nameOfCsvFile The name of the csv file to be read.
     * @return The HashMap containing the names and URL read on the csv file.
     */
    public static HashMap<String, String> readCsv(String nameOfCsvFile) {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        HashMap<String, String> hmap = new HashMap<String, String>();
        try {
            br = new BufferedReader(new FileReader(nameOfCsvFile));
            while ((line = br.readLine()) != null) {
                String[] column = line.split(cvsSplitBy);
                if (column.length>1){
                    String nameInCsv = column[0];
                    String urlInCsv = column[1];

                    if((nameInCsv!="")&&(urlInCsv!="")){
                        if(!urlInCsv.startsWith("http")){
                            urlInCsv="http://"+urlInCsv;
                        }
                        hmap.put(nameInCsv, urlInCsv);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return hmap;
    }

    /**
     * Writes the content of a complete HashMap into a csv file.
     *
     * @param myHashMap The HashMap to be written in a csv file.
     *          fileName The name of the csv file to be created.
     */
    public static void writeCsv(HashMap<String, InfosAlexa> myHashMap, String fileName) {
        String eol = System.getProperty("line.separator");
        CountryCodeConverter converter = new CountryCodeConverter();
        try (Writer writer = new FileWriter(fileName, false)) {
            for (Map.Entry<String, InfosAlexa> entry : myHashMap.entrySet()) {
                String[] countryName = new String[200];
                Integer[] countryRank = new Integer[200];
                Float[] countryPercentage = new Float[200];
                String name = entry.getKey();
                InfosAlexa infos = entry.getValue();
                Map<String, Float> sortedCountries = MapUtil.sortByValue(infos.getPercentUserCountry());
                Integer i = 0;
                for(Map.Entry<String, Float> e : sortedCountries.entrySet()) {
                    countryName[i] = e.getKey();
                    countryRank[i] = infos.getRankInCountry().get(e.getKey());
                    countryPercentage[i] = e.getValue();
                    i++;
                }
                Integer rank = infos.getRank();
                String url = infos.getUrl();
                writer.append(""+rank).append(',');
                writer.append(name).append(',');
                writer.append(url).append(',');
                for(Integer j=4;j>=0;j--){
                    if (countryName[j]!=null){
                        writer.append(converter.getFullCountryName(countryName[j])).append(": ");
                        writer.append(""+countryPercentage[j]).append("% - rank: ");
                        writer.append(""+countryRank[j]).append(',');
                    }
                }
                writer.append(eol);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Removes the last character of a string.
     *
     * @param str The string to be cut.
     * @return The cut string.
     */
    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }

    private static boolean isOlderThan(Calendar dateToCompare, Integer numberOfDays) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -numberOfDays);
        return(dateToCompare.before(c));
    }

    /**
     * Gets all the informations from the Alexa Web Information service.
     *
     * @param  entryHMap The HashMap created from the entry csv file.
     *          completeHMap The HashMap containing all the previously computed data.
     *          accessKey The user's AWS access key.
     *          secretKey The user's AWS secret key.
     * @return The completed HashMap with newly fetched informations.
     */
    public static HashMap<String, InfosAlexa> getInfosFromAlexa(HashMap<String, String> entryHMap,
                                                                HashMap<String, InfosAlexa> completeHMap, 
                                                                String accessKey, 
                                                                String secretKey) throws Exception {
        HashMap<String, InfosAlexa> result = new HashMap<String, InfosAlexa>();
        result.putAll(completeHMap);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder docBuilder= dbf.newDocumentBuilder();
        for (String name : entryHMap.keySet()) {
            if ((!completeHMap.containsKey(name))||isOlderThan((completeHMap.get(name).getDateOfQuery()),NUMBER_OF_DAYS)) {
                UrlInfo urlInfo = new UrlInfo(accessKey, secretKey, entryHMap.get(name));
                String query = urlInfo.buildQuery();
                String toSign = "GET\n" + SERVICE_HOST + "\n/\n" + query;
                String signature = urlInfo.generateSignature(toSign);
                String uri = AWS_BASE_URL + query + "&Signature=" + URLEncoder.encode(signature, "UTF-8");
                InfosAlexa infos = new InfosAlexa();
                URL url = new URL(uri);
                HashMap<String, Integer> rankInCountry = new HashMap<String, Integer>();
                HashMap<String, Float> percentUserCountry = new HashMap<String, Float>();
                URLConnection conn = url.openConnection();
                InputStream in = conn.getInputStream();
                Document responseDoc = dbf.newDocumentBuilder().parse(in);
                Element ranknode = (Element) responseDoc.getElementsByTagNameNS("*", "Rank").item(0);
                if (ranknode.getFirstChild()!=null){
                    Integer rankToSet = Integer.parseInt(ranknode.getFirstChild().getNodeValue());
                    infos.setRank(rankToSet);
                } else {
                    infos.setRank(999999999);
                }
                infos.setUrl(entryHMap.get(name));
                NodeList countries = responseDoc.getElementsByTagNameNS("*", "Country");
                for (int i = 0; i < countries.getLength(); i++) {
                    Element country = (Element) countries.item(i);
                    String countryCode = country.getAttribute("Code");
                    if(countryCode.length()!=1){
                        Element ric = (Element) country.getElementsByTagNameNS("*", "Rank").item(0);
                        rankInCountry.put(countryCode, Integer.parseInt(ric.getFirstChild().getNodeValue()));
                    } else {
                        rankInCountry.put(countryCode,0);
                    }
                    Element puc = (Element) country.getElementsByTagNameNS("*", "Users").item(0);
                    percentUserCountry.put(countryCode, Float.parseFloat(removeLastChar(puc.getFirstChild().getNodeValue())) );
                }
                infos.setRankInCountry(rankInCountry);
                infos.setPercentUserCountry(percentUserCountry);
                result.put(name, infos);
            }
        }
        return result;
    }

    /**
     * Builds the query string
     */
    protected String buildQuery() throws UnsupportedEncodingException {
        String timestamp = getTimestampFromLocalTime(Calendar.getInstance().getTime());
        Map<String, String> queryParams = new TreeMap<String, String>();
        queryParams.put("Action", ACTION_NAME);
        queryParams.put("ResponseGroup", RESPONSE_GROUP_NAME);
        queryParams.put("AWSAccessKeyId", accessKeyId);
        queryParams.put("Timestamp", timestamp);
        queryParams.put("Url", site);
        queryParams.put("SignatureVersion", "2");
        queryParams.put("SignatureMethod", HASH_ALGORITHM);
        String query = "";
        boolean first = true;
        for (String name : queryParams.keySet()) {
            if (first)
                first = false;
            else
                query += "&";
            query += name + "=" + URLEncoder.encode(queryParams.get(name), "UTF-8");
        }
        return query;
    }
}