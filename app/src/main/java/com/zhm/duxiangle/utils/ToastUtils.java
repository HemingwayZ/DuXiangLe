package com.zhm.duxiangle.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhm.duxiangle.R;


/**
 * Created by zhuanghm(183340093@qq.com) on 2015/10/10.
 */
public class ToastUtils {
    private static Toast toast;

    public static void showToast(Context mContext, String text) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.utils_toast, null);
        TextView txtView_Title = (TextView) view
                .findViewById(R.id.txt_Title);
        TextView txtView_Context = (TextView) view
                .findViewById(R.id.txt_context);
        txtView_Context.setText(text);
        ImageView imageView = (ImageView) view
                .findViewById(R.id.image_toast);
        toast = new Toast(mContext.getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
