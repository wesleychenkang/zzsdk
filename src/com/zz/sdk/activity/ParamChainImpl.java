package com.zz.sdk.activity;

import java.util.HashMap;
import java.util.Stack;

import com.zz.sdk.BuildConfig;

/**
 * 参数链表、环境变量表
 * 
 * @author nxliao
 * @version v0.1.0.20131010
 */
public class ParamChainImpl implements ParamChain {
	private static final ParamChainImpl GLOBAL_INSTANCE = new ParamChainImpl();

	static public ParamChainImpl GLOBAL() {
		return GLOBAL_INSTANCE;
	}

	/** 本地变量 */
	private HashMap<String, Object> mData;
	/** 临时变量 */
	private HashMap<String, Object> mDataTmp;

	/** 上一级环境 */
	private ParamChainImpl mParent;

	/** 别名 */
	private String mAliasName;

	/** 层级 */
	private int mLevel;

	public ParamChainImpl() {
		this(null);
	}

	public ParamChainImpl(ParamChainImpl base) {
		this(base, null);
	}

	public ParamChainImpl(ParamChainImpl base, HashMap<String, Object> data) {
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
	public static ParamChainImpl generateUnion(ParamChainImpl base) {
		ParamChainImpl c = new ParamChainImpl();

		ParamChainImpl p = base;
		Stack<ParamChainImpl> s = new Stack<ParamChainImpl>();
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
	public static ParamChainImpl generateUnion(ParamChainImpl base,
			String... keyList) {
		ParamChainImpl c = new ParamChainImpl();
		if (base != null) {
			for (int i = 0, n = keyList.length; i < n; i++) {
				String key = keyList[i];
				ParamChainImpl p = base.containsKey(key);
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
	public ParamChainImpl getParent() {
		return mParent;
	}

	@Override
	public ParamChain getRoot() {
		ParamChainImpl p = this;
		while (p.mParent != null)
			p = p.mParent;
		return p;
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
	public ParamChainImpl containsKey(String key) {
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
	public ParamChainImpl containsKeyReverse(String key) {
		if (mParent != null)
			return mParent.containsKey(key);
		if (containsKeyOwn(key) != null)
			return this;
		return null;
	}

	@Override
	public ParamChain grow() {
		return new ParamChainImpl(this);
	}

	@Override
	public ParamChain grow(String aliasName) {
		ParamChainImpl p = new ParamChainImpl(this);
		if (p != null)
			p.mAliasName = aliasName;
		return p;
	}

	@Override
	public ParamChain grow(HashMap<String, Object> data) {
		return new ParamChainImpl(this, data);
	}

	@Override
	public ParamChain generateUnion() {
		return generateUnion(this);
	}

	@Override
	public ParamChain generateUnion(String... keyList) {
		return generateUnion(this, keyList);
	}

	@Override
	public ParamChain getParent(String aliasName) {
		if (aliasName.equals(mAliasName))
			return this;
		if (mParent != null)
			return mParent.getParent(aliasName);
		return null;
	}

}