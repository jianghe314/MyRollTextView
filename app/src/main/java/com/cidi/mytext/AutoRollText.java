package com.cidi.mytext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by CIDI zhengxuan on 2023/12/6
 * QQ:1309873105
 */
public class AutoRollText extends androidx.appcompat.widget.AppCompatTextView {

    private TextPaint textPaint;
    private String textMsg;
    //滑动方向 0：水平，1:垂直
    private int scrollOrientation;
    private int speed;
    private StaticLayout staticLayout;
    //文字平移变量
    private float step = 0f;

    private static final String TAG = "AutoText";


    public AutoRollText(@NonNull Context context) {
        this(context,null);
    }

    public AutoRollText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AutoRollText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.AutoRollText);
        scrollOrientation = array.getInt(R.styleable.AutoRollText_scrollOrientation,0);
        speed = array.getInt(R.styleable.AutoRollText_setSpeed,5);
        Log.e(TAG,"--->"+scrollOrientation+ "---"+speed);
        array.recycle();
        init();
    }

    private void init() {
        textPaint = getPaint();
        textPaint.setColor(getCurrentTextColor());
        textPaint.setTextSize(getTextSize());
        textPaint.setAntiAlias(true);
        textMsg = getText().toString();
        setRollingText();
    }

    private int getAvailableWidth(){
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getAvailableHeight(){
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    private boolean isOverFlowed(){
        Paint paint = getPaint();
        float width = paint.measureText(getText().toString());
        if(width > getAvailableWidth()){
            return true;
        }
        return false;
    }

    private int getTextWidth(){
        return (int) (textPaint.measureText(textMsg)+10);
    }

    private void setRollingText(){
        if(isOverFlowed()){
            char[] text = textMsg.toCharArray();
            String builderText = "";
            //水平
            if(scrollOrientation == 0){
                for (char c : text) {
                    builderText += c;
                }
            }else {
                //垂直
                for (char c : text) {
                    builderText += c + "\r\n";
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                staticLayout = StaticLayout.Builder.obtain(builderText,0,builderText.length(),textPaint,getTextWidth()).build();
            }else {
                staticLayout = new StaticLayout(builderText,textPaint,getTextWidth(), Layout.Alignment.ALIGN_NORMAL,1.0f,0.0f,true);
            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //setRollingText();
        canvas.save();
        if(scrollOrientation == 0){
            canvas.translate(100 - step,getAvailableHeight()/2);
        }else {
            canvas.translate(getAvailableWidth()/2,100 - step);
        }
        if(staticLayout != null){
            staticLayout.draw(canvas);
        }
        canvas.restore();
        step += speed;
        if(step > textPaint.measureText(textMsg)+50){
            step = 0;
        }
        invalidate();



    }
}
