package com.dahuoji.abstractswipelayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AbstractSwipeLayout abstractSwipeLayout = findViewById(R.id.abstractSwipeLayout);
        MyContentView myContentView = new MyContentView(this);
        MyHeaderView myHeaderView = new MyHeaderView(this);
        MyFooterView myFooterView = new MyFooterView(this);
        try {
            abstractSwipeLayout.addViews(myHeaderView, myFooterView, myContentView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        abstractSwipeLayout.setOnPullListener(new AbstractSwipeLayout.OnPullListener() {
            @Override
            public void refresh() {
                abstractSwipeLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        abstractSwipeLayout.complete();
                    }
                }, 3000);
            }

            @Override
            public void loadMore() {
                abstractSwipeLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        abstractSwipeLayout.complete();
                    }
                }, 3000);
            }
        });
    }
}