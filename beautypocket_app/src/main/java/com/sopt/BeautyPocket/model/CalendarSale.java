package com.sopt.BeautyPocket.model;

/**
 * Created by user on 2017-01-03.
 */

public class CalendarSale {
    public int sale_id;
    public int brand_id;
    public String sale_info;
    public String sale_day;
    public String sale_end;

    public CalendarSale(int brand_id, String sale_day, String sale_end, int sale_id, String sale_info) {
        this.brand_id = brand_id;
        this.sale_day = sale_day;
        this.sale_end = sale_end;
        this.sale_id = sale_id;
        this.sale_info = sale_info;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
    }

    public String getSale_day() {
        return sale_day;
    }

    public void setSale_day(String sale_day) {
        this.sale_day = sale_day;
    }

    public String getSale_end() {
        return sale_end;
    }

    public void setSale_end(String sale_end) {
        this.sale_end = sale_end;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public String getSale_info() {
        return sale_info;
    }

    public void setSale_info(String sale_info) {
        this.sale_info = sale_info;
    }
}
