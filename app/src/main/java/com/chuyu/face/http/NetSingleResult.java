package com.chuyu.face.http;

import org.json.JSONObject;


public class NetSingleResult implements java.io.Serializable{
	
	private int err_code;
	private String err_msg;
	private JSONObject data_single;
	public int getErr_code() {
		return err_code;
	}
	public void setErr_code(int err_code) {
		this.err_code = err_code;
	}
	public String getErr_msg() {
		return err_msg;
	}
	public void setErr_msg(String err_msg) {
		this.err_msg = err_msg;
	}
	public JSONObject getData_single() {
		return data_single;
	}
	public void setData_single(JSONObject data_single) {
		this.data_single = data_single;
	}
	
	
	
	

}
