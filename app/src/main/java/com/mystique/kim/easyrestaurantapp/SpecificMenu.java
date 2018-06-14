package com.mystique.kim.easyrestaurantapp;

/**
 * Created by Kim on 7/12/2017.
 */


public class SpecificMenu {
    private String description, availability, specificImage;
    double price;
    int qty;

    public SpecificMenu() {
    }

    public SpecificMenu(String description, String availability, String specificImage, double price) {
        this.description = description;
        this.availability = availability;
        this.specificImage = specificImage;
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getSpecificImage() {
        return specificImage;
    }

    public void setSpecificImage(String specificImage) {
        this.specificImage = specificImage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}