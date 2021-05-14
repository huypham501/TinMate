package com.hcmus.tinuni.Model;

import java.util.Calendar;
import java.util.Date;

public class AdminAction {
//    private String id;
    private String time;
    private String action;
    private String detail;

    public AdminAction() {
    }

    public AdminAction(String time, String action, String detail) {
        this.time = time;
        this.action = action;
        this.detail = detail;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        long reportTime = Long.parseLong(time);
        calendar.setTime(new Date(reportTime));

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String dd = String.valueOf(day);
        String mm = String.valueOf(month);
        String yyyy = String.valueOf(year);
        String hh = String.valueOf(hour);
        String minmin = String.valueOf(minute);
        String ss = String.valueOf(second);

        if (day < 10)
            dd = "0" + dd;
        if (month < 10)
            mm = "0" + mm;
        if (hour < 10)
            hh = "0" + hh;
        if (minute < 10)
            minmin = "0" + minmin;
        if (second < 10)
            ss = "0" + ss;

        String time = dd + "-" + mm + "-" + yyyy;
        String detailTime = hh + ":" + minmin + ":" + ss;
        String result =  "On " + time + ", " + detailTime + ", " +
                action + ": " + detail;

        return result;
    }
}
