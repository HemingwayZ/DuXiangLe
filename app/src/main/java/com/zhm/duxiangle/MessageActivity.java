package com.zhm.duxiangle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class MessageActivity extends SlidingBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        String Token = "4Cp7WFQq92h1xjdmdaL5AXM//2Y39LDCnuxr2xdDagUSew9ILDZp6tvcV6rRhvbxbTnqk7cS56XBpjxS+NU4Ng==";//test

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
            }

            @Override
            public void onSuccess(String userId) {
                Log.e("MainActivity", "——onSuccess— -" + userId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e("MainActivity", "——onError— -"+errorCode);
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /**
                 * 启动单聊
                 * context - 应用上下文。
                 * targetUserId - 要与之聊天的用户 Id。
                 * title - 聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
                 */
                //{"code":200,"userId":"2462","token":"4Cp7WFQq92h1xjdmdaL5AXM//2Y39LDCnuxr2xdDagUSew9ILDZp6tvcV6rRhvbxbTnqk7cS56XBpjxS+NU4Ng=="}
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(MessageActivity.this, "2462", "hello");
                }
            }
        });
    }
}
