package com.zhm.duxiangle;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.zxing.client.android.Intents;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.api.DouBanApi;
import com.zhm.duxiangle.bean.Book;
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.LogUtils;
import com.zhm.duxiangle.utils.ToastUtils;

@ContentView(R.layout.activity_book_detail)
public class BookDetailActivity extends SlidingBackActivity {

    private String isbn;
    @ViewInject(R.id.collapsingToolbarLayout)
    private CollapsingToolbarLayout collapsingToolbarLayout;
    @ViewInject(R.id.app_bar)
    private AppBarLayout appBarLayout;
    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;
    @ViewInject(R.id.fabShare)
    private FloatingActionButton fabShare;
    @ViewInject(R.id.tvContent)
    private TextView tvContent;
    @ViewInject(R.id.book_cover)
    private KenBurnsView bookCover;
    //滚动条
    @ViewInject(R.id.nestedScrollView)
    private NestedScrollView nestedScrollView;
    // 进度条
    @ViewInject(R.id.progressBar_bookDetail)
    private ProgressBar progressBar;
    //悬浮按钮2
    @ViewInject(R.id.fabShare2)
    private FloatingActionButton fabShare2;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        ViewUtils.inject(this);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                getBookInfoByScan();
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                LogUtils.i(BookDetailActivity.this, "--i:" + i);
                LogUtils.i(BookDetailActivity.this, "--getTotalScrollRange:" + appBarLayout.getTotalScrollRange());

                if (i == -appBarLayout.getTotalScrollRange()) {
                    //这段代码修改是隐藏头部的操作
                    fabShare2.setVisibility(View.VISIBLE);
                    toolbar.setLogo(bookCover.getDrawable());
                } else {
                    if (book != null) {
                        toolbar.setLogo(null);
                    }
                }
                if (i > -132) {
                    fabShare2.setVisibility(View.GONE);
                    toolbar.setLogo(null);
                }
            }
        });

        getBookInfoByScan();
        getDataFromNet();
    }

    /**
     * 根据扫描的isbn获取书籍信息
     */
    private void getBookInfoByScan() {
        Intent intent = getIntent();
        if (null != intent) {
            isbn = intent.getStringExtra(Intents.Scan.RESULT);
            isbn = isbn == null ? "9787512401136" : isbn;
            tvContent.setText(isbn);
        }
        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromNet();
            }
        });
    }

    /**
     * 获取书籍基本信息
     */
    private void getDataFromNet() {
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, DouBanApi.getBookByIsbn(isbn), new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String json = responseInfo.result;
                book = GsonUtils.getInstance().json2Bean(json, Book.class);
                tvContent.setText(book.toString());
//                collapsingToolbarLayout.setTitle(book.getTitle());
//                collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.BOTTOM | Gravity.RIGHT);
                BitmapUtils.getInstance(getApplicationContext()).setBookAvatar(bookCover, book.getImages().getLarge(), toolbar);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                ToastUtils.cancelToast();
                ToastUtils.showToast(getApplicationContext(), "数据请求失败");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        ToastUtils.cancelToast();
    }
}
