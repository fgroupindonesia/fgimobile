package com.fgroupindonesia.helper;

import android.content.SharedPreferences;

public class UserData {

	public static String Username = null, Passw = null, RegisteredClass=null, TodayClass=null;
	public static boolean RememberLogin = false, NotifLimitPayment=false;
	public static String CurrentVoucherCode=null;
	public static String BroadCastTag = "FGIMobile";
	public static int MINUTES_CURRENT=0;
	
	public static String HOUR_MINUTE_SECOND_SAVED=null;
	public static String HOUR_MINUTE_SECOND_ELAPSED_SAVED=null;

	private static SharedPreferences sharedPreference;

	public static void setPreference(SharedPreferences obj){
		sharedPreference = obj;
	}

	public static void savePreference(String keyName, String valHere){

		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putString(keyName, valHere);
		editor.commit();

	}

	public static void savePreference(String keyName, boolean valHere){

		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putBoolean(keyName, valHere);
		editor.commit();

	}

}
