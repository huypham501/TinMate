package com.hcmus.tinuni.Model;

public class User {
    private String id;
    private String email;
    private String imageURL;
    private String userName;
    private String phone;
    private String level;
    private String schoolName;
    private String major;
    private String beginYear;
    private String gender;
    private String banned;

    public User() {
    }

    public User(String id, String email, String imageURL, String banned) {
        this.id = id;
        this.email = email;
        this.imageURL = imageURL;
        this.banned = banned;
    }

    public User(String id, String userName, String email, String imageURL, String phone, String banned) {
        this.id = id;
        this.email = email;
        this.imageURL = imageURL;
        this.userName = userName;
        this.phone = phone;
        this.banned = banned;
    }

    public User(String id, String userName, String email, String imageURL, String banned) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.imageURL = imageURL;
        this.banned = banned;
    }

    public User(String userName, String imageURL, String banned) {
        this.userName = userName;
        this.imageURL = imageURL;
        this.banned = banned;
    }

    public User(String id, String userName, String email, String imageURL, String phone, String gender, String schoolName, String major, String beginYear, String banned) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.imageURL = imageURL;
        this.phone = phone;
        this.gender = gender;
        this.schoolName = schoolName;
        this.major = major;
        this.beginYear = beginYear;
        this.banned = banned;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
        return beginYear;
    }

    public void setYearBegins(String beginYear) {
        this.beginYear = beginYear;
    }

    public String getBanned() { return this.banned;}

    @Override
    public String toString() {
        return email + " " + userName + " " + gender;
    }
}
