package com.fgroupindonesia.beans;

public class StatusVoucher {

	private int id, remainingMinutes, hours;
	private String seri, username, stat;
	
	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRemainingMinutes() {
		return remainingMinutes;
	}
	public void setRemainingMinutes(int remainingMinutes) {
		this.remainingMinutes = remainingMinutes;
	}
	public String getSeri() {
		return seri;
	}
	public void setSeri(String seri) {
		this.seri = seri;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	
	
}
