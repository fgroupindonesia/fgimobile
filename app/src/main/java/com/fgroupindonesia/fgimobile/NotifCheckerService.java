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

    MediaPlayer myPlayer;

    private void playAudio(){
        if(myPlayer!=null){
            myPlayer.stop();
            myPlayer=null;
        }

        myPlayer = MediaPlayer.create(this, R.raw.voice_kelas_dimulai_1jam);
        myPlayer.setLooping(false);
        myPlayer.start();
    }

    private Timer myTimer, timerNotification60m, timerNotification30m,
    timerNotification15m, timerNotification5m;

    private void startIntervalWork(){

        stopTimerTask();

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                // checking here
                playAudio();
            }

        }, 0, 5000);


    }

    public void stopTimerTask () {
        if ( myTimer != null ) {
            myTimer .cancel() ;
            myTimer = null;
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i(TAG, "onDestroy called");
        // restart the never ending service

        stopTimerTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log. e ( TAG , "onStartCommand" ) ;
        super.onStartCommand(intent, flags, startId);

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
