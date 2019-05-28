package com.example.donatetosave;

public class Notification {
    private int type;
    private String title, url;

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Notification(int type, String title, String url) {
        this.type = type;
        this.title = title;
        this.url = url;
    }

    public Notification() {
    }
}
