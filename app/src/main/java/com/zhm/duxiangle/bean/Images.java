package com.zhm.duxiangle.bean;

/**
 * �
 *
 * @author zhm(183340093@qq.com)
 *         <p/>
 *         2015��10��7��
 */
public class Images {
    private String id;
    private String small;//
    private String large;//��
    private String medium;//�

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    @Override
    public String toString() {
        return "Images [small=" + small + ", large=" + large + ", medium=" + medium + "]";
    }

}
