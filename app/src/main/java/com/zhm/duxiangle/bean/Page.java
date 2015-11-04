package com.zhm.duxiangle.bean;

import java.util.List;
import java.util.Objects;

/**
 * 分页
 *
 * @author zhuanghm
 */
public class Page<T> {
    // 页面菜单属性
    private int thispage; // 当前页码
    private int rowperpage; // 每页记录数
    private int countrow; // 总记录数
    private int countpage; // 总页数
    private int firstpage; // 首页
    private int lastpage; // 尾页
    private int prepage; // 下一页
    private int nextpage; // 上一页
    private String sql;
    private List<T> list;// 页面内容属性

    public int getThispage() {
        return thispage;
    }

    public void setThispage(int thispage) {
        this.thispage = thispage;
    }

    public int getRowperpage() {
        return rowperpage;
    }

    public void setRowperpage(int rowperpage) {
        this.rowperpage = rowperpage;
    }

    public int getCountrow() {
        return countrow;
    }

    public void setCountrow(int countrow) {
        this.countrow = countrow;
    }

    public int getCountpage() {
        return countpage;
    }

    public void setCountpage(int countpage) {
        this.countpage = countpage;
    }

    public int getFirstpage() {
        return firstpage;
    }

    public void setFirstpage(int firstpage) {
        this.firstpage = firstpage;
    }

    public int getLastpage() {
        return lastpage;
    }

    public void setLastpage(int lastpage) {
        this.lastpage = lastpage;
    }

    public int getPrepage() {
        return prepage;
    }

    public void setPrepage(int prepage) {
        this.prepage = prepage;
    }

    public int getNextpage() {
        return nextpage;
    }

    public void setNextpage(int nextpage) {
        this.nextpage = nextpage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return "Page [thispage=" + thispage + ", rowperpage=" + rowperpage + ", countrow=" + countrow + ", countpage="
                + countpage + ", firstpage=" + firstpage + ", lastpage=" + lastpage + ", prepage=" + prepage
                + ", nextpage=" + nextpage + ", sql=" + sql + ", list=" + list + "]";
    }

}
