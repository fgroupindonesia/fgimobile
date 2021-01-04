package com.fgroupindonesia.helper;

import android.widget.CheckBox;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UIHelper {

    public static String getText(EditText element) {
        return element.getText().toString();
    }

    private static String dayIndonesia [] = {"Ahad", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
    private static String dayEnglish [] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    public static final int LANG_CODE_ID = 0, LANG_CODE_EN = 1;

    public static String toIndonesian(String dayName){
        int indexFound = 0;
        String newName = null;
        for(String dName: dayEnglish) {

            if (dayName.contains(dName.toLowerCase())) {
                newName = dayName.replace(dName.toLowerCase(), dayIndonesia[indexFound]);
                break;
            }
            indexFound++;
        }


        //return dayIndonesia[indexFound];
        return newName;
    }

    public static String toEnglish(String dayName){
        int indexFound = 0;
        String newName = null;
        for(String dName: dayIndonesia) {

            if (dayName.contains(dName.toLowerCase())) {
                newName = dayName.replace(dName.toLowerCase(), dayEnglish[indexFound]);
                break;
            }

            indexFound++;
        }

        //return dayEnglish[indexFound];
        return newName;
    }

    public static String convertDayName(String computerDate, int langCode) {

        String res = null;
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dt1 = format1.parse(computerDate);
            DateFormat format2 = new SimpleDateFormat("EEEE");
            String finalDay = format2.format(dt1);
            res = finalDay;

            if(langCode == 1) {
                res = toEnglish(finalDay);
            } else {
                res = toIndonesian(finalDay);
            }


        } catch (Exception ex) {

        }

        return res;

    }

    public static boolean empty(EditText element) {

        boolean stat = false;

        if (getText(element).trim().length() < 1) {
            stat = true;
        }

        return stat;

    }

}
