package com.fgroupindonesia.fgimobile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fgroupindonesia.beans.Bill;
import com.fgroupindonesia.beans.Schedule;
import com.fgroupindonesia.helper.ImageHelper;
import com.fgroupindonesia.helper.Navigator;
import com.fgroupindonesia.helper.RespondHelper;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.UIHelper;
import com.fgroupindonesia.helper.URLReference;
import com.fgroupindonesia.helper.WebRequest;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;

public class BillActivity extends Activity implements Navigator {


    Bill dataBillIn;
    TextView textViewTagihanRupiah,textViewTagihanTanggalRilis,
            textViewTagihanDescription, textViewTagihanStatus;
    Button buttonTagihanNantiDulu, buttonTagihanBayarSekarang, buttonTagihanUnggahBuktiPembayaran;

    ImageView imageViewBill;

    LinearLayout loadingLayout, billLayout;
    String filePath;

     final int ACT_CAMERA = 1, ACT_GALLERY = 2,
            // for 3secs
             PERIOD_OF_TIME = 3 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        // for shared preference
        UserData.setPreference(this);

        loadingLayout = (LinearLayout) findViewById(R.id.linearBillLoading);
        billLayout = (LinearLayout) findViewById(R.id.linearBillDetail);

        imageViewBill = (ImageView) findViewById(R.id.imageViewBill);

        textViewTagihanStatus = (TextView) findViewById(R.id.textViewTagihanStatus);
        textViewTagihanDescription = (TextView) findViewById(R.id.textViewTagihanDescription);
        textViewTagihanTanggalRilis = (TextView) findViewById(R.id.textViewTagihanTanggalRilis);
        textViewTagihanRupiah = (TextView) findViewById(R.id.textViewTagihanRupiah);

        buttonTagihanBayarSekarang = (Button) findViewById(R.id.buttonTagihanBayarSekarang);
        buttonTagihanNantiDulu = (Button) findViewById(R.id.buttonTagihanNantiDulu);
        buttonTagihanUnggahBuktiPembayaran = (Button) findViewById(R.id.buttonTagihanUnggahBuktiPembayaran);

        requestCamera();

        // calling to Server API
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getLastBill();

            }
        }, PERIOD_OF_TIME);

    }

    private void requestCamera(){
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
    }

    public void nantiDulu(View v){
        finish();
    }

    public void bayarSekarang(View v){
        buttonTagihanBayarSekarang.setVisibility(View.GONE);
        buttonTagihanNantiDulu.setVisibility(View.GONE);
        buttonTagihanUnggahBuktiPembayaran.setVisibility(View.VISIBLE);
    }

    public void getLastBill(){

        WebRequest httpCall = new WebRequest(BillActivity.this, BillActivity.this);
        httpCall.addData("username", UserData.getPreferenceString(KeyPref.USERNAME));
        httpCall.addData("token", UserData.getPreferenceString(KeyPref.TOKEN));

        httpCall.setWaitState(true);
        httpCall.setRequestMethod(WebRequest.POST_METHOD);
        httpCall.setTargetURL(URLReference.BillLast);
        httpCall.execute();

    }

    private void showBillLayout(boolean b){
        if(b) {
            billLayout.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);
        } else{
            billLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
        }
    }

    public void updateBill(){

        WebRequest httpCall = new WebRequest(BillActivity.this, BillActivity.this);
        httpCall.addData("username", UserData.getPreferenceString(KeyPref.USERNAME));
        httpCall.addData("token", UserData.getPreferenceString(KeyPref.TOKEN));
        httpCall.addData("amount", dataBillIn.getAmount()+"");
        httpCall.addData("description", dataBillIn.getDescription());
        httpCall.addData("id", dataBillIn.getId()+"");

        if(filePath!=null) {
            httpCall.addFile("screenshot", new File(filePath));

        }

        httpCall.setWaitState(true);
        // for uploading image
        httpCall.setMultipartform(true);
        httpCall.setRequestMethod(WebRequest.POST_METHOD);
        httpCall.setTargetURL(URLReference.BillPaid);
        httpCall.execute();

    }


    public void unggahBukti(View v){
        selectImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case ACT_CAMERA:
                    if (resultCode == RESULT_OK && data != null) {
                        //Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        //imageView.setImageBitmap(selectedImage);

                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        //imageView.setImageBitmap(photo);
                        //knop.setVisibility(Button.VISIBLE);


                        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                        Uri tempUri = ImageHelper.getImageUri(getApplicationContext(), photo);

                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                        filePath = ImageHelper.getRealPathFromURI(this,tempUri);
                        filePath = ImageHelper.convertToSmallJPG(this, filePath, "payment");

                        textViewTagihanStatus.setText("didapatlah " + filePath);
                        ShowDialog.message(this, "didapatlah " + filePath);

                        updateBill();

                    }

                    break;
                case ACT_GALLERY:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        filePath = ImageHelper.getPath(this.getApplicationContext(), selectedImage);
                        //ShowDialog.message(UserProfileActivity.this, picturePath);

                        // lets convert it to png to make it save for any server
                        filePath = ImageHelper.convertToSmallJPG(this, filePath, "payment");

                        // showing the loading layout
                        //showBillLayout(false);
                        //updateBill();

                        /*
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                filePath = cursor.getString(columnIndex);

                                //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                                // lets convert it to png to make it save for any server
                                filePath = ImageHelper.convertToSmallJPG(this, filePath);

                                cursor.close();

                                // showing the loading layout
                                showBillLayout(false);
                                updateBill();
                            }
                        }
*/
                        break;
                    }

            }
        }
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Unggah Bukti Pembayaran");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, ACT_CAMERA);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , ACT_GALLERY);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void nextActivity() {

    }

    @Override
    public void onSuccess(String urlTarget, String respond) {

        try {
            Gson objectG = new Gson();

            if (RespondHelper.isValidRespond(respond)) {

                if (urlTarget.contains(URLReference.BillLast)) {

                    showBillLayout(true);

                    String innerData = RespondHelper.getValue(respond, "multi_data");

                    dataBillIn = objectG.fromJson(innerData, Bill.class);

                    textViewTagihanRupiah.setText(UIHelper.formatRupiah(dataBillIn.getAmount()));
                    textViewTagihanStatus.setText("Status : " + UIHelper.convertStatusToIndonesia(dataBillIn.getStatus()));
                    textViewTagihanDescription.setText(dataBillIn.getDescription());
                    textViewTagihanTanggalRilis.setText("Tanggal rilis : " + UIHelper.convertDateToIndonesia(dataBillIn.getDate_created()));

                    // in case he already upload the approval
                    // we will hide all buttons
                    if(dataBillIn.getStatus().equalsIgnoreCase("pending")){
                        hideAllButtons();

                    }else if(dataBillIn.getStatus().equalsIgnoreCase("paid")){
                        hideAllButtons();

                        imageViewBill.setImageResource(R.drawable.cash_lunas);
                    }

                }else if (urlTarget.contains(URLReference.BillPaid)) {

                    // this is when updating the payment bill

                    showBillLayout(true);
                    hideAllButtons();
                    textViewTagihanStatus.setText("Status : menunggu konfirmasi");

                }

            } else if (!RespondHelper.isValidRespond(respond)) {

                ShowDialog.message(this, "tidak ada tagihan terkini");
                finish();

            }
        } catch (Exception ex) {
            ShowDialog.message(this, "Error " + ex.getMessage());
            ex.printStackTrace();
        }


    }

    private void hideAllButtons(){
        buttonTagihanNantiDulu.setVisibility(View.GONE);
        buttonTagihanBayarSekarang.setVisibility(View.GONE);
        buttonTagihanUnggahBuktiPembayaran.setVisibility(View.GONE);
    }
}