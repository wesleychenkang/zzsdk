package com.zz.sdk.layout;

import android.content.Context;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.Utils;

/**
 * 登录主界面
 * 
 * @author nxliao
 * 
 */
class LoginMainLayout extends BaseLayout {

	protected static enum IDC implements IIDC {
		
		ACT_ERR,
		
		ACT_NORMAL,
		
		ACT_LOGIN,
		
		ACT_RIGHSTER,
		
		ACT_MODIFY_PASSWORD,
		
		
		BT_REGISTER, 
		
		BT_LOGIN, BT_QUICK_LOGIN,

		
		
		_MAX_, RG_ACCOUNT_TYPE ;

		protected final static int __start__ = BaseLayout.IDC._MAX_.id();

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

	public LoginMainLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
	}

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
	}

	private void createView_login(Context ctx) {

	}

	private void createView_loginFirst(Context ctx) {

	}

	private void createView_modifyPasswd(Context ctx) {

	}

	private void createView_regist(Context ctx) {

	}

	private void show_auto_login_wait(Context ctx) {

	}

	@Override
	protected void onInitUI(Context ctx) {
		set_child_visibility(BaseLayout.IDC.ACT_TITLE, GONE);

		FrameLayout rv = getSubjectContainer();

		final boolean isVertical = Utils.isOrientationVertical(getContext());

		int widthPixels = getResources().getDisplayMetrics().widthPixels;
		int heightPixels = getResources().getDisplayMetrics().heightPixels;
		int weight1 = widthPixels * 4 / 5;
		int weight2 = widthPixels * (isVertical ? 8 : 7) / 8;

		setOrientation(VERTICAL);
		// 整体背景图
		rv.setBackgroundDrawable(BitmapCache.getDrawable(ctx,
				(isVertical ? Constants.ASSETS_RES_PATH_VERTICAL
						: Constants.ASSETS_RES_PATH) + "bg.jpg"));
		setWeightSum(1.0f);

		LinearLayout layout1 = new LinearLayout(ctx);
		layout1.setOrientation(HORIZONTAL);
		layout1.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(weight1,
				0);
		lp1.weight = 0.27f;
		rv.addView(layout1, lp1);

		ImageView logo = new ImageView(ctx);
		logo.setImageDrawable(BitmapCache.getDrawable(ctx,
				Constants.ASSETS_RES_PATH + "logo.png"));
		LinearLayout.LayoutParams lpLogo = new LinearLayout.LayoutParams(-2, -2);
		layout1.addView(logo, lpLogo);

		LinearLayout layout2 = new LinearLayout(ctx);
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(weight2,
				0);
		lp2.weight = 0.73f;
		rv.addView(layout2, lp2);
		layout2.setGravity(Gravity.RIGHT);

		LinearLayout content = new LinearLayout(ctx);
		content.setBackgroundDrawable(BitmapCache.getDrawable(ctx,
				Constants.ASSETS_RES_PATH + "heibg_03.png"));
		layout2.addView(content);

		content.setPadding(ZZDimen.dip2px(15), ZZDimen.dip2px(20),
				ZZDimen.dip2px(15), ZZDimen.dip2px(15));
		content.setOrientation(VERTICAL);

		// 注册按钮
		Button btnRegister = new Button(ctx);
		btnRegister.setId(IDC.BT_REGISTER.id());
		btnRegister.setOnClickListener(this);
		boolean hasAccount = true;
		if (hasAccount ) {
			btnRegister.setBackgroundDrawable(Utils.getStateListDrawable(ctx,
					"zhuce1.png", "zhuce.png"));
		} else {
			btnRegister.setBackgroundDrawable(Utils.getStateListDrawable(ctx,
					"zhuce3.png", "zhuce2.png"));
		}

		// 登录
		Button btnLogin = new Button(ctx);
		btnLogin.setId(IDC.BT_LOGIN.id());
		btnLogin.setOnClickListener(this);
		if (hasAccount) {
			btnLogin.setBackgroundDrawable(Utils.getStateListDrawable(ctx,"game1.png",
					"game.png"));
		} else {
			btnLogin.setBackgroundDrawable(Utils.getStateListDrawable(ctx,"dlu1.png",
					"dlu.png"));
		}

		// 快速登录
		Button btnQuickLogin = new Button(ctx);
		btnQuickLogin.setId(IDC.BT_QUICK_LOGIN.id());
		btnQuickLogin.setOnClickListener(this);
		btnQuickLogin.setBackgroundDrawable(Utils.getStateListDrawable(ctx,"tiyan1.png",
				"tiyan.png"));
	}

}
