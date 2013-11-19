package com.zz.lib.pojo;

import java.io.Serializable;

import org.json.JSONObject;

import com.zz.lib.utils.Sign;

/**
 * 用户登录对象
 * 
 * @author RSun
 * @Date 2013-6-15下午12:15:33
 */
class Login extends JsonParseInterface implements Serializable {

	private static final long serialVersionUID = 965609947231704326L;

	// 字段key
	private static final String u_account = "a";
	private static final String u_md5pwd = "b";
	private static final String u_atype = "c";
	private static final String u_dtype = "d";
	private static final String u_lastIP = "f";
	private static final String u_app_secret = "y";
	private static final String u_sign = "z";

	public static final int TYPE_ACCOUNT = 1;
	public static final int TYPE_MOBILE = 2;
	public static final int TYPE_EMAIL = 3;
	public static final int TYPE_UNKNOWN = 4;

	public static final int DTYPE_MBOILE = 1;
	public static final int DTYPE_EMAIL = 2;
	public static final int DTYPE_ALL = 3;

	/** a 帐号、手机号、邮箱 **/
	public String account;
	/** b md5后的密码(cmge内部md5) **/
	public String md5pwd;

	/** c 帐号类型[1-帐号；2-手机号；3-邮箱；4-未知] **/
	public int atype;
	/** d 登录类型（根据此决定返回数据），默认0 **/
	public int dtype = 0;
	/** f 最后登录IP **/
	public String lastIP;
	/** y 授权码 **/
	public String app_secret;

	/** z MD5签名 **/
	public String sign;

	public void updateSign(String app_key) {
		try {
			sign = Sign.calc(new String[] { account, md5pwd,
					String.valueOf(atype), String.valueOf(dtype), app_secret,
					app_key });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			setString(json, u_account, account);
			setString(json, u_md5pwd, md5pwd);
			setInt(json, u_atype, atype);
			setInt(json, u_dtype, dtype);
			setString(json, u_lastIP, lastIP);
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
			atype = getInt(json, u_atype);
			dtype = getInt(json, u_dtype);
			lastIP = getString(json, u_lastIP);
			app_secret = getString(json, u_app_secret);
			sign = getString(json, u_sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getShortName() {
		return ShortName.login;
	}

	@Override
	public int getShortType() {
		return ShortType.login;
	}

	@Override
	public String toString() {
		return "Login [account=" + account + ", md5pwd=" + md5pwd + ", atype="
				+ atype + ", dtype" + dtype + ", lastIP" + lastIP
				+ ", app_secret" + app_secret + ", sign" + sign + "]";
	}

}
