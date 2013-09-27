package com.zz.sdk.layout;

import android.content.Context;

import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyGlobal;

/**
 * 视图工厂
 * 
 * @author nxliao
 * @version v0.1.0.20130927
 */
public class LayoutFactory {
	public static class KeyLayoutFactory extends KeyGlobal {
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
		 * 产生一个等待对话框（进度条）
		 */
		public void showWaitDialog(int type, String msg, boolean cancelable);

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

		/**
		 * 对话框被关闭
		 * 
		 * @param dialogId
		 * @return
		 */
		public boolean onDialogCancel(int dialogId);

		/**
		 * 销毁
		 */
		public void onDestory();

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
	public static ChargeAbstractLayout createLayout(Context ctx,
			LAYOUT_TYPE type, ParamChain rootEnv) {

		switch (type) {
		case PaymentList:
			return new ChargePaymentListLayout(ctx);

		default:
			break;
		}

		return null;
	}

}
