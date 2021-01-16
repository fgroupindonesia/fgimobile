package com.fgroupindonesia.helper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageHelper {

    public static String convertToSmallJPG(Activity activityIn, String path1, String prefixName){

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
                    + "/Android/data/"+ activityIn.getApplicationContext().getPackageName();
                    //+ getApplicationContext().getPackageName();

            File photo = new File(path + "/" + String.format(prefixName+"_%d.jpeg", System.currentTimeMillis()));
            FileOutputStream out = new FileOutputStream(photo);

            //ShowDialog.message(this, "first " + path1 + "\nNow become "+ photo);

            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 65, out); //100-best quality
            out.close();

            pathEnd = photo.getPath();

        } catch (Exception e) {
            ShowDialog.message(activityIn, "Error on ImageHelper " + e.getMessage());
            e.printStackTrace();
        }

        return pathEnd;

    }


    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 65, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Activity act, Uri uri) {
        String path = "";
        if (act.getContentResolver() != null) {
            Cursor cursor = act.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
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

}