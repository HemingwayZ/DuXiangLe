package com.zhm.duxiangle.bean;

import java.io.Serializable;

/**
 * 系列
 *
 * @author zhm(183340093@qq.com)
 *         <p/>
 *         2015��10��7��
 */
public class Series implements Serializable {
    private String id;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Series [id=" + id + ", title=" + title + "]";
    }

}
