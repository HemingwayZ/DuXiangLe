package com.zhm.duxiangle.bean;

import java.io.Serializable;

/**
 * Created by zhuanghm on 2015/10/27.
 */
public class User implements Serializable{
    private int userId;
    private String username;
    private String password;
    private String status;//记录登录状态-online：在线  offline：离线

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
