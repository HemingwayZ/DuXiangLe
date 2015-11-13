package com.zhm.duxiangle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
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
import com.zhm.duxiangle.bean.Friends;
import com.zhm.duxiangle.bean.Page;
import com.zhm.duxiangle.bean.UserInfo;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_friends)
public class FriendsActivity extends SlidingBackActivity implements SwipeRefreshLayout.OnRefreshListener {
    private LinearLayoutManager layoutManager;
    @ViewInject(R.id.swipeRefreshLayout)
    private SwipeRefreshLayout mSwipeLayout;
    @ViewInject(R.id.recycler)
    private RecyclerView recyclerView;
    private String userid;

    //toolbar
    @ViewInject(R.id.tvTitle)
    private TextView tvTitle;
    private List<Friends> friends;
    private List<UserInfo> friendsInfo;
    private UserListAdapter adapter;

    //
    @ViewInject(R.id.tvContent)
    private TextView tvContent;
    @ViewInject(R.id.ibBack)
    private ImageButton ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        friendsInfo = new ArrayList<>();
        tvTitle.setText("好友列表");
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
        adapter = new UserListAdapter(FriendsActivity.this, friendsInfo, "FriendsActivity");
        recyclerView.setAdapter(adapter);
        userid = getIntent().getStringExtra("userid");
        getFriendsFromNet();
    }

    /**
     * 网络获取信息
     */
    private void getFriendsFromNet() {
        mSwipeLayout.setRefreshing(true);
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "get_friends_info");
        params.addBodyParameter("userid", userid);
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getFriendsApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mSwipeLayout.setRefreshing(false);
                String result = responseInfo.result;
                if ("action is null".equals(result)) {
                    return;
                }
                if ("userid is null".equals(result)) {
                    return;
                }
                friendsInfo = GsonUtils.getInstance().getFriendsInfo(result);
                if (friendsInfo == null) {
                    tvContent.setText("还木有好友");
                }
                if (adapter != null && friendsInfo != null) {
                    adapter.setUserInfoList(friendsInfo);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                mSwipeLayout.setRefreshing(false);
                ToastUtils.showToast(getApplicationContext(), "网络链接失败");
                tvContent.setText("网络链接失败");
            }
        });
    }

    @Override
    public void onRefresh() {
        getFriendsFromNet();
    }


}
