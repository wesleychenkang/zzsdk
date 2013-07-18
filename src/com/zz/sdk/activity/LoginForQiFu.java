package com.zz.sdk.activity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.qihoopay.insdk.activity.ContainerActivity;
import com.qihoopay.insdk.matrix.Matrix;
import com.qihoopay.sdk.protocols.IDispatcherCallback;
import com.qihoopay.sdk.protocols.ProtocolConfigs;
import com.qihoopay.sdk.protocols.ProtocolKeys;
import com.zz.sdk.entity.QiHooResult;
import com.zz.sdk.entity.Result;
import com.zz.sdk.util.GetDataImpl;
import com.zz.sdk.util.Utils;

public class LoginForQiFu extends Activity {
	protected static final String RESPONSE_TYPE_CODE = "code";
	private static final String AUTH_CODE = "code";
	public static Handler callBackhandler;
	private static int mWhatCallback;
	private String productId = "D1001";// 大话360测试
	private String prjectId = "-1";
	private String sign = "$360U$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prjectId = Utils.getProjectId(this);
		Intent intent = getIntent();
		doSdkLogin(intent.getBooleanExtra("isLandScape", false),
				intent.getBooleanExtra("isBgTransparent", false));
	}

	public static void startLogin(Context ctx, boolean isLandScape,
			boolean isBgTransparent, Handler mhandler, int what) {
		Intent inent = new Intent(ctx, LoginForQiFu.class);
		inent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		inent.putExtra("isLandScape", isLandScape);
		inent.putExtra("isBgTransparent", isBgTransparent);
		callBackhandler = mhandler;
		mWhatCallback = what;
		ctx.startActivity(inent);
	}

	private IDispatcherCallback mLoginCallback = new IDispatcherCallback() {
		@Override
		public void onFinished(String data) {
			Log.d("zz_sdk", "mLoginCallback, data is " + data);
			final String authorizationCode = parseAuthorizationCode(data);
			Log.d("zz_sdk", "360" + authorizationCode);
			if (authorizationCode != null) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						final Context ctx = LoginForQiFu.this.getBaseContext();

						QiHooResult qhResult = GetDataImpl.getInstance(ctx)
								.getAcessToken(productId, authorizationCode);
						LoginCallbackInfo loginCBInfo = new LoginCallbackInfo();
						if (qhResult != null && "0".equals(qhResult.codes)
								&& callBackhandler != null) {
							Application.isLogin = true;
							loginCBInfo.statusCode = 1;
							Application.loginName = qhResult.id + sign;
							loginCBInfo.loginName = Application.loginName;
							Message msg = Message.obtain();
							msg.obj = loginCBInfo;
							msg.what = mWhatCallback;
							if (isExistAcount()) {
								Result r = GetDataImpl.getInstance(
										LoginForQiFu.this).register(
										Application.loginName, "qihumm",
										LoginForQiFu.this);
								if (r != null) {
									if (r.codes.equals("2")
											|| r.codes.equals("0")) {
										Utils.writeAccount2SDcard(ctx,
												Application.loginName, "qihumm");
										loginCBInfo.loginName =Application.loginName;
									}else{
										Application.loginName = null;
										loginCBInfo.loginName = null;
									}
									loginCBInfo.statusCode = Integer
											.parseInt(r.codes);
								 } else {
									Application.loginName = null;
									loginCBInfo.loginName = null;
									loginCBInfo.statusCode = 1;
								}
								Log.d("zz_sdk", "执行了注册回调");
								callBackhandler.sendMessage(msg);

							} else {
								loginCBInfo.statusCode = -1;
								loginCBInfo.loginName = Application.loginName;
								Result r = GetDataImpl.getInstance(
										LoginForQiFu.this).login(
										Application.loginName, "qihumm", 0,
										LoginForQiFu.this);
								if (r != null) {
									loginCBInfo.statusCode = Integer
											.parseInt(r.codes);

								} else {
									loginCBInfo.statusCode = 1;
								}
								Log.d("zz_sdk", "执行了登录回调");
								callBackhandler.sendMessage(msg);
							}
						}
					}
				}).start();
				}else{
					LoginCallbackInfo loginCBInfo = new LoginCallbackInfo();
					loginCBInfo.statusCode = -2;
					loginCBInfo.loginName = null;
					Message msg =new Message();
					msg.what = mWhatCallback;
					msg.obj = loginCBInfo;
					if(callBackhandler!=null){
					callBackhandler.sendMessage(msg);
					}
					Log.d("zz_sdk", "收到的data为null");
					
				}
			LoginForQiFu.this.finish();
			
		}
	};

	/** 判断当前用户是否已写入SD卡 */
	private boolean isExistAcount() {
		Pair<String, String> p = Utils.getAccountFromSDcard(getBaseContext());
		if (p == null) {
			return true;
		} else {

			if (!p.first.equals(Application.loginName)) {
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

	protected void doSdkLogin(boolean isLandScape, boolean isBgTransparent) {
		Intent intent = getLoginIntent(isLandScape, isBgTransparent);
		Matrix.invokeActivity(LoginForQiFu.this, intent, mLoginCallback);
	}

	private Intent getLoginIntent(boolean isLandScape, boolean isBgTransparent) {

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
		return intent;
	}

	@Override
	protected void onDestroy() {

		System.out.println("被销毁掉了");
		super.onDestroy();
	}

	
	
	
	}	
	

