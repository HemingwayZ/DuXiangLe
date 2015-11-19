package com.zhm.duxiangle;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseRequest;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.api.ShareApi;
import com.zhm.duxiangle.bean.Constant;
import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.bean.UserInfo;
import com.zhm.duxiangle.fragment.FindFragment;
import com.zhm.duxiangle.fragment.HomeFragment;
import com.zhm.duxiangle.fragment.UserListFragment;
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.SpUtil;
import com.zhm.duxiangle.utils.ToastUtils;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static boolean updateUserInfo = false;
    public final int REQUEST_CODE = 200;
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

    //用户信息
    private UserInfo userinfo;
    private User user;
    private Tencent mTencent;
    private IWeiboShareAPI mWeiboShareAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constant.SINA_APP_KEY);
        mWeiboShareAPI.registerApp();    // 将应用注册到微博客户端

//        overridePendingTransition(R.anim.hm_base_slide_right_in, 0);
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
        if (user == null)
            getUser();
        super.onStart();
    }


    private void getUser() {
        //获取用户信息
        String json = SpUtil.getSharePerference(getApplicationContext()).getString("user", "");
        if (!TextUtils.isEmpty(json)) {
            user = GsonUtils.getInstance().json2Bean(json, User.class);
            if (user != null) {

                getUserInfo(user.getUserId());
//                tvUsername.setText(user.getUsername());
//                tvDesc.setText(user.getPassword());
                if (user.getToken() != null) {
                    initRong(user.getToken());
                }
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    private void getUserInfo(int userId) {
        userinfo = new UserInfo();
        userinfo.setUserId(userId);
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "userinfo");
        params.addBodyParameter("userid", String.valueOf(userId));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getUserInfoApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(MainActivity.class.getSimpleName(), result);
                if ("no found".equals(result)) {
                    tvUsername.setText("");
                    tvDesc.setText("");
                    ivUser.setImageResource(R.drawable.book_cover_default);
                    return;
                }
                if ("action".equals(result)) {
                    return;
                }
                userinfo = GsonUtils.getInstance().json2Bean(result, UserInfo.class);
                if (userinfo == null) {
                    return;
                }

                tvUsername.setText(userinfo.getNickname());
                tvDesc.setText(userinfo.getDescrib());
                if (userinfo.getAvatar() != null)
                    if (user != null && user.getOpenid() != null) {
                        BitmapUtils.getInstance(getApplicationContext()).setAvatarWithoutReflect(ivUser, userinfo.getAvatar());
                    } else {
                        BitmapUtils.getInstance(getApplicationContext()).setAvatarWithoutReflect(ivUser, DXLApi.BASE_URL + userinfo.getAvatar());
                    }
            }

            @Override
            public void onFailure(HttpException error, String msg) {

            }
        });
    }

    private void initRong(final String token) {
        //访问融云服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * IMKit SDK调用第二步
                 *
                 * 建立与服务器的连接
                 *
                 */
                RongIM.connect(token, new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                        //Connect Token 失效的状态处理，需要重新获取 Token
                        ToastUtils.showToast(getApplicationContext(), "融云服务器异常");
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    /**
                     * 连接融云成功
                     */
                    @Override
                    public void onSuccess(String userId) {
                        Log.e("MainActivity", "——onSuccess— -userid" + userId);
                        /**
                         * 启动单聊
                         * context - 应用上下文。
                         * targetUserId - 要与之聊天的用户 Id。
                         * title - 聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
                         */
                        //{"code":200,"userId":"2462","token":"4Cp7WFQq92h1xjdmdaL5AXM//2Y39LDCnuxr2xdDagUSew9ILDZp6tvcV6rRhvbxbTnqk7cS56XBpjxS+NU4Ng=="}
                        //{"code":200,"userId":"1","token":"8FQcKXFvWDqN2j3qZWDA5nM//2Y39LDCnuxr2xdDagUSew9ILDZp6n9+OUnzkJ/4/W8bX6Y2cB4VGTWNrvchrA=="}
                        if (RongIM.getInstance() != null) {

                            /**
                             * 刷新用户缓存数据。
                             *
                             * @param userInfo 需要更新的用户缓存数据。
                             */
                            if (!TextUtils.isEmpty(userinfo.getAvatar()))// host null --url拼接导致的host为null
                                if (userinfo.getAvatar().startsWith("http")) {
                                    RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(String.valueOf(userinfo.getUserId()), userinfo.getNickname(), Uri.parse(userinfo.getAvatar())));
                                } else {
                                    RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(String.valueOf(userinfo.getUserId()), userinfo.getNickname(), Uri.parse(DXLApi.BASE_URL + userinfo.getAvatar())));
                                }
                        } else {
                        }
                        //回话列表
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        Log.e("MainActivity", "——onError— -" + errorCode);
                    }
                });
            }
        }).start();
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
            new AlertDialog.Builder(this).setTitle("是否退出").setIcon(
                    R.drawable.ic_launcher).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }

    }

    private void dialog() {
        new AlertDialog.Builder(this).setTitle("是否退出").setIcon(
                R.drawable.ic_launcher).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
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
        if (updateUserInfo == true) {
            updateUserInfo = false;
            getUser();
        }
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
            if (user == null) {
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            intent.setClass(MainActivity.this, FriendsActivity.class);
            intent.putExtra("userid", String.valueOf(user.getUserId()));
            startActivity(intent);
            overridePendingTransition(R.anim.hm_base_slide_right_in,
                    0);
            return true;
        } else if (id == R.id.nav_slideshow) {//消息
            //融云即时通讯
            startActivity(new Intent(MainActivity.this, ConversationListActivity.class));
            return true;
        } else if (id == R.id.nav_share) {
//                startActivity(new Intent(MainActivity.this,MessageActivity.class));
            dialogShare();

        } else if (id == R.id.nav_clean) {
//            BitmapUtils.getInstance(getApplication()).cleanCache();
            getUser();

            return true;
        } else if (id == R.id.nav_login) {
            //消除本地缓存
            SpUtil.cleanUser(SpUtil.getSharePerference(getApplicationContext()));
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            SpUtil.cleanUser(SpUtil.getSharePerference(getApplicationContext()));
            finish();
        } else if (id == R.id.nav_aboutus) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AboutUsActivity.class);
            startActivity(intent);

            return true;
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
        switch (requestCode) {
            case REQUEST_CODE:
                //扫描结果 data带有扫描结果集
                data.setClass(MainActivity.this, BookDetailActivity.class);
                startActivity(data);
                break;
        }
        if (null != mTencent)
            mTencent.onActivityResult(requestCode, resultCode, data);
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

    public int getViewPgeCurrentItem() {
        return viewPager.getCurrentItem();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivUser:
                if (user == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, UserInfoDetailActivity.class);
                intent.putExtra("isMy", true);
                intent.putExtra("userinfo", userinfo);
                startActivity(intent);

                break;
            default:
        }
    }

    private void dialogShare() {
        String[] str;
        str = new String[]{"分享到微信朋友圈", "分享给微信朋友", "分享到qq", "分享到新浪微博"};
        new android.app.AlertDialog.Builder(this).setTitle("分享").setIcon(
                R.drawable.ic_launcher).setSingleChoiceItems(
                str, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //1：朋友圈
                                        ShareApi.getInstance(getApplicationContext()).wechatShare(1, DXLApi.getIndexApi());
                                    }

                                }).start();
                                break;
                            case 1:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //1：朋友圈
                                        ShareApi.getInstance(getApplicationContext()).wechatShare(0, DXLApi.getIndexApi());
                                    }

                                }).start();
                                break;
                            case 2:
                                qqShare();
                                break;
                            case 3:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMultiMessage(true);
                                    }
                                }).start();
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    //qq分享
    public void qqShare() {
        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, MainActivity.this);
        Bundle bundle = new Bundle();
        //这条分享消息被好友点击后的跳转URL。
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, DXLApi.getIndexApi());
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_	SUMMARY不能全为空，最少必须有一个是有值的。
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "读享乐");
        //分享的图片URL
        if (userinfo.getAvatar().startsWith("http")) {
            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
                    userinfo.getAvatar());
        } else {
            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
                    DXLApi.BASE_URL + userinfo.getAvatar());
        }
        //分享的消息摘要，最长50个字
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, "zhuanghm");
        //手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, "读享乐");
        ////标识该消息的来源应用，值为应用名称+AppId。
        //        bundle.putString(QQShare.,"星期几" + Constant.QQ_APP_ID);

        mTencent.shareToQQ(this, bundle, new IUiListener() {
            @Override
            public void onComplete(Object o) {
                ToastUtils.showToast(getApplicationContext(), "QQ分享完成");
            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    //微博分享--创建要分享的内容
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = getSharedText();
        return textObject;
    }

    private String getSharedText() {
        return null;
    }

    private void sendMultiMessage(boolean hasWebpage) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj();
        }
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        // 2. 初始化从第三方到微博的消息请求
        boolean b = mWeiboShareAPI.sendRequest(MainActivity.this, request);//发送请求消息到微博，唤起微博分享界面
    }


    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = "读享乐";
        mediaObject.description = "读书 分享 快乐";

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        bitmap = BitmapUtils.compressImage(bitmap);
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = DXLApi.getIndexApi();
        mediaObject.defaultText = "读享乐";
        return mediaObject;
    }
}
