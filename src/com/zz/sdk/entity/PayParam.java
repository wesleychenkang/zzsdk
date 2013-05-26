package com.zz.sdk.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description: 支付参数
 * @author roger
 */

public class PayParam implements Serializable, JsonParseInterface {
	private static final long serialVersionUID = 5L;
	public String loginName;

	public String gameRole;

	public String serverId;
	public String amount;
	public String projectId;
	public String requestId;
	public String channelId;
	public String cardNo;// 易宝充值卡号码
	public String cardPassword; // 易宝充值卡密码
	public String type; // 易宝充值类型
	public String callBackInfo;

	public String smsActionType; // 短信请求类型 1获取通道 2提交订单
	public String smsImsi;
	
	/** 附加参数，直接使用 */
	public HashMap<String, String> attachParam;

	@Override
	public String toString() {
		return "Charge [gameRole=" + gameRole + ", serverId=" + serverId
				+ ", amount=" + amount + ", projectId=" + projectId
				+ ", cardNo=" + cardNo + ", cardPassword=" + cardPassword
				+ ", callBackInfo=" + callBackInfo + "]";
	}

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			json.put("loginName", loginName);
			json.put("gameRole", gameRole);
			json.put("serverId", serverId);
			json.put("amount", amount);
			json.put("projectId", projectId);
			json.put("requestId", requestId);
			json.put("channelId", channelId);
			json.put("cardNo", cardNo);
			json.put("cardPassword", cardPassword);
			json.put("type", type);
			json.put("callBackInfo", callBackInfo);
			json.put("smsActionType", smsActionType);
			json.put("smsImsi", smsImsi);
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
			loginName = json.isNull("loginName") ? null : json
					.getString("loginName");
			gameRole = json.isNull("gameRole") ? null : json
					.getString("gameRole");
			serverId = json.isNull("serverId") ? null : json
					.getString("serverId");
			amount = json.isNull("amount") ? null : json.getString("amount");
			projectId = json.isNull("projectId") ? "-1" : json
					.getString("projectId");
			requestId = json.isNull("requestId") ? "-1" : json
					.getString("requestId");
			channelId = json.isNull("channelId") ? "-1" : json
					.getString("channelId");
			cardNo = json.isNull("cardNo") ? null : json.getString("cardNo");
			cardPassword = json.isNull("cardPassword") ? null : json
					.getString("cardPassword");
			type = json.isNull("type") ? null : json.getString("type");
			callBackInfo = json.isNull("callBackInfo") ? null : json
					.getString("callBackInfo");
			smsActionType = json.isNull("smsActionType") ? null : json
					.getString("smsActionType");
			smsImsi = json.isNull("smsImsi") ? null : json.getString("smsImsi");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getShortName() {
		return "c";
	}

	// :%s/\t.*$//g
	// :sort
	// :%s/^\(.*\)\(\n\1\)\+$/\1/
	// :%s/\(.*\)/public static final String K_\U\1 = "\l\1";/g
	public static final String K_AMOUNT = "amount";
	public static final String K_CARDNO = "cardNo";
	public static final String K_CARDPWD = "cardPwd";
	public static final String K_GAMEROLE = "gameRole";
	public static final String K_IMSI = "imsi";
	public static final String K_LOGINNAME = "loginName";
	public static final String K_PROJECTID = "projectId";
	public static final String K_REQUESTID = "requestId";
	public static final String K_SERVERID = "serverId";
	public static final String K_TYPE = "type";

	public String getUrl_PayAction(int payType) {
		StringBuilder b = new StringBuilder();
		switch (payType) {
		case PayChannel.PAY_TYPE_ALIPAY:
			// :%s/\t.*$//g
			// :%s/\(.*\)/.append("\&").append(K_\U\1\l).append("=").append(\1)/g
			b.append("pali.lg").append("?").append(K_LOGINNAME).append("=")
					.append(loginName).append("&").append(K_GAMEROLE)
					.append("=").append(gameRole).append("&")
					.append(K_SERVERID).append("=").append(serverId)
					.append("&").append(K_PROJECTID).append("=")
					.append(projectId).append("&").append(K_AMOUNT).append("=")
					.append(amount).append("&").append(K_REQUESTID).append("=")
					.append(requestId);
			break;
		case PayChannel.PAY_TYPE_UNMPAY:
			b.append("pupmp.lg");
			b.append("?").append(K_LOGINNAME).append("=").append(loginName)
					.append("&").append(K_GAMEROLE).append("=")
					.append(gameRole).append("&").append(K_SERVERID)
					.append("=").append(serverId).append("&")
					.append(K_PROJECTID).append("=").append(projectId)
					.append("&").append(K_AMOUNT).append("=").append(amount)
					.append("&").append(K_REQUESTID).append("=")
					.append(requestId);
			break;

		case PayChannel.PAY_TYPE_TENPAY:
			b.append("pten.lg");
			b.append("?").append(K_LOGINNAME).append("=").append(loginName)
					.append("&").append(K_GAMEROLE).append("=")
					.append(gameRole).append("&").append(K_SERVERID)
					.append("=").append(serverId).append("&")
					.append(K_PROJECTID).append("=").append(projectId)
					.append("&").append(K_AMOUNT).append("=").append(amount)
					.append("&").append(K_REQUESTID).append("=")
					.append(requestId);
			break;

		case PayChannel.PAY_TYPE_YEEPAY_LT:
			// b.append(null);
			break;

		case PayChannel.PAY_TYPE_YEEPAY_YD:
			// b.append(null);
			break;

		case PayChannel.PAY_TYPE_KKFUNPAY:
			b.append("pkkfun.lg");
			b.append("?").append(K_LOGINNAME).append("=").append(loginName)
					.append("&").append(K_GAMEROLE).append("=")
					.append(gameRole).append("&").append(K_SERVERID)
					.append("=").append(serverId).append("&")
					.append(K_PROJECTID).append("=").append(projectId)
					.append("&").append(K_REQUESTID).append("=")
					.append(requestId).append("&").append(K_IMSI).append("=")
					.append(smsImsi);
			break;

		case PayChannel.PAY_TYPE_KKFUNPAY_EX:
			b.append("pkkfunnt.lg");
			b.append("?").append(K_IMSI).append("=").append(smsImsi);
			break;

		default:
			break;
		}
		if (b.length() < 1)
			return null;
		
		if (attachParam != null) {
			Set<Entry<String, String>> s = attachParam.entrySet();
			for (Entry<String, String> e : s) {
				b.append("&").append(e.getKey()).append("=")
						.append(e.getValue());
			}
		}
		
		return b.toString();
	}
}
