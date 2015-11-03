package com.zhm.duxiangle.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhm.duxiangle.BookDetailActivity;
import com.zhm.duxiangle.R;
import com.zhm.duxiangle.bean.Book;
import com.zhm.duxiangle.utils.BitmapUtils;

import java.util.List;

/**
 * Created by zhuanghm on 2015/10/22.
 */
public class HomeRecycleViewAdapter extends RecyclerView.Adapter<HomeRecycleViewAdapter.MyViewHolder> {
    private List<Book> mData;
    private Context mContext;

    public HomeRecycleViewAdapter(List<Book> mData, Context context) {
        this.mData = mData;
        this.mContext = context;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                mContext).inflate(R.layout.fragment_home_item, parent,
                false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(HomeRecycleViewAdapter.MyViewHolder holder, final int position) {
        BitmapUtils.getInstance(mContext).setAvatar(holder.ivBookCover, mData.get(position).getImage(), null);
        holder.tvTitle.setText("书名:" + mData.get(position).getTitle());
        holder.tvAuthor.setText("作者:" + mData.get(position).getStrAuthor());
        holder.tvIsbn.setText("ISBN:" + mData.get(position).getIsbn13());
        holder.ivBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, BookDetailActivity.class);
                intent.putExtra("book", mData.get(position));
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @ViewInject(R.id.book_cover)
        ImageView ivBookCover;
        @ViewInject(R.id.tvTitle)
        TextView tvTitle;
        @ViewInject(R.id.tvAuthor)
        TextView tvAuthor;
        @ViewInject(R.id.tvIsbn)
        TextView tvIsbn;

        public MyViewHolder(View view) {
            super(view);
            ViewUtils.inject(this, view);//以注解的方式载入视图对象
//            ivBookCover = (ImageView) view.findViewById(R.id.book_cover);
//            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
//            tvAuthor = (TextView) view.findViewById(R.id.tvAuthor);
//            tvIsbn = (TextView) view.findViewById(R.id.tvIsbn);
        }
    }
}