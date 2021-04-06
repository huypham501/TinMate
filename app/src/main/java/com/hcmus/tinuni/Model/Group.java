package com.hcmus.tinuni.Model;

public class Group {
    String name;
    String imageURL;

    // Participants
    // Demand

    public Group() {

    }

    public Group(String name, String imageURL) {
        this.name = name;
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return name;
    }

    public void setTitle(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
