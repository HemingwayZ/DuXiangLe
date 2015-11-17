package com.zhm.duxiangle.bean;

/**
 * 常数
 * Created by zhuanghm on 2015/11/3.
 */
public class Constant {
    //下拉刷新
    public static final int REFRESH_DOWN = 1;
    //上拉加载更多
    public static final int REFRESH_UP = 2;

    //从相册获取照片
    public static final int REQUEST_CODE_MEDIA = 3;

    public static final int REQUEST_CODE_CAPTURE_CAMERIA = 4;

    //微信id
    public static final String APP_ID_WECHAT="wxcb5e68f7c7a3f78a";
    public static final String AppSecret = "d4624c36b6795d1d99dcf0547af5443d";

    //新浪微博用
    public static final String SINA_APP_KEY      = "4158536883";		   // 应用的APP_KEY
    public static final String SINA_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";// 应用的回调页
    public static final String SINA_SCOPE = 							   // 应用申请的高级权限
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";


}
