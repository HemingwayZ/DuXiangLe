package com.zhm.duxiangle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.ToastUtils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_enter_from_bottom, 0);
        ViewUtils.inject(this);
        book = (Book) getIntent().getSerializableExtra("book");
        //设置监听事件
        btnAdd.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnShare.setOnClickListener(this);

        tvBack.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                ShareApi.getInstance(getApplicationContext()).wechatShareToBook(1, book.getUrl(), book.getTitle(), book.getImage());
                break;
            case R.id.btnAdd://增加
                saveBookToNet(btnAdd);
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
                } else {
                    ToastUtils.showToast(getApplicationContext(), "删除失败");
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {

            }
        });
    }

    public void saveBookToNet(View view) {
        String json = GsonUtils.getInstance().bean2Json(book);
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "save_book");
        params.addBodyParameter("book", json);
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.bookApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!"failed".equals(result)) {
                    ToastUtils.showToast(getApplicationContext(), result);
                } else {
                    ToastUtils.showToast(getApplicationContext(), result);
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                ToastUtils.showToast(getApplicationContext(), "链接服务器失败");
            }
        });
    }
}
