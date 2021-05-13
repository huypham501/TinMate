package com.hcmus.tinuni.Model;

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
        return "Report {" +
                "id='" + id + '\'' +
                ", time='" + time + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                ", targetEmail='" + targetEmail + '\'' +
                ", crimeTag='" + crimeTag + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
