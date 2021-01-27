package com.fgroupindonesia.fgimobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fgroupindonesia.beans.Schedule;
import com.fgroupindonesia.helper.ImageHelper;
import com.fgroupindonesia.helper.Navigator;
import com.fgroupindonesia.helper.RespondHelper;
import com.fgroupindonesia.helper.ScheduleObserver;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.UIHelper;
import com.fgroupindonesia.helper.URLReference;
import com.fgroupindonesia.helper.WebRequest;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KelasActivity extends Activity implements Navigator {

    TextView textViewKelasNoEntry, textViewApakahKamuHadir;
    LinearLayout linearKelasNoEntry, linearKelasLoading, linearKelasSignature,
            linearKelasBerlangsung, linearKelasRating;
    SignaturePad mSignaturePad;
    Button buttonSaveSignature, buttonClearSignature,
    buttonHadir, buttonIdzin, buttonRatingNormal, buttonRatingConfused, buttonRatingExcited;
    // in miliseconds
    int PERIOD_OF_TIME = 2000;
    boolean statusStartedClass = true;
    String statusAttendance, fileSignaturePath, statusKelas;

    final int STATUS_RATE_NORMAL = 1, STATUS_RATE_CONFUSED = 0, STATUS_RATE_EXCITED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas);

        // for shared preference
        UserData.setPreference(this);

        textViewKelasNoEntry = (TextView) findViewById(R.id.textViewKelasNoEntry);
        textViewApakahKamuHadir = (TextView) findViewById(R.id.textViewApakahKamuHadir);

        buttonRatingNormal = (Button) findViewById(R.id.buttonRatingNormal);
        buttonRatingConfused = (Button) findViewById(R.id.buttonRatingConfused);
        buttonRatingExcited = (Button) findViewById(R.id.buttonRatingExcited);

        buttonHadir = (Button) findViewById(R.id.buttonHadir);
        buttonIdzin = (Button) findViewById(R.id.buttonIdzin);
        buttonSaveSignature =  (Button) findViewById(R.id.buttonSaveSignature);
        buttonClearSignature =  (Button) findViewById(R.id.buttonClearSignature);

        linearKelasRating = (LinearLayout) findViewById(R.id.linearKelasRating);
        linearKelasLoading = (LinearLayout) findViewById(R.id.linearKelasLoading);
        linearKelasNoEntry = (LinearLayout) findViewById(R.id.linearKelasNoEntry);
        linearKelasSignature = (LinearLayout) findViewById(R.id.linearKelasSignature);
        linearKelasBerlangsung = (LinearLayout) findViewById(R.id.linearKelasBerlangsung);

        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
            }

            @Override
            public void onSigned() {
                buttonSaveSignature.setEnabled(true);
                buttonClearSignature.setEnabled(true);
            }

            @Override
            public void onClear() {
                buttonSaveSignature.setEnabled(false);
                buttonClearSignature.setEnabled(false);
            }
        });

        // first time show loading
        // before ui changes
        checkingClassNow();

       // checkClassStarted();

    }

    private void showButtonKehadiran(boolean b){

        if(b){
            buttonIdzin.setVisibility(View.VISIBLE);
            buttonHadir.setVisibility(View.VISIBLE);
            textViewApakahKamuHadir.setVisibility(View.VISIBLE);
        }else{
            buttonIdzin.setVisibility(View.GONE);
            buttonHadir.setVisibility(View.GONE);
            textViewApakahKamuHadir.setVisibility(View.GONE);
        }
    }

    private String getTodayDateTime(){
        Date tgl = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(tgl);
    }

    private String getTodayDate(){
        Date tgl = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(tgl);
    }

    public boolean isSignedToday(){

        boolean stat = false;
        // time format is using computer based
        // yyyy-MM-dd
        String dateSigned = UserData.getPreferenceString(KeyPref.LAST_SIGNATURE_DATE);
        String timeSigned = UserData.getPreferenceString(KeyPref.LAST_SIGNATURE_DATETIME);
        String statSigned= UserData.getPreferenceString(KeyPref.LAST_SIGNATURE_STATUS);

        String dateNow = getTodayDate();

        ShowDialog.message(this, "terakhir " + dateSigned + " jam " + timeSigned + "\nstatusNya " + statSigned);

        if(dateSigned==null){
            stat = false;
        }else if(dateSigned.equalsIgnoreCase(dateNow)){
            stat = true;
        }

        return stat;

    }

    public void ratingNormal(View v){
        postRating(STATUS_RATE_NORMAL);
    }

    public void ratingConfused(View v){
        postRating(STATUS_RATE_CONFUSED);
    }

    public void ratingExcited(View v){
        postRating(STATUS_RATE_EXCITED);
    }

    private  void postRating(int stat){



        finish();
    }

    public void checkingClassNow(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // showLoading(false);
                // if else here
                if(checkClassStarted()){
                    //showRating();

                    if(isSignedToday()) {
                        showButtonKehadiran(false);
                    }else{
                        showButtonKehadiran(true);
                    }

                    showClassStarted();

                }else{
                    showNoEntry();
                }

            }
        }, PERIOD_OF_TIME);

    }

    public void idzinKelas(View v){
        statusAttendance = "idzin";

        // save by calling API
        saveDataAttendance();

        ShowDialog.message(this,"kelas cancelled -idzin");
        //finish();
    }



    public void showClassStarted(){
        linearKelasSignature.setVisibility(View.GONE);
        linearKelasNoEntry.setVisibility(View.GONE);
        linearKelasBerlangsung.setVisibility(View.VISIBLE);
        linearKelasLoading.setVisibility(View.GONE);
        linearKelasRating.setVisibility(View.GONE);
    }

    public void showNoEntry(){
        linearKelasSignature.setVisibility(View.GONE);
        linearKelasNoEntry.setVisibility(View.VISIBLE);
        linearKelasBerlangsung.setVisibility(View.GONE);
        linearKelasLoading.setVisibility(View.GONE);
        linearKelasRating.setVisibility(View.GONE);
    }

    public void showRating(){
        linearKelasSignature.setVisibility(View.GONE);
        linearKelasNoEntry.setVisibility(View.GONE);
        linearKelasBerlangsung.setVisibility(View.GONE);
        linearKelasLoading.setVisibility(View.GONE);
        linearKelasRating.setVisibility(View.VISIBLE);
    }

    public void showSignaturePad(View v){
        linearKelasSignature.setVisibility(View.VISIBLE);
        linearKelasNoEntry.setVisibility(View.GONE);
        linearKelasBerlangsung.setVisibility(View.GONE);
        linearKelasLoading.setVisibility(View.GONE);
        linearKelasRating.setVisibility(View.GONE);
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            ShowDialog.message(this,"SignaturePad Directory is not created!");
        }
        return file;
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {

            String path = Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + getApplicationContext().getPackageName();

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            File photo = new File(path + "/" + String.format("Signature_%d.jpg", System.currentTimeMillis()));
            ImageHelper.saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;

            // stored for updating file to be sent on Server
            fileSignaturePath = photo.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void saveDataAttendance() {

        String tglSkrg = getTodayDate();
        String jamSkrg = getTodayDateTime();
        UserData.savePreference(KeyPref.LAST_SIGNATURE_DATE, tglSkrg);
        UserData.savePreference(KeyPref.LAST_SIGNATURE_DATETIME, jamSkrg);
        UserData.savePreference(KeyPref.LAST_SIGNATURE_STATUS, statusAttendance);

        // the web request executed by httcall
        // preparing the httpcall
        WebRequest httpCall = new WebRequest(this, this);
        httpCall.addData("token", UserData.getPreferenceString(KeyPref.TOKEN));
        httpCall.addData("username", UserData.getPreferenceString(KeyPref.USERNAME));
        httpCall.addData("status", statusAttendance);
        httpCall.addData("class_registered", UserData.getPreferenceString(KeyPref.CLASS_REGISTERED));
        httpCall.setWaitState(true);

        if(fileSignaturePath!=null){
            httpCall.setMultipartform(true);
            httpCall.addFile("signature", new File(fileSignaturePath));
        }

        httpCall.setRequestMethod(WebRequest.POST_METHOD);
        httpCall.setTargetURL(URLReference.AttendanceAdd);
        httpCall.execute();

    }

    public void saveSignature(View v){
        Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
        if (addJpgSignatureToGallery(signatureBitmap))
        {
            ShowDialog.message(this, "signature saved!");
            statusAttendance = "hadir";
            // calling to API Server
            saveDataAttendance();
        }
    }

    public void clearSignature(View view){
        mSignaturePad.clear();
    }

    private boolean checkClassStarted() {

        boolean stat = false;

        ScheduleObserver schedObs = new ScheduleObserver();

       // String sched1 = UserData.getPreferenceString(KeyPref.
       // String sched2 = UserData.getPreferenceString(KeyPref.SCHEDULE_DAY_2);

        // lets take the all data schedules from json array object
        Gson objectG = new Gson();
        String innerData = UserData.getPreferenceString(KeyPref.ALL_SCHEDULES);
        Schedule[] dataIn = objectG.fromJson(innerData, Schedule[].class);
        // String className = dataIn[0].getClass_registered();
        // String schedText1 = dataIn[0].getDay_schedule() + " " + dataIn[0].getTime_schedule();

        // the schedules are in array variable
        schedObs.setDates(dataIn);

       /* ShowDialog.message(this,"nearest is " + schedObs.getScheduleNearest());
        ShowDialog.message(this, "hari ini " + schedObs.isScheduleToday());
        ShowDialog.message(this, "jam ini " + schedObs.isScheduleThisHour());
        ShowDialog.message(this, "jam lewat " + schedObs.isHourPassed());
        */

        if(schedObs.isScheduleToday() == true && schedObs.isHourPassed() == true){
            statusKelas = "Kelas hari ini sudah usai.";
            textViewKelasNoEntry.setText(statusKelas);
        }

        if (schedObs.isScheduleToday() != true && schedObs.isScheduleThisHour() != true) {
            // when not started
           stat = false;

        } else if(schedObs.isScheduleToday() == true && schedObs.isScheduleThisHour() == true) {
            // when it is time for class
            stat = true;
        }
        return stat;

    }


    @Override
    public void nextActivity() {

    }

    @Override
    public void onSuccess(String urlTarget, String result) {

        try {

            if (RespondHelper.isValidRespond(result)) {

                if (urlTarget.contains(URLReference.AttendanceAdd)) {
                    // means its attendance is valid
                    // thus we remove the signature and the buttons
                    showButtonKehadiran(false);
                }else{
                    ShowDialog.message(this, "Terjadi kesalahan pada saat absensi.");
                    showButtonKehadiran(true);
                }

                showClassStarted();

            }
        } catch(Exception ex){
            ShowDialog.message(this, "Terjadi kesalahan pada Server. Harap close aplikasi.");
        }
    }
}