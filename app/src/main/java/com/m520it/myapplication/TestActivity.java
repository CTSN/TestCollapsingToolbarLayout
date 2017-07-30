package com.m520it.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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


//        text.setTranslationX(0.3f);
//        text.startScroll();
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.startScroll();
            }
        });

        text.setListener(new MyRectCircleEditText.onScollListener() {
            @Override
            public void onScroll(boolean isAdd, MyRectCircleEditText v) {
            }
        });
    }
}
