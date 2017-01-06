package com.sopt.BeautyPocket.model;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

/**
 * Created by dhlee on 2016-12-31.
 */

public class Store implements Serializable {
    int store_id;
    int brand_id;
    String store_name;
    String store_tel;
    String store_address;
    double store_latitude;
    double store_longitude;
    Marker marker;

    public Store(){
    }
    public Marker getMarker() {
        return marker;
    }


    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getStore_tel() {
        return store_tel;
    }

    public void setStore_tel(String store_tel) {
        this.store_tel = store_tel;
    }
    public Store(int brand_id, double store_latitude,double store_longitude){
        this.brand_id = brand_id;
        this.store_latitude = store_latitude;
        this.store_longitude = store_longitude;
    }

    public Store(int store_id, double store_latitude, double store_longitude, String store_name, String store_address, String store_tel){
        this.store_id = store_id;
        this.store_name = store_name;
        this.store_tel = store_tel;
        this.store_address = store_address;
        this.store_latitude = store_latitude;
        this.store_longitude = store_longitude;
    }


    public Store(int store_id, int brand_id, String store_name, String store_tel, String store_address,
                 double store_latitude, double store_longitude) {
        this.store_id = store_id;
        this.brand_id = brand_id;
        this.store_name = store_name;
        this.store_tel = store_tel;
        this.store_address = store_address;
        this.store_latitude = store_latitude;
        this.store_longitude = store_longitude;
    }

    public int getStore_id() {
        return store_id;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public String getSotre_tel() {
        return store_tel;
    }

    public String getStore_address() {
        return store_address;
    }

    public double getStore_latitude() {
        return store_latitude;
    }

    public double getStore_longitude() {
        return store_longitude;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public void setSotre_tel(String sotre_tel) {
        this.store_tel = sotre_tel;
    }

    public void setStore_address(String store_address) {
        this.store_address = store_address;
    }

    public void setStore_latitude(double store_latitude) {
        this.store_latitude = store_latitude;
    }

    public void setStore_longitude(double store_longitude) {
        this.store_longitude = store_longitude;
    }
}
