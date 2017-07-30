package com.m520it.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by roy on 2017/7/29.
 */

public class MyRectCircleEditText extends ViewGroup {

    private int bacColor;
    private int circleColor;
    private int textColor;
    private float textSize;
    private String text="";
    private float speed;

    private final static int IS_SLIDE_MINUS = 0;  //递减状态
    private final static int IS_SLIDE_ADD = 1;     //递增状态

    private MyRectCircleEditText view;
    private int width;          //控件宽度
    private int height;         //控件高度

    private View chidView;
    int cWidth;         //文本宽度
    int cHeight;        //文本高度
    float cX;             //记录文本递减值
    float cX_x;            //记录宽度递减比

    private float center;       //左圆心x y  半径
    private float x; //矩形长度
    private float circle; //小圆的半径；
    private float y = 20; //圆环宽度

    private float y_x; //圆环宽度变化比；

    private boolean isAdd = true;       //记录是否是递增状态
    private boolean isScroll = false;   //记录是否正在滑动

    private Paint paint;        //画笔
    private RectF rectF;        //矩形范围

    private Bitmap iconBit, iconBit2;
    private float iconWidth;
    private float degress;
    private float d_x;  //记录角度变化比

    private onScollListener listener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case IS_SLIDE_MINUS:
                    // TODO: 2017/7/30 设置速率
                    x -= speed;
                    if (x >= center + speed) {
                        isScroll = true;
                        //根据圆环宽度比得到小圆半径
                        circle = y_x * (width - center - x) + center - y;
                        degress = -45 + d_x * x;

                        mHandler.sendEmptyMessageDelayed(IS_SLIDE_MINUS, 1);
                    } else {
                        //动画结束 恢复默认状态
                        x = center;
                        circle = center;
                        degress = -45;

                        isScroll = false;
                        setEnabled(true);
                        //动画完成回调
                        if (listener !=null) {
                            listener.onScroll(isAdd, view);
                        }
                    }
                    break;
                case IS_SLIDE_ADD:
                    x += speed;
                    if (x < width - center) {
                        isScroll = true;
                        //根据圆环宽度比得到小圆半径
                        circle = center - y_x * x;

                        degress = -45 + d_x * x;
                        mHandler.sendEmptyMessageDelayed(IS_SLIDE_ADD, 1);
                    } else {
                        //动画结束 恢复默认状态
                        x = width - center;
                        circle = center - y;

                        degress = 0;
                        isScroll = false;
                        setEnabled(true);
                        //动画完成回调
                        if (listener !=null) {
                            listener.onScroll(isAdd, view);
                        }
                    }

                    break;
            }


            //默认是在距离小圆右侧  cX_x * x 得出的值少了圆的半径 +20设置间距
            cX = cX_x * x + center + 20;

            if (x >= width - center) {
                cX = cWidth + center * 2 + 20;
            }
            postInvalidate();
            requestLayout();
        }
    };

    public MyRectCircleEditText(Context context) {
       this(context,null);
    }

    public MyRectCircleEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        view = this;
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        TypedArray type = context.obtainStyledAttributes(attrs,R.styleable.MyRectCircleEditText);

        bacColor = type.getColor(R.styleable.MyRectCircleEditText_bac_color,getResources().getColor(R.color.colorAccent));
        circleColor = type.getColor(R.styleable.MyRectCircleEditText_inner_circle_color,getResources().getColor(R.color.black));
        textColor = type.getColor(R.styleable.MyRectCircleEditText_text_color,getResources().getColor(R.color.black));
        textSize = type.getFloat(R.styleable.MyRectCircleEditText_text_size,20);
        text = type.getString(R.styleable.MyRectCircleEditText_text);
        speed = type.getFloat(R.styleable.MyRectCircleEditText_speed,80);
        BitmapDrawable dra = (BitmapDrawable) type.getDrawable(R.styleable.MyRectCircleEditText_open_icon);
        if (dra != null) {
            iconBit = dra.getBitmap();
        } else {
            iconBit = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
        }
        BitmapDrawable dra2 = (BitmapDrawable) type.getDrawable(R.styleable.MyRectCircleEditText_close_icon);
        if (dra2 != null) {
            iconBit2 = dra2.getBitmap();
        } else {
            iconBit2 = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_2);
        }
        type.recycle();

        TextView tv = new TextView(context);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(params);
        tv.setText(text);
        tv.setTextColor(textColor);
        tv.setTextSize(textSize);
        addView(tv);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(bacColor);


        iconWidth = iconBit.getWidth();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //初始化一次值
        if (width == 0) {
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = MeasureSpec.getSize(heightMeasureSpec);
            //圆半径
            center = height / 2;
            //初始化默认矩形长度
            x = width - center;

            //小圆半径
            circle = center - y;
            //圆环宽度递减比值
            y_x = y / x;

            iconWidth = circle - 5;
            iconBit = TextMesureUtil.zoomImg(iconBit,(int)circle-5,(int)circle-5);
            iconBit2 = TextMesureUtil.zoomImg(iconBit2,(int)circle-5,(int)circle-5);

            degress = 0;
            d_x = 45 / x;

            //获取子控件
            for (int i = 0; i < getChildCount(); i++) {
                View child = this.getChildAt(i);
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                cWidth = child.getMeasuredWidth();
                cHeight = child.getMeasuredHeight();
                //初始化文本右下角 x坐标  + 10设置间距
                cX = cWidth + center * 2 + 10;

                //文本宽度递减比
                cX_x = (cX - center * 2 - 5) / (width - center * 2);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        paint.setColor(bacColor);
        //画左边圆
        canvas.drawCircle(center, center, center, paint);

        //画矩形
        rectF = new RectF(center, 0, x, height);
        canvas.drawRect(rectF, paint);

        //画右边圆
        canvas.drawCircle(x, center, center, paint);

        //画小圆
        paint.setColor(circleColor);
        canvas.drawCircle(center, center, circle, paint);

        //画图片
        canvas.save();

        if (degress == -45.0) {
            canvas.rotate(degress - 45, center, center);
            canvas.drawBitmap(iconBit2, center - iconWidth / 2, center - iconWidth / 2, paint);
        } else {
            canvas.rotate(degress, center, center);
            canvas.drawBitmap(iconBit, center - iconWidth / 2, center - iconWidth / 2, paint);
        }

        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        chidView = getChildAt(0);

        chidView.layout((int) (center * 2 + 5), (int) (center - cHeight / 2), (int) cX, (int) (center - cHeight / 2 + cHeight));

    }

    //递减状态
    public void startSlide() {
        //滑动时不给点击事件
        setEnabled(false);

        isAdd = false;
        mHandler.sendEmptyMessageDelayed(IS_SLIDE_MINUS, 40);
    }

    //递增状态
    public void reSet() {
        //滑动时不给点击事件
        setEnabled(false);
        isAdd = true;
        mHandler.sendEmptyMessageDelayed(IS_SLIDE_ADD, 40);
    }

    public boolean isAdd() {
        return isAdd;
    }

    public boolean isScroll() {
        return isScroll;
    }

    //开启动画
    public void startScroll() {
        if (!isScroll) {

            if (isAdd) {
                startSlide();
            } else {
                reSet();
            }

        }
    }

    public void setListener(onScollListener listener) {
        this.listener = listener;
    }

    public interface onScollListener {
        void onScroll(boolean isAdd, MyRectCircleEditText v);
    }
}
