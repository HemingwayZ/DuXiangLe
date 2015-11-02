package com.zhm.duxiangle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.R;
import com.zhm.duxiangle.bean.UserInfo;
import com.zhm.duxiangle.utils.BitmapUtils;
import com.zhm.duxiangle.view.CircleImageView;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
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
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserListAdapter.MyViewHolder holder, int position) {
        UserInfo userInfo = userInfoList.get(position);
        BitmapUtils.getInstance(mContext).setBookAvatar(holder.ivUser, userInfo.getAvater(), null);
        holder.tvNickname.setText(userInfo.getNickname());
        holder.tvDesc.setText(userInfo.getDesc());
    }

    @Override
    public int getItemCount() {
        return userInfoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @ViewInject(R.id.ivUser)
        CircleImageView ivUser;
        @ViewInject(R.id.tvNickname)
        TextView tvNickname;
        @ViewInject(R.id.tvDesc)
        TextView tvDesc;

        public MyViewHolder(View itemView) {
            super(itemView);
            ViewUtils.inject(this, itemView);
        }

    }
}