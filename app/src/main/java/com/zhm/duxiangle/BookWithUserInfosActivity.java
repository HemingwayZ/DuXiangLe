package com.zhm.duxiangle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.adapter.UserListAdapter;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.bean.Book;
import com.zhm.duxiangle.bean.Page;
import com.zhm.duxiangle.bean.UserInfo;
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;

import java.util.ArrayList;

@ContentView(R.layout.activity_book_with_user_infos)
public class BookWithUserInfosActivity extends SlidingBackActivity implements SwipeRefreshLayout.OnRefreshListener {
    private int thispage = 0;
    private int rowpaerpage = 5;
    public Page<UserInfo> userInfos;
    private Book book;
    private LinearLayoutManager layoutManager;
    private ArrayList<UserInfo> userinfoList;
    private UserListAdapter adapter;
    //视图注入
    @ViewInject(R.id.swipeRefreshLayout_userlist)
    private SwipeRefreshLayout mSwipeLayout;
    @ViewInject(R.id.recycler_userlist)
    private RecyclerView recyclerView;

    @ViewInject(R.id.ibBack)
    private ImageButton ibBack;
    @ViewInject(R.id.tvTitle)
    private TextView tvTitle;
    //书籍信息
    @ViewInject(R.id.bookCover)
    private ImageView bookCover;
    @ViewInject(R.id.tvBookTitle)
    private TextView tvBookTitle;
    @ViewInject(R.id.tvAuthor)
    private TextView tvAuthor;
    @ViewInject(R.id.tvIsbn)
    private TextView tvIsbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        initData();
        initView();
    }

    private void initView() {
        //三设置下拉刷新监听事件和进度条
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        mSwipeLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(BookWithUserInfosActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        userinfoList = new ArrayList<>();
        adapter = new UserListAdapter(BookWithUserInfosActivity.this, userinfoList);
        recyclerView.setAdapter(adapter);
        if (book != null) {
            getUserInfoByIsbn(book.getIsbn13(), thispage);
            tvTitle.setText("收藏：" + book.getTitle());
        }
        //设置标题栏
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //设置recycleView上拉加载更多的方法
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (layoutManager.findLastCompletelyVisibleItemPosition() == userinfoList.size() - 1) {

                            if (thispage >= userInfos.getCountrow()) {
                                Snackbar.make(mSwipeLayout, "已到尾页", Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                            mSwipeLayout.setRefreshing(true);
                            Log.i("LastItem", String.valueOf(layoutManager.findLastCompletelyVisibleItemPosition()));
                            getUserInfoByIsbn(book.getIsbn13(), thispage);
                        }

                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    private void initData() {
        book = (Book) getIntent().getSerializableExtra("book");
        BitmapUtils.getInstance(BookWithUserInfosActivity.this).setAvatarWithoutReflect(bookCover, book.getImage());
        bookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookWithUserInfosActivity.this, WebImageActivity.class);
                intent.putExtra("url", book.getImage());
                startActivity(intent);
            }
        });
        tvBookTitle.setText(book.getTitle());
        tvAuthor.setText(book.getStrAuthor());
        tvIsbn.setText(book.getIsbn13());

    }

    public void getUserInfoByIsbn(String isbn, int _thispage) {
        mSwipeLayout.setRefreshing(true);
        //http://localhost:8080/DuXiangLeServer/UserInfoServlet?action=find_userinfo_by_isbn&isbn=9781453562680&rowperpage=2
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "find_userinfo_by_isbn");
        params.addBodyParameter("isbn", isbn);
        params.addBodyParameter("thispage", String.valueOf(_thispage));
        params.addBodyParameter("rowperpage", String.valueOf(rowpaerpage));

        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getUserInfoApi(), params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mSwipeLayout.setRefreshing(false);
                String result = responseInfo.result;
//                LogUtils.i(BookDetailActivity.this,"zhm--result:"+result);
                if ("action is null".equals(result)) {
                    return;
                }
                if ("isbn_is_null".equals(result)) {
                    return;
                }
                userInfos = GsonUtils.getInstance().getUserInfos(result);
                if (userInfos.getList() != null && userInfos.getList().size() > 0) {
                    thispage += userInfos.getList().size();
                    userinfoList.addAll(userInfos.getList());
                    adapter.setUserInfoList(userinfoList);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(false);
    }
}