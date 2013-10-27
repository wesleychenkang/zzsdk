package com.zz.sdk.out.util;

/**
 * (常规)状态控制
 * 
 * @author nxliao
 * @version 0.1.0.20130726
 */
public final class FlagControl {

	/** 常规状态值 */
	private int mFlag = 0;

	public FlagControl() {
		reset();
	}

	/** 重置状态 */
	public final void reset() {
		mFlag = 0;
	}

	/** 直接标记状态 */
	public final synchronized void mark(int flag) {
		mFlag |= flag;
	}

	/** 清除状态 */
	public final synchronized void clear(int mask) {
		mFlag &= ~mask;
	}

	/** 检查状态，如果有标记，则清除并返回 true，否则返回 false */
	public final synchronized boolean getAndClear(int mask) {
		if ((mFlag & mask) != 0) {
			mFlag &= ~mask;
			return true;
		}
		return false;
	}

	/** 设置状态 */
	public final synchronized void set(int mask, int flag) {
		mFlag = (mFlag & (~mask)) | (flag & mask);
	}

	/** 获取状态 */
	public final synchronized int get(int mask) {
		return mFlag & mask;
	}

	/** 是否有状态 */
	public final synchronized boolean has(int flag) {
		return (mFlag & flag) != 0;
	}

}
