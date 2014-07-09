package com.appxy.pocketexpensepro.overview.transaction;

import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class AutoListAdapter extends CursorAdapter {
	

	private Context context;
	private LayoutInflater mInflater;
	private Cursor c;
	
	public AutoListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.c = c;
	}


	public class ViewHolder {
		public TextView payeeTextView;
		public TextView categoryTextView;
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewholder = (ViewHolder) arg0.getTag();
		
		viewholder.payeeTextView.setText(arg2.getString(arg2.getColumnIndex("name")) );
		viewholder.categoryTextView.setText(arg2.getString(arg2.getColumnIndex("categoryName")));
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewholder = new ViewHolder();
		View convertView  = mInflater.inflate(R.layout.auto_list_item,	null);
			viewholder.payeeTextView = (TextView) convertView.findViewById(R.id.payee_txt);
			viewholder.categoryTextView = (TextView) convertView.findViewById(R.id.category_txt);
			convertView.setTag(viewholder);

		return convertView;
		
	}
	
	 @Override
	 public CharSequence convertToString(Cursor cursor) {
	  // TODO Auto-generated method stub
	  return cursor == null?"":cursor.getString(cursor.getColumnIndex("name"));
	    
	 }

	
}
