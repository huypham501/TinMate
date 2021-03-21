package com.hcmus.tinuni.Model;

public class User {
    private String id;
    private String email;
    private String imageURL;
    private String username;
    private String phone;
    private String school;
    private String majors;
    private int school_year;
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

    public User(String id, String username, String email, String imageURL, String phone, String gender, String school) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.imageURL = imageURL;
        this.phone = phone;
        this.gender = gender;
        this.school = school;
    }

    public User(String id, String username, String phone, String school, String majors, int school_year, String gender) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.school = school;
        this.majors = majors;
        this.school_year = school_year;
        this.gender = gender;
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

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getMajors() {
        return majors;
    }

    public void setMajors(String majors) {
        this.majors = majors;
    }

    public int getSchool_year() {
        return school_year;
    }

    public void setSchool_year(int school_year) {
        this.school_year = school_year;
    }
}
