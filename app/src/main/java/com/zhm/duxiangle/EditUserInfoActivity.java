package com.zhm.duxiangle;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.bean.UserInfo;

@ContentView(R.layout.activity_edit_user_info)
public class EditUserInfoActivity extends SlidingBackActivity implements View.OnClickListener {

    @ViewInject(R.id.app_bar)
    private AppBarLayout appBarLayout;
    //标题栏
    @ViewInject(R.id.ibBack)
    private ImageButton ibBack;
    @ViewInject(R.id.tvTitle)
    private TextView tvTitle;

    //个人信息
    @ViewInject(R.id.etDesc)
    private EditText etDesc;
    @ViewInject(R.id.etCreated)
    private EditText etCreated;
    @ViewInject(R.id.etNickname)
    private EditText etNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Snackbar.make(view, "修改成功", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        UserInfo userInfo = (UserInfo) getIntent().getSerializableExtra("userinfo");
        TextView tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setText(userInfo.toString());

        ibBack.setOnClickListener(this);
        tvTitle.setText("修改-" + userInfo.getNickname());

        etNickname.setText(userInfo.getNickname());
        etCreated.setText(userInfo.getCreated());
        etDesc.setText(userInfo.getDescrib());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibBack:
                onBackPressed();
                break;
        }
    }
}
