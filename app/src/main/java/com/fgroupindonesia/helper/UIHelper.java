package com.fgroupindonesia.helper;

import android.widget.EditText;

public class UIHelper {

    public static String getText(EditText element){
        return element.getText().toString();
    }

    public static boolean empty(EditText element){

        boolean stat = false;

        if(getText(element).trim().length()<1){
            stat = true;
        }

        return stat;

    }

}
