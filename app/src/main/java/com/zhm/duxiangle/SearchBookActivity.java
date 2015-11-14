package com.zhm.duxiangle;

import android.content.Intent;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.adapter.HomeRecycleViewAdapter;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.bean.Book;
import com.zhm.duxiangle.bean.Page;
import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_search_book)
public class SearchBookActivity extends SlidingBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private int thispage = 0;
    private int rowperpage = 5;
    private User user;
    //搜索视图
    @ViewInject(R.id.etSearch)
    private EditText etSearch;
    @ViewInject(R.id.btnSearch)
    private Button btnSearch;

    @ViewInject(R.id.recycler)
    private RecyclerView recyclerView;
    @ViewInject(R.id.swipeRefreshLayout)
    private SwipeRefreshLayout mSwipeLayout;
    private LinearLayoutManager layoutManager;
    private String keywords;
    private Page<Book> pageBooks;
    private HomeRecycleViewAdapter adapter;
    private List<Book> list;

    //后退
    @ViewInject(R.id.ibBack)
    private ImageButton ibBack;
    @ViewInject(R.id.tvTitle)
    private TextView tvTitle;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        ibBack.setOnClickListener(this);
        userid = String.valueOf(getIntent().getIntExtra("userid", 0));

        tvTitle.setText("搜索书库");
//        getUser();
        pageBooks = new Page<>();
        list = new ArrayList<>();
        btnSearch.setOnClickListener(this);
        //三设置下拉刷新监听事件和进度条
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        mSwipeLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        //设置recycleView和相应的适配器需要设置布局管理器，否则会报错
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HomeRecycleViewAdapter(list, SearchBookActivity.this);
        recyclerView.setAdapter(adapter);
        //设置recycleView上拉加载更多的方法
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (layoutManager.findLastCompletelyVisibleItemPosition() == list.size() - 1) {

                            if (thispage >= pageBooks.getCountrow()) {
                                Snackbar.make(mSwipeLayout, "已到尾页", Snackbar.LENGTH_SHORT).show();
                                return;
                            }

                            Log.i("LastItem", String.valueOf(layoutManager.findLastCompletelyVisibleItemPosition()));
                            searchBooks(keywords, thispage, rowperpage);
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

        searchBooks("", thispage, rowperpage);
    }

    public void getUser() {
        //获取用户信息
        String json = SpUtil.getSharePerference(getApplicationContext()).getString("user", "");
        if (!TextUtils.isEmpty(json)) {
            user = GsonUtils.getInstance().json2Bean(json, User.class);
            if (user != null) {

            } else {
                Intent intent = new Intent(SearchBookActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                list.removeAll(list);
                keywords = etSearch.getText().toString().trim();
                mSwipeLayout.setRefreshing(true);
                thispage = 0;
                searchBooks(keywords, thispage, rowperpage);
                break;
            case R.id.ibBack:
                onBackPressed();
                break;
        }
    }

    private void searchBooks(String keywords, int _thispage, int _rowperpage) {
        mSwipeLayout.setRefreshing(true);
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "searchbook");
        params.addBodyParameter("keywords", keywords);
        params.addBodyParameter("userid", userid);
        params.addBodyParameter("thispage", String.valueOf(_thispage));
        params.addBodyParameter("rowperpage", String.valueOf(_rowperpage));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.bookApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mSwipeLayout.setRefreshing(false);
                String result = responseInfo.result;
                Log.i("Result:", result);
                if ("action is null".equals(result)) {
                    Snackbar.make(btnSearch, result, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if ("userid is null".equals(result)) {
                    Snackbar.make(btnSearch, result, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                pageBooks = GsonUtils.getInstance().getPageBooks(result);
                if (thispage == 0) {
                    Snackbar.make(btnSearch, "共搜到" + pageBooks.getCountrow() + "条", Snackbar.LENGTH_SHORT).show();
                }
                if (pageBooks.getList() != null && pageBooks.getList().size() > 0) {
                    list.addAll(pageBooks.getList());
                    thispage += list.size();
                    if (adapter != null) {
                        adapter.setBooks(list);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                mSwipeLayout.setRefreshing(false);
                Snackbar.make(btnSearch, "网络连接失败", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(false);
    }
}
