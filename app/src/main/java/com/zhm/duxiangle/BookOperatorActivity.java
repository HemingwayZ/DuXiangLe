package com.zhm.duxiangle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.zhm.duxiangle.api.ShareApi;
import com.zhm.duxiangle.bean.Book;
import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.SpUtil;
import com.zhm.duxiangle.utils.ToastUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ShareApi.getInstance(getApplicationContext()).wechatShareToBook(1, book.getAlt(), book.getTitle(), book.getImage(), book.getSummary());
                    }
                }).start();

                break;
            case R.id.btnAdd://增加
                saveBookToNet(btnAdd);

                RichContentMessage message = new RichContentMessage("title","content","url");

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
}
