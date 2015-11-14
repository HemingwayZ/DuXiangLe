package com.zhm.duxiangle;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.utils.DXLHttpUtils;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.SpUtil;
import com.zhm.duxiangle.utils.ToastUtils;

import org.w3c.dom.Text;

import java.util.Locale;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.LocationInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

public class ConversationActivity extends SlidingBackActivity {
    com.zhm.duxiangle.bean.UserInfo userinfo;
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        findViewById(R.id.ibBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        getUser();
        getIntentDate(getIntent());
    }

    private void getUser() {
        userinfo = new com.zhm.duxiangle.bean.UserInfo();
        //获取用户信息
        String json = SpUtil.getSharePerference(getApplicationContext()).getString("user", "");
        if (!TextUtils.isEmpty(json)) {
            User user = GsonUtils.getInstance().json2Bean(json, User.class);
            if (user != null) {
                if (user.getToken() != null) {
//                    getUserInfo(user.getUserId());
                }
            } else {
                Intent intent = new Intent(ConversationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    private void getUserInfo(int userId) {
        userinfo = new com.zhm.duxiangle.bean.UserInfo();
        userinfo.setUserId(userId);
        RequestParams params = new RequestParams();
        params.addBodyParameter("action", "userinfo");
        params.addBodyParameter("userid", String.valueOf(userId));
        DXLHttpUtils.getHttpUtils().send(HttpRequest.HttpMethod.POST, DXLApi.getUserListByPage(), params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i(MainActivity.class.getSimpleName(), result);
                if ("no found".equals(result)) {
                    tvTitle.setText("");
                    return;
                }
                if ("action".equals(result)) {
                    return;
                }
                userinfo = GsonUtils.getInstance().json2Bean(result, com.zhm.duxiangle.bean.UserInfo.class);
                if (userinfo == null) {
                    return;
                }
                if (userinfo.getNickname() != null)
                    tvTitle.setText("与 " + userinfo.getNickname() + " 聊天");
                if (!TextUtils.isEmpty(userinfo.getAvatar()))
                    RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(String.valueOf(userinfo.getUserId()), userinfo.getNickname(), Uri.parse(DXLApi.BASE_URL + userinfo.getAvatar())));

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Log.i(ConversationActivity.class.getSimpleName(), "" + msg);
            }
        });
    }

    /**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     */
    private void getIntentDate(Intent intent) {
        String mTargetId = intent.getData().getQueryParameter("targetId");
        if (!TextUtils.isEmpty(mTargetId))
            getUserInfo(Integer.valueOf(mTargetId));
        String mTargetIds = intent.getData().getQueryParameter("targetIds");
        //intent.getData().getLastPathSegment();//获得当前会话类型
        Conversation.ConversationType mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        enterFragment(mConversationType, mTargetId);

    }

    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType 会话类型
     * @param mTargetId         目标 Id
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        //rong://com.zhm.duxiangle/conversation/private?targetId=1
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();
        Log.i("uri:", uri.toString());
        /**
         * 设置用户信息的提供者，供 RongIM 调用获取用户名称和头像信息。
         *
         * @param userInfoProvider 用户信息提供者。
         * @param isCacheUserInfo  设置是否由 IMKit 来缓存用户信息。<br>
         *                         如果 App 提供的 UserInfoProvider。
         *                         每次都需要通过网络请求用户数据，而不是将用户数据缓存到本地内存，会影响用户信息的加载速度；<br>
         *                         此时最好将本参数设置为 true，由 IMKit 将用户信息缓存到本地内存中。
         * @see UserInfoProvider
         */
//        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
//
//            @Override
//            public UserInfo getUserInfo(String userId) {
//
//                return findUserById(userId);//根据 userId 去你的用户系统里查询对应的用户信息返回给融云 SDK。
//            }
//
//        }, true);

        RongIM.ConversationBehaviorListener listener = new RongIM.ConversationBehaviorListener() {
            @Override
            public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                Intent intent = new Intent(ConversationActivity.this, UserInfoDetailActivity.class);
                intent.putExtra("userinfo", userinfo);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                Intent intent = new Intent(ConversationActivity.this, UserInfoDetailActivity.class);
                intent.putExtra("userinfo", userinfo);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onMessageClick(Context context, View view, Message message) {
                ToastUtils.showToast(ConversationActivity.this, message.getExtra());
                return true;
            }

            @Override
            public boolean onMessageLinkClick(Context context, String s) {
                return false;
            }

            @Override
            public boolean onMessageLongClick(Context context, View view, Message message) {
                return false;
            }
        };

        fragment.setUri(uri);
    }


}
