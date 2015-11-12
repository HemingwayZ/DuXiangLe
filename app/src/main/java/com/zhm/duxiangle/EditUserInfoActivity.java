package com.zhm.duxiangle;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.zhm.duxiangle.bean.UserInfo;
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.utils.DXLHttpUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
            BitmapUtils.getInstance(getApplicationContext()).setAvatarWithoutReflect(ivUser, DXLApi.BASE_URL + userInfo.getAvatar());
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

    protected void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(getImageByCamera, Constant.REQUEST_CODE_CAPTURE_CAMEIA);
        } else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibBack:
                onBackPressed();
                break;
            case R.id.ivUser:
                intentOpenImage();
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case Constant.REQUEST_CODE_MEDIA:
                uri = data.getData();
                if (uri == null) {
                    return;
                }
                String path = BitmapUtils.getImageAbsolutePath(this,uri);
                Bitmap bitmap = BitmapUtils.getimage(Environment.getExternalStorageDirectory()+"/duxiangle_avatar.jpg");
                ivUser.setImageBitmap(bitmap);

//                InputStream is = null;
//                try {
//                    // 直接使用下面的方法会出现OOM异常
//                    // java.lang.OutOfMemoryError
//                    BitmapFactory.Options opts = new BitmapFactory.Options();
//                    opts.inJustDecodeBounds = true;// 设置只获取图片的边界，先不讲图片读入内存
//                    is = getContentResolver().openInputStream(uri);
//                    Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
//
//                    // opts.outWidth = 200;
//                    // int height = opts.outHeight * 200 / opts.outWidth;
//                    // opts.outHeight = height;
//
//                    opts.inJustDecodeBounds = false;
//
//                    // 压缩图片为原来的1/4
//                    bitmap = BitmapUtils.compressImage(bitmap);
//
//                    bitmap = BitmapFactory.decodeStream(getContentResolver()
//                            .openInputStream(uri), null, opts);
//                    ivUser.setImageBitmap(bitmap);
//
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } finally {
//                    if (is != null) {
//                        try {
//                            is.close();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                uploadAvatar();
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
            File file = new File(Environment.getExternalStorageDirectory()+"duxiangle_avatar.jpg");
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
                Snackbar.make(view, "修改失败，请联系庄海明", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }
}
