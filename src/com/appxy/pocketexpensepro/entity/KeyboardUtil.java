package com.appxy.pocketexpensepro.entity;

import java.util.List;  

import com.appxy.pocketexpensepro.R;
import com.appxy.pocketexpensepro.keyboard.CustomKeyboardView;
import com.appxy.pocketexpensepro.overview.transaction.CreatTransactionActivity;

import android.annotation.SuppressLint;
import android.app.Activity;  
import android.content.Context;  
import android.inputmethodservice.Keyboard;  
import android.inputmethodservice.KeyboardView;  
import android.inputmethodservice.Keyboard.Key;  
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;  
import android.text.Editable;  
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;  
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;  
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
   
@SuppressLint("ServiceCast")
public class KeyboardUtil {   // 模拟弹出框
        private Context context;  
        private Activity mActivity;  
        private CustomKeyboardView keyboardView;  
        private Keyboard kb;// 数字键盘  
        private EditText edit;  
      
        
        private final static int CodeClear = 5000; // 清除归零
        
        private final static int CodeDivision = 5001; // 除法
        private final static int CodeMultiplication = 5002; //乘法
        private final static int CodeSubtraction = 5003;//加法
        private final static int CodeAddition = 5004; //减法
        
        private final static int CodeCalculate = 6000;
        private final static int CodePoint = 46;
        
        private int CalculateRule = 0; //0代表尚未选择或者被清零，1234，分别为+-*/
        private int CalculateRuleTag = 0; 
        
        private double preValue = 0.0;
        private boolean isCalculate = false;
        
        private Animation animation; 
        		
        public KeyboardUtil(Activity mActivity, Context context, EditText edit) {  
        	
        	    
                this.mActivity = mActivity;  
                this.context = context;  
                this.edit = edit;  
                animation = AnimationUtils.loadAnimation(context,
    							R.anim.dialog_enter);
                
                kb = new Keyboard(context, R.xml.keycontent);  
                keyboardView = (CustomKeyboardView)mActivity.findViewById(R.id.keyboardview);
                keyboardView.setKeyboard(kb);
                keyboardView.setPreviewEnabled(false);
                keyboardView.setOnKeyboardActionListener(listener);  
                mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }  
   
        private OnKeyboardActionListener listener = new OnKeyboardActionListener() {  
                @Override  
                public void swipeUp() {  
                }  
   
                @Override  
                public void swipeRight() {  
                }  
   
                @Override  
                public void swipeLeft() {  
                }  
   
                @Override  
                public void swipeDown() {  
                }  
   
                @Override  
                public void onText(CharSequence text) {  
                }  
   
                @Override  
                public void onRelease(int primaryCode) {  
                }  
   
                @Override  
                public void onPress(int primaryCode) { 
                	
                }  
   
                @Override  
                public void onKey(int primaryCode, int[] keyCodes) {  
                        Editable editable = edit.getText();  
                        int start = edit.getSelectionStart();  
                        
						 
                        if (primaryCode == CodeClear) {// 完成  
                        	
                        	 if( editable!=null ) editable.clear(); 
                        	 start = 0;
                        	 editable.insert(start, "0");
                        	 CalculateRule = 0;
                        	 CalculateRuleTag = 0;
                        	 preValue = 0.0;
                        	 isCalculate = false;
                        	 
                        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退  
                        	
                                if (editable != null && editable.length() > 0) {  
                                        if (start > 0) {  
                                                editable.delete(start - 1, start); 
                                                if (editable.length() == 0) {
                                                	 start = 0;
                                                	 editable.insert(start, "0");
												}
                                        }  
                                } 
                                
                                try {
       							 preValue = Double.valueOf(editable.toString());
       						} catch (Exception e) {
       							// TODO: handle exception
       							 preValue = 0.0;
       						}
       						 
                                
                        }else if (primaryCode == CodeDivision) { 
                        	CalculateRule = 4;
                        	 CalculateRuleTag = 4;
                        	   try {
      							 preValue = Double.valueOf(editable.toString());
      						} catch (Exception e) {
      							// TODO: handle exception
      							 preValue = 0.0;
      						}
      						 
                        	   
						}else if (primaryCode == CodeMultiplication) {
							CalculateRule = 3;
							 CalculateRuleTag = 3;
							   try {
									 preValue = Double.valueOf(editable.toString());
								} catch (Exception e) {
									// TODO: handle exception
									 preValue = 0.0;
								}
								 
							   
						}else if (primaryCode == CodeSubtraction) {
							CalculateRule = 2;
							 CalculateRuleTag = 2;
							   try {
									 preValue = Double.valueOf(editable.toString());
								} catch (Exception e) {
									// TODO: handle exception
									 preValue = 0.0;
								}
								 
							   
						}else if (primaryCode == CodeAddition) {
							
							CalculateRule = 1;
							 CalculateRuleTag = 1;
							   try {
									 preValue = Double.valueOf(editable.toString());
								} catch (Exception e) {
									// TODO: handle exception
									 preValue = 0.0;
								}
								 
							   
						}else if (primaryCode == CodePoint) { // 处理点
							
							String AllString = editable.toString();
							if (!AllString.contains(".")) {
								 editable.insert(start, Character.toString((char) primaryCode));  
							}
							
						}else if (primaryCode == CodeCalculate) { //计算
							
							double currentValue = 0.0;
							
							try {
								currentValue = Double.valueOf(editable.toString());
							} catch (Exception e) {
								// TODO: handle exception
								currentValue = 0.0;
							}
							
							
							if (CalculateRule > 0) {
								
								if (CalculateRule == 1 ) {
									preValue = preValue+ currentValue;
								} else if (CalculateRule == 2) {
									preValue = preValue-currentValue;
								} else if (CalculateRule == 3) {
									preValue = preValue *currentValue;
								} else if (CalculateRule == 4) {
									try {
										if (currentValue == 0) {
											preValue = 0.0;
										}else {
											preValue = preValue/currentValue;
										}
										
									} catch (Exception e) {
										// TODO: handle exception
										preValue = 0.0;
									}
									
								}
								 isCalculate = true;
								 editable.clear(); 
	                        	 start = 0;
	                        	 if (preValue <0 ) {
	                        		 preValue = 0-preValue;
								}
	                        	 editable.insert(start, MEntity.doubl2str( String.valueOf(preValue) ) ); 
	                        	 
	                        	 
							}
							
							
							 CalculateRule = 0;
							 CalculateRuleTag = 0;
							 
						} else {//排除第一位是0的情况
							
							String AllString = editable.toString();
							String insertChar = Character.toString((char) primaryCode);
							
							Log.v("mtag", "1步骤");
						 if (isCalculate) {
								
								 editable.clear(); 
	                        	 start = 0;
	                        	 editable.insert(start, insertChar);  
	                        	 isCalculate = false;
	                        	 CalculateRuleTag = 0;
	                        	 
	                        	 Log.v("mtag", "isCalculate计算过");
	                        	 
							}else if ( CalculateRuleTag  > 0 ) {
								 editable.clear(); 
	                        	 start = 0;
	                        	 editable.insert(start, insertChar);  
	                        	 CalculateRuleTag = 0;
	                        	 
	                        	 Log.v("mtag", "CalculateRuleTag符号"+CalculateRuleTag);
	                        	 
							}else{
								
								Log.v("mtag", "插入"+insertChar);
								
							 if (AllString.length() == 1 && AllString.equals("0")) {
								 
								 if (insertChar.equals(".")) {
		                        	 editable.insert(start, insertChar);  
		 							}else {
		 								 editable.clear(); 
			                        	 start = 0;
			                        	 editable.insert(start, insertChar);  
									}
								 
							}else {
								editable.insert(start, insertChar);  
								 
							}
						}
							 
                       }  
                        

                }  
        };  
           
        
   
    public void showKeyboard() {  
    	
    	if( edit !=null ) ((InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(edit.getWindowToken(), 0);
    	keyboardView.showWithAnimation(animation);
    	keyboardView.setVisibility(View.VISIBLE);
    	keyboardView.setEnabled(true);
    }  
       
    public void hideKeyboard() {  
    	 keyboardView.setEnabled(false);  
        int visibility = keyboardView.getVisibility();  
        if (visibility == View.VISIBLE) {  
            keyboardView.setVisibility(View.GONE);  
        }  
    }  
    
    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }
   
}  