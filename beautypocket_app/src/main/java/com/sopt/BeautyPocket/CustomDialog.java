package com.sopt.BeautyPocket;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends Dialog {

    CustomDialog dialog;
    Button btnCancel, btnOk;
    TextView tvTitle, tvContent;

    private View.OnClickListener mLeftClickListener=null;
    private View.OnClickListener mRightClickListener=null;

    public CustomDialog(Context context, Activity activity) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_custom);

        tvContent = (TextView) findViewById(R.id.txt_content);
        btnCancel = (Button) findViewById(R.id.btn_left);
        btnOk = (Button) findViewById(R.id.btn_right);
        tvTitle = (TextView)findViewById(R.id.txt_title);
        dialog = this;

        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null && mRightClickListener != null) {
            btnCancel.setOnClickListener(mLeftClickListener);
            btnOk.setOnClickListener(mRightClickListener);
        } else if (mLeftClickListener == null
                && mRightClickListener != null) {
            btnOk.setOnClickListener(mRightClickListener);
        }
    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CustomDialog(Context context, View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mRightClickListener = singleListener;
        this.mLeftClickListener = null;
    }

    public void cancelInVisible(){
        btnCancel.setVisibility(View.GONE);
    }

    // 클릭버튼이 확인과 취소 두개일때 생성자 함수로 이벤트를 받는다
    public CustomDialog(Context context, View.OnClickListener leftListener,
                        View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;
    }

    public CustomDialog() {
        super(null);
    }


    public void setContent(String msg) {
        tvContent.setText(msg);
    }

    public void setTitle(String msg) {
        tvTitle.setText(msg);
    }


}