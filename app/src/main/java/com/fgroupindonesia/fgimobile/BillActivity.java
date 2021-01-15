package com.fgroupindonesia.fgimobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fgroupindonesia.beans.Bill;
import com.fgroupindonesia.beans.Schedule;
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

public class BillActivity extends Activity implements Navigator {

    TextView textViewTagihanRupiah,textViewTagihanTanggalRilis,
            textViewTagihanDescription, textViewTagihanStatus;
    Button buttonTagihanNantiDulu, buttonTagihanBayarSekarang, buttonTagihanUnggahBuktiPembayaran;

    LinearLayout loadingLayout, billLayout;

     final int ACT_CAMERA = 1, ACT_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        // for shared preference
        UserData.setPreference(this);

        loadingLayout = (LinearLayout) findViewById(R.id.linearBillLoading);
        billLayout = (LinearLayout) findViewById(R.id.linearBillDetail);

        textViewTagihanStatus = (TextView) findViewById(R.id.textViewTagihanStatus);
        textViewTagihanDescription = (TextView) findViewById(R.id.textViewTagihanDescription);
        textViewTagihanTanggalRilis = (TextView) findViewById(R.id.textViewTagihanTanggalRilis);
        textViewTagihanRupiah = (TextView) findViewById(R.id.textViewTagihanRupiah);

        buttonTagihanBayarSekarang = (Button) findViewById(R.id.buttonTagihanBayarSekarang);
        buttonTagihanNantiDulu = (Button) findViewById(R.id.buttonTagihanNantiDulu);
        buttonTagihanUnggahBuktiPembayaran = (Button) findViewById(R.id.buttonTagihanUnggahBuktiPembayaran);

        // calling to Server API
        getLastBill();
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

    public void updateBill(Bill dataIn){

        WebRequest httpCall = new WebRequest(BillActivity.this, BillActivity.this);
        httpCall.addData("username", UserData.getPreferenceString(KeyPref.USERNAME));
        httpCall.addData("token", UserData.getPreferenceString(KeyPref.TOKEN));
        httpCall.addData("amount", dataIn.getAmount()+"");
        httpCall.addData("description", dataIn.getDescription());
        httpCall.addData("id", dataIn.getId()+"");

        httpCall.setWaitState(true);
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
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        //imageView.setImageBitmap(selectedImage);
                        ShowDialog.message(this, "Gambar sedang diupload...");

                        // call API to add data for this Bill


                        finish();
                    }

                    break;
                case ACT_GALLERY:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                ShowDialog.message(this, "Gambar sudah terpilih...");
                                cursor.close();
                                finish();
                            }
                        }

                    }
                    break;
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
                    Bill dataIn = objectG.fromJson(innerData, Bill.class);

                    textViewTagihanRupiah.setText(UIHelper.formatRupiah(dataIn.getAmount()));
                    textViewTagihanStatus.setText("Status : " + UIHelper.convertStatusToIndonesia(dataIn.getStatus()));
                    textViewTagihanDescription.setText(dataIn.getDescription());
                    textViewTagihanTanggalRilis.setText("Tanggal rilis : " + UIHelper.convertDateToIndonesia(dataIn.getDate_created()));

                    // in case he already upload the approval
                    // we will hide all buttons
                    if(dataIn.getStatus().equalsIgnoreCase("pending")){
                        buttonTagihanNantiDulu.setVisibility(View.GONE);
                        buttonTagihanBayarSekarang.setVisibility(View.GONE);
                        buttonTagihanUnggahBuktiPembayaran.setVisibility(View.GONE);
                    }

                    //}else  if (UIAction.ACT_API_CURRENT_CALL == OPSAction.ACT_API_USERPROFILE_LOAD_DATA) {
                }
                //} else if (UIAction.ACT_API_CURRENT_CALL == OPSAction.ACT_API_USERPROFILE_DOWNLOAD_PICTURE) {
                // when it is invalid
            } else if (!RespondHelper.isValidRespond(respond)) {

                showBillLayout(false);

            }
        } catch (Exception ex) {
            ShowDialog.message(this, "Error " + ex.getMessage());
            ex.printStackTrace();
        }


    }
}