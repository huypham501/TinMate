package com.hcmus.tinuni.Model;

import java.util.Calendar;
import java.util.Date;

public class ReportMessage {
    private String id;
    private String time;
    private String ownerEmail;
    private String targetEmail;
    private String crimeTag;
    private String description;
    //-------------------------------------------------------------------------------------
    public ReportMessage() {
    }

    public ReportMessage(String time, String ownerEmail, String targetEmail, String crimeTag, String description) {
        this.time = time;
        this.ownerEmail = ownerEmail;
        this.targetEmail = targetEmail;
        this.crimeTag = crimeTag;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCrimeTag() {
        return crimeTag;
    }

    public void setCrimeTag(String crimeTag) {
        this.crimeTag = crimeTag;
    }

    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        long reportTime = Long.parseLong(time);
        calendar.setTime(new Date(reportTime));

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        String dd = String.valueOf(day);
        String mm = String.valueOf(month);
        String yyyy = String.valueOf(year);

        if (day < 10)
            dd = "0" + dd;
        if (month < 10)
            mm = "0" + mm;

        String time = dd + "-" + mm + "-" + yyyy;
        String result = "Report" + "\n\n" +
                "On " + time + ", " + ownerEmail + " reported " + targetEmail + " for " + crimeTag + ".\n\n" +
                "Description: \n" + description;

        return result;
    }
}
