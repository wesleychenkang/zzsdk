package com.zz.sdk.layout;

import android.content.Context;

import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.ValType;
import com.zz.sdk.entity.SMSChannelMessage;
import com.zz.sdk.layout.PaymentListLayout.KeyPaymentList;
import com.zz.sdk.util.Logger;

/**
 * 短信支付
 * <p/>
 * <b>输入:</b>
 * <ul>
 * <li>{@link KeyPaymentList#K_PAY_ORDERNUMBER}</li>
 * <li>{@link KeyPaymentList#K_PAY_AMOUNT}</li>
 * <li>{@link KeyPaymentList#K_PAY_SMS_CHANNELMESSAGE}</li>
 * <li>{@link KeyPaymentList#K_PAY_SMS_CONFIRM_ENABLED}</li>
 * </ul>
 * <p/>
 * <b>输出:</b>
 * <ul>
 * <li>{@link KeyPaymentList#K_PAY_RESULT}</li>
 * </ul>
 * <p/>
 * <b>依赖:</b>
 * <ul>
 * </ul>
 * <p/>
 * 
 * @author nxliao
 * 
 */
class PaymentSMSLayout extends CCBaseLayout {
//"对不起，查询余额失败，请先确认您选择的地区以及运营商信息是否正确，以及请确认您的卡是否已欠费或已失效，如需帮助请联系客服!");
//
//
//"对不起，话费支付失败！请确认您的网络是否正常后再尝试，如需帮助请联系客服!"
//
//"对不起，网络连接失败，请确认您的网络是否正常后再尝试，如需帮助请联系客服!"
	private int mPayResultState;
	/** 支付金额，单位「分」，便于精确比较 */
	private int mAmount;
	private SMSChannelMessage mSmsChannelMessage;
	private SMSChannelMessage[] mSmsChannelMessages;

	public PaymentSMSLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
	}

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
		super.onInitEnv(ctx, env);

		mPayResultState = MSG_STATUS.EXIT_SDK;

		mSmsChannelMessage = null;
		mSmsChannelMessages = env.get(KeyPaymentList.K_PAY_SMS_CHANNELMESSAGE,
				SMSChannelMessage[].class);

		Float amount = env.get(KeyPaymentList.K_PAY_AMOUNT, Float.class);
		mAmount = amount == null ? 0 : (int) (amount * 100);

		if (mSmsChannelMessages != null) {

			if (DEBUG) {
				for (SMSChannelMessage m : mSmsChannelMessages) {
					Logger.d("D: SMS 交易额：" + m.price);
				}
			}

			// TODO:
			Boolean confirmEnabled = env.get(
					KeyPaymentList.K_PAY_SMS_CONFIRM_ENABLED, Boolean.class);
			if (confirmEnabled == null || !confirmEnabled) {
				for (SMSChannelMessage m : mSmsChannelMessages) {
					m.isBlockPrompt = "1";
				}
			}

			if (mAmount > 0) {
				// 定额交易
				for (SMSChannelMessage m : mSmsChannelMessages) {
					if ((int) m.price == mAmount) {
						if (DEBUG) {
							Logger.d("D: SMS 匹配到固定金额: " + m.toString());
						}
						mSmsChannelMessage = m;
						break;
					}
				}

				if (mSmsChannelMessage == null) {
					if (DEBUG) {
						Logger.d("D: SMS 定额交易 无匹配!");
					}
					String tip = "该充值方式，没有您选择的商品金额，请选择其他方式！";
				}

				// 定额交易，无需总表
				mSmsChannelMessages = null;
			}
		} else {
			String tip = "获取不到支付通道，请选择其他方式";
		}
	}

	@Override
	protected void onInitUI(Context ctx) {
	}

	@Override
	public boolean onExit() {

		if (mPayResultState != MSG_STATUS.EXIT_SDK) {
			// XXX: 记录此次充值结果在上级环境中
			getEnv().getParent(PaymentListLayout.class.getName()).add(
					KeyPaymentList.K_PAY_RESULT, mPayResultState,
					ValType.TEMPORARY);
		}

		boolean ret = super.onExit();
		if (ret) {

		}
		return ret;
	}

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		if (!ret)
			return false;

		mPayResultState = MSG_STATUS.CANCEL;

		return ret;
	}
}
