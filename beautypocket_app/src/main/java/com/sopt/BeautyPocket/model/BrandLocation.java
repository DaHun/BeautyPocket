package com.sopt.BeautyPocket.model;

/**
 * Created by user on 2016-12-31.
 */

public class BrandLocation {
    private int brand_id;
    private String user_id;
    private double store_latitude;
    private double store_longitude;

    public BrandLocation(String user_id,double store_latitude,double store_longitude){
        this.user_id = user_id;
        this.store_latitude = store_latitude;
        this.store_longitude = store_longitude;
    }

    public BrandLocation(int brand_id, double store_latitude, double store_longitude) {
        this.brand_id = brand_id;
        this.store_latitude = store_latitude;
        this.store_longitude = store_longitude;
    }

}
