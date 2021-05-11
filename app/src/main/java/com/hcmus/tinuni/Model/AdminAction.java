package com.hcmus.tinuni.Model;

public class AdminAction {
    private String id;
    private String time;
    private String action;

    public AdminAction(String id, String time, String action) {
        this.id = id;
        this.time = time;
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "AdminAction{" +
                "id='" + id + '\'' +
                ", time='" + time + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
