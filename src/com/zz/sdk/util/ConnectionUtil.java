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
import com.zz.sdk.entity.result.ResultQihoo;
import com.zz.sdk.entity.result.ResultRegister;
import com.zz.sdk.entity.result.ResultRequest;
import com.zz.sdk.entity.result.ResultRequestAlipayTenpay;
import com.zz.sdk.entity.result.ResultRequestKKFunPay;
import com.zz.sdk.entity.result.ResultRequestUionpay;
import com.zz.sdk.entity.result.ResultRequestYeePay;

/**
 * 网络连接工具(与服务器通信获取数据在此写相应的方法) 使用该工具类里的方法时。请在线程中使用。
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
	 * @param nvps
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
		npv.add(new BasicNameValuePair(Constants.E, "1"));
		npv.add(new BasicNameValuePair(Constants.SING, sign));
		return npv;
	}

	private <T> T doRequest(Class<T> clazz, String url,
			List<BasicNameValuePair> nvps, int attempts) {

		InputStream is = doRequest(url, nvps, attempts);

		if (is != null) {
			String json = parseJsonData(is);

			try {
				T br = clazz.newInstance();
				if (br instanceof BaseResult)
					((BaseResult) br).parseJson(new JSONObject(json));
				return br;
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
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
	public ResultLogin login(String loginName, String password, int autoLogin,
			Context ctx) {
		return doRequest(ResultLogin.class, Constants.LOGIN_REQ, 2,
				K_LOGIN_NAME, loginName, K_PASSWORD,
				Md5Code.encodePassword(password));
	}

	/**
	 * 快速登录
	 * 
	 * @return 登录结果
	 */
	public ResultAutoLogin quickLogin(Context ctx) {
		String imsi;
		if (DebugFlags.DEBUG) {
			imsi = DebugFlags.DEF_DEBUG_IMSI;
		} else {
			imsi = Utils.getIMSI(ctx);
		}
		return doRequest(ResultAutoLogin.class, Constants.QUICK_LOGIN_REQ, 2,
				"imsi", imsi);
		// Application.isLogin = true;
		// syncSdkUser();
		// isOperationDeviceSyn(result.username, ctx);
		// useraction.actionType = UserAction.AUTOREG;
		// useraction.requestActivon(ctx);
	}

	/**
	 * 用户注册
	 * 
	 * @return 注册, codes=0成功|1失败|2用户名已经存在
	 */
	public ResultRegister register(String loginName, String password) {
		String imsi = Utils.getIMSI(mContext);
		String e_password = Md5Code.encodePassword(password);
		return doRequest(ResultRegister.class, Constants.REG_REQ, 2, "imsi",
				imsi, K_LOGIN_NAME, loginName, K_PASSWORD, e_password);
		// Application.isLogin = true;
		// syncSdkUser();
		// isOperationDeviceSyn(result.username, ctx);
		// useraction.actionType = UserAction.REGISTER;

	}

	/** 获取QiHoo返回的token */
	public ResultQihoo getAcessToken(String productId, String authCode) {
		return doRequest(ResultQihoo.class, Constants.GET_TOKEN, 2,
				"productId", productId, "authCode", authCode);
	}

	/**
	 * 修改密码
	 * 
	 * @param newPassword
	 *            新密码
	 * @param user
	 *            用户名
	 * @param oldPassword
	 *            旧密码
	 * @return 修改密码结果
	 */
	public ResultChangePwd modifyPassword(String user, String oldPassword,
			String newPassword) {
		return doRequest(ResultChangePwd.class, Constants.MODIFY_PWD, 2,
				K_LOGIN_NAME, user, K_PASSWORD,
				Md5Code.encodePassword(oldPassword), "newPassword",
				Md5Code.encodePassword(newPassword), "productId",
				Utils.getProductId(mContext));
		// syncSdkUser();
	}

	/**
	 * 同步用户基本信息到数据库中
	 * 
	 * @return
	 */
	private boolean syncSdkUser() {
		// TODO:
		// 更新用户数据到全局变量
		// Application.password = mSdkUser.password;
		// SdkUserTable t = SdkUserTable.getInstance(mContext);
		// // 将用户名保存到sdcard
		// Utils.writeAccount2SDcard(mContext, mSdkUser.loginName,
		// mSdkUser.password);
		//
		// if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
		// if (PojoUtils.isDouquUser(mSdkUser.loginName)) {
		// // 不将逗趣的账户储存到sd卡
		// PojoUtils.updateDouquUser_SDCard(
		// PojoUtils.getDouquBaseName(mSdkUser.loginName),
		// mSdkUser.password);
		// }
		// }
		//
		// return t.update(mSdkUser);
		return false;
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
	public void canclePay(String OrderNum, String payMsg, String submitAmount) {
		doRequest(BaseResult.class, Constants.NPM_REQ, 1, "cmgeOrderNum",
				OrderNum, "payMsg", payMsg, "submitAmount", submitAmount);
	}

	/** 获取支付URL对应判断消息 */
	public ResultPayMessage getPayUrlMessage() {
		return doRequest(ResultPayMessage.class, Constants.GPM_REQ, 2);
	}

	/**
	 * 用户行为Log通用接口
	 * 
	 * @param user
	 * @return
	 */
	public BaseResult request(UserAction user) {
		return doRequest(BaseResult.class, Constants.LOG_REQ, 1, "actionType",
				"" + user.actionType, K_LOGIN_NAME, user.loginName, "memo", "");
	}

	/**
	 * 客服端同步设备信息请求
	 * 
	 * @param ctx
	 *            上下文对象
	 * @param dp
	 *            设备信息类的对象
	 * @param loginname
	 *            登录的用户名
	 * @return
	 */
	public BaseResult deviceSyn(String loginname, Context ctx) {
		DeviceProperties mDeviceProperties = new DeviceProperties(ctx);
		return doRequest(BaseResult.class, Constants.DSYN_REQ, 1, //
				K_LOGIN_NAME, loginname, //
				"systemVersion", "" + mDeviceProperties.versionCode, //
				"deviceType", mDeviceProperties.type, //
				"imei", mDeviceProperties.imei, //
				"imsi", mDeviceProperties.imsi, //
				"latitude", "" + mDeviceProperties.latitude, //
				"longtitude", "" + mDeviceProperties.longitude, //
				"area", "" + mDeviceProperties.area, //
				"netType", mDeviceProperties.networkInfo, //
				"projectId", mDeviceProperties.projectId, //
				"sdkVersion", mDeviceProperties.sdkVersion);
		// SharedPreferences prefs = mContext.getSharedPreferences(DEVICESYN,
		// Context.MODE_PRIVATE);
		// if ("0".equals(result.codes)) {
		// prefs.edit().putString(DEVICESYN, "0").commit();
		// } else {
		// prefs.edit().putString(DEVICESYN, "1").commit();
		// }
	}

	/**
	 * 平台登入 表示用户打开软件
	 * 
	 * @return
	 */
	public BaseResult online(Context ctx) {
		return doRequest(BaseResult.class, Constants.LOG_REQ, 1, "imsi",
				Utils.getIMSI(mContext), "actionType", UserAction.ONLINE);
	}

	/**
	 * 平台登出，表示用户关闭软件
	 * 
	 * @return
	 */
	public BaseResult offline(Context ctx) {
		return doRequest(BaseResult.class, Constants.LOG_REQ, 1, "imsi",
				Utils.getIMSI(mContext), "actionType", UserAction.OFFLINE);
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

		// parseTelAndQQ(result.payServerDesc);
		// parseTopic(result.payServerDesc);
		// Application.cardAmount = result.cardAmount;
		// Set<Integer> payTypes = PayChannel.getPayType();
		// ArrayList<PayChannel> payLists = new ArrayList<PayChannel>();
		// boolean hasSmsPermission = false;
		//
		// if (payTypes.contains(PayChannel.PAY_TYPE_KKFUNPAY)) {
		// Logger.d("有短信充值方式");
		//
		// try {
		// if (PackageManager.PERMISSION_GRANTED == mContext
		// .getPackageManager().checkPermission(
		// permission.SEND_SMS, mContext.getPackageName())) {
		// hasSmsPermission = true;
		// } else {
		// hasSmsPermission = false;
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		//
		// for (PayChannel cm : channelMessages) {
		// if (payTypes.contains(cm.type)) {
		//
		// if (cm.type == PayChannel.PAY_TYPE_KKFUNPAY
		// && !hasSmsPermission) {
		// continue;
		// }
		// payLists.add(cm);
		// }
		// }
		//
		// channelMessages = payLists.toArray(new PayChannel[payLists.size()]);
		// // 设置全局静态数据
		// Application.mPayChannels = channelMessages;
		// return channelMessages;
	}

	/**
	 * 查询订单
	 */
	public BaseResult checkOrder(String ordrNumber) {
		// TODO:
		return doRequest(BaseResult.class, Constants.GPM_QO, 1, "cmgeOrderNum",
				ordrNumber);
	}

	/**
	 * 单机游戏的登录
	 * 
	 * @param ctx
	 */
	public void loginForLone(Pair<String, String> account) {
		// if (account != null) {
		// final String loginName = account.first;
		// final String password = account.second;
		// if (loginName != null && !"".equals(loginName)) {
		// login(loginName.trim(), password.trim(), 1, mContext);
		// } else {
		// quickLogin(mContext);
		// }
		// } else {
		// quickLogin(mContext);
		// }
		int a = 0 / 10;
	}

	/**
	 * 获取余额
	 * 
	 * @param loginName
	 * @return
	 */
	public ResultBalance getBalance(String loginName) {
		return doRequest(ResultBalance.class, Constants.GBL_REQ, 1,
				K_LOGIN_NAME, loginName);
	}

	/**
	 * 获取道具 列表
	 * 
	 * @param rowstart
	 * @param rowcount
	 */
	public ResultBalance getPropList(int rowstart, int rowcount) {
		return doRequest(ResultBalance.class, Constants.GPRO_REQ, 1,
				"rowstart", String.valueOf(rowstart), "rowcount",
				String.valueOf(rowcount));
	}

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
