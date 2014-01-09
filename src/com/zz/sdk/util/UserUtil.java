package com.zz.sdk.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.ParamChain;
import com.zz.sdk.ParamChain.KeyUser;
import com.zz.sdk.entity.SdkUser;
import com.zz.sdk.entity.SdkUserTable;
import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultAntiaddiction;
import com.zz.sdk.entity.result.ResultAutoLogin;
import com.zz.sdk.entity.result.ResultChangePwd;
import com.zz.sdk.entity.result.ResultLogin;
import com.zz.sdk.entity.result.ResultRegister;

/**
 * <b>注意：</b>这里储存的 <i>用户名、密码</i> 等，均为 ZZSDK 服务器上的数据。
 * <p>
 * 用户操作，如
 * <ul>
 * <li>登录</li>
 * <li>自动注册</li>
 * <li>注册</li>
 * <li>修改密码</li>
 * <li>自动登录（用于单机）</li>
 * </ul>
 * 及用户登录数据缓存，如
 * <ul>
 * <li>数据库
 * <li>SD Card
 * </ul>
 * 其它信息处理，如环境变量设置，则于外部调用者处理。
 */
public class UserUtil {

	private Context mContext;

	private ConnectionUtil mConnectionUtil;

	private String mDefName, mDefPassword;

	/** 用户信息的缓存：用于初始化读取或登录、注册等操作 */
	private SdkUser mSdkUser;
	/** SDK的 user-id，目前不是 {@link Integer}类型，所以这里用 String */
	private String mSdkUserId;
	/** 逗趣用户 ID */
	private int mDouquUserid;
	/**防沉迷状态： 0未知 1未成年 2成年 */
	private int mCMState;
	/** 主键ID */
	private String mSdkId;

	protected UserUtil(Context ctx) {
		mContext = ctx;
		mConnectionUtil = ConnectionUtil.getInstance(ctx);
	}

	public Context getContext() {
		return mContext;
	}

	/** 返回缓存中用户名 */
	public String getCachedLoginName() {
		return mSdkUser == null ? null : mSdkUser.loginName;
	}

	/** 返回缓存中的密码 */
	public String getCachedPassword() {
		return mSdkUser == null ? null : mSdkUser.password;
	}

	/** 返回缓存中的用户id */
	public String getCachedSdkUserId() {
		return mSdkUserId;
	}

	public String getCachedSdkId() {
		return mSdkId;
	}

	public int getCachedCMState() {
		if (mCMState == 1) return 1;
		if (mCMState == 2) return 2;
		return 0;
	}

	/** 返回缓存中的逗趣用户 id */
	public int getCachedDouquUserID() {
		return mDouquUserid;
	}

	private boolean checkLoginName(String name, boolean support_douqu) {
		if (name == null || name.length() == 0 || PojoUtils.isCMGEUser(name)
				|| (!support_douqu && PojoUtils.isDouquUser(name)))
			return false;
		return true;
	}

	/**
	 * 初始化。读取数据库及SD卡的用户登录记录，主要用于自动登录。
	 * <p>
	 * 会改变 {@link #mSdkUser} 的值
	 * 
	 * @param support_douqu
	 *            是否支付逗趣用户登录
	 */
	public void init(boolean support_douqu) {
		SdkUserTable t = SdkUserTable.getInstance(mContext);
		SdkUser sdkUser = t.getSdkUserByAutoLogin();
		if (sdkUser == null
				|| !checkLoginName(sdkUser.loginName, support_douqu)) {
			sdkUser = null;
			SdkUser[] sdkUsers = t.getAllSdkUsers();
			if (sdkUsers != null && sdkUsers.length > 0) {
				if (support_douqu) {
					for (int i = 0; i < sdkUsers.length; i++) {
						if (checkLoginName(sdkUsers[i].loginName, support_douqu)) {
							sdkUser = sdkUsers[i];
							break;
						}
					}
				} else {
					sdkUser = sdkUsers[0];
				}
			}
		} else {
			mDefName = sdkUser.loginName;
			mDefPassword = sdkUser.password;
		}

		mSdkUser = sdkUser;

		if (mSdkUser == null || mSdkUser.loginName == null
				|| "".equals(mSdkUser.loginName)) {
			Pair<String, String> pair = null;

			if (support_douqu) {
				/*
				 * 有：　1,cmge数据库 2,cmge的SD卡 3.zz数据库 4.zz的SD卡 这４个用户信息储存点 ３→１→４→２
				 */
				pair = PojoUtils.checkDouquUser_DB(mContext);
			}

			// 尝试从sdcard中读取
			if (pair == null)
				pair = Utils.getAccountFromSDcard(mContext);

			if (support_douqu) {
				if (pair != null && PojoUtils.isCMGEUser(pair.first)) {
					pair = null;
				}
				if (pair == null)
					pair = PojoUtils.checkDouquUser_SDCard();
			}

			if (pair != null) {
				mSdkUser = new SdkUser();
				mSdkUser.loginName = pair.first;
				mSdkUser.password = pair.second;
			}
		}
	}

	/**
	 * 同步用户信息到数据及SD卡
	 * 
	 * @param user
	 * @return
	 */
	public boolean syncSdkUser(SdkUser user) {
		if (mDefName != null && mDefPassword != null && mDefName.equals(user.loginName) &&
				mDefPassword.equals(user.password)) {
			// 如果是数据库中的默认用户，就不必更新保存了
			return false;
		}

		SdkUserTable t = SdkUserTable.getInstance(mContext);

		if (PojoUtils.isDouquUser(user.loginName)) {
			PojoUtils.updateDouquUser_SDCard(
					PojoUtils.getDouquBaseName(user.loginName), user.password);
		} else if (PojoUtils.isCMGEUser(user.loginName)) {
			// 不将逗趣的账户储存到sd卡
		} else {
			// 将用户名保存到sdcard
			Utils.writeAccount2SDcard(mContext, user.loginName, user.password);
		}

		// 同步设备信息到服务器，目前定义的时机为第一次登录成功
		DeviceUtil.checkAndSync(mContext, user.loginName);

		return t.update(user);
	}

	/**
	 * 同步用户信息到数据库
	 * 
	 * @param auto_login
	 *            是否自动登录属性，如果是 null，表示使用缓存中的设置
	 * @return 同步是否成功
	 */
	public boolean syncSdkUser(Boolean auto_login) {
		SdkUser user = new SdkUser();
		user.loginName = mSdkUser.loginName;
		user.password = mSdkUser.password;
		user.sdkUserId = mSdkUser.sdkUserId;
		if (auto_login == null)
			user.autoLogin = mSdkUser.autoLogin;
		else if (auto_login)
			user.autoLogin = 1;
		else
			user.autoLogin = 0;
		return syncSdkUser(user);
	}

	/**
	 * 同步逗趣用户信息到 数据库
	 * 
	 * @param loginName
	 *            用户名，见 {@link PojoUtils#getDouquName(String)}
	 * @param password
	 *            密码
	 * @param userId
	 *            逗趣的用户ID
	 * @return 是否成功
	 */
	public boolean syncSdkUser_douqu(String loginName, String password,
			int userId) {
		SdkUser user = new SdkUser();
		user.autoLogin = 1;
		user.loginName = loginName;
		user.password = password;
		user.sdkUserId = userId;
		return syncSdkUser(user);
	}

	/**
	 * 登录，如果成功，将自动更新卓越账户信息到缓存，但不会保存到文件。
	 * <p>
	 * 需要主动调用 {@link #syncSdkUser()}
	 * 
	 * @param loginName
	 *            普通用户或逗趣用户名
	 * @param passwd
	 *            密码
	 * @return
	 * @see #mSdkUser
	 * @see #mDouquUserid
	 * @see #syncSdkUser(SdkUser)
	 * @see #syncSdkUser_douqu(String, String, int)
	 */
	public ResultLogin login(String loginName, String passwd) {
		ResultLogin ret;

		if (PojoUtils.isDouquUser(loginName)) {
			String baseName = PojoUtils.getDouquBaseName(loginName);
			com.zz.lib.pojo.Result douquRet = PojoUtils.login(baseName, passwd);
			if (douquRet == null || douquRet.status != 0) {
				Logger.d("D: login failed!" + douquRet);
				ret = new ResultLogin();
				if (douquRet == null) {
					// 这是网络连接失败
				} else {
					// 普通失败
					ret.parseJson(new JSONObject());
					// 有自己的错误描述
					ret.mErrDesc = douquRet.statusdescr;
				}
			} else {
				// 登录逗趣成功，尝试登录或注册卓越
				Pair<String, String> cmgeUer = PojoUtils.genCmgeUser(douquRet,
						loginName, passwd);
				String user_name = cmgeUer.first;
				String pw = cmgeUer.second;
				// step1. 尝试登录
				ret = mConnectionUtil.login(user_name, pw);
				if (!ret.isSuccess()) {
					int code = ret.getCodeNumber();
					if (code == 1) {
						// 用户不存在，那么，注册!
						ret = mConnectionUtil.register(user_name, pw);
						if (!ret.isSuccess()) {
							// 又失败了
							code = ret.getCodeNumber();
							ret.mErrDesc = "登录错误2-(" + (code < 0 ? -1 : code)
									+ ")，请联系客服！";
						}
					} else if (code == 2) {
						// 密码错误
						ret.mErrDesc = "密码错误(2)，请联系客服！";
					} else {
						ret.mErrDesc = "登录错误1-(" + (code < 0 ? -1 : code)
								+ ")，请联系客服！";
					}
				}
				if (ret.isSuccess()) {
					// 登录成功 填字段，这里先保留的是卓越账户信息，当同步到数据库时，再选择是否使用逗趣信息
					SdkUser user = result2user(ret, user_name);
					user.password = pw;
					// 临时类账户，不开启自动登录
					user.autoLogin = 0;
					mSdkUser = user;
					mDouquUserid = douquRet.userid;
				}
			}
		} else {
			ret = mConnectionUtil.login(loginName, passwd);
			if (ret != null && ret.isSuccess()) {
				// 若登录成功，更新缓存
				SdkUser user = result2user(ret, loginName);
				user.password = passwd;
				user.autoLogin = 1;
				mSdkUser = user;
			}
		}

		return ret;
	}


	public ResultAntiaddiction anti_addiction(String loginName, String password, int state) {
		ResultAntiaddiction ret;
		ret = mConnectionUtil.anti_addiction(loginName, password, state);
		if (ret != null && ret.isSuccess()) {
			// 若登录成功，更新缓存
			SdkUser user = result2user(ret, loginName);
			user.password = password;
			user.autoLogin = 1;
			mSdkUser = user;
		}
		return ret;
	}

	/**
	 * 快速登录，即自动注册。如果成功，将自动更新到缓存，但不会保存到文件。需要主动调用 {@link #syncSdkUser()}
	 * 
	 * @return
	 */
	public ResultAutoLogin quickLogin() {
		ResultAutoLogin ret = mConnectionUtil.quickLogin();
		if (ret.isSuccess()) {
			SdkUser user = result2user(ret, null);
			user.autoLogin = 1;
			user.password = ret.mPassword;
			mSdkUser = user;
		}
		return ret;
	}

	/**
	 * 修改密码。如果成功，将自动更新到缓存，但不会保存到文件。需要主动调用 {@link #syncSdkUser()}
	 * 
	 * @return
	 */
	public ResultChangePwd modifyPassword(String loginName, String oldPasswd,
			String newPasswd) {
		ResultChangePwd ret;
		if (PojoUtils.isDouquUser(loginName)) {
			ret = new ResultChangePwd();
			String baseName = PojoUtils.getDouquBaseName(loginName);
			com.zz.lib.pojo.Result err = PojoUtils.updatePasswd(baseName,
					oldPasswd, newPasswd);
			if (err == null) {
				// 连接失败
			} else if (err.status == 0) {
				// 成功
				try {
					JSONObject jo = new JSONObject("{codes:[0]}");
					ret.parseJson(jo);
				} catch (JSONException e) {
				}
				ret.mErrDesc = err.statusdescr;
			} else {
				// 失败
				try {
					JSONObject jo = new JSONObject("{codes:[1]}");
					ret.parseJson(jo);
				} catch (JSONException e) {
				}
				ret.mErrDesc = err.statusdescr;
			}
		} else if (PojoUtils.isCMGEUser(loginName)) {
			// 这类用户名不允许修改密码
			Logger.e("E: can not modify " + loginName);
			ret = new ResultChangePwd();
			try {
				JSONObject jo = new JSONObject("{codes:[1]}");
				ret.parseJson(jo);
			} catch (JSONException e) {
			}
		} else {
			// 常规密码修改
			ret = mConnectionUtil.modifyPassword(loginName, oldPasswd,
					newPasswd);
			if (ret.isSuccess()) {
				mSdkUser.password = newPasswd;
			}
		}
		return ret;
	}

	/**
	 * 向服务器注册。
	 * <p>
	 * 如果成功，将自动更新到缓存，但不会保存到文件。需要主动调用 {@link #syncSdkUser()}
	 * 
	 * @param loginName
	 *            登录名
	 * @param password
	 *            密码
	 * @return
	 */
	public ResultRegister register(String loginName, String password) {
		ResultRegister ret = mConnectionUtil.register(loginName, password);
		if (ret.isSuccess()) {
			SdkUser user = result2user(ret, loginName);
			user.autoLogin = 1;
			user.password = password;
			mSdkUser = user;
		}
		return ret;
	}

	private SdkUser result2user(ResultLogin ret, String defName) {
		SdkUser user = new SdkUser();
		user.loginName = ret.mUserName == null ? defName : ret.mUserName;
		try {
			user.sdkUserId = Integer.parseInt(/*ret.mSdkUserId*/ret.mId);
		} catch (NumberFormatException e) {
		}
		mSdkId = ret.mId;
		mSdkUserId = ret.mSdkUserId;
		mCMState = ret.mCmStatus;
		return user;
	}

	public void clean() {
		// mContext = null;
		// ConnectionUtil.detachInstance(mConnectionUtil);
		// mConnectionUtil = null;
	}

	/**
	 * 单机游戏的登录，有网络访问，必须在线程中调用。登录成功后将更新缓存。
	 * <ul>
	 * 流程
	 * <li>读取记录
	 * <li>尝试登录
	 * <li>如果登录失败，尝试注册
	 * </ul>
	 * 
	 * @param ctx
	 * @param support_douqu
	 *            是否支持逗趣
	 * @return 可根据返回值类型来判断登录模式：自动注册或登录
	 */
	public static BaseResult loginForLone(ParamChain env, Context ctx,
			boolean support_douqu) {
		BaseResult ret = null;
		boolean bNeedTry = true;
		UserUtil uu = UserUtil.getInstance(ctx);
		uu.init(support_douqu);
		String loginName = uu.getCachedLoginName();
		String password = uu.getCachedPassword();
		if (loginName != null && password != null) {
			ret = uu.login(loginName, password);
			if (ret.isSuccess()) {
				bNeedTry = false;
			} else {
				if (DebugFlags.DEBUG) {
					Logger.d("自动登录失败！尝试自动注册");
				}
			}
		}

		if (bNeedTry) {
			ret = uu.quickLogin();
			if (ret.isSuccess()) {
				bNeedTry = false;
				loginName = uu.getCachedLoginName();
				password = uu.getCachedPassword();
			} else {
				if (DebugFlags.DEBUG) {
					Logger.d("自动注册失败！");
				}
				ret = null;
			}
		}

		if (!bNeedTry) {
			if (DebugFlags.DEBUG) {
				Logger.d("D: login success! name=" + loginName + " password="
						+ password);
			}
			if (PojoUtils.isDouquUser(loginName)) {
				uu.syncSdkUser_douqu(loginName, password,
						uu.getCachedDouquUserID());
				env.add(KeyUser.K_LOGIN_NAME_GAME_USER,
						String.valueOf(uu.getCachedDouquUserID()));
				loginName = uu.getCachedLoginName();
				password = uu.getCachedPassword();
			} else {
				uu.syncSdkUser(true);
				env.remove(KeyUser.K_LOGIN_NAME_GAME_USER);
			}
			env.add(KeyUser.K_LOGIN_NAME, loginName);
			env.add(KeyUser.K_PASSWORD, password);
			env.add(KeyUser.K_SDKUSER_ID, uu.getCachedSdkUserId());
			env.add(KeyUser.K_LOGIN_STATE_SUCCESS, Boolean.TRUE);

			SocialUtil su = SocialUtil.getInstance();
			if (su != null) {
				su.onLoginResult(uu.getCachedSdkId());
			}
		} else {
			if (DebugFlags.DEBUG) {
				Logger.d("E: login failed!");
			}
			env.remove(KeyUser.K_LOGIN_NAME);
			env.remove(KeyUser.K_PASSWORD);
			env.remove(KeyUser.K_SDKUSER_ID);
			env.remove(KeyUser.K_LOGIN_STATE_SUCCESS);
		}
		return ret;
	}

	private static UserUtil sInstance;

	public static synchronized UserUtil getInstance(Context ctx) {
		if (sInstance == null) {
			sInstance = new UserUtil(ctx);
		}
		return sInstance;
	}
}
