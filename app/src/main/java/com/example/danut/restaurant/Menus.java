package com.example.danut.restaurant;

import com.google.firebase.database.Exclude;

/**
 * Created by danut on 14/03/2018.
 */

public class Menus {

    private String itemName;
    private String itemDescription;
    private double itemPrice;
    private String itemImage;
    private String restaurantName;
    private String menuKey;

    public Menus(String itemName, String itemDescription, double itemPrice, String itemImage, String restaurantName) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemPrice = itemPrice;
        this.itemImage = itemImage;
        this.restaurantName = restaurantName;
    }

    public Menus() {

    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    @Exclude
    public String getMenuKey() {
        return menuKey;
    }

    @Exclude
    public void setMenuKey(String menuKey) {
        this.menuKey = menuKey;
    }

    public class NONE {
    }
}
