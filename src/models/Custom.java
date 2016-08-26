package models;

import java.io.IOException;

import net.sf.json.JSONObject;


public class Custom {
	private String account;
	private String password;
	boolean online;
	public Custom(String account, String password) {
		super();
		this.account = account;
		this.password = password;
		this.online=true;
	}
	public Custom(String account, String password,boolean online) {
		super();
		this.account = account;
		this.password = password;
		this.online=online;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	@Override
	public String toString() {
		JSONObject obj=JSONObject.fromObject(this);
		return obj.toString();
	}
	public static void main(String[] Args) throws IOException{
		Custom cus=new Custom("18405813906", "1234");
		System.out.println(cus);
	}
	
}
