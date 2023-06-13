package com.example.danut.restaurant;

import com.google.firebase.database.Exclude;

public class Users {

    private String user_picture;
    private String user_firstName;
    private String user_lastName;
    private String user_phone;
    private String user_email;
    private String user_key;

    public Users(){

    }

    public Users(String user_picture, String user_firstName, String user_lastName, String user_phone, String user_email) {
        this.user_picture = user_picture;
        this.user_firstName = user_firstName;
        this.user_lastName = user_lastName;
        this.user_phone = user_phone;
        this.user_email = user_email;
    }

    public String getUser_picture() {
        return user_picture;
    }

    public void setUser_picture(String user_picture) {
        this.user_picture = user_picture;
    }

    public String getUser_firstName() {
        return user_firstName;
    }

    public void setUser_firstName(String user_firstName) {
        this.user_firstName = user_firstName;
    }

    public String getUser_lastName() {
        return user_lastName;
    }

    public void setUser_lastName(String user_lastName) {
        this.user_lastName = user_lastName;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    @Exclude
    public String getUser_key() {
        return user_key;
    }

    @Exclude
    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }
}
