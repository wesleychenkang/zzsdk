package com.zz.lib.pojo;

import java.io.Serializable;

import org.json.JSONObject;


/**
 * 返回请求处理状态
 * @author RSun
 * @Date 2013-6-15上午11:40:29
 */
public class Result extends JsonParseInterface implements Serializable {	
	
	private static final long serialVersionUID = 5780667638751697330L;
	
	// 字段key
	private static final String u_status = "a";
	private static final String u_statusdescr = "b";
	private static final String u_account = "c";
	private static final String u_userid = "d";
	private static final String u_time = "e";
	private static final String u_bindMobile = "f";
	private static final String u_bindEmail = "g";
	private static final String u_isBind = "h";
	private static final String u_randomPwd = "i";
	private static final String u_imsiRegCont = "j";
	private static final String u_isUse = "k";
	private static final String u_sign = "z";
	//登录状态，0-成功；-1 失败，可使用描述提示；-2 失败，无描述
	public static final int SUCCESS = 0;
	public static final int FAIL = -1;
	public static final int FAIL2 = -2;

	/** a 服务器返回的结果码，0成功；-1失败；-2可显示结果描述  **/
	public int status;
	/** b 结果描述  **/
	public String statusdescr;
	
	// ====================== 登录使用  =============================
	/** c 帐号、手机号、邮箱   **/
	public String account;
	/** d 用户id **/
	public int userid = 0;
	/** e 登录时间yyyyMMddHHmmss **/
	public String time;
	/** f 绑定手机号 **/
	public String bindMobile;
	/** g 绑定邮箱 **/
	public String bindEmail;

	/** h 是否绑定，1-已绑定 **/
	public int isBind;
	/** i 注册使用，随机密码   **/
	public String randomPwd;
	public int imsiRegCont;
	public int isUse;
	/** z MD5签名 **/
	public String sign;
	
	
	
	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			json.put(u_status, status);
			setString(json, u_statusdescr, statusdescr);
			setString(json, u_account, account);
			setInt(json, u_userid, userid);
			setString(json, u_time, time);
			setString(json, u_bindMobile, bindMobile);
			setString(json, u_bindEmail, bindEmail);
			setInt(json, u_isBind, isBind);
			setString(json, u_randomPwd, randomPwd);
			setInt(json, u_imsiRegCont, imsiRegCont);
			setInt(json, u_isUse, isUse);
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
			return ;
		try {
			status = getInt(json, u_status);
			statusdescr = getString(json, u_statusdescr);
			account = getString(json, u_account);
			userid = getInt(json, u_userid);
			time = getString(json, u_time);
			bindMobile = getString(json, u_bindMobile);
			bindEmail = getString(json, u_bindEmail);
			isBind = getInt(json, u_isBind);
			randomPwd = getString(json, u_randomPwd);
			if(json.has("j"))
				imsiRegCont = json.getInt("j");
			if(json.has("k"))
				isUse = json.getInt("k");
			sign = getString(json, u_sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public String getShortName() {
		return ShortName.result;
	}
	
	public String toString() {
		return "Result [status=" + status + ", descr=" + statusdescr
				+ ", account=" + account + ", userid" + userid + ", time"
				+ time + ", bindMobile" + bindMobile + ", bindEmail"
				+ bindEmail + ", sign" + sign + "]";
	}

	
}
