package com.zz.sdk.layout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;

import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyGlobal;

/**
 * 视图工厂
 * 
 * @author nxliao
 * @version v0.1.0.20130927
 */
public class LayoutFactory {

	public static class KeyLayoutFactory implements KeyGlobal {
		protected static final String _TAG_ = KeyGlobal._TAG_
				+ "key_layout_factory" + _SEPARATOR_;

		/** 键：宿主, 类型 {@link ILayoutHost} */
		public static final String K_HOST = _TAG_ + "host";

		/** 键：视图, 类型 {@link ILayoutView} */
		public static final String K_VIEW = _TAG_ + "view";

		protected KeyLayoutFactory() {
		}

	}

	public static interface ILayoutHost {

		/**
		 * 产生一个等待对话框（进度条），更详细的对话框构造，可通过 {@link KeyGlobal#UI_ACTIVITY} 获取
		 * {@link Activity}
		 * 
		 * @param type
		 *            类型
		 * @param msg
		 *            消息，可以为空(null)
		 * @param cancelable
		 *            是否允许用户点击对话框外部区域来关闭对话框
		 * @param cancelListener
		 *            关闭对话框的回调，可以为空(null)
		 * @param cancelTag
		 *            回调监听器的消息，可以为空(null)
		 */
		public void showWaitDialog(int type, String msg, boolean cancelable,
				OnCancelListener cancelListener, Object cancelTag);

		/**
		 * 隐藏对话框
		 */
		public void hideWaitDialog();

		/**
		 * 返回上一界面
		 */
		public void back();

		/**
		 * 退出
		 */
		public void exit();

		/**
		 * 进入新界面
		 * 
		 * @param type
		 * @param env
		 */
		public void enter(LAYOUT_TYPE type, ParamChain rootEnv);
	}

	public static interface ILayoutView {

		/***
		 * 进入
		 * 
		 * @return
		 */
		public boolean onEnter();

		/**
		 * 被纳入缓存
		 * 
		 * @return
		 */
		public boolean onPush();

		/**
		 * 被关闭
		 * 
		 * @return
		 */
		public boolean onClose();

		/**
		 * 对话框被关闭
		 * 
		 * @param dialog
		 * @param cancelTag
		 * @return
		 */
		public boolean onDialogCancel(DialogInterface dialog, Object cancelTag);

		/**
		 * 获取环境变量
		 * 
		 * @return
		 */
		public ParamChain getEnv();
	}

	/**
	 * 创建 视图
	 * 
	 * @param ctx
	 * @param type
	 * @param params
	 * @return
	 */
	public static View createLayout(Context ctx, LAYOUT_TYPE type,
			ParamChain rootEnv) {

		switch (type) {
		case PaymentList:
			return new ChargePaymentListLayout(ctx);
		case Exchange:
			return new ExchangeLayout(ctx, rootEnv);
		case ExchangeDetail:
			return new ExchangeDetailLayout(ctx, rootEnv);

		default:
			break;
		}

		return null;
	}

}
