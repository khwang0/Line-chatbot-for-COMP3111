package com.example.bot.spring.webapplication.domain;

public class Activity {
	
	String bootableid;
	int quota;
	
	public void setBootableId(String bootableid) {
		this.bootableid = bootableid;
	}
	
	public String getBootableId() {
		return bootableid;
	}
	
	public void setQuota(int quota) {
		this.quota = quota;
	}
	
	public int getQuota() {
		return quota;
	}
}
