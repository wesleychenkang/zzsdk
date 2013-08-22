package com.zz.lib.pojo;

import java.io.Serializable;

import org.json.JSONObject;

import com.zz.lib.utils.Sign;

/**
 * 用户密码
 * 
 * @author RSun
 * @Date 2013-6-15下午12:15:33
 */
class UpdatePwd extends JsonParseInterface implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5729382919128511084L;

	// 字段key
	private static final String u_account = "a";
	private static final String u_md5pwd = "b";
	private static final String u_newmd5pwd = "c";

	private static final String u_app_secret = "y";
	private static final String u_sign = "z";

	/** a 帐号、手机号、邮箱 **/
	public String account;
	/** b md5后的密码(cmge内部md5) **/
	public String md5pwd;

	/** c 新密码 **/
	public String newmd5pwd;

	/** y 授权码 **/
	public String app_secret;

	/** z MD5签名 **/
	public String sign;

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			setString(json, u_account, account);
			setString(json, u_md5pwd, md5pwd);
			setString(json, u_newmd5pwd, newmd5pwd);
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
			newmd5pwd = getString(json, u_newmd5pwd);
			app_secret = getString(json, u_app_secret);
			sign = getString(json, u_sign);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getShortName() {
		return ShortName.updatePwd;
	}

	@Override
	public int getShortType() {
		return ShortType.updatePwd;
	}

	@Override
	public String toString() {
		return "UpdatePwd [account=" + account + ", md5pwd=" + md5pwd
				+ ", newmd5pwd=" + newmd5pwd + ", app_secret" + app_secret
				+ ", sign" + sign + "]";
	}

	public void updateSign(String app_key) {
		try {
			sign = Sign.calc(new String[] { account, md5pwd, newmd5pwd,
					app_secret, app_key });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
