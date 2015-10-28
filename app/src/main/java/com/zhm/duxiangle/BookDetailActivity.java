package com.zhm.duxiangle;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
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
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.api.DouBanApi;
import com.zhm.duxiangle.bean.Book;
import com.zhm.duxiangle.bean.Images;
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.utils.DXLDbUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.LogUtils;
import com.zhm.duxiangle.utils.ToastUtils;

import java.util.List;

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
    //书籍信息
    @ViewInject(R.id.tvTitle)
    private TextView tvTitle;
    @ViewInject(R.id.tvAuthor)
    private TextView tvAuthor;
    @ViewInject(R.id.book_cover)
    private KenBurnsView bookCover;
    @ViewInject(R.id.tvIsbn)
    private TextView tvIsbn;
    //滚动条
    @ViewInject(R.id.nestedScrollView)
    private NestedScrollView nestedScrollView;
    // 进度条
    @ViewInject(R.id.progressBar_bookDetail)
    private ProgressBar progressBar;
    //悬浮按钮2
    @ViewInject(R.id.fabShare2)
    private FloatingActionButton fabShare2;

    //CreditsRollView
//    @ViewInject(R.id.creditsroll)
//    private CreditsRollView creditsRollView;


    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        ViewUtils.inject(this);
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
        bookCover.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int xEvent = (int) event.getX();
                int yEvent = (int) event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        int x = (int) v.getX();
                        int y = (int) v.getY();
                        Bitmap bitmap = ((BitmapDrawable) bookCover.getDrawable()).getBitmap();
//                        int pixel = bitmap.getPixel(xEvent,yEvent);
////获取颜色
//                        int redValue = Color.red(pixel);
//                        int blueValue = Color.blue(pixel);
//                        int greenValue = Color.green(pixel);
//                        Color color = new Color();
//                        collapsingToolbarLayout.setExpandedTitleColor(Color.argb(100,redValue,greenValue,blueValue));
//                        collapsingToolbarLayout.setBackgroundColor(Color.argb(100,redValue,greenValue,blueValue));
//                        bookCover.pause();
                        break;


                    case MotionEvent.ACTION_MOVE:
//                        bookCover.resume();
//                        bookCover.setVerticalScrollbarPosition(0);
                        break;
                    case MotionEvent.ACTION_UP:
                        bookCover.restart(xEvent, yEvent);
                        break;
                }
                return true;
            }
        });
        bookCover.setTransitionListener(new KenBurnsView.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                LogUtils.i(BookDetailActivity.this, transition.getDuration() + "--begin");
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                LogUtils.i(BookDetailActivity.this, transition.getDuration() + "--end");
            }
        });
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                saveData();

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
//                    toolbar.setLogo(bookCover.getDrawable());
                } else {
                    if (book != null) {
//                        toolbar.setLogo(null);
                    }
                }
                if (i > -132) {
                    fabShare2.setVisibility(View.GONE);
//                    toolbar.setLogo(null);
                }
            }
        });
        book = (Book) getIntent().getSerializableExtra("book");
        if (null == book) {
            getBookInfoByScan();
            getDataFromNet();
        }else{
            tvTitle.setText("书名:" + book.getTitle());
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < book.getAuthor().size(); i++) {
                buffer.append(book.getAuthor().get(i));
            }
            tvAuthor.setText("作者:" + buffer.toString());
            tvIsbn.setText("ISBN:" + book.getIsbn13());
            collapsingToolbarLayout.setTitle(book.getTitle());
            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
            //拓展过后的标题颜色
            collapsingToolbarLayout.setExpandedTitleColor(Color.BLUE);
            collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.BOTTOM | Gravity.RIGHT);
            BitmapUtils.getInstance(getApplicationContext()).setBookAvatar(bookCover, book.getImages().getLarge(), toolbar);
            progressBar.setVisibility(View.GONE);
        }
    }

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
        http.send(HttpRequest.HttpMethod.POST, DouBanApi.getBookByIsbn(isbn), new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String json = responseInfo.result;
                book = GsonUtils.getInstance().json2Bean(json, Book.class);
                tvTitle.setText("书名:" + book.getTitle());
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < book.getAuthor().size(); i++) {
                    buffer.append(book.getAuthor().get(i));
                }
                tvAuthor.setText("作者:" + buffer.toString());
                tvIsbn.setText("ISBN:" + book.getIsbn13());
                collapsingToolbarLayout.setTitle(book.getTitle());
                collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
                //拓展过后的标题颜色
                collapsingToolbarLayout.setExpandedTitleColor(Color.BLUE);
                collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.BOTTOM | Gravity.RIGHT);
                BitmapUtils.getInstance(getApplicationContext()).setBookAvatar(bookCover, book.getImages().getLarge(), toolbar);
                progressBar.setVisibility(View.GONE);
//                creditsRollView.setText(json);
                saveData();
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
}
