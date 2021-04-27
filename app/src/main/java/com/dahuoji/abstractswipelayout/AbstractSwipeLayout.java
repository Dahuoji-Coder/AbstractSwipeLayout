package com.dahuoji.abstractswipelayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class AbstractSwipeLayout extends LinearLayout {

    private View headerView;
    private View footerView;
    private View contentView;

    public AbstractSwipeLayout(Context context) {
        super(context);
        init(context, null);
    }

    public AbstractSwipeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
    }

    public void addViews(View headerView, View footerView, View contentView) throws Exception {
        this.headerView = headerView;
        this.footerView = footerView;
        this.contentView = contentView;
        if (headerView instanceof IHeaderView) {
            LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ((IHeaderView) headerView).getHeaderHeight());
            layoutParams.topMargin = -((IHeaderView) headerView).getHeaderHeight();
            addView(headerView, layoutParams);
        } else {
            if (headerView != null)
                throw new Exception("Your headerView needs to implement IHeaderView");
        }
        if (contentView instanceof IContentView) {
            addView(contentView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            if (contentView != null)
                throw new Exception("Your contentView needs to implement IContentView");
        }
        if (footerView instanceof IFooterView) {
            LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ((IFooterView) footerView).getFooterHeight());
            addView(footerView, layoutParams);
        } else {
            if (footerView != null)
                throw new Exception("Your footerView needs to implement IFooterView");
        }
    }

    private float lastTouchY;
    private final int PULL_INITIAL = 0;
    private final int PULL_TO_REFRESH = 1;
    private final int RELEASE_TO_REFRESH = 2;
    private final int PULL_TO_LOAD_MORE = 3;
    private final int RELEASE_TO_LOAD_MORE = 4;
    private int pullStatus = PULL_INITIAL;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LinearLayout.LayoutParams headerViewLayoutParams = (LayoutParams) headerView.getLayoutParams();
        float currentTouchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchY = currentTouchY;
                contentView.dispatchTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                //内容区域不能继续向下滑动(已经滑动到了顶部)并且dY>0(手势还是向下滑动) 或者 HeaderView已经是可见状态的时候, 执行updateHeader()
                float dY = currentTouchY - lastTouchY;
                if ((!((IContentView) contentView).canSwipeDown() && dY > 0) || headerViewLayoutParams.topMargin > -((IHeaderView) headerView).getHeaderHeight()) {
                    updateHeader(dY);
                } else if ((!((IContentView) contentView).canSwipeUp() && dY < 0) || headerViewLayoutParams.topMargin < -((IHeaderView) headerView).getHeaderHeight()) {
                    updateFooter(dY);
                } else {
                    contentView.dispatchTouchEvent(event);
                }
                lastTouchY = currentTouchY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                contentView.dispatchTouchEvent(event);
                if (headerViewLayoutParams.topMargin < -((IHeaderView) headerView).getHeaderHeight() - ((IFooterView) footerView).getFooterHeight()) {
                    //释放加载更多
                    animateTo(-((IHeaderView) headerView).getHeaderHeight() - ((IHeaderView) headerView).getHeaderHeight());
                    ((IFooterView) footerView).loading();
                    if (onPullListener != null) {
                        onPullListener.loadMore();
                    }
                } else if (headerViewLayoutParams.topMargin < -((IHeaderView) headerView).getHeaderHeight()) {
                    //释放FooterView归位
                    animateTo(-((IHeaderView) headerView).getHeaderHeight());
                } else if (headerViewLayoutParams.topMargin > 0) {
                    //释放开始刷新
                    animateTo(0);
                    ((IHeaderView) headerView).loading();
                    if (onPullListener != null) {
                        onPullListener.refresh();
                    }
                } else if (headerViewLayoutParams.topMargin > -((IHeaderView) headerView).getHeaderHeight()) {
                    //释放HeaderView归位
                    animateTo(-((IHeaderView) headerView).getHeaderHeight());
                }
                break;
        }
        return true;
    }

    private void updateHeader(float dY) {
        LinearLayout.LayoutParams headerViewLayoutParams = (LayoutParams) headerView.getLayoutParams();
        //临界值需要停留,否则因为帧率问题会导致ContentView不能接收到TouchEvent
        if ((headerViewLayoutParams.topMargin + ((IHeaderView) headerView).getHeaderHeight()) * (headerViewLayoutParams.topMargin + ((IHeaderView) headerView).getHeaderHeight() + dY) < 0) {
            headerViewLayoutParams.topMargin = -((IHeaderView) headerView).getHeaderHeight();
        } else {
            headerViewLayoutParams.topMargin += dY;
        }
        headerView.setLayoutParams(headerViewLayoutParams);
        ((IHeaderView) headerView).move(((IHeaderView) headerView).getHeaderHeight() + headerViewLayoutParams.topMargin);
    }

    private void updateFooter(float dY) {
        LinearLayout.LayoutParams headerViewLayoutParams = (LayoutParams) headerView.getLayoutParams();
        if ((headerViewLayoutParams.topMargin + ((IHeaderView) headerView).getHeaderHeight()) * (headerViewLayoutParams.topMargin + ((IHeaderView) headerView).getHeaderHeight() + dY) < 0) {
            headerViewLayoutParams.topMargin = -((IHeaderView) headerView).getHeaderHeight();
        } else {
            headerViewLayoutParams.topMargin += dY;
        }
        headerView.setLayoutParams(headerViewLayoutParams);
        ((IFooterView) footerView).move(-headerViewLayoutParams.topMargin - ((IHeaderView) headerView).getHeaderHeight());
    }

    private void animateTo(float targetY) {
        LinearLayout.LayoutParams headerViewLayoutParams = (LayoutParams) headerView.getLayoutParams();
        boolean isHeaderShowing = headerViewLayoutParams.topMargin > -((IHeaderView) headerView).getHeaderHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(headerViewLayoutParams.topMargin, targetY);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float tempY = (float) valueAnimator.getAnimatedValue();
                headerViewLayoutParams.topMargin = (int) tempY;
                headerView.setLayoutParams(headerViewLayoutParams);
                if (isHeaderShowing) {
                    ((IHeaderView) headerView).move(((IHeaderView) headerView).getHeaderHeight() + headerViewLayoutParams.topMargin);
                } else {
                    ((IFooterView) footerView).move(-headerViewLayoutParams.topMargin - ((IHeaderView) headerView).getHeaderHeight());
                }
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                headerViewLayoutParams.topMargin = (int) targetY;
                headerView.setLayoutParams(headerViewLayoutParams);
                if (isHeaderShowing) {
                    ((IHeaderView) headerView).move(((IHeaderView) headerView).getHeaderHeight() + headerViewLayoutParams.topMargin);
                } else {
                    ((IFooterView) footerView).move(-headerViewLayoutParams.topMargin - ((IHeaderView) headerView).getHeaderHeight());
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                headerViewLayoutParams.topMargin = (int) targetY;
                headerView.setLayoutParams(headerViewLayoutParams);
                if (isHeaderShowing) {
                    ((IHeaderView) headerView).move(((IHeaderView) headerView).getHeaderHeight() + headerViewLayoutParams.topMargin);
                } else {
                    ((IFooterView) footerView).move(-headerViewLayoutParams.topMargin - ((IHeaderView) headerView).getHeaderHeight());
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.setDuration(300);
        valueAnimator.start();
    }

    public void complete() {
        animateTo(-((IHeaderView) headerView).getHeaderHeight());
        ((IHeaderView) headerView).complete();
        ((IFooterView) footerView).complete();
    }

    private OnPullListener onPullListener;

    public void setOnPullListener(OnPullListener onPullListener) {
        this.onPullListener = onPullListener;
    }

    public interface OnPullListener {
        void refresh();

        void loadMore();
    }
}