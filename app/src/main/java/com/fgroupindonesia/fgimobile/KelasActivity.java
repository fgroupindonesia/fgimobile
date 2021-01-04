package com.fgroupindonesia.fgimobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fgroupindonesia.helper.ScheduleObserver;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class KelasActivity extends Activity {

    LinearLayout linearKelasNoEntry, linearKelasLoading, linearKelasSignature;
    SignaturePad mSignaturePad;
    Button buttonSaveSignature, buttonClearSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas);

        buttonSaveSignature =  (Button) findViewById(R.id.buttonSaveSignature);
        buttonClearSignature =  (Button) findViewById(R.id.buttonClearSignature);

        linearKelasLoading = (LinearLayout) findViewById(R.id.linearKelasLoading);
        linearKelasNoEntry = (LinearLayout) findViewById(R.id.linearKelasNoEntry);
        linearKelasSignature = (LinearLayout) findViewById(R.id.linearKelasSignature);

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

        checkClassStarted();

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

            File photo = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + this.getPackageName() + "/" + String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void saveSignature(View v){
        Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
        if (addJpgSignatureToGallery(signatureBitmap))
        {
            ShowDialog.message(this, "signature saved!");
            finish();
        }
    }

    public void clearSignature(View view){
        mSignaturePad.clear();
    }
    private void checkClassStarted() {

        ScheduleObserver schedObs = new ScheduleObserver();

        String sched1 = UserData.getPreferenceString(KeyPref.SCHEDULE_DAY_1);
        String sched2 = UserData.getPreferenceString(KeyPref.SCHEDULE_DAY_2);

        schedObs.setDates(sched1, sched2);

        if (!schedObs.isScheduleStarted()) {
            // when not started
            linearKelasLoading.setVisibility(View.GONE);
            linearKelasNoEntry.setVisibility(View.VISIBLE);
            linearKelasSignature.setVisibility(View.GONE);
        } else {
            // when it is time for class
            linearKelasLoading.setVisibility(View.GONE);
            linearKelasNoEntry.setVisibility(View.GONE);
            linearKelasSignature.setVisibility(View.VISIBLE);
        }

    }


}