package com.zz.sdk;

import com.zz.sdk.entity.PayChannel;

/**
 * 定制SDK的接口
 */
public class SDKDIY {

	private static int sPaySeq_Top = -1;

	/**
	 * 设置将话费作为默认支付方式
	 *
	 * @param enabled 是否将话费支付置顶。true表示开启置顶，false表示以服务器的默认顺序展示支付方式。
	 */
	public static void setPaySequence_CallCharge(boolean enabled) {
		if (enabled) sPaySeq_Top = PayChannel.PAY_TYPE_KKFUNPAY;
		else sPaySeq_Top = -1;
	}

	/**
	 * 获取指定的首先支付方式
	 *
	 * @return -1表示无设置。>=0表示所指定的方式
	 */
	public static int getPaySequenceTop() {
		return sPaySeq_Top;
	}

}
