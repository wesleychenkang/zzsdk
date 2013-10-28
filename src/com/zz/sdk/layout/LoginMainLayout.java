package com.zz.sdk.layout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyGlobal;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.Loading;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.Utils;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;

/**
 * 登录主界面
 * 
 * @author nxliao
 * 
 */
class LoginMainLayout extends BaseLayout {
    private AutoLoginDialog mAutoDialog;
	private FrameLayout main;
	private Handler mHandler = new Handler();
	private Activity ac;
	private Context ctx;
	private LinearLayout content;
	protected static enum IDC implements IIDC {		
		ACT_ERR,
		
		ACT_NORMAL,
		
		ACT_LOGIN,
		
		ACT_RIGHSTER,
		
		ACT_MODIFY_PASSWORD,
		
		
		BT_REGISTER, 
		
		BT_LOGIN, BT_QUICK_LOGIN,BT_UPDATE_PASSWORD,RG_ACCOUNT_TYPE ,BT_BACK,BT_CONFIRM,
		_MAX_;

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
		this.ctx = context;
		initUI(context);
	}
	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
      ac= getEnv().get(KeyGlobal.K_UI_ACTIVITY, Activity.class);
	}
	@Override
	public void onClick(View v) {
		IDC idc = IDC.fromID(v.getId());
		switch(idc){
		//注册账号
		case BT_REGISTER:
			main.removeAllViews();
			main.addView(createView_regist(ctx));
			break;
		//修改密码
		case BT_UPDATE_PASSWORD:
			main.removeAllViews();
			main.addView(createView_modifyPasswd(ctx));
			break;
		//登录
		case BT_LOGIN:
			doPost();
			break;
		//快速登录
		case BT_QUICK_LOGIN:
			
			break;
		//返回
		case BT_BACK:
			main.removeAllViews();
			main.addView(createView_login(ctx,true));
			break;
		 default:
			 super.onClick(v);
	
		}
	}
	
	
    /**
     *服务器请求
     */
	private void doPost() {

	}
	/**
	 * 服务器返回
	 */
	private void doBack(){
		
	}
	/**
	 * 创建登录  LinearLayout
	 * @param ctx 
	 * @param hasAccount 是否为第一次登录
	 * @return
	 */
	private LinearLayout  createView_login(Context ctx,boolean hasAccount) {
		LoginLayout login = new LoginLayout(ctx,this,hasAccount);
		login.setAccount("yyyyyyyy");
		login.setPassWord("yyyyyyyy");
		return login;
	}
	/**
	 * 创建修改密码LinearLayout
	 * @param ctx
	 * @return
	 */
	private View createView_modifyPasswd(Context ctx) {
		LoginUpdatePwdLayout update = new LoginUpdatePwdLayout(ctx,this);
		update.setOldPassWord("yyyyyyyyyyy");
        return update;
	}
	/**
	 * 创建注册LinearLayout
	 * @param ctx
	 * @return
	 */
	private LinearLayout createView_regist(Context ctx) {
		LoginRegisterLayout reg = new LoginRegisterLayout(ctx,this);
		return reg;
	}
	/**
	 * 显示自动游戏登录Dialog
	 */
	private void show_auto_login_wait() {
		 mAutoDialog = new AutoLoginDialog(ac);
			// 显示
         mAutoDialog.show();
			// 2秒
	     mHandler.postDelayed(doAutoLogin, 2 * 1000);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onInitUI(Context ctx) {
		set_child_visibility(BaseLayout.IDC.ACT_TITLE, VISIBLE);

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
						: Constants.ASSETS_RES_PATH) + "bj.jpg"));
	    setWeightSum(1.0f);
	    
	    LinearLayout layout1 = new LinearLayout(ctx);
		layout1.setOrientation(HORIZONTAL);
//		layout1.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(weight1, 0);
		lp1.weight = 0.27f;
		addView(layout1, lp1);
		
		ImageView logo = new ImageView(ctx);
		//logo.setImageDrawable(BitmapCache.getDrawable(mActivity, Constants.ASSETS_RES_PATH + "logo.png"));
		LinearLayout.LayoutParams lpLogo = new LinearLayout.LayoutParams(-2, -2);
		layout1.addView(logo, lpLogo);
		
		LinearLayout layout2 = new LinearLayout(ctx);
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(weight2, 0);
		lp2.weight = 0.73f;
		addView(layout2, lp2);
		layout2.setGravity(Gravity.RIGHT);
		
		

	    FrameLayout top = new FrameLayout(ctx);
	    ImageView image = new ImageView(ctx);
	    image.setImageDrawable(BitmapCache.getDrawable(ctx,Constants.ASSETS_RES_PATH+"logo2.png"));
	    top.addView(image);
	    FrameLayout.LayoutParams l = new  FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    rv.addView(top,l);
		boolean hasAccount = false;
		
		main = new FrameLayout(ctx);
		LinearLayout login = createView_login(ctx,hasAccount);
		main.addView(login);
		rv.addView(main);
		// 显示“自动登录”框
		if (hasAccount) {
			show_auto_login_wait(); 
		 }
	}

	private Runnable doAutoLogin = new Runnable() {
		@Override
		public void run() {
			// 先判断是否已经被cancel
			if (mAutoDialog.isShowing()) {
				try {
					// 取消显示
					mAutoDialog.cancel();
				} catch (Exception e) {
					Logger.d(e.getClass().getName());
				}

				// 模拟用户按下登陆按钮
				//onClick(btnLogin);
			}
		}
	};
	/**
	 * 自动登陆显示进度框
	 */
	class AutoLoginDialog extends Dialog {
		Context ctx;
		private Button cancel;
		public AutoLoginDialog(Context context) {
			super(context);
			this.ctx = context;

			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			LinearLayout content = new LinearLayout(ctx);
			// 垂直
			content.setOrientation(VERTICAL);
			content.setGravity(Gravity.CENTER_HORIZONTAL);
			content.setPadding(ZZDimen.dip2px(20), ZZDimen.dip2px(15), ZZDimen.dip2px(20), ZZDimen.dip2px(15));
			content.setBackgroundDrawable(Utils.getDrawable(ctx,"login_bg_03.png"));

			// 文字
			TextView tv = new TextView(ctx);
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			tv.setTextColor(0xfffeef00);
			tv.setText("剩下2秒自动登陆游戏");
			tv.setTextSize(18);
			//
			Loading loading = new Loading(ctx);

			cancel = new Button(ctx);
			//cancel.setId(IDC_BT_CANCEL);
			cancel.setBackgroundDrawable(Utils.getStateListDrawable(ctx,"quxiao1.png",
					"quxiao.png"));
			cancel.setOnClickListener(LoginMainLayout.this);

			content.addView(tv);
			LinearLayout.LayoutParams lploading = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lploading.topMargin = ZZDimen.dip2px(10);
			content.addView(loading, lploading);
			LinearLayout.LayoutParams lpcancel = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lpcancel.topMargin = ZZDimen.dip2px(10);
			content.addView(cancel, lpcancel);

			// 对话框的内容布局
			setContentView(content);
			setCanceledOnTouchOutside(false);

		}
	
	}
	
	}


