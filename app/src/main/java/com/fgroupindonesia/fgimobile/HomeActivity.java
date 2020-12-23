package com.fgroupindonesia.fgimobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fgroupindonesia.helper.shared.KeyPref;
import com.fgroupindonesia.helper.shared.UserData;

public class HomeActivity extends Activity {

    TextView textviewUsername;

    final int ACT_KELAS = 2,
            ACT_OPTIONS = 3,
            ACT_HISTORY = 4,
            ACT_TAGIHAN = 5,
            ACT_USER_PROFILE = 6,
            ACT_DESKTOP = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textviewUsername = (TextView) findViewById(R.id.textviewUsername);

        // for shared preference usage
        UserData.setPreference(this);

        String username = UserData.getPreferenceString(KeyPref.USERNAME);

        if (username != null) {
            textviewUsername.setText(username);
        }

    }

    public void logout(View v) {

        UserData.savePreference(KeyPref.USERNAME, null);
        UserData.savePreference(KeyPref.PASSWORD, null);

        ActivityCompat.finishAffinity(this);

    }

    public void openTagihan(View v) {
        nextActivity(ACT_TAGIHAN);
    }

    public void openUserProfile(View v) {
        nextActivity(ACT_USER_PROFILE);
    }

    public void openOptions(View v) {
        nextActivity(ACT_OPTIONS);
    }

    public void openKelas(View v) {
        nextActivity(ACT_KELAS);
    }

    public void openHistory(View v) {
        nextActivity(ACT_HISTORY);
    }

    public void openDesktop(View v){
        nextActivity(ACT_DESKTOP);
    }

    private void nextActivity(int jenisActivity) {

        Intent intent = null;

        if (jenisActivity == ACT_OPTIONS) {
            //intent = new Intent(this, OptionActivity.class);
        } else if (jenisActivity == ACT_USER_PROFILE) {
            intent = new Intent(this, UserProfileActivity.class);
        } else if (jenisActivity == ACT_KELAS) {
            //intent = new Intent(this, KelasActivity.class);
        } else if (jenisActivity == ACT_HISTORY) {
            //intent = new Intent(this, HistoryActivity.class);
        } else if (jenisActivity == ACT_DESKTOP) {
            intent = new Intent(this, DesktopActivity.class);
        } else if (jenisActivity == ACT_TAGIHAN) {
            intent = new Intent(this, BillActivity.class);
        }


        if (intent != null) {
            startActivity(intent);
        }

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //NotificationTray.clearAllMessages();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //super.onBackPressed();
                        //Or used finish();
                        ActivityCompat.finishAffinity(HomeActivity.this);
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }

}
