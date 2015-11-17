package com.zhm.duxiangle;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.zhm.duxiangle.adapter.UserListAdapter;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.bean.UserInfo;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_search_user)
public class SearchUserActivity extends SlidingBackActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    //搜索
    @ViewInject(R.id.btnSearch)
    private Button btnSearch;
    @ViewInject(R.id.etSearch)
    private EditText etSearch;
    //工具栏
    @ViewInject(R.id.ibBack)
    private ImageButton ivBack;
    @ViewInject(R.id.tvTitle)
    private TextView tvTitle;

    @ViewInject(R.id.swipeRefreshLayout_userlist)
    SwipeRefreshLayout mSwipeLayout;
    @ViewInject(R.id.recycler_userlist)
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<UserInfo> infoList;
    private UserListAdapter adapter;

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        initToolBar();
        infoList = new ArrayList<>();
        btnSearch.setOnClickListener(this);
        //三设置下拉刷新监听事件和进度条
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        mSwipeLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(SearchUserActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new UserListAdapter(SearchUserActivity.this, infoList);
        recyclerView.setAdapter(adapter);
    }

    private void initToolBar() {
        ivBack.setOnClickListener(this);
        tvTitle.setText("用户搜索");
    }

    private void initData(String keywords) {
        mSwipeLayout.setRefreshing(true);
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "search_user");
        params.addBodyParameter("keywords", keywords);
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getUserInfoApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mSwipeLayout.setRefreshing(false);
                String result = responseInfo.result;
                if ("action is null".equals(result)) {
                    return;
                }
                if ("keywords is null".equals(result)) {
                    return;
                }
                infoList = GsonUtils.getInstance().getFriendsInfo(result);
                if (adapter != null) {
                    adapter.setUserInfoList(infoList);
                    adapter.notifyDataSetChanged();
                }
//                tvContent.setText(infoList.toString());
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                mSwipeLayout.setRefreshing(false);
                ToastUtils.showToast(SearchUserActivity.this,"服务器连接失败");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                String keywords = etSearch.getText().toString().trim();
                if (keywords.isEmpty()) {
                    Snackbar.make(v, "请输入待查询用户名", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                hide(v);
                initData(keywords);
                break;
            case R.id.ibBack:
                onBackPressed();
                break;
        }

    }

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(false);
    }
}
