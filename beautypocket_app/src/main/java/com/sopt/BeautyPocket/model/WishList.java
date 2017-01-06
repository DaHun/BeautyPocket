package com.sopt.BeautyPocket.model;

import java.io.Serializable;

/**
 * Created by dhlee on 2016-12-31.
 */

public class WishList implements Serializable{
    int wish_id;
    String user_id;
    int brand_id;
    int pro_id;
    String wish_title;
    int wish_price;
    String wish_image;
    String wish_memo;
    public boolean isCheck = false;

    String pro_name;
    int pro_price;
    String pro_image;
    String pro_url;

    //temp image
    public int temp_img;
    //temp 생성자
    public WishList()
    {
    }

    // 수정 화면으로 인텐트 해주기 위한 객체
    public WishList(int brand_id, int pro_id, int wish_id, String wish_title, int wish_price, String wish_memo, String wish_image) {
        this.brand_id = brand_id;
        this.pro_id = pro_id;
        this.wish_id = wish_id;
        this.wish_title = wish_title;
        this.wish_price = wish_price;
        this.wish_memo = wish_memo;
        this.wish_image = wish_image;
    }

    //직접작성위시리스트 객체
    public WishList(String user_id, int brand_id, String wish_title,int wish_price,String wish_image,String wish_memo){
        this.user_id = user_id;
        this.brand_id = brand_id;
        this. wish_title = wish_title;
        this.wish_price = wish_price;
        this.wish_image = wish_image;
        this.wish_memo = wish_memo;

    }
    //검색작성 위시리스트 객체
    public WishList(String user_id, int pro_id, int brand_id, String wish_memo, String pro_url){
        this.user_id = user_id;
        this.pro_id = pro_id;
        this.brand_id = brand_id;
        this.wish_memo = wish_memo;
        this.pro_url=pro_url;
    }
    //위시리스트 수정 객체(직접작성)
    public WishList(int wish_id,String wish_title, int wish_price, String wish_image, String wish_memo){
        this.wish_id = wish_id;
        this.wish_title = wish_title;
        this.wish_price = wish_price;
        this.wish_image = wish_image;
        this.wish_memo = wish_memo;
    }

    //위시리스트 수정 객체(검색작성)
    public WishList(int wish_id,String wish_memo){
        this.wish_id = wish_id;
        this.wish_memo = wish_memo;
    }

    //    사용자의 브랜드별 위시리스트 불러오기
//    전체 위시리스트 불러오기
    public WishList(int wish_id, String user_id, int brand_id, int pro_id, String wish_title,
                    int wish_price, String wish_image, String wish_memo) {
        this.wish_id = wish_id;
        this.user_id = user_id;
        this.brand_id = brand_id;
        this.pro_id = pro_id;
        this.wish_title = wish_title;
        this.wish_price = wish_price;
        this.wish_image = wish_image;
        this.wish_memo = wish_memo;
    }

    //    검색 response 객체
    public WishList(String pro_url, int pro_id, String pro_name, int pro_price, String pro_image) {
        this.pro_url=pro_url;
        this.pro_id = pro_id;
        this.pro_name = pro_name;
        this.pro_price = pro_price;
        this.pro_image = pro_image;
    }

    public int getWish_id() {
        return wish_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public int getPro_id() {
        return pro_id;
    }

    public String getWish_title() {
        return wish_title;
    }

    public int getWish_price() {
        return wish_price;
    }

    public String getWish_image() {
        return wish_image;
    }

    public String getWish_memo() {
        return wish_memo;
    }

    public void setWish_id(int wish_id) {
        this.wish_id = wish_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
    }

    public void setPro_id(int pro_id) {
        this.pro_id = pro_id;
    }

    public void setWish_title(String wish_title) {
        this.wish_title = wish_title;
    }

    public void setWish_price(int wish_price) {
        this.wish_price = wish_price;
    }

    public void setWish_image(String wish_image) {
        this.wish_image = wish_image;
    }

    public void setWish_memo(String wish_memo) {
        this.wish_memo = wish_memo;
    }

    public String getPro_image() {
        return pro_image;
    }

    public void setPro_image(String pro_image) {
        this.pro_image = pro_image;
    }

    public String getPro_name() {
        return pro_name;
    }

    public void setPro_name(String pro_name) {
        this.pro_name = pro_name;
    }

    public int getPro_price() {
        return pro_price;
    }

    public void setPro_price(int pro_price) {
        this.pro_price = pro_price;
    }

    public String getPro_url() {
        return pro_url;
    }

}