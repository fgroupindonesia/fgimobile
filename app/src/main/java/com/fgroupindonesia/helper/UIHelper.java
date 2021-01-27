package com.fgroupindonesia.helper;

import android.widget.CheckBox;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UIHelper {

    public static String getText(EditText element) {
        if(element.getText()!=null) {
            return element.getText().toString();
        } else {
            return null;
        }
    }

    private static String dayIndonesia[] = {"Ahad", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
    private static String dayEnglish[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    public static final int LANG_CODE_ID = 0, LANG_CODE_EN = 1;

    public static String toIndonesian(String dayName) {
        int indexFound = 0;
        String newName = null;
        for (String dName : dayEnglish) {

            if (dayName.toLowerCase().contains(dName.toLowerCase())) {
                newName = dayName.replace(dName.toLowerCase(), dayIndonesia[indexFound]);
                break;
            }
            indexFound++;
        }


        //return dayIndonesia[indexFound];
        return newName;
    }

    public static String toEnglish(String dayName) {
        int indexFound = 0;
        String newName = null;
        for (String dName : dayIndonesia) {

            if (dayName.toLowerCase().contains(dName.toLowerCase())) {
                newName = dayName.replace(dName, dayEnglish[indexFound]);
                break;
            }

            indexFound++;
        }

        //return dayEnglish[indexFound];
        return newName;
    }

    public static String convertDateToIndonesia(String dateSet) {
        // yyyy-MM-dd HH:mm:ss
        String dataMentah[] = dateSet.split(" ");
        String tanggalOnly[] = dataMentah[0].split("-");
        String tanggalIndo = tanggalOnly[2] + "-" + tanggalOnly[1] + "-" + tanggalOnly[0];

        return tanggalIndo;
    }

    public static String convertStatusToIndonesia(String stat) {
        String val = null;
        switch (stat) {
            case "unpaid":
                val = "belum dibayar";
                break;
            case "pending":
                val = "menunggu konfirmasi";
                break;
            case "paid":
                val = "lunas";
                break;
        }

        return val;
    }

    public static String formatRupiah(int number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }

    public static boolean isEnglish(String dayName){
        for(String day:dayEnglish){
            if(day.toLowerCase().equalsIgnoreCase(dayName.toLowerCase())){
                return true;
            }
        }

        return false;
    }

    public static String convertDayName(String computerDate) {

        String res = null;
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dt1 = format1.parse(computerDate);
            DateFormat format2 = new SimpleDateFormat("EEEE");
            String finalDay = format2.format(dt1);
            res = finalDay;

            if(isEnglish(finalDay)){
                res = toIndonesian(finalDay);
            } else {
                res = toEnglish(finalDay);
            }


        } catch (Exception ex) {
            res = "";
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
