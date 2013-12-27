package com.zz.sdk.layout;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zz.sdk.LoginCallbackInfo;
import com.zz.sdk.ParamChain;
import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultAntiaddiction;
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.UserUtil;

/**
 * 防沉迷
 * <ul>输入：
 * <li>loginName</li>
 * <li>Password</li>
 * <li>LoginState</li>
 * <li>cmState</li>
 * </ul>
 * <ul>输出：
 * <li>与登录结果相同</li>
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

	/** 是否已经登录成功过 */
	private boolean mLogined;
	/** 默认用户名 */
	private String mLoginName;
	/** 默认密码 */
	private String mPassword;
	/* 0未设置 1未成年 2已成年 */
	private int mState;
	private boolean mHashVerify;

	private TextView tvAccount, tvPasswd;
	private RadioGroup rgState;

	private final int DEF_ANTI_STATE = LoginCallbackInfo.STATUS_AA_ADULT;

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
		mState = i == null ? DEF_ANTI_STATE : i;
		mHashVerify = false;
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
		tv = create_normal_input(ctx, hint, ResConstants.Config.ZZFontColor.CC_RECHARGE_INPUT, ResConstants.Config.ZZFontSize.CC_RECHARGE_INPUT, 0);
		fl.addView(tv, FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);

		tv.setId(input_id.id());
		Drawable d = ResConstants.CCImg.getStateListDrawable(ctx, ResConstants.CCImg.LOGIN_TEXT_BACK_DEFAULT, ResConstants.CCImg.LOGIN_TEXT_BACK_PRESS);
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

	@Override
	protected void clean() {
		super.clean();

		mLoginName = null;
		mPassword = null;
	}

	@Override
	public boolean isExitEnabled(boolean isBack) {
		return mHashVerify || super.isExitEnabled(isBack);
	}

	private void onCancel() {
		if (mHashVerify) return;
		if (isAlive()) callHost_back();
	}

	/** 验证成功，需要将结果返回到 {@link com.zz.sdk.layout.LoginMainLayout} */
	private void onSuccess(ResultAntiaddiction ra) {
		ParamChain env = getEnv().get(ParamChain.KeyGlobal.K_CALLER_ENV, ParamChain.class);
		env.add(LoginMainLayout.KeyLogin.K_ANTIADDICTION, ra);
		mHashVerify = true;
		hidePopup();
		showPopup_Tip("验证成功！");
		removeExitTrigger();
		postDelayed(new Runnable() {
			@Override
			public void run() {
				if (isAlive()) {
					removeExitTrigger();
					callHost_back();
				}
			}
		}, 3500
		);
	}

	private void tryCommit() {
		if (mHashVerify) return;
		if (!isAlive()) return;

		if (!checkCommit()) {
			return;
		}

		// 启动任务+显示等待窗

		showPopup_Wait("正在进行验证……", new SimpleWaitTimeout() {
			public void onTimeOut() {
				onAntiTimeout();
			}
		}
		);
		setExitTrigger(-1, "正在进行验证……");

		ITaskCallBack cb = new ITaskCallBack() {
			@Override
			public void onResult(
					AsyncTask<?, ?, ?> task, Object token,
					BaseResult result) {
				if (isCurrentTaskFinished(task)) {
					onAnitResult(result);
				}

			}
		};
		AsyncTask<?, ?, ?> task = AntiAddictionTask.createAndStart(
				UserUtil.getInstance(getContext()), cb, this, mLoginName, mPassword, mState
		);
		setCurrentTask(task);
	}

	/** 登录超时 */
	private void onAntiTimeout() {
		removeExitTrigger();
		showPopup_Tip(ResConstants.ZZStr.CC_TRY_CONNECT_SERVER_TIMEOUT);
	}

	private void onAnitResult(BaseResult result) {
		hidePopup();
		if (result.isSuccess()) {
			ResultAntiaddiction ra = (ResultAntiaddiction) result;
			onSuccess(ra);
		} else {
			if (result.isUsed()) {
				showPopup_Tip(result.getErrDesc());
			} else {
				showPopup_Tip(ResConstants.ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
			}
		}
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

	private static class AntiAddictionTask extends AsyncTask<Object, Void, ResultAntiaddiction> {
		protected static AsyncTask<?, ?, ?> createAndStart(
				UserUtil uu, ITaskCallBack callback, Object token, String loginName, String password, int state) {
			AntiAddictionTask task = new AntiAddictionTask();
			task.execute(uu, callback, token, loginName, password, state);
			return task;
		}

		private ITaskCallBack mCallback;
		private Object mToken;

		@Override
		protected ResultAntiaddiction doInBackground(Object... params) {
			UserUtil uu = (UserUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];

			String loginName = (String) params[3];
			String password = (String) params[4];
			int state = (Integer) params[5];

			ResultAntiaddiction ret = uu.anti_addiction(loginName, password, state);
			if (!this.isCancelled()) {
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(ResultAntiaddiction result) {
			if (mCallback != null) {
				mCallback.onResult(this, mToken, result);
			}
			mCallback = null;
			mToken = null;
		}
	}

}
