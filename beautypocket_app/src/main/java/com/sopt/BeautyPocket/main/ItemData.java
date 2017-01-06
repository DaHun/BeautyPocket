package com.sopt.BeautyPocket.main;

/**
 * Created by dhlee on 2016-12-27.
 */

public class ItemData {
    public String name;
    public String price;
    public int icon;
    public int _id;
    public int img_marker;
    public double latitude;
    public double longitude;

    public int getImg_marker() {
        return img_marker;
    }

    public void setImg_marker(int img_marker) {
        this.img_marker = img_marker;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public ItemData(int _id, int icon, String name) {
        this._id = _id;
        this.icon = icon;
        this.name = name;
    }
    public ItemData(int _id, int icon, String name,int img_marker) {
        this._id = _id;
        this.icon = icon;
        this.name = name;
        this.img_marker = img_marker;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }
}