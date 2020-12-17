package com.fgroupindonesia.helper.shared;

import android.app.Activity;
import android.content.SharedPreferences;

public class UserData {


    public static boolean RememberLogin = false, NotifLimitPayment = false;
    public static String CurrentVoucherCode = null;
    public static String BroadCastTag = "FGIMobile";
    public static int MINUTES_CURRENT = 0;

    public static String HOUR_MINUTE_SECOND_SAVED = null;
    public static String HOUR_MINUTE_SECOND_ELAPSED_SAVED = null;

    private static SharedPreferences sharedPreference;

    public static void setPreference(Activity act) {
        sharedPreference = act.getApplicationContext().getSharedPreferences(BroadCastTag, 0);
    }

    public static void savePreference(String keyName, String valHere) {

        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(keyName, valHere);
        editor.commit();

    }

    public static void savePreference(String keyName, boolean valHere) {

        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putBoolean(keyName, valHere);
        editor.commit();

    }

    public static String getPreferenceString(String keyName){
    	return sharedPreference.getString(keyName, null);
	}

	public static boolean getPreferenceBoolean(String keyName){
    	return sharedPreference.getBoolean(keyName, false);
	}

}
