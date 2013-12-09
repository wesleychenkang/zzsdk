package com.zz.sdk;

/** 支付配置：移动M-Market */
public interface IPayConfYDMM {
	/** 是否可用 */
	public boolean isValid();

	/**
	 * 由价格获取对应的商品号
	 *
	 * @param price 价格，单位：元
	 * @return 在移动M-Market上定义的道具ID，null表示没有对应的道具
	 */
	public String getPayCode(double price);

	/** MM上分配的应用ID */
	public String getAppID();

	/** MM上分配的应用Key */
	public String getAppKey();
}
