package com.zhm.duxiangle.api;

/**
 * Created by Administrator on 2015/10/27.
 */
public class DXLApi {
    //login:http://localhost:8080/DuXiangLeServer/UserServlet?action=login
    //192.168.253.1
//    public static String HOST = "192.168.23.1:8080";
    public static String HOST = "120.25.201.60";
    public static String PORT = "";

    public static String BASE_URL = "http://" + HOST + PORT;

    /**
     * 获取登录的url
     *
     * @return
     */
    public static String getUserApi() {

        return "http://" + HOST + PORT + "/DuXiangLeServer/UserServlet";
    }

    //获取用户列表
    //http://localhost:8080/DuXiangLeServer/UserInfoServlet?action=userinfopage
    public static String getUserListByPage() {
        return "http://" + HOST + PORT + "/DuXiangLeServer/UserInfoServlet";
    }


    //http://localhost:8080/DuXiangLeServer/FileUploadServlet
    public static String getFileUpLoad() {
        return "http://" + HOST + PORT + "/DuXiangLeServer/FileUploadServlet";
    }

    public static String bookApi() {
        return "http://" + HOST + PORT + "/DuXiangLeServer/BookServlet";
    }

    public static String getIoRongTokenApi() {
        return "http://" + HOST + PORT + "/DuXiangLeServer/IoRongServlet";
    }

    public static String getUpdateUserInfoApi() {

        return "http://" + HOST + PORT + "/DuXiangLeServer/UpdateUserInfoServlet";
    }

    public static String getUpdatePicWallApi() {

        return "http://" + HOST + PORT + "/DuXiangLeServer/UpdatePicWallServlet";
    }
}
