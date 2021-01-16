package com.fgroupindonesia.helper.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fgroupindonesia.beans.Document;
import com.fgroupindonesia.fgimobile.DokumenActivity;
import com.fgroupindonesia.fgimobile.R;
import com.fgroupindonesia.helper.FileOpener;
import com.fgroupindonesia.helper.ShowDialog;

import java.io.File;
import java.util.ArrayList;

public class DocumentArrayAdapter extends ArrayAdapter<Document> {
    private Context context;
    private ArrayList<Document> values;
    //private Activity act;
    private DokumenActivity dokAct;

    private static class ViewHolder {
        TextView txtTitle;
        TextView txtSize;
        TextView txtDate;
        ImageView imageAccess;
        ImageView imageDoc;
    }

    public void setActivity(Activity actIn) {
        dokAct = (DokumenActivity) actIn;
    }

    private Activity getActivity() {
        return dokAct;
    }


    private String getPath() {
        String path = Environment.getExternalStorageDirectory()
                + "/Android/data/" + context.getPackageName() + "/files";

        return path;
    }

    private boolean existLocally(String aFileName) {
        boolean stat = false;

        if (aFileName != null) {

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
        super(context, R.layout.list_document, values);
        this.context = context;
        this.values = values;
    }

    public Document getItem(int post) {
        return values.get(post);
    }

    private void openingFile(String alamatMasuk) {
        ShowDialog.message(getActivity(), "lokasi ke " + new File(getPath(), alamatMasuk).getAbsolutePath());
        FileOpener.openFile(getActivity(), new File(getPath(), alamatMasuk));
    }

    private void downloadFile(String fileNa, String urlNa) {
        ((DokumenActivity) getActivity()).downloadFile(fileNa, urlNa);
        ShowDialog.message(getActivity(),"downloading...");
    }

    public void openingFile(int post, View v) {
        Document d = getItem(post);

        ViewHolder vh = (ViewHolder) v.getTag();
        String jenisIcon = String.valueOf(vh.imageAccess.getTag());

        if (jenisIcon.contains("download")) {
            // downloading...
            //ShowDialog.message(getActivity(), "trying to download " + d.getUrl());
            downloadFile(d.getFilename(), d.getUrl());
        } else if (jenisIcon.contains("checklist")) {
            // opening
            openingFile(d.getFilename());
        } else {
            openBrowser(d.getUrl());
        }

    }

    private void openBrowser(String aURL) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(aURL));
        getActivity().startActivity(browserIntent);
    }

    private String getFileSize(String aFileName) {
        if (aFileName != null) {
            String loc = getPath() + "/" + aFileName;
            return "size : " + Formatter.formatShortFileSize(context, new File(loc).length());
        }

        return "size : 0";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Document dataModel = getItem(position);
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

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.imageAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView img = (ImageView) v;
                String tag = String.valueOf(img.getTag());
                if (tag.contains("download")) {
                    // we are required to download
                    downloadFile(dataModel.getFilename(), dataModel.getUrl());
                } else if (tag.contains("checklist")) {
                    // we may directly open the file locally
                    openingFile(dataModel.getFilename());
                } else if (tag.contains("browser")) {
                    // we just open browser
                    openBrowser(dataModel.getUrl());
                }
            }
        });


        //imageViewAccessDoc.setImageResource(R.drawable.checklist);
        if (existLocally(dataModel.getFilename())) {
            viewHolder.imageAccess.setImageResource(R.drawable.checklist);
            viewHolder.imageAccess.setTag("checklist");
            viewHolder.txtSize.setText(getFileSize(dataModel.getFilename()));
        } else if (dataModel.getUrl().contains("tube")) {
            viewHolder.imageAccess.setImageResource(R.drawable.chrome);
            viewHolder.imageAccess.setTag("browser");
            viewHolder.txtSize.setText("size : not available");
        } else {
            viewHolder.imageAccess.setImageResource(R.drawable.download);
            viewHolder.imageAccess.setTag("download");
            viewHolder.txtSize.setText("size : not available");
        }

        viewHolder.txtTitle.setText(dataModel.getTitle());
        viewHolder.txtDate.setText(dataModel.getDate_created());


        if (dataModel.getFilename() != null) {
            if (dataModel.getFilename().contains(".rar")) {
                viewHolder.imageDoc.setImageResource(R.drawable.rar);
            } else if (dataModel.getFilename().contains(".zip")) {
                viewHolder.imageDoc.setImageResource(R.drawable.zip);
            } else if (dataModel.getFilename().contains(".pdf")) {
                viewHolder.imageDoc.setImageResource(R.drawable.pdf);
            } else if (dataModel.getFilename().contains(".png")) {
                viewHolder.imageDoc.setImageResource(R.drawable.png);
            } else if (dataModel.getFilename().contains(".doc") || dataModel.getFilename().contains(".docx")) {
                viewHolder.imageDoc.setImageResource(R.drawable.document);
            } else if (dataModel.getFilename().contains(".psd")) {
                viewHolder.imageDoc.setImageResource(R.drawable.ps);
            } else if (dataModel.getFilename().contains(".jpg") || dataModel.getFilename().contains(".jpeg")) {
                viewHolder.imageDoc.setImageResource(R.drawable.jpg);
            } else if (dataModel.getFilename().contains(".wav") || dataModel.getFilename().contains(".mp3")) {
                viewHolder.imageDoc.setImageResource(R.drawable.audio);
            } else if (dataModel.getFilename().contains(".xls") || dataModel.getFilename().contains(".xlsx")) {
                viewHolder.imageDoc.setImageResource(R.drawable.excel);
            } else if (dataModel.getFilename().contains(".ppt") || dataModel.getFilename().contains(".pptx")) {
                viewHolder.imageDoc.setImageResource(R.drawable.ppoint);
            } else {

                viewHolder.imageDoc.setImageResource(R.drawable.file);


            }


        } else if (dataModel.getUrl().contains("tube")) {
            viewHolder.imageDoc.setImageResource(R.drawable.youtube);
        }


        return convertView;
    }


}
