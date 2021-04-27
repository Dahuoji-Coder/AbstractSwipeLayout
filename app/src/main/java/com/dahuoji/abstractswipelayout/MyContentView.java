package com.dahuoji.abstractswipelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

public class MyContentView extends LinearLayout implements IContentView {

    private ScrollView scrollView;

    public MyContentView(Context context) {
        super(context);
        init(context);
    }

    public MyContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.my_content_view, this);
        scrollView = findViewById(R.id.scrollView);
    }

    @Override
    public boolean canSwipeUp() {
        return scrollView.canScrollVertically(1);
    }

    @Override
    public boolean canSwipeDown() {
        return scrollView.canScrollVertically(-1);
    }

}
