package com.zz.sdk.util;

import java.io.File;
import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.TextView;

import com.zz.sdk.entity.PayChannel;

public class ResConstants {

	// private static Context mContext;
	private static float mDensity = 1.0f;

	/** 初始化环境配置，如分辨率等 */
	public static void init(Context ctx) {
		// mContext = null;
		mDensity = ctx.getResources().getDisplayMetrics().density;
	}

	public static void clean() {
		// mContext = null;
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

		/** 默认·帮助·标题 */
		DEFAULT_HELP_TITLE(""),
		/** 默认·帮助·内容 */
		DEFAULT_HELP_TOPIC(
				"如有疑问请联系客服，<br /><b>客服热线</b>： <a>020-85525051</a><br><b>客服QQ</b>：9159。"),

		/** 充值中心 */
		CC_RECHARGE_TITLE("充值中心"),
		/** 充值中心(购买) */
		CC_RECHARGE_TITLE_SOCIAL("充值中心(购买)"),

		CC_RECHARGE_COUNT_TITLE("充值数量"), //
		/** 充值中心（游戏购买入口） */
		CC_RECHARGE_COUNT_TITLE_PRICE("道具价格"), //
		CC_RECHAGRE_COUNT_HINT("请输入数量"), //
		CC_RECHARGE_COUNT_CHECK_FAILED("请输入正确的充值数量"), //
		CC_RECHAGRE_COUNT_DESC("卓越币"), //
		/** RMB与卓越币的兑换比例，如 (1元=%s卓越币)，基于 {@link #CC_PRICE_FORMAT} */
		CC_RECHAGRE_RATE_DESC("(1元=%s卓越币)"), //
		CC_RECHAGRE_COST_DESC("应付金额："), //
		/** 充值金额，基于 {@link #CC_PRICE_FORMAT} */
		CC_RECHAGRE_COST_UNIT("%s元"), //
		CC_RECHAGRE_COST_UNIT_ZYCOIN("%s卓越币"), //

		/** 支付金额大于1000元，建议使用大额支付。 */
		CC_RECHARGE_COST_SUMMARY("支付金额大于1000元，建议使用大额支付。"),

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

		/** 您填写的卡号和密码不匹配，请重新输入 */
		CC_RECHARGE_RESULT_FAILED_CARD("您填写的卡号和密码不匹配，请重新输入"),

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
		/** 充值 */
		CC_TRY_SMS_CHOOSE_PREFIX("充值"),
		/** %s元 */
		CC_TRY_SMS_CHOOSE_CONTENT("%s元"),
		/** 您将使用%s公司提供的%s业务进行代支付,资费是%s元，您将收到相关的短信提示，请注意查收！(ffea00) */
		CC_TRY_SMS_PROMPT_HTML(
				"您将使用<font color='#F17040'>%s</font>公司提供的<font color='#F17040'>%s</font>业务进行代支付,资费是<font color='#F17040'> %s </font>元，您将收到相关的短信提示，请注意查收！"),

		/** 拼命加载中... */
		CC_HINT_LOADING("拼命加载中..."),

		CC_PAYCHANNEL_TITLE("支付方式"), //
		CC_CARDNUM_DESC("请输入卡号"), //
		CC_CARDNUM_CHECK_FAILED("请输入完整的充值卡卡号"), //
		CC_PASSWD_DESC("请输入密码"), //
		CC_PASSWD_CHECK_FAILED("请输入完整的充值卡密码"), //
		/** 联通卡(15:19) 移动卡(17:18) */
		CC_CARDNUM_HINT("请输入卡号（%d位）"), //
		CC_PASSWD_HINT("请输入卡号（%d位）"), //
		CC_PAYTYPE_DESC("请点击确认充值，进入到%s充值界面"), //
		/** 暂不可使用%s充值，请使用其他方式 */
		CC_PAYTYPE_DESC_DISABLED("暂不可使用%s充值，请使用其他方式"), //
		/** 卓越币的消耗描述，基于 {@link #CC_PRICE_FORMAT} */
		CC_PAYTYPE_COIN_DESC("确认后，将扣除%s卓越币，您的余额为%s"), //
		/** 卓越币余额不足 */
		CC_PAYTYPE_COIN_DESC_POOR("卓越币余额不足，请更换其他方式进行支付"), //
		CC_COMMIT_RECHARGE("确认充值"), //
		CC_COMMIT_RECHARGE_SMS("确认提交"), //
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
		/** 价格：%s卓越币 */
		CC_EXCHANGE_DETAIL_PRICE_DESC("价格：%s卓越币"),
		/** 消费描述：本次消费%s，您的余额还有%s */
		CC_EXCHANGE_DETAIL_BALANCE_DESC("本次消费%s，您的余额还有%s"),

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
			CC_RECHARGE_NORMAL(Color.BLACK),
			/** 充值界面·描述文本 */
			CC_RECHARGE_DESC(Color.LTGRAY),
			/** 充值界面·警示性文本 */
			CC_RECHARGE_WARN(Color.MAGENTA),
			/** 充值界面·错误提示文本 */
			CC_RECHARGE_ERROR(Color.RED),
			/** 充值界面输入文本 */
			CC_RECHARGE_INPUT(Color.DKGRAY),
			/** 充值界面·应付金额文本 */
			CC_RECHARGE_COST(0xffccaa00),
			/** 充值界面·支付方式子项文本 */
			CC_PAYTYPE_ITEM(Color.BLACK),
			/** 充值界面·确认充值按钮 文本 */
			CC_RECHARGE_COMMIT(Color.WHITE),
			/** 充值界面·帮助按钮 文本 */
			CC_RECHARGE_HELP(Color.LTGRAY),

			/** 充值界面·短信 普通文本 */
			CC_RECHARGE_SMS_NORMAL(Color.BLACK),
			/** 充值界面·短信 选择文本 */
			CC_RECHARGE_SMS_CHOOSE(Color.GRAY),
			/** 充值界面·短信 高亮文本 */
			CC_RECHARGE_SMS_HIGHLIGHT(0xffF17040),

			/** 兑换列表·条目 文本 */
			CC_EXCHANGE_ITEM_TITLE(Color.BLACK), //
			CC_EXCHANGE_ITEM_TITLE_PRESSED(Color.WHITE),
			/** 兑换列表·条目备注 文本 */
			CC_EXCHANGE_ITEM_SUMMARY(Color.GRAY), //
			CC_EXCHANGE_ITEM_SUMMARY_PRESSED(Color.WHITE),

			/** 兑换详情·道具名 */
			CC_EXCHANGE_DETAIL_NAME(Color.BLACK),
			/** 兑换详情·描述文本 */
			CC_EXCHANGE_DETAIL_DESC(0xff777777),

			;

			private int c;

			private ZZFontColor(ZZFontColor c) {
				this.c = c.c;
			}

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
			CC_RECHARGE_NORMAL(16),
			/** 充值界面·描述文本 */
			CC_RECHARGE_DESC(16),
			/** 余额文本 */
			CC_RECHARGE_BALANCE(22),
			/** 充值界面输入文本 */
			CC_RECHARGE_INPUT(18),
			/** 充值界面应付金额文本 */
			CC_RECHARGE_COST(20),
			/** 充值界面·支付方式子项文本 */
			CC_PAYTYPE_ITEM(14),
			/** 充值界面·确认充值按钮 文本 */
			CC_RECHARGE_COMMIT(20),
			/** 充值界面·帮助按钮 文本 */
			CC_RECHARGE_HELP(12),

			/** 充值界面·短信 普通文本 */
			CC_RECHARGE_SMS_NORMAL(18),
			/** 充值界面·短信 选择文本 */
			CC_RECHARGE_SMS_CHOOSE(18),
			/** 充值界面·短信 高亮文本 */
			CC_RECHARGE_SMS_HIGHLIGHT(22),

			/** 兑换列表·条目 文本 */
			CC_EXCHANGE_ITEM_TITLE(16),
			/** 兑换列表·条目备注 文本 */
			CC_EXCHANGE_ITEM_SUMMARY(13),

			/** 兑换详情·道具名 */
			CC_EXCHANGE_DETAIL_NAME(20),
			/** 兑换详情·描述文本 */
			CC_EXCHANGE_DETAIL_DESC(16),

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
			/** 充值界面·各面板垂直方向间隔 */
			CC_SAPCE_PANEL_V(12),

			/** 充值数量输入框的边距 */
			CC_RECHARGE_COUNT_PADDING_H(16), CC_RECHARGE_COUNT_PADDING_V(8),

			/** 充值界面·GridView 的单元格间距 */
			CC_GRIDVIEW_SPACE_H(8), CC_GRIDVIEW_SPACE_V(4),

			/** 充值界面·GridView 的单元格子项的边距 */
			CC_GRIDVIEW_ITEM_HEIGHT(52), //

			/** 充值界面·GridView 单元格大小 */
			CC_GRIDVIEW_COLUMN_WIDTH(96),

			/** 充值界面·充值卡输入框的高度 */
			CC_CARD_HEIGHT(32),

			/** 按钮间隔 */
			CC_COMMIT_SPACE(16),

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

		/** 尺寸配置, DIP */
		public static enum ZZDimenRect {
			/** 充值界面·主活动区的边距 */
			CC_ROOTVIEW_PADDING(24, 16, 24, 12),

			/** 确认充值等提交类按钮 */
			CC_RECHARGE_COMMIT(16, 4, 16, 4),

			/** 充值界面·GridView 的单元格子项的边距 */
			CC_GRIDVIEW_ITEM_PADDDING(16, 12, 16, 12),

			/** 充值界面·话费·GridView 的单元格子项的边距 */
			CC_GRIDVIEW_SMS_PADDDING(8, 4, 8, 4),

			/** 兑换详情 */
			CC_EX_DETAIL_PADDING(12, 8, 12, 8),
			/** 兑换详情的展示面板边距 */
			CC_EX_DETAIL_PANEL(6, 4, 6, 4),

			;

			private float left, top, right, bottom;

			private ZZDimenRect(float l, float t, float r, float b) {
				left = l;
				top = t;
				right = r;
				bottom = b;
			}

			public void apply_padding(View v) {
				int l = ZZDimen.dip2px(left);
				int t = ZZDimen.dip2px(top);
				int r = ZZDimen.dip2px(right);
				int b = ZZDimen.dip2px(bottom);
				v.setPadding(l, t, r, b);
			}

			public Rect rect() {
				int l = ZZDimen.dip2px(left);
				int t = ZZDimen.dip2px(top);
				int r = ZZDimen.dip2px(right);
				int b = ZZDimen.dip2px(bottom);
				return new Rect(l, t, r, b);
			}

			public void apply_margins(MarginLayoutParams mlp) {
				int l = ZZDimen.dip2px(left);
				int t = ZZDimen.dip2px(top);
				int r = ZZDimen.dip2px(right);
				int b = ZZDimen.dip2px(bottom);
				mlp.setMargins(l, t, r, b);
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
		TUP_DEZF("cc_tup_dezf.png"), //
		ZF_WXZ("cc_zf_wxz.9.png"), //
		ZF_XZ("cc_zf_xz.9.png"), //
		TITLE_BACK_DEFAULT("title_back_default.png"), //
		TITLE_BACK_PRESSED("title_back_pressed.png"), //
		TITLE_BG("title_bg.png"), //
		TITLE_EXIT_DEFAULT("title_exit_default.png"), //
		TITLE_EXIT_PRESSED("title_exit_pressed.png"),

		/** XListView */
		XLISTVIEW_ARROW("xlistview_arrow.png"), //
		EMPTY_PHOTO("empty_photo.png"),

		// :%s/\([a-z_]*\)\.\(.*\)$/\U\1\l("\1\.\2"), \/\//g
		/** 登录界面资源 */
		LOGIN_BUTTON_LV_CLICK("login_button_lv_click.9.png"), //
		LOGIN_BUTTON_LV("login_button_lv.9.png"), //
		LOGIN_BUTTON_LAN_CLICK("login_button_lan_click.9.png"), //
		LOGIN_BUTTON_LAN("login_button_lan.9.png"), //
		LOGIN_BUTTON_HUI_CLICK("login_button_hui_click.9.png"), //
		LOGIN_BUTTON_HUI("login_button_hui.9.png"), //
		LOGIN_BUTTON_HUANG_CLICK("login_button_huang_click.9.png"), //
		LOGIN_BUTTON_HUANG("login_button_huang.9.png"), //
		LOGIN_BUTTON_KUAI("login_button_kuai.png"), //
		LOGIN_BUTTON_KUAI_ANXIA("login_button_kuai_anxia.png"), //
		LOGIN_EDIT("login_edit_press.png");

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

		/**
		 * 构造按钮状态图
		 * 
		 * @param ctx
		 *            环境
		 * @param picNormal
		 *            普通状态({@link android.R.attr#state_enabled})
		 * @param picPressed
		 *            <ul>
		 *            特别状态
		 *            <li>按下({@link android.R.attr#state_pressed})、
		 *            <li>焦点({@link android.R.attr#state_focused})、
		 *            <li>选择({@link android.R.attr#state_selected})
		 *            </ul>
		 * @return
		 * @see BitmapCache#getStateListDrawable(Context, Drawable, Drawable)
		 */
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
			case PayChannel.PAY_TYPE_EX_DEZF:
				ret = CCImg.TUP_DEZF;
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
