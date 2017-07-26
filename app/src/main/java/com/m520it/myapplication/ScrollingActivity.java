package com.m520it.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScrollingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.iv_head)
    ImageView mHeadImage;
    @BindView(R.id.subscription_title)
    TextView mTitle;
    @BindView(R.id.test)
    TextView test;
    @BindView(R.id.tv_search)
    TextView tvSearch;

    private float mSelfHeight = 0;  //用以判断是否得到正确的宽高值
    private float mTitleScale;      //标题缩放值
    private float mTestScaleY;      //测试按钮y轴缩放值
    private float mTestScaleX;      //测试按钮x轴缩放值
    private float mHeadImgScale;    //头像缩放值


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);

        final float screenW = getResources().getDisplayMetrics().widthPixels;
        final float toolbarHeight = getResources().getDimension(R.dimen.tool_bar_height);
        final float initHeight = getResources().getDimension(R.dimen.app_bar_height);
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (mSelfHeight == 0) {

                    //获取标题高度
                    mSelfHeight = mTitle.getHeight();

                    //得到标题的高度差
                    float distanceTitle = mTitle.getTop() - (toolbarHeight - mTitle.getHeight()) / 2.0f;
                    //得到测试按钮的高度差
                    float distanceTest = test.getY() - (toolbarHeight - test.getHeight()) / 2.0f;
                    //得到图片的高度差
                    float distanceHeadImg = mHeadImage.getY() - (toolbarHeight - mHeadImage.getHeight()) / 2.0f;
                    //得到测试按钮的水平差值  屏幕宽度一半 - 按钮宽度一半
                    float distanceSubscribeX = screenW / 2.0f - (test.getWidth() / 2.0f);

                    //得到高度差缩放比值  高度差／能滑动总长度 以此类推
                    mTitleScale = distanceTitle / (initHeight - toolbarHeight);
                    mTestScaleY = distanceTest / (initHeight - toolbarHeight);
                    mHeadImgScale = distanceHeadImg / (initHeight - toolbarHeight);
                    mTestScaleX = distanceSubscribeX / (initHeight - toolbarHeight);
                }
                //得到文本框、头像缩放值 不透明 ->透明  文本框x跟y缩放
                float scale = 1.0f - (-verticalOffset) / (initHeight - toolbarHeight);

                tvSearch.setScaleX(scale);
                tvSearch.setScaleY(scale);
                tvSearch.setAlpha(scale);

                mHeadImage.setScaleX(scale);
                mHeadImage.setScaleY(scale);
                //设置头像y轴平移
                mHeadImage.setTranslationY(mHeadImgScale * verticalOffset);
                //设置标题y轴平移
                mTitle.setTranslationY(mTitleScale * verticalOffset);
                //设置测试按钮x跟y平移
                test.setTranslationY(mTestScaleY * verticalOffset);
                test.setTranslationX(-mTestScaleX * verticalOffset);
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScrollingActivity.this,TestScrollActivity.class));
            }
        });
    }

}
