package com.zz.sdk;

/**
 * 充值回调信息
 * 
 * @author nxliao
 * 
 */
public class PaymentCallbackInfo {
	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_FAILURE = -1;
	public static final int STATUS_CANCEL = -2;
	public static final int STATUS_CANBEL = -3;

	/** 状态 */
	public int statusCode;

	/** 金额，单位：元 */
	public String amount;

	/** CMGE订单号 */
	public String cmgeOrderNumber;

	@Override
	public String toString() {
		return "PaymentCallbackInfo [statusCode=" + statusCode + ", amount="
				+ amount + ", cmgeOrderNumber=" + cmgeOrderNumber + "]";
	}
}
