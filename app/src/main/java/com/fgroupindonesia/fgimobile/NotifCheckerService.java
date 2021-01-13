package com.fgroupindonesia.fgimobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.fgroupindonesia.helper.AudioPlayer;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;

import java.util.Timer;
import java.util.TimerTask;

public class NotifCheckerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void playAudio(int fileChoosen){
        AudioPlayer.play(this, fileChoosen);
    }

    private Timer myTimer, timerNotification60m, timerNotification30m,
    timerNotification15m, timerNotification5m;

    private boolean timer60Done, timer30Done, timer15Done, timer5Done;

    private int secTo60, secTo30, secTo15, secTo5;

    private void startIntervalWork(){

        timerNotification60m = new Timer();
        timerNotification60m.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!timer60Done) {
                    playAudio(AudioPlayer.VOICE_60_MIN_CLASS);
                    timer60Done = true;
                }else{
                    stopTimerTask(timerNotification60m);
                }
            }

        }, secTo60, secTo60);

        timerNotification30m = new Timer();
        timerNotification30m.schedule(new TimerTask() {
            @Override
            public void run() {

                if(!timer30Done) {
                    playAudio(AudioPlayer.VOICE_30_MIN_CLASS);
                    timer30Done = true;
                }else{
                    stopTimerTask(timerNotification30m);
                }
            }

        }, secTo30, secTo30);

        timerNotification15m = new Timer();
        timerNotification15m.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!timer15Done) {
                    playAudio(AudioPlayer.VOICE_15_MIN_CLASS);
                    timer15Done = true;
                }else{
                    stopTimerTask(timerNotification15m);
                }
            }

        }, secTo15, secTo15);

        timerNotification5m = new Timer();
        timerNotification5m.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!timer5Done) {
                    playAudio(AudioPlayer.VOICE_5_MIN_CLASS);
                    timer5Done = true;
                }else{
                    stopTimerTask(timerNotification5m);
                }
            }

        }, secTo5, secTo5);

    }

    public void stopTimerTask (Timer timerIn) {
        if ( timerIn != null ) {
            timerIn .cancel() ;
            timerIn = null;
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i(TAG, "onDestroy called");
        // restart the never ending service

        stopTimerTask(timerNotification60m);
        stopTimerTask(timerNotification30m);
        stopTimerTask(timerNotification15m);
        stopTimerTask(timerNotification5m);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log. e ( TAG , "onStartCommand" ) ;
        super.onStartCommand(intent, flags, startId);

        // incase the service is called
        timer5Done = false;
        timer15Done = false;
        timer30Done = false;
        timer60Done = false;

        textJudul = "Notifikasi Kelas";
        textMuncul = "kelas 1 jam lagi pada ";

        textJudul = intent.getExtras().getString(KeyPref.SCHEDULE_DAY_1);


        // initializing
        startIntervalWork();
        createNotification();

        // will recreate service after killed
        //return START_STICKY;

        // will not recreate service after killed
        return START_NOT_STICKY;

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
