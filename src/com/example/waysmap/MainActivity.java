package com.example.waysmap;


import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends Activity implements LocationListener {
    private LocationManager locationManager;
    private GoogleMap gMap;
    private DataDriver data;
    private Button bouton,connexion;
    private Location curLocation=null;
    
    private class ChargerMarqueur extends Thread{
    	public void run(){
    		try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		while(data.dataExtracted==false){
    			try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		
    		ArrayList<Point> listePoint=data.getPoints();
    		
    		//for(Point cur:listePoint){
    		for(int i=0;i<listePoint.size();i++){
    			Point cur=listePoint.get(i);
    			runOnUiThread(new DessinerPoint(cur));
    			
    		}
    		
    		
    		
    	}
    }
    
    private class FindMyPosition extends Thread{
    	public void run(){
    		MyFirstLocation();
    		while(curLocation==null){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		runOnUiThread(new Runnable(){
    			public void run(){
    				CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()))      // Sets the center of the map to Mountain View
                    .zoom(20)                   // Sets the zoom
                    .bearing(80)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
    				gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    				//gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), 19)); 
    			getProche();
    			}
    		});
    		
    	}
    }
    
    private class DessinerPoint implements Runnable{
    	private Point point;
    	public DessinerPoint(Point p){
    		point=p;
    	}
    	public void run(){
    		
    		dessinerMarqueur(point.longitude,point.latitude,point.id+":"+point.tag);
    		
    	}
    }
    private void dessinerMarqueur(double longi,double lat,String tag){
    	Point p=new Point(0, longi, lat, tag);
    	data.addPoint(p);
    	gMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi)).alpha(0.5f).snippet("icit").title(tag).icon(BitmapDescriptorFactory.fromResource(R.drawable.sdf)));
    }
    
    public void MyFirstLocation(){
    	LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabledGPS = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean enabledWiFi = service
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        if (!enabledGPS) {
            notifyView("Erreur :GPS non activé. ");
            
        }
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        curLocation = locationManager.getLastKnownLocation(provider);
        
        if(curLocation!=null){
            onLocationChanged(curLocation);
        }
        
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Demarrage de l'activity");
        gMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        data=new DataDriver("http://aoms.fr/setCoords.php");
        gMap.setBuildingsEnabled(true);
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gMap.setMyLocationEnabled(true);
        bouton=(Button)this.findViewById(R.id.bouton);
        bouton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				if(curLocation== null)
					notifyView("Localisation en cours veuillez patienter"); 
				else
					savecurLocation(curLocation);
			}
        	
        });
        
        connexion=(Button)this.findViewById(R.id.connexion);
        connexion.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				connect();
			}
        	
        });
        gMap.setOnMapLongClickListener(new OnMapLongClickListener(){

			@Override
			public void onMapLongClick(LatLng arg0) {
				// TODO Auto-generated method stub
				if(curLocation== null)
					notifyView("Localisation en cours veuillez patienter"); 
				else{
					curLocation.setLatitude(arg0.latitude);
					curLocation.setLongitude(arg0.longitude);
					savecurLocation(curLocation);
					if(curLocation!=null)
		    			getProche();
				}
					
				
			}
        	
        });
        String []tTag=new String[2];
        tTag[0]="param";
        tTag[1]="all";
        
        ArrayList<String []> liste=new ArrayList<String []>();
        liste.add(tTag);
        System.out.println("Recuperation de la liste des points...");
        data.getRequest2(liste);
        System.out.println("CHargement des marqueurs...");
        ChargerMarqueur chargeur=new ChargerMarqueur();
        chargeur.start();
        System.out.println("Recherche de la poisition actuelle...");
        FindMyPosition f=new FindMyPosition();
        f.start();
        
    }
    
    public void notifyView(String txt){
    	Toast.makeText(this, txt, Toast.LENGTH_SHORT).show(); 
    }
    @Override
    public void onResume() {
        super.onResume();
 
        //Obtention de la référence du service
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
 
        //Si le GPS est disponible, on s'y abonne
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            abonnementGPS();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
 
        //On appelle la méthode pour se désabonner
        desabonnementGPS();
    }
 
    /**
     * Méthode permettant de s'abonner à la localisation par GPS.
     */
    public void abonnementGPS() {
        //On s'abonne
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
    }
 
    /**
     * Méthode permettant de se désabonner de la localisation par GPS.
     */
    public void desabonnementGPS() {
        //Si le GPS est disponible, on s'y abonne
        locationManager.removeUpdates(this);
    }
 
  
    public void onLocationChanged(final Location location) {
    	curLocation=location;
    	
    	        
    }
    
    public void getProche(){
    	System.out.println("Placage des sdf...");
    	int i=0;
    	for(Point cur: data.getPoints()){
	    	Location l=new Location("test");
			l.setLatitude(cur.latitude);
			l.setLongitude(cur.longitude);
			float distance=curLocation.distanceTo(l);
			if(distance<=1000)
				i++;
			
    	}
    	notifyView("Il y a "+i+" sdf proche de vous");
    }
    
    public void savecurLocation(Location location){
       	//On affiche dans un Toat la nouvelle Localisation
    	System.out.println("Enregistrement du point en cours...");
        final StringBuilder msg = new StringBuilder("lat : ");
        double longitude=location.getLongitude();
        double latitude=location.getLatitude();
        

        msg.append(longitude);
        msg.append( "; lng : ");
        msg.append(longitude);
        
        
        String []tLong=new String[2];
        tLong[0]="long";
        tLong[1]=""+longitude;
        
        String []tLat=new String[2];
        tLat[0]="lat";
        tLat[1]=""+latitude;
        
        String []tTag=new String[2];
        tTag[0]="tag";
        tTag[1]="test";
        
        ArrayList<String []> liste=new ArrayList<String []>();
        liste.add(tLong);
        liste.add(tLat);
        liste.add(tTag);
        System.out.println("liste terminé");
        data.setUrl("http://aoms.fr/setCoords.php");
        data.sendRequest2(liste);
        //data.sendRequete(liste);
        
        this.dessinerMarqueur(longitude, latitude, "sdf");
        //notifyView(msg.toString());
    }
 
    @Override
    public void onProviderDisabled(final String provider) {
        //Si le GPS est désactivé on se désabonne
        if("gps".equals(provider)) {
            desabonnementGPS();
        }      
    }
 
    @Override
    public void onProviderEnabled(final String provider) {
        //Si le GPS est activé on s'abonne
        if("gps".equals(provider)) {
            abonnementGPS();
        }
    }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	private void showNavigationMode(){
		
	}
	
	private void connect(){
		Intent intent=new Intent(MainActivity.this,Register.class);  
        startActivityForResult(intent, 1);
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
    {  
              super.onActivityResult(requestCode, resultCode, data);  
                  
               // check if the request code is same as what is passed  here it is 2  
                if(requestCode==1)  
                      {  
                         String pseudo=data.getStringExtra("pseudo");   
                         String type=data.getStringExtra("type");
                         int idMembre=data.getIntExtra("id", -1);
                         Toast.makeText(getApplicationContext(), "Bonjour "+pseudo+" :"+idMembre+":"+type, Toast.LENGTH_LONG).show();
              
                      }  
  
  }  
 
   }


