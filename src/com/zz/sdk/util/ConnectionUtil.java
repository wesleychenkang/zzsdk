package com.zz.sdk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Pair;

import com.zz.sdk.entity.DeviceProperties;
import com.zz.sdk.entity.JsonParseInterface;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.UserAction;
import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultAutoLogin;
import com.zz.sdk.entity.result.ResultBalance;
import com.zz.sdk.entity.result.ResultChangePwd;
import com.zz.sdk.entity.result.ResultLogin;
import com.zz.sdk.entity.result.ResultPayList;
import com.zz.sdk.entity.result.ResultPayMessage;
import com.zz.sdk.entity.result.ResultPropList;
import com.zz.sdk.entity.result.ResultQihoo;
import com.zz.sdk.entity.result.ResultRegister;
import com.zz.sdk.entity.result.ResultRequest;
import com.zz.sdk.entity.result.ResultRequestAlipayTenpay;
import com.zz.sdk.entity.result.ResultRequestKKFunPay;
import com.zz.sdk.entity.result.ResultRequestUionpay;
import com.zz.sdk.entity.result.ResultRequestYeePay;
import com.zz.sdk.entity.result.ResultRequestZZCoin;

/**
 * 网络连接工具(与服务器通信获取数据在此写相应的方法) 使用该工具类里的方法时。<b>请在线程中使用。</b>
 */
public class ConnectionUtil {

	private static final String K_PRODUCT_ID = "productId";
	private static final String K_PROJECT_ID = "projectId";
	private static final String K_SERVER_ID = "serverId";
	private static final String K_LOGIN_NAME = "loginName";
	private static final String K_PASSWORD = "password";

	private Context mContext;

	public ConnectionUtil(Context ctx) {
		mContext = ctx;
	}

	/**
	 * 将 请求参数二次处理
	 * 
	 * @param params    参数
	 */
	private List<BasicNameValuePair> packHttpParams(
			HashMap<String, String> params) {
		if (!params.containsKey(K_PRODUCT_ID))
			params.put(K_PRODUCT_ID, Utils.getProductId(mContext));
		if (!params.containsKey(K_PROJECT_ID))
			params.put(K_PROJECT_ID, Utils.getProjectId(mContext));
		if (!params.containsKey(K_SERVER_ID))
			params.put(K_SERVER_ID, Utils.getGameServerId(mContext));
		String sign = Md5Code.encodeMd5Parameter(params);
		List<BasicNameValuePair> npv = new ArrayList<BasicNameValuePair>();
		for (Entry<String, String> e : params.entrySet()) {
			npv.add(new BasicNameValuePair(e.getKey(), e.getValue()));
		}
		npv.add(new BasicNameValuePair(Constants.SING, sign));
		return npv;
	}

	/**
	 * 请求数据
	 * 
	 * @param clazz
	 *            数据构造类，基于 {@link JsonParseInterface}。 一般为 &lt;? extends
	 *            {@link BaseResult}&gt;
	 * @param url
	 *            地址
	 * @param nvps
	 *            参数
	 * @param attempts
	 *            尝试次数
	 * @return 如果返回 null 表示构造类失败，否则自行判断有无经过
	 *         {@link JsonParseInterface#parseJson(JSONObject)} 构造数据。如
	 *         {@link BaseResult#isUsed()} {@link BaseResult#isSuccess()}等
	 */
	private <T> T doRequest(Class<T> clazz, String url,
			List<BasicNameValuePair> nvps, int attempts) {
		T br = null;
		try {
			// step1: 构造返回值
			br = clazz.newInstance();
			if (DebugFlags.DEBUG) {
				Logger.d("url:" + url);
				Logger.d("request:" + nvps);
			}
			// step2: 向服务器发起请求
			InputStream is = doRequest(url, nvps, attempts);
			if (is != null) {
				// step3: 解析返回值内容
				String json = parseJsonData(is);
				JSONObject o;
				try {
					o = new JSONObject(json);
					if (DebugFlags.DEBUG) {
						Logger.d(o);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					if (DebugFlags.DEBUG) {
						Logger.d("D: 内容无效!");
					}
					// 如果内容无效，则使用空数据，表明至少有过返回值
					o = new JSONObject();
				}
				if (br instanceof JsonParseInterface)
					((JsonParseInterface) br).parseJson(o);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			if (DebugFlags.DEBUG) {
				Logger.d("bad class:" + clazz);
			}
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return br;
	}

	private <T> T doRequest(Class<T> clazz, String url, int attempts,
			String... key_val) {
		HashMap<String, String> param = new HashMap<String, String>();
		if (key_val != null && key_val.length > 0) {
			for (int i = 0; i < key_val.length; i += 2) {
				param.put(key_val[i], key_val[i + 1]);
			}
		}
		return doRequest(clazz, url, param.size() > 0 ? packHttpParams(param)
				: null, attempts);
	}

	/**
	 * 请求c/s数据
	 * 
	 * @param url
	 *            请求的url
	 * 
	 * @param nvps
	 *            请求的参数
	 * 
	 * @param attempts
	 *            请求连接的次数
	 * @return 返回内容
	 */
	private InputStream doRequest(String url, List<BasicNameValuePair> nvps,
			int attempts) {
		HttpClient client = HttpUtil.getHttpClient(mContext);
		if (client == null) {
			return null;
		}
		HttpPost httpPost = new HttpPost(url);
		try {
			if (nvps != null) {
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			Logger.d("UnsupportedEncodingException");
		}
		HttpResponse response = null;
		int i = 0;
		while (i < attempts) {
			try {
				response = client.execute(httpPost);
				int status = response.getStatusLine().getStatusCode();
				Logger.d("status == " + status);
				if (status == HttpStatus.SC_OK) {
					return response.getEntity().getContent();
				}
			} catch (ClientProtocolException e) {
				Logger.d(e.getMessage());
			} catch (IOException e) {
				Logger.d(e.getMessage());
			} finally {
				// client.getConnectionManager().shutdown();
			}
			i++;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
		return null;
	}

	private String parseJsonData(InputStream in) {
		if (in == null)
			return null;
		String tmp = null;
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try {
			while ((tmp = reader.readLine()) != null) {
				sb.append(tmp);
			}
			// System.out.println("解析后的信息---》" + Encrypt.decode(sb.toString()));
			return sb.toString();// Utils.decode(sb.toString());
		} catch (Exception e) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 用户登录
	 * 
	 * @return true表示登录成功 false表示登录失败 </br> codes=0成功|1用户不存在|2密码错误
	 */
	public ResultLogin login(String loginName, String password) {
		return doRequest(ResultLogin.class, Constants.LOGIN_REQ, 2 //
				, K_LOGIN_NAME, loginName //
				, K_PASSWORD, Md5Code.encodePassword(password) //
		);
	}

	/**
	 * 快速登录
	 * 
	 * @return 登录结果
	 */
	public ResultAutoLogin quickLogin() {
		String imsi;
		if (DebugFlags.DEBUG) {
			imsi = DebugFlags.DEF_DEBUG_IMSI;
		} else {
			imsi = Utils.getIMSI(mContext);
		}
		return doRequest(ResultAutoLogin.class, Constants.QUICK_LOGIN_REQ, 2 //
				, "imsi", imsi//
		);
	}

	/**
	 * 用户注册
	 * 
	 * @return 注册, codes=0成功|1失败|2用户名已经存在
	 */
	public ResultRegister register(String loginName, String password) {
		String imsi = Utils.getIMSI(mContext);
		String e_password = Md5Code.encodePassword(password);
		return doRequest(ResultRegister.class, Constants.REG_REQ, 2 //
				, "imsi", imsi //
				, K_LOGIN_NAME, loginName //
				, K_PASSWORD, e_password //
		);
	}

	/** 获取QiHoo返回的token get360UserInfo(获取360用户信息） */
	public ResultQihoo getAcessToken(String productId, String authCode) {
		return doRequest(ResultQihoo.class, Constants.GET_TOKEN, 2 //
				, "productId", productId //
				, "authCode", authCode //
		);
	}

	/**
	 * 修改密码
	 * 
	 * @param user
	 *            用户名
	 * @param newPassword
	 *            新密码
	 * @param oldPassword
	 *            旧密码
	 * @return 修改密码结果
	 */
	public ResultChangePwd modifyPassword(String user, String oldPassword,
			String newPassword) {
		return doRequest(ResultChangePwd.class, Constants.MODIFY_PWD, 2 //
				, K_LOGIN_NAME, user //
				, K_PASSWORD, Md5Code.encodePassword(oldPassword) //
				, "newPassword", Md5Code.encodePassword(newPassword) //
				, "productId", Utils.getProductId(mContext) //
		);
	}


	/**
	 * 取消支付中的结果
	 * 
	 * @param OrderNum
	 *            订单号
	 * @param payMsg
	 *            文本消息
	 * @param submitAmount
	 *            金额（可选）
	 */
	public BaseResult canclePay(String OrderNum, String payMsg,
			String submitAmount) {
		return doRequest(BaseResult.class, Constants.NPM_REQ, 1 //
				, "cmgeOrderNum", OrderNum //
				, "payMsg", payMsg //
				, "submitAmount", submitAmount //
		);
	}

	/** 获取支付URL对应判断消息 */
	public ResultPayMessage getPayUrlMessage() {
		return doRequest(ResultPayMessage.class, Constants.GPM_REQ, 2 //
		);
	}

	/**
	 * 用户行为Log通用接口
	 * 
	 * @param user
	 * @return
	 */
	public BaseResult request(UserAction user) {
		return doRequest(BaseResult.class, Constants.LOG_REQ, 1 //
				, "actionType", "" + user.actionType //
				, K_LOGIN_NAME, user.loginName //
				, "memo", "" //
		);
	}

	/**
	 * 客服端同步设备信息请求
	 *
	 * @param loginname
	 *            登录的用户名
	 * @return
	 */
	public BaseResult deviceSyn(String loginname) {
		DeviceProperties properties = new DeviceProperties(mContext);
		return doRequest(BaseResult.class, Constants.DSYN_REQ, 1 //
				, K_LOGIN_NAME, loginname //
				, "systemVersion", "" + properties.versionCode //
				, "deviceType", properties.type //
				, "imei", properties.imei //
				, "imsi", properties.imsi //
				, "latitude", "" + properties.latitude //
				, "longtitude", "" + properties.longitude //
				, "area", "" + properties.area //
				, "netType", properties.networkInfo //
				, "projectId", properties.projectId //
				, "sdkVersion", properties.sdkVersion //
		);
	}

	/**
	 * 平台登入 表示用户打开软件
	 *
	 * @return
	 */
	public BaseResult online(Context ctx) {
		return doRequest(BaseResult.class, Constants.LOG_REQ, 1 //
				, "imsi", Utils.getIMSI(mContext) //
				, "actionType", UserAction.ONLINE //
		);
	}

	/**
	 * 平台登出，表示用户关闭软件
	 * 
	 * @return
	 */
	public BaseResult offline(Context ctx) {
		return doRequest(BaseResult.class, Constants.LOG_REQ, 1 //
				, "imsi", Utils.getIMSI(mContext) //
				, "actionType", UserAction.OFFLINE //
		);
	}

	/**
	 * 详细支付请求
	 * 
	 * @param type
	 * @param payParam
	 * @return
	 */
	public ResultRequest charge(int type, PayParam payParam) {
		Class<? extends ResultRequest> clazz;
		switch (type) {
		case PayChannel.PAY_TYPE_ALIPAY:
		case PayChannel.PAY_TYPE_TENPAY:
			clazz = ResultRequestAlipayTenpay.class;
			break;
		case PayChannel.PAY_TYPE_UNMPAY:
		case PayChannel.PAY_TYPE_EX_DEZF:
			clazz = ResultRequestUionpay.class;
			break;
		case PayChannel.PAY_TYPE_YEEPAY_LT:
		case PayChannel.PAY_TYPE_YEEPAY_YD:
		case PayChannel.PAY_TYPE_YEEPAY_DX:
			clazz = ResultRequestYeePay.class;
			break;
		case PayChannel.PAY_TYPE_KKFUNPAY:
			clazz = ResultRequestKKFunPay.class;
			break;
		case PayChannel.PAY_TYPE_ZZCOIN:
			clazz = ResultRequestZZCoin.class;
			break;

		case PayChannel.PAY_TYPE_KKFUNPAY_EX:
		default:
			Logger.d("onkonw type!");
			clazz = ResultRequest.class;
			break;
		}

		ArrayList<BasicNameValuePair> all = payParam.getChargeParameters(type);
		if (all == null) {
			// 无效支付方式
			// result1.codes = "-1";
			return null;
		}
		all.add(new BasicNameValuePair("productId", Utils
				.getProductId(mContext)));
		Md5Code.addMd5Parameter(all);
		return doRequest(clazz, Constants.URL_SERVER_SRV + payParam.part, all,
				1);
	}

	/**
	 * 获取支付列表
	 * 
	 * @return
	 */
	public ResultPayList getPaymentList(PayParam charge) {
		return doRequest(ResultPayList.class, Constants.GPL_REQ, 2,
				"requestId", "", "serverId", charge.serverId, K_LOGIN_NAME,
				charge.loginName);
	}

	/**
	 * 查询订单 queryOrder(查询订单状态）0成功|1失败 0成功|其它失败
	 */
	public BaseResult checkOrder(String ordrNumber) {
		return doRequest(BaseResult.class, Constants.GPM_QO, 1 //
				, "cmgeOrderNum", ordrNumber //
		);
	}

	/**
	 * 获取余额
	 * 
	 * @param loginName
	 * @return
	 */
	public ResultBalance getBalance(String loginName) {
		return doRequest(ResultBalance.class, Constants.GBL_REQ, 1 //
				, K_LOGIN_NAME, loginName //
		);
	}

	/**
	 * 获取道具 列表
	 * 
	 * @param rowstart
	 * @param rowcount
	 */
	public ResultPropList getPropList(int rowstart, int rowcount) {
		return doRequest(ResultPropList.class, Constants.GPRO_REQ, 1 //
				, "rowstart", String.valueOf(rowstart) //
				, "rowcount", String.valueOf(rowcount) //
		);
	}

	//
	// ////////////////////////////////////////////////////////////////////////
	//
	//
	//

	private static int sRefCount = 0;
	private static ConnectionUtil sInstance = null;

	public static ConnectionUtil getInstance(Context ctx) {
		if (sInstance == null) {
			sInstance = new ConnectionUtil(ctx);
		}
		sRefCount++;
		return sInstance;
	}

	public static void detachInstance(ConnectionUtil cu) {
		if (sRefCount == 0 || cu == null) {
			return;
		}

		sRefCount--;
		if (sRefCount == 0) {
			sInstance = null;
		}
	}
}
