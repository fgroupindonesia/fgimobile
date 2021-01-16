package com.fgroupindonesia.helper.adapter;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fgroupindonesia.beans.Document;
import com.fgroupindonesia.fgimobile.R;

import java.io.File;
import java.util.ArrayList;

public class DocumentArrayAdapter extends ArrayAdapter<Document>{
    private  Context context;
    private  ArrayList<Document> values;


    private static class ViewHolder {
        TextView txtTitle;
        TextView txtSize;
        TextView txtDate;
        ImageView imageAccess;
        ImageView imageDoc;
    }

    private String getPath(){
        String path = Environment.getExternalStorageDirectory()
                + "/Android/data/" + context.getPackageName() + "/files";

        return path;
    }

    private boolean existLocally(String aFileName){
        boolean stat = false;

        if(aFileName!=null) {

            //+ getApplicationContext().getPackageName();
            File objFile = new File(getPath());

            if (!objFile.exists()) {
                objFile.mkdirs();
            }

            String mypath = getPath() + "/" + aFileName;
            objFile = new File(mypath);
            if (objFile.exists()) {
                stat = true;
            }
        }

        return stat;
    }

    public DocumentArrayAdapter(Context context, ArrayList<Document> values) {
        super(context,R.layout.list_document, values);
        this.context = context;
        this.values = values;
    }

    public Document getItem(int post){
        return values.get(post);
    }

    private String getFileSize(String aFileName){
        if(aFileName!=null) {
            String loc = getPath() + "/" + aFileName;
            return "size : " + Formatter.formatShortFileSize(context, new File(loc).length());
        }

        return "size : 0";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Document dataModel = getItem(position);
        ViewHolder viewHolder;
        View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_document, parent, false);

            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.textViewTitleDoc);
            viewHolder.txtSize = (TextView) convertView.findViewById(R.id.textViewSizeDoc);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.textViewDateDoc);
            viewHolder.imageDoc = (ImageView) convertView.findViewById(R.id.imageViewDocument);
            viewHolder.imageAccess = (ImageView) convertView.findViewById(R.id.imageViewAccessDocument);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.imageAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

            }
        });


        //imageViewAccessDoc.setImageResource(R.drawable.checklist);
        if(existLocally(dataModel.getFilename())){
           viewHolder.imageAccess.setImageResource(R.drawable.checklist);
           viewHolder.txtSize.setText(getFileSize(dataModel.getFilename()));
        }else{
            viewHolder.imageAccess.setImageResource(R.drawable.download);
            viewHolder.txtSize.setText("size : not available");
        }

        viewHolder.txtTitle.setText(dataModel.getTitle());
        viewHolder.txtDate.setText(dataModel.getDate_created());




        if(dataModel.getFilename()!=null){
            if(dataModel.getFilename().contains(".rar")){
                viewHolder.imageDoc.setImageResource(R.drawable.rar);
            }else if(dataModel.getFilename().contains(".zip")){
                viewHolder.imageDoc.setImageResource(R.drawable.zip);
            }else if(dataModel.getFilename().contains(".pdf")){
                viewHolder.imageDoc.setImageResource(R.drawable.pdf);
            }else if(dataModel.getFilename().contains(".png")){
                viewHolder.imageDoc.setImageResource(R.drawable.png);
            }else if(dataModel.getFilename().contains(".doc") || dataModel.getFilename().contains(".docx")){
                viewHolder.imageDoc.setImageResource(R.drawable.document);
            }else if(dataModel.getFilename().contains(".psd")){
                viewHolder.imageDoc.setImageResource(R.drawable.ps);
            }else if(dataModel.getFilename().contains(".jpg") || dataModel.getFilename().contains(".jpeg")){
                viewHolder.imageDoc.setImageResource(R.drawable.jpg);
            }else if(dataModel.getFilename().contains(".wav") || dataModel.getFilename().contains(".mp3")){
                viewHolder.imageDoc.setImageResource(R.drawable.audio);
            }else if(dataModel.getFilename().contains(".xls") || dataModel.getFilename().contains(".xlsx")){
                viewHolder.imageDoc.setImageResource(R.drawable.excel);
            }else if(dataModel.getFilename().contains(".ppt") || dataModel.getFilename().contains(".pptx")){
                viewHolder.imageDoc.setImageResource(R.drawable.ppoint);
            }else {

                    viewHolder.imageDoc.setImageResource(R.drawable.file);


            }


        }else if(dataModel.getUrl().contains("tube")){
            viewHolder.imageDoc.setImageResource(R.drawable.youtube);
        }


        return convertView;
    }


}
