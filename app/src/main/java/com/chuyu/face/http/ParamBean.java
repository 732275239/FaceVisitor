package com.chuyu.face.http;

import java.util.Map;

public class ParamBean implements java.io.Serializable{
	public String act="";
	public Map<String,Object> params=null;
	
	public String getAct() {
		return act;
	}
	public void setAct(String act) {
		this.act = act;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	
	
}
