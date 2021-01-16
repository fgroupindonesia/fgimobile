package com.fgroupindonesia.fgimobile;

import com.fgroupindonesia.helper.AudioPlayer;
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


    CheckBox checkboxNotifPayment, checkboxNotifKelas;

    String aToken;
    int audioChosen;

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

        if (fileOrder == KeyAudio.ALARM_01) {
            audioChosen = 0;
            AudioPlayer.play(this,AudioPlayer.ALARM_01);
        }else if (fileOrder == KeyAudio.ALARM_02) {
            audioChosen = 1;
            AudioPlayer.play(this,AudioPlayer.ALARM_02);
        } else if (fileOrder == KeyAudio.ALARM_03) {
            audioChosen = 2;
            AudioPlayer.play(this,AudioPlayer.ALARM_03);
        }else if(fileOrder==KeyAudio.ALARM_04){
            // empty none
            audioChosen = 3;
        }

    }



    public void activateNotifPayment(View v) {

		UserData.savePreference(KeyPref.NOTIF_PAYMENT, checkboxNotifPayment.isChecked());

    }

    private void showPopupChooseSound(){


        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Audio");
        builder.setSingleChoiceItems(R.array.alarm_audio,-1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playAudioSample(which);

            }
        });

        /*builder.setItems(R.array.alarm_audio, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playAudioSample(which);

            }
        });*/

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
                // save it to the sharedPreference

                    UserData.savePreference(KeyPref.NOTIF_KELAS_AUDIO, audioChosen);


            }
        });

        //builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void activateNotifKelas(View v) {

        UserData.savePreference(KeyPref.NOTIF_KELAS, checkboxNotifKelas.isChecked());

        if(checkboxNotifKelas.isChecked()){

            showPopupChooseSound();

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
