package com.zz.sdk.util;

import android.content.Context;
import android.os.Handler;

import com.zz.sdk.SDKManager;
import com.zz.sdk.ZZSDKConfig;

import java.lang.ref.WeakReference;

/**
 * 社交模块处理
 * <ul>依赖：
 * <li>{@link ZZSDKConfig#SUPPORT_SOCIAL}</li>
 * </ul>
 * <ol>流程：
 * <li>登录ＳＤＫ，获取 sdkUserId</li>
 * <li>启动社交模块</li>
 * <li>...用户操作</li>
 * <li>关闭社交模块</li>
 * </ol>
 *
 * @author nxliao
 * @version 0.1.0.20140109
 */
public class SocialUtil {
	private final static Object LOCK = new Object();

	private Context mContext;
	private SDKManager mSDKManager;
	private String mSdkUserId;

	/** UI线程的Handle */
	private Handler mMainHandler;

	private static WeakReference<SocialUtil> instance = null;

	public static SocialUtil getInstance(Context context, SDKManager sdkManager) {
		synchronized (LOCK) {
			SocialUtil su = instance == null ? null : instance.get();
			if (su == null) {
				su = new SocialUtil();
				instance = new WeakReference<SocialUtil>(su);
			}
			su.init(context, sdkManager);
			return su;
		}
	}

	public static SocialUtil getInstance() {
		synchronized (LOCK) {
			return instance == null ? null : instance.get();
		}
	}

	private SocialUtil() {
		mContext = null;
		mSDKManager = null;
		mSdkUserId = null;
	}

	private void init(Context context, SDKManager sdkManager) {
		mContext = context;
		mSDKManager = sdkManager;
		mMainHandler = new Handler(mContext.getMainLooper());

		if (ZZSDKConfig.SUPPORT_SOCIAL) {
			// 关联回调函数
			com.joygame.socialclient.SocialManager.setOnPayCallBack(new com.joygame.socialclient.interfaces.CallBack() {
				@Override
				public void onCall() {
					mSDKManager.showPaymentView(null, 0, Utils.getGameServerId(mContext), "zzsdk-social", 0, true, false, false);
				}
			}
			);

			// TODO: 目前道具兑换尚未完成，所以不开启
//			com.joygame.socialclient.SocialManager.setOnExChangeCallBackCallBack(new com.joygame.socialclient.interfaces.CallBack() {
//				@Override
//				public void onCall() {
//					mSDKManager.showExchange(null, Utils.getGameServerId(mContext));
//				}
//			}
//			);
		}
	}

	public void recycle() {
		synchronized (LOCK) {
			if (ZZSDKConfig.SUPPORT_SOCIAL) {
				com.joygame.socialclient.SocialManager.destroy(mContext);
			}
			mContext = null;
			mSDKManager = null;
			if (instance != null && instance.get() == this) {
				instance = null;
			}
		}
	}

	private void tryInitSocial() {
		if (ZZSDKConfig.SUPPORT_SOCIAL) {
			if (mMainHandler != null) {
				mMainHandler.post(new Runnable() {
					@Override
					public void run() {
						// 启动社交
						com.joygame.socialclient.SocialManager.startSocialService(mContext, mSdkUserId, Utils.getProjectId(mContext));
					}
				}
				);
			}
		}
	}

	/** 登录成功后 */
	public void onLoginResult(String sdkuserid) {
		synchronized (LOCK) {
			mSdkUserId = sdkuserid;
		}
		if (ZZSDKConfig.SUPPORT_SOCIAL) {
			// 未知开关状态
			new Thread("check-social") {
				ConnectionUtil cu = ConnectionUtil.getInstance(mContext);

				@Override
				public void run() {
					if (com.joygame.socialclient.SocialManager.isEnabled()) {
						// 开启
						tryInitSocial();
					}
				}
			}.start();
		}
	}
}

