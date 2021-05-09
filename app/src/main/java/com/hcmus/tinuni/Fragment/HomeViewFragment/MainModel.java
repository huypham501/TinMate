package com.hcmus.tinuni.Fragment.HomeViewFragment;

public class MainModel {
    String id;
    String roomAvatar;
    String roomName;
    Long roomAmount;


    public MainModel(String id, String roomAvatar, String roomName, Long roomAmount) {
        this.roomAvatar = roomAvatar;
        this.roomName = roomName;
        this.roomAmount = roomAmount;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getRoomAvatar() {
        return roomAvatar;
    }

    public String getRoomName() {
        return roomName;
    }

    public Long getRoomAmount() {
        return roomAmount;
    }
}
