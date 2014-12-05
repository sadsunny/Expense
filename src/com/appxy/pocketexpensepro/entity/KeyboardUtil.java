package com.appxy.pocketexpensepro.entity;

import java.util.List;  

import com.appxy.pocketexpensepro.R;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;  
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
   
@SuppressLint("ServiceCast")
public class KeyboardUtil {   // 模拟弹出框
        private Context context;  
        private Activity mActivity;  
        private KeyboardView keyboardView;  
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
   
        private PopupWindow mPopupWindow ;
        private View mView ;
        private double preValue = 0.0;
        private boolean isCalculate = false;
        
        public KeyboardUtil(Activity mActivity, Context context, EditText edit, View mView) {  
        	
        	    
                this.mActivity = mActivity;  
                this.context = context;  
                this.edit = edit;  
                this.mView = mView ;
                
                kb = new Keyboard(context, R.xml.keycontent);  
                keyboardView = (KeyboardView)mActivity.findViewById(R.id.keyboardview);
                keyboardView.setKeyboard(kb);
                keyboardView.setEnabled(true);  
                keyboardView.setPreviewEnabled(true);
                keyboardView.setOnKeyboardActionListener(listener);  
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
                        	 preValue = 0.0;
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
                                
                        }else if (primaryCode == CodeDivision) { 
                        	CalculateRule = 4;
                        	 try {
								 preValue = Double.valueOf(editable.toString());
							} catch (Exception e) {
								// TODO: handle exception
								 preValue = 0.0;
							}
						}else if (primaryCode == CodeMultiplication) {
							CalculateRule = 3;
							 try {
								 preValue = Double.valueOf(editable.toString());
							} catch (Exception e) {
								// TODO: handle exception
								 preValue = 0.0;
							}
						}else if (primaryCode == CodeSubtraction) {
							CalculateRule = 2;
							 try {
								 preValue = Double.valueOf(editable.toString());
							} catch (Exception e) {
								// TODO: handle exception
								 preValue = 0.0;
							}
						}else if (primaryCode == CodeAddition) {
							CalculateRule = 1;
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
									preValue +=currentValue;
								} else if (CalculateRule == 2) {
									preValue -=currentValue;
								} else if (CalculateRule == 3) {
									preValue *=currentValue;
								} else if (CalculateRule == 4) {
									try {
										if (currentValue == 0) {
											preValue = 0.0;
										}else {
											preValue /=currentValue;
										}
										
									} catch (Exception e) {
										// TODO: handle exception
										preValue = 0.0;
									}
									
								}
								 isCalculate = true;
								 editable.clear(); 
	                        	 start = 0;
	                        	 editable.insert(start, MEntity.doubl2str( String.valueOf(preValue) ) );  
							}
							
							 CalculateRule = 0;
							 
						} else {//排除第一位是0的情况
							
							String AllString = editable.toString();
							String insertChar = Character.toString((char) primaryCode);
							
							if (CalculateRule > 0 || isCalculate) {
								 editable.clear(); 
	                        	 start = 0;
	                        	 editable.insert(start, insertChar);  
	                        	 isCalculate = false;
							}else {
								
							
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
							 
							 try {
								 preValue = Double.valueOf(editable.toString());
							} catch (Exception e) {
								// TODO: handle exception
								 preValue = 0.0;
							}
						}
							
							 
                       }  
                }  
        };  
           
   
    public void showKeyboard() {  
    	
    	if (edit != null) {
    		 InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
    		 imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
		}
       
    	keyboardView.setVisibility(View.VISIBLE);
    	keyboardView.setEnabled(true);
    }  
       
    public void hideKeyboard() {  
        int visibility = keyboardView.getVisibility();  
        if (visibility == View.VISIBLE) {  
            keyboardView.setVisibility(View.INVISIBLE);  
        }  
    }  
    
    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }
   
}  