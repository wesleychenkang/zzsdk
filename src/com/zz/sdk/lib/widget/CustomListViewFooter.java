package com.zz.sdk.lib.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.ZZStr;

/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
public class CustomListViewFooter extends LinearLayout
{
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;

	private RelativeLayout mContentView;
	private View mProgressBar;
	private TextView mHintView;

//	private String listview_footer_hint_ready, listview_footer_hint_normal, listview_footer_hint_loading;

	public CustomListViewFooter(Context context)
	{
		super(context);
		initView(context);
	}

	public CustomListViewFooter(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public void setState(int state)
	{
		mHintView.setVisibility(View.VISIBLE);
		if (state == STATE_READY)
		{
			mProgressBar.setVisibility(View.GONE);
			mHintView.setText(ZZStr.XLISTVIEW_FOOTER_HINT_READY.str());
		}
		else if (state == STATE_LOADING)
		{
			mProgressBar.setVisibility(View.VISIBLE);
			mHintView.setText(ZZStr.XLISTVIEW_FOOTER_HINT_LOADING.str());
		}
		else
		{
			mProgressBar.setVisibility(View.GONE);
			mHintView.setText(ZZStr.XLISTVIEW_FOOTER_HINT_NORMAL.str());
		}
	}

	public RelativeLayout getContentView()
	{
		return mContentView;
	}

	public void setBottomMargin(int height)
	{
		if (height < 0)
			return;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}

	public int getBottomMargin()
	{
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		return lp.bottomMargin;
	}

//	/**
//	 * normal status
//	 */
//	public void normal()
//	{
//		mProgressBar.setVisibility(View.GONE);
//	}
//
//	/**
//	 * loading status
//	 */
//	public void loading()
//	{
//		mProgressBar.setVisibility(View.VISIBLE);
//	}

	/**
	 * hide footer when disable pull load more
	 */
	public void hide()
	{
		mHintView.setVisibility(View.VISIBLE);
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		lp.height = 0;
		mContentView.setLayoutParams(lp);
	}

	/**
	 * show footer
	 */
	public void show()
	{

		mHintView.setVisibility(View.GONE);
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
	}

	private void initView(Context context)
	{
		mContentView = new RelativeLayout(context);
		int padding = ZZDimen.dip2px(12);
		// 布局
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		mHintView = new TextView(context);
		mHintView.setVisibility(View.GONE);
		mHintView.setPadding(0, padding, 0, padding);
		mHintView.setId(android.R.id.text1);
		mHintView.setLines(1);
		mHintView.setGravity(Gravity.CENTER);
		mHintView.setTextAppearance(context, android.R.attr.textAppearanceMedium);
		mHintView.setTextColor(Color.GRAY);
		mContentView.addView(mHintView, lp);

		mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleSmallTitle);
		mProgressBar.setVisibility(View.GONE);
		// 布局
		lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.rightMargin = padding;
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		lp.addRule(RelativeLayout.LEFT_OF, mHintView.getId());
		mContentView.addView(mProgressBar, lp);

		addView(mContentView, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	}

}
