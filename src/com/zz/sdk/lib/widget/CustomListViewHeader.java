package com.zz.sdk.lib.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.ZZStr;

/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
public class CustomListViewHeader extends LinearLayout
{
	protected LinearLayout mContainer;
	protected ImageView mArrowImageView;
	protected ProgressBar mProgressBar;
	protected TextView mHintTextView;
	private int mState = STATE_NORMAL;

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;
	protected RelativeLayout mHeaderViewContent;
	protected TextView txtHeaderTime;
	protected LinearLayout timeLayout;

	private final int ROTATE_ANIM_DURATION = 180;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;
//	private String listview_header_hint_normal, listview_header_hint_ready, listview_header_hint_loading;

	public CustomListViewHeader(Context context)
	{
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CustomListViewHeader(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context)
	{
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.BOTTOM);
		// 初始情况，设置下拉刷新view高度为0
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0);
		mContainer = new LinearLayout(context);
		mContainer.setGravity(Gravity.BOTTOM);
		addView(mContainer, lp);

		mHeaderViewContent = new RelativeLayout(context);
		mContainer.addView(mHeaderViewContent, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, ZZDimen.dip2px(60)));

		// 布局
		RelativeLayout.LayoutParams reLp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		reLp.addRule(RelativeLayout.CENTER_IN_PARENT);
		LinearLayout layoutHeader = new LinearLayout(context);
		layoutHeader.setId(123);
		layoutHeader.setOrientation(LinearLayout.VERTICAL);
		layoutHeader.setGravity(Gravity.CENTER_HORIZONTAL);
		mHeaderViewContent.addView(layoutHeader, reLp);

		mHintTextView = new TextView(context);
		mHintTextView.setText(ZZStr.XLISTVIEW_HEADER_HINT_NORMAL.str());
		mHintTextView.setTextColor(Color.GRAY);
		lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutHeader.addView(mHintTextView, lp);

		timeLayout = new LinearLayout(context);
		lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		timeLayout.setVisibility(View.GONE);
		lp.topMargin = ZZDimen.dip2px(3);
		layoutHeader.addView(timeLayout, lp);

		TextView textView = new TextView(context);
		textView.setTextColor(Color.GRAY);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		textView.setText(ZZStr.XLISTVIEW_HEADER_LAST_TIME.str());
		lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		timeLayout.addView(textView, lp);

		txtHeaderTime = new TextView(context);
		txtHeaderTime.setTextColor(Color.GRAY);
		txtHeaderTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		timeLayout.addView(txtHeaderTime, lp);

		mArrowImageView = new ImageView(context);
		mArrowImageView.setImageDrawable(CCImg.XLISTVIEW_ARROW.getDrawble(context));
		reLp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		reLp.leftMargin = ZZDimen.dip2px(50);
		reLp.rightMargin = ZZDimen.dip2px(20);
		reLp.addRule(RelativeLayout.CENTER_VERTICAL);
		reLp.addRule(RelativeLayout.LEFT_OF, layoutHeader.getId());
		mHeaderViewContent.addView(mArrowImageView, reLp);

		mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleSmallInverse);
		mProgressBar.setVisibility(View.GONE);
		reLp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		reLp.leftMargin = ZZDimen.dip2px(50);
		reLp.rightMargin = ZZDimen.dip2px(20);
		reLp.addRule(RelativeLayout.CENTER_VERTICAL);
		reLp.addRule(RelativeLayout.LEFT_OF, layoutHeader.getId());
		mHeaderViewContent.addView(mProgressBar, reLp);
		// mHintTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP);

		// 初始化动画效果
		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
	}

	public void setState(int state)
	{
		if (state == mState)
			return;

		if (state == STATE_REFRESHING)
		{ // 显示进度
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
		}
		else
		{ // 显示箭头图片
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
		}

		switch (state)
		{
		case STATE_NORMAL:
			if (mState == STATE_READY)
			{
				mArrowImageView.startAnimation(mRotateDownAnim);
			}
			if (mState == STATE_REFRESHING)
			{
				mArrowImageView.clearAnimation();
			}
			mHintTextView.setText(ZZStr.XLISTVIEW_HEADER_HINT_NORMAL.str());
			break;
		case STATE_READY:
			if (mState != STATE_READY)
			{
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mRotateUpAnim);
				mHintTextView.setText(ZZStr.XLISTVIEW_HEADER_HINT_READY.str());
			}
			break;
		case STATE_REFRESHING:
			mHintTextView.setText(ZZStr.XLISTVIEW_HEADER_HINT_LOADING.str());
			break;
		default:
		}

		mState = state;
	}

	public void setVisiableHeight(int height)
	{
		if (height < 0)
			height = 0;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContainer.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight()
	{
		return mContainer.getHeight();
	}

}
