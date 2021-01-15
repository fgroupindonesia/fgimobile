package com.fgroupindonesia.fgimobile;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fgroupindonesia.beans.Schedule;
import com.fgroupindonesia.helper.AudioPlayer;
import com.fgroupindonesia.helper.Navigator;
import com.fgroupindonesia.helper.RespondHelper;
import com.fgroupindonesia.helper.ScheduleObserver;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.TimerAnimate;
import com.fgroupindonesia.helper.UIHelper;
import com.fgroupindonesia.helper.URLReference;
import com.fgroupindonesia.helper.WebRequest;
import com.fgroupindonesia.helper.shared.KeyAudio;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;
import com.google.gson.Gson;


import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends Activity implements Navigator {

    ScheduleObserver schedObs = new ScheduleObserver();
    ;
    CircleImageView imageUserProfileHome;
    TimerAnimate animWorks = new TimerAnimate();
    private Timer timerSchedule;
    TextView textViewLogout, textviewUsername, textViewNextClass, textViewNextSchedule,
            textViewSample;
    WebRequest httpCall;
    String filePropicName;

    public final String NOTIFICATION_CHANNEL_ID = "10002";
    private final String default_notification_payment_id = "payment";

    int TIME_WAIT = 1 * 1000 * 60;// in minutes
    int FIFTEEN_SECOND = 15 * 1000; // in seconds

    final int ACT_KELAS = 2,
            ACT_OPTIONS = 3,
            ACT_HISTORY = 4,
            ACT_TAGIHAN = 5,
            ACT_USER_PROFILE = 6,
            ACT_DESKTOP = 7,
            ACT_ABSENSI = 8,
            ACT_PEMBAYARAN = 9,
            ACT_DOCUMENT = 10;

    LinearLayout linearDocument, linearOption, linearHistory, linearKelas, linearDesktop, linearTagihan,
            linearAbsensi, linearPembayaran;

    boolean mainMenuShown = true, firstTime = true;
    String usName, aToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addScreenUnlockFlag();

        setContentView(R.layout.activity_home);

        requestPermission();

        textviewUsername = (TextView) findViewById(R.id.textviewUsername);
        textViewLogout = (TextView) findViewById(R.id.textViewLogout);
        textViewNextClass = (TextView) findViewById(R.id.textViewNextClass);
        textViewNextSchedule = (TextView) findViewById(R.id.textViewNextSchedule);

        textViewSample = (TextView) findViewById(R.id.textViewSample);

        textViewNextClass.setBackgroundResource(R.color.yellow);
        textViewNextSchedule.setBackgroundResource(R.color.yellow);

        imageUserProfileHome = (CircleImageView) findViewById(R.id.imageUserProfileHome);

        // for shared preference usage
        UserData.setPreference(this);

        usName = UserData.getPreferenceString(KeyPref.USERNAME);
        aToken = UserData.getPreferenceString(KeyPref.TOKEN);

        if (usName != null) {
            textviewUsername.setText(usName);

        }

        // making things underlined
        textviewUsername.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        textViewLogout.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        //ShowDialog.message(this, "a username called " + usName);

        linearAbsensi = (LinearLayout) findViewById(R.id.linearAbsensi);
        linearPembayaran = (LinearLayout) findViewById(R.id.linearPembayaran);
        linearDocument = (LinearLayout) findViewById(R.id.linearDocument);

        linearOption = (LinearLayout) findViewById(R.id.linearOption);
        linearHistory = (LinearLayout) findViewById(R.id.linearHistory);
        linearKelas = (LinearLayout) findViewById(R.id.linearKelas);
        linearDesktop = (LinearLayout) findViewById(R.id.linearDesktop);
        linearTagihan = (LinearLayout) findViewById(R.id.linearTagihan);

        // calling schedule_all from Web API by interval
        callCheckNextSchedule();

        // calling another user data from API Server
        getDataAPI();


        notifPaymentFirstMonth();
    }

    private void createNotification(String title, String message) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), default_notification_payment_id);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setTicker(message);
        mBuilder.setSmallIcon(R.drawable.fg_logo);
        mBuilder.setAutoCancel(true);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, BillActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, default_notification_payment_id, importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            //assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        //assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    private void makePaymentNotification(String thisMonth){
        createNotification("Notif Pembayaran ", "Harap lakukan pembayaran untuk bulan " + thisMonth);

        // if the payment notification from option activity is checked true
        // thus we play the audio as well
        if(UserData.getPreferenceBoolean(KeyPref.NOTIF_PAYMENT)){
            AudioPlayer.play(this, AudioPlayer.VOICE_PAYMENT_EACH_MONTH);
        }

        // we saved for this month and popup the voice
        UserData.savePreference(KeyPref.NOTIF_PAYMENT_MONTH_LAST_CHECKED, thisMonth);
    }

    public void notifPaymentFirstMonth() {

        UserData.savePreference(KeyPref.NOTIF_PAYMENT_MONTH_LAST_CHECKED, null);
        String lastMonth = UserData.getPreferenceString(KeyPref.NOTIF_PAYMENT_MONTH_LAST_CHECKED);
        String thisMonth = schedObs.getThisMonth();
        if (lastMonth != null) {
            // but not this month
            if (!lastMonth.equalsIgnoreCase(thisMonth)) {

                makePaymentNotification(thisMonth);

            }
        } else {
            // when there's no notif record we also make the popup
            makePaymentNotification(thisMonth);

        }

    }

    public void openAlarm() {
        Intent intent = new Intent(this, AlarmNotifActivity.class);
        startActivity(intent);
    }

    public boolean isNotifClassHourBefore() {
        return UserData.getPreferenceBoolean(KeyPref.NOTIF_KELAS);
    }

    // scheduled comes in
    private void prepareAnimation(Date dataIn) {

        animWorks.setActivity(this);
        animWorks.setTextView(textViewNextClass);
        animWorks.setScheduleDate(dataIn);

        timerSchedule = new Timer();
        timerSchedule.schedule(new TimerTask() {
            @Override
            public void run() {
                startAnimate();
            }

        }, 0, 1000);

        animWorks.setTimer(timerSchedule);

    }

    private void startAnimate() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(animWorks);
    }

    private void showHistoryMenus(boolean b) {
        if (b) {
            linearPembayaran.setVisibility(View.VISIBLE);
            linearAbsensi.setVisibility(View.VISIBLE);

        } else {
            linearPembayaran.setVisibility(View.GONE);
            linearAbsensi.setVisibility(View.GONE);
        }
    }

    private void showMainMenu(boolean b) {

        if (b) {
            linearOption.setVisibility(View.VISIBLE);
            linearKelas.setVisibility(View.VISIBLE);
            linearHistory.setVisibility(View.VISIBLE);
            linearDesktop.setVisibility(View.VISIBLE);
            linearTagihan.setVisibility(View.VISIBLE);
            linearDocument.setVisibility(View.VISIBLE);
        } else {
            linearOption.setVisibility(View.GONE);
            linearKelas.setVisibility(View.GONE);
            linearHistory.setVisibility(View.GONE);
            linearDesktop.setVisibility(View.GONE);
            linearTagihan.setVisibility(View.GONE);
            linearDocument.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void addScreenUnlockFlag() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    public void startNotifChecker(String[] dateTimeText, int minWait[], int indexStart, String schedTextNear) {

        if (!isServiceRunning(NotifCheckerService.class)) {
            Intent panggilan = new Intent(this, NotifCheckerService.class);
            // set two arrays containing important variables
            // and the index started
            panggilan.putExtra(KeyPref.NOTIF_AUDIO_SCHEDULE, schedTextNear);
            panggilan.putExtra(KeyPref.NOTIF_AUDIO_DATE_SET, dateTimeText);
            panggilan.putExtra(KeyPref.NOTIF_AUDIO_MIN_SET, minWait);
            panggilan.putExtra(KeyPref.NOTIF_AUDIO_DATE_INDEX, indexStart);
            startService(panggilan);
        }

    }

    public void logout(View v) {

        Intent intent = new Intent(this, NotifCheckerService.class);
        stopService(intent);

        UserData.savePreference(KeyPref.USERNAME, null);
        UserData.savePreference(KeyPref.PASSWORD, null);

        ActivityCompat.finishAffinity(this);

    }

    public void openDocument(View v) {
        nextActivity(ACT_DOCUMENT);
    }


    public void openTagihan(View v) {
        nextActivity(ACT_TAGIHAN);
    }

    public void openAbsensi(View v) {
        nextActivity(ACT_ABSENSI);
    }

    public void openPembayaran(View v) {
        nextActivity((ACT_PEMBAYARAN));
    }

    public void openUserProfile(View v) {
        nextActivity(ACT_USER_PROFILE);
    }

    public void openOptions(View v) {
        nextActivity(ACT_OPTIONS);
    }

    public void openKelas(View v) {
        nextActivity(ACT_KELAS);
    }

    public void openHistory(View v) {
        nextActivity(ACT_HISTORY);
    }

    public void openDesktop(View v) {
        nextActivity(ACT_DESKTOP);
    }

    private void nextActivity(int jenisActivity) {

        Intent intent = null;

        if (jenisActivity == ACT_OPTIONS) {
            intent = new Intent(this, OptionActivity.class);
        } else if (jenisActivity == ACT_USER_PROFILE) {
            intent = new Intent(this, UserProfileActivity.class);
        } else if (jenisActivity == ACT_KELAS) {
            intent = new Intent(this, KelasActivity.class);
        } else if (jenisActivity == ACT_DOCUMENT) {
            intent = new Intent(this, DokumenActivity.class);
        } else if (jenisActivity == ACT_HISTORY) {

            // hide the other menus
            // show the related ones
            mainMenuShown = !mainMenuShown;

            showMainMenu(mainMenuShown);
            showHistoryMenus(!mainMenuShown);

        } else if (jenisActivity == ACT_DESKTOP) {
            intent = new Intent(this, DesktopActivity.class);
        } else if (jenisActivity == ACT_TAGIHAN) {
            intent = new Intent(this, BillActivity.class);
        } else if (jenisActivity == ACT_ABSENSI) {
            intent = new Intent(this, AttendanceActivity.class);
        } else if (jenisActivity == ACT_PEMBAYARAN) {
            intent = new Intent(this, PaymentActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }

    }

    private void callScheduleChecking(int sec) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animWorks = new TimerAnimate();
                animWorks.setWorking(true);

                httpCall = new WebRequest(HomeActivity.this, HomeActivity.this);
                httpCall.addData("username", usName);
                httpCall.addData("token", aToken);

                httpCall.setWaitState(true);
                httpCall.setRequestMethod(WebRequest.POST_METHOD);
                httpCall.setTargetURL(URLReference.ScheduleAll);
                httpCall.execute();

            }
        }, sec);

    }

    private void checkScheduleOnServer() {
        if (animWorks.isWorking()) {
            animWorks.stopTimer();
            animWorks = null;
            textViewNextClass.setText("Loading...");
        }

        if (!firstTime) {
            // call with delay 15 seconds
            callScheduleChecking(FIFTEEN_SECOND);
        } else {
            // direct call
            callScheduleChecking(0);
        }

    }

    private void callCheckNextSchedule() {

        //UIAction.ACT_API_CURRENT_CALL = OPSAction.ACT_API_SCHEDULE_ALL;

        if (firstTime) {
            // quick post
            checkScheduleOnServer();
            firstTime = false;
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // the web request executed by httcall
                    // preparing the httpcall
                    checkScheduleOnServer();

                }
            }, TIME_WAIT);

        }
    }

    private boolean requestPermission() {
        boolean request = true;
        String[] permissions = {Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.SET_ALARM
        };

        if (permissions.length != 0) {
            ActivityCompat.requestPermissions(this, permissions, 102);
            request = true;
        } else {
            ShowDialog.message(this, "Permissions Denied");

            request = false;
        }

        return request;

    }

    private void startAlarm(int codeUsage) {

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();

        String dataJam = dateFormat.format(date);
        String dataJamMentah[] = dataJam.split(":");

        int jam = Integer.parseInt(dataJamMentah[0]), menit = Integer.parseInt(dataJamMentah[1]);

        ShowDialog.message(this, "notif siap jalan ...\n" + dataJam);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);

        //Create a new PendingIntent and add it to the AlarmManager
        Intent intent = new Intent(this, AlarmNotifActivity.class);

        intent.putExtra("sound", KeyAudio.ALARM_01);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am =
                (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                pendingIntent);


    }

    @Override
    public void onResume() {
        super.onResume();

        // refreshing the picture
        getDataAPI();
    }

    @Override
    public void onDestroy() {

        //startService(new Intent(this, NotifCheckerService.class));

        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();

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

        if (!mainMenuShown) {
            mainMenuShown = !mainMenuShown;
            showMainMenu(mainMenuShown);
            showHistoryMenus(!mainMenuShown);
        } else {


            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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


    @Override
    public void nextActivity() {

    }

    @Override
    public void onSuccess(String urlTarget, String respond) {

        try {
            Gson objectG = new Gson();

            if (RespondHelper.isValidRespond(respond)) {

                //if (UIAction.ACT_API_CURRENT_CALL == OPSAction.ACT_API_SCHEDULE_ALL) {
                if (urlTarget.contains(URLReference.ScheduleAll)) {

                    String innerData = RespondHelper.getValue(respond, "multi_data");
                    Schedule[] dataIn = objectG.fromJson(innerData, Schedule[].class);
                    String className = dataIn[0].getClass_registered();
                    String schedText1 = dataIn[0].getDay_schedule() + " " + dataIn[0].getTime_schedule();
                    String schedText2 = dataIn[1].getDay_schedule() + " " + dataIn[1].getTime_schedule();

                    // store it inside the shared reference
                    UserData.savePreference(KeyPref.SCHEDULE_DAY_1, schedText1);
                    UserData.savePreference(KeyPref.SCHEDULE_DAY_2, schedText2);

                    // schedule helper to calculate and animate time interval before class started
                    //schedObs = new ScheduleObserver();

                    schedObs.setDates(schedText1, schedText2);

                    // String schedIndo = UIHelper.toIndonesian(schedObs.getScheduleNearest());

                    textViewNextSchedule.setSelected(true);
                    textViewNextSchedule.setText("Jadwal Kelas " + className + " : " + UIHelper.toIndonesian(schedText1) + " & " + UIHelper.toIndonesian(schedText2));

                    prepareAnimation(schedObs.getDateNearest());

                    String schedTextNear = schedObs.getScheduleNearest();

                    if (schedObs.isScheduleToday()) {

                        // now executing the Android Services

                        //ShowDialog.message(this, "we got " + schedText1 + " and " + schedText2 +"\n" +schedObs.getDateNearest() + "\n" +schedObs.isDay1Passed() + "\n" + schedObs.getStat());
                        String dataJam[] = schedObs.generateTimeNotif(schedTextNear);
                        long dataDetik[] = schedObs.generateSecondTimeDistance(dataJam);

                        int indexTime = schedObs.getIndexOfSmallestNonNegative(dataDetik);

                        String[] dateTimeSetDekat = schedObs.generateDateSetNotif(dataJam);
                        int[] minToGoSet = schedObs.generateMinSet();

                       /* textViewSample.setText("data jam " + Arrays.toString(dataJam) + "\n" +
                                "data second " + Arrays.toString(dataDetik) + "\n"+
                                "hour to play notif " + Arrays.toString(dateTimeSetDekat) + "\n" +
                                "waiting to " + minToGoSet[indexTime]); */

                        startNotifChecker(dateTimeSetDekat, minToGoSet, indexTime, schedTextNear);

                    } else {
                        //ShowDialog.message(this, "the scheduled will be not today!" );
                    }
                    textViewSample.setText("next is " + schedTextNear);

                    //ShowDialog.message(this, "data jam " + Arrays.toString(dataJam));
                    //ShowDialog.message(this, "data " + Arrays.toString(dataDetik));

                    // now calling again for 5 min delay
                    if (!firstTime) {
                        callCheckNextSchedule();
                    }

                    //}else  if (UIAction.ACT_API_CURRENT_CALL == OPSAction.ACT_API_USERPROFILE_LOAD_DATA) {
                } else if (urlTarget.contains(URLReference.UserProfile)) {

                    JSONObject jo = RespondHelper.getObject(respond, "multi_data");
                    filePropicName = jo.getString("propic");

                    //ShowDialog.message(this, "propic got " + filePropicName);

                    // calling another process to show the images
                    downloadPictureAPI();

                }
                //} else if (UIAction.ACT_API_CURRENT_CALL == OPSAction.ACT_API_USERPROFILE_DOWNLOAD_PICTURE) {
                // when it is invalid
            } else if (!RespondHelper.isValidRespond(respond)) {

                if (urlTarget.contains(URLReference.UserPicture)) {
                    // refreshing the imageview
                    //ShowDialog.message(this, "downloading got " + respond);
                    // memory saver
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 5;

                    Bitmap b = BitmapFactory.decodeFile(respond);
                    imageUserProfileHome.setImageBitmap(b);
                }

            }
        } catch (Exception ex) {
            ShowDialog.message(this, "Error " + ex.getMessage());
            ex.printStackTrace();
        }


    }

    // for obtaining a user profile but not rendered in UI
    public void getDataAPI() {

        //UIAction.ACT_API_CURRENT_CALL = OPSAction.ACT_API_USERPROFILE_LOAD_DATA;
        // the web request executed by httcall
        // preparing the httpcall
        WebRequest httpCall = new WebRequest(this, this);
        httpCall.addData("token", UserData.getPreferenceString(KeyPref.TOKEN));
        httpCall.addData("username", UserData.getPreferenceString(KeyPref.USERNAME));
        httpCall.setWaitState(true);
        httpCall.setRequestMethod(WebRequest.POST_METHOD);
        httpCall.setTargetURL(URLReference.UserProfile);
        httpCall.execute();

    }

    public void downloadPictureAPI() {

        //UIAction.ACT_API_CURRENT_CALL = OPSAction.ACT_API_USERPROFILE_DOWNLOAD_PICTURE;

        WebRequest httpCall = new WebRequest(this, this);
        //httpCall.addData("token", UserData.getPreferenceString(KeyPref.TOKEN));
        httpCall.addData("propic", filePropicName);
        httpCall.setWaitState(true);
        httpCall.setDownloadState(true);

        httpCall.setRequestMethod(WebRequest.GET_METHOD);
        httpCall.setTargetURL(URLReference.UserPicture + filePropicName);
        httpCall.execute();

    }

}
