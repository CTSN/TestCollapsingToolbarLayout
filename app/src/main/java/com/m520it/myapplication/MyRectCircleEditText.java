package com.m520it.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
    private String text = "";
    private float speed;

    private final static int IS_SLIDE_DECREASE = 0;  //递减状态
    private final static int IS_SLIDE_INCREASE = 1;     //递增状态

    private MyRectCircleEditText view;
    private int width;          //控件宽度
    private int height;         //控件高度

    private View chidView;
    int cWidth;         //文本宽度
    int cHeight;        //文本高度
    float tX;             //记录文本递减值
    float tX_x;            //记录宽度递减比

    private float center;       //左圆心x y  半径
    private float x; //矩形长度
    private float circle; //小圆的半径；
    private float y = 20; //圆环宽度
    private float y_x; //圆环宽度变化比；

    private boolean isIncrease = true;       //记录是否是递增状态
    private boolean isScroll = false;   //记录是否正在滑动

    private Paint paint;        //画笔
    private RectF rectF;        //矩形范围

    private Bitmap iconBit, iconBit2;   //小图标
    private float iconWidth;    //图标宽度
    private float degrees;  //图标旋转角度
    private float d_x;  //记录角度变化比

    private onScrollListener listener;   //动画完成监听
    private onClickListener onClickListener;    //点击事件监听
    private boolean canClick = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case IS_SLIDE_DECREASE:
                    x -= speed;
                    if (x >= center + speed) {
                        isScroll = true;
                        //根据圆环宽度比得到小圆半径
                        circle = y_x * (width - center - x) + center - y;
                        //根据角度比值 计算旋转角度
                        degrees = -45 + d_x * x;
                        mHandler.sendEmptyMessageDelayed(IS_SLIDE_DECREASE, 1);
                    } else {
                        //动画结束 恢复默认状态
                        x = center;
                        circle = center;
                        degrees = -45;
                        isScroll = false;
                        setEnabled(true);
                        //动画完成回调
                        if (listener != null) {
                            listener.onScroll(isIncrease, view);
                        }
                    }
                    break;
                case IS_SLIDE_INCREASE:
                    x += speed;
                    if (x < width - center) {
                        isScroll = true;
                        //根据圆环宽度比得到小圆半径
                        circle = center - y_x * x;
                        //根据角度比值 计算旋转角度
                        degrees = -45 + d_x * x;
                        mHandler.sendEmptyMessageDelayed(IS_SLIDE_INCREASE, 1);
                    } else {
                        //动画结束 恢复默认状态
                        x = width - center;
                        circle = center - y;
                        degrees = 0;
                        isScroll = false;
                        setEnabled(true);
                        //动画完成回调
                        if (listener != null) {
                            listener.onScroll(isIncrease, view);
                        }
                    }
                    break;
            }
            //默认是在距离小圆右侧  tX_x * x 得出的值少了圆的半径 +20设置间距
            tX = tX_x * x + center + 20;

            if (x >= width - center) {
                tX = cWidth + center * 2 + 20;
            }

            postInvalidate();
            requestLayout();
        }
    };

    public MyRectCircleEditText(Context context) {
        this(context, null);
    }

    public MyRectCircleEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        view = this;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.MyRectCircleEditText);
        bacColor = type.getColor(R.styleable.MyRectCircleEditText_bac_color, getResources().getColor(R.color.colorAccent));
        circleColor = type.getColor(R.styleable.MyRectCircleEditText_inner_circle_color, getResources().getColor(R.color.black));
        textColor = type.getColor(R.styleable.MyRectCircleEditText_text_color, getResources().getColor(R.color.black));
        textSize = type.getFloat(R.styleable.MyRectCircleEditText_text_size, 20);
        text = type.getString(R.styleable.MyRectCircleEditText_text);
        speed = type.getFloat(R.styleable.MyRectCircleEditText_speed, 80);
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
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(params);
        tv.setText(text);
        tv.setTextColor(textColor);
        tv.setTextSize(textSize);
        addView(tv);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(bacColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //初始化一次值
        if (width == 0) {
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = MeasureSpec.getSize(heightMeasureSpec);

            center = height / 2;
            //初始化默认矩形长度
            x = width - center;
            //小圆半径
            circle = center - y;
            //圆环宽度递减比值
            y_x = y / x;
            //初始化小图标相关变量
            iconWidth = circle - 5;
            iconBit = TextMesureUtil.zoomImg(iconBit, (int) circle - 5, (int) circle - 5);
            iconBit2 = TextMesureUtil.zoomImg(iconBit2, (int) circle - 5, (int) circle - 5);
            degrees = 0;
            d_x = 45 / x;
            //获取子控件
            View child = this.getChildAt(0);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            cWidth = child.getMeasuredWidth();
            cHeight = child.getMeasuredHeight();
            //初始化文本范围右下角 x坐标  +10设置间距
            tX = cWidth + center * 2 + 10;

            //文本宽度递减比
            tX_x = (tX - center * 2 - 5) / (width - center * 2);
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

        if (degrees == -45.0) {
            canvas.rotate(degrees - 45, center, center);
            canvas.drawBitmap(iconBit2, center - iconWidth / 2, center - iconWidth / 2, paint);
        } else {
            canvas.rotate(degrees, center, center);
            canvas.drawBitmap(iconBit, center - iconWidth / 2, center - iconWidth / 2, paint);
        }

        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        chidView = getChildAt(0);

        chidView.layout((int) (center * 2 + 5), (int) (center - cHeight / 2), (int) tX, (int) (center - cHeight / 2 + cHeight));

    }

    public boolean isIncrease() {
        return isIncrease;
    }

    public boolean isScroll() {
        return isScroll;
    }

    //递减状态
    public void startDecrease() {
        //滑动时不给点击事件
        setEnabled(false);

        isIncrease = false;
        mHandler.sendEmptyMessageDelayed(IS_SLIDE_DECREASE, 40);
    }

    //递增状态
    public void startIncrease() {
        //滑动时不给点击事件
        setEnabled(false);
        isIncrease = true;
        mHandler.sendEmptyMessageDelayed(IS_SLIDE_INCREASE, 40);
    }

    //开启动画
    public void startScroll() {
        if (!isScroll) {
            if (isIncrease) {
                startDecrease();
            } else {
                startIncrease();
            }

        }
    }

    public interface onScrollListener {
        void onScroll(boolean isIncrease, MyRectCircleEditText v);
    }

    public interface onClickListener {
        void onClick(View view);
    }

    public void setListener(onScrollListener listener) {
        this.listener = listener;
    }

    public void setOnClickListener(MyRectCircleEditText.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                canClick = judgeCanClick(event.getX(), event.getY());
                if (!canClick)
                    return super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                if (onClickListener != null && canClick) {
                    onClickListener.onClick(this);
                }
                break;
        }
        return true;
    }

    /**
     * 确定点击范围 区分两种 展开区域为整个控件 不展开点击区域为圆形
     * @param x
     * @param y
     * @return
     */
    private boolean judgeCanClick(float x, float y) {
        boolean click;
        if (isIncrease) {
            click = true;
        } else {
            if (x < center * 2 && y < center * 2) {
                click = true;
            } else {
                click = false;
            }
        }
        return click;
    }
}
