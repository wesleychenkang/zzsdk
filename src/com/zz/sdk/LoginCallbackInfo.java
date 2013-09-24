package com.zz.sdk;

/**
 * 登录回调信息，设置给Handler里Message的obj
 * 
 * @author roger
 */

public class LoginCallbackInfo {

	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_FAILURE = -1;
	public static final int STATUS_CLOSE_VIEW = -2;

	/** 状态值 */
	public int statusCode;
	/** 登录的用户名 */
	public String loginName;
    
    public String sdkuserid;
	@Override
	public String toString() {
		return "LoginCallbackInfo [statusCode=" + statusCode + ", loginName="
				+ loginName +", sdkuserid="+sdkuserid+ "]";
	}
}
