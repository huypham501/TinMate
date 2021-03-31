package com.hcmus.tinuni.Fragment.HomeViewFragment;

public class MainModel {
    Integer roomAvatar;
    String roomName;
    String roomAmount;


    public MainModel(Integer roomAvatar, String roomName, String roomAmount) {
        this.roomAvatar = roomAvatar;
        this.roomName = roomName;
        this.roomAmount = roomAmount;
    }

    public Integer getRoomAvatar() {
        return roomAvatar;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomAmount() {
        return roomAmount;
    }
}
