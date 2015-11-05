package com.zhm.duxiangle;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhm.duxiangle.api.ShareApi;
import com.zhm.duxiangle.bean.Constant;
import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.fragment.FindFragment;
import com.zhm.duxiangle.fragment.HomeFragment;
import com.zhm.duxiangle.fragment.UserListFragment;
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.SpUtil;

import java.net.URL;
import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private int REQUEST_CODE = 200;
    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;
    @ViewInject(R.id.fabScan)
    private FloatingActionButton fabScan;

    //
    @ViewInject(R.id.tabLayout)
    private TabLayout tabLayout;
    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;

    //抽屉头部部分
    @ViewInject(R.id.ivUser)
    private ImageView ivUser;
    @ViewInject(R.id.tvUserName)
    private TextView tvUsername;
    @ViewInject(R.id.tvDesc)
    private TextView tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.hm_base_slide_right_in, 0);
        ShareApi.getInstance(getApplicationContext()).regToWx();
        ViewUtils.inject(this);

        setSupportActionBar(toolbar);
        fabScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "扫描二维码", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                callScanning("UTF-8");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ivUser.setOnClickListener(this);
        //初始化recycleView和相应标题
        initTab();
    }

    String[] mData;

    @Override
    protected void onStart() {
        //获取用户信息
        String json = SpUtil.getSharePerference(getApplicationContext()).getString("user", "");
        if (!TextUtils.isEmpty(json)) {
            User user = GsonUtils.getInstance().json2Bean(json, User.class);
            if (user != null) {
                tvUsername.setText(user.getUsername());
                tvDesc.setText(user.getPassword());
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
        super.onStart();
    }

    /**
     * 初始化tab菜单
     */
    private void initTab() {
        TabLayout.Tab tab1 = tabLayout.newTab().setText("首页");
        tabLayout.addTab(tab1);
        TabLayout.Tab tab2 = tabLayout.newTab().setText("发现");
        tabLayout.addTab(tab2);
        FindFragment findFragment = new FindFragment();
        HomeFragment homeFragment = new HomeFragment();
        UserListFragment userListFragment = new UserListFragment();
        final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

        fragmentArrayList.add(homeFragment);
        fragmentArrayList.add(findFragment);
        fragmentArrayList.add(userListFragment);
        mData = new String[]{"我的书库", "发现更多", "更多用户"};
        viewPager.setOffscreenPageLimit(2);//设置缓存页面为2
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public CharSequence getPageTitle(int position) {
                return mData[position];
            }

            @Override
            public Fragment getItem(int position) {
                return fragmentArrayList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentArrayList.size();
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            //定位操作
            Intent intent = new Intent(MainActivity.this, BaiduMapActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, BookDetailActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.hm_base_slide_right_in,
                    0);
            return false;
        } else if (id == R.id.nav_slideshow) {//消息
            //融云即时通讯
            startActivity(new Intent(MainActivity.this, ConversationListActivity.class));

        } /*else if (id == R.id.nav_manage) {

        }*/ else if (id == R.id.nav_share) {
//                startActivity(new Intent(MainActivity.this,MessageActivity.class));
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    ShareApi.getInstance(getApplicationContext()).share2WeChatWithWebUrl("http://120.25.201.60/ZL/ZL.html");
//                    ShareApi.getInstance(getApplicationContext()).share2WeChatWithImage(1);
                    ShareApi.getInstance(getApplicationContext()).wechatShare(1);
//                    wechatShare(1);
                }

            }).start();

        } else if (id == R.id.nav_send) {
//            Intent intent = new Intent(MainActivity.this, WeChatPicActivity.class);
//            startActivity(intent);
        } else if (id == R.id.nav_clean) {
            BitmapUtils.getInstance(getApplication()).cleanCache();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void wechatShare(int flag) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://120.25.201.60/ZL/ZL.html";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "Be Happy";
        msg.description = "Be Happy everyday";
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        ShareApi.api.sendReq(req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || null == data) {
            return;
        }
        //扫描结果 data带有扫描结果集
        data.setClass(MainActivity.this, BookDetailActivity.class);
        startActivity(data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 使用zxing进行扫描
     *
     * @param characterSet 扫描格式
     */
    private void callScanning(String characterSet) {
        Intent intent = new Intent();
        intent.setAction(Intents.Scan.ACTION);
        // intent.putExtra(Intents.Scan.MODE, Intents.Scan.QR_CODE_MODE);
        intent.putExtra(Intents.Scan.CHARACTER_SET, characterSet);
        intent.putExtra(Intents.Scan.WIDTH, 800);
        intent.putExtra(Intents.Scan.HEIGHT, 600);
        intent.putExtra(Intents.Scan.PROMPT_MESSAGE, "请于框内扫描条形码");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivUser:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;

            default:

        }
    }
}
