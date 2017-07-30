package com.m520it.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestMyRectActivity extends AppCompatActivity {


    private float llOffDistance;
    private FrameLayout.LayoutParams params;
    private boolean isUp = false,isDown = false;
    private int i=0;


    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.text)
    MyRectCircleEditText text;
    @BindView(R.id.fl)
    NestedScrollView fl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_my_rect);
        ButterKnife.bind(this);

        text.setListener(new MyRectCircleEditText.onScollListener() {
            @Override
            public void onScroll(boolean isAdd, MyRectCircleEditText v) {
                if (isAdd)
                    isUp = true;
                else
                    isDown = true;
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestMyRectActivity.this,ScrollingActivity.class));
            }
        });
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //防止初始化进来两次
                i++;
                if (i<=2){
                    return;
                }

                if (params == null) {
                    params = (FrameLayout.LayoutParams) text.getLayoutParams();
                    llOffDistance = params.topMargin;
                    isUp = true;
                    isDown = true;
                }


                float distance = llOffDistance + verticalOffset;
                if (distance <= 20) {
                    distance = 20;
                    startScoll();
                }

                if (verticalOffset == 0){
                    if (isDown && !text.isAdd()) {
                        text.startScroll();
                    }
                }
                params.topMargin = (int) distance;
                fl.requestLayout();


            }
        });


    }

    public void startScoll() {
        if (isUp) {
            isUp = false;
            if (!text.isScroll())
                text.startScroll();
        }
    }
}
