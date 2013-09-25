package com.zz.sdk.util;

import java.io.File;
import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.widget.TextView;

import com.zz.sdk.entity.PayChannel;

public class ResConstants {

	private static Context mContext;
	private static float mDensity = 1.0f;

	/** 初始化环境配置，如分辨率等 */
	public static void init(Context ctx) {
		mContext = null;
		mDensity = ctx.getResources().getDisplayMetrics().density;
	}

	public static void clean() {
		mContext = null;
	}

	public static int dip2px(int dpValue) {
		return (int) (dpValue * mDensity + 0.5f);
	}

	public static int px2dip(int pxValue) {
		return (int) (pxValue / mDensity + 0.5f);
	}

	public static enum ZZStr {
		CC_BALANCE_DESC("您的卓越币余额是："), //
		/** 余额显示，使用 {@link DecimalFormat} 转换 */
		CC_BALANCE_UNIT("##"), //
		CC_RECHAGRE_COUNT_DESC("充值数量"), //
		/** 充值中心（游戏购买入口） */
		CC_RECHAGRE_PRICE_DESC("道具价格"), //
		CC_RECHAGRE_COUNT_HINT("请输入数量"), //
		CC_RECHAGRE_RATE_DESC("(1元=10卓越币)"), //
		CC_RECHAGRE_COST_DESC("应付金额："), //
		/** 充值金额，使用 {@link DecimalFormat} 转换 */
		CC_RECHAGRE_COST_UNIT("##.##元"), //
		CC_PAYCHANNEL_DESC("支付方式"), //
		CC_CARDNUM_DESC("请输入卡号"), //
		CC_PASSWD_DESC("请输入密码"), //
		/** 联通卡(15:19) 移动卡(17:18) */
		CC_CARDNUM_HINT("请输入卡号（%d位）"), //
		CC_PASSWD_HINT("请输入卡号（%d位）"), //
		CC_PAYTYPE_DESC("请点击确认充值，进入到%s充值界面"), //
		CC_PAYTYPE_COIN_DESC("确认后，将扣除%d卓越币，您的余额为%d"), //
		CC_COMMIT_RECHARGE("确认充值"), //
		CC_COMMIT_BUY("确认购买"), //
		CC_COMMIT_EXCHANGE("确认兑换"), //
		CC_PAYCHANNEL_ERROR("很抱歉！未能获取到可用的支付通道。"), //
		CC_HELP_TITLE("帮助说明"), //
		CC_HELP_TEL("客服电话: 0123-45678901"), //
		CC_RECHARGE_LIST_NONE("不能显示候选列表"), //
		;

		private String context;

		private ZZStr(String txt) {
			context = txt;
		}

		@Override
		public String toString() {
			return context;
		}
	}

	/**
	 * 配置
	 */
	public static class Config {

		/**
		 * 字体颜色配置
		 */
		public static enum ZZFontColor {
			/** 充值界面·普通文本 */
			CC_RECHAGR_NORMAL(Color.BLACK),
			/** 充值界面输入文本 */
			CC_RECHAGR_INPUT(Color.DKGRAY),
			/** 充值界面·帮助按钮文本 */
			CC_HELP(Color.LTGRAY),
			/** 充值界面·应付金额文本 */
			CC_RECHAGRE_COST(0xffccaa00),
			/** 充值界面·支付方式子项文本 */
			CC_PAYTYPE_ITEM(Color.BLACK),
			/** 充值界面·确认充值按钮 文本 */
			CC_RECHARGE_COMMIT(Color.WHITE), ;

			private int c;

			private ZZFontColor(int c) {
				this.c = c;
			}

			public int toColor() {
				return c;
			}
		}

		/**
		 * 字体大小配置
		 */
		public static enum ZZFontSize {
			/** 充值界面普通文本 */
			CC_RECHAGR_NORMAL(16),
			/** 余额文本 */
			CC_RECHAGR_BALANCE(22),
			/** 充值界面输入文本 */
			CC_RECHAGR_INPUT(12),
			/** 充值界面应付金额文本 */
			CC_RECHAGR_COST(20),
			/** 充值界面·支付方式子项文本 */
			CC_PAYTYPE_ITEM(14),
			/** 充值界面·确认充值按钮 文本 */
			CC_RECHARGE_COMMIT(20), ;

			private float size;

			private ZZFontSize(float s) {
				size = s;
			}

			public void apply(TextView tv) {
				tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
			}
		}

		/**
		 * 尺寸配置, DIP
		 */
		public static enum ZZDimen {
			/** 充值界面·各面板垂直方向间隔 */
			CC_SAPCE_PANEL_V(16),

			/** 充值界面·GridView 的单元格间距 */
			CC_GRIDVIEW_SPACE_H(8), CC_GRIDVIEW_SPACE_V(4),
			/** 充值界面·GridView 的单元格子项的边距 */
			CC_GRIDVIEW_ITEM_PADDDING_LEFT(16), //
			CC_GRIDVIEW_ITEM_PADDDING_TOP(12), //
			CC_GRIDVIEW_ITEM_PADDDING_RIGHT(16), //
			CC_GRIDVIEW_ITEM_PADDDING_BOTTOM(12),

			/** 充值界面·GridView 单元格大小 */
			CC_GRIDVIEW_COLUMN_WIDTH(96), ;

			private float dimen;

			private ZZDimen(float d) {
				dimen = d;
			}

			/** pixels */
			public int toPx() {
				return (int) (dimen * mDensity + 0.5f);
			}
		}
	}

	public static enum CCImg {
		BACKGROUND("cc_background.9.png"), //
		BUTTON("cc_button.9.png"), //
		BUTTON_CLICK("cc_button_click.9.png"), //
		BUY_BUTTON("cc_buy_button.png"), //
		BUY_BUTTON_CLICK("cc_buy_button_click.png"), //
		CHARGE_PULL("cc_charge_pull.png"), //
		EX_BUTTON("cc_ex_button.png"), //
		EX_BUTTON_CLICK("cc_ex_button_click.png"), //
		EX_RIGHT("cc_ex_Right.png"), //
		EX_RIGHT_CLICK("cc_ex_Right_click.png"), //
		HELP("cc_help.png"), //
		MONEY("cc_money.png"), //
		PAYMENT_INPUT("cc_payment_input.9.png"), //
		RECHARGE_BAN("cc_recharge_ban.png"), //
		RECHARGE_INPUT("cc_recharge_input.9.png"), //
		TUP_CFT("cc_tup_cft.png"), //
		TUP_DX("cc_tup_dx.png"), //
		TUP_LT("cc_tup_lt.png"), //
		TUP_SJDX("cc_tup_sjdx.png"), //
		TUP_YD("cc_tup_yd.png"), //
		TUP_YL("cc_tup_yl.png"), //
		TUP_ZFB("cc_tup_zfb.png"), //
		TUP_ZYB("cc_tup_zyb.png"), //
		ZF_WXZ("cc_zf_wxz.9.png"), //
		ZF_XZ("cc_zf_xz.9.png"), //
		TITLE_BACK_DEFAULT("title_back_default.png"), //
		TITLE_BACK_PRESSED("title_back_pressed.png"), //
		TITLE_BG("title_bg.png"), //
		TITLE_EXIT_DEFAULT("title_exit_default.png"), //
		TITLE_EXIT_PRESSED("title_exit_pressed.png");

		private String context;

		private String getContext() {
			return this.context;
		}

		private CCImg(String context) {
			this.context = context;
		}

		public String toString() {
			// 覆盖了父类Enum的toString()
			// return Constants.ASSETS_RES_PATH + File.separator + PATH
			// + File.separator + context;
			return context;
		}

		public Drawable getDrawble(Context ctx) {
			String path = Constants.ASSETS_RES_PATH + PATH + File.separator
					+ context;
			if (context.endsWith(".9.png")) {
				return BitmapCache.getNinePatchDrawable(ctx, path);
			}
			return BitmapCache.getDrawable(ctx, path);
		}

		public static StateListDrawable getStateListDrawable(Context ctx,
				CCImg picNormal, CCImg picPressed) {
			Drawable dn = picNormal.getDrawble(ctx);
			Drawable dp = picPressed.getDrawble(ctx);
			return BitmapCache.getStateListDrawable(ctx, dp, dn);
		}

		/**
		 * 获取 支付方式 的图标
		 * 
		 * @param type
		 * @return
		 */
		public static CCImg getPaychannelIcon(int payType) {
			CCImg ret;
			switch (payType) {
			case PayChannel.PAY_TYPE_ALIPAY:
				ret = CCImg.TUP_ZFB;
				break;
			case PayChannel.PAY_TYPE_KKFUNPAY:
				ret = CCImg.TUP_SJDX;
				break;
			case PayChannel.PAY_TYPE_TENPAY:
				ret = CCImg.TUP_CFT;
				break;
			case PayChannel.PAY_TYPE_UNMPAY:
				ret = CCImg.TUP_YL;
				break;
			case PayChannel.PAY_TYPE_YEEPAY_LT:
				ret = CCImg.TUP_LT;
				break;
			case PayChannel.PAY_TYPE_YEEPAY_YD:
				ret = CCImg.TUP_YD;
				break;
			default:
				// 卓越币
				ret = CCImg.TUP_ZYB;
				break;
			}
			return ret;
		}

		private static final String PATH = "drawable";
	}
}
