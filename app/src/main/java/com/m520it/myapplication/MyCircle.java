package com.m520it.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by roy on 2017/7/29.
 */

public class MyCircle extends View {

    private int width;
    private int center;
    private int innerCircle = 60; //设置内圆半径
    private int ringWidth = 5; //设置圆环宽度
    private int color = Color.BLUE;

    private Paint mPaint;
    private  RectF oval;
    private int degress = -60;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            degress +=6;
            if (degress >= 300){
                degress = -60;
            }else{
                postInvalidate();
                mHandler.sendEmptyMessageDelayed(0,1);
            }

        }
    };

    public MyCircle(Context context) {
        super(context);
        init();
    }

    public MyCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public MyCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);

        center = width/2;

        oval = new RectF(center-innerCircle-3,center-innerCircle-3
                ,center+innerCircle+3,center+innerCircle+3);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(){

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setStyle(Paint.Style.STROKE); //绘制空心圆
    }
    private void init(Context context,AttributeSet attrs){

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyCircle);
        innerCircle = (int)ta.getDimension(R.styleable.MyCircle_circle_innerWidth,60f);
        ringWidth = (int)ta.getDimension(R.styleable.MyCircle_circle_width,5f);
        color = ta.getColor(R.styleable.MyCircle_circle_color,Color.BLUE);

        ta.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setStyle(Paint.Style.STROKE); //绘制空心圆

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //绘制内圆
        mPaint.setARGB(155, 167, 190, 206);
        mPaint.setStrokeWidth(2);
        canvas.drawCircle(center,center, innerCircle, mPaint);

        //绘制圆环
        mPaint.setARGB(255, 212 ,225, 233);
        mPaint.setStrokeWidth(ringWidth);
        canvas.drawCircle(center,center, innerCircle+1+ringWidth/2, mPaint);

        //绘制外圆
        mPaint.setARGB(155, 167, 190, 206);
        mPaint.setStrokeWidth(2);
        canvas.drawCircle(center,center, innerCircle+ringWidth, mPaint);

        canvas.save();
        canvas.rotate(degress,center,center);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(5);
        canvas.drawArc(oval,0,60,false,mPaint);
        canvas.restore();




    }

    public void startScroll(){
        mHandler.sendEmptyMessageDelayed(0,1);
    }


}
