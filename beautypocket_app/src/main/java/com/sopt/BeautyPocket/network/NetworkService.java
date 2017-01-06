package com.sopt.BeautyPocket.network;

import com.sopt.BeautyPocket.model.Brand;
import com.sopt.BeautyPocket.model.BrandLocation;
import com.sopt.BeautyPocket.model.CalendarSale;
import com.sopt.BeautyPocket.model.Innisfree;
import com.sopt.BeautyPocket.model.MainSale;
import com.sopt.BeautyPocket.model.Store;
import com.sopt.BeautyPocket.model.User;
import com.sopt.BeautyPocket.model.WishId;
import com.sopt.BeautyPocket.model.WishList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by user on 2016-12-31.
 */

public interface NetworkService {


    /**
     * 익명게시글 목록 가져오기 : GET, /posts
     * 익명게시글 상태보기: GET, /posts{id}  =>{id}라고하면 동적으로 변한다는것임
     * 익명게시글 등록하기: POST, /posts
     */
    //로그인
    @POST("/login")
    Call<Void> login(@Body User user);

    //위시리스트추가1(사용자가 직접사진추가)
    @Multipart
    @POST("/wishlists/add")
    Call<WishList> add_wishList(@Part MultipartBody.Part file,
                              @Part("user_id") RequestBody user_id,
                              @Part("brand_id") RequestBody brand_id,
                              @Part("wish_title") RequestBody wish_title,
                              @Part("wish_price") RequestBody wish_price,
                              @Part("wish_memo") RequestBody wish_memo);

    //위시리스트추가2(사용자가 검색해서 리스트로 추가)
    @POST("/wishlists/add")
    Call<WishList> add_searchWishList(@Body WishList wishList);

    //위시리스트 불러오기(메인에서 브랜드리스트출력)
    @GET("/wishlists/load/{user_id}")
    Call<Brand> get_brandList(@Path("user_id") String user_id);

    //위시리스트 불러오기2(브랜드별 위시리스트)
    @GET("/wishlists/load2/{user_id}/{brand_id}")
    Call<ArrayList<WishList>> getWishlist_brand(@Path("user_id") String user_id,@Path("brand_id") int brand_id);

    //전체 위시리스트 불러오기
    @GET("/wishlists/all/{user_id}")
    Call<ArrayList<WishList>> getWishlistAll(@Path("user_id") String user_id);

    // 위시리스트 불러오기(상세정보)
    @GET("/wishlists/specific/{wish_id}")
    Call<WishList> getWishlist(@Path("wish_id") int wish_id);

    //    위시리스트 수정1
    @Multipart
    @POST("/wishlists/modify")
    Call<Object> modifyWishList1(@Part MultipartBody.Part file,
                                 @Part("wish_id") RequestBody wish_id,
                                 @Part("wish_title") RequestBody wish_title,
                                 @Part("wish_price") RequestBody wish_price,
                                 @Part("wish_memo") RequestBody wish_memo);

    //    위시리스트 수정2
    @POST("/wishlists/modify")
    Call<Object> modifyWishList2(@Body WishList wishList);

    //위시리스트 삭제
    @GET("/wishlists/delete/{wish_id}")
    Call<Object>  deleteWishList(@Path("wish_id") int wish_id);

    //제품 검색
    @GET("/wishlists/search/{pro_name}/{brand_id}")
    Call<ArrayList<Innisfree>> getInnisfree(@Path("pro_name") String pro_name,@Path("brand_id") int brand_id);

    //브랜드별지도
    @POST("/map/brandMap")
    Call<ArrayList<Store>> getBrandMap(@Body BrandLocation brandLocation);

    //주변에 있는 모든 브랜드 지도
    @POST("/map/totalMap")
    Call<ArrayList<Store>> getTotalMap(@Body BrandLocation brandLocation);
    //위치전송(알림서비스)
    @POST("/map/alertLoc")
    Call<Store> getAlarm(@Body BrandLocation brandLocation);

    //메인 세일 정보랜덤 5개  불러오기
    @GET("/mainSale/main")
    Call<ArrayList<MainSale>> getMainSale();
    //전체 세일 리스트 불러오기
    @GET("/mainSale/main2")
    Call<ArrayList<MainSale>> getMainSale2();
    //달력세일정보 불러오기
    @GET("/mainSale/calendar/")
    Call<ArrayList<CalendarSale>> getCalendarSale();



}

