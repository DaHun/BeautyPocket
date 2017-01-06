package com.sopt.BeautyPocket.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import com.kakao.util.helper.log.Logger;
import com.sopt.BeautyPocket.R;
import com.sopt.BeautyPocket.application.ApplicationController;
import com.sopt.BeautyPocket.main.MainActivity;
import com.sopt.BeautyPocket.model.User;
import com.sopt.BeautyPocket.network.NetworkService;
import com.sopt.BeautyPocket.register.RegisterActivity;

import retrofit2.Call;
import retrofit2.Response;

public class KakaoSignupActivity extends Activity {
    public String KakaoID;
    public String kakaoNickname;
    public static String user_id;
    public static String user_name;

    NetworkService networkService;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MyTag", "kakaosignupActivity로 이동");
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_kakao_signup);
        requestMe();
    }
    protected void requestMe() { //유저의 정보를 받아오는 함수
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {} // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                KakaoID = String.valueOf(userProfile.getId());
                kakaoNickname = userProfile.getNickname();
                ApplicationController.user_id = String.valueOf(userProfile.getId());
                ApplicationController.user_name = userProfile.getNickname();
                Logger.d("UserProfile : " + userProfile);
                Log.d("MyTag","로그인 성공 메인액티비티");
                Log.d("MyTag","KaKaoID : " + KakaoID);
                Log.d("MyTag","kakaoNickname" + kakaoNickname);
                //자동로그인
                networkService = ApplicationController.getInstance().getNetworkService();

                user = new User(ApplicationController.user_id,ApplicationController.user_name);

                user.setUser_id(ApplicationController.user_id);
                user.setUser_name(ApplicationController.user_name);

                Call<Void> login = networkService.login(user);
                login.enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if(response.isSuccessful()){

                            redirectMainActivity(); // 로그인 성공시 MainActivity로
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Log.i("SOPT", "에러내용: " + t.getMessage());


                    }
                });

            }
        });
    }

    private void redirectMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
