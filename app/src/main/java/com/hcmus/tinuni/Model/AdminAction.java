package com.hcmus.tinuni.Model;

public class AdminAction {
//    private String id;
    private String time;
    private String action;
    private String detail;

    public AdminAction() {
    }

    public AdminAction(String time, String action, String detail) {
        this.time = time;
        this.action = action;
        this.detail = detail;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
