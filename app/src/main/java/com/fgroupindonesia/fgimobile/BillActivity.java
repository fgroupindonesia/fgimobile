package com.fgroupindonesia.fgimobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fgroupindonesia.helper.ShowDialog;

public class BillActivity extends Activity {

    Button buttonTagihanNantiDulu, buttonTagihanBayarSekarang, buttonTagihanUnggahBuktiPembayaran;

     final int ACT_CAMERA = 1, ACT_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        buttonTagihanBayarSekarang = (Button) findViewById(R.id.buttonTagihanBayarSekarang);
        buttonTagihanNantiDulu = (Button) findViewById(R.id.buttonTagihanNantiDulu);
        buttonTagihanUnggahBuktiPembayaran = (Button) findViewById(R.id.buttonTagihanUnggahBuktiPembayaran);
    }

    public void nantiDulu(View v){
        finish();
    }

    public void bayarSekarang(View v){
        buttonTagihanBayarSekarang.setVisibility(View.GONE);
        buttonTagihanNantiDulu.setVisibility(View.GONE);
        buttonTagihanUnggahBuktiPembayaran.setVisibility(View.VISIBLE);
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

}