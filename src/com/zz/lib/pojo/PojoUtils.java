package com.zz.lib.pojo;

import java.net.HttpURLConnection;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Pair;

import com.zz.lib.utils.Encrypt1;
import com.zz.lib.utils.MD5Util;
import com.zz.sdk.BuildConfig;
import com.zz.sdk.util.GetDataImpl;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.Utils;

/**
 * 封装豆趣接口
 * <p>
 * 帐号说明：
 * <ul>
 * <li>20位 字母、数字</li>
 * <li>不区分大小写， 登录无限制</li>
 * <li>密码统一按协议，该用md5的就要md5哦 md5.md5Encode</li>
 * </ul>
 * 
 */
public class PojoUtils {
	private final static String app_secret = "cmge_zy";
	private final static String app_key = "cddc0e6375017d2b258e07af1fe72f01";

	private final static String HOST = "http://user.cmge.com";
	private final static String HOST_1 = "http://58.68.150.154:9191/user/interfaceAction";
	// private final static String HOST_1 =
	// "http://user.cmge.com/interfaceAction";

	/** 豆趣的URL http://user.cmge.com?t=请求类型&a=加密版本&b=压缩 */
	private final static String URL = HOST + "?t=%d&a=%d&b=%d";
	private final static String URL_1 = HOST_1 + "?requestId=%d&a=%d&b=%d";

	private static final String SIGN = "$DQU$";
	/** 压缩 */
	private static final int CONFIG_COMPRESS = 1;
	/** 加密 */
	private static final int CONFIG_ENCRYPT = 4;

	private static int connectCount = 1;

	private static final int CONFIG_TIMEOUT = 1000 * 20;

	//
	// ////////////////////////////////////////////////////////////////////////
	//
	// - 用户操作
	//

	/**
	 * 判断当前用户是否已写入SD卡
	 * 
	 * @param name
	 *            待比对的用户名
	 * @return true表示与 name 不匹配，需要向服务器重新注册
	 */
	private static boolean checkLoginNameExist(Context ctx, String name,
			String passwd) {
		Pair<String, String> p = Utils.getAccountFromSDcard(ctx);
		if (p == null) {
			return true;
		} else {
			if (p.first == null || !p.first.equals(name)
					|| !passwd.equals(p.second)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 自动注册或登录，用于登录　豆趣　成功后。
	 * 
	 * @param ctx
	 * @param result
	 * @param account
	 * @param passwd
	 * @return 返回用户名，若操作失败，将返回 null
	 */
	private static String auto_registe(Context ctx, Result result,
			String account, String passwd) {
		String loginName = result.userid + SIGN;
		if (checkLoginNameExist(ctx, loginName, passwd)) {
			// 向服务器注册， codes=0成功|1失败|2用户名已经存在
			com.zz.sdk.entity.Result r = GetDataImpl.getInstance(ctx).register(
					loginName, passwd, ctx);
			if (r != null) {
				// 已经存在(2)或注册成功(0)
				if ("2".equals(r.codes) || "0".equals(r.codes)) {
					Utils.writeAccount2SDcard(ctx, loginName, passwd);
				} else {
					loginName = null;
				}
			} else {
				// 未知结果
				loginName = null;
			}
			Logger.d("执行了注册回调");
		} else {
			com.zz.sdk.entity.Result r = GetDataImpl.getInstance(ctx).login(
					loginName, passwd, 0, ctx);
			Logger.d("login [" + loginName + " result:" + r);
			if (r != null) {
				// codes=0成功|1用户不存在|2密码错误
				if ("0".equals(r.codes)) {
				} else {
					loginName = null;
				}
			} else {
				loginName = null;
			}
			Logger.d("执行了登录回调");
		}
		return loginName;
	}

	//
	// ////////////////////////////////////////////////////////////////////////
	//
	// - http 操作
	//

	/**
	 * 调用豆趣的 http 请求
	 * 
	 * @param data
	 * @return JSON字串，如果失败则返回null
	 */
	private static String douquPost(JsonParseInterface data) {
		JSONObject json = new JSONObject();
		BaseData baseData = new BaseData();
		baseData.version = "1.0.0";
		try {
			json.put(baseData.getShortName(), baseData.buildJson());
			json.put(data.getShortName(), data.buildJson());

			String requestStr = json.toString();
			String url = String.format(URL_1, data.getShortType(),
					CONFIG_ENCRYPT, CONFIG_COMPRESS);

			String content = Encrypt1.encode(requestStr,
					String.valueOf(CONFIG_ENCRYPT));
			java.net.URL u = new java.net.URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(CONFIG_TIMEOUT);
			conn.connect();

			// int responseCode = conn.getResponseCode();
			// if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功

			Encrypt1.compress(conn.getOutputStream(), content,
					String.valueOf(CONFIG_COMPRESS));

			String responseStr = Encrypt1.decompress(conn.getInputStream(),
					String.valueOf(CONFIG_COMPRESS));
			responseStr = Encrypt1.decode(responseStr,
					String.valueOf(CONFIG_ENCRYPT));
			conn.disconnect();
			return responseStr;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从输入流中解析　豆趣　的返回结果 ({@link Result})
	 * 
	 * @param jsonStr
	 * @return
	 */
	private static Result parseResult(String jsonStr) {
		if (jsonStr != null) {
			try {
				JSONObject jsonObject = new JSONObject(jsonStr);
				Result result = new Result();
				if (jsonObject.has(result.getShortName())) {
					result.parseJson(jsonObject.getJSONObject(result
							.getShortName()));
					return result;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	// - 对外接口
	//

	private final static Pattern DEF_QOUQU_PATTERN = Pattern.compile("");

	/**
	 * 检查　用户名是否是豆趣类型 ( id+"$DQU$" )
	 * 
	 * @param loginName
	 * @return
	 */
	public static String getGameName(String loginName) {
		// if (loginName!=null && )
		return loginName;
	}

	/**
	 * 登录
	 * 
	 * @param name
	 * @param passwd
	 * @return 返回可使用的用户名
	 */
	public static String login(Context ctx, String name, String passwd) {
		Login login = new Login();
		login.account = name;
		login.md5pwd = MD5Util.md5Encode(passwd);
		login.atype = Login.TYPE_UNKNOWN;
		login.dtype = Login.DTYPE_ALL;
		login.lastIP = null;
		login.app_secret = app_secret;
		login.updateSign(app_key);

		if (BuildConfig.DEBUG) {
			int b = 1;
			if (b == 0) {
				String new_n = registe(ctx, name, passwd);
			}
		}

		String r = douquPost(login);
		Result result = parseResult(r);

		// 检查是否成功
		if (result != null
				&& result.checkSign(result.account, result.time, app_key)) {
			if (result.status == 0) {
				// 登录成功，自动注册
				String zyName = auto_registe(ctx, result, name, passwd);
				return zyName;
			}
		}
		return null;
	}

	/**
	 * 注册账号
	 * 
	 * @param ctx
	 * @param name
	 * @param passwd
	 * @return
	 */
	static String registe(Context ctx, String name, String passwd) {
		Register register = new Register();
		register.account = name;
		register.md5pwd = MD5Util.md5Encode(passwd);
		register.bindMobile = null;
		register.bindEmail = null;
		register.type = 5;
		register.app_secret = app_secret;
		register.updateSign(app_key);

		String r = douquPost(register);
		Result result = parseResult(r);
		if (result != null
				&& result.checkSign(register.account,
						String.valueOf(result.userid), app_key)) {
			return register.account;
		}
		return null;
	}

	/**
	 * 修改用户密码
	 * 
	 * @param ctx
	 * @param name
	 * @param oldPasswd
	 * @param newPasswd
	 * @return
	 */
	static boolean updatePasswd(Context ctx, String name, String oldPasswd,
			String newPasswd) {
		UpdatePwd updatePwd = new UpdatePwd();
		updatePwd.account = name;
		updatePwd.md5pwd = MD5Util.md5Encode(oldPasswd);
		updatePwd.newmd5pwd = MD5Util.md5Encode(newPasswd);
		updatePwd.app_secret = app_secret;
		updatePwd.updateSign(app_key);

		String r = douquPost(updatePwd);
		Result result = parseResult(r);
		if (result != null
				&& result.checkSign(updatePwd.account, updatePwd.newmd5pwd,
						updatePwd.app_secret, app_key)) {
			if (result.status == 0) {
				return true;
			}
		}
		return false;
	}
}
