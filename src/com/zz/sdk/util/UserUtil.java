package com.zz.sdk.util;

import org.json.JSONObject;

import android.content.Context;
import android.util.Pair;

import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.ParamChain;
import com.zz.sdk.ParamChain.KeyUser;
import com.zz.sdk.ZZSDKConfig;
import com.zz.sdk.entity.Result;
import com.zz.sdk.entity.SdkUser;
import com.zz.sdk.entity.SdkUserTable;
import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultAutoLogin;
import com.zz.sdk.entity.result.ResultChangePwd;
import com.zz.sdk.entity.result.ResultLogin;
import com.zz.sdk.entity.result.ResultRegister;
import com.zz.sdk.out.util.Application;

/**
 * <b>注意：</b>这里储存的 <i>用户名、密码</i> 等，均为 ZZSDK 服务器上的数据。
 * <p>
 * 用户操作，如
 * <p>
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

	Context mContext;
	ConnectionUtil mConnectionUtil;
	private SdkUser mSdkUser;
	private com.zz.lib.pojo.Result mDouquRet;

	public UserUtil(Context ctx) {
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

	public boolean setCachedAutoLogin(boolean auto_login) {
		if (mSdkUser != null) {
			mSdkUser.autoLogin = auto_login ? 1 : 0;
			return true;
		}
		return false;
	}

	public boolean setCachedSdkUserId(int sdkUserId) {
		if (mSdkUser != null) {
			mSdkUser.sdkUserId = sdkUserId;
			return true;
		}
		return false;
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
		if (sdkUser == null) {
			SdkUser[] sdkUsers = t.getAllSdkUsers();
			if (sdkUsers != null && sdkUsers.length > 0) {
				if (support_douqu) {
					for (int i = 0; i < sdkUsers.length; i++) {
						if (PojoUtils.isCMGEUser(sdkUsers[i].loginName))
							continue;
						sdkUser = sdkUsers[i];
						break;
					}
				} else {
					sdkUser = sdkUsers[0];
				}
			}
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
	 * 同步用户基本信息到数据库中
	 * 
	 * @return
	 */
	private boolean syncSdkUser(ParamChain env, Context ctx, String loginName,
			String password) {
		// 更新用户数据到全局变量
		env.add(KeyUser.K_LOGIN_NAME, loginName);
		env.add(KeyUser.K_PASSWORD, password);

		SdkUserTable t = SdkUserTable.getInstance(ctx);

		// 将用户名保存到sdcard
		Utils.writeAccount2SDcard(ctx, loginName, password);

		if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
			if (PojoUtils.isDouquUser(loginName)) {
				// 不将逗趣的账户储存到sd卡
				PojoUtils.updateDouquUser_SDCard(
						PojoUtils.getDouquBaseName(loginName), password);
			}
		}
		SdkUser user = new SdkUser();
		user.autoLogin = 1;
		user.loginName = loginName;
		user.password = password;

		// return t.update(mSdkUser);
		return false;
	}

	public boolean syncSdkUser(SdkUser user) {
		SdkUserTable t = SdkUserTable.getInstance(mContext);
		return t.update(user);
	}

	/** 同步逗趣用户信息到 数据库 */
	public boolean syncSdkUser_douqu() {
		SdkUser user = mSdkUser;
		user.autoLogin = 1;
		user.loginName = PojoUtils.getDouquName(mDouquRet.account);
		return syncSdkUser(user);
	}

	/**
	 * 登录，如果成功，将自动更新到缓存，但不会保存到文件。需要主动调用 {@link #syncSdkUser()}
	 * 
	 * @param loginName
	 *            登录名
	 * @param passwd
	 *            密码
	 * @return
	 */
	public ResultLogin login(String loginName, String passwd) {
		ResultLogin ret = mConnectionUtil.login(loginName, passwd);
		if (ret.isSuccess()) {
			// 若登录成功，更新缓存
			SdkUser user = new SdkUser();
			user.loginName = ret.mUserName == null ? loginName : ret.mUserName;
			user.password = passwd;
			try {
				user.sdkUserId = Integer.parseInt(ret.mSdkUserId);
			} catch (NumberFormatException e) {
			}
			mSdkUser = user;
		}
		return ret;
	}

	/**
	 * 登录逗趣用户，如果成功，将自动更新到缓存，但不会保存到文件。需要主动调用 {@link #syncSdkUser()}
	 * 
	 * @param loginName
	 *            逗趣用户名
	 * @param passwd
	 *            密码
	 * @return
	 */
	public ResultLogin login_douqu(String loginName, String passwd) {
		ResultLogin ret;

		String newName = PojoUtils.getDouquBaseName(loginName);
		com.zz.lib.pojo.Result douquRet = PojoUtils.login(newName, passwd);
		if (douquRet == null || douquRet.status != 0) {
			Logger.d("D: login failed!" + douquRet);
			ret = new ResultLogin();
			if (douquRet != null) {
				// 普通失败
				ret.parseJson(new JSONObject());
				if (douquRet.status == -2) {
					// 有自己的错误描述
					ret.mErrDesc = douquRet.statusdescr;
				} else if (douquRet.status == -1) {
				}
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
				// 登录成功 填字段
				SdkUser user = new SdkUser();
				user.loginName = ret.mUserName == null ? user_name
						: ret.mUserName;
				user.password = pw;
				user.sdkUserId = douquRet.userid;
				mSdkUser = user;
			}
		}
		mDouquRet = douquRet;
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
			mSdkUser = new SdkUser();
			mSdkUser.autoLogin = 1;
			mSdkUser.loginName = ret.mUserName;
			mSdkUser.password = ret.mPassword;
			try {
				mSdkUser.sdkUserId = Integer.parseInt(ret.mSdkUserId);
			} catch (NumberFormatException e) {
			}
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
		ResultChangePwd ret = mConnectionUtil.modifyPassword(loginName,
				oldPasswd, newPasswd);
		if (ret.isSuccess()) {
			mSdkUser.password = newPasswd;
		}
		return ret;
	}

	/**
	 * 向服务器注册。如果成功，将自动更新到缓存，但不会保存到文件。需要主动调用 {@link #syncSdkUser()}
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
			mSdkUser = new SdkUser();
			mSdkUser.autoLogin = 1;
			mSdkUser.loginName = ret.mUserName == null ? loginName
					: ret.mUserName;
			mSdkUser.password = password;
			try {
				mSdkUser.sdkUserId = Integer.parseInt(ret.mSdkUserId);
			} catch (NumberFormatException e) {
			}
		}
		return ret;
	}

	public void clean() {
		// mContext = null;
		// ConnectionUtil.detachInstance(mConnectionUtil);
		// mConnectionUtil = null;
	}

	/** 更新登录缓存 */
	public void updateLogin(String loginName, String password, int userid,
			int autoLogin, Context ctx) {
		mSdkUser = new SdkUser();
		mSdkUser.sdkUserId = userid;
		mSdkUser.loginName = loginName;
		mSdkUser.autoLogin = autoLogin;
		mSdkUser.password = password;
		syncSdkUser();
	}

	public boolean syncSdkUser() {
		return syncSdkUser(mSdkUser);
	}

	/**
	 * 同步用户信息到数据库
	 * 
	 * @param auto_login
	 *            是否自动登录属性
	 * @return
	 */
	public boolean syncSdkUser(boolean auto_login) {
		mSdkUser.autoLogin = auto_login ? 1 : 0;
		return syncSdkUser(mSdkUser);
	}

	public void syncSdkSDCard() {

	}

	public void updateLogin_passwd(String new_passwd) {
		Application.password = new_passwd;
		mSdkUser.password = new_passwd;
		syncSdkUser();
	}

	/**
	 * 单机游戏的登录，有网络访问，必须在线程中调用。登录成功后将更新缓存
	 * 
	 * @param ctx
	 * @return 可根据返回值类型来判断登录模式：自动注册或登录
	 */
	public static BaseResult loginForLone(Context ctx) {
		BaseResult ret = null;
		final Pair<String, String> account = Utils.getAccountFromSDcard(ctx);
		final String loginName = account != null ? account.first : null;
		final String password = account != null ? account.second : null;

		boolean bNeedTry = true;
		UserUtil uu = new UserUtil(ctx);
		if (loginName != null && password != null) {
			ret = uu.login(loginName.trim(), password.trim());
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
			} else {
				if (DebugFlags.DEBUG) {
					Logger.d("自动注册失败！");
				}
				ret = null;
			}
		}

		if (!bNeedTry) {
			// 同步缓存
			uu.syncSdkUser(true);
			Utils.writeAccount2SDcard(ctx, uu.getCachedLoginName(),
					uu.getCachedPassword());
		}
		return ret;
	}

	static UserUtil sInstance;

	public static synchronized UserUtil getInstance(Context ctx) {
		if (sInstance == null) {
			sInstance = new UserUtil(ctx);
		}
		return sInstance;
	}
}
