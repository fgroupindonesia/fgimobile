package com.fgroupindonesia.fgimobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fgroupindonesia.beans.Attendance;
import com.fgroupindonesia.helper.Navigator;
import com.fgroupindonesia.helper.RespondHelper;
import com.fgroupindonesia.helper.ShowDialog;
import com.fgroupindonesia.helper.UIHelper;
import com.fgroupindonesia.helper.URLReference;
import com.fgroupindonesia.helper.WebRequest;
import com.fgroupindonesia.helper.shared.HistoryCall;
import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AttendanceActivity extends Activity implements Navigator {

    TableLayout tableLayoutAttendance;
    String aToken, userName;
    ArrayList <Attendance> dataAttendance = new ArrayList<Attendance>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        // for shared preference usage
        UserData.setPreference(this);

        aToken = UserData.getPreferenceString(KeyPref.TOKEN);
        userName = UserData.getPreferenceString(KeyPref.USERNAME);

        // for History API call
        HistoryCall.setReference(this, this, aToken);

        tableLayoutAttendance = (TableLayout) findViewById(R.id.tableLayoutAttendance);

        ShowDialog.message(this, "pencarian data " + userName);

        callDataAttendance(userName);
    }

    public  void callDataAttendance(String userName){

        // only if the activity and navigator are predefined earlier

            WebRequest httpCall = new WebRequest(AttendanceActivity.this, this);

            httpCall.addData("username", userName);
            httpCall.addData("token", aToken);
            httpCall.setWaitState(true);
            httpCall.setRequestMethod(WebRequest.POST_METHOD);
            httpCall.setTargetURL(URLReference.AttendanceAll);
            httpCall.execute();

        //    ShowDialog.message(this, URLReference.AttendanceAll);
    }

    @Override
    public void nextActivity() {

    }

    @Override
    public void onSuccess(String urlTarget, String respond) {

        try {

            if (RespondHelper.isValidRespond(respond)) {

                JSONArray jsons = RespondHelper.getArray(respond, "multi_data");

                JsonParser parser = new JsonParser();
                JsonElement mJson =  parser.parse(jsons.toString());
                Gson gson = new Gson();
                Attendance object [] = gson.fromJson(mJson, Attendance[].class);

                //ShowDialog.message(this, "we got " + jsons.toString());
                for (Attendance single:object){
                    addingDataRow(single);
                    dataAttendance.add(single);
                }

            }

        } catch (Exception ex) {
            ShowDialog.message(this, "error at "  + ex.getMessage());
        }

    }

    private TextView createTextView(String mess){


        TextView el = new TextView(this);
        el.setText(mess);
        el.setGravity(Gravity.CENTER);
        //el.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));;

        return el;

    }

    private void clearAllRows(){

        // all data Row are cleared except the last (top) header at index-0th position.
        int count = tableLayoutAttendance.getChildCount();
        for (int i = count; i != 0; i--) {
            View child = tableLayoutAttendance.getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }

    }

    private void showDataByFilter(Spinner month, Spinner stat){

        String monthMode = month.getSelectedItem().toString().toLowerCase();
        String statMode = stat.getSelectedItem().toString().toLowerCase();

        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        int bulanDicari = Integer.parseInt(dateFormat.format(today));

        // 1 bulan lalu
        if(monthMode.contains("lalu")){
            bulanDicari--;
        }

        ShowDialog.message(this, "filtering bulan " + bulanDicari + " untuk " + statMode);

        clearAllRows();
        int manyDataFound = 0;

        try {
            for (Attendance dataKehadiran : dataAttendance) {
                String tanggalNa = dataKehadiran.getDate_created();
                // here january is 0 based
                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tanggalNa);
                //ShowDialog.message(this, "data na " + date1.getMonth());

                // we show all data if 'semua' is selected
                if(statMode.equalsIgnoreCase("semua")){
                    addingDataRow(dataKehadiran);
                    manyDataFound++;
                }else if (dataKehadiran.getStatus().equalsIgnoreCase(statMode) && date1.getMonth()+1 == bulanDicari){
                    addingDataRow(dataKehadiran);
                    manyDataFound++;
                }
            }

        } catch (Exception ex){
            ShowDialog.message(this, "error while filtering data...");
        }

        // show dummy empty not available data
        if(manyDataFound == 0){
            Attendance dummy = new Attendance();
            dummy.setClass_registered("- not available -");
            dummy.setStatus("-");
            dummy.setDate_created("-");
            addingDataRow(dummy);
        }

    }

    public void showFilterAttendance(View v){
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter By");
        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.filter_attendance, null);
        builder.setView(customLayout);
        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Spinner spinnerMonth = customLayout.findViewById(R.id.spinnerMonthFilter);
                Spinner spinnerStatus = customLayout.findViewById(R.id.spinnerStatusFilter);

                showDataByFilter(spinnerMonth, spinnerStatus);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addingDataRow(Attendance dataIn){

        TableRow tr = new TableRow(this);

        TableRow.LayoutParams trLayout = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

        TextView dataText1 = createTextView(dataIn.getClass_registered());
        TextView dataText2 = createTextView(dataIn.getStatus());
        TextView dataText3 = createTextView(UIHelper.convertDayName(dataIn.getDate_created(), UIHelper.LANG_CODE_ID));
        TextView dataText4 = createTextView(dataIn.getDate_created());

        tr.addView(dataText1, trLayout );
        tr.addView(dataText2, trLayout);
        tr.addView(dataText3, trLayout);
        tr.addView(dataText4, trLayout);

        tableLayoutAttendance.addView(tr);
    }

}