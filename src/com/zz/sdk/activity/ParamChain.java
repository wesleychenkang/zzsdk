package com.zz.sdk.activity;

import java.util.HashMap;

import android.app.Activity;
import android.os.Handler;

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
public interface ParamChain {
	// private static final ParamChain GLOBAL_INSTANCE = new ParamChain();
	//
	// static public ParamChain GLOBAL() {
	// return GLOBAL_INSTANCE;
	// }

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
		public static final String K_UI_NAME = _TAG_ + "ui_activity_name";

		/** 键：窗体句柄, {@link Activity}，必须在销毁窗体时清理此引用 */
		public static final String K_UI_ACTIVITY = _TAG_
				+ "ui_activity_instance";

		/** 键：主视图类型, {@link LAYOUT_TYPE} */
		public static final String K_UI_VIEW_TYPE = _TAG_ + "ui_view_type";

		/** 键：价格, {@link Float}，单位 [卓越币]或[人民币]，与支付方式有关，精度 0.01 */
		public static final String K_PAY_AMOUNT = _TAG_ + "pay_amount";

		/** 键：汇率，{@link Float}，人民币与卓越币的兑换比例, 精度 0.01 */
		public static final String K_PAY_COIN_RATE = _TAG_ + "pay_coin_rate";

		/** 键：用户信息, {@link ParamChain} */
		public static final String K_USER = _TAG_ + "user";

		/** 键：IMSI, {@link String} */
		public static final String K_DEV_IMSI = _TAG_ + "dev_imsi";

		/** 键：帮助标题，{@link String} 用于展示给用户，内容是网页 html */
		public static final String K_HELP_TITLE = _TAG_ + "help_title";

		/** 键：帮助内容，{@link String} 用于展示给用户，内容是网页 html */
		public static final String K_HELP_TOPIC = _TAG_ + "help_topic";
	}

	public static interface KeyCaller extends KeyGlobal {
		static final String _TAG_ = KeyGlobal._TAG_ + "caller" + _SEPARATOR_;

		/** 键：Handle, {@link Handler} 用于处理回调 */
		public static final String K_MSG_HANDLE = _TAG_ + "msg_handler";

		/** 键：消息类型, {@link Integer} 用于处理回调 */
		public static final String K_MSG_WHAT = _TAG_ + "msg_what";

		/** 键：游戏服务器名称，{@link String} */
		static final String K_SERVER_NAME = _TAG_ + "server_name";

		/** 键：游戏服务器ID，{@link String} */
		static final String K_GAME_SERVER_ID = _TAG_ + "game_server_id";

		/** 键：角色ID，{@link String} */
		static final String K_ROLE_ID = _TAG_ + "role_id";

		/** 键：角色名称，{@link String} */
		static final String K_GAME_ROLE = _TAG_ + "game_role";

		/** 键：定额价格, 单位为 [分], 如果 >0表示此次充值只能以指定的价格交易.，{@link Integer} */
		static final String K_AMOUNT = _TAG_ + "amount";

		/** 键：支付成功是否自动关闭支付SDK, 如果是 true 则在充值成功后自动退出SDK，{@link Boolean} */
		static final String K_IS_CLOSE_WINDOW = _TAG_ + "is_close_window";

		/** 键：支付结果通知，{@link String} */
		static final String K_CALL_BACK_INFO = _TAG_ + "call_back_info";
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

	public static enum ValType {
		/** 普通变量 */
		NORMAL,
		/** 临时变量，在调用 {@link ParamChain#autoRelease()} 后立即回收 */
		TEMPORARY;
	}

	/**
	 * 构造出一个子级变量环境
	 * 
	 * @return
	 */
	public ParamChain grow();

	/**
	 * 构造出一个子级变量环境
	 * 
	 * @param aliasName
	 *            别名
	 * @return
	 */
	public ParamChain grow(String aliasName);

	/**
	 * 构造出一个子级变量环境
	 * 
	 * @param data
	 *            附加的变量表
	 * @return
	 */
	public ParamChain grow(HashMap<String, Object> data);

	/**
	 * 合并整个环境链为一级环境
	 * 
	 * @return
	 */
	public ParamChain generateUnion();

	/**
	 * 变量名列表抽取新的环境
	 * 
	 * @param base
	 * @param keyList
	 * @return
	 */
	public ParamChain generateUnion(String... keyList);

	/**
	 * 返回层级，0 表示根级
	 * 
	 * @return
	 */
	public int getLevel();

	/**
	 * 返回父级环境
	 * 
	 * @return
	 */
	public ParamChain getParent();

	/**
	 * 根据别名返回父级环境，有可能返回自己
	 * 
	 * @param aliasName
	 *            别名
	 * @return
	 */
	public ParamChain getParent(String aliasName);

	/**
	 * 返回根级环境
	 * 
	 * @return
	 */
	public ParamChain getRoot();

	/**
	 * 添加(当前级)
	 * 
	 * @param key
	 * @param val
	 * @return 是否成功
	 */
	public boolean add(String key, Object val);

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
	public boolean add(String key, Object val, ValType type);

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
	public Object remove(String key);

	/**
	 * 清空(当前级)
	 */
	public void reset();

	/**
	 * 清空(当前级)临时变量
	 */
	public void autoRelease();

	/**
	 * 获取(当前级)
	 * 
	 * @param key
	 * @return
	 */
	public Object getOwned(String key);

	/**
	 * 是否存在(当前级)
	 * 
	 * @param key
	 * @return
	 */
	public ValType containsKeyOwn(String key);

	//
	// ////////////////////////////////////////////////////////////////////////
	//
	// - 扩展接口
	//
	//
	//

	public Object get(Enum<?> key);

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
	public <T> T getOwned(String key, Class<T> clazz);

	/**
	 * 获取(所有级)
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key);

	/**
	 * 获取指定类型的变量（所有级），示例
	 * <P>
	 * Float amount = env.get({@link KeyGlobal#K_PAY_AMOUNT}, {@link Float
	 * Float.class});
	 * 
	 * @param key
	 *            变量名
	 * @param clazz
	 *            将返回值的类型
	 * @return 如果包含变量并且类型符合，则返回之，否则返回 null
	 * 
	 */
	public <T> T get(String key, Class<T> clazz);

	/**
	 * 是否存在变量
	 * 
	 * @param key
	 * @return 变量所在的实例，null表示不包含此变量
	 */
	public ParamChain containsKey(String key);

	/**
	 * 是否存在变量，从父级开始检查
	 * 
	 * @param key
	 * @return 变量所在的实例，null表示不包含此变量
	 */
	public ParamChain containsKeyReverse(String key);

}