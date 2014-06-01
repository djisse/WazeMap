package com.example.waysmap;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HTTPTools {
    /** The time it takes for our client to timeout */
    public static final int HTTP_TIMEOUT = 30 * 1000; // milliseconds

    /** Single instance of our HttpClient */
    private static HttpClient mHttpClient;

    /**
     * Get our single instance of our HttpClient object.
     *
     * @return an HttpClient object with connection parameters set
     */
    private static HttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
            final HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
            ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
        }
        return mHttpClient;
    }

    /**
     * Performs an HTTP Post request to the specified url with the
     * specified parameters.
     *
     * @param url The web address to post the request to
     * @param postParameters The parameters to send via the request
     * @return The result of the request
     * @throws Exception
     */
    public static String executeHttpPost(String url, ArrayList<NameValuePair> postParameters) throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = getHttpClient();
            HttpPost request = new HttpPost(url);
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            String result = sb.toString();
            return result;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Performs an HTTP GET request to the specified url.
     *
     * @param url The web address to post the request to
     * @return The result of the request
     * @throws Exception
     */
    public static String executeHttpGet(String url) throws Exception {
    	
    	
        BufferedReader in = null;
        try {
            HttpClient client = getHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            String result = sb.toString();
            return result;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new Exception("Erreur lors de la recuperation de la requete");
                }
            }
        }
    }
    
    public static ArrayList<Point> parseJSON(String result) throws Exception{
    	String returnString="";
    	// Parse les données JSON
    	ArrayList<Point> liste=new ArrayList<Point>();
    			try{
    				JSONArray jArray = new JSONArray(result);
    				for(int i=0;i<jArray.length();i++){
    					JSONObject json_data = jArray.getJSONObject(i);
    					Point current=new Point(json_data.getInt("id"),
    							json_data.getDouble("longitude"),
    							json_data.getDouble("latitude"),
    							json_data.getString("tag"));
    							liste.add(current);
    					// Résultats de la requête
    					returnString += "\n\t" + jArray.getJSONObject(i); 
    				}
    			}catch(JSONException e){
    				throw new Exception("Erreur lors du parsage JSON");
    			}
    			return liste; 
    }
    
    public static ArrayList<Membre> parseJSONMembre(String result) throws Exception{
    	String returnString="";
    	// Parse les données JSON
    	ArrayList<Membre> liste=new ArrayList<Membre>();
    			try{
    				JSONArray jArray = new JSONArray(result);
    				for(int i=0;i<jArray.length();i++){
    					JSONObject json_data = jArray.getJSONObject(i);
    					Membre current=new Membre(json_data.getInt("id"),
    							json_data.getString("pseudo"),
    							json_data.getString("type"),
    							json_data.getString("mdp"));
    							liste.add(current);
    					// Résultats de la requête
    					returnString += "\n\t" + jArray.getJSONObject(i); 
    				}
    			}catch(JSONException e){
    				throw new Exception("Erreur lors du parsage JSON");
    			}
    			return liste; 
    }
}