package com.zhm.duxiangle;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.SpUtil;
import com.zhm.duxiangle.view.SlidingBackLayout;

/**
 * 想要实现向右滑动删除Activity效果只需要继承SwipeBackActivity即可，如果当前页面含有ViewPager
 * 只需要调用SwipeBackLayout的setViewPager()方法即可
 *
 * @author zhm
 * @version 2015-7-11
 */
public class SlidingBackActivity extends AppCompatActivity {
    protected SlidingBackLayout layout;
    public User user;

    /**
     * 获取用户
     */
    private void getUser() {
        //获取用户信息
        String json = SpUtil.getSharePerference(getApplicationContext()).getString("user", "");
        if (!TextUtils.isEmpty(json)) {
            user = GsonUtils.getInstance().json2Bean(json, User.class);
            if (user != null) {
            } else {
                Intent intent = new Intent(SlidingBackActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        getUser();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.hm_base_slide_right_in,
                0);
        super.onCreate(savedInstanceState);
        //1需要继承AppCompatActivity
        //2需要将背景弄成透明
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        layout = (SlidingBackLayout) LayoutInflater.from(this).inflate(
                R.layout.hm_base_activity, null);
        layout.attachToActivity(this);
    }

    // Press the back button in mobile phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.hm_base_slide_right_out);
    }

    public void hide(View v) {
        InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(getApplication().INPUT_METHOD_SERVICE);
//                　　//显示键盘
//                　　imm.showSoftInput(editText, 0);
        //隐藏键盘
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}