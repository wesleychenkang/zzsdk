package com.zz.sdk.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.zz.sdk.util.JsonUtil;

/**
 * 服务器请求返回的结果 集类
 * 登录，注册，修改密码，通用接口（取消支付等）
 * @author roger
 *
 */
public class Result implements JsonParseInterface {

	//结果码
		public String codes; 

		public String username;
		
		public String password;
		
		@Override
		public String toString() {
			return "Result [codes=" + codes + "&username=" +username + 
					"&password=" + password + "]";
		}
		@Override
		public JSONObject buildJson() {
			try {
				JSONObject json = new JSONObject();
				json.put("codes", codes);
				json.put("username", username);
				json.put("password", password);
				return json;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void parseJson(JSONObject json) {
			if (json == null) {
				return;
			}
			try {
				codes = json.isNull("codes") ? null : json.getJSONArray("codes").getString(0);
				username = json.isNull("username") ? null : json.getString("username");
				password = json.isNull("password") ? null : json.getString("password");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

}
