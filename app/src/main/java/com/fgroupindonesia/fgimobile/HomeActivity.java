package com.fgroupindonesia.fgimobile;

import com.fgroupindonesia.helper.NavigatorHelper;
import com.fgroupindonesia.helper.NotificationTray;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {

	final int ACT_CHECK_STATUS=2, ACT_OPTIONS=3, ACT_KONSULTASI=4, ACT_DATA_ABSEN=5,
			ACT_BUY_VOUCHER=6, ACT_SEND_REQUEST=7;
	

	Intent intentTukBroadcastReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_home);
	        
	        Button tombolKirimReq = (Button) findViewById(R.id.buttonRequest);
	        Button tombolBeliVoucher = (Button) findViewById(R.id.buttonBuyVoucher);
	        Button tombolDataAbsensi = (Button) findViewById(R.id.buttonDataAbsensi);
	        Button tombolMulaiKonsul = (Button) findViewById(R.id.buttonStartConsultation);
	        Button tombolOptions = (Button) findViewById(R.id.buttonOptions);
	        Button tombolCheckStat = (Button) findViewById(R.id.buttonCheckStatus);
	        
	        tombolCheckStat.setOnClickListener(new View.OnClickListener() {
	           
	            public void onClick(View v) {
	               
	            nextActivity(ACT_CHECK_STATUS);
	            	
	            }
	         });
	        
	        tombolOptions.setOnClickListener(new View.OnClickListener() {
	           
	            public void onClick(View v) {
	               
	            nextActivity(ACT_OPTIONS);
	            	
	            }
	         });
	        
	        tombolMulaiKonsul.setOnClickListener(new View.OnClickListener() {
	           
	            public void onClick(View v) {
	               
	            nextActivity(ACT_KONSULTASI);
	            	
	            }
	         });
	        
	        tombolDataAbsensi.setOnClickListener(new View.OnClickListener() {
	           
	            public void onClick(View v) {
	               
	            nextActivity(ACT_DATA_ABSEN);
	            	
	            }
	         });
	        
	        tombolBeliVoucher.setOnClickListener(new View.OnClickListener() {
	           
	            public void onClick(View v) {
	               
	            nextActivity(ACT_BUY_VOUCHER);
	            	
	            }
	         });
	        
	        tombolKirimReq.setOnClickListener(new View.OnClickListener() {
	         
	            public void onClick(View v) {
	               
	            nextActivity(ACT_SEND_REQUEST);
	            	
	            }
	         });
	        
	        registerService();
	        
	}
	
	private void nextActivity(int jenisActivity){
	    	
    	 Intent intent = null;
    	 
    	 if(jenisActivity == ACT_BUY_VOUCHER){
    		 intent = new Intent(this, BuyVoucherActivity.class);
    	 } else if(jenisActivity == ACT_CHECK_STATUS){
    		 intent = new Intent(this, CheckStatusActivity.class);
    	 } else if(jenisActivity == ACT_KONSULTASI){
    		 intent = new Intent(this, ConsultationActivity.class);
    		 intent.putExtra("comingFrom", NavigatorHelper.ACT_HOME);
    	 } else if(jenisActivity == ACT_DATA_ABSEN){
    		 intent = new Intent(this, DataAbsensiActivity.class);
    	 }else if(jenisActivity == ACT_OPTIONS){
    		 intent = new Intent(this, OptionActivity.class);
    	 } else if(jenisActivity == ACT_SEND_REQUEST){
    		 intent = new Intent(this, RequestActivity.class);
    	 }
    	 
         startActivity(intent);
    	
    }
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	NotificationTray.AppContext=context;
        	
        	String title = intent.getStringExtra("judul");
        	String pesan = intent.getStringExtra("sesuatu");
        	
        	NotificationTray.showMessage(title, pesan);
        }
    };
    
    private void registerService(){
    	intentTukBroadcastReceiver = new Intent(this, BroadcastServiceChecking.class);
    	startService(intentTukBroadcastReceiver);
		registerReceiver(broadcastReceiver, new IntentFilter(BroadcastServiceChecking.BROADCAST_ACTION_CHECKING));
    }
    
    private void unregisterService(){
    	unregisterReceiver(broadcastReceiver);
		stopService(intentTukBroadcastReceiver); 
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	registerService();
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	NotificationTray.clearAllMessages();
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	unregisterService();
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
    
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
 
                        //super.onBackPressed();
                        //Or used finish();
						ActivityCompat.finishAffinity(HomeActivity.this);
                    }
 
                })
                .setNegativeButton("No", null)
                .show();
 
    }
	
}
