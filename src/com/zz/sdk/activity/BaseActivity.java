package com.zz.sdk.activity;

import java.util.Stack;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.zz.sdk.BuildConfig;
import com.zz.sdk.activity.ParamChain.KeyCaller;
import com.zz.sdk.activity.ParamChain.KeyGlobal;
import com.zz.sdk.activity.ParamChain.ValType;
import com.zz.sdk.layout.LAYOUT_TYPE;
import com.zz.sdk.layout.LayoutFactory;
import com.zz.sdk.layout.LayoutFactory.ILayoutView;
import com.zz.sdk.layout.LayoutFactory.KeyLayoutFactory;
import com.zz.sdk.util.DialogUtil;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.Utils;

/**
 * 基本窗体
 * 
 * @author nxliao
 * 
 */
public class BaseActivity extends Activity {

	/** 提示类框，如等待进度条等，关闭窗体时需要销毁 */
	protected Dialog mDialog;

	/** 视图栈 */
	final private Stack<ILayoutView> mViewStack = new Stack<ILayoutView>();
	private ILayoutView mActView;

	private String mName;

	private ParamChain mRootEnv;

	static final class KeyBaseActivity implements KeyGlobal {
		protected static final String _TAG_ = KeyGlobal._TAG_ + "base_activity"
				+ _SEPARATOR_;

		public static final String DIALOG_CANCEL_LISTENER = _TAG_
				+ "dialog_cancel_listener";
		public static final String DIALOG_CANCEL_TAG = _TAG_
				+ "dialog_cancel_tag";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepare_activity(this);
		boolean init_success = init(this);
		if (!init_success)
			end();
	}

	protected void prepare_activity(Activity activity) {
		// Utils.loack_screen_orientation(activity);
		// activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
			mName = intent.getStringExtra(KeyGlobal.K_UI_NAME);
			if (mName != null) {
				Object o = ParamChainImpl.GLOBAL().getRoot().remove(mName);
				if (o instanceof ParamChain) {
					env = (ParamChain) o;
				}
			}
		}

		if (env == null) {
			if (BuildConfig.DEBUG) {
				Logger.e("找不到有效变量环境");
			}
			return false;
		}

		mRootEnv = env.grow();
		mRootEnv.add(KeyGlobal.K_UI_ACTIVITY, activity, ValType.TEMPORARY);
		mRootEnv.add(KeyLayoutFactory.K_HOST, new LayoutFactory.ILayoutHost() {
			@Override
			public void showWaitDialog(int type, String msg,
					boolean cancelable, OnCancelListener cancelListener,
					Object cancelTag) {
				showDialog(msg, cancelable, cancelListener, cancelTag);
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
		LAYOUT_TYPE type = mRootEnv.get(KeyGlobal.K_UI_VIEW_TYPE,
				LAYOUT_TYPE.class);
		if (!tryEnterView(type, mRootEnv)) {
			Logger.e("bad root view");
			return false;
		}

		return true;
	}

	private boolean tryEnterView(LAYOUT_TYPE type, ParamChain rootEnv) {
		if (type == null) {
			return false;
		}

		ILayoutView vl = LayoutFactory.createLayout(getBaseContext(), type,
				rootEnv);
		if (vl != null) {
			pushView2Stack(vl);
			vl.onEnter();
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		if (popViewFromStack() != null)
			return;
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (BuildConfig.DEBUG) {
			System.out.println("销毁掉了 " + mName);
		}
		clean();
	}

	private void showDialog(CharSequence msg, boolean cancelable,
			OnCancelListener cancelListener, Object cancelTag) {
		hideDialog();

		mRootEnv.add(KeyBaseActivity.DIALOG_CANCEL_LISTENER, cancelListener,
				ValType.TEMPORARY);
		mRootEnv.add(KeyBaseActivity.DIALOG_CANCEL_TAG, cancelTag,
				ValType.TEMPORARY);
		mDialog = DialogUtil.showProgress(this, msg, cancelable);
		mDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (mRootEnv != null) {
					OnCancelListener l = mRootEnv.getOwned(
							KeyBaseActivity.DIALOG_CANCEL_LISTENER,
							OnCancelListener.class);
					if (l != null) {
						l.onCancel(dialog);
					} else {
						// end();
						if (mActView != null) {
							Object tag = mRootEnv
									.getOwned(KeyBaseActivity.DIALOG_CANCEL_TAG);
							mActView.onDialogCancel(dialog, tag);
						}
					}
				}
			}
		});
	}

	/** 关闭提示框，如「等待框」等 */
	protected void hideDialog() {
		if (null != mDialog && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
			mRootEnv.remove(KeyBaseActivity.DIALOG_CANCEL_LISTENER);
			mRootEnv.remove(KeyBaseActivity.DIALOG_CANCEL_TAG);
		}
	}

	protected void clean() {
		hideDialog();
		if (mViewStack != null && !mViewStack.isEmpty()) {
			ILayoutView lv; // = mViewStack.pop();
			while (!mViewStack.isEmpty()) {
				lv = mViewStack.pop();
				if (lv.isAlive()) {
					lv.onExit();
				}
			}
			// mViewStack.clear();
		}
		mName = null;
		if (mRootEnv != null) {
			mRootEnv.autoRelease();
			mRootEnv = null;
		}
	}

	protected void pushView2Stack(ILayoutView vl) {
		if (mViewStack.size() > 0) {
			ILayoutView top = mViewStack.peek();
			if (top.isAlive()) {
				View peek = top.getRootView();
				peek.clearFocus();
				top.onPause();
				// peek.startAnimation(mAnimLeftOut);
			}
		}
		mViewStack.push(vl);
		View curView = vl.getRootView();
		setContentView(curView);
		curView.requestFocus();
		if (mViewStack.size() > 1) {
			// 启动动画
			// newView.startAnimation(mAnimRightIn);
		}
	}

	private View popViewFromStack() {
		if (mViewStack.size() > 1) {
			Boolean isCloseWindow = mRootEnv.get(KeyCaller.K_IS_CLOSE_WINDOW,
					Boolean.class);
			// if (Application.isCloseWindow && Application.isAlreadyCB == 1) {
			if (Boolean.TRUE.equals(isCloseWindow)) {
				this.finish();
				return null;
			}
			ILayoutView lv;

			// 弹出旧ui
			lv = mViewStack.peek();
			if (lv.isAlive()) {
				// 先判断是否允许关闭
				View pop = lv.getRootView();
				if (pop == null || lv.isExitEnabled()) {
					lv.onExit();
				} else {
					return pop;
				}
				if (pop != null) {
					pop.clearFocus();
				}
				// if (pop instanceof SmsChannelLayout) {
				// Application.isMessagePage = 1;
				// }
				// if (pop instanceof ChargeSMSDecLayout) {
				// Application.isMessagePage = 0;
				// }
				// if (Application.isMessagePage == 1 && isSendMessage == false)
				// {
				// // 短信取消后发送取消支付请求
				// Application.isMessagePage = 0;
				// smsPayCallBack(-2, null);
				//
				// }
			}
			lv = mViewStack.pop();

			lv = mViewStack.peek();
			View curView = lv.getRootView();
			setContentView(curView);
			curView.requestFocus();
			lv.onResume();

			return curView;
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
		if (mViewStack != null && mViewStack.size() > 0) {
			// 关闭前先判断是否允许关闭
			ILayoutView lv = mViewStack.peek();
			if (lv.isAlive() && !lv.isExitEnabled()) {
				return;
			}
		}

		finish();
	}
}
