package com.zhm.duxiangle;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zhm.duxiangle.bean.User;
import com.zhm.duxiangle.utils.GsonUtils;
import com.zhm.duxiangle.utils.SpUtil;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class ConversationListActivity extends SlidingBackActivity {
    String Token = "TgvtMFddoNkHDeWcaXtKWwB9ft/fZ3RIRK/GfxqI/3AS+vgXGRPNaiQ6XcHmxeendjCnD8jE8K6z8kfj1J8WUA==";//test userid=2

    //        String  Token = "8FQcKXFvWDqN2j3qZWDA5nM//2Y39LDCnuxr2xdDagUSew9ILDZp6n9+OUnzkJ/4/W8bX6Y2cB4VGTWNrvchrA==";//test userid=1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("消息列表");
        //获取token
        String json = SpUtil.getSharePerference(getApplicationContext()).getString("user", "");
        User user = GsonUtils.getInstance().json2Bean(json, User.class);
        if (user == null) {
            Intent intent = new Intent(ConversationListActivity.this, LoginActivity.class);
            startActivity(intent);
            return;
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
//        fragment.onEventMainThread(new UserInfo());
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
//                RongIM.getInstance().D
//                //先从获取数据库操作的实例
//                FriendDao friendDao = DBManager.getInstance(ConversationListActivity.this)
//                        .getDaoSession().getFriendDao();
//                //获取数据库中我所有好友的bean对象
//                List<Friend> friends = friendDao.loadAll();
//                if (friends != null && friends.size() > 0) {
//                    //增强for把所有的用户信息 return 给融云
//                    for (Friend friend : friends) {
//                        //判断返回的userId
//                        if (friend.getRongId().equals(userId)) {
//                            return new UserInfo(friend.getRongId(), friend.getName(),
//                                    Uri.parse(friend.getPortraitUri()));
//                        }
//                    }
//                }
                return null;
            }
        }, true);
        fragment.setUri(uri);
    }


}
