package com.mystique.kim.easyrestaurantapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kim on 7/18/2017.
 */

public class CartModel implements Parcelable {

    String desc, image;
    double price;

    public CartModel() {
    }

    public CartModel(String desc, String image, double price) {
        this.desc = desc;
        this.image = image;
        this.price = price;
    }

    protected CartModel(Parcel in) {
        desc = in.readString();
        image = in.readString();
        price = in.readDouble();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(desc);
        parcel.writeString(image);
        parcel.writeDouble(price);
    }

    public static final Creator<CartModel> CREATOR = new Creator<CartModel>() {
        @Override
        public CartModel createFromParcel(Parcel in) {
            return new CartModel(in);
        }

        @Override
        public CartModel[] newArray(int size) {
            return new CartModel[size];
        }
    };
}
