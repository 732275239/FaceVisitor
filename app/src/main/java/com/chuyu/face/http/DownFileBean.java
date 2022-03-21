package com.chuyu.face.http;

public class DownFileBean implements java.io.Serializable {

	private String fileName = "";
	/**
	 * 下载地址
	 */
	private String downUrl = "";
	/**
	 * 下载文件存储的绝对路径
	 */
	private String savePath = "";

	public DownFileBean(String fileName, String downUrl, String savePath) {
		super();
		this.fileName = fileName;
		this.downUrl = downUrl;
		this.savePath = savePath;
	}

	public String getDownUrl() {
		return downUrl;
	}

	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
