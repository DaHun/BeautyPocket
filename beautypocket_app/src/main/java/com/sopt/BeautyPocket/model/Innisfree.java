package com.sopt.BeautyPocket.model;
import java.io.Serializable;

public class Innisfree implements Serializable{
    int pro_id;
    String pro_name;
    int pro_price;
    String pro_image;
    String pro_url;



    public Innisfree(int pro_id, String pro_name, int pro_price, String pro_image, String pro_url) {
        this.pro_id = pro_id;
        this.pro_name = pro_name;
        this.pro_price = pro_price;
        this.pro_image = pro_image;
        this.pro_url=pro_url;

    }

    public int getPro_id() {
        return pro_id;
    }

    public String getPro_name() {
        return pro_name;
    }

    public int getPro_price() {
        return pro_price;
    }

    public String getPro_image() {
        return pro_image;
    }

    public String getPro_url() {
        return pro_url;
    }

    public void setPro_id(int pro_id) {
        this.pro_id = pro_id;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public void setPro_price(int pro_price) {
        this.pro_price = pro_price;
    }

    public void setPro_image(String pro_image) {
        this.pro_image = pro_image;
    }
    public void setPro_url(String pro_url) {
        this.pro_url = pro_url;
    }

}