package com.zhm.duxiangle.utils;

import com.lidroid.xutils.HttpUtils;

/**
 * Created by Administrator on 2015/11/3.
 */
public class DXLHttpUtils {
    public static HttpUtils getHttpUtils() {
        HttpUtils http = new HttpUtils();
        http.configTimeout(3000);
        return http;
    }
}
