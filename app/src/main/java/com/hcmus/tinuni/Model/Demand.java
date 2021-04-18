package com.hcmus.tinuni.Model;

public class Demand {

    private String subject;
    private String major;
    private String school;
    private String id;
    private String userId;

    public Demand() {

    }

    public Demand(String subject, String major, String school, String id, String userId) {
        this.subject = subject;
        this.major = major;
        this.school = school;
        this.id = id;
        this.userId = userId;
    }

    public Demand(String subject, String major, String school) {
        this.subject = subject;
        this.major = major;
        this.school = school;
    }

    public boolean isEqual(Demand demand) {
        return subject.equals(demand.getSubject()) && major.equals(demand.getMajor()) && school.equals(demand.getSchool());
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Demand{" +
                "subject='" + subject + '\'' +
                ", major='" + major + '\'' +
                ", school='" + school + '\'' +
                ", id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
