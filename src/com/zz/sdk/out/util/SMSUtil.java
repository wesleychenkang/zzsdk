package com.zz.sdk.out.util;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.Result;
import com.zz.sdk.entity.SMSChannelMessage;
import com.zz.sdk.out.activity.SmsSendReceiver;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.Utils;

public class SMSUtil {

	public static final String EXTRA_SERVICE_TYPE = "service_type";
	public static final String EXTRA_PRICE = "price";
	public static final int TITLE_COLOR = 0xfffbcf4b;
	public static final int BODY_COLOR = 0xfffffbcc;

	public static final String XML_COMMAND = "sms_conmmand";
	public static final String IMSI = "imsi";
	public static final String PHONEINFO = "phoneinfo";
	public static final String AMOUNT = "amount";

	private static CustomDialog mDialog;

	// Handler mHandler = new Handler() {
	// public void handleMessage(Message msg) {
	// hideDialog();
	// }
	// };

	private static class CustomDialog extends Dialog {

		private CustomDialog dialog;
		private Activity context;
		private int type; // 1：查询， 2充值 3 ,连接
		private TextView tv;
		private DialogInterface.OnCancelListener listener = new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (1 == type) {
					DialogUtil
							.showDialogErr(context,
									"对不起，查询余额失败，请先确认您选择的地区以及运营商信息是否正确，以及请确认您的卡是否已欠费或已失效，如需帮助请联系客服!");
				} else if (2 == type) {
					Utils.toastInfo(context,
							"对不起，话费支付失败！请确认您的网络是否正常后再尝试，如需帮助请联系客服!");
				} else if (3 == type) {
					Utils.toastInfo(context,
							"对不起，网络连接失败，请确认您的网络是否正常后再尝试，如需帮助请联系客服!");
				}
			}
		};

		private CustomDialog(Activity context, String text, int type) {
			super(context);
			this.context = context;
			this.type = type;
			dialog = this;

			getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			requestWindowFeature(Window.FEATURE_NO_TITLE);

			LinearLayout layout = new LinearLayout(context);
			layout.setGravity(Gravity.CENTER_VERTICAL);
			layout.setBackgroundDrawable(BitmapCache.getDrawable(context,
					Constants.ASSETS_RES_PATH + "sms_wait_bg.png"));
			int p = DimensionUtil.dip2px(context, 20);
			layout.setPadding(p, p, p, p);

			ProgressBar progressBar = new ProgressBar(context);
			progressBar.setInterpolator(context,
					android.R.anim.linear_interpolator);
			layout.addView(progressBar);

			tv = new TextView(context);
			tv.setTextColor(Color.WHITE);
			tv.setTextSize(16);
			if (type == 2) {
				tv.setText("正在为您充值，请稍候...");
			} else {
				tv.setText(text);
			}
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
			lp.leftMargin = DimensionUtil.dip2px(context, 15);
			layout.addView(tv, lp);
			setContentView(layout);
			setOnCancelListener(listener);
			setCanceledOnTouchOutside(false);
		}

		@Override
		public void show() {
			super.show();
			// 20秒超时去进度框
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				public void run() {
					if (null != dialog && dialog.isShowing()) {
						dialog.cancel();
					}
				}
			}, 90 * 1000);
		}

		@Override
		public void onBackPressed() {
		}
	}

	public static void setTextSMS() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.tv
					.setText("正在查询话费余额，这个过程可能需要花费20秒至1分钟左右，请您耐心等候！查询过程中请勿进行任何其它操作！谢谢！");
		}
	}

	public static void hideDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	public static void showDialog(Activity context, int type) {
		mDialog = new CustomDialog(context, "正在查询，请稍候...", type);
		mDialog.show();
	}

	// 獲取查詢余額指令
	public static void getSMSCheckCommand(final Context ctx,
			final PayParam payParam, final Handler handler, final int what) {
		new Thread() {
			public void run() {
				GetDataImpl instance = GetDataImpl.getInstance(ctx);
				Result result = instance.charge(PayChannel.PAY_TYPE_KKFUNPAY, payParam);
				Message msg = handler.obtainMessage(what);
				msg.obj = result;
				msg.sendToTarget();
			};
		}.start();
	}

	// 發送信息查詢余額
	// public static void CheckAmount(Context context,
	// SMSCheckCommandInfo checkCommandInfo) {
	// Intent intent = new Intent();
	// SmsManager smsManager = SmsManager.getDefault();
	// intent.setAction(DouwanSdkReceiver.ACTION_CHECK);
	// // Bundle bundle = new Bundle();
	// // bundle.putInt("type", 1);
	// // intent.putExtras(bundle);
	// PendingIntent sentIntent = PendingIntent.getBroadcast(context, 1,
	// intent, PendingIntent.FLAG_UPDATE_CURRENT);
	// try {
	// smsManager.sendTextMessage(checkCommandInfo.sendToAddress, null,
	// checkCommandInfo.command, sentIntent, null);
	// } catch (Exception e) {
	// hideDialog();
	// Utils.toastInfo(context,"你已取消短信查余额！");
	// }
	// }

	// public static void getSmsChannel(final Context ctx, final Charge charge,
	// final Handler handler, final int what) {
	// mDialog = new CustomDialog(ctx, "正在查询充值通道，请稍候...",);
	// mDialog.show();
	// new Thread() {
	// public void run() {
	// GetDataImpl instance = GetDataImpl.getInstance(ctx);
	// Result result = instance.charge(charge);
	// Message msg = handler.obtainMessage(what);
	// msg.obj = result;
	// msg.sendToTarget();
	// };
	// }.start();
	// }

	public static boolean sendMessage(Activity ctx, SMSChannelMessage msg) {
		mDialog = new CustomDialog(ctx, "正在为您充值，请稍候...", 2);
		mDialog.show();
		Logger.d("sms body length -> " + msg.command.length());
		Logger.d("sms body -> " + msg.command);

		SmsManager smsManager = SmsManager.getDefault();
		Intent intent = new Intent();
		intent.setAction(SmsSendReceiver.ACTION);
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_SERVICE_TYPE, msg.serviceType);
		bundle.putString(EXTRA_PRICE, "" + msg.price);
		ArrayList<String> divideMessage = smsManager.divideMessage(msg.command);
		Logger.d("divide size -> " + divideMessage.size());
		intent.putExtras(bundle);
		PendingIntent sentIntent = PendingIntent.getBroadcast(ctx, 1, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
		sentIntents.add(sentIntent);

		try {
			smsManager.sendMultipartTextMessage(msg.sendToAddress, null,
					divideMessage, sentIntents, null);
			return true;
		} catch (Exception e) {
			hideDialog();
			Utils.toastInfo(ctx, "你已取消话费充值！");
			return false;
		}
	}

	public static String charge(final Context ctx, final PayParam payParam, final int what) {
		new ChargeTask(ctx, payParam, what).start();
		return null;
	}

	private static class ChargeTask extends Thread {
		Context ctx;
		PayParam payParam;
		int what;
		ChargeTask(final Context ctx, final PayParam payParam, final int what) {
			this.ctx = ctx;
			this.payParam = payParam;
			this.what = what;
		}

		@Override
		public void run() {
			/*
			 * loginName
				cmgeOrderNum
				imsi
				serviceType
				status
				dueFee
				/pkkfunnt.lg

			 */
			GetDataImpl getDataImpl = GetDataImpl.getInstance(ctx);
			//Message msg = handler.obtainMessage(what);
			Result result = getDataImpl.charge(PayChannel.PAY_TYPE_KKFUNPAY_EX, payParam);
//			Logger.d("result -> " + result);
//			msg.obj = result;
//			msg.sendToTarget();
		}
	}

	/**
	 * 
	 * @param s
	 *            返回的查詢余額信息
	 * @param ereg
	 *            截取金額正則表達式
	 * @return 基本余額
	 */
	public static String findAmount(String s, String ereg) {

		// Logger.d("s--->" + s);
		Logger.d("ereg--->" + ereg);

		Pattern pattern = Pattern.compile(ereg);
		Matcher matcher = pattern.matcher(s);
		boolean boo = matcher.find();
		if (boo) {
			return getAmount(matcher.group());
		}

		return "";
	}

	private static String getAmount(String s) {
		String rege = "\\d+\\.*\\d*";
		Pattern pattern = Pattern.compile(rege);
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			return matcher.group();
		}

		return "";
	}

	// public static SmsInfo getSmsInfo(Activity activity) {
	//
	// try {
	// Uri uri = Uri.parse("content://sms/inbox");
	// String[] projection = new String[] { "_id", "address", "person",
	// "body", "date", "type" };
	// Cursor cusor = activity.getContentResolver().query(uri, projection, null,
	// null,
	// "date desc");
	// int nameColumn = cusor.getColumnIndex("person");
	// int phoneNumberColumn = cusor.getColumnIndex("address");
	// int smsbodyColumn = cusor.getColumnIndex("body");
	// int dateColumn = cusor.getColumnIndex("date");
	// int typeColumn = cusor.getColumnIndex("type");
	// SmsInfo smsinfo = null;
	// if (cusor != null) {
	// cusor.moveToNext();
	// smsinfo = new SmsInfo();
	// smsinfo.name = cusor.getString(nameColumn);
	// smsinfo.date = cusor.getString(dateColumn);
	// smsinfo.phoneNumber = cusor.getString(phoneNumberColumn);
	// smsinfo.smsbody = cusor.getString(smsbodyColumn);
	// smsinfo.type = cusor.getString(typeColumn);
	// }
	// cusor.close();
	// return smsinfo;
	// } catch (Exception e) {
	//
	// return null;
	// }
	// }
	//
	// public static void writeImsiAndProAndCityAndCar2xml(Context context,
	// String imsi, String json) {
	//
	// SharedPreferences prefs = context.getSharedPreferences(XML_COMMAND,
	// Context.MODE_PRIVATE);
	// Editor edit = prefs.edit();
	// edit.putString(IMSI, Encrypt.encode(imsi));
	// edit.putString(PHONEINFO, Encrypt.encode(json));
	// // edit.putString(AMOUNT, amount);
	// edit.commit();
	// }

	// public static void clearImsiAndProAndCityAndCar2xml(Context context) {
	// SharedPreferences prefs = context.getSharedPreferences(XML_COMMAND,
	// Context.MODE_PRIVATE);
	// prefs.edit().clear().commit();
	// }
}
