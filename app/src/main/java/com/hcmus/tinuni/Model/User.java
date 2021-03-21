package com.hcmus.tinuni.Model;

public class User {
    private String id;
    private String email;
    private String imageURL;
    private String username;
    private String phone;
    private String level;
    private String schoolName;
    private String major;
    private String yearBegins;
    private String gender;

    public User() {

    }



    public User(String id, String email, String imageURL) {
        this.id = id;
        this.email = email;
        this.imageURL = imageURL;
    }

    public User(String id, String username, String email, String imageURL) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.imageURL = imageURL;
    }

    public User(String id, String username, String email, String imageURL, String phone, String gender, String schoolName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.imageURL = imageURL;
        this.phone = phone;
        this.gender = gender;
        this.schoolName = schoolName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getYearBegins() {
        return yearBegins;
    }

    public void setYearBegins(String yearBegins) {
        this.yearBegins = yearBegins;
    }
}
