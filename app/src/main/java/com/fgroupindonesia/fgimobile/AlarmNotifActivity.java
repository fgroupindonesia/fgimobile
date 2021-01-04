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
import com.fgroupindonesia.helper.shared.KeyAudio;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;

public class AlarmNotifActivity extends Activity {

    MediaPlayer mp;
    ImageView imageviewGIF;
    String fileAudio;
    int ops = -1;
    TextView textViewAlarmMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notif);

        textViewAlarmMessage = (TextView) findViewById(R.id.textViewAlarmMessage);

        fileAudio = UserData.getPreferenceString(KeyPref.NOTIF_KELAS_AUDIO);
        setSound(fileAudio);
        play();

        ShowDialog.message(this, "playing alarm audio " + ops);

        //imageviewGIF = (ImageView) findViewById(R.id.imageviewGIF);

        showGIF();

    }

    private void showGIF() {

        //Ion.with(imageviewGIF)
        //      .error(R.drawable.error)
        //      .animateGif(AnimateGifMode.ANIMATE)
        //      .load("file:///android_asset/alarm_animated.gif");

    }

    private int getSound() {
        return ops;
    }

    public void setSound(String fileName) {

        switch (fileName){
            case "alarm_01.wav":
                ops = 0;
                break;
            case "alarm_02.wav":
                ops = 1;
                break;
            case "alarm_03.wav":
                ops = 2;
                break;
        }

    }

    public void stopAlarm(View v) {
        finish();
    }

    private void play() {

        AudioManager audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);

        if (mp != null) {
            mp.stop();
            mp = null;
        }

        if (this.getSound() == KeyAudio.ALARM_01) {
            mp = MediaPlayer.create(this, R.raw.alarm_01);
        } else if (this.getSound() == KeyAudio.ALARM_02) {
            mp = MediaPlayer.create(this, R.raw.alarm_02);
        } else if (this.getSound() == KeyAudio.ALARM_03) {
            mp = MediaPlayer.create(this, R.raw.alarm_03);
        }

        mp.setLooping(true);
        mp.start();

    }
}