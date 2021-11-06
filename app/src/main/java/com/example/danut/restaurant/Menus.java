package com.example.danut.restaurant;

import com.google.firebase.database.Exclude;

/**
 * Created by danut on 14/03/2018.
 */

public class Menus {

    private String menu_Name;
    private String menu_Description;
    private double menu_Price;
    private String menu_Image;
    private String restaurant_Name;
    private String restaurant_Key;
    private String menu_Key;

    public Menus() {

    }

    public Menus(String menu_Name, String menu_Description, double menu_Price, String menu_Image, String restaurant_Name, String restaurant_Key, String menu_Key) {
        this.menu_Name = menu_Name;
        this.menu_Description = menu_Description;
        this.menu_Price = menu_Price;
        this.menu_Image = menu_Image;
        this.restaurant_Name = restaurant_Name;
        this.restaurant_Key = restaurant_Key;
        this.menu_Key = menu_Key;
    }

    public String getMenu_Name() {
        return menu_Name;
    }

    public void setMenu_Name(String menu_Name) {
        this.menu_Name = menu_Name;
    }

    public String getMenu_Description() {
        return menu_Description;
    }

    public void setMenu_Description(String menu_Description) {
        this.menu_Description = menu_Description;
    }

    public double getMenu_Price() {
        return menu_Price;
    }

    public void setMenu_Price(double menu_Price) {
        this.menu_Price = menu_Price;
    }

    public String getMenu_Image() {
        return menu_Image;
    }

    public void setMenu_Image(String menu_Image) {
        this.menu_Image = menu_Image;
    }

    public String getRestaurant_Name() {
        return restaurant_Name;
    }

    public void setRestaurant_Name(String restaurant_Name) {
        this.restaurant_Name = restaurant_Name;
    }

    public String getRestaurant_Key() {
        return restaurant_Key;
    }

    public void setRestaurant_Key(String restaurant_Key) {
        this.restaurant_Key = restaurant_Key;
    }

    public String getMenu_Key() {
        return menu_Key;
    }

    public void setMenu_Key(String menu_Key) {
        this.menu_Key = menu_Key;
    }
}
