package com.zz.sdk.util;

import java.io.File;
import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Bitmap;
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
		/** 价格或卓越币数的表达规则, {@link DecimalFormat} */
		CC_PRICE_FORMAT("##.##"), //

		CC_BALANCE_TITLE("您的卓越币余额是："), //
		/** 余额显示，基于 {@link #CC_PRICE_FORMAT} */
		CC_BALANCE_UNIT("%s"), //

		/** 充值中心 */
		CC_RECHARGE_TITLE("充值中心"), CC_RECHARGE_TITLE_SOCIAL("充值中心(社交)"),

		CC_RECHARGE_COUNT_TITLE("充值数量"), //
		/** 充值中心（游戏购买入口） */
		CC_RECHARGE_COUNT_TITLE_PRICE("道具价格"), //
		CC_RECHAGRE_COUNT_HINT("请输入数量"), //
		CC_RECHAGRE_COUNT_DESC("卓越币"), //
		/** RMB与卓越币的兑换比例，如 (1元=%s卓越币)，基于 {@link #CC_PRICE_FORMAT} */
		CC_RECHAGRE_RATE_DESC("(1元=%s卓越币)"), //
		CC_RECHAGRE_COST_DESC("应付金额："), //
		/** 充值金额，基于 {@link #CC_PRICE_FORMAT} */
		CC_RECHAGRE_COST_UNIT("%s元"), //

		/** 充值数量候选列表 %s个，基于 {@link #CC_PRICE_FORMAT} */
		CC_RECHAGRE_CANDIDATE_UNIT("%s个"), //

		/** 正在与服务器通信 */
		CC_TRY_CONNECT_SERVER("正在与服务器通信..."),

		/** 连接被取消！ */
		CC_TRY_CONNECT_SERVER_CANCELD("连接被取消！"),

		/** 连接服务器失败，请稍候重试！ */
		CC_TRY_CONNECT_SERVER_FAILED("连接服务器失败，请稍候重试！"),

		/** 连接服务器超时，请继续等待或稍候重试！ */
		CC_TRY_CONNECT_SERVER_TIMEOUT("连接服务器超时，请继续等待或稍候重试！"),

		/** 如需中止操作，请立即再次按下[返回] */
		CC_EXIT_LOCKED_TIP("如需中止操作，请立即再次按下[返回]"), //

		/** 等待充值结果…… */
		CC_RECHARGE_WAIT_RESULT("等待充值结果……"), //

		/** 充值结果：充值正在进行中，请稍后在游戏中查看 */
		CC_RECHARGE_RESULT_SUCCESS(
				"充值正在进行中，请稍后在游戏中查看，一般1-10分钟到账，如未到账，请联系客服。祝您游戏愉快！"),

		/** 充值结果：充值操作被取消 */
		CC_RECHARGE_RESULT_CANCEL("充值操作被取消！"),

		/** 充值结果：充值未到账 */
		CC_RECHARGE_RESULT_FAILED("充值未到账！请立即联系客服解决问题。祝您游戏愉快！"),

		/** 对不起，手机没有插入SIM卡，无法使用话费支付，请选择其它支付方式，如需帮助请联系客服! */
		CC_TRY_SMS_NO_IMSI("对不起，手机没有插入SIM卡，无法使用话费支付，请选择其它支付方式，如需帮助请联系客服!"),
		/** 获取不到支付通道，请选择其他方式 */
		CC_TRY_SMS_NO_CHANNEL("获取不到支付通道，请选择其他方式!"),
		/** 该充值方式，没有您选择的商品金额，请选择其他方式！ */
		CC_TRY_SMS_NO_MATCH("该充值方式，没有您选择的商品金额，请选择其他方式！"),
		/** 对不起，话费支付失败！请确认您的卡是否已欠费或已失效，如需帮助请联系客服! */
		CC_TRY_SMS_FAILED("对不起，话费支付失败！请确认您的卡是否已欠费或已失效，如需帮助请联系客服!"),
		/** 请选择充值金额： */
		CC_TRY_SMS_CHOOSE_TITILE("请选择充值金额："),
		/** 您将使用%s公司提供的%s业务进行代支付,资费是%s元，您将收到相关的短信提示，请注意查收！ */
		CC_TRY_SMS_PROMPT_HTML(
				"您将使用<font color='#ffea00'>%s</font>公司提供的<font color='#ffea00'>%s</font>业务进行代支付,资费是<font color='#ffea00'>%s</font>元，您将收到相关的短信提示，请注意查收！"),

		/** 拼命加载中... */
		CC_HINT_LOADING("拼命加载中..."),

		CC_PAYCHANNEL_TITLE("支付方式"), //
		CC_CARDNUM_DESC("请输入卡号"), //
		CC_PASSWD_DESC("请输入密码"), //
		/** 联通卡(15:19) 移动卡(17:18) */
		CC_CARDNUM_HINT("请输入卡号（%d位）"), //
		CC_PASSWD_HINT("请输入卡号（%d位）"), //
		CC_PAYTYPE_DESC("请点击确认充值，进入到%s充值界面"), //
		/** 卓越币的消耗描述，基于 {@link #CC_PRICE_FORMAT} */
		CC_PAYTYPE_COIN_DESC("确认后，将扣除%s卓越币，您的余额为%s"), //
		CC_COMMIT_RECHARGE("确认充值"), //
		CC_COMMIT_BUY("确认购买"), //
		CC_COMMIT_EXCHANGE("确认兑换"), //
		CC_PAYCHANNEL_ERROR("很抱歉！未能获取到可用的支付通道。"), //
		CC_PAYCHANNEL_NOCHOOSE("必须选择一个可用的支付通道！"), //

		CC_HELP_TITLE("帮助说明"), //
		CC_HELP_TEL("客服电话: 0123-45678901"), //
		CC_RECHARGE_LIST_NONE("不能显示候选列表"), //

		/** 道具兑换 */
		CC_EXCHANGE_TITLE("道具兑换"),
		/** 道具兑换详情 */
		CC_EXCHANGE_DETAIL_TITLE("兑换——%s"),

		/** XLISTVIEW: 下拉刷新 */
		XLISTVIEW_HEADER_HINT_NORMAL("下拉刷新"),
		/** XLISTVIEW: 松开刷新数据 */
		XLISTVIEW_HEADER_HINT_READY("松开刷新数据"),
		/** XLISTVIEW: 正在加载... */
		XLISTVIEW_HEADER_HINT_LOADING("正在加载..."),
		/** XLISTVIEW: 上次更新时间： */
		XLISTVIEW_HEADER_LAST_TIME("上次更新时间："),
		/** XLISTVIEW: 查看更多 */
		XLISTVIEW_FOOTER_HINT_NORMAL("查看更多"),
		/** XLISTVIEW: 松开载入更多 */
		XLISTVIEW_FOOTER_HINT_READY("松开载入更多"),
		/** XLISTVIEW: 正在加载更多... */
		XLISTVIEW_FOOTER_HINT_LOADING("正在加载更多..."),

		/** 无网络连接 */
		BITMAP_FUN_BADNETWORK("请检查网络连接！"),

		;

		private String context;

		private ZZStr(String txt) {
			context = txt;
		}

		public String str() {
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
			/** 充值界面·应付金额文本 */
			CC_RECHAGRE_COST(0xffccaa00),
			/** 充值界面·支付方式子项文本 */
			CC_PAYTYPE_ITEM(Color.BLACK),
			/** 充值界面·确认充值按钮 文本 */
			CC_RECHARGE_COMMIT(Color.WHITE),
			/** 充值界面·帮助按钮 文本 */
			CC_RECHARGE_HELP(Color.LTGRAY),

			/** 兑换列表·条目 文本 */
			CC_EXCHANGE_ITEM_TITLE(Color.BLACK), //
			CC_EXCHANGE_ITEM_TITLE_PRESSED(Color.WHITE),
			/** 兑换列表·条目备注 文本 */
			CC_EXCHANGE_ITEM_SUMMARY(Color.GRAY), //
			CC_EXCHANGE_ITEM_SUMMARY_PRESSED(Color.WHITE),

			;

			private int c;

			private ZZFontColor(int c) {
				this.c = c;
			}

			public int color() {
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
			CC_RECHARGE_COMMIT(20),
			/** 充值界面·帮助按钮 文本 */
			CC_RECHARGE_HELP(12),

			/** 兑换列表·条目 文本 */
			CC_EXCHANGE_ITEM_TITLE(16),
			/** 兑换列表·条目备注 文本 */
			CC_EXCHANGE_ITEM_SUMMARY(13),

			;

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
			/** 充值界面·主活动区的边距 */
			CC_ROOTVIEW_PADDING_LEFT(24), //
			CC_ROOTVIEW_PADDING_TOP(16), //
			CC_ROOTVIEW_PADDING_RIGHT(24), //
			CC_ROOTVIEW_PADDING_BOTTOM(12),

			/** 充值界面·各面板垂直方向间隔 */
			CC_SAPCE_PANEL_V(16),

			/** 充值数量输入框的边距 */
			CC_RECHARGE_COUNT_PADDING_H(16), CC_RECHARGE_COUNT_PADDING_V(8),

			/** 充值界面·GridView 的单元格间距 */
			CC_GRIDVIEW_SPACE_H(8), CC_GRIDVIEW_SPACE_V(4),
			/** 充值界面·GridView 的单元格子项的边距 */
			CC_GRIDVIEW_ITEM_PADDDING_LEFT(16), //
			CC_GRIDVIEW_ITEM_PADDDING_TOP(12), //
			CC_GRIDVIEW_ITEM_PADDDING_RIGHT(16), //
			CC_GRIDVIEW_ITEM_PADDDING_BOTTOM(12), //
			CC_GRIDVIEW_ITEM_HEIGHT(52), //

			/** 充值界面·GridView 单元格大小 */
			CC_GRIDVIEW_COLUMN_WIDTH(96),

			/** 充值界面·充值卡输入框的高度 */
			CC_CARD_HEIGHT(32),

			/** 道具兑换列表·图标宽度 */
			CC_EX_ICON_W(48),
			/** 道具兑换列表·图标高度 */
			CC_EX_ICON_H(48),
			/** 道具兑换列表·空隙 */
			CC_EX_PADDING(8),

			;

			private float dimen;

			private ZZDimen(float d) {
				dimen = d;
			}

			/** pixels */
			public int px() {
				return (int) (dimen * mDensity + 0.5f);
			}

			public static int dip2px(float d) {
				return (int) (d * mDensity + 0.5f);
			}
		}
	}

	public static enum CCImg {
		BACKGROUND("cc_background.9.png"), //
		BUTTON("cc_button.9.png"), //
		BUTTON_CLICK("cc_button_click.9.png"), //
		BUY_BUTTON("cc_buy_button.9.png"), //
		BUY_BUTTON_CLICK("cc_buy_button_click.9.png"), //
		CHARGE_PULL("cc_charge_pull.png"), //
		EX_BUTTON("cc_ex_button.9.png"), //
		EX_BUTTON_CLICK("cc_ex_button_click.9.png"), //
		EX_RIGHT("cc_ex_right.png"), //
		EX_RIGHT_CLICK("cc_ex_right_click.png"), //
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
		TITLE_EXIT_PRESSED("title_exit_pressed.png"),

		/** XListView */
		XLISTVIEW_ARROW("xlistview_arrow.png"),

		EMPTY_PHOTO("empty_photo.png"),

		;

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

		public Bitmap getBitmap(Context ctx) {
			String path = Constants.ASSETS_RES_PATH + PATH + File.separator
					+ context;
			return BitmapCache.getBitmap(ctx, path);
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
			case PayChannel.PAY_TYPE_YEEPAY_DX:
				ret = CCImg.TUP_DX;
				break;
			case PayChannel.PAY_TYPE_ZZCOIN:
				// 卓越币
				ret = CCImg.TUP_ZYB;
				break;

			default:
				ret = null;
				break;
			}
			return ret;
		}

		private static final String PATH = "drawable";
	}
}
