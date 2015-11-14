package com.zhm.duxiangle;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zhm.duxiangle.api.ShareApi;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ShareApi.getInstance(AboutUsActivity.this).regToWx();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        final String url = "http://120.25.201.60/DuXiangLeServer/index/index.html";
        final WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(true);
        // 设置支持缩放
        webView.getSettings().setSupportZoom(true);
        webView.loadUrl(url);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);设置单行
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });

        findViewById(R.id.btnBig).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareApi.getInstance(AboutUsActivity.this).wechatShare(1, url);
            }
        });
    }
}
