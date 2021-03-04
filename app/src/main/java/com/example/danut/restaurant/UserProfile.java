package com.example.danut.restaurant;

import com.google.firebase.database.FirebaseDatabase;

public class UserProfile {

    public String First_Name;
    public String Last_Name;
    public String Phone_Number;
    public String Email_Address;

    public UserProfile(){

    }

    public UserProfile(String first_Name, String last_Name, String phone_Number, String email_Address) {
        First_Name = first_Name;
        Last_Name = last_Name;
        Phone_Number = phone_Number;
        Email_Address = email_Address;
    }

}
