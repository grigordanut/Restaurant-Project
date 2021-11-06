package com.example.danut.restaurant;

import java.util.List;

/**
 * Created by danut on 25/03/2018.
 */

public class Restaurants {
    private String rest_Name;
    private String rest_Address;
    private String rest_Key;

    public Restaurants(){

    }

    public Restaurants(String rest_Name, String rest_Address, String rest_Key) {
        this.rest_Name = rest_Name;
        this.rest_Address = rest_Address;
        this.rest_Key = rest_Key;
    }

    public String getRest_Name() {
        return rest_Name;
    }

    public void setRest_Name(String rest_Name) {
        this.rest_Name = rest_Name;
    }

    public String getRest_Address() {
        return rest_Address;
    }

    public void setRest_Address(String rest_Address) {
        this.rest_Address = rest_Address;
    }

    public String getRest_Key() {
        return rest_Key;
    }

    public void setRest_Key(String rest_Key) {
        this.rest_Key = rest_Key;
    }
}
