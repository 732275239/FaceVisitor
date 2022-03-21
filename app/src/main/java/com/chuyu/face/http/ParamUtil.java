package com.chuyu.face.http;

import com.chuyu.face.base.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成请求网络的参数（包括URL）
 *
 * @author Administrator
 *
 */
public class ParamUtil {

	/**
	 * 键 String 值 任何数据类型（String、int、File...）
	 *
	 * @return
	 */
	public static Map<String, Object> init() {
		Map<String, Object> paramMaps = new HashMap<String, Object>();
		return paramMaps;
	}

	/**
	 * 创建任意参数 (不带用户参数)
	 *
	 * @param act
	 * @param params
	 * @return
	 */
	public static ParamBean create(String act, Map<String, Object> params) {
		ParamBean paramBean = new ParamBean();
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		paramBean.setParams(params);
		paramBean.setAct(act);
		return paramBean;
	}

	/**
	 * 创建任意参数 (带用户参数)
	 *
	 * @param act
	 * @param params
	 * @return
	 */
	public static ParamBean createMy(String act, Map<String, Object> params) {
		ParamBean paramBean = new ParamBean();
		Map<String, Object> requestMap = createTokenMap();
		if (params != null && params.size() > 0) {
			requestMap.putAll(params);
		}
		paramBean.setAct(act);
		paramBean.setParams(requestMap);
		return paramBean;
	}

	/**
	 * 创建带分页的列表参数 (不带用户参数)
	 *
	 * @param act
	 * @param currPageNo
	 * @return
	 */
	public static ParamBean createPageInfo(String act, int currPageNo) {
		ParamBean paramBean = new ParamBean();
		Map<String, Object> pageInfoMap = new HashMap<String, Object>();
		pageInfoMap.put("page", currPageNo);
		pageInfoMap.put("size", HttpConstants.DEFAULT_PAGE_SIZE);
		paramBean.setAct(act);
		paramBean.setParams(pageInfoMap);
		return paramBean;
	}

	/**
	 * 创建带分页带用户的列表参数 (带用户参数)
	 *
	 * @param act
	 * @param currPageNo
	 * @return
	 */
	public static ParamBean createMyPageInfo(String act, int currPageNo) {
		ParamBean paramBean = new ParamBean();
		Map<String, Object> requestMap = createTokenMap();
		requestMap.put("page", currPageNo);
		requestMap.put("size", HttpConstants.DEFAULT_PAGE_SIZE);
		paramBean.setAct(act);
		paramBean.setParams(requestMap);
		return paramBean;
	}

	/**
	 * 创建带分页的其它参数 (不带用户参数)
	 *
	 * @param act
	 * @param currPageNo
	 * @param params
	 * @return
	 */
	public static ParamBean createPageOtherInfo(String act, int currPageNo,
												Map<String, Object> params) {
		ParamBean paramBean = new ParamBean();
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		params.put("page", currPageNo);
		params.put("size", HttpConstants.DEFAULT_PAGE_SIZE);
		paramBean.setAct(act);
		paramBean.setParams(params);
		return paramBean;
	}

	/**
	 * 创建带分页带用户的其它参数 (带用户参数)
	 *
	 * @param act
	 * @param currPageNo
	 * @param params
	 * @return
	 */
	public static ParamBean createMyPageOtherInfo(String act, int currPageNo,
												  Map<String, Object> params) {
		ParamBean paramBean = new ParamBean();
		Map<String, Object> requestMap = createTokenMap();
		params.put("page", currPageNo);
		params.put("size", HttpConstants.DEFAULT_PAGE_SIZE);
		if (params != null && params.size() > 0) {
			requestMap.putAll(params);
		}
		paramBean.setAct(act);
		paramBean.setParams(requestMap);
		return paramBean;
	}

	private static Map<String, Object> createTokenMap() {
		Map<String, Object> authMap = new HashMap<String, Object>();
		authMap.put("token",ApplicationContext.getInstance().getSpTools().getLogin().getToken());
		return authMap;
	}


}
