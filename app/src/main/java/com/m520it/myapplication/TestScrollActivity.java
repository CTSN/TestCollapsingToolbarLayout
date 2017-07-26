package com.m520it.myapplication;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestScrollActivity extends AppCompatActivity {


    private float totalHeight;      //总高度
    private float toolBarHeight;    //toolBar高度
    private float offSetHeight;     //总高度 -  toolBar高度  布局位移值
    private float llHeight;         //搜索框高度

    private float llHeightOffScale;     //高度差比值
    private float llOffDistance;        //距离差
    private float llOffDistanceScale;   //距离差比值
    private FrameLayout.LayoutParams params;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.fab)
    EditText fab;
    @BindView(R.id.ll)
    RelativeLayout ll;
    @BindView(R.id.bac)
    TextView bac;
    @BindView(R.id.fl)
    FrameLayout fl;
    @BindView(R.id.image)
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling2);
        ButterKnife.bind(this);

        bac.setAlpha(0f);
        totalHeight = getResources().getDimension(R.dimen.app_bar_height);
        toolBarHeight = getResources().getDimension(R.dimen.tool_bar_height);
        offSetHeight = totalHeight - toolBarHeight;

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                //第一次进入获取高度，以及差值 高度差比值
                if (llHeight == 0){
                    llHeight = ll.getMeasuredHeight();
                    params = (FrameLayout.LayoutParams) ll.getLayoutParams();

                    //算出高度偏移量比值  相对与llHeight
                    llHeightOffScale = 1.0f - (toolBarHeight / llHeight);

                    //得到高度差值 就是布局marginTop
                    llOffDistance = params.topMargin;
                    //得到高度差缩放比值
                    llOffDistanceScale = llOffDistance / offSetHeight;
                }

                //滑动一次 得到渐变缩放值
                float alphaScale = (-verticalOffset) / offSetHeight;

                //获取高度缩放值
                float llHeightScale = 1.0f-(llHeightOffScale*((-verticalOffset)/offSetHeight));

                float distance = llOffDistance - (-verticalOffset)*llOffDistanceScale;

                image.setAlpha(1.0f-alphaScale);
                bac.setAlpha(alphaScale);
                params.height = (int)(llHeight* llHeightScale);
                params.setMargins(0,(int)distance,0,0);

                fl.requestLayout();


            }
        });
    }
}
