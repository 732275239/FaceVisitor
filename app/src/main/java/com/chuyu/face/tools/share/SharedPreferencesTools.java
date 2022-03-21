package com.chuyu.face.tools.share;


import android.content.Context;

import com.chuyu.face.bean.UserBean;
import com.chuyu.face.tools.JsonUtil;


public class SharedPreferencesTools {

    private static final String NAME_USER = "QRSOFT_USER";
    private static final String KEY_USER_LOGIN_DATA="KEY_USER_BEAN";

    private static SharedPreferencesBase spBase;

    public SharedPreferencesTools(Context context) {
        spBase = new SharedPreferencesBase(context);
    }

    public void saveLogin(UserBean userBean) {
        try{
            String userJson= JsonUtil.objectToJson(userBean);
            spBase.writeStringPreferences(NAME_USER, KEY_USER_LOGIN_DATA, userJson);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public UserBean getLogin(){
        UserBean userBean=null;
        try{
            String userJsons=spBase.readStringPreferences(NAME_USER,KEY_USER_LOGIN_DATA);
            userBean=JsonUtil.jsonToObject(userJsons,UserBean.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(userBean==null){
            userBean=new UserBean();
        }
        return userBean;

    }


    /**
     * 清除用户数据
     */
    public void clearUser() {
        try{
            spBase.clearPreferences(NAME_USER,KEY_USER_LOGIN_DATA);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public boolean ifLogin() {
        UserBean ubean=getLogin();
        if(ubean.getToken()==null){
            return false;
        }else{
            return true;
        }
    }



    public void saveParams(String key,String params){
        try{
            spBase.writeStringPreferences(NAME_USER, key, params);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getParams(String key){
        try{
            return spBase.readStringPreferences(NAME_USER,key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public void clearParams(String key){
        try{
            spBase.clearPreferences(NAME_USER,key);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
