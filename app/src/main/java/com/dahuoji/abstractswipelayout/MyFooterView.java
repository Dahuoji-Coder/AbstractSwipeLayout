package com.dahuoji.abstractswipelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MyFooterView extends LinearLayout implements IFooterView {

    private TextView textView;
    private boolean islLoading = false;

    public MyFooterView(Context context) {
        super(context);
        init(context);
    }

    public MyFooterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.my_header_view, this);
        textView = findViewById(R.id.textView);
    }

    @Override
    public int getFooterHeight() {
        return 140;
    }

    @Override
    public void move(float dY) {
        if (islLoading) return;

        if (Math.abs(dY) >= 140) {
            textView.setText("松手开始加载");
        } else {
            textView.setText("上拉加载更多 " + dY);
        }
    }

    @Override
    public void loading() {
        islLoading = true;
        textView.setText("加载中...");
    }

    @Override
    public void complete() {
        islLoading = false;
        textView.setText("完成");
    }
}
