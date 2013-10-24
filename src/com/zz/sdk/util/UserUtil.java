package com.zz.sdk.util;

import android.content.Context;

import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.ZZSDKConfig;
import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyUser;
import com.zz.sdk.entity.SdkUser;
import com.zz.sdk.entity.SdkUserTable;

/**
 * 用户操作，如
 * <p>
 * <ul>
 * <li>登录</li>
 * <li>自动注册</li>
 * <li>注册</li>
 * <li>修改密码</li>
 * <li>缓存用户信息到文件</li>
 * <li>从文件中加载用户信息</li>
 * <li>……其它</li>
 * </ul>
 * 
 */
public class UserUtil {

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

	/** 登录 */
	public void login(String loginName, String passwd) {

	}

	/** 快速登录，即自动注册 */
	public void quickLogin() {

	}

	/** 修改密码 */
	public void modifyPassword(String loginName, String oldPasswd,
			String newPasswd) {

	}

}
