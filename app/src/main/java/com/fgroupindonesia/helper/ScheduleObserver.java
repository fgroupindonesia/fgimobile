package com.fgroupindonesia.helper;

/*
 *  This is a same file as used in Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ScheduleObserver {

    String dayOrder[] = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    int indexFound = -1;
    int manyDays = 0;
    String timeSet;
    String nowDaySet, daySet, nowDayTimeSet, estimatedNextDate;
    int hour, minute;
    String hourText, minuteText;
    Date realDate, nowDate, date1, date2;
    String date1Text, date2Text;

    int dayToSched1 = 0;
    int dayToSched2 = 0;

    String stat;

    boolean day1Passed, day2Passed;

    // using mysql format
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatterComplete = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dayOnlyformatter = new SimpleDateFormat("EEEE");

    public ScheduleObserver() {

    }

    public boolean isScheduleStarted(){

        boolean startNow = true;
        // check whether for day1 or day2 is now?


        return startNow;

    }

    public String getStat(){
        return stat;
    }

    public int getDifferenceDay() {
        return manyDays;

    }

    public int getDay1ToSched(){
        return dayToSched1;
    }

    public int getDay2ToSched(){
        return dayToSched2;
    }



    public boolean isDay1Passed(){

       day1Passed = checkPassedTime(getDay1ToSched());
        return day1Passed;

    }

    public boolean isDay2Passed(){
        day2Passed = checkPassedTime(getDay2ToSched());
        return day2Passed;
    }

    private boolean checkPassedTime(int diffDay){

        boolean passedBy = false;
        int hourNow, minuteNow;
        String hourNowText, minuteNowText;

        String dataTempNow[] = nowDayTimeSet.split(" ");
        hourNowText = dataTempNow[1].split(":")[0];
        minuteNowText = dataTempNow[1].split(":")[1];

        hourNow = Integer.parseInt(hourNowText);
        minuteNow = Integer.parseInt(minuteNowText);

        if (diffDay == 0) {
            // means today but hour not reached yet
            if (hourNow < hour) {
                passedBy = false;
                stat = hourNow + "<" + hour;
            }else if(hourNow == hour && minuteNow > minute){
                passedBy = true;
                stat = hourNow + "=" + hour;
            }else if(hourNow > hour){
                passedBy = true;
                stat = hourNow + ">" + hour;
            }
        }else if(diffDay> 0){
            // means day is on next day / week
            passedBy = false;
            stat = "too far";
        }

        return passedBy;
    }

    public String getScheduleNearest(){
        String text = null;
        if ((dayToSched1 < dayToSched2)&& !isDay1Passed()) {

            text = date1Text;

        } else {
            if (date2 != null) {
                text = date2Text;
            } else {
                text = date1Text;
            }

        }


        return text;
    }

    public Date getDateNearest() {
        Date foundDate = null;

        if ((dayToSched1 < dayToSched2)&& !isDay1Passed()) {

            foundDate = date1;

        } else {
            if (date2 != null) {
                foundDate = date2;
            } else {
                foundDate = date1;
            }

        }

        return foundDate;
    }

    public void setDates(String... formatted) {
        setDate(formatted[0]);
        dayToSched1 = getDifferenceDay();
        date1 = getDate();
        date1Text = formatted[0];

        if (formatted.length > 1) {
            setDate(formatted[1]);
            dayToSched2 = getDifferenceDay();
            date2 = getDate();
            date2Text = formatted[1];
        }

    }

    public void setDate(String formattedDate) {
        // input is DAY <space> TIME, for example
        // monday 12:00

        String dataRaw[] = formattedDate.split(" ");
        // day
        daySet = dataRaw[0];
        // time
        // additional for measuring differences later with precision
        timeSet = dataRaw[1] + ":00";

        String dataRaw2[] = dataRaw[1].split(":");
        hourText = dataRaw2[0];
        minuteText = dataRaw2[1];

        hour = Integer.parseInt(hourText);
        minute = Integer.parseInt(minuteText);

        nowDate = new Date();
        nowDaySet = dayOnlyformatter.format(nowDate).toLowerCase();
        nowDayTimeSet = formatterComplete.format(nowDate).toLowerCase();

        manyDays = countDifferenceDay(nowDaySet, daySet);

        System.out.println("looking for " + nowDaySet + " to " + daySet + " found " + manyDays);

        Calendar c = Calendar.getInstance();
        c.setTime(nowDate); // Now use today date.
        c.add(Calendar.DATE, manyDays); // Adding X days

        estimatedNextDate = formatter.format(c.getTime()) + " " + timeSet;
        //UIEffect.popup(estimatedNextDate, null);

    }

    private int getIndexOf(String dayFind) {

        int val = -1;
        indexFound = -1;
        // search once more
        for (String name : dayOrder) {
            indexFound++;
            if (name.equalsIgnoreCase(dayFind)) {
                val = indexFound;
                break;
            }
        }

        return val;
    }

    private int countDifferenceDay(String todayDay, String nextDay) {
        int val = 0;
        int indexToday = 0, indexNextDay = 0;

        indexNextDay = getIndexOf(nextDay);
        indexToday = getIndexOf(todayDay);

        // if they're in the same position
        // no difference day
        if (indexNextDay == indexToday) {
            val = 0;
            System.out.println("Same day");
        } else if (indexNextDay < indexToday) {
            val = 7 - (indexToday - indexNextDay);
            System.out.println("next day is on next week");
        } else if (indexNextDay > indexToday) {
            val = indexNextDay - indexToday;
            System.out.println("next day is on the same week");
        }

        return val;
    }

    public Date getDate() {
        Date foundDate = null;
        try {
            foundDate = formatterComplete.parse(estimatedNextDate);

        } catch (Exception e) {

        }

        return foundDate;
    }
}

