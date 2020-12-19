package com.fgroupindonesia.fgimobile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;

import com.fgroupindonesia.helper.ShowDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;


public class DesktopActivity extends Activity {

    private IntentIntegrator intentIntegrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desktop);

        intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode");
        intentIntegrator.setCameraId(0);  // Use a specific camera of the device
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setBarcodeImageEnabled(false);

        intentIntegrator.setCaptureActivity(PortraitCaptureActivity.class);

        intentIntegrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null){
                ShowDialog.message(this, "Hasil tidak ditemukan");
            }else{
                 // jika qrcode berisi data

                    String message = result.getContents();

                    // post to the server to unlock the client
                    ShowDialog.message(this, message);



            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}