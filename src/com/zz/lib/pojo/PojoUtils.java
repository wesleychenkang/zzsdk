package com.zz.lib.pojo;

import java.net.HttpURLConnection;
import java.util.regex.Matcher;
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

	private final static String HOST = "http://user.cmge.com/interfaceAction";
	private final static String HOST_1 = "http://58.68.150.154:9191/user/interfaceAction";
	// private final static String HOST_1 =
	// "http://user.cmge.com/interfaceAction";

	/** 豆趣的URL http://user.cmge.com?t=请求类型&a=加密版本&b=压缩 */
	private final static String URL = HOST + "?requestId=%d&a=%d&b=%d";
	private final static String URL_1 = HOST_1 + "?requestId=%d&a=%d&b=%d";

	/** CMGE 通行证 */
	private static final String SIGN = ".cmge";
	/** 转换卓越账户的规则 ( id+{@value #SIGN} ) */
	private static final String SIGN_ID_PATTERN = "\\d+\\.cmge$";

	/** 逗趣用户 */
	private static final String SIGN_DOUQU = "\ndouqu";
	private static final String SIGN_NAME_DOUQU_PATTERN = ".*\ndouqu$";// "[a-z][a-z0-9]{5,19}\\.cmge$";

	/** 压缩 */
	private static final int CONFIG_COMPRESS = 1;
	/** 加密 */
	private static final int CONFIG_ENCRYPT = 4;

	private static int connectCount = 1;

	private static final int CONFIG_TIMEOUT = 1000 * 20;

	/** 逗趣到卓越账号的默认密码 */
	private static final String DEF_DOUQU_PASSWD = "douqu0OP";

	//
	// ////////////////////////////////////////////////////////////////////////
	//
	// - 操作记录
	//

	/** 未初始化 */
	public static final int CODE_UNSET = -100;
	/** 成功 */
	public static final int CODE_SUCCESS = 0;
	/** 失败 */
	public static final int CODE_FAILED = -1;
	/** 其它错误 */
	public static final int CODE_FAILED_OTHER = -2;
	/** 卓越服务器连接错误 */
	public static final int CODE_FAILED_ZUOYUE = -3;
	private static int sLastCode;
	private static int sLoginUserID;

	private static void reset_last_code() {
		set_last_code(CODE_UNSET);
	}

	private static void set_last_code(int code) {
		sLastCode = code;
	}

	/**
	 * 
	 * @return 0成功；-1失败；-2可显示结果描述 -3向卓越注册或登录失败
	 * @see #CODE_SUCCESS
	 * @see #CODE_FAILED
	 * @see #CODE_FAILED_OTHER
	 * @see #CODE_FAILED_ZUOYUE
	 * @see #CODE_UNSET
	 */
	public static int get_last_code() {
		return sLastCode;
	}

	private static void set_login_user_id(int id) {
		sLoginUserID = id;
	}

	/***
	 * @return 获取登录的用户ID
	 */
	public static int get_login_user_id() {
		return sLoginUserID;
	}

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
		Pair<String, String> p = com.zz.sdk.util.Utils.getAccountFromDB(ctx,
				name);
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
		if (DEF_DOUQU_PASSWD != null) {
			passwd = DEF_DOUQU_PASSWD;
		}
		String loginName = result.userid + SIGN;
		if (checkLoginNameExist(ctx, loginName, passwd)) {
			// 向服务器注册， codes=0成功|1失败|2用户名已经存在
			com.zz.sdk.entity.Result r = GetDataImpl.getInstance(ctx).register(
					loginName, passwd, ctx);
			if (r != null) {
				// 已经存在(2)或注册成功(0)
				if ("2".equals(r.codes) || "0".equals(r.codes)) {
					com.zz.sdk.util.Utils.writeAccount2DB(ctx, loginName,
							passwd, get_login_user_id(), 0);
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

	/** 获取　数据库　中的 CMGE 用户 */
	public static Pair<String, String> checkDouquUser_DB(Context ctx) {
		TSession ts = TSession.getInstance(ctx);
		Session s = ts.getSessionByAutoLogin();
		if (s == null) {
			Session[] ss = ts.getAllSessions();
			if (ss != null && ss.length != 0) {
				s = ss[0];
			}
		}
		if (s != null) {
			// int userid = s.sessionId;
			return new Pair<String, String>(getDouquName(s.userName),
					s.password);
		}
		return null;
	}

	/** 从 SD 卡中获取 CMGE 用户信息 */
	public static Pair<String, String> checkDouquUser_SDCard() {
		Pair<String, String> user = Utils.getAccountFromSDcard();
		if (user != null) {
			return new Pair<String, String>(getDouquName(user.first),
					user.second);
		}
		return null;
	}

	public static void updateDouquUser_SDCard(String user, String pw) {
		Utils.writeAccount2SDcard(user, pw);
	}

	//
	// ////////////////////////////////////////////////////////////////////////
	//
	// - http 操作
	//

	/** 获取 url */
	private static String getUrl(int type, int encrypt, int compress) {
		return String.format(URL, type, encrypt, compress);
	}

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
			String url = getUrl(data.getShortType(), CONFIG_ENCRYPT,
					CONFIG_COMPRESS);

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

	//
	// ////////////////////////////////////////////////////////////////////////
	//
	// - 账号转换接口
	//

	/** 数字通行证账户规则，{@value #SIGN_ID_PATTERN} */
	private final static Pattern DEF_PATTERN_CMGE_ID = Pattern
			.compile(SIGN_ID_PATTERN);

	/**
	 * @param name
	 *            用户名
	 * @return 判断　目标账户　是否符合卓越数字通行证账号的规则：{@value #SIGN_ID_PATTERN}
	 */
	public static boolean isCMGEUser(String name) {
		if (name != null) {
			Matcher m = DEF_PATTERN_CMGE_ID.matcher(name);
			if (m != null && m.matches()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param nor_name
	 *            普通用户名
	 * @return 将普通用户名拼接成代表 逗趣 用户名的格式， "NAME"+{@value #SIGN}
	 */
	public static String getCMGEName(String nor_name) {
		return nor_name + SIGN;
	}

	/**
	 * @param account
	 *            用户名
	 * @return 截掉尾巴（{@value #SIGN}）后的用户名
	 */
	public static String getCMGEBaseName(String account) {
		if (account != null && account.endsWith(SIGN)) {
			/* isCMGEUser(loginName) */
			return account.substring(0, account.length() - SIGN.length());
		}
		return account;
	}

	/** 逗趣账户规则，{@value #SIGN_NAME_DOUQU_PATTERN} */
	private final static Pattern DEF_PATTERN_DOUQU_NAME = Pattern
			.compile(SIGN_NAME_DOUQU_PATTERN);

	/**
	 * 判断　目标账户　是否符合豆趣账号的规则：{@value #SIGN_NAME_DOUQU_PATTERN}
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isDouquUser(String name) {
		if (name != null) {
			Matcher m = DEF_PATTERN_DOUQU_NAME.matcher(name);
			if (m != null && m.matches()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param nor_name
	 *            普通用户名
	 * @return 将普通用户名拼接成代表 逗趣 用户名的格式， "NAME"+{@value #SIGN_DOUQU}
	 */
	public static String getDouquName(String nor_name) {
		return nor_name + SIGN_DOUQU;
	}

	/**
	 * @param loginName
	 *            用户名
	 * @return 截掉尾巴（{@value #SIGN_DOUQU}）后的用户名
	 */
	public static String getDouquBaseName(String loginName) {
		if (loginName != null && loginName.endsWith(SIGN_DOUQU)) {
			/* isDouquUser(loginName) */
			return loginName.substring(0,
					loginName.length() - SIGN_DOUQU.length());
		}
		return loginName;
	}

	/**
	 * @param str
	 * @return true表示包含有中文
	 */
	private static boolean getChinese(String str) {
		boolean HasChinese = false;
		if (str == null || "".equals(str.trim())) {
			return false;
		}
		char[] pwd = str.toCharArray();
		for (int i = 0; i < pwd.length; i++) {
			char c = pwd[i];
			if (Pattern.matches("[\u4e00-\u9fa5]", String.valueOf(c))) {
				HasChinese = true;
				break;
			}
		}
		return HasChinese;
	}

	/**
	 * @param passwd
	 *            待检测密码
	 * @return 是否符合 逗趣的密码要求，若为 null 表示符合，否则为描述文本
	 */
	public static String isDouquPasswd(String passwd) {
		if (passwd != null && passwd.length() < 6) {
			return "密码不能少于6位";
		} else if (passwd.length() > 20) {
			return "密码的长度太长超过20位";
		} else if (getChinese(passwd)) {
			return "密码不能包含中文";
			// } else if (!passwd.matches("^(?!_)(?!.*?_$)[a-zA-Z0-9]+$")) {
			// return "密码中只能包含数字和字母";
		}
		return null;
	}

	//
	// ////////////////////////////////////////////////////////////////////////
	//
	// - 对外接口
	//

	private static void _test(Context ctx, String name, String passwd) {
		if (BuildConfig.DEBUG) {
			String s = null;
			boolean success = false;
			boolean run = false;
			if (run) {
				s = registe(ctx, name, passwd);
			}
			if (run) {
				success = updatePasswd(ctx, name, passwd, passwd);
			}
			if (run) {
				s = login(ctx, name, passwd);
			}
		}
	}

	/**
	 * 登录
	 * 
	 * @param name
	 * @param passwd
	 * @return 返回可使用的用户名
	 */
	public static String login(Context ctx, String name, String passwd) {
		reset_last_code();

		Login login = new Login();
		login.account = name;
		login.md5pwd = MD5Util.md5Encode(passwd);
		login.atype = Login.TYPE_UNKNOWN;
		login.dtype = Login.DTYPE_ALL;
		login.lastIP = null;
		login.app_secret = app_secret;
		login.updateSign(app_key);

		if (BuildConfig.DEBUG) {
			_test(ctx, name, passwd);
		}

		String r = douquPost(login);
		Result result = parseResult(r);

		// 检查是否成功
		if (result != null
				&& result.checkSign(result.account, result.time, app_key)) {
			set_last_code(result.status);
			set_login_user_id(result.userid);
			if (result.status == 0) {
				// 登录成功，自动注册
				String zyName = auto_registe(ctx, result, name, passwd);
				if (zyName == null) {
					set_last_code(CODE_FAILED_ZUOYUE);
				}
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
	public static boolean updatePasswd(Context ctx, String name,
			String oldPasswd, String newPasswd) {
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
