package com.dahuoji.abstractswipelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MyHeaderView extends LinearLayout implements IHeaderView {

    private TextView textView;
    private final int height = 140;
    private boolean islLoading = false;

    public MyHeaderView(Context context) {
        super(context);
        init(context);
    }

    public MyHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.my_header_view, this);
        textView = findViewById(R.id.textView);
    }

    @Override
    public int getHeaderHeight() {
        return height;
    }

    @Override
    public void move(float dY) {
        if (islLoading) return;

        if (Math.abs(dY) >= height) {
            textView.setText("松手");
        } else {
            textView.setText("下拉刷新 " + dY);
        }
    }

    @Override
    public void loading() {
        islLoading = true;
        textView.setText("加载中");
    }

    @Override
    public void complete() {
        islLoading = false;
        textView.setText("完成");
    }

}
