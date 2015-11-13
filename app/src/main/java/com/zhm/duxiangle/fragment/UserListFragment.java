package com.zhm.duxiangle.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.LoginActivity;
import com.zhm.duxiangle.R;
import com.zhm.duxiangle.adapter.UserListAdapter;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.bean.Constant;
import com.zhm.duxiangle.bean.Page;
import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.bean.UserInfo;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.SpUtil;
import com.zhm.duxiangle.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View view;

    @ViewInject(R.id.swipeRefreshLayout_userlist)
    SwipeRefreshLayout mSwipeLayout;
    @ViewInject(R.id.recycler_userlist)
    private RecyclerView recyclerView;
    private OnFragmentInteractionListener mListener;
    private int rowperpage = 4;//每页的条数
    private int thispage = 0;//起始页
    private int countRow = 0;
    private String action = "userinfopage";

    private Page<UserInfo> page;

    //未登录状态
    @ViewInject(R.id.isLogin)
    private ImageView isLogin;
    UserListAdapter userListAdapter;
    List<UserInfo> list;
    //recycleView布局管理器
    LinearLayoutManager layoutManager;
    //消息处理
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.REFRESH_DOWN://下拉刷新
                    if (countRow > 0) {
                        getUserFromNet(0, countRow, Constant.REFRESH_DOWN);
                    }
                    mSwipeLayout.setRefreshing(false);
                    break;
                case Constant.REFRESH_UP://上拉加载更多
                    if (page != null && thispage <= page.getCountrow() - 1) {
                        getUserFromNet(thispage, rowperpage, Constant.REFRESH_UP);
                    } else {
                        Snackbar.make(recyclerView, "已到尾页", Snackbar.LENGTH_SHORT).show();
                    }
                    mSwipeLayout.setRefreshing(false);
                    break;
            }
        }
    };
    private User user;

    @Override
    public void onDestroy() {
        ToastUtils.cancelToast();
        super.onDestroy();
    }

    private int lastVisibleItem = 0;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserListFragment newInstance(String param1, String param2) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UserListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * 联网获取数据
     */
    private void getUserFromNet(int _thispage, int _rowperpage, final int type) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", action);
        params.addBodyParameter("thispage", String.valueOf(_thispage));
        params.addBodyParameter("rowperpage", String.valueOf(_rowperpage));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getUserListByPage(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                String result = responseInfo.result;

                page = GsonUtils.getInstance().getUserInfos(result);
//                tvContent.setText(page.toString());
                if (page.getList() != null && page.getList().size() > 0) {
                    //分页逻辑
                    switch (type) {
                        case Constant.REFRESH_UP:
                            thispage += page.getList().size();
                            list.addAll(page.getList());
                            break;
                        case Constant.REFRESH_DOWN:
                            list.removeAll(list);
                            list.addAll(page.getList());
                            break;
                    }
                    countRow = list.size();
                    userListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
            }
        });
    }

    public User getUser() {
        //获取用户信息
        String json = SpUtil.getSharePerference(getActivity()).getString("user", "");
        if (!TextUtils.isEmpty(json)) {
            return GsonUtils.getInstance().json2Bean(json, User.class);

        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //初始化参数
        thispage = 0;
        countRow = 1;
        lastVisibleItem = 0;
        view = inflater.inflate(R.layout.fragment_user_list, container, false);
        ViewUtils.inject(this, view);


        //三设置下拉刷新监听事件和进度条
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        mSwipeLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        //未登录处理
        user = getUser();
        if (user == null) {
            isLogin.setVisibility(View.VISIBLE);
            isLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            });
            return view;
        }
        list = new ArrayList<UserInfo>();
        userListAdapter = new UserListAdapter(getActivity(), list);
        recyclerView.setAdapter(userListAdapter);
        getUserFromNet(thispage, rowperpage, Constant.REFRESH_UP);

        //设置recycleView上拉加载更多的方法
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (userListAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == userListAdapter.getItemCount()) {
                    mSwipeLayout.setRefreshing(true);
                    // 此处在现实项目中，请换成网络请求数据代码，sendRequest .....
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.what = Constant.REFRESH_UP;
                            handler.sendMessage(message);
                        }
                    }).start();
                }
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        Message msg = new Message();
        msg.what = Constant.REFRESH_DOWN;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        handler.sendMessage(msg);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
