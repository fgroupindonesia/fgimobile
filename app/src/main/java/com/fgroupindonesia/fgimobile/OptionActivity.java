package com.fgroupindonesia.fgimobile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import com.fgroupindonesia.helper.AudioPlayer;
import com.fgroupindonesia.helper.OptionConfiguration;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.UserData;
import com.fgroupindonesia.helper.WebRequest;

import android.Manifest;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
public class OptionActivity extends Activity{

	Context contextNya;
	WebRequest cobaPassing = new WebRequest(WebRequest.POST_METHOD);
	String passBaru=null;
	
	final int OPTION_MODE_PASS = 1, OPTION_MODE_CONFIG = 2;
	CheckBox opsiRememberLogin, opsiNotifPembayaran;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_option);
	        
	        contextNya=getApplicationContext();

	        Button tombolChangePass = (Button) findViewById(R.id.buttonChangePassword);
	        setOnClickListener(tombolChangePass);
	        
	        opsiRememberLogin= (CheckBox) findViewById(R.id.checkboxRememberLogin);
	        opsiNotifPembayaran = (CheckBox) findViewById(R.id.checkboxPaymentNotification);
	        
	
	        opsiNotifPembayaran.setOnClickListener(new View.OnClickListener() {

	            public void onClick(View v) {
	            	UserData.NotifLimitPayment = ((CheckBox)v).isChecked();
	            	changeOptions();
	            }
	        });
	        
	        opsiRememberLogin.setOnClickListener(new View.OnClickListener() {

	            public void onClick(View v) {
	            	UserData.RememberLogin= ((CheckBox)v).isChecked();
	            	changeOptions();
	            }
	        });
	        
	        //this will ensure the data remain same until user close the app
	        checkUserData();
	        
	}
	
	private void checkUserData(){
		opsiNotifPembayaran.setChecked(UserData.NotifLimitPayment);
		opsiRememberLogin.setChecked(UserData.RememberLogin);
	}
	
	
	
	private void setOnClickListener(Button komp){
    	

    	
    }
	
	public void changeMyPass(){
		OptionChanger cobaPanggil = new OptionChanger();
		cobaPanggil.setMode(OPTION_MODE_PASS);
		cobaPanggil.execute();
		
		changeOptions();
	}
	
	public void changeOptions(){

		// these 2 data is stored locally
		OptionChanger cobaPanggil = new OptionChanger();
	    cobaPanggil.setMode(OPTION_MODE_CONFIG);
	    cobaPanggil.execute();
		
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	

    private class OptionChanger extends AsyncTask<String, String, String> {

    	boolean keepTrying = true;
    	boolean statusPermission=false;
    	int modePilihan = -1;
    	String terbaca=null;
    	OptionConfiguration configurator = new OptionConfiguration();

    	public void setMode(int pilihanNya){
    		modePilihan = pilihanNya;
    	}
    	
    	public boolean isStatusPermissionSuccess(){
    		return statusPermission;
    	}
    	
    	public OptionChanger(){
    		configurator.setContext(contextNya);
    		configurator.setActivity(OptionActivity.this);
    	}
    	
    	private void requestPermission(){
    		
    		try {
    		String[] PERMISSION_ACCESS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
    				Manifest.permission.READ_EXTERNAL_STORAGE,
    				Manifest.permission.INTERNET};
    		
    			
    			ActivityCompat.requestPermissions(OptionActivity.this,
    					PERMISSION_ACCESS,
    	                200);

    			statusPermission=  true;
    			
    			} catch (Exception e) {
    				ShowDialog.message(OptionActivity.this, "Permission error!");
    			}
    			
    		
    		
    		}
    	
        @Override
        protected String doInBackground(String... params) {

        	while(keepTrying==true){
        		try {

                    if (ActivityCompat.checkSelfPermission(OptionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    	//publishProgress("Giving app permission!");
                    	requestPermission();
                	
                		Thread.sleep(3000);
                		// wait for 3 seconds before checking and asking once more time
                		
                    } else if(modePilihan==OPTION_MODE_PASS) {
                    	
                    	//publishProgress("Changing new password...");
                    	keepTrying=false;

                   		cobaPassing.addData("username", UserData.Username);
                   		cobaPassing.addData("passold", UserData.Passw);
                   		cobaPassing.addData("passnew", passBaru);
                   		cobaPassing.setTargetURL(WebRequest.URL_CHANGE_PASS);
                    	cobaPassing.execute();
                    	
                    }else if(modePilihan==OPTION_MODE_CONFIG){
                    	configurator.writeConfigFile();
                    	keepTrying=false;
                    	//publishProgress("Updating new config...");
                    	return "sukses-config";
                    }
                    
                } catch (Exception e) {
                	publishProgress("Ada kesalahan saat eksekusi AsyncTask!");
                }
        	}
        	
        	// last final message after all completed
            return "sukses";

           
        }

        @Override
        protected void onProgressUpdate(String... text) {
        	// munculkan pesannya di layar
           ShowDialog.shortMessage(OptionActivity.this, text[0]);
        }
        
        @Override
        protected void onPostExecute(String someEnd) {
        	
        	if(someEnd.equalsIgnoreCase("sukses")){
        		try{
        			// store the new pass
        			
        			UserData.Passw = passBaru;
        			
        			int jawaban = cobaPassing.getStatusCode();
        			// the data returned from server is a randomkey
                    if(jawaban!=WebRequest.SERVER_NO_RESULT){
                    	AudioPlayer.play(OptionActivity.this, AudioPlayer.VOICE_UPDATED);
                    	ShowDialog.shortMessage(OptionActivity.this,"Password updated!");
                    }else{
                    	ShowDialog.shortMessage(OptionActivity.this,"Nothing changed!");
                    }
        		} catch(Exception ex){
        			// when conversion data returned from server is invalid(error)
        			// means the data passing might be mistaken (from client)
        			ShowDialog.shortMessage(OptionActivity.this,"Error after data returned from Server!");
        			ShowDialog.shortMessage(OptionActivity.this,ex.getMessage());
        		}
                
        	}else if(someEnd.equalsIgnoreCase("sukses-config")){
        		//ShowDialog.shortMessage(OptionActivity.this,"Config updated!");
        		//AudioPlayer.play(OptionActivity.this, AudioPlayer.VOICE_UPDATED);
        		//ShowDialog.message(OptionActivity.this,"Terbaca: " + readConfigFile());
        		
        	} 
           
        }
    }
    
    
	
}
