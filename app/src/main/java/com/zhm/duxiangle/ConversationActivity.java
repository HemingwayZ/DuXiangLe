package com.zhm.duxiangle;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Locale;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;

public class ConversationActivity extends SlidingBackActivity {

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
        getIntentDate(getIntent());
    }

    /**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     */
    private void getIntentDate(Intent intent) {

        String mTargetId = intent.getData().getQueryParameter("targetId");
        String mTargetIds = intent.getData().getQueryParameter("targetIds");
        //intent.getData().getLastPathSegment();//获得当前会话类型
        Conversation.ConversationType mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        enterFragment(mConversationType, mTargetId);
    }

    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType 会话类型
     * @param mTargetId 目标 Id
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
    }
}
