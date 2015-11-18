package com.zhm.duxiangle.bean;

/**
 * Created by zhuanghm on 2015/11/18.
 */
public class QQUserInfo {
    private int userid;
    private String city;
    private String figureurl_qq_2;
    private String figureurl_qq_1;
    private String gender;
    private String nickname;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFigureurl_qq_2() {
        return figureurl_qq_2;
    }

    public void setFigureurl_qq_2(String figureurl_qq_2) {
        this.figureurl_qq_2 = figureurl_qq_2;
    }

    public String getFigureurl_qq_1() {
        return figureurl_qq_1;
    }

    public void setFigureurl_qq_1(String figureurl_qq_1) {
        this.figureurl_qq_1 = figureurl_qq_1;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "QQUserInfo{" +
                "userid=" + userid +
                ", city='" + city + '\'' +
                ", figureurl_qq_2='" + figureurl_qq_2 + '\'' +
                ", figureurl_qq_1='" + figureurl_qq_1 + '\'' +
                ", gender='" + gender + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
//    {
//        "city": "",
//            "figureurl": "http://qzapp.qlogo.cn/qzapp/1104835827/723C6CAE02AC239CF51B2D179FA1E530/30",
//            "figureurl_1": "http://qzapp.qlogo.cn/qzapp/1104835827/723C6CAE02AC239CF51B2D179FA1E530/50",
//            "figureurl_2": "http://qzapp.qlogo.cn/qzapp/1104835827/723C6CAE02AC239CF51B2D179FA1E530/100",
//            "figureurl_qq_1": "http://q.qlogo.cn/qqapp/1104835827/723C6CAE02AC239CF51B2D179FA1E530/40",
//            "figureurl_qq_2": "http://q.qlogo.cn/qqapp/1104835827/723C6CAE02AC239CF51B2D179FA1E530/100",
//            "gender": "女",
//            "is_lost": 0,
//            "is_yellow_vip": "0",
//            "is_yellow_year_vip": "0",
//            "level": "0",
//            "msg": "",
//            "nickname": "梦",
//            "province": "",
//            "ret": 0,
//            "vip": "0",
//            "yellow_vip_level": "0"
//    }