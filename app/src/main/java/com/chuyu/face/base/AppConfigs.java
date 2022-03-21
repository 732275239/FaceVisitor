package com.chuyu.face.base;


/**
 *名称：app配置
 *创建人：xxu
 *创建时间：2017/4/13 上午 9:56
 *详细说明：
 */
public class AppConfigs {
	/**
	 * 是否允许DEBUG模式
	 */
	public static final boolean CHECK_DEBUG_ENABLE = true;// false不允许DEBUG运行,以及不打印LOG
	/**
	 * 应用同意Log标示
	 */
	public static final String TAG = "qr_log";// Debug Log标识


	private static AppConfigs appConfig;

	public static AppConfigs getInstance() {
		if (appConfig == null) {
			appConfig = new AppConfigs();
		}
		return appConfig;
	}


}
