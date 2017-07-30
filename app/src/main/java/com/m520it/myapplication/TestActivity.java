package com.m520it.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by roy on 2017/7/29.
 */

public class TestActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        MyCircle circle = (MyCircle) findViewById(R.id.circle);
        circle.startScroll();

        final MyRectCircleEditText text = (MyRectCircleEditText)findViewById(R.id.text);

        text.setOnClickListener(new MyRectCircleEditText.onClickListener() {
            @Override
            public void onClick(View view) {
                text.startScroll();
            }
        });

        text.setListener(new MyRectCircleEditText.onScrollListener() {
            @Override
            public void onScroll(boolean isAdd, MyRectCircleEditText v) {
            }
        });
    }
}
