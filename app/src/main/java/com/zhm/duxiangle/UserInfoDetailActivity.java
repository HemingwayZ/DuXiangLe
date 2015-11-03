package com.zhm.duxiangle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.bean.UserInfo;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

@ContentView(R.layout.activity_user_info_detail)
public class UserInfoDetailActivity extends SlidingBackActivity implements View.OnClickListener {
    @ViewInject(R.id.tvUserInfo)
    private TextView tvUserInfo;
    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;
    String Token = "TgvtMFddoNkHDeWcaXtKWwB9ft/fZ3RIRK/GfxqI/3AS+vgXGRPNaiQ6XcHmxeendjCnD8jE8K6z8kfj1J8WUA==";//test userid=2
    @ViewInject(R.id.toolbar_layout)
    private CollapsingToolbarLayout collapsingToolbarLayout;
    @ViewInject(R.id.ibBack)
    private ImageButton ivVBack;
    UserInfo userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUtils.inject(this);

        initData(getIntent());
        setSupportActionBar(toolbar);
        ivVBack.setOnClickListener(this);
        toolbar.getLogo();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                enterConversation(view);
            }
        });
    }

    /**
     * 进入聊天
     *
     * @param view
     */
    private void enterConversation(final View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * IMKit SDK调用第二步
                 *
                 * 建立与服务器的连接
                 *
                 */
                RongIM.connect(Token, new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                        //Connect Token 失效的状态处理，需要重新获取 Token
                        Snackbar.make(view, "Token错误", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                    /**
                     * 连接融云成功
                     */
                    @Override
                    public void onSuccess(String userId) {
                        Log.e("MainActivity", "——onSuccess— -" + userId);

                        /**
                         * 启动单聊
                         * context - 应用上下文。
                         * targetUserId - 要与之聊天的用户 Id。
                         * title - 聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
                         */
                        //{"code":200,"userId":"2462","token":"4Cp7WFQq92h1xjdmdaL5AXM//2Y39LDCnuxr2xdDagUSew9ILDZp6tvcV6rRhvbxbTnqk7cS56XBpjxS+NU4Ng=="}
                        //{"code":200,"userId":"1","token":"8FQcKXFvWDqN2j3qZWDA5nM//2Y39LDCnuxr2xdDagUSew9ILDZp6n9+OUnzkJ/4/W8bX6Y2cB4VGTWNrvchrA=="}
                        if (RongIM.getInstance() != null) {
                            RongIM.getInstance().startPrivateChat(UserInfoDetailActivity.this, String.valueOf(userinfo.getUserId()), userinfo.getNickname());
//                                    RongIM.getInstance().startConversationList(MessageActivity.this);
//                                    RongIM.getInstance().startConversation(MessageActivity.this, Conversation.ConversationType.APP_PUBLIC_SERVICE,"2","aaa");
                        } else {
                            Snackbar.make(view, "初始化失败", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        //回话列表
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        Log.e("MainActivity", "——onError— -" + errorCode);
                        Snackbar.make(view, "访问服务器失败", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        }).start();
    }

    private void initData(Intent intent) {
        userinfo = (UserInfo) intent.getSerializableExtra("userinfo");
        if (userinfo != null)
            tvUserInfo.setText(userinfo.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibBack:
                onBackPressed();
                break;
        }
    }
}
