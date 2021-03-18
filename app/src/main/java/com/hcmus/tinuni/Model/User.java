package com.hcmus.tinuni.Model;

public class User {
    private String id;
    private String username;
    private String email;
    private String imageURL;
    private String phone;
    private String gender;
    private String school;

    public User() {
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
}
