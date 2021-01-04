package com.fgroupindonesia.fgimobile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.SurfaceView;

import com.fgroupindonesia.helper.ErrorLogger;
import com.fgroupindonesia.helper.Navigator;
import com.fgroupindonesia.helper.RespondHelper;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.UIHelper;
import com.fgroupindonesia.helper.URLReference;
import com.fgroupindonesia.helper.WebRequest;
import com.fgroupindonesia.helper.shared.HistoryCall;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.OPSAction;
import com.fgroupindonesia.helper.shared.UIAction;
import com.fgroupindonesia.helper.shared.UserData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;


public class DesktopActivity extends Activity implements Navigator {

    private IntentIntegrator intentIntegrator;


    String aToken, usName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desktop);

        // for shared preference usage
        UserData.setPreference(this);

        usName = UserData.getPreferenceString(KeyPref.USERNAME);
        aToken = UserData.getPreferenceString(KeyPref.TOKEN);

        // for History API call
        HistoryCall.setReference(this, this, aToken);

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
        WebRequest httpCall = new WebRequest(this, this);

        httpCall.addData("machine_unique", machineID);
        httpCall.addData("username", usName);
        httpCall.addData("token",aToken);
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
                // let's back to Home Dashboard
                finish();
            }else{
                 // jika qrcode berisi data

                    String message = result.getContents();

                    // post to the server to unlock the client
                    //ShowDialog.message(this, message);

                    // make a FLAG
                    UIAction.ACT_API_CURRENT_CALL = OPSAction.ACT_API_REMOTELOGIN_VERIFY;

                    verifyClient(message);

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void nextActivity() {

    }

    @Override
    public void onSuccess(String urlTarget, String respond) {

        try{

            if (RespondHelper.isValidRespond(respond)) {

                // when it comes from Verifying Client
                if(UIAction.ACT_API_CURRENT_CALL == OPSAction.ACT_API_REMOTELOGIN_VERIFY){

                    // we end this activity, simply to say "OK, done!"
                    // and post a history API call

                    HistoryCall.addHistory(usName, "verifying client successfully.");

                }

            } else {
                ShowDialog.message(this, "Invalid Token, please relogin this mobile app!");

            }

            finish();

        } catch(Exception err){
            ErrorLogger.write(err);
            ShowDialog.message(this, "Error verifying client. Please contact administrator!");

        }



    }
}