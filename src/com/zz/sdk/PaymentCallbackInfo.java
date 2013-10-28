package com.zz.sdk;

/**
 * 充值回调信息
 * <p>
 * <b><font size=4 >说明</font></b>：
 * <ul>
 * <li>对于如充值卡等依赖<font size=4
 * color="#c02040">第三方充值平台</font>的充值方式，SDK并不能实时判断此次交易是否有效。</li>
 * <li>SDK告诉客户端的 {@link #statusCode} 只作为参考，SDK会返回游戏 {@link #cmgeOrderNumber}
 * ，<font color="#00a000">拿这个到游戏服务器去确认一次</font>，这个结果才对。
 * <li>对于单机游戏没有服务器的，请注意相关<font size=4 color="#ff0000">漏单</font>风险。</li>
 * </ul>
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

	/** 金额，单位：元，精度 0.01 */
	public String amount;

	/** CMGE订单号 */
	public String cmgeOrderNumber;

	/** 充值方式：ID，-1表示无效 */
	public int payWayType;

	/** 充值方式：名称， null 表示无效 */
	public String payWayName;

	/** 充值币种，如 "RMB" "ZYCOIN" 等 */
	public String currency;

	@Override
	public String toString() {
		return "PaymentCallbackInfo [statusCode=" + statusCode + ", amount="
				+ amount + ", cmgeOrderNumber=" + cmgeOrderNumber + ", payWay="
				+ payWayName + "(" + payWayType + ")" + ", currency="
				+ currency + "]";
	}
}
