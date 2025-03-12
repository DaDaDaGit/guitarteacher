package it.unipi.guitarteacher.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpConnector {
    
    private static final Logger logger = LogManager.getLogger(HttpConnector.class.getName());
    private static final String BASE_URL = "http://localhost:8080";
    
    public static HttpResponse getRequest(String uri, String urlParameters) throws IOException {
        urlParameters = urlParameters.replaceAll(" ", "%20");
        String url = BASE_URL + uri + "?" + urlParameters;
        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
        httpClient.setRequestMethod("GET");    

        //lettura risposta
        int responseCode = httpClient.getResponseCode();
        String responseBody = input(httpClient, responseCode);

        printConnection("GET", url, "", responseCode);
        return new HttpResponse(responseCode,responseBody);
    }
    
    //richiesta al server di eliminare una canzone
    public static HttpResponse deleteRequest(String uri, String urlParameters) throws IOException {
        urlParameters = urlParameters.replaceAll(" ", "%20");
        String url = BASE_URL + uri + "?" + urlParameters;
        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
        httpClient.setRequestMethod("DELETE"); 
        
        //lettura risposta
        int responseCode = httpClient.getResponseCode();
        String responseBody = input(httpClient, responseCode);
        
        printConnection("DELETE", url, "", responseCode);
        return new HttpResponse(responseCode, responseBody);
    }
    
     
    //leggo il body della risposta e lo restituisco in stringa
    private static String input(HttpURLConnection httpClient, int responseCode) {

        StringBuilder response = new StringBuilder();

        try(InputStream inputStream = 
                (200 <= responseCode && responseCode <= 299) ? httpClient.getInputStream() : httpClient.getErrorStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));) 
        {
            String currentLine;
            while ((currentLine = in.readLine()) != null) 
                response.append(currentLine);
        }
        catch (IOException ioe) {
            logger.error(ioe);
        }
        return response.toString();
    }
    
     
    private static void printConnection(String type, String url, String urlParameters, int responseCode) {
        logger.debug("\nSending " + type + " request to URL : " + url + 
                    "\nparameters : " + urlParameters +
                    "\nResponse Code : " + responseCode + "\n");
    }
}
