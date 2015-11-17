package com.zhm.duxiangle.utils;

import com.lidroid.xutils.HttpUtils;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.RequestUserAgent;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/11/3.
 */
public class DXLHttpUtils {
    public static HttpUtils getHttpUtils() {
        HttpUtils http = new HttpUtils();
        http.configTimeout(10*1000);
        return http;
    }
}
