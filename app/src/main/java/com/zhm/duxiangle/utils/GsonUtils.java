package com.zhm.duxiangle.utils;

import com.google.gson.Gson;

/**
 * Created by zhuanghm(183340093@qq.com) on 2015/10/9.
 */
public class GsonUtils {
    private static GsonUtils skHttpUtils;
    private Gson gson;

    private GsonUtils() {
    }

    public static GsonUtils getInstance() {
        if (null == skHttpUtils) {
            skHttpUtils = new GsonUtils();
        }
        return skHttpUtils;
    }

    public <T> T json2Bean(String json, Class<T> tClass) {
        //gson源码已经对json空值进行判断
        if (null == gson) {
            gson = new Gson();
        }
//        return gson.fromJson(json, new TypeToken<T>() {
//        }.getType());
        return gson.fromJson(json, tClass);
    }
}
