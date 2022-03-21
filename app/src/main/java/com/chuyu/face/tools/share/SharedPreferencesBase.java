package com.chuyu.face.tools.share;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesBase {
    private Context context;

    public SharedPreferencesBase(Context context) {
        this.context = context;
    }

    /**
     * 读取自定义的整型参数
     *
     * @param perferencesName 表名
     *                        <BR>
     *                        使用SharedPreferencesTools提供的内表面
     * @param key             键
     * @return 返回值不存在返回-1
     */
    public int readIntPreferences(String perferencesName, String key) {
        SharedPreferences preferences = context.getSharedPreferences(
                perferencesName, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    /**
     * 读取自定义的String参数
     *
     * @param perferencesName 表名
     *                        <BR>
     *                        使用SharedPreferencesTools提供的内表面
     * @param key             键
     * @return 返回值不存在返回""
     */
    public String readStringPreferences(String perferencesName, String key) {
        SharedPreferences preferences = context.getSharedPreferences(
                perferencesName, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    /**
     * 读取自定义的Longg参数
     *
     * @param perferencesName 表名
     *                        <BR>
     *                        使用SharedPreferencesTools提供的内表面
     * @param key             键
     * @return 返回值不存在返回 "-1"
     */
    public Long readLongPreferences(String perferencesName, String key) {
        SharedPreferences preferences = context.getSharedPreferences(
                perferencesName, Context.MODE_PRIVATE);
        return preferences.getLong(key, -1l);
    }

    /**
     * 读取自定义的boolean参数
     *
     * @param perferencesName 表名
     * @param key             键
     * @return 返回值不存在返回false
     */
    public boolean readBooleanPreferences(String perferencesName, String key) {
        SharedPreferences preferences = context.getSharedPreferences(
                perferencesName, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }


    /**
     * 保存整型参数
     *
     * @param perferencesName 表名
     *                        <BR>
     *                        使用SharedPreferencesTools提供的内表面
     * @param key             键
     * @param value           值
     */
    public void writeIntPreferences(String perferencesName, String key,
                                    int value) {
        SharedPreferences preferences = context.getSharedPreferences(
                perferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 保存String参数
     *
     * @param perferencesName 表名
     *                        <BR>
     *                        使用SharedPreferencesTools提供的内表面
     * @param key             键
     * @param value           值
     */
    public void writeStringPreferences(String perferencesName, String key,
                                       String value) {
        SharedPreferences preferences = context.getSharedPreferences(
                perferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 保存Long参数
     *
     * @param perferencesName 表名
     *                        <BR>
     *                        使用SharedPreferencesTools提供的内表面
     * @param key             键
     * @param value           值
     */
    public void writeLongPreferences(String perferencesName, String key,
                                     Long value) {
        SharedPreferences preferences = context.getSharedPreferences(
                perferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * 清除保存的 值
     *
     * @param perferencesName 表名
     *                        <BR>
     *                        使用SharedPreferencesTools提供的内表面
     * @param key             键
     */
    public void clearPreferences(String perferencesName, String key) {
        SharedPreferences preferences = context.getSharedPreferences(
                perferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 保存boolean参数
     *
     * @param perferencesName 表名
     * @param key             键
     * @param value           键
     */
    public void writeBooleanPreferences(String perferencesName, String key,
                                        boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(
                perferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
