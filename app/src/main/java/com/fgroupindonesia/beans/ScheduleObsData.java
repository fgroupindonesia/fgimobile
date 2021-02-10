package com.fgroupindonesia.beans;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleObsData {

    private int differentDay;
    private Date date;
    private String scheduleText;

    public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(getDate());
    }

    public int getDifferentDay() {
        return differentDay;
    }

    public void setDifferentDay(int differentDay) {
        this.differentDay = differentDay;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getScheduleText() {
        return scheduleText;
    }

    public void setScheduleText(String scheduleText) {
        this.scheduleText = scheduleText;
    }
}
