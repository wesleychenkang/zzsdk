package com.zz.sdk.layout;

import java.lang.reflect.Constructor;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyGlobal;
import com.zz.sdk.protocols.ActivityControlInterface;
import com.zz.sdk.util.Logger;

/**
 * 视图工厂
 * 
 * @author nxliao
 * @version v0.1.0.20130927
 */
public class LayoutFactory {

	public static final class KeyLayoutFactory implements KeyGlobal {
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

		/**
		 * 进入新界面
		 * 
		 * @param classLoader
		 *            类加载器，null表示使用默认
		 * @param className
		 *            类名
		 * @param rootEnv
		 *            环境变量
		 */
		public void enter(ClassLoader classLoader, String className,
				ParamChain rootEnv);

		/**
		 * 设置窗体事件监听器，调用者自己维护生命周期
		 * 
		 * @param controlInterface
		 */
		public void addActivityControl(ActivityControlInterface controlInterface);

		public void removeActivityControl(
				ActivityControlInterface controlInterface);
	}

	public static interface ILayoutView {

		/***
		 * 进入，此时可启动初始化代码
		 * 
		 * @return
		 */
		public boolean onEnter();

		/**
		 * 被纳入缓存，即 pause
		 * 
		 * @return
		 */
		public boolean onPause();

		/**
		 * 即 resume
		 * 
		 * @return
		 */
		public boolean onResume();

		/**
		 * 是否允许被关闭
		 * 
		 * @return
		 */
		public boolean isExitEnabled();

		/**
		 * 被关闭
		 * 
		 * @return
		 */
		public boolean onExit();

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

		/***
		 * 是否有效
		 * 
		 * @return
		 */
		public boolean isAlive();

		/**
		 * 获取主视图，用于窗体显示
		 * 
		 * @return
		 */
		public View getRootView();
	}

	/**
	 * 创建 视图
	 * 
	 * @param ctx
	 * @param type
	 * @param params
	 * @return
	 */
	public static ILayoutView createLayout(Context ctx, LAYOUT_TYPE type,
			ParamChain rootEnv) {

		switch (type) {
		case PaymentList:
			return new PaymentListLayout(ctx, rootEnv);
		case Exchange:
			return new ExchangeLayout(ctx, rootEnv);
		case ExchangeDetail:
			return new ExchangeDetailLayout(ctx, rootEnv);
		default:
			break;
		}

		return null;
	}

	/**
	 * 构造指定类名
	 * 
	 * @param ctx
	 * @param className
	 *            类名
	 * @param classLoader
	 *            加载器，若为null表示使用虚拟机的默认类加载器
	 * @param rootEnv
	 * @return
	 */
	public static ILayoutView createLayout(Context ctx, String className,
			ClassLoader classLoader, ParamChain rootEnv) {
		try {
			Class<?> lFactoryClass = Class
					.forName(className, true, classLoader);
			if (ILayoutView.class.isAssignableFrom(lFactoryClass)) {
				Constructor<?> c = lFactoryClass.getConstructor(Context.class,
						ParamChain.class);
				return (ILayoutView) c.newInstance(ctx, rootEnv);
				// return (ILayoutView) lFactoryClass.newInstance();
			}
		} catch (Exception e) {
			Logger.d("Cannot instanciate layout [" + className + "]");
		}
		return null;
	}
}
