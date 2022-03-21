package com.chuyu.face.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chuyu.face.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 *名称：Toast工具
 *创建人：xw
 *创建时间：2017/3/29 下午 4:12
 *详细说明：
 */
public class ToastUtil {

	private static Toast toast;

	public static void toast(Context context, String str) {
		createTips(context,str,0,0);
	}

	public static void toast(Context context, int strid) {
		createTips(context,"",strid,0);
	}


	
	
	/**
	 * 根据字符串显示短时间消息Tip
	 * @param mContext
	 * @param msg
	 */
	public static void showTips(Context mContext,String msg){
		createTips(mContext,msg,0,0);
	}
	
	/**
	 * 根据资源ID示短时间消息Tip
	 * @param mContext
	 * @param msgId
	 */
	public static void showTips(Context mContext,int msgId){
		createTips(mContext,"",msgId,0);
	}

	/**
	 * 根据字符串显示长时间消息Tip
	 * @param mContext
	 * @param msg
	 */
	public static void showLongTips(Context mContext,String msg){
		createTips(mContext,msg,0,1);
	}
	/**
	 * 根据资源ID示长时间消息Tip
	 * @param mContext
	 * @param msgId
	 */
	public static void showLongTips(Context mContext,int msgId){
		createTips(mContext,"",msgId,1);
	}
	
	
	/**
	 * 创建消息体
	 * @param mContext
	 * @param msg
	 * @param msgId
	 * @param type
	 */
	private static void createTips(Context mContext,String msg,int msgId,int type){
		if(mContext==null){
			return;
		}
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		View commonTipsView = mInflater.inflate(R.layout.common_tips, null);
		if(toast == null) {
			toast = new Toast(mContext);
			if(msgId>0){
				((TextView)commonTipsView.findViewById(R.id.common_tips_txt)).setText(msgId);
			}else{
				if(msg==null){
					msg="";
				}
				((TextView)commonTipsView.findViewById(R.id.common_tips_txt)).setText(msg);
			}
		}else {
			if(msgId>0){
				((TextView)commonTipsView.findViewById(R.id.common_tips_txt)).setText(msgId);
			}else{
				if(msg==null){
					msg="";
				}
				((TextView)commonTipsView.findViewById(R.id.common_tips_txt)).setText(msg);
			}
			if(type==0){
				toast.setDuration(Toast.LENGTH_SHORT);
			}else{
				toast.setDuration(Toast.LENGTH_LONG);
			}
		}

		toast.setView(commonTipsView);
		toast.show();
	}
//	private static Toast mToast;
//	/**
//	 * 显示Toast
//	 */
//	public static void showToast(Context context, CharSequence text) {
//		if(mToast == null) {
//			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
//		} else {
//			mToast.setText(text);
//			mToast.setDuration(Toast.LENGTH_SHORT);
//		}
//		mToast.show();
//	}
	public static void showMyToast(Context mContext,final String msg){
		if(mContext==null){
			return;
		}
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		final View commonTipsView = mInflater.inflate(R.layout.my_tips, null);

		final TextView tv=((TextView)commonTipsView.findViewById(R.id.common_tips_txt));

		tv.setText(msg);
		final Toast toast = new Toast(mContext);
		toast.setView(commonTipsView);

		final Timer timer =new Timer();
		final int m=30;
		final int tt=m*1000;
		if(toast==null){
			return;
		}

		timer.schedule(new TimerTask() {
			int mct=m;
			@Override
			public void run() {
				mct=mct-1;
				toast.show();
			}
		},0,1000);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				toast.cancel();
				timer.cancel();
			}
		}, tt);
	}

	
}