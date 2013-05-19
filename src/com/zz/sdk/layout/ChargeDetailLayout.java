package com.zz.sdk.layout;

import java.util.List;

import android.app.Activity;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zz.sdk.activity.Application;
import com.zz.sdk.activity.ChargeActivity;
import com.zz.sdk.activity.Constants;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.Utils;

/**
 * 
 * 支付宝，财付通，银联(三个界面规范相同)
 * 
 */
public class ChargeDetailLayout extends ChargeAbstractLayout {

	protected static final int ID_TV1 = 20001;
	protected static final int ID_INPUT = 20002;
	protected static final int ID_SELECT = 20003;
	public static final int ID_ALIPAY = 20006;
	public static final int ID_TENPAY = 20007;
	public static final int ID_UNICOMPAY = 20008;

	protected static int ID_PAY;

	public void setID_PAY(int id) {
		switch (id) {
		// 支付宝
		case ChargeActivity.INDEX_CHARGE_ZHIFUBAO:
			ID_PAY = ID_ALIPAY;
			break;
		// 银联卡
		case ChargeActivity.INDEX_CHARGE_UNIONPAY:
			ID_PAY = ID_UNICOMPAY;
			break;
		// 财付通
		case ChargeActivity.INDEX_CHARGE_CAIFUTONG:
			ID_PAY = ID_TENPAY;
			break;
		}
	}

	private EditText mInput;
	private ImageButton mSelect;
	private Button mCharge;
	private Button mCancel;
	private PayChannel payChannel;
	private Activity mActivity;
	private PayParam payParam;

	public PayParam getPayParam() {
		payParam.amount = getInputMoney();
		return payParam;
	}

	public boolean checkMoney() {
		return Utils.formatMoney(getInputMoney())
				&& Double.parseDouble(getInputMoney()) < MAXAMOUNT;
	}

	public String getInputMoney() {
		return mInput.getText().toString().trim();
	}

	public ChargeDetailLayout(Activity activity, PayChannel channelMessage,
			PayParam charge) {
		super(activity);
		this.mActivity = activity;
		this.payChannel = channelMessage;
		this.payParam = charge;
		setID_PAY(payChannel.type);
		initUI(activity);
	}

	@Override
	protected void initUI(Activity activity) {
		super.initUI(activity);

		// 顯示支付類型
		ChargeTypeView chargeTypeView = new ChargeTypeView(activity);
		chargeTypeView.mPaymentDesc.setText(Html
				.fromHtml("你已选择<font color='#ffea00'>\""
						+ payChannel.channelName + "\"</font>支付"));
		LayoutParams lp = new LayoutParams(-1, -2);
		lp.leftMargin = DimensionUtil.dip2px(activity, 20);
		lp.rightMargin = DimensionUtil.dip2px(activity, 15);
		lp.topMargin = DimensionUtil.dip2px(activity, 10);
		mSubject.addView(chargeTypeView, lp);

		ScrollView scrollView = new ScrollView(activity);
		lp = new LayoutParams(-1, -1);
		mSubject.addView(scrollView, lp);

		LinearLayout parent = new LinearLayout(activity);
		parent.setOrientation(LinearLayout.VERTICAL);
		lp = new LayoutParams(-1, -1);
		parent.setPadding(DimensionUtil.dip2px(activity, 30),
				DimensionUtil.dip2px(activity, 10),
				DimensionUtil.dip2px(activity, 10),
				DimensionUtil.dip2px(activity, 10));
		scrollView.addView(parent, lp);

		//
		LinearLayout layout = new LinearLayout(activity);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		lp = new LayoutParams(-1, -2);
		layout.setGravity(Gravity.CENTER_VERTICAL);
		lp.topMargin = DimensionUtil.dip2px(activity, 20);
		parent.addView(layout, lp);

		// 充值金额
		TextView tv1 = new TextView(activity);
		tv1.setGravity(Gravity.CENTER_VERTICAL);
		lp = new LayoutParams(-2, -2);
		tv1.setText("充值金额: ");
		tv1.setTextColor(0xfffdc581);
		tv1.setTextSize(16);
		tv1.setId(ID_TV1);
		layout.addView(tv1, lp);

		mInput = new EditText(activity);
		mInput.setText("50");
		mInput.setTextColor(0xffbf956e);
		mInput.setTextSize(14);
		mInput.setId(ID_INPUT);
		mInput.setGravity(Gravity.CENTER_VERTICAL);
		mInput.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		mInput.setBackgroundDrawable(BitmapCache.getDrawable(activity,
				Constants.ASSETS_RES_PATH + "input_money.png"));
		lp = new LayoutParams(-2, -2);
		lp.leftMargin = DimensionUtil.dip2px(activity, 10);
		layout.addView(mInput, lp);

		mSelect = new ImageButton(activity);
		mSelect.setId(ID_SELECT);
		mSelect.setOnClickListener(new SelectMoneyOnlickListener());
		mSelect.setScaleType(ScaleType.FIT_CENTER);
		mSelect.setBackgroundDrawable(BitmapCache.getDrawable(activity,
				Constants.ASSETS_RES_PATH + "charge_money.png"));
		lp = new LayoutParams(-2, -2);
		lp.leftMargin = DimensionUtil.dip2px(activity, 10);
		layout.addView(mSelect, lp);

		LinearLayout buttonLayout = new LinearLayout(activity);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.setGravity(Gravity.CENTER);
		lp = new LayoutParams(-2, -2);
		lp.topMargin = DimensionUtil.dip2px(activity, 10);
		lp.leftMargin = DimensionUtil.dip2px(activity, 80);
		parent.addView(buttonLayout, lp);

		mCharge = new Button(activity);
		mCharge.setId(ID_PAY);
		mCharge.setBackgroundDrawable(Utils.getStateListDrawable(activity,
				"zhifu_pressed.png", "zhifu_normal.png"));
		lp = new LayoutParams(-2, -2);
		buttonLayout.addView(mCharge, lp);

		mCancel = new Button(activity);
		mCancel.setId(ID_CANCEL);
		mCancel.setBackgroundDrawable(Utils.getStateListDrawable(activity,
				"cancel_pressed.png", "cancel_normal.png"));
		lp = new LayoutParams(-2, -2);
		lp.leftMargin = DimensionUtil.dip2px(activity, 20);
		buttonLayout.addView(mCancel, lp);

		TextView dec = new TextView(activity);
		lp = new LayoutParams(-2, -2);
		lp.topMargin = DimensionUtil.dip2px(activity, 10);
		dec.setText(payChannel.desc);
		dec.setTextColor(0xffcba16f);
		dec.setTextSize(14);
		parent.addView(dec, lp);

		LinearLayout help = new LinearLayout(activity);
		help.setOrientation(LinearLayout.HORIZONTAL);
		lp = new LayoutParams(-1, -2);
		parent.addView(help, lp);

		TextView tv2 = new TextView(activity);
		// tv2.setId(ID_TV2);
		tv2.setText(Application.customerServiceHotline);
		tv2.setTextColor(0xffcba16f);
		tv2.setTextSize(14);
		tv2.setLineSpacing(DimensionUtil.dip2px(activity, 5), 1);
		lp = new LinearLayout.LayoutParams(-2, -2);
		help.addView(tv2, lp);

		TextView tv3 = new TextView(activity);
		// tv2.setId(ID_TV2);
		tv3.setText(Application.customerServiceQQ);
		tv3.setTextColor(0xffcba16f);
		tv3.setTextSize(14);
		tv3.setLineSpacing(DimensionUtil.dip2px(activity, 5), 1);
		lp = new LayoutParams(-2, -2);
		lp.leftMargin = DimensionUtil.dip2px(activity, 10);
		help.addView(tv3, lp);

	}

	class SelectMoneyOnlickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			List<String> list = Utils.payMoneyList(payChannel);
			if (list.size() == 0) {
				Utils.toastInfo(mActivity, "没有可供选择的金额!");
				return;
			}
			// 第二个参数是表示Dialog的风格样式，第三个是Dialog的数据，主要是尝试下怎么给一个类数据而已
			final PayDialogHelper dlg = new PayDialogHelper(mActivity, list,
					mInput);
			dlg.show();
		}

	}

	@Override
	public void setButtonClickListener(OnClickListener listener) {
		super.setButtonClickListener(listener);
		mCancel.setOnClickListener(listener);
		mCharge.setOnClickListener(listener);
	}

}
