package com.appxy.pocketexpensepro;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class myView extends ViewPager{

	public myView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public myView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		int action = arg0.getAction();
		int count = arg0.getPointerCount();;
		
		if (count<2) {
			return false;
		}
		
		switch(action & MotionEvent.ACTION_MASK)
	    {
	        case MotionEvent.ACTION_POINTER_DOWN:
	            // multitouch!! - touch down
	        	return true;
	            
	    }
		
		return false;
	}
	
	
}