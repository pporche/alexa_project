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

    ///////////////
    // DO CHANGE //
    ///////////////
    //next are Pierre Porche's personnal keys.
    private static String precedentData = "data/precedent.ser";


    private static final String DATEFORMAT_AWS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

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
    protected String generateSignature(String data)
            throws java.security.SignatureException {
        String result;
        try {
            // get a hash key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(
                    secretAccessKey.getBytes(), HASH_ALGORITHM);

            // get a hasher instance and initialize with the signing key
            Mac mac = Mac.getInstance(HASH_ALGORITHM);
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());

            // base64-encode the hmac
            // result = Encoding.EncodeBase64(rawHmac);
            // result = new BASE64Encoder().encode(rawHmac);
            result = DatatypeConverter.printBase64Binary(rawHmac);


        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : "
                    + e.getMessage());
        }
        return result;
    }


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

	public static HashMap<String, InfosAlexa> deserializePrecedent() throws Exception {
		
		HashMap<String, InfosAlexa> hmap = new HashMap<String, InfosAlexa>();

		File yourFile = new File(precedentData);
		if(yourFile.exists()) {

			try
			{
				FileInputStream fis = new FileInputStream(precedentData);
				ObjectInputStream ois = new ObjectInputStream(fis);
				hmap = (HashMap) ois.readObject();
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



	public static HashMap<String, String> readCsv(String nameOfCsvFile){

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		HashMap<String, String> hmap = new HashMap<String, String>();

		try {
			br = new BufferedReader(new FileReader(nameOfCsvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
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

	public static void writeCsv(HashMap<String, InfosAlexa> myHashMap, String fileName){
		String eol = System.getProperty("line.separator");

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
						writer.append(countryName[j]).append(": ");
						writer.append(""+countryPercentage[j]).append("% rank: ");
						writer.append(""+countryRank[j]).append(',');
					}
				}
				writer.append(eol);

			}
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}

	}

	private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }



	
	public static HashMap<String, InfosAlexa> getInfosFromAlexa(HashMap<String, String> entryHMap, 
		HashMap<String, InfosAlexa> completeHMap, String accessKey, String secretKey) throws Exception {

		HashMap<String, InfosAlexa> result = new HashMap<String, InfosAlexa>();
		result.putAll(completeHMap);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder docBuilder= dbf.newDocumentBuilder();


		for (String name : entryHMap.keySet()) {

			if (!completeHMap.containsKey(name)) {

				UrlInfo urlInfo = new UrlInfo(accessKey, secretKey, entryHMap.get(name));
				String query = urlInfo.buildQuery();
				String toSign = "GET\n" + SERVICE_HOST + "\n/\n" + query;
				String signature = urlInfo.generateSignature(toSign);
				String uri = AWS_BASE_URL + query + "&Signature=" +	URLEncoder.encode(signature, "UTF-8");

				InfosAlexa infos = new InfosAlexa();
				URL url = new URL(uri);
				HashMap<String, Integer> rankInCountry = new HashMap<String, Integer>();
				HashMap<String, Float> percentUserCountry = new HashMap<String, Float>();
				URLConnection conn = url.openConnection();
				InputStream in = conn.getInputStream();
				Document responseDoc = dbf.newDocumentBuilder().parse(in);
				Element ranknode = (Element) responseDoc.getElementsByTagNameNS("*", "Rank").item(0);
				infos.setRank(Integer.parseInt(ranknode.getFirstChild().getNodeValue()));
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
    protected String buildQuery()
            throws UnsupportedEncodingException {
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

/*
    public static void main(String[] args) throws Exception {

        HashMap<String, InfosAlexa> hmInitial = deserializePrecedent();
        String nameOfCsvFile = "data/testCsv.csv";
        String csvOutput = "data/somefile.csv";
        HashMap<String, String> hmEntering = readCsv(nameOfCsvFile);
		HashMap<String, InfosAlexa> hmComplete = getInfosFromAlexa(hmEntering, hmInitial);

		writeCsv(hmComplete, csvOutput);
		serializePrecedent(hmComplete);

    }
*/

}
