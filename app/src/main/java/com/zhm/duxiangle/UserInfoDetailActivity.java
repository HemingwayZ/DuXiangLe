package com.zhm.duxiangle;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.bean.Constant;
import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.bean.UserInfo;
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.SpUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

@ContentView(R.layout.activity_user_info_detail)
public class UserInfoDetailActivity extends SlidingBackActivity implements View.OnClickListener {
    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;
    @ViewInject(R.id.toolbar_layout)
    private CollapsingToolbarLayout collapsingToolbarLayout;
    @ViewInject(R.id.ibBack)
    private ImageButton ivVBack;
    UserInfo userinfo;

    //发送消息和编辑资料
    @ViewInject(R.id.btnSend)
    private Button btnSend;
    @ViewInject(R.id.btnEdit)
    private Button btnEdit;

    //资料部分
    @ViewInject(R.id.etNickname)
    private TextView etNickname;
    @ViewInject(R.id.etCreated)
    private TextView etCreated;
    @ViewInject(R.id.etDesc)
    private TextView etDesc;

    //照片墙
    @ViewInject(R.id.ivWall)
    private ImageView ivWall;
    String Token = "TgvtMFddoNkHDeWcaXtKWwB9ft/fZ3RIRK/GfxqI/3AS+vgXGRPNaiQ6XcHmxeendjCnD8jE8K6z8kfj1J8WUA==";//test userid=2


    //悬浮按钮
    @ViewInject(R.id.fab)
    private FloatingActionButton fab;

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onResume() {
        if (MainActivity.updateUserInfo == true) {
            getUser();
        }
        super.onResume();
    }

    private void getUser() {
        //获取用户信息
        String json = SpUtil.getSharePerference(getApplicationContext()).getString("user", "");
        if (!TextUtils.isEmpty(json)) {
            User user = GsonUtils.getInstance().json2Bean(json, User.class);
            if (user != null) {
                getUserInfo(user.getUserId());
            } else {
            }
        }
    }

    private void getUserInfo(final int userId) {
        userinfo = new UserInfo();
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "userinfo");
        params.addBodyParameter("userid", String.valueOf(userId));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getUserListByPage(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(MainActivity.class.getSimpleName(), result);
                if ("no found".equals(result)) {
                    return;
                }
                if ("action".equals(result)) {
                    return;
                }
                userinfo = GsonUtils.getInstance().json2Bean(result, UserInfo.class);
                if (userinfo == null) {
                    return;
                }
                etNickname.setText(userinfo.getNickname());
                etCreated.setText(userinfo.getCreated());
                etDesc.setText(userinfo.getDescrib());
                collapsingToolbarLayout.setTitle(userinfo.getNickname());
                BitmapUtils.getInstance(getApplicationContext()).setAvatarWithoutReflect(fab, DXLApi.BASE_URL + userinfo.getAvatar());
            }

            @Override
            public void onFailure(HttpException error, String msg) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUtils.inject(this);
        //获取token
        String json = SpUtil.getSharePerference(getApplicationContext()).getString("user", "");
        User user = GsonUtils.getInstance().json2Bean(json, User.class);
        Token = user.getToken();

        initData(getIntent());
        setSupportActionBar(toolbar);

        if (userinfo.getAvatar() != null)
            BitmapUtils.getInstance(getApplicationContext()).setAvatar(fab, DXLApi.BASE_URL + userinfo.getAvatar());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                enterConversation(view);
            }
        });

        //照片墙
        if (!TextUtils.isEmpty(userinfo.getPicWall())) {
            BitmapUtils.getInstance(getApplicationContext()).setAvatarWithoutReflect(ivWall, DXLApi.BASE_URL + userinfo.getPicWall());
        }
        ivWall.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
    }

    /**
     * 进入聊天
     *
     * @param view
     */
    private void enterConversation(final View view) {

        if (RongIM.getInstance() != null) {
            RongIM.getInstance().startPrivateChat(UserInfoDetailActivity.this, String.valueOf(userinfo.getUserId()), userinfo.getNickname());
//                                    RongIM.getInstance().startConversationList(MessageActivity.this);
//                                    RongIM.getInstance().startConversation(MessageActivity.this, Conversation.ConversationType.APP_PUBLIC_SERVICE,"2","aaa");
        } else {
            Snackbar.make(view, "初始化失败", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void initData(Intent intent) {
        userinfo = (UserInfo) intent.getSerializableExtra("userinfo");
        if (userinfo != null) {
            etCreated.setText(userinfo.getCreated());
            etNickname.setText(userinfo.getNickname());
            etDesc.setText(userinfo.getDescrib());
            collapsingToolbarLayout.setTitle(userinfo.getNickname());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ibBack:
                onBackPressed();
                break;
            case R.id.btnSend:
                enterConversation(v);
                break;
            case R.id.btnEdit:
                intent = new Intent(UserInfoDetailActivity.this, EditUserInfoActivity.class);
                intent.putExtra("userinfo", userinfo);
                startActivity(intent);
                break;

            case R.id.ivWall:
                intentOpenImage();
                break;
        }
    }

    private void intentOpenImage() {
        Intent intent;
        intent = new Intent();
        intent.setAction(intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Constant.REQUEST_CODE_MEDIA);
    }

    Uri uri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        switch (requestCode) {
            case Constant.REQUEST_CODE_MEDIA:
                ContentResolver resolver = getContentResolver();

                uri = data.getData();
                if (uri == null) {
                    return;
                }
                InputStream is = null;
                try {
                    // 直接使用下面的方法会出现OOM异常
                    // java.lang.OutOfMemoryError
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;// 设置只获取图片的边界，先不讲图片读入内存
                    is = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);

                    // opts.outWidth = 200;
                    // int height = opts.outHeight * 200 / opts.outWidth;
                    // opts.outHeight = height;

                    opts.inJustDecodeBounds = false;

                    // 压缩图片为原来的1/4
                    opts.inSampleSize = 4;// 图片缩小为原来的1/8

                    bitmap = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(uri), null, opts);
                    ivWall.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                uploadPicWall();
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 修改照片墙的照片
     */
    private void uploadPicWall() {
        String path = BitmapUtils.getImageAbsolutePath(this, uri);
        File file = new File(path);
        if (file.exists()) {
            RequestParams params = new RequestParams();
            params.addBodyParameter("file", file);
            params.addBodyParameter("userid", String.valueOf(userinfo.getUserId()));
            DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getUpdatePicWallApi(), params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    Log.i(UserInfoDetailActivity.class.getSimpleName() + "responseInfo:", responseInfo.result);
                    MainActivity.updateUserInfo = true;
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    Log.i(UserInfoDetailActivity.class.getSimpleName() + "msg:", msg);
                }
            });
        }
    }

    protected void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(getImageByCamera, Constant.REQUEST_CODE_CAPTURE_CAMEIA);
        } else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }
}
