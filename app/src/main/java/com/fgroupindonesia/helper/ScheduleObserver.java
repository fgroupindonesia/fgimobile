package com.fgroupindonesia.helper;

/*
 *  This is a same file as used in Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */

import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    String date1Text, date2Text,
            a60MinBefore, a30MinBefore,
            a15MinBefore, a5MinBefore;


    int dayToSched1 = 0;
    int dayToSched2 = 0;

    String stat;

    boolean day1Passed, day2Passed;

    // using mysql format
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatterComplete = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dayOnlyFormatter = new SimpleDateFormat("EEEE");
    SimpleDateFormat monthOnlyFormatter = new SimpleDateFormat("MMMM");
    SimpleDateFormat hourOnlyFormatter = new SimpleDateFormat("HH:mm");

    public ScheduleObserver() {

    }

    public String getThisMonth(){
        nowDate = new Date();
        String mNumber = monthOnlyFormatter.format(nowDate);
        return mNumber;
    }

    public boolean isScheduleStarted() {

        boolean startNow = true;
        // check whether for day1 or day2 is now?


        return startNow;

    }

    public String getStat() {
        return stat;
    }

    public int getDifferenceDay() {
        return manyDays;

    }

    public int getDay1ToSched() {
        return dayToSched1;
    }

    public int getDay2ToSched() {
        return dayToSched2;
    }


    private boolean checkPassedTime(int diffDay) {

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
            } else if (hourNow == hour && minuteNow > minute) {
                passedBy = true;
                stat = hourNow + "=" + hour;
            } else if (hourNow > hour) {
                passedBy = true;
                stat = hourNow + ">" + hour;
            }
        } else if (diffDay > 0) {
            // means day is on next day / week
            passedBy = false;
            stat = "too far";
        }

        return passedBy;
    }

    public String getScheduleNearest() {
        String text = null;

        // get the scheduleNearest but not passed yet
        // and will get the next one if any
        nowDate = new Date();

        if(date2.before(date1)){
            text = date2Text;
        }else {
            text = date1Text;
        }

        return text;
    }

    public Date getDateNearest() {
        Date foundDate = null;

        nowDate = new Date();

        if(date2.before(date1)){
            foundDate = date2;
        }else {
            foundDate = date1;
        }

        return foundDate;

    }

    public int convertIndexToMinToGo(int val){

        int min=0;

        // based upon array position
        // 60min,30min,15min,5min
        if(val==0){
            min = 60;
        }else if(val==1){
            min = 30;
        }else if(val==2){
            min = 15;
        }else if(val==3){
            min = 5;
        }

        return min;
    }

    public int getIndexOfSmallestNonNegative(long[] entry){

        int x=0;
        for(long data: entry){
            if(data>-1){
                return x;
            }
            x++;
        }

        if(x==entry.length){
            x = -1;
        }

        return x;

    }

    public boolean isScheduleToday(){
        // this has : day HH:mm pattern
        String nearest = getScheduleNearest();

        nowDate = new Date();
        nowDaySet = dayOnlyFormatter.format(nowDate).toLowerCase();

        if(nearest.contains(nowDaySet)){
            return true;
        }

        return false;

    }

    public int getIndexOfSmallest(long val, long[] entry){

        int x=0;
        for(long data: entry){
            if(data == val){
                return x;
            }
            x++;
        }

        return x;

    }

    public long getSmallest(long[] secondSetEntry) {

        long selected = -1;
        //Arrays.sort(secondSetEntry);

        for (long sec : secondSetEntry) {
            if (sec > -1) {
                selected = sec;
                break;
            }
        }

        return selected;
    }

    public long[] generateSecondTimeDistance(String[] hourSetTarget) {

        long[] secondDistances = new long[4];
        long value;
        int x = 0;

        nowDate = new Date();
        String timeNow = hourOnlyFormatter.format(nowDate);

        for (String timeIn : hourSetTarget) {

            value = getSecondIntervalBetween(timeNow, timeIn);
            secondDistances[x] = value;

            x++;
        }

        return secondDistances;

    }

    // for audio purposes
    public int [] generateMinSet(){
        // 60min, 30min, 15min, 5min
        int minSet [] = {60, 30, 15, 5};
        return minSet;

    }

    public String generateDateNotif(String hourText){

        nowDate = new Date();
        nowDaySet = formatter.format(nowDate).toLowerCase();
        // yyyy-MM-dd HH:mm:ss
        String result = nowDaySet + " " + hourText + ":00";
        return result;

    }

    public String [] generateDateSetNotif(String [] hourSetText){
        String newData [] = new String[hourSetText.length];

        int i = 0;
        for(String timeNa: hourSetText){
            newData[i] = generateDateNotif(timeNa);
            i++;
        }

        return newData;
    }

    public String[] generateTimeNotif(String date1TextIn) {
        // using the following format : day HH:mm
        // for example -> monday 10:00
        String[] data = date1TextIn.split(" ");

        String hourTextNa = data[1].split(":")[0];
        int hour = Integer.parseInt(hourTextNa);

        a60MinBefore = getTimeByHour(hour - 1, 0);
        a30MinBefore = getTimeByHour(hour - 1, 30);
        a15MinBefore = getTimeByHour(hour - 1, 45);
        a5MinBefore = getTimeByHour(hour - 1, 55);


        // a returned value is an array of time targeted
        // with the following format : hh:mm -> 12:00
        // the order is : 11:00,11:30,11:45,11:55
        String timeNotif[] = {a60MinBefore, a30MinBefore, a15MinBefore, a5MinBefore};
        return timeNotif;

        //int x = getSecondIntervalBetween("10:00", a15MinBefore);
        //return "got " + x + " for " + a15MinBefore;
        //return a5MinBefore;
    }


    public String getTimeBy5Min() {
        return a5MinBefore;
    }

    public String getTimeBy15Min() {
        return a15MinBefore;
    }

    public String getTimeBy30Min() {
        return a30MinBefore;
    }

    public String getTimeBy60Min() {
        return a60MinBefore;
    }

    public long getSecondIntervalBetween(String hour1Text, String hourTarget) {

        long diffSeconds = 0;

        // hour1Text is using format -> HH:00
        // for example -> 12:00
        try {
            Date date1x = hourOnlyFormatter.parse(hour1Text), date2x = hourOnlyFormatter.parse(hourTarget);
            long diff = date2x.getTime() - date1x.getTime();
            diffSeconds = diff / 1000;

        } catch (Exception ex) {
            diffSeconds = -1;
        }

        return diffSeconds;

    }

    private String getTimeByHour(int h, int min) {
        String n = null;
        String hourNa = null;
        if (h < 10) {
            // 0x:00
            hourNa = "0" + h;
        } else {
            hourNa = "" + h;
        }

        if (min < 10) {
            n = hourNa + ":0" + min;
        } else {
            n = hourNa + ":" + min;
        }


        return n;
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
        nowDaySet = dayOnlyFormatter.format(nowDate).toLowerCase();
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

