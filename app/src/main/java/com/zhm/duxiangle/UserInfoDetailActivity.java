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
import com.zhm.duxiangle.bean.UserInfo;
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.utils.DXLHttpUtils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUtils.inject(this);

        initData(getIntent());
        setSupportActionBar(toolbar);

        toolbar.getLogo();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (userinfo.getAvatar() != null)
            BitmapUtils.getInstance(getApplicationContext()).setAvatar(fab, userinfo.getAvatar());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                enterConversation(view);
            }
        });
        etDesc.setFocusable(false);
        etCreated.setFocusable(false);
        etNickname.setFocusable(false);
        etCreated.setClickable(false);
        etNickname.setClickable(false);
        etDesc.setClickable(false);
        ivVBack.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        //照片墙
        ivWall.setOnClickListener(this);
    }

    /**
     * 进入聊天
     *
     * @param view
     */
    private void enterConversation(final View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * IMKit SDK调用第二步
                 *
                 * 建立与服务器的连接
                 *
                 */
                RongIM.connect(Token, new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                        //Connect Token 失效的状态处理，需要重新获取 Token
                        Snackbar.make(view, "Token错误", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                    /**
                     * 连接融云成功
                     */
                    @Override
                    public void onSuccess(String userId) {
                        Log.e("MainActivity", "——onSuccess— -" + userId);
                        /**
                         * 启动单聊
                         * context - 应用上下文。
                         * targetUserId - 要与之聊天的用户 Id。
                         * title - 聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
                         */
                        //{"code":200,"userId":"2462","token":"4Cp7WFQq92h1xjdmdaL5AXM//2Y39LDCnuxr2xdDagUSew9ILDZp6tvcV6rRhvbxbTnqk7cS56XBpjxS+NU4Ng=="}
                        //{"code":200,"userId":"1","token":"8FQcKXFvWDqN2j3qZWDA5nM//2Y39LDCnuxr2xdDagUSew9ILDZp6n9+OUnzkJ/4/W8bX6Y2cB4VGTWNrvchrA=="}
                        if (RongIM.getInstance() != null) {


                            RongIM.getInstance().startPrivateChat(UserInfoDetailActivity.this, String.valueOf(userinfo.getUserId()), userinfo.getNickname());
//                                    RongIM.getInstance().startConversationList(MessageActivity.this);
//                                    RongIM.getInstance().startConversation(MessageActivity.this, Conversation.ConversationType.APP_PUBLIC_SERVICE,"2","aaa");


                        } else {
                            Snackbar.make(view, "初始化失败", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        //回话列表
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        Log.e("MainActivity", "——onError— -" + errorCode);
                        Snackbar.make(view, "访问服务器失败", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        }).start();
    }

    private void initData(Intent intent) {
        userinfo = (UserInfo) intent.getSerializableExtra("userinfo");
        if (userinfo != null) {
            etCreated.setText(userinfo.getCreated());
            etNickname.setText(userinfo.getNickname());
            etDesc.setText(userinfo.getDescrib());
            toolbar.setTitle(userinfo.getNickname());
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
                intent = new Intent();
                intent.setAction(intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Constant.REQUEST_CODE_MEDIA);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        switch (requestCode) {
            case Constant.REQUEST_CODE_MEDIA:
                ContentResolver resolver = getContentResolver();

                Uri uri = data.getData();
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
                String path = getImageAbsolutePath(this, uri);
                File file = new File(path);
                if (file.exists()) {
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("file", file);
                    DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getFileUpLoad(), params, new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {

                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {

                        }
                    });
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
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

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param imageUri
     * @author yaoxing
     * @date 2014-10-12
     */
    public static String getImageAbsolutePath(AppCompatActivity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
