package com.fgroupindonesia.fgimobile;

import com.fgroupindonesia.helper.ErrorLogger;
import com.fgroupindonesia.helper.RespondHelper;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.UIHelper;
import com.fgroupindonesia.helper.URLReference;
import com.fgroupindonesia.helper.Navigator;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;
import com.fgroupindonesia.helper.WebRequest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.ActivityCompat;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

public class LoginActivity extends Activity implements Navigator {

    // in miliseconds
    int PERIOD_OF_TIME = 2000;

    EditText textfieldUsername, textfieldPass;
    WebRequest httpCall;

    boolean permitted = false;

    TextView textRegisterUser;
    ProgressBar loadingProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // for shared preference usage
        UserData.setPreference(this);

        loadingProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        textfieldUsername = (EditText) findViewById(R.id.editTextUsername);
        textfieldPass = (EditText) findViewById(R.id.editTextPassword);

        requestPermission();

        if(UserData.getPreferenceString(KeyPref.USERNAME) != null){
            // if there is a valid login attempt
            // we forward this to home directly
            nextActivity();
        }


    }



    @Override
    public void onPause() {
        super.onPause();
    }

    public void registerUser(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse(URLReference.RegistrationPage));
        startActivity(browserIntent);
    }

    private void showLoading(boolean t) {
        if (t == false) {
            loadingProgressBar.setVisibility(View.GONE);
        } else {
            loadingProgressBar.setVisibility(View.VISIBLE);
        }


    }

    public void verifyUser(View view) {

        showLoading(true);
        periksaFormulir();

    }

    @Override
    public void nextActivity() {

        Intent intent = new Intent(this, HomeActivity.class);
        finish();
        startActivity(intent);

    }


    @Override
    public void onSuccess(String respond) {


        try {

            if (RespondHelper.isValidRespond(respond)) {

                JSONObject json = RespondHelper.getObject(respond, "multi_data");

               if( json != null){
                   // we got the token here
                   String tokenObtained = json.getString("token");

                   UserData.savePreference(KeyPref.TOKEN, tokenObtained);

                   //ShowDialog.message(LoginActivity.this, tokenObtained);
               }

                // save the username somewhere
                String username = UIHelper.getText(textfieldUsername);
                String passw = UIHelper.getText(textfieldPass);

                UserData.savePreference(KeyPref.USERNAME, username);
                UserData.savePreference(KeyPref.PASSWORD, passw);

                // move to the next activity after period of time
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                        nextActivity();


                    }
                }, PERIOD_OF_TIME);

            } else {
                ShowDialog.message(this, "Username or password are invalid, please try again!");
                showLoading(false);

                // this is invalid attempt
                // clearing the login cridential shared
                UserData.savePreference(KeyPref.USERNAME, null);
                UserData.savePreference(KeyPref.PASSWORD, null);


            }

        } catch (Exception err) {
            ErrorLogger.write(err);
            ShowDialog.message(this, "Error verification. Please contact administrator!");
            showLoading(false);
        }

    }

    private void periksaFormulir() {
        if (!UIHelper.empty(textfieldUsername) && !UIHelper.empty(textfieldPass)) {

            // the web request executed by httcall
            // preparing the httpcall
            httpCall = new WebRequest(this, this);
            httpCall.addData("username", UIHelper.getText(textfieldUsername));
            httpCall.addData("password", UIHelper.getText(textfieldPass));
            httpCall.setWaitState(true);
            httpCall.setRequestMethod(WebRequest.POST_METHOD);
            httpCall.setTargetURL(URLReference.UserLogin);
            httpCall.execute();

            //ShowDialog.message(this,"going to " + URLReference.UserLogin);

        } else {
            ShowDialog.message(this, "Isi username dan password dengan benar!");
        }
    }


    private void requestPermission() {

        try {


            String[] PERMISSIONS_HISTORY = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE};

            ActivityCompat.requestPermissions(LoginActivity.this,
                    PERMISSIONS_HISTORY,
                    100);

            permitted = true;
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
