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

import com.fgroupindonesia.helper.Navigator;
import com.fgroupindonesia.helper.ScheduleObserver;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.URLReference;
import com.fgroupindonesia.helper.WebRequest;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class KelasActivity extends Activity implements Navigator {

    LinearLayout linearKelasNoEntry, linearKelasLoading, linearKelasSignature,
            linearKelasBerlangsung, linearKelasRating;
    SignaturePad mSignaturePad;
    Button buttonSaveSignature, buttonClearSignature,
    buttonHadir, buttonIdzin, buttonRatingNormal, buttonRatingConfused, buttonRatingExcited;
    // in miliseconds
    int PERIOD_OF_TIME = 2000;
    boolean statusStartedClass = true;
    String statusAttendance, fileSignaturePath;

    final int STATUS_RATE_NORMAL = 1, STATUS_RATE_CONFUSED = 0, STATUS_RATE_EXCITED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas);

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
                //showLoading(false);
                // if else here
                if(checkClassStarted()){
                    //showRating();
                    showClassStarted();
                }else{
                    showNoEntry();
                }

            }
        }, PERIOD_OF_TIME);

    }

    public void idzinKelas(View v){
        statusAttendance = "idzin";
        ShowDialog.message(this,"kelas cancelled -idzin");
        finish();
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

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws Exception {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
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
            saveBitmapToJPG(signature, photo);
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


        // the web request executed by httcall
        // preparing the httpcall
        WebRequest httpCall = new WebRequest(this, this);
        httpCall.addData("token", UserData.getPreferenceString(KeyPref.TOKEN));
        httpCall.addData("username", UserData.getPreferenceString(KeyPref.USERNAME));
        httpCall.addData("status", statusAttendance);
        httpCall.addData("class_registered", UserData.getPreferenceString(KeyPref.CLASS_REGISTERED));
        httpCall.setWaitState(true);
        httpCall.setMultipartform(true);

        if(fileSignaturePath!=null){
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

        String sched1 = UserData.getPreferenceString(KeyPref.SCHEDULE_DAY_1);
        String sched2 = UserData.getPreferenceString(KeyPref.SCHEDULE_DAY_2);

        schedObs.setDates(sched1, sched2);
        

        if (!schedObs.isScheduleToday() && !schedObs.isScheduleThisHour()) {
            // when not started
           stat = false;

        } else {
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

    }
}