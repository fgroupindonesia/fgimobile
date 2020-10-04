package com.fgroupindonesia.helper;

import android.app.Activity;
import android.widget.Toast;


public class ShowDialog {

	public static void message(Activity komp, String pesanMasuk){
    	
    	Toast.makeText(komp,pesanMasuk,Toast.LENGTH_LONG).show();
    	
    }
	
	public static void shortMessage(Activity komp, String pesanMasuk){
    	
    	Toast.makeText(komp,pesanMasuk,Toast.LENGTH_SHORT).show();
    	
    }
	
}
