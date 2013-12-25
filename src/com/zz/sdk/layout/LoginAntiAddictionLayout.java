package com.zz.sdk.layout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zz.sdk.ParamChain;
import com.zz.sdk.util.ResConstants;

/**
 * 防沉迷
 * <p/>
 * <ul>输入：
 * <li>loginName</li>
 * <li>Password</li>
 * <li>LoginState</li>
 * <li>cmState</li>
 * </ul>
 *
 * @version 0.1.0.20131224
 */
class LoginAntiAddictionLayout extends BaseLayout {
//	static final boolean DEBUG_UI = BuildConfig.DEBUG;

	static enum IDC implements IIDC {

		/*账号输入框*/
		ET_ACCOUNT,
		/*密码输入框*/
		ET_PASSWORD,
		/*单选按钮组*/
		RADIO_GROUP,
		/*单选：已成年*/
		RB_ADULT,
		/*单选：未成年*/
		RB_NONAGE,
		/*确认验证*/
		BT_COMMIT,
		/*取消验证*/
		BT_CANCEL,

		_MAX_;

		/** ID 的起点 */
		protected static int __start__ = BaseLayout.IDC._MAX_.id();

		public final int id() {
			return ordinal() + __start__;
		}

		/** 从 id 反查，如果失败则返回 {@link #_MAX_} */
		public final static IDC fromID(int id) {
			id -= __start__;
			if (id >= 0 && id < _MAX_.ordinal()) {
				return values()[id];
			}
			return _MAX_;
		}
	}

	private boolean mLogined;
	private String mLoginName;
	private String mPassword;
	/* 0未设置 1未成年 2已成年 */
	private int mState;

	private TextView tvAccount, tvPasswd;
	private RadioGroup rgState;

	public LoginAntiAddictionLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
	}

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
		Boolean b = env.get(ParamChain.KeyUser.K_LOGIN_STATE_SUCCESS, Boolean.class);
		mLogined = (b != null && b);
		mLoginName = env.get(ParamChain.KeyUser.K_LOGIN_NAME, String.class);
		mPassword = env.get(ParamChain.KeyUser.K_PASSWORD, String.class);
		Integer i = env.get(ParamChain.KeyUser.K_ANTIADDICTION, Integer.class);
		mState = i == null ? 0 : i;
	}

	@Override
	protected void onInitUI(Context ctx) {

		FrameLayout fl = getSubjectContainer();
		{
			ScrollView sv = new ScrollView(ctx);
			fl.addView(sv, new FrameLayout.LayoutParams(LP_MM));
			sv.addView(createView_main(ctx));
		}

		// 设置标题
		{
			setTileTypeText(ResConstants.ZZStr.CC_ANTIADDICTION_TITLE.str());
		}
	}

	private TextView createView_input(
			LinearLayout ll, Context ctx, ResConstants.ZZStr label, ResConstants.CCImg icon, ResConstants.ZZStr hint,
			IDC input_id) {
		TextView tv;

		// label
		tv = create_normal_label(ctx, label);
		ResConstants.Config.ZZDimenRect.CC_LABEL_PADDING.apply_padding(tv);
		ll.addView(tv);

		FrameLayout fl = new FrameLayout(ctx);
		ll.addView(fl);

		Rect r = ResConstants.Config.ZZDimenRect.CC_INPUT_ICON_ACCOUNT_PADDING.rect();

		// 编辑框
		tv = new EditText(ctx);
		fl.addView(tv, FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);

		tv.setHint(hint.str());
		tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		tv.setSingleLine(true);
		tv.setTextColor(Color.BLACK);
		tv.setId(input_id.id());
		Drawable d = ResConstants.CCImg.getStateListDrawable(ctx, ResConstants.CCImg.LOGIN_TEXT_BG_DEFAULT, ResConstants.CCImg.LOGIN_TEXT_BG_PRESSED);
		tv.setBackgroundDrawable(d);
		tv.setPadding(r.left, r.top, r.right, r.bottom);

		// icon
		tv.setCompoundDrawablesWithIntrinsicBounds(icon.getDrawble(ctx), null, null, null);
		tv.setCompoundDrawablePadding(r.left);


		// TODO: 删除按钮

		return tv;
	}

	private Button createView_button(Context ctx, LinearLayout rv, ResConstants.ZZStr txt, IDC id, Drawable bg) {
		LinearLayout ll = create_normal_pannel(ctx, rv);
		Button bt = new Button(ctx);
		ll.addView(bt);
		bt.setId(id.id());
		bt.setBackgroundDrawable(bg);
		bt.setTextColor(ResConstants.Config.ZZFontColor.CC_RECHARGE_COMMIT.color());
		bt.setOnClickListener(this);
		bt.setText(txt.str());
		ResConstants.Config.ZZDimenRect.CC_RECHARGE_COMMIT.apply_padding(bt);
		ResConstants.Config.ZZFontSize.CC_RECHARGE_COMMIT.apply(bt);
		return bt;
	}

	/*创建主视图*/
	private View createView_main(Context ctx) {
		LinearLayout rv = new LinearLayout(ctx);
		rv.setOrientation(VERTICAL);
		ResConstants.Config.ZZDimenRect.CC_ROOTVIEW_PADDING.apply_padding(rv);

		TextView tv;

		//帐号栏
		{
			LinearLayout ll = create_normal_pannel(ctx, rv);
			tvAccount = createView_input(ll, ctx, ResConstants.ZZStr.CC_ANTIADDICTION_ACCOUNT_LABEL, ResConstants.CCImg.USER_ICON, ResConstants.ZZStr.CC_ANTIADDICTION_ACCOUNT_HINT, IDC.ET_ACCOUNT);
			tvAccount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(45)});
		}

		//密码栏
		{
			LinearLayout ll = create_normal_pannel(ctx, rv);
			tvPasswd = createView_input(ll, ctx, ResConstants.ZZStr.CC_ANTIADDICTION_PASSWD_LABEL, ResConstants.CCImg.PWD_ICON, ResConstants.ZZStr.CC_ANTIADDICTION_PASSWD_HINT, IDC.ET_PASSWORD);
			tvPasswd.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
		}

		// 防沉迷选项
		{
			LinearLayout ll = create_normal_pannel(ctx, rv);
			tv = create_normal_label(ctx, ResConstants.ZZStr.CC_ANTIADDICTION_CHOOSE_LABEL);
			ResConstants.Config.ZZDimenRect.CC_LABEL_PADDING.apply_padding(tv);
			ll.addView(tv);
			{
				RadioGroup radioGroup = new RadioGroup(ctx);
				radioGroup.setOrientation(HORIZONTAL);
				rgState = radioGroup;
				if (DEBUG_UI) radioGroup.setBackgroundColor(0xcc804000);
				ll.addView(radioGroup, new LayoutParams(LP_MW));
				radioGroup.setId(IDC.RADIO_GROUP.id());
				Rect r = ResConstants.Config.ZZDimenRect.CC_ROOTVIEW_PADDING.rect();
				radioGroup.setPadding(r.left, 0, r.right, 0);

				{
					RadioButton rb = new RadioButton(ctx);
					radioGroup.addView(rb, new RadioGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f));
					Drawable d = ResConstants.CCImg.getStateRadioDrawable(ctx, ResConstants.CCImg.ANTIADDICTION_RADIO_NOR, ResConstants.CCImg.ANTIADDICTION_RADIO_SEL);
					rb.setButtonDrawable(d);
					rb.setPadding(d.getIntrinsicWidth() + ResConstants.Config.ZZDimen.dip2px(6), 0, 0, 0);
					rb.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
					rb.setTextColor(ResConstants.Config.ZZFontColor.CC_RECHARGE_NORMAL.color());
					rb.setText(ResConstants.ZZStr.CC_ANTIADDICTION_CHOOSE_ADULT.str());
					rb.setId(IDC.RB_ADULT.id());
					if (DEBUG_UI) rb.setBackgroundColor(0xcc40a000);
				}
				{
					RadioButton rb = new RadioButton(ctx);
					radioGroup.addView(rb, new RadioGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f));
					Drawable d = ResConstants.CCImg.getStateRadioDrawable(ctx, ResConstants.CCImg.ANTIADDICTION_RADIO_NOR, ResConstants.CCImg.ANTIADDICTION_RADIO_SEL);
					rb.setButtonDrawable(d);
					rb.setPadding(d.getIntrinsicWidth() + ResConstants.Config.ZZDimen.dip2px(6), 0, 0, 0);
					rb.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
					rb.setTextColor(ResConstants.Config.ZZFontColor.CC_RECHARGE_NORMAL.color());
					rb.setText(ResConstants.ZZStr.CC_ANTIADDICTION_CHOOSE_NONAGE.str());
					rb.setId(IDC.RB_NONAGE.id());
					if (DEBUG_UI) rb.setBackgroundColor(0xcc80a000);

				}
			}
		}


		/*进行验证*/
		{
			Drawable d = ResConstants.CCImg.getStateListDrawable(ctx, ResConstants.CCImg.ANTIADDICTION_COMMIT_LINK, ResConstants.CCImg.ANTIADDICTION_COMMIT_HOVER);
			Button bt = createView_button(ctx, rv, ResConstants.ZZStr.CC_ANTIADDICTION_BT_COMMIT, IDC.BT_COMMIT, d);
		}


		/*以后再说*/
		{
			Drawable d = ResConstants.CCImg.getStateListDrawable(ctx, ResConstants.CCImg.ANTIADDICTION_CANCEL_LINK, ResConstants.CCImg.ANTIADDICTION_CANCEL_HOVER);
			Button bt = createView_button(ctx, rv, ResConstants.ZZStr.CC_ANTIADDICTION_BT_CANCEL, IDC.BT_CANCEL, d);
		}

		// 其它描述文本
		{
			LinearLayout ll = create_normal_pannel(ctx, rv);

			/*分隔线*/
			{
				ImageView iv = new ImageView(ctx);
				iv.setImageDrawable(ResConstants.CCImg.ANTIADDICTION_DOTLINE.getDrawble(ctx));
				ResConstants.Config.ZZDimenRect.CC_LABEL_PADDING.apply_padding(iv);
				ll.addView(iv, new LayoutParams(LP_MW));
			}

			{
				LinearLayout ll2 = new LinearLayout(ctx);
				ll2.setOrientation(HORIZONTAL);
				ll.addView(ll2, new LayoutParams(LP_MW));
				ll2.setGravity(Gravity.TOP);

				ImageView iv = new ImageView(ctx);
				iv.setImageDrawable(ResConstants.CCImg.ANTIADDICTION_TIP.getDrawble(ctx));
				iv.setScaleType(ImageView.ScaleType.CENTER);
				ll2.addView(iv, new LayoutParams(LP_WW));
				ResConstants.Config.ZZDimenRect.CC_PANEL_PADDING.apply_padding(iv);

				tv = new TextView(ctx);
				tv.setSingleLine(false);
				tv.setTextColor(ResConstants.Config.ZZFontColor.CC_RECHARGE_DESC.color());
				tv.setText(Html.fromHtml(ResConstants.ZZStr.CC_ANTIADDICTION_DESC_HTML.str()));
				ll2.addView(tv, new LayoutParams(LP_MW));
				tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				ResConstants.Config.ZZFontSize.CC_RECHARGE_HELP.apply(tv);
				ResConstants.Config.ZZDimenRect.CC_LABEL_PADDING.apply_padding(tv);
			}
		}
		return rv;
	}

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		if (ret) {
			tvAccount.setText(mLoginName);
			tvPasswd.setText(mPassword);
			if (mLogined) {
				tvAccount.setEnabled(false);
				tvPasswd.setEnabled(false);
			}
			if (mState == 1) {
				rgState.check(IDC.RB_NONAGE.id());
			} else if (mState == 2) {
				rgState.check(IDC.RB_ADULT.id());
			}
		}
		return ret;
	}


	@Override
	public void onClick(View v) {
		final int id = v.getId();
		IDC idc = IDC.fromID(id);
		switch (idc) {
			case BT_CANCEL:
				onCancel();
				break;
			case BT_COMMIT:
				tryCommit();
				break;
			default:
				super.onClick(v);
				break;
		}
	}

	private void onCancel() {
		getHost().back();
	}

	private void tryCommit() {
		if (!checkCommit()) {
			return;
		}

		// TODO: 启动任务+显示等待窗
	}

	private boolean checkCommit() {
		do {
			String name = tvAccount.getText().toString().trim();
			if (name.length() == 0) {
				showToast("请输入账号！");
				break;
			}

			String passwd = tvPasswd.getText().toString().trim();
			if (passwd.length() == 0) {
				showToast("请输入密码！");
				break;
			}

			int id = rgState.getCheckedRadioButtonId();
			if (id == IDC.RB_ADULT.id()) {
				mState = 2;
			} else if (id == IDC.RB_NONAGE.id()) {
				mState = 1;
			} else {
				showToast("请选择当前年龄状态！");
				break;
			}

			mLoginName = name;
			mPassword = passwd;
			return true;
		} while (false);
		return false;
	}

}
