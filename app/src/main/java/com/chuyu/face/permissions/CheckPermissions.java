package com.chuyu.face.permissions;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.chuyu.face.base.BaseActivity;
import com.chuyu.face.constant.Constants;
import com.chuyu.face.permissions.easy.EasyPermissions;
import com.chuyu.face.tools.LogUtil;
import com.chuyu.face.tools.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *名称：检查手机权限
 *创建人：xw
 *创建时间：2017/3/29 下午 4:08
 *详细说明：
 */
public class CheckPermissions implements EasyPermissions.PermissionCallbacks {
	private static final int RC_ALL = 000;
	private static final int RC_CALENDAR = 111;
	private static final int RC_CAMERA = 222;
	private static final int RC_CONTACTS = 333;
	private static final int RC_LOCATION = 444;
	private static final int RC_MICROPHONE = 555;
	private static final int RC_PHONE = 666;
	private static final int RC_SENSORS = 777;
	private static final int RC_SMS = 888;
	private static final int RC_STORAGE = 999;

	private static CheckPermissions instance;

	private BaseActivity activity;

	// public CheckPermissions(Activity activity) {
	// this.activity = (BaseActivity) activity;
	// }

	public static CheckPermissions getInstance(Activity activity) {
		if (instance == null) {
			instance = new CheckPermissions();
			instance.activity = (BaseActivity) activity;
		}
		return instance;
	}

	private void requestPermissions(String success, String whyRequest,
			int code, String... permissions) {
		if (EasyPermissions.hasPermissions(activity, permissions)) {
			ToastUtil.showTips(activity, success);
		} else {
			EasyPermissions.requestPermissions(activity, whyRequest, code,
					permissions);
		}
	}

	/**
	 * 获取所有权限
	 */
	public void getAllPermissions() {

		String[] perms = { Manifest.permission.READ_PHONE_STATE,
				Manifest.permission.READ_CALENDAR, Manifest.permission.CAMERA,
				Manifest.permission.READ_CONTACTS,
				Manifest.permission.RECORD_AUDIO,
				Manifest.permission.CALL_PHONE,
				Manifest.permission.BODY_SENSORS, Manifest.permission.SEND_SMS,
				Manifest.permission.READ_EXTERNAL_STORAGE };

		// 剔除已有权限
		for (int i = 0; i < perms.length; i++) {
			if (EasyPermissions.hasPermissions(activity, perms[i])) {
				perms[i] = "";
			}
		}
		// 判断权限是否被拒绝开启
		for (int i = 0; i < perms.length; i++) {
			if (!perms[i].equals("")) {// 判空
				// 如果被禁止
				if (ActivityCompat.shouldShowRequestPermissionRationale(
						activity, perms[i])) {
					// 已经禁止，提示
					
					String settingMethod="请在手机系统中应用权限设置中手动开启！";
					switch (i) {
					case 0:
						ToastUtil.showTips(activity, Constants.APPNAME+Constants.APPNAME+"需要您授权开启读取手机状态权限！");
						ToastUtil.showTips(activity, settingMethod);
						break;
					case 1:
						ToastUtil.showTips(activity, Constants.APPNAME+"需要您授权开启读取日历权限！");
						ToastUtil.showTips(activity, settingMethod);
						break;
					case 2:
						ToastUtil.showTips(activity, Constants.APPNAME+"需要您授权开启相机拍照权限！");
						ToastUtil.showTips(activity, settingMethod);
						break;
					case 3:
						ToastUtil.showTips(activity, Constants.APPNAME+"需要您授权开启读取通讯录权限！");
						ToastUtil.showTips(activity, settingMethod);
						break;
					case 4:
						ToastUtil.showTips(activity, Constants.APPNAME+"需要您授权开启GPS定位权限！");
						ToastUtil.showTips(activity, settingMethod);
						break;
					case 5:
						ToastUtil.showTips(activity, Constants.APPNAME+"需要您授权开启麦克风权限！");
						ToastUtil.showTips(activity, settingMethod);
						break;
					case 6:
						ToastUtil.showTips(activity, Constants.APPNAME+"需要您授权开启拨打电话权限！");
						ToastUtil.showTips(activity, settingMethod);
						break;
					case 7:
						ToastUtil.showTips(activity, Constants.APPNAME+"需要您授权开启传感器权限！");
						ToastUtil.showTips(activity, settingMethod);
						break;
					case 8:
						ToastUtil.showTips(activity, Constants.APPNAME+"需要您授权开启读取短信权限！");
						ToastUtil.showTips(activity, settingMethod);
						break;
					case 9:
						ToastUtil.showTips(activity, Constants.APPNAME+"需要您授权开启存读写储卡权限！");
						ToastUtil.showTips(activity, settingMethod);
						break;
					}

					// 并移除
					perms[i] = "";
				}

			}

		}

		ArrayList<String> al = new ArrayList<String>();
		for (int i = 0; i < perms.length; i++) {
			if (!perms[i].equals("")) {
				al.add(perms[i]);
			}
		}
		String[] cusPermission = new String[al.size()];
		for (int i = 0; i < al.size(); i++) {
			cusPermission[i] = al.get(i);
		}
		if (cusPermission.length != 0) {
			try {
				// Ask for both permissions
				EasyPermissions.requestPermissions(activity, Constants.APPNAME+"需要获取权限",
						RC_ALL, cusPermission);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	public boolean checkSDCardPermission(){
		try{
			String[] perms = {
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE};
			if (EasyPermissions.hasPermissions(activity, Manifest.permission.READ_EXTERNAL_STORAGE) && EasyPermissions.hasPermissions(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
				return true;
			}else{
				Toast.makeText(activity, "需要您开启读写存储卡权限！", Toast.LENGTH_SHORT).show();
				return false;
			}
		}catch (Exception e){
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * 获取手机状态
	 */
	public void status() {
		// 判断有无权限
		if (EasyPermissions.hasPermissions(activity,
				Manifest.permission.READ_PHONE_STATE)) {
			LogUtil.e("volley", "手机状态权限已打开！");
		} else {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					Manifest.permission.READ_PHONE_STATE)) {
				// 已经禁止，提示
				ToastUtil.showTips(activity, "您已禁止手机状态权限，需要重新开启。");
			} else {
				// 没禁止，打开权限
				requestPermissions("手机状态权限请求成功", Constants.APPNAME+"需要获得手机状态权限", RC_CALENDAR,
						Manifest.permission.READ_PHONE_STATE);

			}

		}

	}

	/**
	 * 获取日历权限
	 */
	public void calendar() {
		// 判断有无权限
		if (EasyPermissions.hasPermissions(activity,
				Manifest.permission.READ_CALENDAR)) {
			LogUtil.e("volley", "日历权限已打开！");
		} else {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					Manifest.permission.READ_CALENDAR)) {
				// 已经禁止，提示
				ToastUtil.showTips(activity, "您已禁止日历权限，需要重新开启。");
			} else {
				// 没禁止，打开权限
				requestPermissions("日历权限请求成功", Constants.APPNAME+"需要获得日历权限", RC_CALENDAR,
						Manifest.permission.READ_CALENDAR);
			}

		}

	}

	/**
	 * 获取相机权限
	 */
	public void camera() {
		try {
			// 判断有无权限
			if (EasyPermissions.hasPermissions(activity,
					Manifest.permission.CAMERA)) {
				LogUtil.e("volley", "相机权限已打开！");
			} else {
				ToastUtil.showTips(activity, "您已禁止相机权限，需要手动开启。");
				// if
				// (ActivityCompat.shouldShowRequestPermissionRationale(activity,
				// Manifest.permission.CAMERA)) {
				// // 已经禁止，提示
				// ToastUtil.showTips(activity, "您已禁止相机权限，需要重新开启。");
				// } else {
				// 没禁止，打开权限
				requestPermissions("相机权限请求成功", Constants.APPNAME+"需要获得相机权限", RC_CAMERA,
						Manifest.permission.CAMERA);
				// }

			}
		} catch (Exception e) {
			ToastUtil.showTips(activity, "您已禁止相机权限，需要重新开启。");
		}

	}

	/**
	 * 获取电话权限
	 */
	public void contacts() {
		// 判断有无权限
		if (EasyPermissions.hasPermissions(activity,
				Manifest.permission.READ_CONTACTS)) {
			LogUtil.e("volley", "电话权限已打开！");
		} else {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					Manifest.permission.READ_CONTACTS)) {
				// 已经禁止，提示
				ToastUtil.showTips(activity, "您已禁止电话权限，需要重新开启。");
			} else {
				// 没禁止，打开权限
				requestPermissions("电话权限请求成功", Constants.APPNAME+"需要获得电话权限", RC_CONTACTS,
						Manifest.permission.READ_CONTACTS);
			}

		}

	}

	/**
	 * 获取位置权限
	 */
	public void location() {
		// 判断有无权限
		if (EasyPermissions.hasPermissions(activity,
				Manifest.permission.ACCESS_FINE_LOCATION)) {
			LogUtil.e("volley", "位置权限已打开！");
		} else {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					Manifest.permission.ACCESS_FINE_LOCATION)) {
				// 已经禁止，提示
				ToastUtil.showTips(activity, "您已禁止位置权限，需要重新开启。");
			} else {
				// 没禁止，打开权限
				requestPermissions("位置权限请求成功", Constants.APPNAME+"需要获得位置权限", RC_LOCATION,
						Manifest.permission.ACCESS_FINE_LOCATION);
			}

		}

	}

	/**
	 * 获取麦克风权限
	 */
	public void microphone() {
		// 判断有无权限
		if (EasyPermissions.hasPermissions(activity,
				Manifest.permission.RECORD_AUDIO)) {
			LogUtil.e("volley", "麦克风权限已打开！");
		} else {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					Manifest.permission.RECORD_AUDIO)) {
				// 已经禁止，提示
				ToastUtil.showTips(activity, "您已禁止麦克风权限，需要重新开启。");
			} else {
				// 没禁止，打开权限
				requestPermissions("麦克风权限请求成功", Constants.APPNAME+"需要获得麦克风权限", RC_MICROPHONE,
						Manifest.permission.RECORD_AUDIO);
			}

		}

	}

	/**
	 * 获取拨打电话权限
	 */
	public void phone() {
		// 判断有无权限
		if (EasyPermissions.hasPermissions(activity,
				Manifest.permission.CALL_PHONE)) {
			LogUtil.e("volley", "拨打电话权限已打开！");
		} else {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					Manifest.permission.CALL_PHONE)) {
				// 已经禁止，提示
				ToastUtil.showTips(activity, "您已禁止拨打电话权限，需要重新开启。");
			} else {
				// 没禁止，打开权限
				requestPermissions("拨打电话权限请求成功", Constants.APPNAME+"需要获得拨打电话权限", RC_PHONE,
						Manifest.permission.CALL_PHONE);
			}

		}

	}

	/**
	 * 获取传感器权限
	 */
	public void sensors() {
		// 判断有无权限
		if (EasyPermissions.hasPermissions(activity,
				Manifest.permission.BODY_SENSORS)) {
			LogUtil.e("volley", "传感器权限已打开！");
		} else {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					Manifest.permission.BODY_SENSORS)) {
				// 已经禁止，提示
				ToastUtil.showTips(activity, "您已禁止传感器权限，需要重新开启。");
			} else {
				// 没禁止，打开权限
				requestPermissions("传感器权限请求成功", Constants.APPNAME+"需要获得传感器权限", RC_SENSORS,
						Manifest.permission.BODY_SENSORS);
			}

		}

	}

	/**
	 * 获取短信权限
	 */
	public void sms() {
		// 判断有无权限
		if (EasyPermissions.hasPermissions(activity,
				Manifest.permission.SEND_SMS)) {
			LogUtil.e("volley", "短信权限已打开！");
		} else {
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
					Manifest.permission.SEND_SMS)) {
				// 已经禁止，提示
				ToastUtil.showTips(activity, "您已禁止短信权限，需要重新开启。");
			} else {
				// 没禁止，打开权限
				requestPermissions("短信权限请求成功", Constants.APPNAME+"需要获得短信权限", RC_SMS,
						Manifest.permission.SEND_SMS);
			}

		}

	}

	/**
	 * 获取存储空间权限
	 */
	public boolean storage() {
		try {
			// 判断有无权限
			if (EasyPermissions.hasPermissions(activity,
					Manifest.permission.READ_EXTERNAL_STORAGE)) {
				LogUtil.e("volley", "存储空间权限已打开！");
			} else {
				// if
				// (ActivityCompat.shouldShowRequestPermissionRationale(activity,
				// Manifest.permission.READ_EXTERNAL_STORAGE)) {
				// // 已经禁止，提示
				ToastUtil.showTips(activity, "您已禁止存储空间权限，需要手动开启。");
				// } else {
				// 没禁止，打开权限
				requestPermissions("存储空间权限请求成功", Constants.APPNAME+"需要获得存储空间权限", RC_STORAGE,
						Manifest.permission.READ_EXTERNAL_STORAGE);
				// }

				return false;
			}

		} catch (Exception e) {
//			ToastUtil.showTips(activity, "您已禁止存储空间权限，需要手动开启。");
			return false;
		}

		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
		// TODO Auto-generated method stub
		// EasyPermissions handles the request result.
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions,
				grantResults, this);
	}

	/**
	 * 此处表示权限申请已经成功，可以使用该权限完成app的相应的操作了
	 */
	@Override
	public void onPermissionsGranted(int requestCode, List<String> perms) {
	}

	/**
	 * 此处表示权限申请被用户拒绝了，此处可以通过弹框等方式展示申请该权限的原因，以使用户允许使用该权限
	 */
	@Override
	public void onPermissionsDenied(int requestCode, List<String> perms) {

		switch (requestCode) {
		case RC_ALL:
			// ToastUtil.showTips(activity, perms.toString());
			break;
		case RC_CALENDAR:
			// ToastUtil.showTips(activity, "日历权限已失败...！");
			break;
		case RC_CAMERA:
			// ToastUtil.showTips(activity, "相机权限已失败！");
			break;
		case RC_CONTACTS:
			// ToastUtil.showTips(activity, "电话权限已失败...！");
			break;
		case RC_LOCATION:
			// ToastUtil.showTips(activity, "位置权限已失败...！");
			break;
		case RC_MICROPHONE:

			// ToastUtil.showTips(activity, "麦克风权限已失败...！");
			break;
		case RC_PHONE:
			// ToastUtil.showTips(activity, "拨打电话权限已失败...！");

			break;
		case RC_SENSORS:
			// ToastUtil.showTips(activity, "传感器权限已失败...！");

			break;
		case RC_SMS:
			// ToastUtil.showTips(activity, "短信权限已失败...！");

			break;
		case RC_STORAGE:
			// ToastUtil.showTips(activity, "存储空间权限已失败...！");

			break;

		default:
			break;
		}
	}

}
