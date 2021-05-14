package com.hcmus.tinuni.Model;

import java.lang.reflect.Field;
import java.util.HashMap;

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
        String result = "Group Chat" + "\n\n" +
                "Name: " + name + "\n" +
                "Subject: " + subject + "\n" +
                "Major: " + major + "\n" +
                "School: " + school + "\n";

        return result;
    }

    public static HashMap<String, Object> toHashMap(Object group) {
        HashMap<String, Object> hashMap = new HashMap<>();
        for (Field field : group.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                hashMap.put(field.getName(), field.get(group));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return hashMap;
    }
}
