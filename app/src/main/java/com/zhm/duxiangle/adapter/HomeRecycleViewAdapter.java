package com.zhm.duxiangle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhm.duxiangle.R;

import java.util.List;

/**
 * Created by zhuanghm on 2015/10/22.
 */
public class HomeRecycleViewAdapter  extends RecyclerView.Adapter<HomeRecycleViewAdapter.MyViewHolder> {
    private List<String> mDatas;
    private Context mContext;

    public HomeRecycleViewAdapter(List<String> mData, Context context) {
        this.mDatas = mData;
        this.mContext = context;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.fragment_home_item, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(HomeRecycleViewAdapter.MyViewHolder holder, int position) {
        holder.tv.setText(mDatas.get(position));
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.id_num);
        }
    }
}