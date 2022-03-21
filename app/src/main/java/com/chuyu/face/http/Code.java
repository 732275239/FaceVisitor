package com.chuyu.face.http;

public class Code {

	public static final int SUCCEED = 0;   //成功
	public static final int FAIL = 1;// 操作失败
	public static final int REDIRECT = 302;  //重定向
	public static final int PARAM_ERROR = 400;  //参数错误
	public static final int USER_NO_LOGIN = 402;  //无效的身份验证凭证
	public static final int NO_RESOURE = 404;  //没有相关资源
	public static final int EXCEPTION_ERROR = 500;// 系统错误

	public static final int NO_DATA_ERROR = 101;// 没有数据
	public static final int USER_EXIST = 104;// 用户已经存在
	public static final int NET_NOT_CONN = 404; // 网络连接异常
	public static final int NET_CONN_TIMEOUT = 408; // 网络连接超时
	public static final int URL_BROKEN = 406; // url错误
	public static final int JSON_ERROR = 5001;// JSON解析出错
	public static final int DATA_ERROR = 1111;// 数据异常

}
