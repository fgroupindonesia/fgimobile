package com.fgroupindonesia.helper;

import android.app.Activity;
import android.widget.TextView;

import com.fgroupindonesia.fgimobile.HomeActivity;

import java.util.Date;
import java.util.Timer;

public class TimerAnimate implements Runnable {
    private Date nowDate, scheduleDate;
    private TextView textView;
    private Timer timerRef;
    private HomeActivity homeAct;
    private boolean working= false;



    public void setWorking(boolean b){
        working = b;
    }

    public boolean isWorking(){
        return working;
    }

    public void setActivity(Activity act){
        homeAct = (HomeActivity) act;
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

    @Override
    public void run() {

        nowDate = new Date();

        //in milliseconds
        if(scheduleDate!=null){


        long diff = scheduleDate.getTime() - nowDate.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays != -1) {

            if (textView != null) {
                if (diffDays > 0) {

                    //labelInterval.setText("Next Class : " + diffDays + " hari, " + diffHours + " jam, " + diffMinutes + " menit, " + diffSeconds + " detik.");
                    textView.setText("countdown : " + diffDays + " Day, "
                            + diffHours + " Hour, "
                            + diffMinutes + " Minute, "
                            + diffSeconds + " Second.");

                } else if (diffHours > 0) {

                    textView.setText("countdown : Today, "
                            + diffHours + " Hour, "
                            + diffMinutes + " Minute, "
                            + diffSeconds + " Second.");
                } else if (diffHours == 0 && diffMinutes>-1) {

                    textView.setText("countdown : Today, "
                            + diffMinutes + " Minute, "
                            + diffSeconds + " Second.");

                } else if (diffDays == 0 && diffHours < 0) {
                    // here when the class is already passed

                    textView.setText("Class was ended");

                    stopTimer();
                }


            }

            if(homeAct != null) {
                if (homeAct.isNotifClassHourBefore()) {
                    if (diffDays == 0 && diffHours == 1 && diffMinutes == 0 && (diffSeconds == 0 || diffSeconds == 1)) {
                        homeAct.openAlarm();
                    }
                }
            }
        } else if (diffDays < 0) {

            stopTimer();

            textView.setText("");

        }

        }

    }



    public void stopTimer(){
        if(timerRef!=null) {
            timerRef.cancel();
        }
    }
}
