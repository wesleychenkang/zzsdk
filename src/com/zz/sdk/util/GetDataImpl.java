package com.zz.sdk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest.permission;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import com.zz.sdk.entity.DeviceProperties;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.PayResult;
import com.zz.sdk.entity.QiHooResult;
import com.zz.sdk.entity.Result;
import com.zz.sdk.entity.SdkUser;
import com.zz.sdk.entity.SdkUserTable;
import com.zz.sdk.entity.UserAction;

public class GetDataImpl {

	private static GetDataImpl mInstance;

	private DeviceProperties mDeviceProperties = null;

	protected static SdkUser mSdkUser;

	public static final String DEVICESYN = "devicesyn";
	/**
	 * 缓存从服务器上拿取的数据
	 */
	private static HashMap<String, String> mUrlCache = new HashMap<String, String>();

	private static Context mContext;

	private GetDataImpl(Context ctx) {

		mContext = ctx;

		mSdkUser = new SdkUser();
	}

	public static GetDataImpl getInstance(Context ctx) {
		if (mInstance == null) {
			mInstance = new GetDataImpl(ctx.getApplicationContext());
		}
		return mInstance;
	}

	/**
	 * 用户登录
	 * 
	 * @return true表示登录成功 false表示登录失败 </br> codes=0成功|1用户不存在|2密码错误
	 */
	public Result login(String loginName, String password, int autoLogin,
			Context ctx) {
		Application.isLogin = false;
		mSdkUser = new SdkUser();
		mSdkUser.loginName = loginName;
		mSdkUser.password = Utils.md5Encode(password);
		ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		list.add(new BasicNameValuePair("loginName", loginName));
		list.add(new BasicNameValuePair("password", Md5Code.encodePassword(password)));
		list.add(new BasicNameValuePair("projectId", Utils.getProjectId(ctx)));
		list.add(new BasicNameValuePair("productId",Utils.getProductId(ctx)));
		list.add(new BasicNameValuePair("serverId", Utils.getGameServerId(ctx)));
		String url = Constants.LOGIN_REQ;
		InputStream in = doRequest(url, list, 2);
		String json = parseJsonData(in);
		Logger.d("login json -> " + json);
		if (json == null) {
			return null;
		}
		Result result = (Result) JsonUtil.parseJSonObject(Result.class, json);
		if (result == null) {
			return null;
		}
		if ("0".equals(result.codes)) {
			Logger.d("LoginName ---------------- " + loginName);
			Application.loginName = loginName;
			Application.isLogin = true;
			mSdkUser.autoLogin = autoLogin;
			mSdkUser.password = password;
			syncSdkUser();
			isOperationDeviceSyn(loginName, ctx);
			UserAction useraction = new UserAction();
			useraction.loginName = loginName;
			useraction.actionType = UserAction.LOGIN;
			useraction.requestActivon(ctx);
		}
		return result;
	}

	/**
	 * 快速登录
	 * 
	 * @return 登录结果
	 */
	public Result quickLogin(Context ctx) {

		Application.isLogin = false;

		Logger.d("quicklogin---------");

		ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		list.add(new BasicNameValuePair("projectId", Utils.getProjectId(ctx)));
		list.add(new BasicNameValuePair("serverId", Utils.getGameServerId(ctx)));
		list.add(new BasicNameValuePair("productId",Utils.getProductId(ctx)));
		if (DebugFlags.DEBUG) {
			list.add(new BasicNameValuePair("imsi", DebugFlags.DEF_DEBUG_IMSI));
		} else {
			list.add(new BasicNameValuePair("imsi", Utils.getIMSI(ctx)));
		}
		String url = Constants.QUICK_LOGIN_REQ;
		try {
			InputStream in = doRequest(url, list, 2);
			if (in == null)
				return null;

			String json = parseJsonData(in);
			Logger.d("quick login json ----> " + json);
			if (json == null)
				return null;
			Result result = (Result) JsonUtil.parseJSonObject(Result.class,
					json);
			if (result != null && "0".equals(result.codes)) {
				Application.loginName = result.username;
				Application.isLogin = true;
				mSdkUser = new SdkUser();
				mSdkUser.loginName = result.username;
				mSdkUser.password = result.password;
				mSdkUser.autoLogin = 1;
				syncSdkUser();
				isOperationDeviceSyn(result.username, ctx);
				UserAction useraction = new UserAction();

				useraction.loginName = result.username;
				useraction.actionType = UserAction.AUTOREG;
				useraction.requestActivon(ctx);

			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 用户注册
	 * 
	 * @return 注册, codes=0成功|1失败|2用户名已经存在
	 */
	public Result register(String loginName, String password, Context ctx) {
		mSdkUser = new SdkUser();
		mSdkUser.loginName = loginName;
		mSdkUser.password = Utils.md5Encode(password);
		ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		list.add(new BasicNameValuePair("loginName", loginName));
		list.add(new BasicNameValuePair("password", Md5Code.encodePassword(password)));
		list.add(new BasicNameValuePair("projectId", Utils.getProjectId(ctx)));
		list.add(new BasicNameValuePair("serverId", Utils.getGameServerId(ctx)));
		list.add(new BasicNameValuePair("imsi", Utils.getIMSI(ctx)));
		list.add(new BasicNameValuePair("productId",Utils.getProductId(ctx)));
		String url = Constants.REG_REQ;
		InputStream in = doRequest(url, list, 2);
		String json = parseJsonData(in);
		Logger.d("register json -> " + json);
		if (json == null) {
			return null;
		}
		Result result = (Result) JsonUtil.parseJSonObject(Result.class, json);
		if (result == null) {
			return null;
		}
		Logger.d("register loginName -> " + loginName);
		if ("0".equals(result.codes)) {
			Application.loginName = loginName;
			Application.isLogin = true;
			mSdkUser.autoLogin = 1;
			mSdkUser.password = password;
			syncSdkUser();
			isOperationDeviceSyn(result.username, ctx);
			UserAction useraction = new UserAction();
			useraction.loginName = loginName;
			useraction.actionType = UserAction.REGISTER;
		}
		return result;
	}

	/** 获取QiHoo返回的token */
	public QiHooResult getAcessToken(String productId, String authCode) {
		ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		list.add(new BasicNameValuePair("productId", productId));
		list.add(new BasicNameValuePair("authCode", authCode));
		InputStream in = doRequest(Constants.GET_TOKEN, list, 2);
		String json = parseJsonData(in);
		Logger.d("QiHoo json -> " + json);
		if (json == null) {
			return null;
		}
		QiHooResult qr = (QiHooResult) JsonUtil.parseJSonObject(
				QiHooResult.class, json);
		if (qr != null) {
			return qr;
		}
		return null;
	}

	/**
	 * 修改密码
	 * 
	 * @param newPassword
	 *            新密码
	 * @return 修改密码结果
	 */
	public Result modifyPassword(String newPassword) {
		String oldPassword = Application.password;
		mSdkUser.newPassword = Utils.md5Encode(newPassword);
		mSdkUser.password = Utils.md5Encode(oldPassword);
		ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		list.add(new BasicNameValuePair("loginName", Application.loginName));
		list.add(new BasicNameValuePair("password", Md5Code.encodePassword(oldPassword)));
		list.add(new BasicNameValuePair("newPassword", Md5Code.encodePassword(newPassword)));
		list.add(new BasicNameValuePair("productId",Utils.getProductId(mContext)));
		InputStream in = doRequest(Constants.MODIFY_PWD, list, 2);

		if (in == null)
			return null;

		String json = parseJsonData(in);
		Logger.d("modify password json ----> " + json);
		if (json == null)
			return null;
		Result result = (Result) JsonUtil.parseJSonObject(Result.class, json);
		if (result != null && "0".equals(result.codes)) {
			Application.password = mSdkUser.password = newPassword;
			syncSdkUser();
		}
		return result;
	}

	/**
	 * 同步用户基本信息到数据库中
	 * 
	 * @return
	 */
	private boolean syncSdkUser() {
		// 更新用户数据到全局变量
		Application.password = mSdkUser.password;

		SdkUserTable t = SdkUserTable.getInstance(mContext);
		// 将用户名保存到sdcard
		Utils.writeAccount2SDcard(mContext, mSdkUser.loginName,
				mSdkUser.password);
		return t.update(mSdkUser);
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
	 * @param connectCount
	 *            请求连接的次数
	 * @return 返回内容
	 */
	private InputStream doRequest(String url,
			ArrayList<BasicNameValuePair> nvps, int connectCount) {
		HttpClient client = HttpUtil.getHttpClient(mContext);
		if (client == null) {
			return null;
		}
		HttpPost httpPost = new HttpPost(url);
		try {
			if (nvps != null) {
				Md5Code.addMd5Parameter(nvps);
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		HttpResponse response = null;
		int i = 0;
		while (i < connectCount) {
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
	 * 清除缓存
	 */
	public void clearCache() {
		mUrlCache.clear();
	}

	// 解析客服熱線和QQ
	private static void parseTelAndQQ(String str) {

		if (null != str && !"".equals(str)) {
			try {

				JSONObject json = new JSONObject(str);
				Application.customerServiceHotline = json.isNull("c") ? null
						: json.getString("c").trim();
				Application.customerServiceQQ = json.isNull("b") ? null : json
						.getString("b").trim();
			} catch (JSONException e) {
			}
		}
	}

	/**
	 * 取消支付中的结果
	 * 
	 * @param ctx
	 * @param OrderNum
	 * @param payMsg
	 */
	public void canclePay(String OrderNum, String payMsg) {
		String url = Constants.NPM_REQ;
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("cmgeOrderNum", OrderNum));
		nvps.add(new BasicNameValuePair("payMsg", payMsg));
		nvps.add(new BasicNameValuePair("productId",Utils.getProductId(mContext)));
		doRequest(url, nvps, 1);
	}

	/** 获取支付URL对应判断消息 */
	public Result getPayUrlMessage() {
		String url = Constants.GPM_REQ;
		InputStream in = doRequest(url, null, 2);
		String json = parseJsonData(in);
		Logger.d("zz_sdk" + json);
		if (json == null) {
			return null;
		}
		Result result = (Result) JsonUtil.parseJSonObject(Result.class, json);
		if (result == null) {
			return null;
		}
		return result;
	}

	/**
	 * 客服端的取消请求操作
	 * 
	 * @param ctx
	 *            上下文对象
	 * @param type
	 *            请求的取消的类型
	 * @return
	 */
	public Result request(Context ctx, UserAction user) {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("projectId", Utils.getProjectId(ctx)));
		nvps.add(new BasicNameValuePair("serverId",
				(user.serverId == null || user.serverId.length() == 0) ? Utils
						.getGameServerId(ctx) : user.serverId));
		nvps.add(new BasicNameValuePair("actionType", "" + user.actionType));
		nvps.add(new BasicNameValuePair("loginName", user.loginName));
		nvps.add(new BasicNameValuePair("memo", ""));
		nvps.add(new BasicNameValuePair("productId",Utils.getProductId(ctx)));
		String url = Constants.LOG_REQ;
		Log.d("zz_sdk", "请求的url:" + url);
		InputStream in = doRequest(url, nvps, 1);
		if (in == null)
			return null;

		String json = parseJsonData(in);
		if (json == null)
			return null;
		Result result = (Result) JsonUtil.parseJSonObject(Result.class, json);
		return result;

	}

	/**
	 * 
	 * 确认是否需要同步设备信息
	 * 
	 * @param loginName
	 */

	private void isOperationDeviceSyn(String loginName, Context ctx) {
		SharedPreferences prefs = mContext.getSharedPreferences(DEVICESYN,
				Context.MODE_PRIVATE);
		String res = prefs.getString(DEVICESYN, "tt");
		if (res.equals("1") || res.equals("tt")) {
			deviceSyn(loginName, ctx);
		}
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
	private Result deviceSyn(String loginname, Context ctx) {
		mDeviceProperties = new DeviceProperties(ctx);
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("loginName", loginname));
		nvps.add(new BasicNameValuePair("systemVersion", ""
				+ mDeviceProperties.versionCode));
		nvps.add(new BasicNameValuePair("deviceType", mDeviceProperties.type));
		nvps.add(new BasicNameValuePair("imei", mDeviceProperties.imei));
		nvps.add(new BasicNameValuePair("imsi", mDeviceProperties.imsi));
		nvps.add(new BasicNameValuePair("latitude", ""
				+ mDeviceProperties.latitude));
		nvps.add(new BasicNameValuePair("longtitude", ""
				+ mDeviceProperties.longitude));
		nvps.add(new BasicNameValuePair("area", "" + mDeviceProperties.area));
		nvps.add(new BasicNameValuePair("netType",
				mDeviceProperties.networkInfo));
		nvps.add(new BasicNameValuePair("projectId",
				mDeviceProperties.projectId));
		nvps.add(new BasicNameValuePair("sdkVersion",
				mDeviceProperties.sdkVersion));
		nvps.add(new BasicNameValuePair("productId",
				Utils.getProductId(ctx)));
		String url = Constants.DSYN_REQ;
		InputStream in = doRequest(url, nvps, 1);
		if (in == null)
			return null;
		String json = parseJsonData(in);
		if (json == null)
			return null;
		Result result = (Result) JsonUtil.parseJSonObject(Result.class, json);
		if (result == null) {
			return null;
		}
		SharedPreferences prefs = mContext.getSharedPreferences(DEVICESYN,
				Context.MODE_PRIVATE);
		if ("0".equals(result.codes)) {

			prefs.edit().putString(DEVICESYN, "0").commit();

		} else {

			prefs.edit().putString(DEVICESYN, "1").commit();
		}
		return result;
	}

	/**
	 * 平台登入 表示用户打开软件
	 * 
	 * @return
	 */
	public Result online(Context ctx) {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("projectId", Utils.getProjectId(ctx)));
		nvps.add(new BasicNameValuePair("imsi", Utils.getIMSI(ctx)));
		nvps.add(new BasicNameValuePair("actionType", UserAction.ONLINE));
		nvps.add(new BasicNameValuePair("productId",Utils.getProductId(ctx)));
		String url = Constants.LOG_REQ;
		InputStream in = doRequest(url, nvps, 1);
		if (in == null)
			return null;

		String json = parseJsonData(in);
		Logger.d("online json -> " + json);
		if (json == null)
			return null;
		Result result = (Result) JsonUtil.parseJSonObject(Result.class, json);
		return result;
	}

	/**
	 * 平台登出，表示用户关闭软件
	 * 
	 * @return
	 */
	public Result offline(Context ctx) {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("projectId", Utils.getProjectId(ctx)));
		nvps.add(new BasicNameValuePair("imsi", Utils.getIMSI(ctx)));
		nvps.add(new BasicNameValuePair("actionType", UserAction.OFFLINE));
		nvps.add(new BasicNameValuePair("productId",Utils.getProductId(ctx)));
		String url = Constants.LOG_REQ;

		InputStream in = doRequest(url, nvps, 1);
		String json = parseJsonData(in);
		Logger.d("offline json ----> " + json);
		if (json == null)
			return null;
		Result result = (Result) JsonUtil.parseJSonObject(Result.class, json);
		return result;
	}

	/**
	 * 
	 * @param type
	 * @param payParam
	 * @return
	 */
	public Result charge(int type, PayParam payParam) {
		if (DebugFlags.DEBUG) {
			payParam.loginName = DebugFlags.DEF_LOGIN_NAME;
		}
		ArrayList<BasicNameValuePair> all = payParam.getChargeParameters(type);
		all.add(new BasicNameValuePair("productId",Utils.getProductId(mContext)));
		if (all == null) {
			Result result1 = new Result();
			result1.codes = "-1";
			// 无效支付方式
			return result1;
		}
		String url = Constants.URL_SERVER_SRV + payParam.part;
		Log.d("zz_sdk", url);
		mSdkUser = new SdkUser();
		mSdkUser.loginName = Application.loginName;
		InputStream in = doRequest(url, all, 1);
		if (in == null)
			return null;
		String json = parseJsonData(in);
		Logger.d("charge json -> " + json);
		if (json == null)
			return null;
		Result result = (Result) JsonUtil.parseJSonObject(Result.class, json);
		if (result != null) {
			result.attach2 = json;
		}
		return result;
	}

	/**
	 * 获取支付列表
	 * 
	 * @return
	 */
	public PayChannel[] getPaymentList(PayParam charge) {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("requestId", ""));
		nvps.add(new BasicNameValuePair("serverId", charge.serverId));
		nvps.add(new BasicNameValuePair("productId",Utils.getProductId(mContext)));
		String url = Constants.GPL_REQ;
		InputStream in = doRequest(url, nvps, 2);
		if (in == null)
			return null;

		String json = parseJsonData(in);
		Logger.d("payment list josn ----> " + json);
		if (json == null)
			return null;
		Result result = (Result) JsonUtil.parseJSonObject(Result.class, json);
		PayChannel[] channelMessages = (PayChannel[]) JsonUtil.parseJSonArray(
				PayChannel.class, json);

		if (null == result || !"0".equals(result.codes)
				|| channelMessages == null) {
			return null;
		}

		parseTelAndQQ(result.payServerDesc);

		parseTopic(result.payServerDesc);

		Application.cardAmount = result.cardAmount;

		Set<Integer> payTypes = PayChannel.getPayType();

		ArrayList<PayChannel> payLists = new ArrayList<PayChannel>();
		boolean hasSmsPermission = false;

		if (payTypes.contains(PayChannel.PAY_TYPE_KKFUNPAY)) {
			Logger.d("有短信充值方式");

			try {
				if (PackageManager.PERMISSION_GRANTED == mContext
						.getPackageManager().checkPermission(
								permission.SEND_SMS, mContext.getPackageName())) {
					hasSmsPermission = true;
				} else {
					hasSmsPermission = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (PayChannel cm : channelMessages) {
			if (payTypes.contains(cm.type)) {

				if (cm.type == PayChannel.PAY_TYPE_KKFUNPAY
						&& !hasSmsPermission) {
					continue;
				}
				payLists.add(cm);
			}
		}

		channelMessages = payLists.toArray(new PayChannel[payLists.size()]);
		// 设置全局静态数据
		Application.mPayChannels = channelMessages;
		return channelMessages;
	}

	// 充值/活动说明
	private static void parseTopic(String str) {

		if (null != str && !"".equals(str)) {
			Application.topicDes = str;
		}
	}

	/**
	 * 查询订单
	 */
	public PayResult checkOrder(String ordrNumber) {
		ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		list.add(new BasicNameValuePair("cmgeOrderNum", ordrNumber));
		String url = Constants.GPM_QO;
		InputStream in = doRequest(url, list, 1);
		if (in == null) {
			return null;
		}
		String json = parseJsonData(in);
		Log.d("zz_sdk", json);
		PayResult p = (PayResult) JsonUtil.parseJSonObject(PayResult.class,
				json);
		if (p != null) {
			return p;
		}
		return null;
	}
}
