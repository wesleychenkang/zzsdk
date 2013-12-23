package com.zz.sdk.layout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.ParamChain;
import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.ParamChain.KeyCaller;
import com.zz.sdk.ParamChain.KeyUser;
import com.zz.sdk.ParamChain.ValType;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultRequest;
import com.zz.sdk.layout.BaseLayout.ITaskCallBack;
import com.zz.sdk.layout.PaymentListLayout.ChargeStyle;
import com.zz.sdk.layout.PaymentListLayout.IDC;
import com.zz.sdk.layout.PaymentListLayout.KeyPaymentList;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.ConnectionUtil;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.Utils;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZDimenRect;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;
public class PaymentYBLayout extends CCBaseLayout {
	
 static enum IDC implements IIDC{
	     
		/**卡号*/
		ED_CARD,
		/**密码*/
		ED_PASSWD,
		/**gridView ID*/
		ED_GRIDVIEW,
		/**余额*/
		BT_BALANCE,
		/** 余额描述文本 */
		TV_BALANCE,
		/** 余额刷新等待进度条 */
		PB_BALANCE,
		/**充值*/
		CHARGE,
		/**不能选择状态*/
		UNCLICK,
		_MAX_,
;
		@Override
		public int id() {
			// TODO Auto-generated method stub
			return ordinal();
		}
		
	}
	private ChargeStyle mChargeStyle;
	 /** 余额 */
	private Double mCoinBalance;
	private Double [] counts = new Double[]{10.0,20.0,30.0,50.0,100.0,200.0};
	/**当前选择项*/
	private int now = -1;
	/**当前选择的金额*/
	private Double nowCount;
	private Context context;
	private int type = 0;
	private Integer amount;
	private final static String cardAmount = "cardAmount";
	public PaymentYBLayout(Context context, ParamChain env) {
		super(context, env);
		this.context = context;
		initUI(context);
	}
  
	@Override
	public boolean onResume() {
		return super.onResume();
	}

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
		super.onInitEnv(ctx, env);
		Boolean b = env.get(KeyCaller.K_PAYMENT_ZYCOIN_DISABLED, Boolean.class);
		mCoinBalance = env.getParent(KeyUser.class.getName()).getOwned(
				KeyUser.K_COIN_BALANCE, Double.class);
		//boolean mPaymentTypeSkipZYCoin = (b != null && b);
		b = env.get(KeyCaller.K_PAYMENT_IS_BUY_MODE, Boolean.class);
		mChargeStyle = (b != null && b) ? ChargeStyle.BUY : ChargeStyle.RECHARGE;
		nowCount = env.get(KeyPaymentList.K_PAY_AMOUNT, Double.class);
		ZZStr title;
		if (mChargeStyle == ChargeStyle.BUY) {
			title = ZZStr.CC_RECHARGE_TITLE_SOCIAL;
		} else {
			title = ZZStr.CC_RECHARGE_TITLE;
		}
		amount = env.get(KeyCaller.K_AMOUNT, Integer.class);
		env.add(KeyPaymentList.K_PAY_TITLE, title, ValType.TEMPORARY);
		
	}
	@Override
	protected void onInitUI(Context ctx) {
		type = getEnv().get(KeyPaymentList.K_PAY_CHANNELTYPE,Integer.class);
		hideView_footer();
		setTileTypeText(getEnv().getOwned(KeyPaymentList.K_PAY_TITLE,
				ZZStr.class).str());
	    FrameLayout frame = getSubjectContainer();
	     
	    LinearLayout all = new LinearLayout(ctx);
	    all.setOrientation(LinearLayout.VERTICAL);
	    frame.addView(all,new LayoutParams(LP_MM));
	    
	    LinearLayout top = new LinearLayout(ctx);
	    top.setOrientation(LinearLayout.VERTICAL);
	    createView_balance(ctx, top);
	    all.addView(top,new LayoutParams(LP_MW));
	    
	    
	    LinearLayout lybuttom = new LinearLayout(ctx);
	    lybuttom.setOrientation(LinearLayout.VERTICAL);

	    ScrollView sroll = new ScrollView(ctx);
	    sroll.setVerticalScrollBarEnabled(false);
	    sroll.setFillViewport(true);
	    sroll.addView(lybuttom,new LayoutParams(LP_MM));
	    all.addView(sroll,new LayoutParams(LP_MW));
	    if(mChargeStyle!=ChargeStyle.BUY ||(mChargeStyle==ChargeStyle.BUY && amount!=null && amount>0)){
	    	preparText(ctx,lybuttom);
	    	preparGridView(ctx,lybuttom);
	    }
	    prepparePayType_Card(ctx,lybuttom,type);
	    preparButton(ctx,lybuttom);
	    preparePayDec(ctx,lybuttom);
	}

	@Override
	public boolean onEnter() {
		resetExitTrigger();
		return super.onEnter();
		
	}
     
	@Override
	public void onClick(View v) {
	   if(v.getId()==IDC.CHARGE.id()){
			 charger();
		 }
		super.onClick(v);
	}
    // 充值卡充值
	private void charger() {
		String erro = checkInput();
	    if(erro==null){
		showPopup_Wait(ZZStr.CC_TRY_CONNECT_SERVER.str(),
				new SimpleWaitTimeout() {
					public void onTimeOut() {
						setExitTrigger(-1, null);
						showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
					}
		      });
		ITaskCallBack cb = new ITaskCallBack() {
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token,
					BaseResult result) {
				if (isCurrentTaskFinished(task)) {
					if (result==null || !result.isUsed()) {
						// 连接服务器失败
						showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
					}else{
						ResultRequest s = (ResultRequest)result;
						getEnv().add(KeyPaymentList.K_PAY_ORDERNUMBER, s.mCmgeOrderNum,
								ValType.TEMPORARY);
						// 支付成功  
					    notifyPayResult(getEnv(),MSG_STATUS.SUCCESS);
					    showPayResult(getEnv(), MSG_STATUS.SUCCESS);  
					    // 通知用户
						
					}

				}
			}

			
		};
		int type = 1; // 此type 要从上一个界面传入是哪种支付类型
		AsyncTask<?, ?, ?> task = PayTask.createAndStart(getConnectionUtil(),
			cb, type,genPayParam(context,getEnv()));
		setCurrentTask(task);
	   }else{
		   
		showToast(erro);
		   
	   }
	}
	private void notifyPayResult(ParamChain env, int state) {
		int code;
		switch (state) {
		case MSG_STATUS.SUCCESS:
			code = PaymentCallbackInfo.STATUS_SUCCESS;
			break;
		case MSG_STATUS.FAILED:
			code = PaymentCallbackInfo.STATUS_FAILURE;
			break;
		case MSG_STATUS.CANCEL:
		default:
			code = PaymentCallbackInfo.STATUS_CANCEL;
			break;
		}

		PaymentCallbackInfo info = new PaymentCallbackInfo();

		Object price = env.remove(KeyPaymentList.K_PAY_RESULT_PRICE);
		Double amount = (price instanceof Double) ? (Double) price : env.get(
				KeyPaymentList.K_PAY_AMOUNT, Double.class);
		info.amount = amount == null ? null : Utils.price2str(amount);

		info.cmgeOrderNumber = env.get(KeyPaymentList.K_PAY_ORDERNUMBER,
				String.class);
		info.statusCode = code;

		Integer payWayType = env.get(KeyPaymentList.K_PAY_CHANNELTYPE,
				Integer.class);
		info.payWayType = payWayType == null ? -1 : payWayType;
		info.payWayName = env.get(KeyPaymentList.K_PAY_CHANNELNAME,
				String.class);

		info.currency = "RMB";

		notifyCaller(MSG_TYPE.PAYMENT, state, info);
	}
	private void showPayResult(ParamChain env, int state) {
		final ZZStr str;
		boolean autoclose; // 是否自动关闭
		switch (state) {
		case MSG_STATUS.SUCCESS: {
			str = (mChargeStyle != null && mChargeStyle == ChargeStyle.RECHARGE) ? ZZStr.CC_RECHARGE_RESULT_SUCCESS_ZYCOIN : ZZStr.CC_RECHARGE_RESULT_SUCCESS;
//			Boolean autoClose = (env == null) ? null : env.get(
//					KeyCaller.K_IS_CLOSE_WINDOW, Boolean.class);
//			if (autoClose != null && autoClose) {
//				autoclose = true;
//			} else {
//				autoclose = false;
//			}
		}
			break;

		case MSG_STATUS.FAILED:
			str = ZZStr.CC_RECHARGE_RESULT_FAILED;
			autoclose = false;
			break;

		case MSG_STATUS.CANCEL:
		case MSG_STATUS.EXIT_SDK:
		default:
			str = null;
			autoclose = false;
			break;
		}
		if (str != null) {
//			set_child_focuse(IDC.ACT_PAY_GRID);
			autoclose = true;
			showPopup_Tip(!autoclose, str);
			if (autoclose) {
				removeExitTrigger();
				postDelayed(new Runnable() {
					@Override
					public void run() {
						hidePopup();
						callHost_back();
					}
				}, 1500);
			} else {
				resetExitTrigger();
			}
		} else {
			hidePopup();
			// 强制让支付方式获取焦点，修改　
			//   "华为U9200手机上：从话费支付返回到支付列表界面时，列表不可操作"　
			// 的问题.  nxliao 2013.11.14
			//set_child_focuse(IDC.ACT_PAY_GRID);
		}
	}
	protected void resetExitTrigger() {
		setExitTrigger(-1, null);
	}
	private String checkInput() {
		String ret = null;
		String cardNumber = ((EditText)findViewById(IDC.ED_CARD.id())).getText().toString();
		String password = ((EditText)findViewById(IDC.ED_PASSWD.id())).getText().toString();
		if(cardNumber==null ||cardNumber.length()==0){
			ret = ZZStr.CC_CARDNUM_CHECK_FAILED.str();
			
			return ret;
		}
		if(null ==password || password.length()==0){
			ret = ZZStr.CC_PASSWD_CHECK_FAILED.str();
			return ret;
		}
		if(now == -1){
			ret = "请选择充值卡面额";
			return ret;
		}
		getEnv().add(KeyPaymentList.K_PAY_CARD, cardNumber, ValType.TEMPORARY);
		getEnv().add(KeyPaymentList.K_PAY_CARD_PASSWD, password,
				ValType.TEMPORARY);
		ret = null;
		return ret;
		
	}
	private static PayParam genPayParam(Context ctx, ParamChain env) {
		PayParam payParam = new PayParam();
		payParam.loginName = env.get(KeyUser.K_LOGIN_NAME, String.class);
		String loginName = payParam.loginName;
		payParam.gameRole = env.get(KeyCaller.K_GAME_ROLE, String.class);
		payParam.serverId = env.get(KeyCaller.K_GAME_SERVER_ID, String.class);
		payParam.projectId = Utils.getProjectId(ctx);
		Double amount = env.get(KeyPaymentList.K_PAY_AMOUNT, Double.class);
		payParam.amount = Utils.price2str(amount == null ? 0 : amount);
		payParam.requestId = "";
		payParam.cardAmount = String.valueOf(env.get(cardAmount,Integer.class));
		payParam.cardNo = env.get(KeyPaymentList.K_PAY_CARD,String.class);
		payParam.cardPassword = env.get(KeyPaymentList.K_PAY_CARD_PASSWD,String.class);
		payParam.type = String.valueOf(env.get(KeyPaymentList.K_PAY_CHANNELTYPE,Integer.class));
		return payParam;
	}
	
	// 充值卡金额
	private void preparText(Context ctx, LinearLayout layout){
		TextView txt = create_normal_label(ctx, ZZStr.YB_TEXT_VALUE);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lp.topMargin = ZZDimen.dip2px(8);
		layout.addView(txt,lp);
		ZZDimenRect.CC_YB_TEXT.apply_padding(txt);
	}
	//立即充值
	private void preparButton(Context ctx,LinearLayout layout){
		Button btn = new Button(ctx);
		btn.setId(IDC.CHARGE.id());
		Rect rc = ZZDimenRect.CC_ROOTVIEW_PADDING.rect();
		LayoutParams lp = new LayoutParams(LP_MW);
		lp.setMargins(rc.left, rc.bottom, rc.right, 0);
		btn.setBackgroundDrawable(BitmapCache.getStateListDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/btn_login_pressed.9.png", Constants.ASSETS_RES_PATH + "drawable/btn_login_default.9.png"));
		btn.setText(ZZStr.CC_COMMIT_RECHARGE.str());
		btn.setOnClickListener(this);
		layout.addView(btn,lp);
	}
	
    private void  preparGridView(Context ctx,LinearLayout layout){
		GridView grid = new TypeGridView(ctx);
		layout.addView(grid);
		grid.setId(IDC.ED_GRIDVIEW.id());
		grid.setNumColumns(GridView.AUTO_FIT);
		grid.setSelector(android.R.color.transparent);
		grid.setColumnWidth(ZZDimen.CC_GRIDVIEW_COLUMN_WIDTH.px());
		Rect rc = ZZDimenRect.CC_TITLE_BT_PADDING.rect();
		grid.setPadding(rc.left, rc.top, rc.right, rc.bottom);
		final YBListAdapter l = new YBListAdapter(ctx);
		grid.setAdapter(l);
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			  if(arg1.getId()!=IDC.UNCLICK.id()){
				 
				  l.updateUI(arg2);
			  }
			}
		});
		
	}
	/** 卡号输入面板 */
	private void prepparePayType_Card(Context ctx, LinearLayout rv,int type) {
		int limitCard = 0 ;
		int limitPasswd = 0;
		switch(type){
		case PayChannel.PAY_TYPE_YEEPAY_LT:
			limitCard =15;
			limitPasswd = 19;
			break;
		case PayChannel.PAY_TYPE_YEEPAY_DX:
			limitCard =19;
			limitPasswd = 18;
		  break;
		case PayChannel.PAY_TYPE_YEEPAY_YD:
			limitCard =17;
			limitPasswd = 18;
		  break;
		}
		TextView tv;
		tv = create_normal_label(ctx, ZZStr.YB_DECE_NUMBER);
		LayoutParams ltv = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		rv.addView(tv, ltv);
		ZZDimenRect.CC_YB_TEXT.apply_padding(tv);

		// 卡号
		tv = create_normal_input(ctx, null, ZZFontColor.CC_RECHARGE_INPUT,
				ZZFontSize.CC_RECHARGE_INPUT, limitCard);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		Rect rc = ZZDimenRect.CC_RECHARGE_INPUT.rect();
		lp.setMargins(rc.left,0, rc.right,0);
		rv.addView(tv,lp);
		tv.setId(IDC.ED_CARD.id());
		tv.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		tv.setBackgroundDrawable(BitmapCache.getStateListDrawable(ctx, 
	    Constants.ASSETS_RES_PATH + "drawable/login_text_bg_pressed.9.png", Constants.ASSETS_RES_PATH + "drawable/login_text_bg_default.9.png"));
	
		if (limitCard > 0) {
			String hint = String.format(ZZStr.CC_CARDNUM_HINT.str(), limitCard);
			tv.setHint(hint);
		}
		ZZDimenRect.CC_YB_EDIT.apply_padding(tv);
		tv = create_normal_label(ctx, ZZStr.YB_DECE_PWD);
		rv.addView(tv, new LayoutParams(LP_WW));
		ZZDimenRect.CC_YB_TEXT.apply_padding(tv);

		// 密码
		tv = create_normal_input(ctx, null, ZZFontColor.CC_RECHARGE_INPUT,
				ZZFontSize.CC_RECHARGE_INPUT, limitPasswd);
		rv.addView(tv,lp);
		tv.setId(IDC.ED_PASSWD.id());
		tv.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		tv.setBackgroundDrawable(BitmapCache.getStateListDrawable(ctx, 
		Constants.ASSETS_RES_PATH + "drawable/login_text_bg_pressed.9.png", Constants.ASSETS_RES_PATH + "drawable/login_text_bg_default.9.png"));
		if (limitPasswd > 0) {
			String hint = String.format(ZZStr.CC_CARDNUM_HINT.str(),
					limitPasswd);
			tv.setHint(hint);
		}
		ZZDimenRect.CC_YB_EDIT.apply_padding(tv);
	}
    
	private void preparePayDec(Context ctx,LinearLayout rv){
		TextView txtp = new TextView(ctx);
		txtp.setText("温馨提示:");
		txtp.setTextColor(Color.rgb(72, 145, 44));
		rv.addView(txtp);
		ZZDimenRect.CC_YB_TEXT.apply_padding(txtp);
		
		TextView txt_one = new TextView(ctx);
		txt_one.setText("1. 所选面额喝充值卡面额不符合时,卡内余额将充入卓越币");
		ZZDimenRect.CC_YB_TEXT.apply_padding(txt_one);
		txt_one.setTextColor(ZZFontColor.CC_RECHARGE_COST.color());
		rv.addView(txt_one);
		
		TextView txt_two = new TextView(ctx);
		txt_two.setText("2. 1元=1卓越币,一般1-10分钟即可到账,请放心充值");
		ZZDimenRect.CC_YB_TEXT.apply_padding(txt_two);
		rv.addView(txt_two);
		
		TextView txt_three = new TextView(ctx);
		txt_three.setText("3. 客服热线 :"+"4007555999"+ "  客服QQ:"+"4008848808");
		ZZDimenRect.CC_YB_TEXT.apply_padding(txt_three);
		rv.addView(txt_three);
		
	}

    class YBListAdapter extends BaseAdapter{
    	private int mItemHeight;
        private Context ctx;
        public YBListAdapter(Context ctx){
        	mItemHeight = ZZDimen.CC_GRIDVIEW_ITEM_HEIGHT.px();
        	this.ctx = ctx;
        }
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return counts.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return counts[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			TextView txtholder = (TextView) convertView;
			if(txtholder==null){
			 txtholder = new TextView(ctx);
			 txtholder.setText(counts[arg0].intValue()+"元");
			 txtholder.setGravity(Gravity.CENTER);
			 txtholder.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,mItemHeight));
			 }
			 if(arg0 == now){
			 txtholder.setBackgroundDrawable(CCImg.YB_BACKPRESS.getDrawble(ctx));
			 }else if(counts[arg0]<nowCount){
		     txtholder.setBackgroundDrawable(CCImg.YB_BACK_UNPRESS.getDrawble(ctx));
			 txtholder.setId(IDC.UNCLICK.id());
		     }else{
			 txtholder.setBackgroundDrawable(CCImg.getStateListDrawable(ctx, CCImg.YB_BACKDEFAULT, CCImg.YB_BACKPRESS));  
			 }
			 ZZDimenRect.CC_GRIDVIEW_ITEM_PADDDING.apply_padding(txtholder);
			return txtholder;
		}
		
		public void updateUI(int count){
			now = count;
			System.out.println();
			getEnv().add(cardAmount, counts[now].intValue());
		    notifyDataSetChanged();
	    }
    	
    }
    /**
	 * 自定义的 GridView
	 */
	static final class TypeGridView extends GridView {

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

	private static class PayTask extends AsyncTask<Object, Void, ResultRequest> {
		protected static AsyncTask<?, ?, ?> createAndStart(ConnectionUtil cu,
				ITaskCallBack callback, int type, PayParam charge) {
			PayTask task = new PayTask();
			task.execute(cu, callback, type, charge);
			if (DEBUG) {
				Logger.d("PayTask: created!");
			}
			return task;
		}
		private ITaskCallBack mCallback;
		private Object mToken;
        
		@Override
		protected ResultRequest doInBackground(Object... params) {
			ConnectionUtil cu = (ConnectionUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			int type = PayChannel.PAY_TYPE_YEEPAY_DX;
			PayParam charge = (PayParam) params[3];

			if (DEBUG) {
				Logger.d("PayTask: run!");
			}

			ResultRequest ret = cu.charge(type, charge);
			if (!this.isCancelled()) {
				mCallback = callback;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(ResultRequest result) {
			if (DEBUG) {
				Logger.d("PayTask: result!");
			}
			if (mCallback != null) {
				mCallback.onResult(this, mToken, result);
			}
			mCallback = null;
			mToken = null;
		}
	}
	
	
}
