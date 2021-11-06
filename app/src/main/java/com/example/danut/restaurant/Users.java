package com.example.danut.restaurant;

public class Users {

    private String firstName_User;
    private String lastName_User;
    private String phoneNum_User;
    private String email_User;

    public Users(){

    }

    public Users(String firstName_User, String lastName_User, String phoneNum_User, String email_User) {
        this.firstName_User = firstName_User;
        this.lastName_User = lastName_User;
        this.phoneNum_User = phoneNum_User;
        this.email_User = email_User;
    }

    public String getFirstName_User() {
        return firstName_User;
    }

    public void setFirstName_User(String firstName_User) {
        this.firstName_User = firstName_User;
    }

    public String getLastName_User() {
        return lastName_User;
    }

    public void setLastName_User(String lastName_User) {
        this.lastName_User = lastName_User;
    }

    public String getPhoneNum_User() {
        return phoneNum_User;
    }

    public void setPhoneNum_User(String phoneNum_User) {
        this.phoneNum_User = phoneNum_User;
    }

    public String getEmail_User() {
        return email_User;
    }

    public void setEmail_User(String email_User) {
        this.email_User = email_User;
    }
}
