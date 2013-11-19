package com.zz.lib.pojo;

import java.io.Serializable;

import org.json.JSONObject;

import com.zz.lib.utils.Sign;

/**
 * 用户注册对象
 * 
 * @author RSun
 * @Date 2013-6-15下午12:16:17
 */
class Register extends JsonParseInterface implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2713534923923387744L;
	// 字段key
	private static final String u_account = "a";
	private static final String u_md5pwd = "b";
	private static final String u_bindMobile = "c";
	private static final String u_bindEmail = "d";
	private static final String u_nickName = "e";
	private static final String u_email = "f";
	private static final String u_sex = "g";
	private static final String u_imageUrl = "h";

	private static final String u_type = "i";
	private static final String u_deviceParams = "j";
	private static final String u_product = "k";
	private static final String u_networkInfo = "l";
	private static final String u_gatewaytype = "m";
	private static final String u_densityDpi = "n";
	private static final String u_displayScreenWidth = "o";
	private static final String u_displayScreenHeight = "p";

	private static final String u_regIP = "q";
	private static final String u_douId = "r";
	private static final String u_productId = "s";
	private static final String u_imsi = "t";
	private static final String u_channelId = "u";
	private static final String u_versionName = "v";
	private static final String u_mobile = "w";

	private static final String u_isFast = "x";
	private static final String u_app_secret = "y";
	private static final String u_sign = "z";

	public String addTime = "";
	public String lastTime = "";
	public String lastIp = "";

	/** 快速注册 **/
	public static final int reg_fast = 1;
	/** 正常注册 **/
	public static final int reg_non = 0;

	/** a 帐号，20、35位 **/
	public String account;
	/** b md5后的密码(cmge内部md5) **/
	public String md5pwd;
	/** c 绑定手机 **/
	public String bindMobile;
	/** d 绑定邮箱 **/
	public String bindEmail;
	/** e 昵称 **/
	public String nickName;
	/** f 邮箱 **/
	public String email;
	/** g 1为男，2为女，0为保密，默认0 **/
	public int sex;
	/** h 头像地址url **/
	public String imageUrl;

	/** i 用户类型，1为CMGE平台用户，2为SDK用户，3为web注册用户，4为wap注册用户 **/
	public int type;
	/** j 自定义手机唯一标识 **/
	public String deviceParams;
	/** k 手机型号 **/
	public String product;
	/** l 网络类型 **/
	public String networkInfo;
	/** m 运营商，1:移动，2:联通，4:电信 **/
	public int gatewaytype;
	/** n 手机屏幕密度 **/
	public int densityDpi;
	/** o 手机屏幕宽度 **/
	public int displayScreenWidth;
	/** p 手机屏幕高度 **/
	public int displayScreenHeight;
	/** q 注册IP **/
	public String regIP;

	/** r douId **/
	public String douId;
	/** s **/
	public int productId;
	/** t **/
	public String imsi;
	/** u **/
	public int channelId;
	/** v **/
	public String versionName;
	/** w **/
	public String mobile;

	/** x 是否快速注册，1-快速注册；0-正常注册 **/
	public int isFast;
	/** y 授权码 **/
	public String app_secret;
	/** z MD5签名 **/
	public String sign;

	public int age;
	public String birthday;
	public String address;
	public String occupational;
	public String hobby;

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			setString(json, u_account, account);
			setString(json, u_md5pwd, md5pwd);
			setString(json, u_bindMobile, bindMobile);
			setString(json, u_bindEmail, bindEmail);
			setString(json, u_nickName, nickName);
			setString(json, u_email, email);
			setInt(json, u_sex, sex);
			setString(json, u_imageUrl, imageUrl);

			setInt(json, u_type, type);
			setString(json, u_deviceParams, deviceParams);
			setString(json, u_product, product);
			setString(json, u_networkInfo, networkInfo);
			setInt(json, u_gatewaytype, gatewaytype);
			setInt(json, u_densityDpi, densityDpi);
			setInt(json, u_displayScreenWidth, displayScreenWidth);
			setInt(json, u_displayScreenHeight, displayScreenHeight);
			setString(json, u_regIP, regIP);

			setString(json, u_douId, douId);
			setInt(json, u_productId, productId);
			setString(json, u_imsi, imsi);
			setInt(json, u_channelId, channelId);
			setString(json, u_versionName, versionName);
			setString(json, u_mobile, mobile);

			setInt(json, u_isFast, isFast);
			setString(json, u_app_secret, app_secret);
			setString(json, u_sign, sign);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void parseJson(JSONObject json) {
		if (json == null)
			return;
		try {
			account = getString(json, u_account);
			md5pwd = getString(json, u_md5pwd);
			bindMobile = getString(json, u_bindMobile);
			bindEmail = getString(json, u_bindEmail);
			nickName = getString(json, u_nickName);
			email = getString(json, u_email);
			sex = getInt(json, u_sex);
			imageUrl = getString(json, u_imageUrl);

			type = getInt(json, u_type);
			deviceParams = getString(json, u_deviceParams);
			product = getString(json, u_product);
			networkInfo = getString(json, u_networkInfo);
			gatewaytype = getInt(json, u_gatewaytype);
			densityDpi = getInt(json, u_densityDpi);
			displayScreenWidth = getInt(json, u_displayScreenWidth);
			displayScreenHeight = getInt(json, u_displayScreenHeight);
			regIP = getString(json, u_regIP);

			douId = getString(json, u_douId);
			productId = getInt(json, u_productId);
			imsi = getString(json, u_imsi);
			channelId = getInt(json, u_channelId);
			versionName = getString(json, u_versionName);
			mobile = getString(json, u_mobile);

			isFast = getInt(json, u_isFast);
			app_secret = getString(json, u_app_secret);
			sign = getString(json, u_sign);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getShortName() {
		return ShortName.register;
	}

	@Override
	public int getShortType() {
		return ShortType.register;
	}

	@Override
	public String toString() {
		return "register [account=" + account + ", md5pwd=" + md5pwd
				+ ", bindMobile=" + bindMobile + ", bindEmail" + bindEmail
				+ ", nickName" + nickName + ",email" + email + ",sex" + sex
				+ ",imageUrl" + imageUrl + ",type=" + type + ",deviceParams="
				+ deviceParams + ",product=" + product + ",networkInfo="
				+ networkInfo + ",gatewaytype=" + gatewaytype + ","
				+ ",densityDpi=" + densityDpi + ",displayScreenWidth="
				+ displayScreenWidth + ",displayScreenHeight="
				+ displayScreenHeight + ",regIP=" + regIP + ",douId=" + douId
				+ ",productId=" + productId + ",imsi=" + imsi + ",channelId="
				+ channelId + ",versionName=" + versionName + ", isFast="
				+ isFast + ", app_secret" + app_secret + ", sign" + sign + "]";
	}

	public void updateSign(String app_key) {
		try {
			sign = Sign.calc(new String[] { account, md5pwd,
					String.valueOf(type), app_secret, app_key });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
