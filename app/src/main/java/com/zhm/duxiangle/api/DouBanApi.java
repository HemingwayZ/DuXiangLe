package com.zhm.duxiangle.api;

public class DouBanApi {

    private static final String DouBanApi = "https://api.douban.com/v2";

    /**
     * 根据isbn获取书籍信息(https://api.douban.com/v2/book/isbn/:9787111128069)
     */
    public static String getBookByIsbn(String isbn) {
        return DouBanApi + "/book/isbn/:" + isbn;
    }

}
