package com.zz.sdk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.zz.sdk.activity.Application;
import com.zz.sdk.activity.Constants;
import com.zz.sdk.entity.DeviceProperties;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.Result;
import com.zz.sdk.entity.SdkUser;
import com.zz.sdk.entity.SdkUserTable;

public class GetDataImpl {

	private static GetDataImpl mInstance;

	private DeviceProperties mDeviceProperties;

	protected static SdkUser mSdkUser;

	/**
	 * 缓存从服务器上拿取的数据
	 */
	private static HashMap<String, String> mUrlCache = new HashMap<String, String>();

	private static Context mContext;

	private GetDataImpl(Context ctx) {
		mContext = ctx;
		mDeviceProperties = new DeviceProperties(ctx);
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
	 * @return true表示登录成功 false表示登录失败
	 */
	public Result login(String loginName, String password, int autoLogin,
			Context ctx) {
		Application.isLogin = false;

		mSdkUser = new SdkUser();
		mSdkUser.loginName = loginName;
		mSdkUser.password = Utils.md5Encode(password);
		Logger.d("----------password: " + password);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("loginName", "" + loginName);
		params.put("password", "" + password);
		params.put("projectId", "" + Utils.getProjectId(ctx));

		String url = Constants.LOGIN_REQ + appendUrl(params);
		InputStream in = doRequest(url, "");
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
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("projectId", Utils.getProjectId(ctx));
		params.put("imsi", Utils.getIMSI(ctx));
		String url = Constants.QUICK_LOGIN_REQ + appendUrl(params);
		try {
			InputStream in = doRequest(url, "");
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
	 * @return 注册
	 */
	public Result register(String loginName, String password, Context ctx) {
		mSdkUser = new SdkUser();
		mSdkUser.loginName = loginName;
		mSdkUser.password = Utils.md5Encode(password);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("loginName", loginName);
		params.put("password", password);
		params.put("projectId", Utils.getProjectId(ctx));
		params.put("imsi", Utils.getIMSI(ctx));

		String url = Constants.REG_REQ + appendUrl(params);
		InputStream in = doRequest(url, "");
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
		}
		return result;
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
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("newPassword", newPassword);
		String url = Constants.MODIFY_PWD + appendUrl(params);

		InputStream in = doRequest(url, "");
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
		SdkUserTable t = SdkUserTable.getInstance(mContext);
		// 将用户名保存到sdcard
		Utils.writeAccount2SDcard(mSdkUser.loginName, mSdkUser.password);
		return t.update(mSdkUser);
	}

	/**
	 * 请求c/s数据
	 * 
	 * @param params
	 *            url参数
	 * @param bytes
	 *            post内容
	 * @return 返回内容
	 */
	private InputStream doRequest(String url, String str) {
		HttpClient client = HttpUtil.getHttpClient(mContext);

		if (client == null) {
			return null;
		}

		HttpPost httpPost = new HttpPost(url);
		// if (str != null) {
		// HttpEntity entity = new ByteArrayEntity(Utils.encode(str)
		// .getBytes());
		// httpPost.setEntity(entity);
		// }

		HttpResponse response = null;
		int reconnectCount = 0;
		while (reconnectCount < 2) {
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
			}
			reconnectCount++;
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

	private String appendUrl(HashMap<String, String> params) {
		String url = "";
		// 添加url参数
		if (params != null) {
			Iterator<String> it = params.keySet().iterator();
			StringBuffer sb = null;
			while (it.hasNext()) {
				String key = it.next();
				String value = params.get(key);
				if (sb == null) {
					sb = new StringBuffer();
					sb.append("?");
				} else {
					sb.append("&");
				}
				sb.append(key);
				sb.append("=");
				sb.append(value);
			}
			url += sb.toString();
		}
		return url;
	}

	/**
	 * 清除缓存
	 */
	public void clearCache() {
		mUrlCache.clear();
	}

	/**
	 * 易宝支付
	 * 
	 * @param ctx
	 * @param req
	 * @return
	 * @throws IOException
	 */
	public List<String> URLGet(String req) {

		String path = "https://www.yeepay.com/app-merchant-proxy/command.action?"
				+ req;
		List<String> result = new ArrayList<String>();
		HttpClient httpClient = HttpUtil.getHttpClient(mContext);
		if (httpClient == null) {
			return null;
		}
		HttpGet httpGet = new HttpGet(path);
		HttpResponse response = null;

		try {
			response = httpClient.execute(httpGet);
			int status = response.getStatusLine().getStatusCode();
			Logger.d("status == " + status);
			if (status == HttpStatus.SC_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				// response.getEntity().getContent();
				while (true) {
					String line = in.readLine();
					if (line == null) {
						break;
					} else {
						result.add(line);
					}
				}
				in.close();
				return result;
			}
		} catch (ClientProtocolException e) {
			Logger.d(e.getMessage());
			return null;
		} catch (IOException e) {
			Logger.d(e.getMessage());
			return null;
		}
		return null;
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
	 * 平台登入 表示用户打开软件
	 * 
	 * @return
	 */
	public Result online(Context ctx) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("projectId", Utils.getProjectId(ctx));
		params.put("imsi", Utils.getIMSI(ctx));
		params.put("actionType", "" + Constants.ACTIONTYPE.INSTALL);
		String url = Constants.LOG_REQ + appendUrl(params);

		InputStream in = doRequest(url, "");
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
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("projectId", Utils.getProjectId(ctx));
		params.put("imsi", Utils.getIMSI(ctx));
		params.put("actionType", "" + Constants.ACTIONTYPE.INSTALL);
		String url = Constants.LOG_REQ + appendUrl(params);

		InputStream in = doRequest(url, "");

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
		String action = payParam.getUrl_PayAction(type);

		if (action == null) {
			Result result1 = new Result();
			result1.codes = "-1";
			// 无效支付方式
			return result1;
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("requestId", "");
		String url = /* appendUrl(params) */Constants.URL_SERVER_SRV + action;

		if (Application.loginName == null || !Application.isLogin) {
			Result result1 = new Result();
			result1.codes = "-1";
			return result1;
		}

		mSdkUser = new SdkUser();
		mSdkUser.loginName = Application.loginName;
		InputStream in = doRequest(url, "");
		if (in == null)
			return null;

		String json = parseJsonData(in);
		Logger.d("charge json -> " + json);
		if (json == null)
			return null;
		Result result = (Result) JsonUtil.parseJSonObject(Result.class, json);
		result.attach2 = json;
		return result;
	}

	/**
	 * 获取「短信」支付的通道表
	 * 
	 * @param payParam
	 */
	// public void getSMSChannelList(PayParam payParam) {
	// if (Application.loginName == null || !Application.isLogin) {
	// Result result1 = new Result();
	// result1.codes = "-1";
	// return /* result1 */;
	// }
	//
	// String action = payParam.getUrl_PayAction(PayChannel.PAY_TYPE_KKFUNPAY);
	// String url = Constants.URL_SERVER_SRV + action;
	//
	// mSdkUser = new SdkUser();
	// mSdkUser.loginName = Application.loginName;
	// InputStream in = doRequest(url, "");
	// if (in != null) {
	//
	// String json = parseJsonData(in);
	// Logger.d("charge json -> " + json);
	// if (json != null) {
	// Result result = (Result) JsonUtil.parseJSonObject(Result.class,
	// json);
	//
	// PayChannel[] channelMessages = (PayChannel[]) JsonUtil
	// .parseJSonArray(PayChannel.class, json);
	// }
	// }
	// }

	/**
	 * 获取支付列表
	 * 
	 * @return
	 */
	public PayChannel[] getPaymentList(PayParam charge) {

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("requestId", ""/* + RequestId.ID_PAYMENT_LIST */);
		params.put("serverId", charge.serverId);
		String url = Constants.GPL_REQ + appendUrl(params);

		// JSONObject jsonObject = getSessionAndDevicesPropertiesJson();
		// try {
		// jsonObject.put(charge.getShortName(), charge.buildJson());
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }

		InputStream in = doRequest(url, "");
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
			// try {
			// JSONObject json = new JSONObject(str);
			// Application.topicTitle = json.isNull("a") ? null : json
			// .getString("a").trim();
			// Application.topicDes = json.isNull("b") ? null : json
			// .getString("b").trim();
			// } catch (JSONException e) {
			// }
			Application.topicDes = str;
		}
	}

	public void getChannelMessage(DeviceProperties deviceProperties) {
		// TODO Auto-generated method stub
		// XXX: 暂时强制写入 projectID
		Utils.writeProjectId2cache(mContext, deviceProperties.projectId);
	}

}
