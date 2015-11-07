package com.zhm.duxiangle.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.R;
import com.zhm.duxiangle.UserInfoDetailActivity;
import com.zhm.duxiangle.api.DXLApi;
import com.zhm.duxiangle.bean.UserInfo;
import com.zhm.duxiangle.utils.BitmapUtils;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> implements View.OnClickListener {
    private Context mContext;
    private List<UserInfo> userInfoList;

    /**
     * @param mContext     上下文
     * @param userInfoList 用户列表信息
     */
    public UserListAdapter(Context mContext, List<UserInfo> userInfoList) {
        this.mContext = mContext;
        this.userInfoList = userInfoList;
    }

    @Override
    public UserListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_user_list_item, null);
        view.setOnClickListener(this);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserListAdapter.MyViewHolder holder, int position) {
        final UserInfo userInfo = userInfoList.get(position);
        if (!TextUtils.isEmpty(userInfo.getAvatar())) {
            BitmapUtils.getInstance(mContext).setAvatar(holder.ivUser, DXLApi.BASE_URL+userInfo.getAvatar(), null);
        }
        holder.tvNickname.setText(userInfo.getNickname());
        holder.tvDesc.setText(userInfo.getDescrib());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, UserInfoDetailActivity.class);
                intent.putExtra("userinfo", userInfo);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userInfoList.size();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.layout.fragment_user_list_item:
        Intent intent = new Intent();
        intent.setClass(mContext, UserInfoDetailActivity.class);
        mContext.startActivity(intent);
//                break;
//        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @ViewInject(R.id.ivUser)
        ImageView ivUser;
        @ViewInject(R.id.tvNickname)
        TextView tvNickname;
        @ViewInject(R.id.tvDesc)
        TextView tvDesc;

        public MyViewHolder(View itemView) {
            super(itemView);
            ViewUtils.inject(this, itemView);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.layout.fragment_user_list_item:

                    break;
            }
        }
    }
}