package com.zhm.duxiangle.api;

import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class DouBanApi {

    private static final String DouBanApi = "https://api.douban.com/v2";

    /**
     * 根据isbn获取书籍信息(https://api.douban.com/v2/book/isbn/:9787111128069)
     */
    public static String getBookByIsbn(String isbn) {
        return DouBanApi + "/book/isbn/:" + isbn;
    }


    //https://api.douban.com/v2/book/search?q=%22%22
    public static String searchBooksFromDouBanApi(String q, int start, int count) {
//        API Key 028f9ddc0036915507aed99400a3b3ff
//        Secret 8664adb103b289b2
        String url = "";

//        return "https://api.douban.com/v2/book/search?apikey=028f9ddc0036915507aed99400a3b3ff&q='" + q + "'";
//        return "http://192.168.23.1:8080/DuXiangLeServer/DouBanServlet";
        return "https://api.douban.com/v2/book/search?apikey=028f9ddc0036915507aed99400a3b3ff&q='" + q + "'&start=" + start + "&count=" + count;
    }

}
