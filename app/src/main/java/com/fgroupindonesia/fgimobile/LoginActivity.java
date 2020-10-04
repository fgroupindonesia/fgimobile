package com.fgroupindonesia.fgimobile;

import com.fgroupindonesia.beans.StatusLesson;
import com.fgroupindonesia.beans.StatusVoucher;
import com.fgroupindonesia.helper.AudioPlayer;
import com.fgroupindonesia.helper.OptionConfiguration;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.UIHelper;
import com.fgroupindonesia.helper.URLReference;
import com.fgroupindonesia.helper.UserData;
import com.fgroupindonesia.helper.WebCall;
import com.fgroupindonesia.helper.WebRequest;
import com.google.gson.Gson;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

public class LoginActivity extends Activity implements WebCall  {

	EditText textfieldUsername, textfieldPass;
	WebRequest httpCall;

	boolean permitted=false;

	TextView textRegisterUser;
	String URL_FGROUP_REGISTER = "http://fgroupindonesia.com/pendaftaran";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // preparing the httpcall
        httpCall = new WebRequest(this,this);

        textfieldUsername = (EditText) findViewById(R.id.editTextUsername);
        textfieldPass = (EditText) findViewById(R.id.editTextPassword);

        requestPermission();

    }



    
    
    
    @Override
    public void onPause(){
    	super.onPause();
    	
    }

    public void registerUser(View v){
		Intent browserIntent = new Intent(Intent.ACTION_VIEW);
		browserIntent.setData(Uri.parse(URLReference.RegistrationPage));
		startActivity(browserIntent);
	}

    public void verifyUser(View view){

				periksaFormulir();

	}

	@Override
    public void nextActivity(){
    	
    	 Intent intent = new Intent(this, HomeActivity.class);
         finish();
    	 startActivity(intent);

    }

    @Override
    public void onSuccess(String respond) {
        ShowDialog.message(this, respond);
    }

    private void periksaFormulir(){
   	if(!UIHelper.empty(textfieldUsername) && !UIHelper.empty(textfieldPass)){

   		// the web request executed by httcall

        httpCall.addData("username", UIHelper.getText(textfieldUsername));
        httpCall.addData("password", UIHelper.getText(textfieldPass));
        httpCall.setWaitState(true);
        httpCall.execute();

    	}else{
    		ShowDialog.message(this, "Isi username dan password dengan benar!");
    	}
    }
    


    private void requestPermission(){
		
    	try {
    		
    	
    	String[] PERMISSIONS_HISTORY = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
    			Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_WIFI_STATE,
				Manifest.permission.ACCESS_NETWORK_STATE};
    	
		ActivityCompat.requestPermissions(LoginActivity.this,
				PERMISSIONS_HISTORY,
                100);
		
			permitted=true;
		} catch (Exception e) {
			ShowDialog.message(LoginActivity.this, "Permission error!");
		}
		
	}
    
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        
        }
        
       
       
        
        return super.onOptionsItemSelected(item);
    }
    

    
}
