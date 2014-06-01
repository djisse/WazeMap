package com.example.waysmap;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity implements OnClickListener{
	
	private EditText pseudo,mdp;
	private Button valider;
	private DataDriver data;
	@Override
    protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        pseudo=(EditText)this.findViewById(R.id.pseudo);
        mdp=(EditText)this.findViewById(R.id.mdp);
        
        valider=(Button)this.findViewById(R.id.valider);
        valider.setOnClickListener(this);
        
        
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.valider:
			Toast.makeText(getApplicationContext(), "connexion...", Toast.LENGTH_LONG).show();
			connexion();
			break;
		}
	}
	
	private void connexion(){
		Intent intent=new Intent();  
        
          
        data=new DataDriver("http://aoms.fr/getConnexion.php?");
        String []tTag=new String[2];
        tTag[0]="pseudo";
        tTag[1]=pseudo.getText().toString();
        
        String []tTag2=new String[2];
        tTag2[0]="mdp";
        tTag2[1]=mdp.getText().toString();
        
        ArrayList<String []> liste=new ArrayList<String []>();
        liste.add(tTag);
        liste.add(tTag2);
        
        data.getRequest2(liste);
        while(data.dataExtracted==false){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ArrayList<Membre> listeMembre=data.getMembres();
		if(listeMembre.size()==1){
			
			intent.putExtra("id",listeMembre.get(0).getId());  
	        intent.putExtra("type",listeMembre.get(0).getType());  
	        intent.putExtra("pseudo",listeMembre.get(0).getPseudo());  
	        
	        
		}
		else{
			intent.putExtra("id",-1);  
	        intent.putExtra("type","");  
	        intent.putExtra("pseudo","");
		}
		setResult(1,intent);  
        finish();
	}
	
	
}
