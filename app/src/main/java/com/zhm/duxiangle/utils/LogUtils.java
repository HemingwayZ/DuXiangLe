package com.zhm.duxiangle.utils;

import android.app.Activity;
import android.util.Log;

/**
 * Created by zhuanghm(183340093@qq.com) on 2015/10/8.
 */
public class LogUtils {
    /**
     * @param activity
     * @param text
     */
    public static void i(Activity activity, String text) {
        Log.i("log" + activity.getClass().getSimpleName(), text);
    }
}
