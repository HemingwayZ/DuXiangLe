package com.zhm.duxiangle;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.SpUtil;
import com.zhm.duxiangle.utils.ToastUtils;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class ConversationListActivity extends SlidingBackActivity {
    String Token = "TgvtMFddoNkHDeWcaXtKWwB9ft/fZ3RIRK/GfxqI/3AS+vgXGRPNaiQ6XcHmxeendjCnD8jE8K6z8kfj1J8WUA==";//test userid=2
    private Intent intent;
    private List<com.zhm.duxiangle.bean.UserInfo> friendsInfo;

    //        String  Token = "8FQcKXFvWDqN2j3qZWDA5nM//2Y39LDCnuxr2xdDagUSew9ILDZp6n9+OUnzkJ/4/W8bX6Y2cB4VGTWNrvchrA==";//test userid=1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("消息列表");
        findViewById(R.id.ibBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //获取token
        String json = SpUtil.getSharePerference(getApplicationContext()).getString("user", "");
        user = GsonUtils.getInstance().json2Bean(json, User.class);
        if (user == null || TextUtils.isEmpty(user.getToken())) {
            Intent intent = new Intent(ConversationListActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            getFriendsFromNet();
        }
        intent = getIntent();
        if (intent != null && intent.getData() != null
                && intent.getData().getScheme().equals("rong")
                && intent.getData().getQueryParameter("push").equals("true")) {
            String id = intent.getData().getQueryParameter("pushId");
            RongIMClient.recordNotificationEvent(id);
        }

        enterFragment();
    }

    /**
     * 加载 会话列表 ConversationListFragment
     */
    private void enterFragment() {

        ConversationListFragment fragment = (ConversationListFragment) getSupportFragmentManager().findFragmentById(R.id.conversationlist);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build();
        Log.i(this.getClass().getSimpleName(), "host:" + uri.getHost());
        Log.i(this.getClass().getSimpleName(), "toString:" + uri.toString());
        Log.i(this.getClass().getSimpleName(), "getEncodedPath:" + uri.getEncodedPath());
        Log.i(this.getClass().getSimpleName(), "getUserInfo:" + uri.getUserInfo());

        fragment.setUri(uri);
    }

    /**
     * 网络获取信息
     */
    private void getFriendsFromNet() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "get_friends_info");
        params.addBodyParameter("userid", String.valueOf(user.getUserId()));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getFriendsApi(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if ("action is null".equals(result)) {
                    return;
                }
                if ("userid is null".equals(result)) {
                    return;
                }
                friendsInfo = GsonUtils.getInstance().getFriendsInfo(result);
                if (friendsInfo != null) {
                    for (com.zhm.duxiangle.bean.UserInfo userinfo : friendsInfo) {
                        if (userinfo != null) {
                            if (TextUtils.isEmpty(userinfo.getAvatar())) {
                                userinfo.setAvatar("");
                            }
                            if (userinfo.getAvatar() != null) {
                                if (userinfo.getAvatar().startsWith("http")) {
                                    RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(String.valueOf(userinfo.getUserId()), userinfo.getNickname(), Uri.parse(userinfo.getAvatar())));
                                } else {
                                    RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(String.valueOf(userinfo.getUserId()), userinfo.getNickname(), Uri.parse(DXLApi.BASE_URL + userinfo.getAvatar())));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                ToastUtils.showToast(getApplicationContext(), "网络链接失败");
            }
        });
    }

}
