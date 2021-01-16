package com.fgroupindonesia.fgimobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.fgroupindonesia.helper.AudioPlayer;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NotifCheckerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void playAudio(int fileChoosen) {
        AudioPlayer.play(this, fileChoosen);
    }

    private Date audioDate;
    private Timer myTimer;
    private String scheduleText;
    private String dateTimeText;
    private String [] dateTimeSetText;
    private int minToGoSetInt[];
    private int minToGo;
    private SimpleDateFormat formatterComplete = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int indexDate;
    private Date nextDate(){

        indexDate++;
        try {
            audioDate = formatterComplete.parse(dateTimeSetText[indexDate]);
        } catch (Exception ex){
            audioDate = new Date();
        }

        return audioDate;
    }

    private void startIntervalWork() {

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                if (minToGo == 60) {
                    // for 60min
                    textMuncul = "kelas 1 jam lagi...";
                    playAudio(AudioPlayer.VOICE_60_MIN_CLASS);
                } else if (minToGo == 30) {
                    // for 30min
                    textMuncul = "kelas 30 menit lagi...";
                    playAudio(AudioPlayer.VOICE_30_MIN_CLASS);
                } else if (minToGo == 15) {
                    // for 15min
                    textMuncul = "kelas 15 menit lagi...";
                    playAudio(AudioPlayer.VOICE_15_MIN_CLASS);
                } else if (minToGo == 5) {
                    // for 5min
                    textMuncul = "kelas 5 menit lagi...";
                    playAudio(AudioPlayer.VOICE_5_MIN_CLASS);
                }

                prepareText();
                createNotification();
                // open the UI
                openAlarm();

                if(indexDate+1<dateTimeSetText.length) {
                    // run for the next date
                    audioDate = nextDate();
                    startIntervalWork();
                }else{
                    // if the dateSet is complete thus
                    // we stop everything,the timer along with this services
                    stopTimerTask(myTimer);
                    stopSelf();
                }

            }
        }, audioDate);


    }

    public void stopTimerTask(Timer timerIn) {
        if (timerIn != null) {
            timerIn.cancel();
            timerIn = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i(TAG, "onDestroy called");
        // restart the never ending service

        //stopTimerTask(myTimer);
    }

    private void prepareText(){
        textJudul = "Notifikasi Kelas " + scheduleText;
        dateTimeText = dateTimeSetText[indexDate];
        //textJudul += " " + dateTimeText;

        minToGo = minToGoSetInt[indexDate];
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log. e ( TAG , "onStartCommand" ) ;
        super.onStartCommand(intent, flags, startId);

        Bundle bundle = intent.getExtras();

        scheduleText = bundle.getString(KeyPref.NOTIF_AUDIO_SCHEDULE);
        dateTimeSetText = bundle.getStringArray(KeyPref.NOTIF_AUDIO_DATE_SET);
        minToGoSetInt = bundle.getIntArray(KeyPref.NOTIF_AUDIO_MIN_SET);

        indexDate = bundle.getInt(KeyPref.NOTIF_AUDIO_DATE_INDEX);

        prepareText();


        try {
            audioDate = formatterComplete.parse(dateTimeText);
            // initializing
            startIntervalWork();
        } catch (Exception ex) {
            audioDate = new Date();
        }

        // will recreate service after killed
        //return START_STICKY;

        // will not recreate service after killed
        return START_NOT_STICKY;

    }

    public void openAlarm() {
        Intent dialogIntent = new Intent(this, AlarmNotifActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra(KeyPref.SCHEDULE, scheduleText);
        startActivity(dialogIntent);
    }

    private String textMuncul = null, textJudul = null;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    private void createNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), default_notification_channel_id);
        mBuilder.setContentTitle(textJudul);
        mBuilder.setContentText(textMuncul);
        mBuilder.setTicker(textMuncul);
        mBuilder.setSmallIcon(R.drawable.fg_logo);
        mBuilder.setAutoCancel(true);

        // once the notification is clicked
        // user may go to KelasActivity
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, KelasActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            //assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        //assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }
}
