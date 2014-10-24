package com.appxy.pocketexpensepro.passcode;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.entity.MyApplication;
import com.appxy.pocketexpensepro.setting.SettingDao;
import com.dropbox.sync.android.DbxRecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Activity_ClosePass extends BaseHomeActivity{
	EditText et;
	LinearLayout ll;
	ImageView line1,line2,line3,line4,point1,point2,point3,point4;
	Context context;
	String password;
    TextView tv;
    public static final String PREFS_NAME = "SAVE_INFO";
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(MyApplication.isPad){
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT){
			setContentView(R.layout.password);
		}else if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_LANDSCAPE){
		//	setContentView(R.layout.password2);
		}
		context = this;
		
		List<Map<String, Object>> mList = SettingDao.selectSetting(this);
		password = (String) mList.get(0).get("passcode");
		
		line1 = (ImageView)findViewById(R.id.passwordline1);
		line2 = (ImageView)findViewById(R.id.passwordline2);
		line3 = (ImageView)findViewById(R.id.passwordline3);
		line4 = (ImageView)findViewById(R.id.passwordline4);
		point1 = (ImageView)findViewById(R.id.passpoint1);
		point2 = (ImageView)findViewById(R.id.passpoint2);
		point3 = (ImageView)findViewById(R.id.passpoint3);
		point4 = (ImageView)findViewById(R.id.passpoint4);
		et = (EditText)findViewById(R.id.passedit11);
		tv = (TextView)findViewById(R.id.passtext);
		tv.setText("Enter your passcode");
		ll = (LinearLayout)findViewById(R.id.passlinear);
		ll.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				et.requestFocus();
				return false;
			}
			
		});
		et.setText("");
		et.requestFocus();
		et.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String note = arg0.toString();
				switch(note.length()){
				case 0:
					if(MyApplication.isPad){
						line1.setImageResource(R.drawable.passline2_t);
						line2.setImageResource(R.drawable.passline_t);
						line3.setImageResource(R.drawable.passline_t);
						line4.setImageResource(R.drawable.passline_t);
					}else{
						line1.setImageResource(R.drawable.passline2);
						line2.setImageResource(R.drawable.passline);
						line3.setImageResource(R.drawable.passline);
						line4.setImageResource(R.drawable.passline);
					}
					
					point1.setVisibility(4);
					point2.setVisibility(4);
					point3.setVisibility(4);
					point4.setVisibility(4);
					break;
				case 1:
					if(MyApplication.isPad){
						line1.setImageResource(R.drawable.passline_t);
						line2.setImageResource(R.drawable.passline2_t);
						line3.setImageResource(R.drawable.passline_t);
						line4.setImageResource(R.drawable.passline_t);
					}else{
						line1.setImageResource(R.drawable.passline);
						line2.setImageResource(R.drawable.passline2);
						line3.setImageResource(R.drawable.passline);
						line4.setImageResource(R.drawable.passline);
					}
					
					point1.setVisibility(0);
					point2.setVisibility(4);
					point3.setVisibility(4);
					point4.setVisibility(4);
					break;
				case 2:
					if(MyApplication.isPad){
						line1.setImageResource(R.drawable.passline_t);
						line2.setImageResource(R.drawable.passline_t);
						line3.setImageResource(R.drawable.passline2_t);
						line4.setImageResource(R.drawable.passline_t);
					}else{
						line1.setImageResource(R.drawable.passline);
						line2.setImageResource(R.drawable.passline);
						line3.setImageResource(R.drawable.passline2);
						line4.setImageResource(R.drawable.passline);
					}
					
					point1.setVisibility(0);
					point2.setVisibility(0);
					point3.setVisibility(4);
					point4.setVisibility(4);
					break;
				case 3:
					if(MyApplication.isPad){
						line1.setImageResource(R.drawable.passline_t);
						line2.setImageResource(R.drawable.passline_t);
						line3.setImageResource(R.drawable.passline_t);
						line4.setImageResource(R.drawable.passline2_t);
					}else{
						line1.setImageResource(R.drawable.passline);
						line2.setImageResource(R.drawable.passline);
						line3.setImageResource(R.drawable.passline);
						line4.setImageResource(R.drawable.passline2);
					}
					
					point1.setVisibility(0);
					point2.setVisibility(0);
					point3.setVisibility(0);
					point4.setVisibility(4);
					break;
				case 4:
					point1.setVisibility(0);
					point2.setVisibility(0);
					point3.setVisibility(0);
					point4.setVisibility(0);
					if(password.equals(note)){
						
						SettingDao.updatePasscode(Activity_ClosePass.this, "22");
						
						Intent intent = new Intent(context, Activity_ChangePass.class);
						setResult(11, intent);
						finish();
						
					}else{
						et.setText("");
						Toast toast = Toast.makeText(context,
							     "Passcode does not match, please try again!", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.TOP, 0, 0);
						toast.show();
					}
					break;
					
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		if(savedInstanceState!=null){
			String text = savedInstanceState.getString("text");
			et.setText(text);
		}
	}
	
	

	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		et.requestFocus();
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(et.getWindowToken(),0);
        imm.showSoftInput(et, 0);
		return true;
	}
	

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			finish();
			return true;
			}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString("text", et.getText().toString());
	}




	@Override
	public void syncDateChange(Map<String, Set<DbxRecord>> mMap) {
		// TODO Auto-generated method stub
		
	}
	
	
}
