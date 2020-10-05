package com.fgroupindonesia.fgimobile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import com.fgroupindonesia.helper.AudioPlayer;
import com.fgroupindonesia.helper.OptionConfiguration;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.UIHelper;
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

public class OptionActivity extends Activity {

    Context contextNya;
    WebRequest cobaPassing = new WebRequest(WebRequest.POST_METHOD);
    String passBaru = null;

    final int OPTION_MODE_PASS = 1, OPTION_MODE_CONFIG = 2;
    CheckBox opsiRememberLogin, opsiNotifPembayaran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        contextNya = getApplicationContext();

        opsiRememberLogin = (CheckBox) findViewById(R.id.checkboxRememberLogin);
        opsiNotifPembayaran = (CheckBox) findViewById(R.id.checkboxPaymentNotification);

		//this will ensure the data remain same until user close the app
		reloadUserOptionData();

    }

    private void reloadUserOptionData(){
		opsiRememberLogin.setChecked(UserData.RememberLogin);
		opsiNotifPembayaran.setChecked(UserData.NotifLimitPayment);
	}


    public void activateNotifikasi(View v) {
		UserData.NotifLimitPayment = opsiNotifPembayaran.isChecked();
    }

    public void activateRemember(View v) {
        UserData.RememberLogin = opsiRememberLogin.isChecked();
    }

    public void changePass(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(contextNya);

		final EditText et = new EditText(contextNya);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(et);

		// set dialog message
		alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
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


}
