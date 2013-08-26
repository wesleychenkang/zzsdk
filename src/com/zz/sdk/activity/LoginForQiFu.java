package com.zz.sdk.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Pair;

import com.qihoopay.insdk.activity.ContainerActivity;
import com.qihoopay.insdk.matrix.Matrix;
import com.qihoopay.sdk.protocols.IDispatcherCallback;
import com.qihoopay.sdk.protocols.ProtocolConfigs;
import com.qihoopay.sdk.protocols.ProtocolKeys;
import com.zz.sdk.BuildConfig;
import com.zz.sdk.LoginCallbackInfo;
import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.ZZSDKConfig;
import com.zz.sdk.entity.QiHooResult;
import com.zz.sdk.entity.Result;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.DialogUtil;
import com.zz.sdk.util.GetDataImpl;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.Utils;

/**
 * 封装 奇虎(360SDK) 的用户登录
 */
public class LoginForQiFu extends Activity {
	private static final String RESPONSE_TYPE_CODE = "code";
	private static final String AUTH_CODE = "code";

	private static Handler mCallBackhandler;
	private static int mWhatCallback;

	private static final String PRODUCT_ID = ZZSDKConfig.QIHOO_PRODUCT_ID;
	private static final String SIGN = "$360U$";

	private static final String K_IS_LANDSCAPE = "isLandScape";
	private static final String K_IS_BG_TRANSPARENT = "isBgTransparent";
	/** 默认的奇虎密码 */
	private static final String DEF_PWD_QIHOO = "qihumm";

	private Dialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		boolean isLand = intent.getBooleanExtra(K_IS_LANDSCAPE, false);
		boolean isTransparent = intent.getBooleanExtra(K_IS_BG_TRANSPARENT,
				false);
		invoke360SDK(isLand, isTransparent);
		setRequestedOrientation(!isLand ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
				: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (BuildConfig.DEBUG) {
			Logger.d("被销毁掉了");
		}

		try {

			if (mDialog != null) {
				mDialog.dismiss();
			}

			if (mCallBackhandler != null) {
				Message msg = Message.obtain(mCallBackhandler, mWhatCallback,
						MSG_TYPE.LOGIN, MSG_STATUS.EXIT_SDK);
				mCallBackhandler.sendMessage(msg);
			}
			clean();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void startLogin(Context ctx, boolean isLandScape,
			boolean isBgTransparent, Handler mhandler, int what) {
		Intent inent = new Intent(ctx, LoginForQiFu.class);
		inent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		inent.putExtra(K_IS_LANDSCAPE, isLandScape);
		inent.putExtra(K_IS_BG_TRANSPARENT, isBgTransparent);
		mCallBackhandler = mhandler;
		mWhatCallback = what;
		ctx.startActivity(inent);
	}

	private synchronized void clean() {
		mCallBackhandler = null;
		mWhatCallback = 0;
		mDialog = null;
	}

	private synchronized void tryNotify(int status, LoginCallbackInfo info) {
		if (mCallBackhandler != null) {
			Message msg = mCallBackhandler.obtainMessage(mWhatCallback,
					MSG_TYPE.LOGIN, status, info);
			mCallBackhandler.sendMessage(msg);
		}
	}

	private IDispatcherCallback mLoginCallback = new IDispatcherCallback() {
		@Override
		public void onFinished(String data) {
			Logger.d("mLoginCallback, data is " + data);
			final String authorizationCode = parseAuthorizationCode(data);
			Logger.d("360" + authorizationCode);
			if (authorizationCode != null) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						final Context ctx = LoginForQiFu.this.getBaseContext();
						QiHooResult qhResult = GetDataImpl.getInstance(ctx)
								.getAcessToken(PRODUCT_ID, authorizationCode);

						// 失败的 codes = "1"
						if (qhResult != null && "0".equals(qhResult.codes)) {
							String loginName = qhResult.id + SIGN;
							if (checkLoginNameExist(loginName)) {
								// 向服务器注册， codes=0成功|1失败|2用户名已经存在
								Result r = GetDataImpl
										.getInstance(ctx)
										.register(loginName, DEF_PWD_QIHOO, ctx);
								if (r != null) {
									// 已经存在(2)或注册成功(0)
									if ("2".equals(r.codes)
											|| "0".equals(r.codes)) {
										Utils.writeAccount2SDcard(ctx,
												loginName, DEF_PWD_QIHOO);
									} else {
										loginName = null;
									}
								} else {
									// 未知结果
									loginName = null;
								}
								Logger.d("执行了注册回调");
							} else {
								Result r = GetDataImpl.getInstance(ctx).login(
										loginName, DEF_PWD_QIHOO, 0, ctx);
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

							Application.setLoginName(loginName);

							LoginCallbackInfo loginCBInfo = new LoginCallbackInfo();
							loginCBInfo.loginName = loginName;
							loginCBInfo.statusCode = loginName == null ? LoginCallbackInfo.STATUS_FAILURE
									: LoginCallbackInfo.STATUS_SUCCESS;
							tryNotify(loginName == null ? MSG_STATUS.FAILED
									: MSG_STATUS.SUCCESS, loginCBInfo);
						}
						LoginForQiFu.this.finish();
					}
				}).start();
				try {
					mDialog = DialogUtil.showProgress(LoginForQiFu.this,
							"请稍候...", false);
					getWindow().setBackgroundDrawable(
							new ColorDrawable(0xFFAEDAEB));
				} catch (Exception e) {
				}
			} else {
				Logger.d("收到的data为null");
				LoginCallbackInfo info = new LoginCallbackInfo();
				info.statusCode = LoginCallbackInfo.STATUS_CLOSE_VIEW;
				info.loginName = null;
				tryNotify(MSG_STATUS.CANCEL, info);
				LoginForQiFu.this.finish();
			}
		}
	};

	/**
	 * 判断当前用户是否已写入SD卡
	 * 
	 * @param name
	 *            待比对的用户名
	 * @return true表示与 name 不匹配，需要向服务器重新注册
	 */
	private boolean checkLoginNameExist(String name) {
		Pair<String, String> p = Utils.getAccountFromSDcard(getBaseContext());
		if (p == null) {
			return true;
		} else {
			if (p.first == null || !p.first.equals(name)
					|| !DEF_PWD_QIHOO.equals(p.second)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 从Json字符中获取授权码
	 * 
	 * @param data
	 *            Json字符串
	 * @return 授权码
	 */
	private String parseAuthorizationCode(String data) {
		String authorizationCode = null;
		if (!TextUtils.isEmpty(data)) {
			try {
				JSONObject json = new JSONObject(data);
				int errCode = json.optInt("error_code");
				if (errCode == 0) {
					// 只支持code登陆模式
					JSONObject content = json.optJSONObject("content");
					authorizationCode = content.optString(AUTH_CODE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return authorizationCode;
	}

	protected void invoke360SDK(boolean isLandScape, boolean isBgTransparent) {
		Bundle bundle = new Bundle();

		// 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);

		// 界面相关参数，360SDK登录界面背景是否透明。
		bundle.putBoolean(ProtocolKeys.IS_LOGIN_BG_TRANSPARENT, isBgTransparent);

		// *** 以下非界面相关参数 ***

		// 必需参数，登录回应模式：CODE模式，即返回Authorization Code的模式。
		bundle.putString(ProtocolKeys.RESPONSE_TYPE, RESPONSE_TYPE_CODE);
		// 必需参数，使用360SDK的登录模块。
		bundle.putInt(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_LOGIN);
		Intent intent = new Intent(this, ContainerActivity.class);
		intent.putExtras(bundle);
		Matrix.invokeActivity(LoginForQiFu.this, intent, mLoginCallback);
	}

}
