package com.zhm.duxiangle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.api.ShareApi;
import com.zhm.duxiangle.bean.Book;
import com.zhm.duxiangle.bean.Constant;
import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.SpUtil;
import com.zhm.duxiangle.utils.ToastUtils;

import java.io.IOException;
import java.net.URL;

import io.rong.message.RichContentMessage;

@ContentView(R.layout.activity_book_operator)
public class BookOperatorActivity extends Activity implements View.OnClickListener {
    private Book book;
    //按钮设置
    @ViewInject(R.id.btnAdd)
    private Button btnAdd;
    @ViewInject(R.id.btnDelete)
    private Button btnDelete;
    @ViewInject(R.id.btnShare)
    private Button btnShare;

    //
    @ViewInject(R.id.tvBack)
    private TextView tvBack;
    private User user;
    private boolean isMy = false;
    private Tencent mTencent;
    private IWeiboShareAPI mWeiboShareAPI;

    public void getUser() {
        //获取用户信息
        String json = SpUtil.getSharePerference(getApplicationContext()).getString("user", "");
        if (!TextUtils.isEmpty(json)) {
            user = GsonUtils.getInstance().json2Bean(json, User.class);
            if (user != null) {
            } else {
                Intent intent = new Intent(BookOperatorActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ShareApi.getInstance(getApplicationContext()).regToWx();
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_enter_from_bottom, 0);
        ViewUtils.inject(this);
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constant.SINA_APP_KEY);
        mWeiboShareAPI.registerApp();    // 将应用注册到微博客户端
        getUser();

        book = (Book) getIntent().getSerializableExtra("book");
        //设置监听事件
        btnAdd.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnShare.setOnClickListener(this);

        tvBack.setOnClickListener(this);
        isMy = getIntent().getBooleanExtra("isMy", false);
        if (isMy) {
            btnAdd.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnAdd.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.activity_out_from_top);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (book == null) {
            Snackbar.make(v, "没有书籍信息", Snackbar.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.btnShare://分享

                dialog();
                break;
            case R.id.btnAdd://增加
                saveBookToNet(btnAdd);

                RichContentMessage message = new RichContentMessage("title", "content", "url");

                break;
            case R.id.btnDelete://删除
                Dialog dialog = new AlertDialog.Builder(this).setIcon(
                        R.drawable.ic_launcher).setTitle("确认删除").setMessage(book.getTitle()).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        removeBook();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        onBackPressed();
                    }
                }).create();
                dialog.show();
                break;
            case R.id.tvBack:
                onBackPressed();
                break;
        }
    }

    /**
     * 删除书本
     */
    private void removeBook() {

        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "removebook");
        params.addBodyParameter("userid", String.valueOf(book.getUserId()));
        params.addBodyParameter("bookid", String.valueOf(book.getBookId()));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.bookApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if ("1".equals(result)) {
                    ToastUtils.showToast(getApplicationContext(), "删除成功");
                    finish();
                } else {
                    ToastUtils.showToast(getApplicationContext(), "删除失败");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                ToastUtils.showToast(getApplicationContext(), "服务器链接失败");
            }
        });
    }

    public void saveBookToNet(View view) {
        if (book.getImages() != null && book.getImages().getLarge() != null) {
            book.setImage(book.getImages().getLarge());
        }
        String json = GsonUtils.getInstance().bean2Json(book);
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "save_book");
        params.addBodyParameter("book", json);
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.bookApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if ("failed".equals(result)) {
                    ToastUtils.showToast(getApplicationContext(), "书籍已存在,请勿重复添加");
                } else {
                    ToastUtils.showToast(getApplicationContext(), "添加成功");
                    finish();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                ToastUtils.showToast(getApplicationContext(), "服务器链接失败");
            }
        });
    }

    private void dialog() {
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
                                        ShareApi.getInstance(getApplicationContext()).wechatShareToBook(1, book.getAlt(), book.getTitle(), book.getImage(), book.getSummary());
                                    }
                                }).start();
                                break;
                            case 1:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //1：朋友圈
                                        ShareApi.getInstance(getApplicationContext()).wechatShareToBook(0, book.getAlt(), book.getTitle(), book.getImage(), book.getSummary());
                                    }

                                }).start();
                                break;
                            case 2:
                                qqShare();
                                break;
                            case 3:
                                sendMultiMessage(true);
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
        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, BookOperatorActivity.this);
        Bundle bundle = new Bundle();
        //这条分享消息被好友点击后的跳转URL。
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, book.getAlt());
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_	SUMMARY不能全为空，最少必须有一个是有值的。
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, book.getTitle());
        //分享的图片URL
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
                book.getImage());
        //分享的消息摘要，最长50个字
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, book.getStrAuthor());
        //手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, book.getTitle());
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != mTencent)
            mTencent.onActivityResult(requestCode, resultCode, data);
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
        boolean b = mWeiboShareAPI.sendRequest(BookOperatorActivity.this, request);//发送请求消息到微博，唤起微博分享界面
    }


    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = book.getTitle();
        mediaObject.description = book.getStrAuthor();
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = book.getAlt();
        mediaObject.defaultText = "读享乐";
        return mediaObject;
    }
}
