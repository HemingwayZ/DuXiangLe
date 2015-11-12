package com.zhm.duxiangle;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 图片放大
 */
public class WebImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_image);
        String url = getIntent().getStringExtra("url");
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(true);
        // 设置支持缩放
        webView.getSettings().setSupportZoom(true);
        webView.loadUrl(url);
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
    }
}
