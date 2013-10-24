package com.zz.sdk.activity;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

import com.zz.sdk.BuildConfig;

/**
 * 参数链表、环境变量表
 * 
 * @author nxliao
 * @version v0.1.0.20131010
 */
public class ParamChainImpl implements ParamChain {
	private static final ParamChain GLOBAL_INSTANCE = new ParamChainImpl();

	static public ParamChain GLOBAL() {
		return GLOBAL_INSTANCE;
	}

	/** 本地变量 */
	private HashMap<String, Object> mData;
	/** 临时变量 */
	private HashMap<String, Object> mDataTmp;

	/** 上一级环境 */
	private ParamChain mParent;

	/** 别名 */
	private String mAliasName;

	/** 层级 */
	private int mLevel;

	public ParamChainImpl() {
		this(null);
	}

	public ParamChainImpl(ParamChain base) {
		this(base, null);
	}

	public ParamChainImpl(ParamChain base, HashMap<String, Object> data) {
		mParent = base;
		mLevel = base != null ? (base.getLevel() + 1) : 0;

		if (data == null) {
			mData = new HashMap<String, Object>(8);
		} else {
			mData = new HashMap<String, Object>(data);
		}

		mDataTmp = new HashMap<String, Object>();
	}

	private void dump_own(ParamChain accept, boolean force,
			HashMap<String, Object> data, ValType type) {
		for (Entry<String, Object> e : data.entrySet()) {
			String key = e.getKey();
			ValType t = accept.containsKeyOwn(key);
			if (force || t == null /* || (force && t != type) */) {
				// 强制或目标不存在
				if (t != null) {
					// 如果是强制且目标类型不匹配，则删除旧数据
					accept.remove(key);
				}
				accept.add(key, e.getValue(), type);
			}
		}
	}

	@Override
	public void dumpOwn(ParamChain accept, boolean force) {
		dump_own(accept, force, mData, ValType.NORMAL);
		dump_own(accept, force, mDataTmp, ValType.TEMPORARY);
	}

	/**
	 * 合并整个环境链为一级环境
	 * 
	 * @param base
	 * @return
	 */
	public static ParamChain generateUnion(ParamChain base) {
		ParamChainImpl c = new ParamChainImpl();

		ParamChain p = base;
		Stack<ParamChain> s = new Stack<ParamChain>();
		while (p != null) {
			s.push(p);
			p = p.getParent();
		}

		while (!s.isEmpty()) {
			p = s.pop();
			p.dumpOwn(c, true);
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
		ParamChain c = new ParamChainImpl();
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

	@Override
	public ParamChain getRoot() {
		ParamChain p = this;
		while (p.getParent() != null)
			p = p.getParent();
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
		ParamChain p = this;
		do {
			Object ret = p.getOwned(key);
			if (ret != null) {
				return ret;
			}
			p = p.getParent();
		} while (p != null);
		return null;
	}

	/**
	 * 获取指定类型的变量（所有级），示例
	 * <P>
	 * Double amount = env.get({@link KeyGlobal#K_PAY_AMOUNT}, {@link Double
	 * Double.class});
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
		ParamChain p = this;
		do {
			if (p.containsKeyOwn(key) != null)
				return p;
			p = p.getParent();
		} while (p != null);
		return null;
	}

	/**
	 * 是否存在变量，从父级开始检查
	 * 
	 * @param key
	 * @return 变量所在的实例，null表示不包含此变量
	 */
	public ParamChain containsKeyReverse(String key) {
		ParamChain p = this;
		Stack<ParamChain> s = new Stack<ParamChain>();
		while (p != null) {
			s.push(p);
			p = p.getParent();
		}

		while (!s.isEmpty()) {
			p = s.pop();
			if (p.containsKeyOwn(key) != null) {
				return p;
			}
		}
		return null;
	}

	@Override
	public ParamChain grow() {
		return new ParamChainImpl(this);
	}

	@Override
	public ParamChain grow(String aliasName) {
		ParamChain p = new ParamChainImpl(this);
		p.setAliasName(aliasName);
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
		if (aliasName != null) {
			ParamChain p = this;
			do {
				if (aliasName.equals(p.getAliasName())) {
					return p;
				}
				p = p.getParent();
			} while (p != null);
		} else {
			ParamChain p = this;
			do {
				if (aliasName == p.getAliasName()) {
					return p;
				}
				p = p.getParent();
			} while (p != null);
		}
		return null;
	}

	@Override
	public boolean setAliasName(String aliasName) {
		if (mAliasName != null)
			return false;
		mAliasName = aliasName;
		return true;
	}

	@Override
	public String getAliasName() {
		return mAliasName;
	}

}