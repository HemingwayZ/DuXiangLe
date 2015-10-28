package com.zhm.duxiangle.api;

/**
 * Created by Administrator on 2015/10/27.
 */
public class DXLApi {
    //login:http://localhost:8080/DuXiangLeServer/UserServlet?username=zhuanghm&password=zhuanghm
    //http://192.168.253.1:8080/DuXiangLeServer/UserServlet?username=zhuanghm&password=zhuanghm
    //192.168.253.1
    public static String HOST = "192.168.173.1";
    public static final String PORT = ":8080";

    /**
     * 获取登录的url
     *
     * @return
     */
    public static String getLoginApi() {

        return "http://"+HOST + PORT + "/DuXiangLeServer/UserServlet";
    }

}
