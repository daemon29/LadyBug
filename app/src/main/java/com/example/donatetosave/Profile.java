package com.example.donatetosave;

public class Profile {
    private String email;
    private String image_url;
    private String name;
    private String organization;

    public Profile(String email, String image_url, String name, String organization) {
        this.email = email;
        this.image_url = image_url;
        this.name = name;
        this.organization = organization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}
