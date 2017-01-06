package com.sopt.BeautyPocket.login;

/**
 * Created by user on 2016-12-31.
 */

public class Login {

    private int _id;
    private String kakao_id;
    private String kakap_name;
    public Login() {
        this._id = 0;
        this.kakao_id = "";
        this.kakap_name = "";
    }

    public Login(int _id, String kakao_id, String kakap_name) {
        this._id = _id;
        this.kakao_id = kakao_id;
        this.kakap_name = kakap_name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getKakao_id() {
        return kakao_id;
    }

    public void setKakao_id(String kakao_id) {
        this.kakao_id = kakao_id;
    }

    public String getKakap_name() {
        return kakap_name;
    }

    public void setKakap_name(String kakap_name) {
        this.kakap_name = kakap_name;
    }
}
