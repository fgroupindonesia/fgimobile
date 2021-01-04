package com.fgroupindonesia.fgimobile;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fgroupindonesia.beans.Schedule;
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
import com.fgroupindonesia.helper.shared.OPSAction;
import com.fgroupindonesia.helper.shared.UIAction;
import com.fgroupindonesia.helper.shared.UserData;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends Activity implements Navigator {

    TimerAnimate animWorks = new TimerAnimate();
    private Timer myTimer;
    TextView textviewUsername, textViewNextClass;
    WebRequest httpCall;

    final int ACT_KELAS = 2,
            ACT_OPTIONS = 3,
            ACT_HISTORY = 4,
            ACT_TAGIHAN = 5,
            ACT_USER_PROFILE = 6,
            ACT_DESKTOP = 7,
            ACT_ABSENSI = 8,
            ACT_PEMBAYARAN = 9;

    LinearLayout linearOption, linearHistory, linearKelas, linearDesktop, linearTagihan,
            linearAbsensi, linearPembayaran;

    boolean mainMenuShown = true;
    String usName, aToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textviewUsername = (TextView) findViewById(R.id.textviewUsername);
        textViewNextClass = (TextView) findViewById(R.id.textViewNextClass);

        // for shared preference usage
        UserData.setPreference(this);

        usName = UserData.getPreferenceString(KeyPref.USERNAME);
        aToken = UserData.getPreferenceString(KeyPref.TOKEN);

        if (usName != null) {
            textviewUsername.setText(usName);
        }

        //ShowDialog.message(this, "a username called " + usName);

        linearAbsensi = (LinearLayout) findViewById(R.id.linearAbsensi);
        linearPembayaran = (LinearLayout) findViewById(R.id.linearPembayaran);

        linearOption = (LinearLayout) findViewById(R.id.linearOption);
        linearHistory = (LinearLayout) findViewById(R.id.linearHistory);
        linearKelas = (LinearLayout) findViewById(R.id.linearKelas);
        linearDesktop = (LinearLayout) findViewById(R.id.linearDesktop);
        linearTagihan = (LinearLayout) findViewById(R.id.linearTagihan);

        // calling schedule_all from Web API
        callCheckNextSchedule();

    }

    public void openAlarm(){
        Intent intent = new Intent(this, AlarmNotifActivity.class);
        startActivity(intent);
    }

    public boolean isNotifClassHourBefore(){
        return UserData.getPreferenceBoolean(KeyPref.NOTIF_KELAS);
    }

    // scheduled comes in
    private void prepareAnimation(Date dataIn){

        animWorks.setActivity(this);
        animWorks.setTextView(textViewNextClass);
        animWorks.setScheduleDate(dataIn);

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                startAnimate();
            }

        }, 0, 1000);

        animWorks.setTimer(myTimer);

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
        } else {
            linearOption.setVisibility(View.GONE);
            linearKelas.setVisibility(View.GONE);
            linearHistory.setVisibility(View.GONE);
            linearDesktop.setVisibility(View.GONE);
            linearTagihan.setVisibility(View.GONE);
        }
    }

    public void logout(View v) {

        UserData.savePreference(KeyPref.USERNAME, null);
        UserData.savePreference(KeyPref.PASSWORD, null);

        ActivityCompat.finishAffinity(this);

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
            //intent = new Intent(this, KelasActivity.class);
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

    private void callCheckNextSchedule() {

        UIAction.ACT_API_CURRENT_CALL = OPSAction.ACT_API_SCHEDULE_ALL;

        // the web request executed by httcall
        // preparing the httpcall
        httpCall = new WebRequest(this, this);
        httpCall.addData("username", usName);
        httpCall.addData("token", aToken);

        httpCall.setWaitState(true);
        httpCall.setRequestMethod(WebRequest.POST_METHOD);
        httpCall.setTargetURL(URLReference.ScheduleAll);
        httpCall.execute();

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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //NotificationTray.clearAllMessages();
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

                if (UIAction.ACT_API_CURRENT_CALL == OPSAction.ACT_API_SCHEDULE_ALL) {

                    String innerData = RespondHelper.getValue(respond, "multi_data");
                    Schedule[] dataIn = objectG.fromJson(innerData, Schedule[].class);
                    String className = dataIn[0].getClass_registered();
                    String schedText = dataIn[0].getDay_schedule() + " " + dataIn[0].getTime_schedule();

                    // schedule helper to calculate and animate time interval before class started
                    ScheduleObserver schedObs = new ScheduleObserver();
                    schedObs.setDate(schedText);

                    prepareAnimation(schedObs.getDate());

                    ShowDialog.message(this, "we got " + schedText);

                }
            } else {

                ShowDialog.message(this, "invalid");
            }

        } catch (Exception ex) {
            ShowDialog.message(this, "ERror");
        }


    }
}
