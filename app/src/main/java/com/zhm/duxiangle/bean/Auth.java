package com.zhm.duxiangle.bean;

import java.io.Serializable;

/**
 * Auth sina qq
 * Created by Administrator on 2015/11/17.
 */
public class Auth implements Serializable {
    private int authid;
    private int userid;
    private String openid;
    private String pay_token;

    public int getAuthid() {
        return authid;
    }

    public void setAuthid(int authid) {
        this.authid = authid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPay_token() {
        return pay_token;
    }

    public void setPay_token(String pay_token) {
        this.pay_token = pay_token;
    }
}
