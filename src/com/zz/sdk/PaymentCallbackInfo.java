package com.zz.sdk;

/**
 * 充值回调
 * 
 * @author nxliao
 * 
 */
public class PaymentCallbackInfo {
	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_FAILURE = -1;
	public static final int STATUS_CANCEL = -2;

	/** 状态 */
	public int statusCode;
	/** 金额 */
	public int amount;

	@Override
	public String toString() {
		return "PaymentCallbackInfo [statusCode=" + statusCode + ", amount="
				+ amount + "]";
	}
}
