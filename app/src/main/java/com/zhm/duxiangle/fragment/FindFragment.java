package com.zhm.duxiangle.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.BookDetailActivity;
import com.zhm.duxiangle.BookPage;
import com.zhm.duxiangle.MainActivity;
import com.zhm.duxiangle.R;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.api.DouBanApi;
import com.zhm.duxiangle.bean.Book;
import com.zhm.duxiangle.bean.Constant;
import com.zhm.duxiangle.fragment.dummy.DummyContent;
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.ToastUtils;

import org.apache.http.Header;
import org.w3c.dom.Text;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class FindFragment extends Fragment implements AbsListView.OnItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private int start = 0;
    private int count = 9;
    private String search = "";
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;
    private String TAG = "FindFragment";
    BookPage bookPage;

    // TODO: Rename and change types of parameters
    public static FindFragment newInstance(String param1, String param2) {
        FindFragment fragment = new FindFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FindFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
    }

    /**
     * The fragment's ListView/GridView.
     */
    @ViewInject(android.R.id.list)
    private AbsListView mListView;
    @ViewInject(R.id.swipeRefreshLayout)
    private SwipeRefreshLayout mSwipeLayout;
    //搜索框部分
    @ViewInject(R.id.etSearch)
    private EditText etSearch;
    @ViewInject(R.id.btnSearch)
    private Button btnSearch;
    private boolean isRefresh = false;//判断是否正在刷新
    List<Book> books;
    SearchAdapter adapter;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.REFRESH_UP:
                    mSwipeLayout.setRefreshing(false);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_grid, container, false);
        ViewUtils.inject(this, view);
        //三设置下拉刷新监听事件和进度条
        bookPage = new BookPage();
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        mSwipeLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        books = new ArrayList<>();
        // Set the adapter

        adapter = new SearchAdapter();
        ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        btnSearch.setOnClickListener(this);
        getBooksFromDouBan(search, start, count);


        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                scrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        Log.i("SCROLL_STATE_IDLE", mListView.getLastVisiblePosition() + "");
                        if (mListView.getLastVisiblePosition() + 1 == bookPage.getTotal()) {
                            Snackbar.make(mListView, "已到尾页", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        if (isRefresh == false&&mListView.getLastVisiblePosition() + 1 == books.size()) {
                            isRefresh = true;//标志正在刷新
                            mSwipeLayout.setRefreshing(true);
                            getBooksFromDouBan(search, start, count);
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        return view;
    }
//    GET  https://api.douban.com/v2/book/search
//    参数	意义	备注
//    q	查询关键字	q和tag必传其一
//    tag	查询的tag	q和tag必传其一
//    start	取结果的offset	默认为0
//    count	取结果的条数	默认为20，最大为100

    private void getBooksFromDouBan(String q, int _start, final int _count) {
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.GET, DouBanApi.searchBooksFromDouBanApi(q, _start, _count), new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                isRefresh = false;
                String result = responseInfo.result;
                Log.i(TAG, result);
                bookPage = GsonUtils.getInstance().json2Bean(result, BookPage.class);
                if (bookPage.getBooks().size() > 0) {
                    if (start == 0) {
                        books.removeAll(books);
                        if (getActivity() instanceof MainActivity) {
                            if (((MainActivity) getActivity()).getViewPgeCurrentItem() == 1) {
                                Snackbar.make(mListView, "搜索到" + bookPage.getTotal() + "项", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                    start += count;
                    books.addAll(bookPage.getBooks());
//                    books = bookPage.getBooks();
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }
                mSwipeLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                isRefresh = false;
                ToastUtils.showToast(getActivity(), "FindFragment net failed" + msg);
                Log.i(TAG, "" + msg);
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        Message msg = new Message();
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
        msg.what = Constant.REFRESH_UP;
        handler.sendMessage(msg);
    }

    class SearchAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return books.size();
        }

        @Override
        public Object getItem(int position) {
            return books.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_find_item, null);
                holder = new ViewHolder();
                holder.ivBookCover = (ImageView) convertView.findViewById(R.id.ivBookCover);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            BitmapUtils.getInstance(getActivity()).setAvatar(holder.ivBookCover, books.get(position).getImage());
            holder.tvTitle.setText(books.get(position).getTitle());
            return convertView;
        }

        class ViewHolder {
            public ImageView ivBookCover;
            public TextView tvTitle;

            public ViewHolder() {
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), BookDetailActivity.class);
        intent.putExtra("book", books.get(position));
        startActivity(intent);
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();
        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                //搜索
                search = etSearch.getText().toString().trim();
                mSwipeLayout.setRefreshing(true);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
//                　　//显示键盘
//                　　imm.showSoftInput(editText, 0);
                //隐藏键盘
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                //
                start = 0;
                count = 9;
                getBooksFromDouBan(search, start, count);
                break;
        }
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
        public void onFragmentInteraction(String id);
    }

}
