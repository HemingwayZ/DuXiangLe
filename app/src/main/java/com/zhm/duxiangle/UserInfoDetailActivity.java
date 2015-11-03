package com.zhm.duxiangle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.bean.UserInfo;
import com.zhm.duxiangle.utils.BitmapUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

@ContentView(R.layout.activity_user_info_detail)
public class UserInfoDetailActivity extends SlidingBackActivity implements View.OnClickListener {
    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;
    @ViewInject(R.id.toolbar_layout)
    private CollapsingToolbarLayout collapsingToolbarLayout;
    @ViewInject(R.id.ibBack)
    private ImageButton ivVBack;
    UserInfo userinfo;

    //发送消息和编辑资料
    @ViewInject(R.id.btnSend)
    private Button btnSend;
    @ViewInject(R.id.btnEdit)
    private Button btnEdit;

    //资料部分
    @ViewInject(R.id.etNickname)
    private EditText etNickname;
    @ViewInject(R.id.etCreated)
    private EditText etCreated;
    @ViewInject(R.id.etDesc)
    private EditText etDesc;


    String Token = "TgvtMFddoNkHDeWcaXtKWwB9ft/fZ3RIRK/GfxqI/3AS+vgXGRPNaiQ6XcHmxeendjCnD8jE8K6z8kfj1J8WUA==";//test userid=2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewUtils.inject(this);

        initData(getIntent());
        setSupportActionBar(toolbar);

        toolbar.getLogo();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (userinfo.getAvatar() != null)
            BitmapUtils.getInstance(getApplicationContext()).setAvatar(fab, userinfo.getAvatar());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                enterConversation(view);
            }
        });
        etDesc.setFocusable(false);
        etCreated.setFocusable(false);
        etNickname.setFocusable(false);
        etCreated.setClickable(false);
        etNickname.setClickable(false);
        etDesc.setClickable(false);
        ivVBack.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnSend.setOnClickListener(this);
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
        if (userinfo != null) {
            etCreated.setText(userinfo.getCreated());
            etNickname.setText(userinfo.getNickname());
            etDesc.setText(userinfo.getDescrib());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibBack:
                onBackPressed();
                break;
            case R.id.btnSend:
                enterConversation(v);
                break;
            case R.id.btnEdit:

                break;
        }
    }
}
