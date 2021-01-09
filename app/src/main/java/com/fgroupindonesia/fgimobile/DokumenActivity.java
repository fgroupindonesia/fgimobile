package com.fgroupindonesia.fgimobile;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.fgroupindonesia.beans.Document;
import com.fgroupindonesia.beans.Schedule;
import com.fgroupindonesia.helper.ArrayHelper;
import com.fgroupindonesia.helper.Navigator;
import com.fgroupindonesia.helper.RespondHelper;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.URLReference;
import com.fgroupindonesia.helper.WebRequest;
import com.fgroupindonesia.helper.adapter.DocumentArrayAdapter;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DokumenActivity extends Activity implements Navigator {

    TextView textViewDocumentTotal;
    ListView listViewDocument;
    DocumentArrayAdapter arrayDocAdapter;
    ArrayList<Document> dataDocuments = new ArrayList<Document>();

    String usName, aToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dokumen);

        textViewDocumentTotal = (TextView) findViewById(R.id.textViewDocumentTotal);

        listViewDocument = (ListView) findViewById(R.id.listViewDocument);
        arrayDocAdapter = new DocumentArrayAdapter(this, dataDocuments);
        listViewDocument.setAdapter(arrayDocAdapter);

        // for shared preference usage
        UserData.setPreference(this);

        usName = UserData.getPreferenceString(KeyPref.USERNAME);
        aToken = UserData.getPreferenceString(KeyPref.TOKEN);

        // calling to Server API for documents
        getDocumentsUser();
    }

    public void getDocumentsUser() {

        WebRequest httpCall = new WebRequest(this, this);
        httpCall.addData("username", usName);
        httpCall.addData("token", aToken);

        httpCall.setWaitState(true);
        httpCall.setRequestMethod(WebRequest.POST_METHOD);
        httpCall.setTargetURL(URLReference.DocumentAll);
        httpCall.execute();

    }


    @Override
    public void nextActivity() {

    }


    @Override
    public void onSuccess(String urlTarget, String respond) {

        try {
            Gson objectG = new Gson();

            if (RespondHelper.isValidRespond(respond)) {

                if (urlTarget.contains(URLReference.DocumentAll)) {

                    String innerData = RespondHelper.getValue(respond, "multi_data");
                    Document[] dataIn = objectG.fromJson(innerData, Document[].class);

                    textViewDocumentTotal.setText("Keseluruhan dokumen anda berjumlah : " + dataIn.length + " file.");
                    dataDocuments = ArrayHelper.fillArrayList(dataDocuments, dataIn);

                    ShowDialog.message(this, "Documents are " + dataDocuments.size());

                    arrayDocAdapter.notifyDataSetChanged();

                }

                ShowDialog.message(this, "we got " + respond);

            }

        } catch (Exception ex) {
            ShowDialog.message(this, "error on " + ex.getMessage());
        }

    }
}