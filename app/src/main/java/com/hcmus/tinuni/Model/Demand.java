package com.hcmus.tinuni.Model;

public class Demand {


    private String subject;
    private String major;
    private String school;
    private String id;

    public Demand() {

    }

    public Demand(String subject, String major, String school) {
        this.subject = subject;
        this.major = major;
        this.school = school;
    }

    public Demand(Demand demand, String id) {
        this.subject = demand.subject;
        this.major = demand.major;
        this.school = demand.school;
        this.id = id;
    }

    public boolean isEqual(Demand demand) {
        if (subject.equals(demand.getSubject()) && major.equals(demand.getMajor()) && school.equals(demand.getSchool())) {
            return true;
        }
        return false;
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

    @Override
    public String toString() {
        return "Demand{" +
                "subject='" + subject + '\'' +
                ", major='" + major + '\'' +
                ", school='" + school + '\'' +
                '}';
    }
}
