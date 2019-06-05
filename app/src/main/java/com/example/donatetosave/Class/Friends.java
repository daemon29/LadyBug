package com.example.donatetosave.Class;

public class Friends {
    private String name,image_url;
    private String uid;

    public String getUid() {
        return uid;
    }

    public Friends() {
    }

    public String getName() {
        return name;
    }

    public String getImage_url() {
        return image_url;
    }

    public Friends(String name, String image_url,String uid) {
        this.name = name;
        this.image_url = image_url;
        this.uid=uid;
    }
}
