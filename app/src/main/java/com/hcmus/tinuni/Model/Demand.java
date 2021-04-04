package com.hcmus.tinuni.Model;

public class Demand {
    private String subject;
    private String major;
    private String school;

    public Demand() {

    }

    public Demand(String subject, String major, String school) {
        this.subject = subject;
        this.major = major;
        this.school = school;
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

    @Override
    public String toString() {
        return "Demand{" +
                "subject='" + subject + '\'' +
                ", major='" + major + '\'' +
                ", school='" + school + '\'' +
                '}';
    }
}
