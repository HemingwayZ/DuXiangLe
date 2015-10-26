package com.zhm.duxiangle.bean;

/**
 * ���鱾������
 *
 * @author zhm(183340093@qq.com)
 *         <p/>
 *         2015��10��7��
 */
public class Rating {
    private String id;
    private int max;//
    private int numRaters;//������������
    private String average;//
    private int min;//�

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getNumRaters() {
        return numRaters;
    }

    public void setNumRaters(int numRaters) {
        this.numRaters = numRaters;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Override
    public String toString() {
        return "Rating [max=" + max + ", numRaters=" + numRaters + ", average=" + average + ", min=" + min + "]";
    }


}
