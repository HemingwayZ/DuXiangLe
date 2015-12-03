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
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnScrollStateChanged;
import com.zhm.duxiangle.LoginActivity;
import com.zhm.duxiangle.R;
import com.zhm.duxiangle.SearchBookActivity;
import com.zhm.duxiangle.adapter.HomeRecycleViewAdapter;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.bean.Book;
import com.zhm.duxiangle.bean.Images;
import com.zhm.duxiangle.bean.Page;
import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.utils.DXLDbUtils;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    //分页控制
    private int thispage = 0;
    private int rowperpage = 4;
    private int countRows = 0;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private User user;

    private Page<Book> bookPage;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookPage = new Page<Book>();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private final int REFRESH_COMPLETE = 100;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    if (bookList != null && bookList.size() > 0)
                        if (homeAdapter != null)
                            homeAdapter.notifyDataSetChanged();
                    mSwipeLayout.setRefreshing(false);
                    break;
                case 101:
                    if (bookList != null && bookList.size() > 0)
                        mSwipeLayout.setRefreshing(false);
                    break;
            }
        }

        ;
    };
    View view;
    @ViewInject(R.id.swipeRefreshLayout)
    private SwipeRefreshLayout mSwipeLayout;
    @ViewInject(R.id.recycler)
    private RecyclerView recyclerView;
    @ViewInject(R.id.book_cover)
    private ImageView ivBookCover;

    //搜索框
    @ViewInject(R.id.llSearch)
    private LinearLayout llSearch;
    @ViewInject(R.id.etSearch)
    private EditText etSearch;
    @ViewInject(R.id.btnSearch)
    private ImageButton btnSearch;
    //未登录状态
    @ViewInject(R.id.isLogin)
    private ImageView isLogin;
    private LinearLayoutManager layoutManager;
    private HomeRecycleViewAdapter homeAdapter;
    List<Book> bookList;

    @Override
    public void onStart() {
        super.onStart();
    }

    private void getDataFromNet(User user, int _thispage, int _rowperpage) {

        if (user == null || user.getUserId() <= 0) {
            return;
        }
        mSwipeLayout.setRefreshing(true);
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "pagebooks");
        params.addBodyParameter("userid", String.valueOf(user.getUserId()));
        params.addBodyParameter("thispage", String.valueOf(_thispage));
        params.addBodyParameter("rowperpage", String.valueOf(_rowperpage));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.bookApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mSwipeLayout.setRefreshing(false);
                String result = responseInfo.result;
                Log.i(this.getClass().getSimpleName(), result);
                if ("action is null".equals(result)) {
                    return;
                }
                if ("userid is null".equals(result)) {
                    return;
                }
                bookPage = GsonUtils.getInstance().getPageBooks(result);
                if (bookPage == null) {
                    return;
                }
                if (bookPage != null && bookPage.getList() != null) {
                    //分页控制
                    thispage += bookPage.getList().size();
                }
                if (bookPage.getList() != null && bookPage.getList().size() > 0) {
                    countRows += bookPage.getList().size();
                    bookList.addAll(bookPage.getList());
                    if (homeAdapter != null) {
                        homeAdapter.setBooks(bookList);
                        homeAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                mSwipeLayout.setRefreshing(false);
                Log.i("HomeFragment+onFailure", msg);
                getDataFromLocalDb();
            }
        });
    }

    /**
     * 从本地数据库获取书籍详情
     */
    private void getDataFromLocalDb() {
        DbUtils dbUtils = DXLDbUtils.getInstance(getActivity()).getDbByName(DXLDbUtils.DB_BOOK);
        if (dbUtils != null) {
            try {
                dbUtils.createTableIfNotExist(Book.class);

                bookList = new ArrayList<Book>();
                bookList = dbUtils.findAll(Book.class);

                //获取图片信息
                dbUtils.createTableIfNotExist(Images.class);

                if (bookList != null && bookList.size() > 0) {
                    for (int i = 0; i < bookList.size(); i++) {
                        //从头数据库获取图片对象
                        Images images = dbUtils.findFirst(Selector.from(Images.class).where("id", "=", bookList.get(i).getId()));
                        bookList.get(i).setImages(images);
                        //处理作者信息
                        if (bookList.get(i).getStrAuthor() != null) {
                            String[] author = bookList.get(i).getStrAuthor().split("\\+");
                            List<String> authorList = new ArrayList<>();
                            for (String str : author) {
                                authorList.add(str);
                            }
                            bookList.get(i).setAuthor(authorList);
                        }
                    }
                    if (homeAdapter != null) {
                        homeAdapter.notifyDataSetChanged();
                    }
                }

            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    public void getUser() {
        //获取用户信息
        String json = SpUtil.getSharePerference(getActivity()).getString("user", "");
        if (!TextUtils.isEmpty(json)) {
            user = GsonUtils.getInstance().json2Bean(json, User.class);
            if (user != null) {

            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        bookList = new ArrayList<Book>();
        getUser();
        //初始化数据
        thispage = 0;
        rowperpage = 4;
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ViewUtils.inject(this, view);
        //三设置下拉刷新监听事件和进度条
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        mSwipeLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        //设置recycleView和相应的适配器需要设置布局管理器，否则会报错
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        if (user == null) {
            isLogin.setVisibility(View.VISIBLE);
            isLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                }
            });
        }

        homeAdapter = new HomeRecycleViewAdapter(bookList, getActivity());
        recyclerView.setAdapter(homeAdapter);
        homeAdapter.notifyDataSetChanged();
        //设置recycleView上拉加载更多的方法
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (layoutManager.findLastCompletelyVisibleItemPosition() == bookList.size() - 1) {

                            if (bookPage != null && thispage >= bookPage.getCountrow()) {
                                Snackbar.make(mSwipeLayout, "已到尾页", Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                            mSwipeLayout.setRefreshing(true);
                            Log.i("LastItem", String.valueOf(layoutManager.findLastCompletelyVisibleItemPosition()));

                            getDataFromNet(user, thispage, rowperpage);
                            Message message = new Message();
                            message.what = 101;
                            mHandler.sendMessage(message);
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
        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchBookActivity.class);
                intent.putExtra("userid", user.getUserId());
                startActivity(intent);
            }
        });
        //初始化数据
        getDataFromNet(user, thispage, rowperpage);
        initSearch();
        return view;
    }

    private void initSearch() {
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchBookActivity.class);
                intent.putExtra("userid", user.getUserId());
                startActivity(intent);
            }
        });
        etSearch.setFocusable(false);
        etSearch.setClickable(false);
        btnSearch.setFocusable(false);
        btnSearch.setClickable(false);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        getDataFromNet(user, thispage, thispage);
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 1000);
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
