package com.sopt.BeautyPocket.detail;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.sopt.BeautyPocket.R;

public class WebViewActivity extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Intent intent=getIntent();
        webView=(WebView)findViewById(R.id.webview);
        webView.loadUrl(intent.getStringExtra("url"));

    }
}