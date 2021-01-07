package com.fgroupindonesia.helper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fgroupindonesia.beans.Document;
import com.fgroupindonesia.fgimobile.R;

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

    public DocumentArrayAdapter(Context context, ArrayList<Document> values) {
        super(context,R.layout.list_document, values);
        this.context = context;
        this.values = values;
    }

    public Document getItem(int post){
        return values.get(post);
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


        viewHolder.txtTitle.setText(dataModel.getTitle());
        viewHolder.txtDate.setText(dataModel.getDate_created());
        viewHolder.txtSize.setText(dataModel.getSize());

        //imageViewAccessDoc.setImageResource(R.drawable.checklist);
        //imageViewAccessDoc.setImageResource(R.drawable.download);
        viewHolder.imageDoc.setImageResource(R.drawable.rar);

        return convertView;
    }


}
