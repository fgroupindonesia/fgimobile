package com.fgroupindonesia.helper;

/*
 *  This is a same file as used in Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */

import com.fgroupindonesia.beans.Schedule;
import com.fgroupindonesia.beans.ScheduleObsData;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;


public class ScheduleObserver {

    String dayOrder[] = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    int indexFound = -1;
    int manyDays = 0;
    String timeSet;
    String nowDaySet, nowHourSet, daySet, nowDayTimeSet, estimatedNextDate;
    int hour, minute, post;
    String hourText, minuteText;
    Date nowDate;
    String a60MinBefore, a30MinBefore,
            a15MinBefore, a5MinBefore;

    /* replaced with array scheduleObsData object
    int diffDay[] = null;
    Date dateObjects[] = null;
    String scheduleTextEntries[] = null;
     */

    ScheduleObsData schedeObjectsData[] = null;

    String stat;

    // using mysql format
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatterComplete = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dayOnlyFormatter = new SimpleDateFormat("EEEE");
    SimpleDateFormat monthOnlyFormatter = new SimpleDateFormat("MMMM");
    SimpleDateFormat hourOnlyFormatter = new SimpleDateFormat("HH:mm");

    public ScheduleObserver() {

    }

    public String getThisMonth() {
        nowDate = new Date();
        String mNumber = monthOnlyFormatter.format(nowDate);
        return mNumber;
    }

    public String getStat() {
        return stat;
    }

    public int getDifferenceDay() {
        return manyDays;

    }

    public boolean isHourPassed(String dateTimeIn, int hourPassedLimit) {

        // dateTimeIn is using this format :
        // HH:mm for example
        // 12:00
        boolean stat = false;

        // we calculate the hour if only the day name is now

        String nearestHourData[] = dateTimeIn.split(":");

        nowDate = new Date();
        nowHourSet = hourOnlyFormatter.format(nowDate);

        // HH:mm for example 12:00
        String data[] = nowHourSet.split(":");
        int jamSkarang = Integer.parseInt(data[0]);
        int menitSkarang = Integer.parseInt(data[1]);

        int jamNearest = Integer.parseInt(nearestHourData[0]);
        int menitNearest = Integer.parseInt(nearestHourData[1]);

        // we say now is on schedule time
        // when : 12:00-14:00 (2 hours only)

        if (menitNearest == 0 && jamNearest == jamSkarang && menitSkarang == menitNearest) {
            // when now is the hour time, and minute is exactly 0
            // so it is not passed yet
            stat = false;
        } else if (jamSkarang >= jamNearest ) {
            // when the hour now is Matched with Limit hour less
            if (jamSkarang == jamNearest + hourPassedLimit && menitSkarang == 0) {
                // when the hour is reach Limit hour exactly
                // so it is not passed yet
                stat = false;
            } else if (jamSkarang <= jamNearest + (hourPassedLimit - 1) && menitSkarang != 0) {
                // when the hour is not more than 1 hour before
                // and the minute is whatever
                // so it is not passed yet
                stat = false;
            } else {
                // when the hour now is more than Limit hours
                // so it is passed precisely
                stat = true;
            }
        }

        return stat;

    }

    public boolean isHourPassed() {

        // nearest is using this format :
        // day HH:mm for example
        // monday 12:00
        boolean stat = false;

        // we calculate the hour if only the day name is now
        if (isScheduleToday()) {

            String nearest = getScheduleNearest();
            String nearestData[] = nearest.split(" ");
            String nearestHourData[] = nearestData[1].split(":");

            nowDate = new Date();
            nowHourSet = hourOnlyFormatter.format(nowDate);

            // HH:mm for example 12:00
            String data[] = nowHourSet.split(":");
            int jamSkarang = Integer.parseInt(data[0]);
            int menitSkarang = Integer.parseInt(data[1]);

            int jamNearest = Integer.parseInt(nearestHourData[0]);
            int menitNearest = Integer.parseInt(nearestHourData[1]);

            // we say now is on schedule time
            // when : 12:00-14:00 (2 hours only)

            if (menitNearest == 0 && jamNearest == jamSkarang && menitSkarang == menitNearest) {
                // when now is the hour time, and minute is exactly 0
                // so it is not passed yet
                stat = false;
            } else if (jamSkarang >= jamNearest && jamSkarang <= jamNearest + 2) {
                // when the hour now is two hour less
                if (jamSkarang == jamNearest + 2 && menitSkarang == 0) {
                    // when the hour is reach 2 hour exactly
                    // so it is not passed yet
                    stat = false;
                } else if (jamSkarang <= jamNearest + 1 && menitSkarang != 0) {
                    // when the hour is not more than 1 hour
                    // and the minute is whatever
                    // so it is not passed yet
                    stat = false;
                } else {
                    // when the hour now is more than 2 hours
                    // so it is passed precisely
                    stat = true;
                }
            }

        }

        return stat;

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

    // the next means the next from matched one
    public String getScheduleNext() {
        String text = null;

        // get the scheduleNearest but not passed yet
        // and will get the next one if any
        nowDate = new Date();

        if (post < schedeObjectsData.length - 1) {
            post++;
        } else {
            post = 0;
        }

        // we retrieve the text only
        text = schedeObjectsData[post].getScheduleText();
        /*if (date2.before(date1)) {

            text = date1Text;

        } else {

            text = date2Text;

        }*/

        return text;
    }

    public String getAllSchedules(){
        String data = "";

        for(ScheduleObsData scd : schedeObjectsData){
           data += scd.getScheduleText() + " hour passed - " +  isHourPassed(scd.getTime(), 2) +"\n";
        }

        return data;
    }

    // nearest before
    // but not nearest after
    public String getScheduleNearest() {
        String text = null;

        // get the scheduleNearest but not passed yet
        // and will get the next one if any
        nowDate = new Date();
        // what day is it now?
        daySet = dayOnlyFormatter.format(nowDate).toLowerCase();

        ScheduleObsData sod = null;
        boolean missing = true;

        for (int i = 0; i < schedeObjectsData.length; i++) {

            sod = schedeObjectsData[i];

            // is it same day name?
            if(sod.getScheduleText().toLowerCase().contains(daySet)){
                // is the time is passed or not reached yet?
                if(!sod.getDate().before(nowDate)){
                    text = sod.getScheduleText();
                    missing = false;
                    break;
                }
            }

        }

        if(missing){
            text = schedeObjectsData[0].getScheduleText();
        }

        return text;
    }

    public Date getDateNearest() {
        Date foundDate = null;

        nowDate = new Date();

        String text = null;

        // get the scheduleNearest but not passed yet
        // and will get the next one if any
        nowDate = new Date();
        // what day is it now?
        daySet = dayOnlyFormatter.format(nowDate).toLowerCase();

        ScheduleObsData sod = null;
        boolean missing = true;

        for (int i = 0; i < schedeObjectsData.length; i++) {

            sod = schedeObjectsData[i];

            // is it same day name?
            if(sod.getScheduleText().toLowerCase().contains(daySet)){
                // is the time is passed or not reached yet?
                if(!sod.getDate().before(nowDate)){
                    foundDate = sod.getDate();
                    missing = false;
                    break;
                }
            }

        }

        if(missing){
            foundDate = schedeObjectsData[0].getDate();
        }

        return foundDate;

    }

    public int convertIndexToMinToGo(int val) {

        int min = 0;

        // based upon array position
        // 60min,30min,15min,5min
        if (val == 0) {
            min = 60;
        } else if (val == 1) {
            min = 30;
        } else if (val == 2) {
            min = 15;
        } else if (val == 3) {
            min = 5;
        }

        return min;
    }

    public int getIndexOfSmallestNonNegative(long[] entry) {

        int x = 0;
        for (long data : entry) {
            if (data > -1) {
                return x;
            }
            x++;
        }

        if (x == entry.length) {
            x = -1;
        }

        return x;

    }

    public boolean isScheduleThisHour() {

        boolean found = false;

        // we should check any of the data given
        nowDate = new Date();
        nowDaySet = dayOnlyFormatter.format(nowDate).toLowerCase();

        for(ScheduleObsData scd: schedeObjectsData){

            // is it same day?
            if(scd.getScheduleText().toLowerCase().contains(nowDaySet)){

                // let check the time
                // is it time passed?
                if(scd.getDate().before(nowDate)){
                    // lets check the time is it passed yet by 2 hours?
                    if(!isHourPassed(scd.getTime(), 2)){
                        found = true;
                        break;
                    }
                }

            }

        }

        return found;
    }



    public boolean isScheduleToday() {
        // this has : day HH:mm pattern
        boolean found = false;

        // we should check any of the data given
        nowDate = new Date();
        nowDaySet = dayOnlyFormatter.format(nowDate).toLowerCase();

        for(ScheduleObsData scd: schedeObjectsData){

            // is it same day?
            if(scd.getScheduleText().toLowerCase().contains(nowDaySet)){

                // let check the time
                // is it time passed?
                if(scd.getDate().before(nowDate)){
                    // lets check the time is it passed yet by 2 hours?
                    if(!isHourPassed(scd.getTime(), 2)){
                        found = true;
                        break;
                    }
                }

            }

        }


        return found;

    }

    public int getIndexOfSmallest(long val, long[] entry) {

        int x = 0;
        for (long data : entry) {
            if (data == val) {
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
    public int[] generateMinSet() {
        // 60min, 30min, 15min, 5min
        int minSet[] = {60, 30, 15, 5};
        return minSet;

    }

    public String generateDateNotif(String hourText) {

        nowDate = new Date();
        nowDaySet = formatter.format(nowDate).toLowerCase();
        // yyyy-MM-dd HH:mm:ss
        String result = nowDaySet + " " + hourText + ":00";
        return result;

    }

    public String[] generateDateSetNotif(String[] hourSetText) {
        String newData[] = new String[hourSetText.length];

        int i = 0;
        for (String timeNa : hourSetText) {
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

    // public void setDates(String... formatted) {
    public void setDates(Schedule... raw) {


        // arrays preparation
        schedeObjectsData = new ScheduleObsData[raw.length];
        /*diffDay = new int[raw.length];
        dateObjects = new Date[raw.length];
        scheduleTextEntries = new String[raw.length];*/

        String val = null;
        int i = 0;

        for (Schedule dat : raw) {
            val = dat.getDay_schedule() + " " + dat.getTime_schedule();

            setDate(val);
            /* earlier stage are :
            dayToSched1 = getDifferenceDay();
            date1 = getDate();
            date1Text = val;
             */
            // current stage data are stored in array
            //diffDay[i] = getDifferenceDay();
            //dateObjects[i] = getDate();
            //scheduleTextEntries[i] = val;

            // implementing object
            ScheduleObsData obj = new ScheduleObsData();
            obj.setDifferentDay(getDifferenceDay());
            obj.setDate(getDate());
            obj.setScheduleText(val);

            schedeObjectsData[i] = obj;

            val = null;
            i++;
        }

        if (i != 0) {
            // lets sort them all
            /*Arrays.sort(diffDay);
            Arrays.sort(dateObjects);
            Arrays.sort(scheduleTextEntries);*/

            // this is comparing based upon the difference day
            Arrays.sort(schedeObjectsData, new Comparator<ScheduleObsData>() {

                public int compare(ScheduleObsData o1, ScheduleObsData o2) {
                    return new Integer(o1.getDifferentDay()).compareTo(new Integer(o2.getDifferentDay()));
                }
            });

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

        indexNextDay = getIndexOf(nextDay.toLowerCase());
        indexToday = getIndexOf(todayDay.toLowerCase());

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

