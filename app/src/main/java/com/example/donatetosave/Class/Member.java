package com.example.donatetosave.Class;

public class Member {
private long role;
private String uid, name, email;

    public Member() {
    }
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    public String getUid() {
        return uid;
    }
    public long getRole() {
        return role;
    }
    public Member(long role, String uid, String name, String email) {
        this.role = role;
        this.uid = uid;
        this.name = name;
        this.email = email;
    }





}
