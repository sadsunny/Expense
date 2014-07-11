package com.appxy.pocketexpensepro.setting.category;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.Common;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {
	private Context context;
	private int mSelect = 0;
	private int mBack = -1;

	public GridViewAdapter(Context context) {
		this.context = context;
	}

	public void changeStatus(int select) {
		this.mSelect = select;
	}

	public void changeBack(int back) {
		this.mBack = back;
	}

	@Override
	public int getCount() {
		return Common.CATEGORY_ICON.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	// get the current selector's id number
	@Override
	public long getItemId(int position) {
		return position;
	}

	// create view method
	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {
		ImgTextWrapper wrapper;
		if (view == null) {
			wrapper = new ImgTextWrapper();
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.gridview_item, null);
			wrapper.imageView = (ImageView) view.findViewById(R.id.image_item);
			view.setTag(wrapper);
			// view.setPadding(-5,-5, -5,-5); //ÿ��ļ��
		} else {
			wrapper = (ImgTextWrapper) view.getTag();
		}

		wrapper.imageView
				.setBackgroundResource(Common.CATEGORY_ICON[position]);

		return view;
	}

	class ImgTextWrapper {
		ImageView imageView;
	}

}
