package com.renny.simplebrowser.view.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.renny.simplebrowser.view.widget.pullextend.ExtendListHeaderNew;

/**
 * @author Created by Renny on on 2018/3/26
 */

public class MyRecyclerView extends RecyclerView {


    ExtendListHeaderNew mExtendListHeader;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setExtendListHeader(ExtendListHeaderNew extendListHeader) {
        mExtendListHeader = extendListHeader;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
            if (layoutManager.findFirstVisibleItemPosition() == 0) {
                View firstVisibleItemView = layoutManager.getChildAt(0);
                if (firstVisibleItemView != null) {
                    int scrollY = firstVisibleItemView.getBottom();
                    int headerListHeight = mExtendListHeader.getListSize();
                    if (scrollY < headerListHeight / 2) {
                        layoutManager.scrollToPositionWithOffset(1, 0);
                    } else if (scrollY < headerListHeight || scrollY > headerListHeight) {
                        layoutManager.scrollToPositionWithOffset(1, headerListHeight);
                    }
                }
            }
        }
        return super.onTouchEvent(ev);
    }

    ValueAnimator valueAnimator;

    private void scroll(final LinearLayoutManager layoutManager, int start, int end) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofInt(start, end);

        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取估值器给我们的返回值
                int animatedValue = (int) animation.getAnimatedValue();
                //调用RecyclerView的scrollBy执行滑动
                scrollBy(0, animatedValue);
                Log.e("TAG", "animatedValue:" + animatedValue);
            }
        });
        valueAnimator.start();
    }
}


