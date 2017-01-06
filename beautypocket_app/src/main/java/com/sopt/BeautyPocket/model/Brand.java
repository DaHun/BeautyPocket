package com.sopt.BeautyPocket.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dhlee on 2016-12-31.
 */

public class Brand implements Serializable{
    int brand_id;
    String brand_name;
    int total;
    int count;
    ArrayList<Brand> brand;

    public ArrayList<Brand> getBrand() {
        return brand;
    }

    public void setBrand(ArrayList<Brand> brand) {
        this.brand = brand;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }





    public Brand(int brand_id, String brand_name) {
        this.brand_id = brand_id;
        this.brand_name = brand_name;
    }

    public Brand(ArrayList<Brand> brand, int total, int count){
        this.brand = brand;
        this.count = count;
        this.total = total;
    }
    public Brand(int brand_id){
        this.brand_id= brand_id;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }
}
