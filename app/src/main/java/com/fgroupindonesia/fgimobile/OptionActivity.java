package com.fgroupindonesia.fgimobile;

import com.fgroupindonesia.helper.Navigator;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.shared.HistoryCall;
import com.fgroupindonesia.helper.shared.KeyAudio;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;
import com.fgroupindonesia.helper.WebRequest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import android.content.Context;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OptionActivity extends Activity implements Navigator {

    MediaPlayer mp;

    WebRequest cobaPassing = new WebRequest(WebRequest.POST_METHOD);
    String passBaru = null;

    final int OPTION_MODE_PASS = 1, OPTION_MODE_CONFIG = 2;
    CheckBox checkboxNotifPayment, checkboxNotifKelas;

    SharedPreferences sharedpreferences;
    String aToken, audioChosen;

    int ops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        // this is for shared pref part
        UserData.setPreference(this);

        checkboxNotifPayment = (CheckBox) findViewById(R.id.checkboxNotifPayment);
        checkboxNotifKelas = (CheckBox) findViewById(R.id.checkboxNotifKelas);

		//this will ensure the data remain same until user close the app
		reloadUserOptionData();

        aToken = UserData.getPreferenceString(KeyPref.TOKEN);
        // for History API call
        HistoryCall.setReference(this, this, aToken);

    }

    private void reloadUserOptionData(){
        checkboxNotifKelas.setChecked(UserData.getPreferenceBoolean(KeyPref.NOTIF_KELAS));
        checkboxNotifPayment.setChecked(UserData.getPreferenceBoolean(KeyPref.NOTIF_PAYMENT));
	}



    private void playAudioSample(int fileOrder){

        AudioManager audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);

        if(mp!=null){
            mp.stop();
            mp = null;
        }

        if (fileOrder == KeyAudio.ALARM_01) {
            mp = MediaPlayer.create(this, R.raw.alarm_01);
        }else if (fileOrder == KeyAudio.ALARM_02) {
            mp = MediaPlayer.create(this, R.raw.alarm_02);
        } else if (fileOrder == KeyAudio.ALARM_03) {
            mp = MediaPlayer.create(this, R.raw.alarm_03);
        }

        mp.start();

    }

    private void setAudioChosen(int orderAlarm){
        switch (orderAlarm){
            case 0:
                audioChosen = "alarm_01.wav";
                break;
            case 1:
                audioChosen = "alarm_02.wav";
                break;
            case 2:
                audioChosen = "alarm_03.wav";
                break;
        }

    }

    public void activateNotifPayment(View v) {

		UserData.savePreference(KeyPref.NOTIF_PAYMENT, checkboxNotifPayment.isChecked());

		if(checkboxNotifPayment.isChecked()){

		    // show popup first
            ops = 2;
            showPopupChooseSound();

		    //startAlarm(KeyAudio.ALARM_01);
        }


    }

    private void showPopupChooseSound(){

        // ops 1 for KELAS
        // ops 2 for PAYMENT

        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Audio");
        builder.setSingleChoiceItems(R.array.alarm_audio,-1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playAudioSample(which);
                setAudioChosen(which);

            }
        });

        /*builder.setItems(R.array.alarm_audio, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playAudioSample(which);
                setAudioChosen(which);
            }
        });*/

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
                // save it to the sharedPreference
                if(ops==1){
                    UserData.savePreference(KeyPref.NOTIF_KELAS_AUDIO, audioChosen);
                }else if(ops==2){
                    UserData.savePreference(KeyPref.NOTIF_PAYMENT_AUDIO, audioChosen);
                }

            }
        });

        //builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void activateNotifKelas(View v) {

        UserData.savePreference(KeyPref.NOTIF_KELAS, checkboxNotifKelas.isChecked());

        if(checkboxNotifKelas.isChecked()){

            // show popup first
            ops = 1;
            showPopupChooseSound();

            //startAlarm(KeyAudio.ALARM_01);
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

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void nextActivity() {

    }

    @Override
    public void onSuccess(String urlTarget, String result) {

    }
}
