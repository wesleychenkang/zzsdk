package com.zz.sdk.out.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.Utils;

public class ChargeDetailLayoutForCard extends ChargeAbstractLayout {

	protected Activity mActivity;
	protected static final int ID_PAY = 10001;
	protected static final int ID_TVNUMER = 10002;
	protected static final int ID_ETNUMER = 10003;
	protected static final int ID_TVPASSWORD = 10004;
	protected static final int ID_ETPASSWORD = 10005;
	protected static final int ID_TVMONEY = 10006;
	protected static final int ID_ETMONEY = 10007;
	protected static final int ID_TVCONFIRM = 10008;
	public static final int ID_BTNSUBMIT_LT = 10009;
	public static final int ID_BTNSUBMIT_YD = 10011;
	protected static final int ID_BTNCANNEL = 10010;
	protected static final int ID_BTNMONEY = 10010;

	private Button btnCannel;
	private Button btnSubmit;
	private EditText etNumber;
	private EditText etPassword;
	private EditText etPayMoney;

	private PayChannel mPayChannel;
	private List<String> moneyList = new ArrayList<String>();
	private PayParam mPayParam;

	public ChargeDetailLayoutForCard(Activity activity,
			PayChannel payChannel, PayParam charge) {
		super(activity);
		this.mPayChannel = payChannel;
		this.mPayParam = charge;
		initUI(activity);
	}

	@Override
	protected void initUI(Activity activity) {
		super.initUI(activity);
		mActivity = activity;

		ChargeTypeView chargeTypeView = new ChargeTypeView(activity);
		chargeTypeView.mPaymentDesc.setText(Html.fromHtml("你已选择<font color='#ffea00'>\""  + mPayChannel.channelName + "\"</font>支付"));
		LinearLayout.LayoutParams  lp = new LayoutParams(-1, -2);
		lp.leftMargin = DimensionUtil.dip2px(activity, 10);
		lp.rightMargin = DimensionUtil.dip2px(activity, 10);
		lp.topMargin = DimensionUtil.dip2px(activity, 10);
		mSubject.addView(chargeTypeView, lp);
		
		ScrollView scrollView = new ScrollView(mActivity);
		mSubject.addView(scrollView, -1, -1);

//		LinearLayout linkLayout = new LinearLayout(mActivity);
//		lp = new LayoutParams(-1, -1);
//		linkLayout.setOrientation(LinearLayout.VERTICAL);
//		scrollView.addView(linkLayout, lp);

		LinearLayout accountLayout = new LinearLayout(mActivity);
		accountLayout.setOrientation(LinearLayout.VERTICAL);
		lp = new LayoutParams(-1, -1);
		accountLayout.setPadding(DimensionUtil.dip2px(activity, 30),
				DimensionUtil.dip2px(activity, 10),
				DimensionUtil.dip2px(activity, 10),
				DimensionUtil.dip2px(activity, 10));
		scrollView.addView(accountLayout, lp);

		

		// 充值卡号
		LinearLayout number = new LinearLayout(mActivity);
		number.setOrientation(LinearLayout.HORIZONTAL);
		lp = new LayoutParams(-1, -2);
		lp.topMargin =DimensionUtil.dip2px(activity, 10);
		accountLayout.addView(number,lp);
		
		
		
		TextView numberText = new TextView(mActivity);
		numberText.setText("充值卡号：");
		numberText.setTextSize(16);
		numberText.setTextColor(0xfffdc581);
		numberText.setId(ID_TVNUMER);		
		 lp = new LayoutParams(-2,
				-2);
		number.addView(numberText, lp);
		
		etNumber = new EditText(mActivity);
		etNumber.setId(ID_ETNUMER);
		etNumber.setTextColor(0xffffe5c5);
		etNumber.setTextSize(16);
		etNumber.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		etNumber.setPadding(DimensionUtil.dip2px(activity, 8), 0, 0, 0);
		lp = new LayoutParams(-2,-2);
//		lp.rightMargin = DimensionUtil.dip2px(activity, 30);
		etNumber.setSingleLine();
		etNumber.setBackgroundDrawable(BitmapCache.getDrawable(activity,
				Constants.ASSETS_RES_PATH + "input_card.png"));
		number.addView(etNumber, lp);
		
		
		
		// 充值密码
		LinearLayout password = new LinearLayout(mActivity);
		password.setOrientation(LinearLayout.HORIZONTAL);
		lp = new LayoutParams(-2, -1);
		lp.topMargin =DimensionUtil.dip2px(activity, 10);
		accountLayout.addView(password,lp);
		
		
		TextView passwordText = new TextView(mActivity);
		passwordText.setText("充值密码：");
		passwordText.setTextSize(16);
		passwordText.setTextColor(0xfffdc581);
		passwordText.setId(ID_TVPASSWORD);
		lp = new LayoutParams(-2,-2);
		password.addView(passwordText, lp);
		
		etPassword = new EditText(mActivity);
		etPassword.setId(ID_ETPASSWORD);
		etPassword.setTextColor(0xffffe5c5);
		etPassword.setTextSize(16);
		etPassword.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		etPassword.setPadding(DimensionUtil.dip2px(activity, 8), 0, 0, 0);
		lp = new LayoutParams(-2,-2);
		etPassword.setSingleLine();
		etPassword.setBackgroundDrawable(BitmapCache.getDrawable(activity,
				Constants.ASSETS_RES_PATH + "input_card.png"));
		password.addView(etPassword, lp);
		
		if (mPayChannel.type == PayChannel.PAY_TYPE_YEEPAY_LT) { //联通充值卡
			etNumber.setHint("请输入卡号（15位）");
			etNumber.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					15) });

			etPassword.setHint("请输入密码（19位）");
			etPassword
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							19) });

		} else if (mPayChannel.type == PayChannel.PAY_TYPE_YEEPAY_YD) {// 移动充值卡
			etNumber.setHint("请输入卡号（17位）");
			etNumber.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					17) });

			etPassword.setHint("请输入密码（18位）");
			etPassword
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							18) });
		}

		// 充值金额
		LinearLayout moneyLayout = new LinearLayout(mActivity);
		moneyLayout.setOrientation(LinearLayout.HORIZONTAL);
		lp = new LayoutParams(-2, -1);
		lp.topMargin =DimensionUtil.dip2px(activity, 10);
		accountLayout.addView(moneyLayout,lp);
		
		
		TextView moneyText = new TextView(mActivity);
		moneyText.setText("充值金额：");
		moneyText.setTextSize(16);
		moneyText.setTextColor(0xfffdc581);
		moneyText.setId(ID_TVMONEY);
		lp = new LayoutParams(-2,-2);
		moneyLayout.addView(moneyText, lp);
		
		etPayMoney = new EditText(mActivity);
		etPayMoney.setId(ID_ETMONEY);
		etPayMoney.setTextColor(0xffffe5c5);
		etPayMoney.setTextSize(16);
		etPayMoney.setInputType(EditorInfo.TYPE_CLASS_NUMBER
				| EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
		etPayMoney.setGravity(Gravity.CENTER_VERTICAL);
		etPayMoney.setPadding(DimensionUtil.dip2px(activity, 8), 0, 0, 0);
		etPayMoney.setBackgroundDrawable(BitmapCache.getDrawable(activity,
				Constants.ASSETS_RES_PATH + "input_money.png"));
		lp = new LayoutParams(-2,-2);
		
		
		
		moneyLayout.addView(etPayMoney, lp);
		ImageButton btnSelectMoney = new ImageButton(mActivity);
		
		btnSelectMoney.setId(ID_BTNMONEY);
		btnSelectMoney.setBackgroundDrawable(BitmapCache.getDrawable(activity,
				Constants.ASSETS_RES_PATH + "charge_money.png"));
		lp = new LayoutParams(-2,-2);
		lp.leftMargin = DimensionUtil.dip2px(activity, 10);
		// lp10.addRule(RelativeLayout.ALIGN_BASELINE, ID_ETMONEY);
		moneyLayout.addView(btnSelectMoney, lp);
		int tempAmount = Application.changeCount;
		if(0!= tempAmount) {
			etPayMoney.setText(Utils.formateInt(tempAmount));
			etPayMoney.setEnabled(false);
			btnSelectMoney.setVisibility(GONE);
		}else{
			etPayMoney.setText("50");
		}
		
		LinearLayout buttonLayout = new LinearLayout(mActivity);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		lp = new LayoutParams(-2, -1);
		lp.topMargin =DimensionUtil.dip2px(activity, 10);
		lp.leftMargin =DimensionUtil.dip2px(activity, 80);
		accountLayout.addView(buttonLayout,lp);

		btnSubmit = new Button(mActivity);
		btnSubmit.setBackgroundDrawable(Utils.getStateListDrawable(activity,
				"tijiao_pressed.png", "tijiao_normal.png"));
		if (mPayChannel.type == PayChannel.PAY_TYPE_YEEPAY_LT) {
			btnSubmit.setId(ID_BTNSUBMIT_LT);
		}
		else if (mPayChannel.type == PayChannel.PAY_TYPE_YEEPAY_YD){
			btnSubmit.setId(ID_BTNSUBMIT_YD);
		}
		// btnSubmit.setOnClickListener(listener2);
		lp = new LayoutParams(-2,-2);
		buttonLayout.addView(btnSubmit, lp);

		btnCannel = new Button(mActivity);
		btnCannel.setBackgroundDrawable(Utils.getStateListDrawable(activity,
				"cancel_pressed.png", "cancel_normal.png"));
		btnCannel.setId(ID_CANCEL);
		lp = new LayoutParams(-2,-2);
		lp.leftMargin =DimensionUtil.dip2px(activity, 20);
		buttonLayout.addView(btnCannel, lp);
		
		TextView dec = new TextView(activity);
		lp = new LayoutParams(-2,-2);
		lp.topMargin = DimensionUtil.dip2px(activity, 10);
		dec.setText(Html.fromHtml(mPayChannel.desc));
		dec.setTextColor(0xffcba16f);
		dec.setTextSize(14);
		accountLayout.addView(dec, lp);

		LinearLayout help = new LinearLayout(activity);
		help.setOrientation(LinearLayout.HORIZONTAL);
		lp = new LayoutParams(-1,-2);
		accountLayout.addView(help, lp);

		TextView tv2 = new TextView(activity);
//		tv2.setId(ID_TV2);
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



		btnSelectMoney.setOnClickListener(selectMoneyListener);
		//etPayMoney.setOnClickListener(listener2);
	}

	@Override
	public void setButtonClickListener(OnClickListener listener) {
		super.setButtonClickListener(listener);
		btnCannel.setOnClickListener(listener);
		btnSubmit.setOnClickListener(listener);
	}

	/**
	 * 用户输入的卡号
	 * 
	 * @return
	 */
	private String getInputCardNum() {

		return etNumber.getText().toString();
	}

	/**
	 * 用户输入的卡密码
	 * 
	 * @return
	 */
	private String getInputCardPassward() {
		return etPassword.getText().toString();
	}

	/**
	 * 用户充值的金额
	 * 
	 * @return
	 */
	private String getInputAmount() {
		return etPayMoney.getText().toString();
	}

	@Override
	public PayParam getPayParam() {
		mPayParam.cardNo = getInputCardNum();
		mPayParam.cardPassword = getInputCardPassward();
		if (TextUtils.isEmpty(getInputAmount())) {
			mPayParam.amount = "0.00";
		} else {
			mPayParam.amount = getInputAmount();
		}
		return mPayParam;
	}

	private OnClickListener selectMoneyListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			moneyList = Utils.payMoneyList(mPayChannel);
			if (moneyList == null || moneyList.size() == 0) {
				Utils.toastInfo(mActivity, "没有可供选择的金额！");
				return;
			}
			// 第二个参数是表示Dialog的风格样式，第三个是Dialog的数据，主要是尝试下怎么给一个类数据而已
			final PayDialogHelper dlg = new PayDialogHelper(mActivity,
					moneyList, etPayMoney);
			dlg.show();

		}
	};

	public boolean checkNum() {

		// 检测卡号
		if (TextUtils.isEmpty(getInputCardNum())) {
			Utils.toastInfo(mActivity, "充值帐号不能为空!");
			return false;
		} else {
			int length = getInputCardNum().toString().length();
			switch (mPayChannel.type) {
			case PayChannel.PAY_TYPE_YEEPAY_LT: // 联通
				if (length != 15) {
					Utils.toastInfo(mActivity, "请输入15位卡号!");
					return false;
				}
				break;
			case PayChannel.PAY_TYPE_YEEPAY_YD:// 移动
				if (length != 17) {
					Utils.toastInfo(mActivity, "请输入17位卡号!");
					return false;
				}
				break;
			}
		}

		// 检测密码
		if (TextUtils.isEmpty(getInputCardPassward())) {
			Utils.toastInfo(mActivity, "充值密码不能为空!");
			return false;
		} else {
			int length = getInputCardPassward().toString().length();
			switch (mPayChannel.type) {
			case PayChannel.PAY_TYPE_YEEPAY_LT: // 联通
				if (length != 19) {
					Utils.toastInfo(mActivity, "请输入19位密码!");
					return false;
				}
				break;
			case PayChannel.PAY_TYPE_YEEPAY_YD:// 移动
				if (length != 18) {
					Utils.toastInfo(mActivity, "请输入18位密码!");
					return false;
				}
				break;
			}
		}

		if (TextUtils.isEmpty(getInputAmount())) {
			Utils.toastInfo(mActivity, "充值金额不能为空");
			return false;
		}

		if (!Utils.formatMoney(getInputAmount())) {
			Utils.toastInfo(mActivity, "充值金额不正确，请输入1-9999范围内的金额");
			return false;
		}

		return true;

	}
}
