package com.hcmus.tinuni.Model;

public class Group {
    String id;

    String name;
    String imageURL;
    String subject;
    String major;
    String school;
    public Group(String id, String name, String imageURL, String subject, String major, String school) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.subject = subject;
        this.major = major;
        this.school = school;
    }

    public Group() {

    }
    public Group(String id, String name, String imageURL) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", subject='" + subject + '\'' +
                ", major='" + major + '\'' +
                ", school='" + school + '\'' +
                '}';
    }
}
