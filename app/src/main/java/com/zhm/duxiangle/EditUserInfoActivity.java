package com.zhm.duxiangle;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    @ViewInject(R.id.ivUser)
    private ImageView ivUser;
    UserInfo userInfo;
    @ViewInject(R.id.fab)
    FloatingActionButton fab;
    //从相册获取图片的uri
    //进度条控制
    @ViewInject(R.id.pbLoading)
    private ProgressBar pbLoading;
    @ViewInject(R.id.tvLoading)
    private TextView tvLoading;
    Uri uri;

    @Override
    protected void onStart() {

        super.onStart();
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
                Intent intent = new Intent(EditUserInfoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        showProgress(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ivUser.setOnClickListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateUserinfo(fab);
            }
        });
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfo");

        ibBack.setOnClickListener(this);
        //设置视图内容
        tvTitle.setText("修改-" + userInfo.getNickname() == null ? "" : userInfo.getNickname());

        etNickname.setText(userInfo.getNickname());
        etCreated.setText(userInfo.getCreated());
        etDesc.setText(userInfo.getDescrib());
        if (!TextUtils.isEmpty(userInfo.getAvatar())) {
            if (userInfo.getAvatar().startsWith("http")) {
                BitmapUtils.getInstance(getApplicationContext()).setAvatarWithoutReflect(ivUser, userInfo.getAvatar());
            } else {
                BitmapUtils.getInstance(getApplicationContext()).setAvatarWithoutReflect(ivUser, DXLApi.BASE_URL + userInfo.getAvatar());
            }

        }
    }

    /**
     * 进度条控制
     *
     * @param b
     */
    private void showProgress(boolean b) {
        if (b) {
            pbLoading.setVisibility(View.VISIBLE);
            tvLoading.setVisibility(View.VISIBLE);
        } else {
            pbLoading.setVisibility(View.GONE);
            tvLoading.setVisibility(View.GONE);
        }
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
            uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/duxiangle_avatar.jpg"));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, Constant.REQUEST_CODE_CAPTURE_CAMERIA);
        } else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    private void dialog() {
        String[] str;
        str = new String[]{"从相册选择", "拍照选择", "查看大图"};
        new AlertDialog.Builder(this).setTitle("个性背景墙").setIcon(
                R.drawable.ic_launcher).setSingleChoiceItems(
                str, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                intentOpenImage();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibBack:
                onBackPressed();
                break;
            case R.id.ivUser:
//                intentOpenImage();
                dialog();
                break;
        }
    }

    private void bigImage() {
        Intent intent;
        intent = new Intent();
        intent.setClass(EditUserInfoActivity.this, WebImageActivity.class);
        intent.putExtra("url", DXLApi.BASE_URL + userInfo.getAvatar());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Log.i("data:", "null" + RESULT_OK);
            return;
        }
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
                ivUser.setImageBitmap(bitmap);
                break;
            case Constant.REQUEST_CODE_CAPTURE_CAMERIA:
                String path1 = BitmapUtils.getImageAbsolutePath(this, uri);
                Log.i(EditUserInfoActivity.class.getSimpleName(), path1);
                Bitmap bitmap1 = BitmapUtils.getimage(path1);
                ivUser.setImageBitmap(bitmap1);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUserinfo(final View view) {
        showProgress(true);
        userInfo.setCreated(etCreated.getText().toString().trim());
        userInfo.setNickname(etNickname.getText().toString().trim());
        userInfo.setDescrib(etDesc.getText().toString().trim());

        RequestParams params = new RequestParams();
        params.addBodyParameter("userid", String.valueOf(userInfo.getUserId()));
        params.addBodyParameter("nickname", userInfo.getNickname());
        params.addBodyParameter("created", userInfo.getCreated());
        params.addBodyParameter("describ", userInfo.getDescrib());
        if (uri != null) {
            String path = BitmapUtils.getImageAbsolutePath(this, uri);
            File file = new File(path);
//            File file = new File(path);
            if (file.exists())
                params.addBodyParameter("file", file);
        }
        Log.i("userid:", userInfo.getUserId() + "");
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getUpdateUserInfoApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                showProgress(false);
                String result = responseInfo.result;
                Log.i(EditUserInfoActivity.class.getSimpleName(), result);
                Snackbar.make(view, "修改成功", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                MainActivity.updateUserInfo = true;

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                showProgress(false);
                Snackbar.make(view, "修改失败，请联系183340093@qq.com", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }
}
