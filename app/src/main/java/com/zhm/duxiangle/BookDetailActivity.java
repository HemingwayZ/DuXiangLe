package com.zhm.duxiangle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.Transition;
import com.google.zxing.client.android.Intents;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.adapter.HorizontalScrollViewAdapter;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.api.DouBanApi;
import com.zhm.duxiangle.api.ShareApi;
import com.zhm.duxiangle.bean.Book;
import com.zhm.duxiangle.bean.Images;
import com.zhm.duxiangle.bean.Page;
import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.bean.UserInfo;
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.utils.DXLDbUtils;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.LogUtils;
import com.zhm.duxiangle.utils.SpUtil;
import com.zhm.duxiangle.utils.ToastUtils;
import com.zhm.duxiangle.view.MyHorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.RichContentMessage;

@ContentView(R.layout.activity_book_detail)
public class BookDetailActivity extends SlidingBackActivity {

    //分页处理
    int thispage = 0;
    int rowpaerpage = 5;
    //收藏了该书的人的信息
    @ViewInject(R.id.tvUserInfo)
    private TextView tvUserInfo;
    //    @ViewInject(R.id.vpAvatar)
//    private ViewPager vpAvatar;
    private String isbn;
    @ViewInject(R.id.collapsingToolbarLayout)
    private CollapsingToolbarLayout collapsingToolbarLayout;
    @ViewInject(R.id.app_bar)
    private AppBarLayout appBarLayout;
    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;
    @ViewInject(R.id.fabShare)
    private FloatingActionButton fabShare;
    //书籍信息
    @ViewInject(R.id.tvTitle)
    private TextView tvTitle;
    @ViewInject(R.id.tvAuthor)
    private TextView tvAuthor;
    @ViewInject(R.id.book_cover)
    private KenBurnsView bookCover;
    @ViewInject(R.id.tvIsbn)
    private TextView tvIsbn;
    @ViewInject(R.id.tvSummary)
    private TextView tvSummary;
    @ViewInject(R.id.tvAuthorIntro)
    private TextView tvAuthorIntro;
    @ViewInject(R.id.tvPublisher)
    private TextView tvPublisher;
    @ViewInject(R.id.tvSubtitle)
    private TextView tvSubtitle;
    @ViewInject(R.id.tvCatalog)
    private TextView tvCatalog;
    //滚动条
    @ViewInject(R.id.nestedScrollView)
    private NestedScrollView nestedScrollView;
    // 进度条
    @ViewInject(R.id.progressBar_bookDetail)
    private ProgressBar progressBar;

    //后退
    @ViewInject(R.id.ibBack)
    private ImageButton ibBack;
    //头像
    @ViewInject(R.id.iv1)
    private ImageView iv1;
    @ViewInject(R.id.iv2)
    private ImageView iv2;
    @ViewInject(R.id.iv3)
    private ImageView iv3;
    @ViewInject(R.id.iv4)
    private ImageView iv4;
    @ViewInject(R.id.iv5)
    private ImageView iv5;
    @ViewInject(R.id.tvCount)
    private TextView tvCount;
    //CreditsRollView
//    @ViewInject(R.id.creditsroll)
//    private CreditsRollView creditsRollView;

    private User user;
    private Book book;
    private boolean isMy;
    private Page<UserInfo> userInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        ViewUtils.inject(this);
        isMy = getIntent().getBooleanExtra("isMy", false);
        //获取用户信息
        getUser();
        bookCover.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (DragEvent.ACTION_DRAG_STARTED == event.getAction()) {
                    bookCover.pause();
                }
                if (DragEvent.ACTION_DROP == event.getAction()) {
                    bookCover.resume();
                    bookCover.setScaleType(ImageView.ScaleType.CENTER);
                }
                return true;
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        bookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, WebImageActivity.class);
                if (book.getImages() != null) {
                    intent.putExtra("url", book.getImages().getLarge());
                } else {
                    intent.putExtra("url", book.getImage());
                }
                startActivity(intent);
            }
        });
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailActivity.this, BookOperatorActivity.class);
                book.setUserId(user.getUserId());
                intent.putExtra("book", book);
                if (isMy) {
                    intent.putExtra("isMy", true);
                }
                startActivity(intent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                ShareApi.getInstance(getApplicationContext()).wechatShare(1, book.getAlt());//分享到朋友圈
//                saveData();
            }
        });

        book = (Book) getIntent().getSerializableExtra("book");
        if (null == book) {
            getBookInfoByScan();
            getDataFromNet();
        } else {
            tvTitle.setText("书名:" + book.getTitle());
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; book.getAuthor() != null && i < book.getAuthor().size(); i++) {
                buffer.append(book.getAuthor().get(i));
            }
            getUserInfoByIsbn(book.getIsbn13(), 0);
            tvAuthor.setText(buffer.toString());
            tvIsbn.setText(book.getIsbn13());
            tvSummary.setText(book.getSummary());
            tvAuthorIntro.setText(book.getAuthor_intro());
            tvPublisher.setText(book.getPublisher());
            tvSubtitle.setText(book.getSubtitle());
            tvCatalog.setText(book.getCatalog());
            collapsingToolbarLayout.setTitle(book.getTitle());
            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
            //拓展过后的标题颜色
//            collapsingToolbarLayout.setExpandedTitleColor(Color.BLUE);
            collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.BOTTOM | Gravity.RIGHT);
            if (book.getImages() != null) {
                BitmapUtils.getInstance(getApplicationContext()).setAvatarWithoutReflect(bookCover, book.getImages().getLarge());
            } else {
                BitmapUtils.getInstance(getApplicationContext()).setAvatarWithoutReflect(bookCover, book.getImage());
            }
            progressBar.setVisibility(View.GONE);
        }
//        initViewPager();
    }

//    private void initViewPager() {
//
//        vpAvatar.setAdapter(new PagerAdapter() {
//            @Override
//            public int getCount() {
//                return 0;
//            }
//
//            @Override
//            public boolean isViewFromObject(View view, Object object) {
//                return false;
//            }
//        });
//    }

    /**
     * 将数据存储到本地数据库
     */
    private void saveData() {
        DbUtils.DaoConfig config = new DbUtils.DaoConfig(getApplicationContext());
        config.setDbName("books");
        DbUtils dbUtils = DXLDbUtils.getInstance(getApplicationContext()).createDb(DXLDbUtils.DB_BOOK);
        try {
            dbUtils.createTableIfNotExist(Book.class);
            if (dbUtils.findById(Book.class, book.getId()) == null) {//如果书籍已经存在自己的数据库,则不必存储,重复存储会造成异常
                //设置作者的字符串--数据库只能存储一维数据，若是二维数据需要用外键的方式或者使用字符串的方式
                StringBuffer bAuthor = new StringBuffer();
                if (book.getAuthor() != null && book.getAuthor().size() > 0) {
                    bAuthor.append(book.getAuthor().get(0));
                    for (int i = 1; i < book.getAuthor().size(); i++) {
                        bAuthor.append("+");
                        bAuthor.append(book.getAuthor().get(i));
                    }
                    book.setStrAuthor(bAuthor.toString());
                }
                //设置翻译人员信息
                if (book.getTranslator() != null && book.getTranslator().size() > 0) {
                    StringBuffer bTranslator = new StringBuffer();
                    bTranslator.append(book.getTranslator().get(0));
                    for (int i = 1; i < book.getTranslator().size(); i++) {
                        bTranslator.append("+");
                        bTranslator.append(book.getTranslator().get(i));
                    }
                    book.setStrTranslator(bTranslator.toString());
                }
                //存储书籍信息
                dbUtils.save(book);

                //存储图片操作
                dbUtils.createTableIfNotExist(Images.class);
                book.getImages().setId(book.getId());
                dbUtils.save(book.getImages());
            }

        } catch (DbException e) {

            e.printStackTrace();
        }
    }

    public void saveBookToNet(Book book) {
        String json = GsonUtils.getInstance().bean2Json(book);
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "save_book");
        params.addBodyParameter("book", json);
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.bookApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!"failed".equals(result)) {
                    ToastUtils.showToast(getApplicationContext(), "result:" + result);
                    return;
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {

            }
        });
    }

    /**
     * 根据扫描的isbn获取书籍信息
     */
    private void getBookInfoByScan() {
        Intent intent = getIntent();
        if (null != intent) {
            isbn = intent.getStringExtra(Intents.Scan.RESULT);
            isbn = isbn == null ? "9787512401136" : isbn;
        }
    }

    /**
     * 获取书籍基本信息
     */
    private void getDataFromNet() {
        HttpUtils http = new HttpUtils();
        http.configTimeout(1000 * 3);
        http.send(HttpRequest.HttpMethod.POST, DouBanApi.getBookByIsbn(isbn), new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                String json = responseInfo.result;
                book = GsonUtils.getInstance().json2Bean(json, Book.class);
                tvTitle.setText(book.getTitle());
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < book.getAuthor().size(); i++) {
                    buffer.append(book.getAuthor().get(i));
                }
                //书籍信息
                tvAuthor.setText(buffer.toString());
                tvIsbn.setText(book.getIsbn13());
                tvSummary.setText(book.getSummary());
                tvAuthorIntro.setText(book.getAuthor_intro());
                tvPublisher.setText(book.getPublisher());
                tvSubtitle.setText(book.getSubtitle());
                tvCatalog.setText(book.getCatalog());

                collapsingToolbarLayout.setTitle(book.getTitle());
                collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
                //拓展过后的标题颜色
                collapsingToolbarLayout.setExpandedTitleColor(Color.BLUE);
                collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.BOTTOM | Gravity.RIGHT);
                BitmapUtils.getInstance(getApplicationContext()).setAvatarWithoutReflect(bookCover, book.getImages().getLarge());
                progressBar.setVisibility(View.GONE);
//              creditsRollView.setText(json);
                saveData();

                book.setUserId(user.getUserId());
//                saveBookToNet(book);
                getUserInfoByIsbn(book.getIsbn13(), 0);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                ToastUtils.cancelToast();
                ToastUtils.showToast(getApplicationContext(), "数据请求失败");
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        ToastUtils.cancelToast();
    }

    public void getUser() {
        //获取用户信息
        user = new User();
        String json = SpUtil.getSharePerference(getApplicationContext()).getString("user", "");
        if (!TextUtils.isEmpty(json)) {
            user = GsonUtils.getInstance().json2Bean(json, User.class);
            if (user != null) {
            } else {
                Intent intent = new Intent(BookDetailActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void getUserInfoByIsbn(String isbn, int _thispage) {
        //http://localhost:8080/DuXiangLeServer/UserInfoServlet?action=find_userinfo_by_isbn&isbn=9781453562680&rowperpage=2
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "find_userinfo_by_isbn");
        params.addBodyParameter("isbn", isbn);
        params.addBodyParameter("thispage", String.valueOf(_thispage));
        params.addBodyParameter("rowperpage", String.valueOf(rowpaerpage));

        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getUserInfoApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
//                LogUtils.i(BookDetailActivity.this,"zhm--result:"+result);
                if ("action is null".equals(result)) {
                    return;
                }
                if ("isbn_is_null".equals(result)) {
                    return;
                }
                userInfos = GsonUtils.getInstance().getUserInfos(result);
                UserInfo userinfo = null;
                if (userInfos.getList() != null&&userInfos.getList().size()>0) {
                    tvCount.setText("共有" + userInfos.getCountrow() + "位用户收藏了该书");
                    if (userInfos.getList().size() > 4) {
                        iv5.setVisibility(View.VISIBLE);
                        iv5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BookDetailActivity.this, BookWithUserInfosActivity.class);
                                intent.putExtra("book", book);
                                startActivity(intent);
                            }
                        });
                    }
                    for (int i = 0; i < userInfos.getList().size(); i++) {
                        userinfo = userInfos.getList().get(i);
                        if (userinfo.getAvatar() != null) {
                            switch (i) {
                                case 0:
                                    iv1.setVisibility(View.VISIBLE);
                                    if (userinfo.getAvatar().startsWith("http"))
                                        BitmapUtils.getInstance(BookDetailActivity.this).setAvatarWithoutReflect(iv1, userinfo.getAvatar());
                                    else
                                        BitmapUtils.getInstance(BookDetailActivity.this).setAvatarWithoutReflect(iv1, DXLApi.BASE_URL + userinfo.getAvatar());
                                    openUserInfoActivity(iv1, userinfo);
                                    break;
                                case 1:
                                    iv2.setVisibility(View.VISIBLE);
                                    if (userinfo.getAvatar().startsWith("http"))
                                        BitmapUtils.getInstance(BookDetailActivity.this).setAvatarWithoutReflect(iv2, userinfo.getAvatar());
                                    else
                                        BitmapUtils.getInstance(BookDetailActivity.this).setAvatarWithoutReflect(iv2, DXLApi.BASE_URL + userinfo.getAvatar());
                                    openUserInfoActivity(iv2, userinfo);
                                    break;
                                case 2:
                                    iv3.setVisibility(View.VISIBLE);
                                    if (userinfo.getAvatar().startsWith("http"))
                                        BitmapUtils.getInstance(BookDetailActivity.this).setAvatarWithoutReflect(iv3, userinfo.getAvatar());
                                    else
                                        BitmapUtils.getInstance(BookDetailActivity.this).setAvatarWithoutReflect(iv3, DXLApi.BASE_URL + userinfo.getAvatar());
                                    openUserInfoActivity(iv3, userinfo);
                                    break;
                                case 3:
                                    iv4.setVisibility(View.VISIBLE);
                                    if (userinfo.getAvatar().startsWith("http"))
                                        BitmapUtils.getInstance(BookDetailActivity.this).setAvatarWithoutReflect(iv4, userinfo.getAvatar());
                                    else
                                        BitmapUtils.getInstance(BookDetailActivity.this).setAvatarWithoutReflect(iv4, DXLApi.BASE_URL + userinfo.getAvatar());
                                    openUserInfoActivity(iv4, userinfo);
                                    break;
                            }
                        }
                    }
                }else {//无用户收藏该书
                    tvCount.setText("还没有用户收藏该书，赶紧收藏吧");
                    return;
                }
            }

            private void openUserInfoActivity(ImageView iv1, final UserInfo userinfo) {
                iv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(BookDetailActivity.this, UserInfoDetailActivity.class);
                        intent.putExtra("userinfo", userinfo);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(HttpException error, String msg) {

            }
        });
    }
}
