package com.example.waysmap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class DataDriver {
	private String adresse;
	private ArrayList<Point> points;
	private ArrayList<Membre> membres;
	public ArrayList<Membre> getMembres() {
		return membres;
	}

	public void setMembres(ArrayList<Membre> membres) {
		this.membres = membres;
	}

	public boolean dataExtracted;
	
	
	public DataDriver(String url){
		this.adresse=url;
		points=new ArrayList<Point>();
		dataExtracted=false;
	}
	
	public void addPoint(Point p){
		points.add(p);
	}

	public void sendRequest2(ArrayList<String[]> parametres){
		ConnectUrlTask task=new ConnectUrlTask();
		task.execute(parametres);
		

	}
	
	public void getRequest2(ArrayList<String[]> parametres){
		GetUrlTask task=new GetUrlTask();
		task.execute(parametres);
		
	}

	public String getUrl() {
		return adresse;
	}

	public ArrayList<Point> getPoints(){
		return points;
	}


	public void setUrl(String url) {
		this.adresse = url;
	}
	
	private class ConnectUrlTask extends AsyncTask<ArrayList<String[]>, Void, Void> {

	    private Exception exception;

	    protected Void doInBackground(ArrayList<String[]> ...parametres) {
	    	String donnees="http://aoms.fr/setCoords.php?";
	    	try{
			  // Encodage des paramètres de la requête
				for(int i=0;i<parametres[0].size();i++)
					if(i==0)
						donnees+=URLEncoder.encode(parametres[0].get(i)[0], "UTF-8")+ "="+URLEncoder.encode(parametres[0].get(i)[1], "UTF-8");
					else
						donnees+="&"+URLEncoder.encode(parametres[0].get(i)[0], "UTF-8")+ "="+URLEncoder.encode(parametres[0].get(i)[1], "UTF-8");
	    	String ress=HTTPTools.executeHttpGet(donnees);
	    	System.out.println(ress);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	        return null;
	    }

	}
	
	private class GetUrlTask extends AsyncTask<ArrayList<String[]>, Void, Void> {

	    private Exception exception;

	    protected Void doInBackground(ArrayList<String[]> ...parametres) {
	    	String donnees="http://aoms.fr/getCoords.php?";
	    	try{
			  // Encodage des paramètres de la requête
				for(int i=0;i<parametres[0].size();i++)
					if(i==0)
						donnees+=URLEncoder.encode(parametres[0].get(i)[0], "UTF-8")+ "="+URLEncoder.encode(parametres[0].get(i)[1], "UTF-8");
					else
						donnees+="&"+URLEncoder.encode(parametres[0].get(i)[0], "UTF-8")+ "="+URLEncoder.encode(parametres[0].get(i)[1], "UTF-8");
	    	String ress=HTTPTools.executeHttpGet(donnees);
	    	//System.out.println(ress);
	    	points=HTTPTools.parseJSON(ress);
	    	dataExtracted=true;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	        return null;
	    }

	}
	
	private class GetConnexionTask extends AsyncTask<ArrayList<String[]>, Void, Void> {

	    private Exception exception;

	    protected Void doInBackground(ArrayList<String[]> ...parametres) {
	    	String donnees=adresse;
	    	try{
			  // Encodage des paramètres de la requête
				for(int i=0;i<parametres[0].size();i++)
					if(i==0)
						donnees+=URLEncoder.encode(parametres[0].get(i)[0], "UTF-8")+ "="+URLEncoder.encode(parametres[0].get(i)[1], "UTF-8");
					else
						donnees+="&"+URLEncoder.encode(parametres[0].get(i)[0], "UTF-8")+ "="+URLEncoder.encode(parametres[0].get(i)[1], "UTF-8");
	    	String ress=HTTPTools.executeHttpGet(donnees);
	    	//System.out.println(ress);
	    	membres=HTTPTools.parseJSONMembre(ress);
	    	dataExtracted=true;
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	        return null;
	    }

	}
}
