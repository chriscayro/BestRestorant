package com.mystique.kim.easyrestaurantapp;

/**
 * Created by Kim on 7/12/2017.
 */

public class CategoryMenu {
    private String name,image;

    public CategoryMenu() {
    }

    public CategoryMenu(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
