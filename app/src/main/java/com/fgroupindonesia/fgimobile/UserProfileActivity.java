package com.fgroupindonesia.fgimobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.fgroupindonesia.helper.ErrorLogger;
import com.fgroupindonesia.helper.Navigator;
import com.fgroupindonesia.helper.RespondHelper;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.UIHelper;
import com.fgroupindonesia.helper.URLReference;
import com.fgroupindonesia.helper.WebRequest;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.OPSAction;
import com.fgroupindonesia.helper.shared.UIAction;
import com.fgroupindonesia.helper.shared.UserData;
//import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends Activity implements Navigator {

    public void saveUserProfile(View v) {
        saveDataAPI();
    }

    public void downloadPictureAPI() {

        UIAction.ACT_API_CURRENT_CALL = OPSAction.ACT_API_USERPROFILE_DOWNLOAD_PICTURE;

        WebRequest httpCall = new WebRequest(this, this);
        //httpCall.addData("token", UserData.getPreferenceString(KeyPref.TOKEN));
        httpCall.addData("propic", filePropicName);
        httpCall.setWaitState(true);
        httpCall.setDownloadState(true);

        httpCall.setRequestMethod(WebRequest.GET_METHOD);
        httpCall.setTargetURL(URLReference.UserPicture + filePropicName);
        httpCall.execute();

    }

    public void saveDataAPI() {
        UIAction.ACT_API_CURRENT_CALL = OPSAction.ACT_API_USERPROFILE_UPDATE_DATA;

        // the web request executed by httcall
        // preparing the httpcall
        WebRequest httpCall = new WebRequest(this, this);
        httpCall.addData("token", UserData.getPreferenceString(KeyPref.TOKEN));
        httpCall.addData("id", idText);
        httpCall.addData("username", UIHelper.getText(editTextUsername));
        httpCall.addData("password", UIHelper.getText(editTextPassword));
        httpCall.addData("mobile", UIHelper.getText(editTextMobile));
        httpCall.addData("email", UIHelper.getText(editTextEmail));
        httpCall.addData("address", UIHelper.getText(editTextAddress));
        httpCall.addData("tmv_id", UIHelper.getText(editTextTmvID));
        httpCall.addData("tmv_pass", UIHelper.getText(editTextTmvPass));

        if (picturePath != null) {
            httpCall.addFile("propic", new File(picturePath));
        }

        httpCall.setWaitState(true);
        // for uploading image
        httpCall.setMultipartform(true);
        httpCall.setRequestMethod(WebRequest.POST_METHOD);
        httpCall.setTargetURL(URLReference.UserUpdate);
        httpCall.execute();


    }

    EditText editTextUsername, editTextPassword, editTextEmail,
            editTextAddress, editTextMobile, editTextTmvID, editTextTmvPass;

    LinearLayout linearUserProfileLoading;
    ScrollView scrollViewUserProfile;

    CircleImageView imageUserProfile;
    String picturePath, idText, filePropicName;

    // just some code to remember
    private int TIME_WAIT = 2000, PICK_PICTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // for shared pref usage
        UserData.setPreference(this);

        editTextUsername = (EditText) findViewById(R.id.editTextUserProfileUsername);
        // lock the editing
        editTextUsername.setEnabled(false);

        editTextPassword = (EditText) findViewById(R.id.editTextUserProfilePassword);
        editTextEmail = (EditText) findViewById(R.id.editTextUserProfileEmail);
        editTextAddress = (EditText) findViewById(R.id.editTextUserProfileAddress);
        editTextMobile = (EditText) findViewById(R.id.editTextUserProfileMobilePhone);
        editTextTmvID = (EditText) findViewById(R.id.editTextUserProfileTmvID);
        editTextTmvPass = (EditText) findViewById(R.id.editTextUserProfileTmvPass);

        linearUserProfileLoading = (LinearLayout) findViewById(R.id.linearUserProfileLoading);
        scrollViewUserProfile = (ScrollView) findViewById(R.id.scrollViewUserProfile);

        imageUserProfile = (CircleImageView) findViewById(R.id.imageUserProfile);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getDataAPI();

            }
        }, TIME_WAIT);


    }

    public void pickPicture(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PICTURE);

    }

    public static String getPath(Context context, Uri uri) {
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            //Log.e(ImageConverter.class.getSimpleName(), "ID for requested image not found: " + uri.toString());
            return filePath;
        }
        String imgId = m.group();

        String[] column = {MediaStore.Images.Media.DATA};
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{imgId}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // stop if there's nothing
                return;
            }

            try {
                Uri imageURI = data.getData();
                imageUserProfile.setImageURI(imageURI);

                // this will be sent to server later
                picturePath = getPath(this.getApplicationContext(), imageURI);
                //ShowDialog.message(UserProfileActivity.this, picturePath);

                // lets convert it to png to make it save for any server
                picturePath = convertToSmallJPG(picturePath);



            } catch (Exception ex) {
            }

        }
    }

    private String convertToSmallJPG(String path1){


        String pathEnd = null;


        try {
            // memory saver
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;

            Bitmap bmp = BitmapFactory.decodeFile(path1);

            int width = bmp.getWidth();
            int height = bmp.getHeight();
            float scaleWidth = ((float) width/2) / width;
            float scaleHeight = ((float) height/2) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bmp, 0, 0, width, height, matrix, false);
            bmp.recycle();

            String path = Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + getApplicationContext().getPackageName();

            File photo = new File(path + "/" + String.format("propic_%d.jpeg", System.currentTimeMillis()));
            FileOutputStream out = new FileOutputStream(photo);

            ShowDialog.message(this, "first " + path1 + "\nNow become "+ photo);

            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 65, out); //100-best quality
            out.close();

            pathEnd = photo.getPath();

        } catch (Exception e) {
            ShowDialog.message(this, "Error on 223 " + e.getMessage());
        }

        return pathEnd;

    }

    public void getDataAPI() {

        UIAction.ACT_API_CURRENT_CALL = OPSAction.ACT_API_USERPROFILE_LOAD_DATA;
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

    @Override
    public void nextActivity() {


    }

    @Override
    public void onSuccess(String urlTarget, String respond) {

        try {

            // ShowDialog.message(UserProfileActivity.this, "respond is " + respond);

            if (RespondHelper.isValidRespond(respond)) {

                if (UIAction.ACT_API_CURRENT_CALL == OPSAction.ACT_API_USERPROFILE_LOAD_DATA) {

                    JSONObject jo = RespondHelper.getObject(respond, "multi_data");

                    linearUserProfileLoading.setVisibility(View.GONE);
                    scrollViewUserProfile.setVisibility(View.VISIBLE);

                    idText = jo.getString("id");
                    filePropicName = jo.getString("propic");

                    editTextUsername.setText(jo.getString("username"));
                    editTextPassword.setText(jo.getString("pass"));
                    editTextEmail.setText(jo.getString("email"));
                    editTextMobile.setText(jo.getString("mobile"));
                    editTextAddress.setText(jo.getString("address"));
                    editTextTmvID.setText(jo.getString("tmv_id"));
                    editTextTmvPass.setText(jo.getString("tmv_pass"));

                    // calling the image download
                    downloadPictureAPI();

                } else if (UIAction.ACT_API_CURRENT_CALL == OPSAction.ACT_API_USERPROFILE_UPDATE_DATA) {
                    // back to the dashboard (home)
                    finish();
                }

                // the invalid output is sometimes for non post method
            } else if (UIAction.ACT_API_CURRENT_CALL == OPSAction.ACT_API_USERPROFILE_DOWNLOAD_PICTURE) {
                // refreshing the imageview
                //ShowDialog.message(this, "downloading got " + respond);

                Bitmap b = BitmapFactory.decodeFile(respond);
                imageUserProfile.setImageBitmap(b);
            }
        } catch (Exception err) {
            ErrorLogger.write(err);
            ShowDialog.message(this, "Error obtaining userprofile data! Please contact administrator!");
            ShowDialog.message(this, err.getMessage());

        }

    }


}