package com.hcmus.tinuni.Model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String time;
    private Boolean seen;
    // text/image/file
    private String type;

    public Chat() {

    }

    public Chat(String sender, String receiver, String message, String time, Boolean seen, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
        this.seen = seen;
        this.type = type;
    }

    public Boolean isSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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
