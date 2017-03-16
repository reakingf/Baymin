package com.qa.fgj.baymin.model.entity;

/**
 * 用户个人信息listView item实体
 */

public class UserInfoItem {

	private String tip;
	private String content;

	public UserInfoItem(String tip, String content) {
		this.tip = tip;
		this.content=content;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
