package com.zz.sdk.layout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;



public class LoginNameAdpter<T> extends BaseAdapter implements Filterable {
	private List<T> mObjects;
    private Context ctx;
    private List<T> mOriginalValues;
    private ArrayFilter mFilter;
    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();
	public LoginNameAdpter(Context ctx,List<T> objects){
    	this.mObjects = objects;
    	this.ctx = ctx;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mObjects.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	     TextView holder = new TextView(ctx);
		  Object t = mObjects.get(position);
	      if (t instanceof String) {
	    	  String new_name = (String) t;
	    	  holder.setText(new_name);
	    	  holder.setTextSize(ZZDimen.dip2px(12));
	    	  holder.setGravity(Gravity.CENTER_VERTICAL);
	    	  holder.setBackgroundColor(Color.TRANSPARENT);
	    	  holder.setTextColor(Color.BLACK);
	    	  holder.setCompoundDrawablesWithIntrinsicBounds(null,null,null,CCImg.LOGIN_UNDER_LINE.getDrawble(ctx));
	    	  holder.setPadding(ZZDimen.dip2px(2), ZZDimen.dip2px(8), 0, ZZDimen.dip2px(0));
		  }
		 return holder;
	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
		return mFilter;
	}
	
	 /**
     * {@inheritDoc}
     */
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
	 private class ArrayFilter extends Filter {
	        @Override
	        protected FilterResults performFiltering(CharSequence prefix) {
	            FilterResults results = new FilterResults();

	            if (mOriginalValues == null) {
	                synchronized (mLock) {
	                    mOriginalValues = new ArrayList<T>(mObjects);
	                }
	            }

	            if (prefix == null || prefix.length() == 0) {
	                ArrayList<T> list;
	                synchronized (mLock) {
	                    list = new ArrayList<T>(mOriginalValues);
	                }
	                results.values = list;
	                results.count = list.size();
	            } else {
	                String prefixString = prefix.toString().toLowerCase();

	                ArrayList<T> values;
	                synchronized (mLock) {
	                    values = new ArrayList<T>(mOriginalValues);
	                }

	                final int count = values.size();
	                final ArrayList<T> newValues = new ArrayList<T>();

	                for (int i = 0; i < count; i++) {
	                    final T value = values.get(i);
	                    final String valueText = value.toString().toLowerCase();

	                    // First match against the whole, non-splitted value
	                    if (valueText.startsWith(prefixString)) {
	                        newValues.add(value);
	                    } else {
	                        final String[] words = valueText.split(" ");
	                        final int wordCount = words.length;

	                        // Start at index 0, in case valueText starts with space(s)
	                        for (int k = 0; k < wordCount; k++) {
	                            if (words[k].startsWith(prefixString)) {
	                                newValues.add(value);
	                                break;
	                            }
	                        }
	                    }
	                }

	                results.values = newValues;
	                results.count = newValues.size();
	            }

	            return results;
	        }

	        @Override
	        protected void publishResults(CharSequence constraint, FilterResults results) {
	            //noinspection unchecked
	            mObjects = (List<T>) results.values;
	            if (results.count > 0) {
	                notifyDataSetChanged();
	            } else {
	                notifyDataSetInvalidated();
	            }
	        }
	    }
	

}
