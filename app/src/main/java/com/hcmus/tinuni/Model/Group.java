package com.hcmus.tinuni.Model;

public class Group {
    String title;
    String imageURL;

    // Participants
    // Demand

    public Group() {

    }

    public Group(String title, String imageURL) {
        this.title = title;
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
