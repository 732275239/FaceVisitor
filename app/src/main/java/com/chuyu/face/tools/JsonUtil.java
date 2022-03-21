package com.chuyu.face.tools;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *名称：Gson工具类
 *创建人：xw
 *创建时间：2017/3/29 下午 2:41
 *详细说明：
 */
public class JsonUtil {
	
	
	/**
	 * Object 转 Json 串
	 * @param obj
	 * @return
	 */
	public static String objectToJson(Object obj){
		try{
			if(obj==null){
				return "";
			}
			return new Gson().toJson(obj);
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
		
	}
	
	
	/**
	 * JSON字符串转换成对应的对象
	 * @param jsons
	 * @param clazz
	 * @return T
	 */
	public static <T> T jsonToObject(String jsons,Class<T> clazz){
		try{
			if(TextUtils.isEmpty(jsons)){
				return null;
			}
			return new Gson().fromJson(jsons,clazz);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * JSON字符串转换成对应的List泛型
	 * @param jsons
	 * @return clazzType
	 * @throws Exception
	 */
	public static <T> List<T>  jsonToList(String jsons,TypeToken clazzType){
		try{
			if(TextUtils.isEmpty(jsons)){
				return null;
			}
	        return new Gson().fromJson(jsons, clazzType.getType());
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}

	}
	

	
	/**
	 * 将Map转化为Json
	 * 
	 * @param map
	 * @return String
	 */
	public static String mapToJson(Map map) {
		String jsonStr="{}";
		try{
			if(map!=null){
				jsonStr = new Gson().toJson(map);
			}
		}catch(Exception e){
			Log.i("JSON", "异常"+e.getMessage());
			e.printStackTrace();
		}
		return jsonStr;
	}

	public static <T> Map<String,T> jsonToMap(String jsons){
		try{
			if(TextUtils.isEmpty(jsons)){
				return null;
			}

			return new Gson().fromJson(jsons,new TypeToken<HashMap<String,T>>(){}.getType());
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**===================================================================**/
	
	public static <T> T jsonToResult(String jsons,TypeToken clazzType){
		try{
			if(TextUtils.isEmpty(jsons)){
				return null;
			}
	        return new Gson().fromJson(jsons, clazzType.getType());
		}catch(Exception e){
			Log.i("JSON", "异常"+e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * JSON字符串转换成对应的List泛型
	 * @param jsons
	 * @param clazz
	 * @return List
	 * @throws Exception
	 */
//	public static <T> PageResults<T>  jsonToPageList(String jsons,TypeToken clazzType){
//		try{
//			if(TextUtils.isEmpty(jsons)){
//				return null;
//			}
//	        return new Gson().fromJson(jsons, clazzType.getType());
//		}catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}
//
//	}
	
}
