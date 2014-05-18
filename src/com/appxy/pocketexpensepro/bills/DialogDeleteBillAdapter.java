package com.appxy.pocketexpensepro.bills;

	import java.util.List;
import java.util.Map;

import com.appxy.pocketexpensepro.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

	public class DialogDeleteBillAdapter extends BaseAdapter{
		
		private final String[] data = {"Delete only this bill","Delete this and all future bills"};
		
		private LayoutInflater mInflater;
		private Context context;
		private int conut = 2;
		    
	    public  DialogDeleteBillAdapter(Context context) {
				this.context = context;
				this.mInflater = LayoutInflater.from(context);
			} 
	    
	    public void listCount(int count) {
			this.conut = count;
		}
	    
		    
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			 ViewHolder viewholder = null;

			 if(convertView == null)
	         {
				 viewholder = new ViewHolder();
	             convertView = mInflater.inflate(R.layout.dialog_edit_bill_item, null);
	             
	             viewholder.textView1 = (TextView)convertView.findViewById(R.id.diaTextView1);
	             
	             convertView.setTag(viewholder);
	         }else
	         {
	        	 viewholder = (ViewHolder)convertView.getTag();
	         }

			 viewholder.textView1.setText(data[position]);
	         return convertView;
		}
		
		
		public class ViewHolder
	    {
	        public TextView textView1; 
	    }	
	}
