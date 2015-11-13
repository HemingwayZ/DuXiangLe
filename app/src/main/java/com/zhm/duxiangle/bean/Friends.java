package com.zhm.duxiangle.bean;

import java.io.Serializable;

public class Friends implements Serializable {

	private int id;
	private int userid;
	private int friendid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getFriendid() {
		return friendid;
	}

	public void setFriendid(int friendid) {
		this.friendid = friendid;
	}

}
