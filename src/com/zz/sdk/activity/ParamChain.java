package com.zz.sdk.activity;

import java.util.HashMap;
import java.util.Stack;

import android.app.Activity;
import android.os.Handler;

import com.zz.sdk.BuildConfig;
import com.zz.sdk.layout.LAYOUT_TYPE;

/**
 * 参数链表、环境变量表
 * <p>
 * <ul>
 * 规则：
 * <li>默认访问规则为 {@link ValType#NORMAL}</li>
 * </ul>
 * 
 * @author nxliao
 * @version v0.1.0.20130927
 */
public class ParamChain {
	private static final ParamChain GLOBAL_INSTANCE = new ParamChain();

	static public ParamChain GLOBAL() {
		return GLOBAL_INSTANCE;
	}

	/**
	 * 全局变量名
	 * <p>
	 * 子级变量名定义规则：
	 * 
	 * <pre>
	 * // 示例，二级变量组  KeyLayout，名为 layout，定义一个变量 K_YOUR_NAME  
	 * public static interface <font color="#ff0000">KeyLayout</font> extends <b>KeyGlobal</b> {
	 *  	static final String _TAG_ = <b>KeyGlobal._TAG_</b> + "layout" + _SEPARATOR_;
	 *  
	 *  	<i>/** 键：自定义名, 类型 {@link String}，其它说明 *</i><i>/</i>
	 *  	public static final String K_YOUR_NAME = _TAG_ + "myName";
	 * }
	 * </pre>
	 * 
	 * @author nxliao
	 * 
	 */
	public static interface KeyGlobal {
		static final String _SEPARATOR_ = ".";
		static final String _TAG_ = "global" + _SEPARATOR_;

		/** 键：窗体名, {@link String} */
		public static final String UI_NAME = _TAG_ + "ui_activity_name";

		/** 键：窗体句柄, {@link Activity}，必须在销毁窗体时清理此引用 */
		public static final String UI_ACTIVITY = _TAG_ + "ui_activity_instance";

		/** 键：主视图类型, {@link LAYOUT_TYPE} */
		public static final String UI_VIEW_TYPE = _TAG_ + "ui_view_type";

		/** 键：价格, {@link Float}，单位 [卓越币]，精度 0.01 */
		public static final String PAY_AMOUNT = _TAG_ + "pay_amount";

		/** 键：IMSI, {@link String} */
		public static final String DEV_IMSI = _TAG_ + "dev_imsi";

		/** 键：Handle, {@link Handler} 用于处理回调 */
		public static final String CALLER_MSG_HANDLE = _TAG_
				+ "caller_msg_handler";

		/** 键：消息类型, {@link Integer} 用于处理回调 */
		public static final String CALLER_MSG_WHAT = _TAG_ + "caller_msg_what";

		/** 键：帮助标题，{@link String} 用于展示给用户，内容是网页 html */
		public static final String HELP_TITLE = _TAG_ + "help_title";
		/** 键：帮助内容，{@link String} 用于展示给用户，内容是网页 html */
		public static final String HELP_TOPIC = _TAG_ + "help_topic";
	}

	public static interface KeyUser extends KeyGlobal {
		static final String _TAG_ = KeyGlobal._TAG_ + "user" + _SEPARATOR_;

		/** 登录名, {@link String} */
		public static final String K_LOGIN_NAME = _TAG_ + "loginname";

		/** 游戏名|用户ID, {@link String} */
		public static final String K_ID = _TAG_ + "id";

		/** 用户密码, {@link String} */
		public static final String K_PASSWORD = _TAG_ + "password";

		/** 新用户密码, {@link String} */
		public static final String K_NEW_PASSWORD = _TAG_ + "password_new";

		/** 余额，{@link Float}，格式 0.00 */
		public static final String K_COIN_BALANCE = _TAG_ + "coin_balance";

		/** 卓越币与RMB的兑换比例，{@link Float}，格式 0.00 */
		public static final String K_COIN_RATE = _TAG_ + "coin_rate";
	}

	/** 本地变量 */
	private HashMap<String, Object> mData;
	/** 临时变量 */
	private HashMap<String, Object> mDataTmp;

	/** 上一级环境 */
	private ParamChain mParent;

	/** 层级 */
	private int mLevel;

	public static enum ValType {
		/** 普通变量 */
		NORMAL,
		/** 临时变量，在调用 {@link ParamChain#autoRelease()} 后立即回收 */
		TEMPORARY;
	}

	public ParamChain() {
		this(null);
	}

	public ParamChain(ParamChain base) {
		this(base, null);
	}

	public ParamChain(ParamChain base, HashMap<String, Object> data) {
		mParent = base;
		mLevel = base != null ? (base.mLevel + 1) : 0;

		if (data == null) {
			mData = new HashMap<String, Object>(8);
		} else {
			mData = new HashMap<String, Object>(data);
		}

		mDataTmp = new HashMap<String, Object>();
	}

	/**
	 * 合并整个环境链为一级环境
	 * 
	 * @param base
	 * @return
	 */
	public static ParamChain generateUnion(ParamChain base) {
		ParamChain c = new ParamChain();

		ParamChain p = base;
		Stack<ParamChain> s = new Stack<ParamChain>();
		while (p != null) {
			s.push(p);
			p = p.mParent;
		}

		while (!s.isEmpty()) {
			p = s.pop();
			c.mData.putAll(p.mData);
			c.mDataTmp.putAll(p.mDataTmp);
		}
		return c;
	}

	/**
	 * 变量名列表抽取新的环境
	 * 
	 * @param base
	 * @param keyList
	 * @return
	 */
	public static ParamChain generateUnion(ParamChain base, String... keyList) {
		ParamChain c = new ParamChain();
		if (base != null) {
			for (int i = 0, n = keyList.length; i < n; i++) {
				String key = keyList[i];
				ParamChain p = base.containsKey(key);
				if (p != null) {
					Object val = p.getOwned(key);
					ValType type = p.containsKeyOwn(key);
					if (key != null && val != null)
						c.add(key, val, type);
				}
			}
		}
		return c;
	}

	/**
	 * 返回层级，0 表示根级
	 * 
	 * @return
	 */
	public int getLevel() {
		return mLevel;
	}

	/**
	 * 返回父级环境
	 * 
	 * @return
	 */
	public ParamChain getParent() {
		return mParent;
	}

	/**
	 * 添加(当前级)
	 * 
	 * @param key
	 * @param val
	 * @return 是否成功
	 */
	public boolean add(String key, Object val) {
		return add(key, val, ValType.NORMAL);
	}

	/**
	 * 添加(当前级)
	 * 
	 * @param key
	 *            变量名
	 * @param val
	 *            值
	 * @param type
	 *            类型
	 * @return 是否成功
	 */
	public boolean add(String key, Object val, ValType type) {
		if (BuildConfig.DEBUG) {
			if (val == null || key == null) {
				return false;
			}
		}

		if (type == ValType.TEMPORARY) {
			// if (mDataTmp.containsKey(key)) {
			// return false;
			// }
			return mDataTmp.put(key, val) == val;
		} else {
			// if (mData.containsKey(key))
			// return false;
			return mData.put(key, val) == val;
		}
	}

	/**
	 * 修改变量(当前级)
	 * 
	 * @param key
	 * @param val
	 * @return
	 */
	// public Object put(String key, Object val) {
	// if (mDataTmp.containsKey(key)) {
	// return mDataTmp.put(key, val);
	// }
	// return mData.put(key, val);
	// }

	/**
	 * 删除(当前级)
	 * 
	 * @param key
	 * @return
	 */
	public Object remove(String key) {
		if (mData.containsKey(key)) {
			return mData.remove(key);
		}
		return mDataTmp.remove(key);
	}

	/**
	 * 清空(当前级)
	 */
	public void reset() {
		mData.clear();
		mDataTmp.clear();
	}

	/**
	 * 清空(当前级)临时变量
	 */
	public void autoRelease() {
		mDataTmp.clear();
	}

	/**
	 * 获取(当前级)
	 * 
	 * @param key
	 * @return
	 */
	public Object getOwned(String key) {
		if (mData.containsKey(key)) {
			return mData.get(key);
		}
		return mDataTmp.get(key);
	}

	/**
	 * 是否存在(当前级)
	 * 
	 * @param key
	 * @return
	 */
	public ValType containsKeyOwn(String key) {
		if (mData.containsKey(key))
			return ValType.NORMAL;
		if (mDataTmp.containsKey(key))
			return ValType.TEMPORARY;
		return null;
	}

	//
	// ////////////////////////////////////////////////////////////////////////
	//
	// - 扩展接口
	//
	//
	//

	public Object get(Enum<?> key) {
		return get(key.toString());
	}

	/**
	 * 获取指定类型的变量（当前级）
	 * 
	 * @param key
	 *            变量名
	 * @param clazz
	 *            将返回值的类型
	 * @return 如果包含变量并且类型符合，则返回之，否则返回 null
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T> T getOwned(String key, Class<T> clazz) {
		Object ret = getOwned(key);
		if (ret != null && clazz.isInstance(ret)) {
			return (T) ret;
		}
		return null;
	}

	/**
	 * 获取(所有级)
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		Object ret = getOwned(key);
		if (ret == null && mParent != null)
			ret = mParent.get(key);
		return ret;
	}

	/**
	 * 获取指定类型的变量（所有级），示例
	 * <P>
	 * Float amount = env.get({@link KeyGlobal#PAY_AMOUNT}, {@link Float
	 * Float.class});
	 * 
	 * @param key
	 *            变量名
	 * @param clazz
	 *            将返回值的类型
	 * @return 如果包含变量并且类型符合，则返回之，否则返回 null
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> clazz) {
		Object ret = get(key);
		if (ret != null && clazz.isInstance(ret)) {
			return (T) ret;
		}
		return null;
	}

	/**
	 * 是否存在变量
	 * 
	 * @param key
	 * @return 变量所在的实例，null表示不包含此变量
	 */
	public ParamChain containsKey(String key) {
		if (containsKeyOwn(key) != null)
			return this;
		if (mParent != null)
			return mParent.containsKey(key);
		return null;
	}

	/**
	 * 是否存在变量，从父级开始检查
	 * 
	 * @param key
	 * @return 变量所在的实例，null表示不包含此变量
	 */
	public ParamChain containsKeyReverse(String key) {
		if (mParent != null)
			return mParent.containsKey(key);
		if (containsKeyOwn(key) != null)
			return this;
		return null;
	}
}