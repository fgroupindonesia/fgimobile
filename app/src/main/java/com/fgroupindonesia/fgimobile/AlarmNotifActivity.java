package com.fgroupindonesia.fgimobile;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.UIHelper;
import com.fgroupindonesia.helper.shared.KeyAudio;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;

public class AlarmNotifActivity extends Activity {

    MediaPlayer mp;
    String message;
    TextView textViewAlarmMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notif);

        textViewAlarmMessage = (TextView) findViewById(R.id.textViewAlarmMessage);

        message = getIntent().getStringExtra(KeyPref.SCHEDULE);
        textViewAlarmMessage.setText("Kelas anda : " + UIHelper.toIndonesian(message));

        playSound(UserData.getPreferenceInt(KeyPref.NOTIF_KELAS_AUDIO));


    }

    public void stopAlarm(View v) {

        if(mp!=null){
            mp.stop();
            mp = null;
        }

        finish();

    }

    @Override
    public void onBackPressed() {
            stopAlarm(null);
    }

        private void playSound(int ops) {

        AudioManager audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);

        if (mp != null) {
            mp.stop();
            mp = null;
        }

        if (ops == KeyAudio.ALARM_01) {
            mp = MediaPlayer.create(this, R.raw.alarm_01);
        } else if (ops == KeyAudio.ALARM_02) {
            mp = MediaPlayer.create(this, R.raw.alarm_02);
        } else if (ops == KeyAudio.ALARM_03) {
            mp = MediaPlayer.create(this, R.raw.alarm_03);
        }

        // for non silent mode we play the audio
        if(ops != KeyAudio.ALARM_04){
            mp.setLooping(true);
            mp.start();
        }

    }
}