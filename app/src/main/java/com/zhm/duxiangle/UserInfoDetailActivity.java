package com.zhm.duxiangle;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import io.rong.imkit.RongIM;

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
    @ViewInject(R.id.btnAddFriend)
    private Button btnAddFriend;
    @ViewInject(R.id.btnBookRoom)
    private Button btnBookRoom;
    @ViewInject(R.id.btnRemoveFriend)
    private Button btnRemoveFriend;
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


    //悬浮按钮
    @ViewInject(R.id.fab)
    private ImageView fab;
    private boolean isMy;
    private User user;
    private boolean bIsMyFriend;

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onResume() {
        if (MainActivity.updateUserInfo == true) {
            getUser();
            if (user != null) {
                getUserInfo(user.getUserId());
            }
        }
        super.onResume();
    }

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
                Intent intent = new Intent(UserInfoDetailActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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

        getUser();
        initData(getIntent());
        isMyFriend();
        setSupportActionBar(toolbar);


    }

    /**
     * 进入聊天
     *
     * @param view
     */
    private void enterConversation(final View view) {

        if (RongIM.getInstance() != null) {
            RongIM.getInstance().startPrivateChat(UserInfoDetailActivity.this, String.valueOf(userinfo.getUserId()), userinfo.getNickname());
            RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(String.valueOf(userinfo.getUserId()), userinfo.getNickname(), Uri.parse(DXLApi.BASE_URL + userinfo.getAvatar())));

//                                    RongIM.getInstance().startConversationList(MessageActivity.this);
//                                    RongIM.getInstance().startConversation(MessageActivity.this, Conversation.ConversationType.APP_PUBLIC_SERVICE,"2","aaa");
        } else {
            Snackbar.make(view, "初始化失败", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void dialog() {
        String[] str;
        if (isMy) {
            str = new String[]{"从相册选择", "拍照选择", "查看大图"};
        } else {
            str = new String[]{"查看大图"};
        }
        new AlertDialog.Builder(this).setTitle("个性背景墙").setIcon(
                R.drawable.ic_launcher).setSingleChoiceItems(
                str, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (isMy) {
                                    intentOpenImage();
                                } else {
                                    bigImage();
                                }

                                break;
                            case 1:
                                intentOpenCamera();
                                break;
                            case 2:
                                bigImage();
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

    private void initData(Intent intent) {
        userinfo = (UserInfo) intent.getSerializableExtra("userinfo");
        isMy = getIntent().getBooleanExtra("isMy", false);
        if (userinfo.getUserId() == user.getUserId()) {
            isMy = true;
        }
        if (userinfo != null) {
            etCreated.setText(userinfo.getCreated());
            etNickname.setText(userinfo.getNickname());
            etDesc.setText(userinfo.getDescrib());
            collapsingToolbarLayout.setTitle(userinfo.getNickname());
            if (userinfo.getAvatar() != null) {
                //头像
                BitmapUtils.getInstance(getApplicationContext()).setAvatarWithoutReflect(fab, DXLApi.BASE_URL + userinfo.getAvatar());
            }
            if (!TextUtils.isEmpty(userinfo.getPicWall())) {
                //照片墙
                BitmapUtils.getInstance(getApplicationContext()).setAvatarWithoutReflect(ivWall, DXLApi.BASE_URL + userinfo.getPicWall());
            }
            btnSend.setOnClickListener(this);
            btnEdit.setOnClickListener(this);
            btnAddFriend.setOnClickListener(this);
            btnRemoveFriend.setOnClickListener(this);
            btnBookRoom.setOnClickListener(this);
            ivWall.setOnClickListener(this);
            if (isMy) {
                btnSend.setVisibility(View.GONE);
            } else {
                btnEdit.setVisibility(View.GONE);
            }
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
                if (bIsMyFriend) {
                    enterConversation(v);
                } else {
                    Snackbar.make(btnSend, "请先添加用户为好友", Snackbar.LENGTH_SHORT).setAction("添加", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addFriend();
                        }
                    }).show();
                }
                break;
            case R.id.btnEdit:
                intent = new Intent(UserInfoDetailActivity.this, EditUserInfoActivity.class);
                intent.putExtra("userinfo", userinfo);
                startActivity(intent);
                break;
            case R.id.ivWall:
                dialog();
                break;
            case R.id.fab:
//                dialog();
                break;
            case R.id.btnAddFriend:
                //添加好友操作
                addFriend();
                break;
            case R.id.btnRemoveFriend:
                //删除好友操作
                removeFriend();
                break;
            case R.id.btnBookRoom:
                intoBookRoom();
                break;
        }
    }

    private void intoBookRoom() {
        Intent intent = new Intent();
        intent.setClass(UserInfoDetailActivity.this, SearchBookActivity.class);
        intent.putExtra("userid", userinfo.getUserId());
        this.startActivity(intent);
    }

    private void addFriend() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "add_friend");
        params.addBodyParameter("userid", String.valueOf(user.getUserId()));
        params.addBodyParameter("friendid", String.valueOf(userinfo.getUserId()));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getFriendsApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if ("friendid is null".equals(result)) {
                    return;
                }
                if ("userid is null".equals(result)) {
                    return;
                }
                if ("1".equals(result)) {
                    FriendsActivity.isUpdateList = true;
                    Snackbar.make(btnAddFriend, "添加成功", Snackbar.LENGTH_SHORT).show();
                    isMyFriend();
                } else {
                    Snackbar.make(btnAddFriend, "添加失败", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Snackbar.make(btnAddFriend, "服务器链接", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFriend() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "remove_friend");
        params.addBodyParameter("userid", String.valueOf(user.getUserId()));
        params.addBodyParameter("friendid", String.valueOf(userinfo.getUserId()));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getFriendsApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if ("friendid is null".equals(result)) {
                    return;
                }
                if ("userid is null".equals(result)) {
                    return;
                }
                if ("true".equals(result)) {
                    Snackbar.make(btnAddFriend, "删除成功", Snackbar.LENGTH_SHORT).show();
                    isMyFriend();
                    FriendsActivity.isUpdateList = true;
                } else {
                    Snackbar.make(btnAddFriend, "删除失败", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Snackbar.make(btnAddFriend, "服务器链接", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    //判断是否为我的好友
    private void isMyFriend() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "is_my_friend");
        params.addBodyParameter("userid", String.valueOf(user.getUserId()));
        params.addBodyParameter("friendid", String.valueOf(userinfo.getUserId()));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getFriendsApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if ("friendid is null".equals(result)) {
                    return;
                }
                if ("userid is null".equals(result)) {
                    return;
                }
                if ("true".equals(result)) {
                    bIsMyFriend = true;
                    btnAddFriend.setVisibility(View.GONE);
                    btnRemoveFriend.setVisibility(View.VISIBLE);
                } else {
                    bIsMyFriend = false;
                    btnAddFriend.setVisibility(View.VISIBLE);
                    btnRemoveFriend.setVisibility(View.GONE);
                }
                if (isMy) {
                    btnAddFriend.setVisibility(View.GONE);
                    btnRemoveFriend.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Snackbar.make(btnAddFriend, "服务器链接", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 查看大图
     */
    private void bigImage() {
        Intent intent;
        intent = new Intent();
        intent.setClass(UserInfoDetailActivity.this, WebImageActivity.class);
        intent.putExtra("url", DXLApi.BASE_URL + userinfo.getPicWall());
        startActivity(intent);
    }

    private void intentOpenImage() {
        Intent intent;
        intent = new Intent();
        intent.setAction(intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Constant.REQUEST_CODE_MEDIA);
    }

    private void intentOpenCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/duxiangle_ivWall.jpg"));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, Constant.REQUEST_CODE_CAPTURE_CAMERIA);
        } else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    Uri uri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Log.i("data:", "null" + RESULT_OK);
            return;
        }
//        if(uri==null){
//            Bundle bundle = data.getExtras();
//            Bitmap bitmap = (Bitmap) bundle.get("data");
//            uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
//        }
        switch (requestCode) {
            case Constant.REQUEST_CODE_MEDIA:
                if (data == null) {
                    return;
                }
                uri = data.getData();
                if (uri == null) {
                    Log.i("uri", "uri为null");
                    return;
                }
                String path = BitmapUtils.getImageAbsolutePath(this, uri);
                Log.i("path:", path);
                Bitmap bitmap = BitmapUtils.getimage(path);
                ivWall.setImageBitmap(bitmap);
                uploadPicWall();
                break;
            case Constant.REQUEST_CODE_CAPTURE_CAMERIA:
                String path1 = BitmapUtils.getImageAbsolutePath(this, uri);
                Bitmap bitmap1 = BitmapUtils.getimage(path1);
                ivWall.setImageBitmap(bitmap1);
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
                    Snackbar.make(ivWall, "修改成功", Snackbar.LENGTH_SHORT).show();
                    getUser();
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    Log.i(UserInfoDetailActivity.class.getSimpleName() + "msg:", msg);
                    Snackbar.make(ivWall, "网络异常，修改失败", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    protected void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(getImageByCamera, Constant.REQUEST_CODE_CAPTURE_CAMERIA);
        } else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }
}
