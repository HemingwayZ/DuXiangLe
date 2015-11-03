package com.zhm.duxiangle.bean;

import java.io.Serializable;

/**
 * @author zhuanghm
 */
public class UserInfo implements Serializable{
    private int userinfoId;
    private int userId;
    private String nickname;
    private String avatar;
    private String created;
    private String describ;

    public int getUserinfoId() {
        return userinfoId;
    }

    public void setUserinfoId(int userinfoId) {
        this.userinfoId = userinfoId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDescrib() {
        return describ;
    }

    public void setDescrib(String describ) {
        this.describ = describ;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userinfoId=" + userinfoId +
                ", userId=" + userId +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", created='" + created + '\'' +
                ", describ='" + describ + '\'' +
                '}';
    }
}
