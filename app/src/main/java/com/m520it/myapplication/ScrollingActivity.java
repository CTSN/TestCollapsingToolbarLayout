package com.m520it.myapplication;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    @BindView(R.id.subscribe)
    TextView mSubscribe;
    @BindView(R.id.tv_search)
    TextView tvSearch;

    private float mSelfHeight = 0;//用以判断是否得到正确的宽高值
    private float mTitleScale;
    private float mSubScribeScale;
    private float mSubScribeScaleX;
    private float mHeadImgScale;


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

                    mSelfHeight = mTitle.getHeight();

                    float distanceTitle = mTitle.getTop() - (toolbarHeight - mTitle.getHeight()) / 2.0f;
                    float distanceSubscribe = mSubscribe.getY() - (toolbarHeight - mSubscribe.getHeight()) / 2.0f;
                    float distanceHeadImg = mHeadImage.getY() - (toolbarHeight - mHeadImage.getHeight()) / 2.0f;
                    float distanceSubscribeX = screenW / 2.0f - (mSubscribe.getWidth() / 2.0f);

                    mTitleScale = distanceTitle / (initHeight - toolbarHeight);
                    mSubScribeScale = distanceSubscribe / (initHeight - toolbarHeight);
                    mHeadImgScale = distanceHeadImg / (initHeight - toolbarHeight);
                    mSubScribeScaleX = distanceSubscribeX / (initHeight - toolbarHeight);
                }

                float scale = 1.0f - (-verticalOffset) / (initHeight - toolbarHeight);

                tvSearch.setScaleX(scale);
                tvSearch.setScaleY(scale);
                tvSearch.setAlpha(scale);

                mHeadImage.setScaleX(scale);
                mHeadImage.setScaleY(scale);
                mHeadImage.setTranslationY(mHeadImgScale * verticalOffset);

                mTitle.setTranslationY(mTitleScale * verticalOffset);

                mSubscribe.setTranslationY(mSubScribeScale * verticalOffset);
                mSubscribe.setTranslationX(-mSubScribeScaleX * verticalOffset);
            }
        });
    }

}
