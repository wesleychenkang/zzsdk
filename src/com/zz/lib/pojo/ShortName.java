package com.zz.lib.pojo;

/**
 * 请求标识和对象标识
 * 
 * @author RSun
 * @Date 2013-6-15下午12:15:06
 */
class ShortName {

	/** 登录 **/
	public final static String login = "a";
	/** 注册 **/
	public final static String register = "b";
	/** 修改资料 **/
	public final static String updateData = "c";
	/** 修改密码 **/
	public final static String updatePwd = "d";
	/** 绑定/解绑手机 **/
	public final static String bindMobile = "e";
	/** 绑定/解绑邮箱 **/
	public final static String bindEmail = "f";
	/** 校验手机、邮箱是否已绑定 **/
	public final static String isBind = "g";
	/** 获取用户信息 **/
	public final static String getUserInfo = "h";
	/** 返回数据 **/
	public final static String userInfo = "i";
	/** 返回imsi注册数 **/
	public final static String imsiRegCount = "j";

	/** 请求必带参数 **/
	public final static String baseData = "z";

	/** 请求结果描述 **/
	public final static String result = "r";

	private ShortName() {
	}

}
