package com.zz.sdk.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.zz.sdk.util.HttpUtil;
import com.zz.sdk.util.LocationUtil;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.Utils;

/** 
 * @Description: 设备环境参数
 * @author roger
 */

public class DeviceProperties implements Serializable, JsonParseInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4L;
	
	/**
	 * 1.0.1
	 * 手机系统版本
	 */
	public String sdkVersion;
	
	
	/**	
	 * 1.0.1
	 * 手机型号
	 */
	public String type;
	
	/**
	 * 1.0.1
	 * Sim卡序列号
	 */
	public String imsi;
	
	/**
	 * 1.0.1
	 * 手机序列号
	 */
	public String imei;
	
	
	/**
	 * 1.0.1
	 * 应用版本号
	 */
	public int versionCode;
	
	/**
	 * 1.0.1
	 * 手机屏幕密码
	 */
	public int densityDpi;
	
	/**
	 * 	1.0.1
	 * 手机屏幕宽度
	 */
	public int displayScreenWidth;

	/**
	 * 1.0.1
	 * 手机屏幕高度
	 */
	public int displayScreenHeight;

	/**
	 * 1.0.1
	 * 经度
	 */
	public double latitude;

	/**
	 * 1.0.1
	 * 纬度
	 */
	public double longitude;

	/**
	 * 1.0.1
	 * 地域（省、市、县/区）
	 */
	public String area;


	/**
	 * 1.0.1
	 * 网络类型
	 */
	public String networkInfo;

	/**
	 * 项目ID
	 */
	public String projectId;
	
	/**
	 * 游戏包名
	 */
	public String packageName;
	/**
	 * sdk协议版本号
	 */
	public String version = "1.0.0";
	
	// 自定义手机唯一标识  v1.0.2
	public String deviceParams;
	
	public DeviceProperties(Context ctx) {
		
		type = android.os.Build.PRODUCT;
		sdkVersion = android.os.Build.VERSION.SDK;
		
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
//		imsi = tm.getSubscriberId();
		imei = tm.getDeviceId();
		imsi = Utils.getIMSI(ctx);
		Logger.d("imsi --> " + imsi);
		
		//--获取手机分辨率
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		densityDpi = metrics.densityDpi;
		displayScreenWidth = metrics.widthPixels;
		displayScreenHeight = metrics.heightPixels;
		
		PackageManager pm = ctx.getPackageManager();
		packageName = ctx.getPackageName();
		PackageInfo info;
		try {
			info = pm.getPackageInfo(ctx.getPackageName(), 0);
			versionCode = info.versionCode;
		} catch (NameNotFoundException e1) {
		}
		
		try {
			projectId = Utils.getProjectId(ctx);
			android.util.Log.d("zz_sdk", "project id -> " + projectId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		networkInfo = HttpUtil.getNetworkTypeName(ctx);
		if (networkInfo == null) {
			networkInfo = "unknown";
		}
		Location location = LocationUtil.getLocation(ctx);
		if (location != null) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			String tmp = LocationUtil.getAddress(longitude, latitude);
			if (tmp != null) {
				area = tmp;
			}
		}
		// 获取硬件参数
		deviceParams = getDeviceParams(ctx);
	}
	
	private String getDeviceParams(Context ctx) {
		StringBuilder deviceParams = new StringBuilder();
		deviceParams.append(imei);
    	//在wifi未开启状态下，仍然可以获取MAC地址，但是IP地址必须在已连接状态下否则为0
    	WifiManager wifiMgr = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
    	WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
    	if (null != info) {
    	    String macAddress = info.getMacAddress();
    	    if (macAddress != null) 
    	    	deviceParams.append(macAddress);
    	}
    	return Utils.md5Encode(deviceParams.toString());
    }

	@Override
	public String toString() {
		return "DeviceProperties [sdkVersion=" + sdkVersion + ", type="
				+ type + ", imsi=" + imsi + ", imei=" + imei
				+  ", versionCode="
				+ versionCode + ", densityDpi=" + densityDpi
				+ ", displayScreenWidth=" + displayScreenWidth
				+ ", displayScreenHeight=" + displayScreenHeight
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", area=" + area + ", networkInfo=" + networkInfo
				+ ", projectId=" + projectId 
				+ ", packageName=" + packageName + ", version=" + version
				+ ", deviceParams=" + deviceParams + "]";
	}

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			json.put("a", sdkVersion);
			json.put("b", type);
			json.put("c", imsi);
			json.put("d", imei);
			json.put("f", versionCode);
			json.put("g", densityDpi);
			json.put("h", displayScreenWidth);
			json.put("i", displayScreenHeight);
			json.put("j", latitude);
			json.put("k", longitude);
			json.put("l", area);
			json.put("m", networkInfo);
			json.put("n", projectId);
			json.put("p", packageName);
			json.put("q", version);
			json.put("r", deviceParams);
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void parseJson(JSONObject json) {
		if (json == null)
			return;
		try {
			sdkVersion = json.isNull("a") ? null : json.getString("a");
			type = json.isNull("b") ? null : json.getString("b");
			imsi = json.isNull("c") ? null : json.getString("c");
			imei = json.isNull("d") ? null : json.getString("d");
			versionCode = json.isNull("f") ? -1 : json.getInt("f");
			densityDpi = json.isNull("g") ? 240 : json.getInt("g");
			displayScreenWidth = json.isNull("h") ? 0 : json.getInt("h");
			displayScreenHeight = json.isNull("i") ? 0 : json.getInt("i");
			latitude = json.isNull("j") ? -500 : json.getDouble("j");
			longitude = json.isNull("k") ? -500 : json.getDouble("k");
			area = json.isNull("l") ? null : json.getString("l");
			networkInfo = json.isNull("m") ? null : json.getString("m");
			projectId = json.isNull("n") ? null : json.getString("n");
			packageName = json.isNull("p") ? null : json.getString("p");
			version = json.isNull("q") ? null : json.getString("q");
			deviceParams = json.isNull("r") ? null : json.getString("r");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getShortName() {
		return "a";
	}
}
