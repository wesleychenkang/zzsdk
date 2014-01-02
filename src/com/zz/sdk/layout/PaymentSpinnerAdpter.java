package com.zz.sdk.layout;

import com.zz.sdk.util.AntiAddictionUtil;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.ZZStr;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsSpinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class PaymentSpinnerAdpter extends BaseAdapter implements SpinnerAdapter {
    private Context ctx;
    private Double [] args;
    private int nowposition;
	public PaymentSpinnerAdpter(Context ctx, Double [] args){
    	this.ctx = ctx;
    	this.args = args;
    }
	
	public void updateAdpter(int nowposition){
		this.nowposition = nowposition;
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return args.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return args[position];
	}
   
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView txt = new TextView(ctx);
		if(position == 0){
		 txt.setText("请选择指定的充值服务");
		}else{
			
		txt.setText(String.format(AntiAddictionUtil.isCommon() ? ZZStr.YB_DECE_SERVICE_COMM.str():ZZStr.YB_DECE_SERVICE.str(),args[position].intValue(),args[position].intValue()));
		
		}
		//txt.setPadding(ZZDimen.dip2px(3), ZZDimen.dip2px(5), 0, ZZDimen.dip2px(5));
		  return txt;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView txt = new TextView(ctx);
	  if(position == 0){
		 txt.setText("请选择指定的充值服务");
		}else{
		txt.setText(String.format(AntiAddictionUtil.isCommon() ? ZZStr.YB_DECE_SERVICE_COMM.str():ZZStr.YB_DECE_SERVICE.str(),args[position].intValue(),args[position].intValue()));
		}
		txt.setGravity(Gravity.CENTER_VERTICAL);
		
		txt.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,CCImg.SPINNER_TXT_DEFAULT,CCImg.SPINNER_TXT_CLICK ));
		if(position == nowposition){
		txt.setTextColor(Color.WHITE);
		txt.setBackgroundDrawable(CCImg.SPINNER_TXT_CLICK.getDrawble(ctx));
		txt.setPressed(true);
		}
		
		txt.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		txt.setPadding(ZZDimen.dip2px(5), 0, 0, 0);
		
		return txt;
	}

}
