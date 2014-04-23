package com.appxy.pocketexpensepro.accounts;

import java.util.List;

import com.appxy.pocketexpensepro.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class NavigationListAdapter extends BaseAdapter implements SpinnerAdapter {
	
    private LayoutInflater mInflater;
    private String title;
    private List<String> content; 
    
    public NavigationListAdapter(Context context,String title){
    	this.mInflater = LayoutInflater.from(context);
    	this.title = title;
    }
    
    public void setDownItemData( List<String> itemStrings) {
		this.content = itemStrings;
	}
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (content == null) {
			return 0;
		} 
		return content.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 ViewHolder viewholder = null;
		 
		 if(convertView == null)
         {
			 viewholder = new ViewHolder();
             convertView = mInflater.inflate(R.layout.calender_getview, null);
             viewholder.mTextView = (TextView)convertView.findViewById(R.id.get_text);
             convertView.setTag(viewholder);
         }else
         {
        	 viewholder = (ViewHolder)convertView.getTag();
         }
		 
		 viewholder.mTextView.setText(title+" ");
		 
		return convertView;
	}

	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DropViewHolder viewholder = null;
		 
		 if(convertView == null)
         {
			 viewholder = new DropViewHolder();
             convertView = mInflater.inflate(R.layout.getdropdownview, null);
             viewholder.mTextView1 = (TextView)convertView.findViewById(R.id.report_drop_text);
//             viewholder.lineView = (View)convertView.findViewById(R.id.blacklineview);
             convertView.setTag(viewholder);
         }else
         {
        	 viewholder = (DropViewHolder)convertView.getTag();
         }
		 viewholder.mTextView1.setText(content.get(position));
//		 
//		 if (position == 2) {
//			 viewholder.lineView.setVisibility(View.INVISIBLE);
//		} else {
//			 viewholder.lineView.setVisibility(View.VISIBLE);
//		}
		
		 
		return convertView;
	}
	
	class ViewHolder
    {
        public TextView mTextView;
    }
	
	class DropViewHolder
    {
        public TextView mTextView1;
        public View lineView;
    }
	
}
