package com.fgroupindonesia.helper;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.fgroupindonesia.fgimobile.R;

import java.util.Date;
import java.util.Timer;

public class ElapsedAnimate implements Runnable {

    Date nowDate, scheduleDate;
    TextView textView;
    Timer timerRef;
    StringBuffer stb;

    public void stopTimer() {
        if (timerRef != null) {
            timerRef.cancel();
        }
    }

    public void setTimer(Timer aref){
        timerRef = aref;
    }

    public void setTextView(TextView textObj){
        textView = textObj;
    }

    public void setScheduleDate(Date dataIn){
        scheduleDate = dataIn;
    }

    public void run() {

        nowDate = new Date();

        if (scheduleDate != null) {

            long diff = nowDate.getTime() - scheduleDate.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = diff / daysInMilli;
            diff = diff % daysInMilli;

            long elapsedHours = diff / hoursInMilli;
            diff = diff % hoursInMilli;

            long elapsedMinutes = diff / minutesInMilli;
            diff = diff % minutesInMilli;

            long elapsedSeconds = diff / secondsInMilli;

            if (elapsedHours < 2) {

                if (textView != null) {

                    if(elapsedHours>0) {
                        stb = new StringBuffer("sejak : " +
                                +elapsedHours + " jam, "
                                        + elapsedMinutes + " menit, "
                                        + elapsedSeconds + " detik");
                    }else if(elapsedMinutes>0){
                        stb = new StringBuffer("sejak : " +
                                        + elapsedMinutes + " menit, "
                                        + elapsedSeconds + " detik");
                    }else if(elapsedSeconds>0){
                        stb = new StringBuffer("sejak : " +
                                elapsedSeconds + " detik");
                    }

                    if(stb!=null)
                    textView.setText(stb.toString() + " yang lalu.");

                }


            } else if (elapsedHours >= 2) {

                textView.setText("");
                textView.setVisibility(View.GONE);
                stopTimer();

            }

        }

    }
}
