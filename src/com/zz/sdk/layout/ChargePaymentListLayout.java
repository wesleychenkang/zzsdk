package com.zz.sdk.layout;

import java.text.DecimalFormat;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.zz.sdk.BuildConfig;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;
import com.zz.sdk.util.Utils;

/**
 * 充值列表主界面
 * 
 * <p>
 * 分<b> 充值模式 </b>和<b> 购买模式 </a>
 * 
 * @author nxliao
 * 
 */
public class ChargePaymentListLayout extends ChargeAbstractLayout implements
		View.OnClickListener {
	/** 卓越币与RMB的兑换比例 */
	static float ZZ_COIN_RATE = 1f;

	protected static final int IDC_ACT_PAY = 201301;
	protected static final int IDC_ACT_ERR = 201302;
	protected static final int IDC_HELP = 201303;
	protected static final int IDC_ACT_PAY_GRID = 201310;

	/** 余额描述文本 */
	protected static final int IDC_TV_BALANCE = 201320;
	/** 充值数量 */
	protected static final int IDC_ED_RECHARGE_COUNT = 201321;
	/** 充值金额快速输入列表 */
	protected static final int IDC_BT_RECHARGE_PULL = 201321;
	/** 充值花费 */
	protected static final int IDC_TV_RECHARGE_COST = 201322;
	/** 充值金额确认充值 */
	protected static final int IDC_BT_RECHARGE_COMMIT = 201323;
	/** 充值卡输入区 */
	protected static final int IDC_PANEL_CARDINPUT = 201324;
	/** 充值卡输入区·卡号 */
	protected static final int IDC_ED_CARD = 201325;
	/** 充值卡输入区·密码 */
	protected static final int IDC_ED_PASSWD = 201326;

	/* 设置主区域的边距，单位 dip */
	protected static final int ROOTVIEW_SPACE_LEFT = 24;
	protected static final int ROOTVIEW_SPACE_TOP = 16;
	protected static final int ROOTVIEW_SPACE_RIGHT = 24;
	protected static final int ROOTVIEW_SPACE_BOTTOM = 12;

	protected GridView mPaymentType;

	private int mPaymentTypeChoose = -1;

	private static final int _MSG_USER_ = 0x10000;

	private DecimalFormat mRechargeCostFormat = new DecimalFormat(
			ZZStr.CC_RECHAGRE_COST_UNIT.toString());
	private DecimalFormat mBalanceFormat = new DecimalFormat(
			ZZStr.CC_BALANCE_UNIT.toString());

	private PaymentListAdapter mPaymentListAdapter;
	private AdapterView.OnItemClickListener mPaytypeItemListener;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what >= _MSG_USER_) {
				int id = msg.what - _MSG_USER_;
				handleUIChanged(id);
			} else {
				super.handleMessage(msg);
			}
		}
	};

	public void setOnItemClickListener(
			AdapterView.OnItemClickListener onItemClickListener) {
		mPaytypeItemListener = onItemClickListener;
		// mPaymentType.setOnItemClickListener(onItemClickListener);
	}

	public ChargePaymentListLayout(Activity activity) {
		super(activity);
		initUI(activity);
	}

	private void handleUIChanged(int etID) {
		switch (etID) {
		case IDC_ED_RECHARGE_COUNT:
			updateRechargeCost();
			break;
		default:
			break;
		}
	}

	private class MyTextWatcher implements TextWatcher {
		private int mId;

		MyTextWatcher(int id) {
			mId = id;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			postUIChangedMsg(mId);
		}
	}

	/** 更新充值的花费金额数值 */
	private void updateRechargeCost() {
		View v = findViewById(IDC_ED_RECHARGE_COUNT);
		if (v instanceof EditText) {
			float count = 0;
			String s = ((EditText) v).getText().toString().trim();
			if (s != null && s.length() > 0) {
				try {
					count = Float.parseFloat(s);
				} catch (NumberFormatException e) {
					if (BuildConfig.DEBUG) {
						Logger.d("bad count:" + s);
					}
				}
			}
			updateRechargeCost(count);
		}
	}

	/** 更新 “应付金额”值 */
	private void updateRechargeCost(float count) {
		View v = findViewById(IDC_TV_RECHARGE_COST);
		if (v instanceof TextView) {
			((TextView) v).setText(mRechargeCostFormat.format(count
					* ZZ_COIN_RATE));
		}
	}

	/** 更新卓越币余额 */
	private void updateBalance(float count) {
		View v = findViewById(IDC_TV_BALANCE);
		if (v instanceof TextView) {
			((TextView) v).setText(mBalanceFormat.format(count));
		}
	}

	/** 更改支付方式 */
	private void updatePayType(int pos) {
		if (pos == -1) {
			if (mPaymentTypeChoose >= 0)
				pos = mPaymentTypeChoose;
			else
				pos = 0;
		} else {
			if (pos == mPaymentTypeChoose)
				return;
		}

		int type = getPaychannelType(pos);

		if (type < 0) {
			if (BuildConfig.DEBUG) {
				Logger.d("无效索引或支付列表尚未初始化");
			}
			return;
		}

		if (mPaymentTypeChoose != pos) {
			do {
				View v;

				v = findViewById(IDC_PANEL_CARDINPUT);
				if (!(v instanceof ViewSwitcher))
					break;
				ViewSwitcher vs = (ViewSwitcher) v;

				if (mPaymentTypeChoose >= 0) {
					v = vs.getCurrentView();
					if (v instanceof LinearLayout) {
						Object tag = v.getTag();
						if (tag instanceof Integer
								&& ((Integer) tag).intValue() != getPaychannelType(mPaymentTypeChoose)) {
							// 与当前支付方式同类，不需要切换
							break;
						}
					}
				}

				v = vs.getNextView();
				if (v instanceof LinearLayout) {
					Object tag = v.getTag();
					if (tag == null || !(tag instanceof Integer)
							|| ((Integer) tag).intValue() != type) {
						((LinearLayout) v).removeAllViews();
						prepparePayType(mContext, ((LinearLayout) v), type);
						v.setTag(pos);
					}
					vs.showNext();
				}
			} while (false);
			mPaymentTypeChoose = pos;
		}

		if (mPaymentListAdapter != null) {
			mPaymentListAdapter.choose(mPaymentTypeChoose);
		}
	}

	private int getPaychannelType(int itemPos) {
		if (mPaymentListAdapter != null) {
			Object o = mPaymentListAdapter.getItem(itemPos);
			if (o instanceof PayChannel) {
				return ((PayChannel) o).type;
			}
		}
		return -1;
	}

	/** 准备支付方式的附加数据输入或简单描述 */
	private void prepparePayType(Context ctx, LinearLayout rv, int type) {
		TextView tv;
		switch (type) {
		case PayChannel.PAY_TYPE_ALIPAY:
		case PayChannel.PAY_TYPE_TENPAY:
		case PayChannel.PAY_TYPE_UNMPAY: {
			tv = new TextView(ctx);
			rv.addView(tv, new LayoutParams(LP_MW));
			tv.setText(String.format(ZZStr.CC_PAYTYPE_DESC.toString(),
					PayChannel.CHANNEL_NAME[type]));
			ZZFontSize.CC_RECHAGR_NORMAL.apply(tv);
		}
			break;
		case PayChannel.PAY_TYPE_YEEPAY_LT:
			prepparePayType_Card(ctx, rv, 15, 19);
			break;
		case PayChannel.PAY_TYPE_YEEPAY_YD:
			prepparePayType_Card(ctx, rv, 17, 18);
			break;

		case PayChannel.PAY_TYPE_KKFUNPAY: {
			// XXX: 暂不可使用短信充值，请使用其他方式
			tv = new TextView(ctx);
			rv.addView(tv, new LayoutParams(LP_MW));

			tv.setText("暂不可使用短信充值，请使用其他方式");
			tv.setTextColor(Color.RED);
			ZZFontSize.CC_RECHAGR_NORMAL.apply(tv);
		}
			break;

		default:
			break;
		}
	}

	/** 卡号输入面板 */
	private void prepparePayType_Card(Context ctx, LinearLayout rv,
			int limitCard, int limitPasswd) {
		TextView tv;
		EditText et;
		tv = createNormalLabel(ctx, ZZStr.CC_CARDNUM_DESC);
		rv.addView(tv, new LayoutParams(LP_WW));

		// 卡号
		et = createNormalInput(ctx, null, ZZFontColor.CC_RECHAGR_INPUT,
				ZZFontSize.CC_RECHAGR_INPUT, limitCard);
		rv.addView(et, new LayoutParams(LP_MW));
		et.setId(IDC_ED_CARD);
		et.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		et.setBackgroundDrawable(CCImg.ZF_WXZ.getDrawble(ctx));
		if (limitCard > 0) {
			String hint = String.format(ZZStr.CC_CARDNUM_HINT.toString(),
					limitCard);
			et.setHint(hint);
		}

		tv = createNormalLabel(ctx, ZZStr.CC_PASSWD_DESC);
		rv.addView(tv, new LayoutParams(LP_WW));

		// 密码
		et = createNormalInput(ctx, null, ZZFontColor.CC_RECHAGR_INPUT,
				ZZFontSize.CC_RECHAGR_INPUT, limitPasswd);
		rv.addView(et, new LayoutParams(LP_MW));
		et.setId(IDC_ED_PASSWD);
		et.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		et.setBackgroundDrawable(CCImg.ZF_WXZ.getDrawble(ctx));
		if (limitPasswd > 0) {
			String hint = String.format(ZZStr.CC_CARDNUM_HINT.toString(),
					limitPasswd);
			et.setHint(hint);
		}

	}

	private void postUIChangedMsg(int id) {
		int what = id + _MSG_USER_;
		mHandler.removeMessages(what);
		mHandler.sendEmptyMessageDelayed(what, 300);
	}

	/** 支付界面主工作视图 */
	private View createView_Charge(Context ctx) {
		// 主视图
		LinearLayout rv = new LinearLayout(ctx);
		rv.setPadding(DimensionUtil.dip2px(ctx, ROOTVIEW_SPACE_LEFT),
				DimensionUtil.dip2px(ctx, ROOTVIEW_SPACE_TOP),
				DimensionUtil.dip2px(ctx, ROOTVIEW_SPACE_RIGHT),
				DimensionUtil.dip2px(ctx, ROOTVIEW_SPACE_BOTTOM));
		rv.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams lp;
		LinearLayout ll;
		TextView tv;
		EditText et;

		// 余额描述
		{
			ll = createNormalPannel(ctx, rv);
			ll.setOrientation(HORIZONTAL);

			tv = createNormalLabel(ctx, ZZStr.CC_BALANCE_DESC);
			ll.addView(tv, new LayoutParams(LP_WM));

			tv = createNormalLabel(ctx, null);
			ll.addView(tv, new LayoutParams(LP_WM));
			tv.setId(IDC_TV_BALANCE);
			tv.setCompoundDrawablesWithIntrinsicBounds(null, null,
					CCImg.MONEY.getDrawble(ctx), null);
			ZZFontSize.CC_RECHAGR_BALANCE.apply(tv);
			// XXX: 更新余额值
			updateBalance(0);
		}

		// 充值数量输入
		{
			ll = createNormalPannel(ctx, rv);

			tv = createNormalLabel(ctx, ZZStr.CC_RECHAGRE_COUNT_DESC);
			ll.addView(tv, new LayoutParams(LP_MW));

			LinearLayout ll2;

			// 输入框
			{
				ll2 = new LinearLayout(ctx);
				ll.addView(ll2, new LayoutParams(LP_MW));
				ll2.setOrientation(HORIZONTAL);

				et = createNormalInput(ctx, ZZStr.CC_RECHAGRE_COUNT_HINT,
						ZZFontColor.CC_RECHAGR_INPUT,
						ZZFontSize.CC_RECHAGR_INPUT, 8);
				ll2.addView(et, new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT, 1.0f));
				et.setId(IDC_ED_RECHARGE_COUNT);
				et.setBackgroundDrawable(CCImg.ZF_XZ.getDrawble(ctx));
				et.addTextChangedListener(new MyTextWatcher(
						IDC_ED_RECHARGE_COUNT));
				et.setInputType(EditorInfo.TYPE_CLASS_NUMBER
						| EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);

				ImageButton ib = new ImageButton(ctx);
				ll2.addView(ib, new LayoutParams(LP_WW));
				ib.setId(IDC_BT_RECHARGE_PULL);
				ib.setBackgroundDrawable(null);
				ib.setImageDrawable(CCImg.CHARGE_PULL.getDrawble(ctx));
				ib.setScaleType(ScaleType.CENTER_INSIDE);
				ib.setOnClickListener(this);

				tv = createNormalLabel(ctx, null);
				String rate = new DecimalFormat("#.##").format(ZZ_COIN_RATE);
				String rate_desc = String.format(
						ZZStr.CC_RECHAGRE_RATE_DESC.toString(), rate);
				tv.setText(rate_desc);
				ll2.addView(tv, new LayoutParams(LP_WM));
			}

			// 应付金额
			{
				ll2 = new LinearLayout(ctx);
				ll2.setOrientation(HORIZONTAL);
				ll.addView(ll2);

				tv = createNormalLabel(ctx, ZZStr.CC_RECHAGRE_COST_DESC);
				ll2.addView(tv, new LayoutParams(LP_WM));

				tv = createNormalLabel(ctx, null);
				ll2.addView(tv, new LayoutParams(LP_WM));
				tv.setId(IDC_TV_RECHARGE_COST);
				tv.setTextColor(ZZFontColor.CC_RECHAGRE_COST.toColor());
				ZZFontSize.CC_RECHAGR_COST.apply(tv);
			}
		}

		// 支付方式
		{
			ll = createNormalPannel(ctx, rv);

			tv = createNormalLabel(ctx, ZZStr.CC_PAYCHANNEL_DESC);
			ll.addView(tv, new LayoutParams(LP_MW));

			// GridView 展示支付方式
			GridView gv = new TypeGridView(ctx);
			gv.setId(IDC_ACT_PAY_GRID);
			gv.setHorizontalSpacing(ZZDimen.CC_GRIDVIEW_SPACE_H.toPx());
			gv.setVerticalSpacing(ZZDimen.CC_GRIDVIEW_SPACE_V.toPx());
			gv.setNumColumns(GridView.AUTO_FIT);
			gv.setSelector(android.R.color.transparent);
			gv.setColumnWidth(ZZDimen.CC_GRIDVIEW_COLUMN_WIDTH.toPx());
			ll.addView(gv, new LayoutParams(LP_MW));
			mPaymentType = gv;

			OnItemClickListener listener = new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					updatePayType(position);
				}
			};
			mPaymentType.setOnItemClickListener(listener);

			mPaymentListAdapter = new PaymentListAdapter(ctx, null);
			gv.setAdapter(mPaymentListAdapter);
		}

		// 输入面板
		{
			ll = createNormalPannel(ctx, rv);

			ViewSwitcher vs = new ViewSwitcher(ctx);
			ll.addView(vs, new LayoutParams(LP_MW));
			vs.setId(IDC_PANEL_CARDINPUT);

			// TODO: 设置动画
			AnimationSet in = new AnimationSet(true);
			in.addAnimation(new AlphaAnimation(0.2f, 1.0f));
			in.setDuration(300);
			vs.setInAnimation(in);
			Animation out = new AlphaAnimation(1.0f, 0f);
			out.setDuration(250);
			vs.setOutAnimation(out);

			ll = new LinearLayout(ctx);
			ll.setOrientation(VERTICAL);
			vs.addView(ll, new ViewGroup.LayoutParams(LP_MW));

			ll = new LinearLayout(ctx);
			ll.setOrientation(VERTICAL);
			vs.addView(ll, new ViewGroup.LayoutParams(LP_MW));
		}

		// 确认充值
		{
			ll = createNormalPannel(ctx, rv);

			Button bt = new Button(ctx);
			ll.addView(bt, new LayoutParams(LP_WW));
			bt.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
					CCImg.BUTTON, CCImg.BUTTON_CLICK));
			bt.setId(IDC_BT_RECHARGE_COMMIT);
			bt.setText(ZZStr.CC_COMMIT_RECHARGE.toString());
			bt.setTextColor(ZZFontColor.CC_RECHARGE_COMMIT.toColor());
			bt.setPadding(24, 8, 24, 8);
			ZZFontSize.CC_RECHARGE_COMMIT.apply(bt);
			bt.setOnClickListener(this);
		}

		// XXX: 说明文本（暂定）
		if (false) {
			ll = createNormalPannel(ctx, rv);

			TextView mTopicTitle;
			TextView mTopicDes;
			mTopicTitle = new TextView(ctx);
			ll.addView(mTopicTitle, new LayoutParams(LP_WW));
			mTopicTitle.setTextColor(0xffe7c5aa);
			mTopicTitle.setTextSize(16);
			mTopicTitle.setText(null == Application.topicTitle ? null : Html
					.fromHtml(Application.topicTitle));

			mTopicDes = new TextView(ctx);
			mTopicDes.setTextSize(14);
			// mTopicDes.getPaint().setFakeBoldText(true);
			// mTopicDes.setText(Html.fromHtml("1、1元人民币=10金币，一般1-10分钟即可到账，简单方便。<br/>2、充值卡充值请根据充值卡面额选择正确的充值金额，并仔细核对卡号和密码。<br/>3、如有疑问请联系客服，客服热线：020-85525051 客服QQ：9159。"));
			String str = ToDBC(Application.topicDes);
			mTopicDes.setText(null == str ? null : Html.fromHtml(str));
			// mTopicDes.setText(null == Application.topicTitle ? null
			// : Html.fromHtml(Application.topicDes));
			ll.addView(mTopicDes, new LayoutParams(LP_WW));
		}

		return rv;
	}

	@Override
	protected void initUI(Context ctx) {
		super.initUI(ctx);

		mTileType.setText("充值中心");

		// 主活动区
		{
			FrameLayout actView = new FrameLayout(ctx);
			mSubject.addView(actView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));

			// 工作区
			{
				// 使用 scrollView
				ScrollView sv = new ScrollView(ctx);
				sv.setId(IDC_ACT_PAY);
				actView.addView(sv, new FrameLayout.LayoutParams(LP_MM));

				View rv = createView_Charge(ctx);
				sv.addView(rv);

				updateBalance(0);
				updateRechargeCost(0);
				updatePayType(-1);
			}

			{
				LinearLayout.LayoutParams lp;
				TextView mErr;
				mErr = new TextView(mContext);
				mErr.setId(IDC_ACT_ERR);
				lp = new LayoutParams(-1, -1);
				lp.gravity = Gravity.CENTER;
				mErr.setText("很抱歉！未能获取到可用的支付通道。");
				mErr.setTextColor(0xfffdc581);
				mErr.setTextSize(16);
				mErr.setVisibility(View.GONE);
				mErr.setGravity(Gravity.CENTER);
				actView.addView(mErr, new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			}
		}

		// 帮助区
		{
			LinearLayout footer = new LinearLayout(ctx);
			LinearLayout.LayoutParams lp = new LayoutParams(
					LayoutParams.MATCH_PARENT, DimensionUtil.dip2px(ctx, 50),
					1.0f);
			mSubject.addView(footer, lp);

			footer.setOrientation(LinearLayout.HORIZONTAL);
			footer.setId(IDC_HELP);
			footer.setOnClickListener(this);
			// footer.setBackgroundColor(0x80ff0000);

			TextView tvHelp = new TextView(ctx);
			footer.addView(tvHelp, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, 1.0f));
			tvHelp.setCompoundDrawablesWithIntrinsicBounds(
					CCImg.HELP.getDrawble(ctx), null, null, null);
			tvHelp.setText(ZZStr.CC_HELP_TITLE.toString());
			tvHelp.setTextColor(ZZFontColor.CC_RECHARGE_HELP.toColor());
			tvHelp.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			tvHelp.setCompoundDrawablePadding(DimensionUtil.dip2px(ctx, 8));
			tvHelp.setPadding(DimensionUtil.dip2px(ctx, 4), 0, 0, 0);
			ZZFontSize.CC_RECHARGE_HELP.apply(tvHelp);
			// tvHelp.setBackgroundColor(0x8000ff00);

			TextView tvDesc = new TextView(ctx);
			footer.addView(tvDesc, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, 1.0f));
			tvDesc.setText(ZZStr.CC_HELP_TEL.toString());
			tvDesc.setTextColor(ZZFontColor.CC_RECHARGE_HELP.toColor());
			tvDesc.setGravity(Gravity.CENTER);
			ZZFontSize.CC_RECHARGE_HELP.apply(tvDesc);
			// tvDesc.setBackgroundColor(0x800000ff);
		}
	}

	private void _setChildViewVisiblity(int id, int visibility) {
		View cv = findViewById(id);
		if (cv != null)
			cv.setVisibility(visibility);
	}

	public void showPayList(boolean visibility) {
		_setChildViewVisiblity(IDC_ACT_PAY, visibility ? VISIBLE : GONE);
		_setChildViewVisiblity(IDC_ACT_ERR, visibility ? GONE : VISIBLE);
	}

	@Override
	public PayParam getPayParam() {
		return null;
	}

	public void setChannelMessages(PayChannel[] channelMessages) {
		if (mPaymentListAdapter == null) {
			mPaymentListAdapter = new PaymentListAdapter(mContext,
					channelMessages);
			mPaymentType.setAdapter(mPaymentListAdapter);
		} else {
			mPaymentListAdapter.updatePayChannels(channelMessages);
		}
		updatePayType(-1);
	}

	/**
	 * 自定义的 GridView
	 */
	class TypeGridView extends GridView {

		public TypeGridView(Context context) {
			super(context);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		}
	}

	// 将所有的数字、字母及标点全部转为全角字符
	public String ToDBC(String input) {
		if (null == input)
			return null;
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case IDC_BT_RECHARGE_COMMIT:
			if (mPaytypeItemListener != null) {
				mPaytypeItemListener.onItemClick(mPaymentType, null,
						mPaymentTypeChoose, 0);
			}
			break;

		case IDC_BT_RECHARGE_PULL:
			showPopup_ChargePull(new float[] { 100, 500, 1000, 3000, 5000,
					10000 });
			break;

		case IDC_HELP:
			showPopup_Help();
			break;

		default:
			break;
		}
	}

	/** 展示帮助说明内容 */
	private void showPopup_Help() {
		Context ctx = mContext;
		LinearLayout ll = new LinearLayout(ctx);
		ll.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
				Gravity.BOTTOM));
		ll.setBackgroundDrawable(CCImg.BACKGROUND.getDrawble(ctx));
		ll.setPadding(DimensionUtil.dip2px(ctx, 48),
				DimensionUtil.dip2px(ctx, 5), DimensionUtil.dip2px(ctx, 48),
				DimensionUtil.dip2px(ctx, 24));

		AnimationSet in = new AnimationSet(true);
		in.addAnimation(new AlphaAnimation(0f, 0.8f));
		in.addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1f,
				Animation.RELATIVE_TO_SELF, 0));
		in.setDuration(350);
		ll.setAnimation(in);

		TextView mTopicTitle;
		TextView mTopicDes;
		mTopicTitle = new TextView(ctx);
		ll.addView(mTopicTitle, new LayoutParams(LP_WW));
		mTopicTitle.setTextColor(0xffe7c5aa);
		mTopicTitle.setTextSize(16);
		mTopicTitle.setText(null == Application.topicTitle ? null : Html
				.fromHtml(Application.topicTitle));

		mTopicDes = new TextView(ctx);
		mTopicDes.setTextSize(14);
		// mTopicDes.getPaint().setFakeBoldText(true);
		// mTopicDes.setText(Html.fromHtml("1、1元人民币=10金币，一般1-10分钟即可到账，简单方便。<br/>2、充值卡充值请根据充值卡面额选择正确的充值金额，并仔细核对卡号和密码。<br/>3、如有疑问请联系客服，客服热线：020-85525051 客服QQ：9159。"));
		String str = ToDBC(Application.topicDes);
		mTopicDes.setText(null == str ? null : Html.fromHtml(str));
		// mTopicDes.setText(null == Application.topicTitle ? null
		// : Html.fromHtml(Application.topicDes));
		ll.addView(mTopicDes, new LayoutParams(LP_WW));
		showPopup(ll);
	}

	/** 展示候选列表 */
	private void showPopup_ChargePull(float priceList[]) {
		Context ctx = mContext;
		LinearLayout ll = new LinearLayout(ctx);
		ll.setPadding(DimensionUtil.dip2px(ctx, 48),
				DimensionUtil.dip2px(ctx, 5), DimensionUtil.dip2px(ctx, 48),
				DimensionUtil.dip2px(ctx, 30));
		if (priceList == null || priceList.length == 0) {
			TextView tv = createNormalLabel(ctx, ZZStr.CC_RECHARGE_LIST_NONE);
			ll.addView(tv, new LayoutParams(LP_WW));
		} else {
			GridView gv = new TypeGridView(mContext);
			ll.addView(gv, new LayoutParams(LP_MW));
			gv.setSelector(android.R.color.transparent);
			gv.setColumnWidth(DimensionUtil.dip2px(mContext, 80));
			gv.setHorizontalSpacing(0);
			gv.setVerticalSpacing(0);
			gv.setNumColumns(GridView.AUTO_FIT);

			MyMoneyAdapter adapter = new MyMoneyAdapter(mContext,
					new DecimalFormat("#个"), priceList);
			gv.setAdapter(adapter);

			// 为GridView设置监听器
			gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					View v = findViewById(IDC_ED_RECHARGE_COUNT);
					Object o = parent.getAdapter();
					if ((v instanceof TextView)
							&& (o instanceof MyMoneyAdapter)) {
						// 将数量应用到文本输入框
						((TextView) v).setText(String
								.valueOf(((MyMoneyAdapter) o)
										.getValue(position)));
					}
					showPopup(false);
				}
			});
		}
		showPopup(ll);
	}

}

/**
 * 支付方式 adapter
 */
class PaymentListAdapter extends BaseAdapter {

	/** 当前选择项 */
	private int mCurPos = -1;

	private Context mContext;
	private PayChannel[] mPayChannels;

	private int mItemPaddingLeft, mItemPaddingTop, mItemPaddingRight,
			mItemPaddingBootom;
	private int mItemHeight;

	public PaymentListAdapter(Context ctx, PayChannel[] payChannels) {
		mContext = ctx;
		mItemPaddingLeft = ZZDimen.CC_GRIDVIEW_ITEM_PADDDING_LEFT.toPx();
		mItemPaddingRight = ZZDimen.CC_GRIDVIEW_ITEM_PADDDING_RIGHT.toPx();
		mItemPaddingTop = ZZDimen.CC_GRIDVIEW_ITEM_PADDDING_TOP.toPx();
		mItemPaddingBootom = ZZDimen.CC_GRIDVIEW_ITEM_PADDDING_BOTTOM.toPx();
		mItemHeight = ZZDimen.CC_GRIDVIEW_ITEM_HEIGHT.toPx();
		mPayChannels = payChannels;
	}

	// 只能在 UI 线程中调用
	protected void updatePayChannels(PayChannel[] payChannels) {
		mPayChannels = payChannels;
		notifyDataSetInvalidated();
	}

	// 只能在 UI 线程中调用
	protected void choose(int pos) {
		mCurPos = pos;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mPayChannels == null ? 0 : mPayChannels.length;
	}

	@Override
	public Object getItem(int position) {
		return mPayChannels == null ? null : mPayChannels[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mPayChannels == null) {
			return convertView;
		}

		TextView holder = (TextView) convertView;
		if (holder == null) {
			holder = new TextView(mContext);
			// holder.setBackgroundDrawable(Utils.getStateListDrawable(
			// mActivity, "type_bg1.png", "type_bg.png"));
			holder.setGravity(Gravity.CENTER);
			holder.setSingleLine();
			holder.setTextColor(ZZFontColor.CC_PAYTYPE_ITEM.toColor());
			holder.setPadding(mItemPaddingLeft, mItemPaddingTop,
					mItemPaddingRight, mItemPaddingBootom);
			holder.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, mItemHeight));
			ZZFontSize.CC_PAYTYPE_ITEM.apply(holder);
		}
		if (position == mCurPos) {
			holder.setBackgroundDrawable(CCImg.ZF_XZ.getDrawble(mContext));
		} else {
			holder.setBackgroundDrawable(CCImg.getStateListDrawable(mContext,
					CCImg.ZF_WXZ, CCImg.ZF_XZ));
		}
		holder.setText(mPayChannels[position].channelName);
		CCImg icon = CCImg.getPaychannelIcon(mPayChannels[position].type);
		if (icon != null)
			holder.setCompoundDrawablesWithIntrinsicBounds(
					icon.getDrawble(mContext), null, null, null);
		return holder;
	}
}

/** 候选列表 */
class MyMoneyAdapter extends BaseAdapter {

	private Context mContext;
	private DecimalFormat mFormat;
	private float mData[];

	public MyMoneyAdapter(Context ctx, DecimalFormat format, float data[]) {
		mContext = ctx;
		mFormat = format;
		mData = data;
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.length;
	}

	public float getValue(int position) {
		return (mData == null || position < 0 || position >= mData.length) ? 0
				: mData[position];
	}

	@Override
	public Object getItem(int position) {
		return (mData == null || position < 0 || position >= mData.length) ? null
				: mData[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView holder;
		if (convertView == null) {
			holder = new TextView(mContext);

			holder.setGravity(Gravity.CENTER);
			holder.setBackgroundDrawable(Utils.getStateListDrawable(mContext,
					"money_bg1.png", "money_bg.png"));
			holder.setTextSize(20);
			holder.setTextColor(Color.WHITE);
		} else {
			holder = (TextView) convertView;
		}
		if (mData != null && position >= 0 && position < mData.length) {
			holder.setText(mFormat.format(mData[position]));
		} else {
			holder.setText("Unknown:" + position);
		}
		return holder;
	}
}