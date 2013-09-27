package com.zz.sdk.activity;

import java.util.HashMap;
import java.util.Stack;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.zz.sdk.BuildConfig;
import com.zz.sdk.activity.ParamChain.KeyGlobal;
import com.zz.sdk.activity.ParamChain.ValType;
import com.zz.sdk.layout.ChargeAbstractLayout;
import com.zz.sdk.layout.LAYOUT_TYPE;
import com.zz.sdk.layout.LayoutFactory;
import com.zz.sdk.layout.LayoutFactory.KeyLayoutFactory;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.DialogUtil;
import com.zz.sdk.util.FlagControl;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.Utils;

/**
 * 基本窗体
 * 
 * @author nxliao
 * 
 */
public class BaseActivity extends Activity {

	protected FlagControl mFlag;

	/** 提示类框，如等待进度条等 */
	protected Dialog mDialog;

	/** 视图栈 */
	final private Stack<View> mViewStack = new Stack<View>();
	private View mCurrentView;

	private LayoutFactory.ILayoutView mActView;

	private String mName;

	private ParamChain mRootEnv;

	/** 参数缓冲区:<窗口名，参数表> */
	private static HashMap<String, ParamChain> sMapENV;

	protected static synchronized ParamChain pushENV(String key, ParamChain env) {
		return sMapENV.put(key, env);
	}

	protected static synchronized ParamChain popENV(String key) {
		if (key == null)
			return null;
		return sMapENV.remove(key);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mFlag = new FlagControl();

		boolean init_success = init(this);
		if (!init_success)
			end();
	}

	protected void init_activity(Activity activity) {
		Utils.loack_screen_orientation(activity);
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);

	}

	/**
	 * 初始化。
	 * 
	 * @param activity
	 *            窗体实例
	 * @return 是否成功
	 */
	protected boolean init(Activity activity) {
		ParamChain env = null;
		Intent intent = activity.getIntent();
		if (intent != null) {
			mName = intent.getStringExtra(KeyGlobal.K_NAME);
			env = popENV(mName);
		}

		if (env == null)
			return false;

		mRootEnv = new ParamChain(env);
		mRootEnv.add(KeyGlobal.K_ACTIVITY, activity, ValType.TEMPORARY);
		mRootEnv.add(KeyLayoutFactory.K_HOST, new LayoutFactory.ILayoutHost() {

			@Override
			public void showWaitDialog(int type, String msg, boolean cancelable) {
				showDialog(msg, cancelable);
			}

			@Override
			public void hideWaitDialog() {
				hideDialog();
			}

			@Override
			public void exit() {
				end();
			}

			@Override
			public void back() {
				popViewFromStack();
			}

			@Override
			public void enter(LAYOUT_TYPE type, ParamChain rootEnv) {
				// TODO Auto-generated method stub
				tryEnterView(type, rootEnv);
			}
		}, ValType.TEMPORARY);

		// 创建主视图
		LAYOUT_TYPE type = mRootEnv.get(KeyGlobal.K_VIEW_TYPE,
				LAYOUT_TYPE.class);
		if (!tryEnterView(type, mRootEnv)) {
			return false;
		}

		init_activity(activity);

		return true;
	}

	private boolean tryEnterView(LAYOUT_TYPE type, ParamChain rootEnv) {
		if (type == null) {
			if (BuildConfig.DEBUG) {
				Logger.d("unknow view type!");
			}
			return false;
		}

		ChargeAbstractLayout vl = LayoutFactory.createLayout(getBaseContext(),
				type, rootEnv);
		if (vl != null) {
			pushView2Stack(vl);
			return true;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (BuildConfig.DEBUG) {
			System.out.println("销毁掉了 " + mName);
		}
		clean();
	}

	private void showDialog(CharSequence msg, boolean cancelable) {
		hideDialog();
		mDialog = DialogUtil.showProgress(this, msg, cancelable);
		mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// end();
				if (mActView != null)
					mActView.onDialogCancel(0);
			}
		});
	}

	/** 关闭提示框，如「等待框」等 */
	protected void hideDialog() {
		if (null != mDialog && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	protected void clean() {
		hideDialog();
		mViewStack.clear();
		mName = null;
		mRootEnv.autoRelease();
		mRootEnv = null;
	}

	protected void pushView2Stack(View newView) {
		if (mViewStack.size() > 0) {
			View peek = mViewStack.peek();
			peek.clearFocus();
			// peek.startAnimation(mAnimLeftOut);
		}
		mViewStack.push(newView);
		mCurrentView = newView;
		setContentView(newView);
		newView.requestFocus();
		if (mViewStack.size() > 1) {
			// 启动动画
			// newView.startAnimation(mAnimRightIn);
		}
	}

	private View popViewFromStack() {
		if (mViewStack.size() > 1) {
			if (Application.isCloseWindow && Application.isAlreadyCB == 1) {
				this.finish();
				return null;
			}
			// // 弹出旧ui
			// View pop = mViewStack.pop();
			// if (pop instanceof SmsChannelLayout) {
			// Application.isMessagePage = 1;
			// }
			// if (pop instanceof ChargeSMSDecLayout) {
			// Application.isMessagePage = 0;
			// }
			// if (Application.isMessagePage == 1 && isSendMessage == false) {
			// // 短信取消后发送取消支付请求
			// Application.isMessagePage = 0;
			// smsPayCallBack(-2, null);
			//
			// }
			// pop.clearFocus();
			mCurrentView = mViewStack.peek();
			setContentView(mCurrentView);
			mCurrentView.requestFocus();

			return mCurrentView;
		} else {
			Logger.d("ChargeActivity exit");
			// if (Application.isAlreadyCB == 1) {
			// allPayCallBack(-2);
			// Application.isAlreadyCB = 0;
			// }
			finish();
			return null;
		}
	}

	protected void end() {
		finish();
	}
}
