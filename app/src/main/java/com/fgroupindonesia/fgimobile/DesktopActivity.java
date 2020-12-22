package com.fgroupindonesia.fgimobile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;

import com.fgroupindonesia.helper.Navigator;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.UIHelper;
import com.fgroupindonesia.helper.URLReference;
import com.fgroupindonesia.helper.WebRequest;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UIAction;
import com.fgroupindonesia.helper.shared.UserData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;


public class DesktopActivity extends Activity implements Navigator {

    private IntentIntegrator intentIntegrator;
    WebRequest httpCall;

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

    private void verifyClient(String machineID){

        // the web request executed by httcall
        // preparing the httpcall
        httpCall = new WebRequest(this, this);
        httpCall.addData("machine_unique", machineID);

        String usName = UserData.getPreferenceString(KeyPref.USERNAME);

        httpCall.addData("username", usName);
        httpCall.setWaitState(true);
        httpCall.setRequestMethod(WebRequest.POST_METHOD);
        httpCall.setTargetURL(URLReference.RemoteLoginVerify);
        httpCall.execute();

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

                UIAction.ACT_API_CURRENT_CALL
                    verifyClient(message);

                    finish();

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void nextActivity() {

    }

    @Override
    public void onSuccess(String urlTarget, String result) {



    }
}