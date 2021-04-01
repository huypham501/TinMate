package com.hcmus.tinuni.Model;

public class Demand {
    private String subject;
    private String major;
    private String school;
    private String level;

    public Demand(String subject, String major, String school, String level) {
        this.subject = subject;
        this.major = major;
        this.school = school;
        this.level = level;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
