package com.chuyu.face.tools;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 * @author Crazy24k@gmail.com
 * 
 */
public class StringUtil {
	/**
	 * 是否不为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNotEmpty(String s) {
		return s != null && !"".equals(s.trim());
	}

	/**
	 * 是否为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || "".equals(s.trim())||s.equals("null");
	}

	/**
	 * 通过{n},格式化.
	 * 
	 * @param src
	 * @param objects
	 * @return
	 */
	public static String format(String src, Object... objects) {
		int k = 0;
		for (Object obj : objects) {
			src = src.replace("{" + k + "}", obj.toString());
			k++;
		}
		return src;
	}

	/**
	 * 加密手机号
	 * 
	 * @return
	 */
	public static String encryptPhoen(String phone) {
		if (StringUtil.isEmpty(phone)) {
			return "";
		}
		if (phone.length() >= 11) {
			return phone.substring(0, 3) + "****" + phone.substring(7, 11);
		} else {
			return "";
		}

	}

	/**
	 * 格式化银行卡，身份证
	 * 
	 * @param card
	 * @return
	 */
	public static String encryptCard(String card) {
		if (StringUtil.isEmpty(card)) {
			return "";
		}
		if (card.length() <= 5) {
			return "";
		}
		String str = "";
		for(int i =0 ;i <(card.length()-4);i++){
			str = str+"*";
		}
		return card.substring(0, 2) + str
				+ card.substring(card.length() - 2, card.length());
	}
	/**
	 * 得到json文件中的内容
	 *
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String getJson(Context context, String fileName) {
		StringBuilder stringBuilder = new StringBuilder();
		//获得assets资源管理器
		AssetManager assetManager = context.getAssets();
		//使用IO流读取json文件内容
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
					assetManager.open(fileName), "utf-8"));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}
}
