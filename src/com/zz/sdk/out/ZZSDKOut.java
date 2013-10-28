package com.zz.sdk.out;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;

import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.LoginCallbackInfo;
import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.SDKManager;
import com.zz.sdk.ZZSDKConfig;
import com.zz.sdk.entity.SdkUser;
import com.zz.sdk.entity.SdkUserTable;
import com.zz.sdk.out.activity.ChargeActivity;
import com.zz.sdk.out.activity.LoginActivity;
import com.zz.sdk.out.activity.LoginForQiFu;
import com.zz.sdk.out.util.Application;
import com.zz.sdk.util.Utils;

public class ZZSDKOut {

	private static void autoLoginUser(Context ctx) {
		SdkUserTable t = SdkUserTable.getInstance(ctx);
		SdkUser sdkUser = t.getSdkUserByAutoLogin();
		if (sdkUser == null) {
			SdkUser[] sdkUsers = t.getAllSdkUsers();
			if (sdkUsers != null && sdkUsers.length > 0) {
				if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
					for (int i = 0; i < sdkUsers.length; i++) {
						if (PojoUtils.isCMGEUser(sdkUsers[i].loginName))
							continue;
						sdkUser = sdkUsers[i];
						break;
					}
				} else
					sdkUser = sdkUsers[0];
			}
		}
		if (sdkUser != null) {
			Application.setLoginName(sdkUser.loginName);
			Application.password = sdkUser.password;
		}
		if (Application.loginName == null || "".equals(Application.loginName)) {
			Pair<String, String> pair = null;

			if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
				/*
				 * 有：　1,cmge数据库 2,cmge的SD卡 3.zz数据库 4.zz的SD卡 这４个用户信息储存点 ３→１→４→２
				 */
				pair = PojoUtils.checkDouquUser_DB(ctx);
			}

			// 尝试从sdcard中读取
			if (pair == null)
				pair = Utils.getAccountFromSDcard(ctx);

			if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
				if (pair != null && PojoUtils.isCMGEUser(pair.first)) {
					pair = null;
				}
				if (pair == null)
					pair = PojoUtils.checkDouquUser_SDCard();
			}

			if (pair != null) {
				Application.setLoginName(pair.first);
				Application.password = pair.second;
			}
		}
	}

	/**
	 * 启动 SDK 登录界面，登录结果以回调消息形式通知游戏调用方。<br/>
	 * <i>消息规则如下：</i>
	 * 
	 * <table>
	 * <tr>
	 * <th>{@link Message android.os.Message:}</th>
	 * </tr>
	 * <tr>
	 * <th>{@link Message#what .what}</th>
	 * <th>{@link Message#arg1 .arg1}</th>
	 * <th>{@link Message#arg2 .arg2}</th>
	 * <th>{@link Message#obj .obj}</th>
	 * <td>描述</td>
	 * <tr>
	 * <tr>
	 * <td></td>
	 * <td>{@link MSG_TYPE#LOGIN}</td>
	 * <td>{@link MSG_STATUS#SUCCESS}</td>
	 * <td>{@link LoginCallbackInfo}</td>
	 * <td>登录成功，可获取用户信息</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#CANCEL}</td>
	 * <td>..</td>
	 * <td>用户取消登录，无其它信息</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#EXIT_SDK}</td>
	 * <td>..</td>
	 * <td>登录业务结束，无其它信息</td>
	 * </tr>
	 * </table>
	 * 
	 * @param callbackHandler
	 *            游戏登录回调接口
	 * @param what
	 *            回调消息
	 * @see MSG_TYPE#LOGIN
	 * @see MSG_STATUS#SUCCESS
	 * @see MSG_STATUS#FAILED
	 * @see MSG_STATUS#EXIT_SDK
	 * @see LoginCallbackInfo
	 */
	public static void showLoginView(Context ctx, Handler callbackHandler,
			int what) {
		if (ZZSDKConfig.SUPPORT_360SDK) {
			LoginForQiFu.startLogin(ctx, !Utils.isOrientationVertical(ctx),
					false, callbackHandler, what);
		} else {
			autoLoginUser(ctx);
			// init(); //统计登录
			LoginActivity.start(ctx, callbackHandler, what);
			// savaChannalMessage();
		}
	}

	/**
	 * 调用支付功能，支付结果以回调消息形式通知游戏调用方。<br/>
	 * <i>消息规则如下：</i>
	 * 
	 * <table>
	 * <tr>
	 * <th>{@link Message android.os.Message:}</th>
	 * </tr>
	 * <tr>
	 * <th>{@link Message#what .what}</th>
	 * <th>{@link Message#arg1 .arg1}</th>
	 * <th>{@link Message#arg2 .arg2}</th>
	 * <th>{@link Message#obj .obj}</th>
	 * <td>描述</td>
	 * <tr>
	 * <tr>
	 * <td></td>
	 * <td>{@link MSG_TYPE#PAYMENT .PAYMENT}</td>
	 * <td>{@link MSG_STATUS#SUCCESS .SUCCESS}</td>
	 * <td>{@link PaymentCallbackInfo}</td>
	 * <td>支付成功，可获取支付金额方式等</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#FAILED .FAILED}</td>
	 * <td>..</td>
	 * <td>支付失败，无其它信息</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#CANCEL .CANCEL}</td>
	 * <td>..</td>
	 * <td>支付取消，无其它信息</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#EXIT_SDK .EXIT_SDK}</td>
	 * <td>..</td>
	 * <td>此次业务结束，或成功或失败，原因见前面的消息</td>
	 * </tr>
	 * </table>
	 * 
	 * @param callbackHandler
	 *            支付结果通知　Handle
	 * @param what
	 *            支付结果消息号
	 * @param gameServerID
	 *            游戏服务器ID
	 * @param serverName
	 *            游戏服务器名称
	 * @param roleId
	 *            角色ID
	 * @param gameRole
	 *            角色名称
	 * @param amount
	 *            定额价格, 单位为 [分], 如果 >0表示此次充值只能以指定的价格交易.
	 * @param isCloseWindow
	 *            支付成功是否自动关闭支付SDK, 如果是 true 则在充值成功后自动退出SDK
	 * @param callBackInfo
	 *            厂家自定义参数
	 * 
	 * @see MSG_TYPE#PAYMENT
	 * @see MSG_STATUS#SUCCESS
	 * @see MSG_STATUS#FAILED
	 * @see MSG_STATUS#CANCEL
	 * @see MSG_STATUS#EXIT_SDK
	 * @see PaymentCallbackInfo
	 */
	public static void showPaymentView(Context mContext,
			Handler callbackHandler, int what, String gameServerID,
			final String serverName, final String roleId,
			final String gameRole, final int amount,
			final boolean isCloseWindow, final String callBackInfo) {
		Application.isCloseWindow = isCloseWindow;
		/* 固定金额设置 */
		if (amount > 0) {
			// 修改为整型int 接收
			Application.changeCount = amount;
		} else {
			Application.changeCount = 0;
		}
		Application.staticAmountIndex = -1;
		if (Application.loginName == null) {
			Pair<String, String> account = Utils.getAccountFromSDcard(mContext);
			Application.setLoginName(account.first);
			Application.password = account.second;
		}

		if (gameServerID != null && gameServerID.length() > 0) {
			SDKManager.setGameServerId(gameServerID);
		} else {
			gameServerID = Utils.getGameServerId(mContext);
		}

		ChargeActivity.start(callbackHandler, what, mContext, gameServerID,
				serverName, roleId, gameRole, callBackInfo);
	}

}
