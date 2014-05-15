package com.appxy.pocketexpensepro;


import com.appxy.pocketexpensepro.R;

import android.content.Context;  
import android.content.res.TypedArray;
import android.graphics.Canvas;  
import android.graphics.Color;
import android.graphics.Paint;  
import android.util.AttributeSet;  
import android.view.View;  
  
public class CircleView extends View {  
  
    private final  Paint paint;  
    private final Context context;  
    private int background;
    
    public CircleView(Context context) {  
          
        // TODO Auto-generated constructor stub  
        this(context, null);  
    }  
  
    public CircleView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        // TODO Auto-generated constructor stub  
        this.context = context;  
        this.paint = new Paint();  
        this.paint.setAntiAlias(true); //消除锯齿  
        this.paint.setStyle(Paint.Style.FILL); //绘制空心圆   
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyView); 
        background = a.getColor(R.styleable.MyView_background, 0XFFFFFFFF);
        this.paint.setColor(background);
        a.recycle();
    }  
  
    @Override  
    protected void onDraw(Canvas canvas) {  
        // TODO Auto-generated method stub  
        super.onDraw(canvas);  
        int center = getWidth()/2;  
        int innerCircle = dip2px(context, 4); //设置内圆半径  
          
        //绘制内圆  
       
        this.paint.setStrokeWidth(1);  
        canvas.drawCircle(center,center, innerCircle, this.paint);  
          
    }  
    
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
}  