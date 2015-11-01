package com.zhm.duxiangle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ConversationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        //继承的是ActionBarActivity，直接调用 自带的 Actionbar，下面是Actionbar 的配置，如果不用可忽略…
        getSupportActionBar().setTitle("聊天F");
        getSupportActionBar().setLogo(R.drawable.rc_bar_logo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.bg);


    }
}
