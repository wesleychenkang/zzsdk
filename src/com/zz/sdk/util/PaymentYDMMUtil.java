package com.zz.sdk.util;

import android.app.Activity;
import android.os.Handler;

import com.zz.sdk.IPayConfYDMM;
import com.zz.sdk.ZZSDKConfig;

import java.util.HashMap;

/**
 * 移动ＭＭ支付操作
 *
 * @author nxliao
 * @version 0.1.0.20131206
 */
public class PaymentYDMMUtil {
	static final int __MSG_USER__ = 2013;
	public static final int MSG_INIT_START = __MSG_USER__ + 1;
	public static final int MSG_INIT_FINISH = __MSG_USER__ + 2;
	public static final int MSG_BILLING_START = __MSG_USER__ + 3;
	public static final int MSG_BILLING_FINISH = __MSG_USER__ + 4;

	public static class ResultData {
		// 此次订购的orderID
		public String orderID;
		// 商品的paycode
		public String paycode;
		// 商品的有效期(仅租赁类型商品有效)
		public String leftday;
		// 商品的交易 ID，用户可以根据这个交易ID，查询商品是否已经交易
		public String tradeID;
		public String ordertype;

		private ResultData() {

		}

		private static ResultData fromPay(HashMap<String, String> data) {
			if (ZZSDKConfig.SUPPORT_YDMM) {
				if (data != null) {
					ResultData ret = new ResultData();
					ret.orderID = data == null ? null : data.get(mm.purchasesdk.OnPurchaseListener.ORDERID);
					ret.paycode = data == null ? null : data.get(mm.purchasesdk.OnPurchaseListener.PAYCODE);
					ret.leftday = data == null ? null : data.get(mm.purchasesdk.OnPurchaseListener.LEFTDAY);
					ret.tradeID = data == null ? null : data.get(mm.purchasesdk.OnPurchaseListener.TRADEID);
					ret.ordertype = data == null ? null : data.get(mm.purchasesdk.OnPurchaseListener.ORDERTYPE);
					return ret;
				}
			}
			return null;
		}
	}

	private static IPayConfYDMM sConf;
	private static String sIMSI;

	public static boolean isValid() {
		return sConf != null && sConf.isValid() && checkIMSI_yidong(sIMSI);
	}

	private static boolean checkIMSI_yidong(String imsi) {
		return imsi != null && (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007"));
	}

	public static void setIMSI(String imsi) {
		sIMSI = imsi;
	}

	public static void setsConf(IPayConfYDMM conf) {
		sConf = conf;
	}

	public static IPayConfYDMM getsConf() {
		return sConf;
	}

	public static String getAppID() {
		return sConf == null ? null : sConf.getAppID(); // 300007704659
	}

	public static String getAppKey() {
		return sConf == null ? null : sConf.getAppKey();
	}

	public static String getPayCode(double price) {
		return sConf == null ? null : sConf.getPayCode(price); // 300007704659
	}

	public static boolean isInitOK(int code) {
		if (ZZSDKConfig.SUPPORT_YDMM) {
			return code == mm.purchasesdk.PurchaseCode.INIT_OK;
		}
		return false;
	}

	public static boolean isOrderOK(int code) {
		if (ZZSDKConfig.SUPPORT_YDMM) {
			return (code == mm.purchasesdk.PurchaseCode.ORDER_OK) || (code == mm.purchasesdk.PurchaseCode.AUTH_OK);
		}
		return false;
	}

	public static boolean isOrderCancel(int code) {
		if (ZZSDKConfig.SUPPORT_YDMM) {
			return (code == mm.purchasesdk.PurchaseCode.BILL_CANCEL_FAIL);
		}
		return false;
	}

	/** 初始化支付管理器，如果出现错误，则返回null */
	public static Object initPurchase(Activity activity, Object listener) {
		if (ZZSDKConfig.SUPPORT_YDMM) {
			try {
				mm.purchasesdk.Purchase purchase = mm.purchasesdk.Purchase.getInstance();
				purchase.setAppInfo(getAppID(), getAppKey()); // 设置计费应用 ID 和 Key (必须)
				purchase.setTimeout(10000, 10000);  // 设置超时时间(可选)，可不设置，缺省都是 10s
				purchase.init(activity, (mm.purchasesdk.OnPurchaseListener) listener); //初始化，传入监听器
				return purchase;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static boolean tryOrder(
			Activity activity, Object purchase, String payCode, String orderNumber, Object listener) {
		if (ZZSDKConfig.SUPPORT_YDMM) {
			if ((purchase instanceof mm.purchasesdk.Purchase) && (listener instanceof mm.purchasesdk.OnPurchaseListener)) {
				try {
					((mm.purchasesdk.Purchase) purchase).order(activity, payCode, 1, orderNumber, true, (mm.purchasesdk.OnPurchaseListener) listener);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/** 获取事件监听器 */
	public static Object genPurchaseListener(final Handler handler) {
		if (ZZSDKConfig.SUPPORT_YDMM) {
			return new mm.purchasesdk.OnPurchaseListener() {
				private Handler mHandler = handler;

				@Override
				public void onAfterApply() {

				}

				@Override
				public void onAfterDownload() {

				}

				@Override
				public void onBeforeApply() {

				}

				@Override
				public void onBeforeDownload() {

				}

				@Override
				public void onInitFinish(int code) {
					mHandler.obtainMessage(PaymentYDMMUtil.MSG_INIT_FINISH, code, 0).sendToTarget();
				}

				@Override
				public void onBillingFinish(int code, HashMap arg1) {
					mHandler.obtainMessage(PaymentYDMMUtil.MSG_BILLING_FINISH, code, 0, ResultData.fromPay(arg1)).sendToTarget();
				}

				@Override
				public void onQueryFinish(int code, HashMap arg1) {
					if (code == mm.purchasesdk.PurchaseCode.QUERY_OK) {
					}
				}

				/** 退订结果 */
				@Override
				public void onUnsubscribeFinish(int code) {
					//			String result = "退订结果：" + Purchase.getReason(code);
					//			System.out.println(result);
				}
			};
		}
		return null;
	}
}
