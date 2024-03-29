package com.hcmus.tinuni.Model;

public class ChatGroup {
    private String sender;
    private String message;
    private String time;

    // text/image/file
    private String type;

    public ChatGroup() {

    }

    public ChatGroup(String sender, String message, String time, String type) {
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
