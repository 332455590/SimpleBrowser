package com.renny.simplebrowser.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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
                    final int scrollY = firstVisibleItemView.getBottom();
                    final int headerListHeight = mExtendListHeader.getListSize();
                    if (scrollY < headerListHeight / 2) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                smoothScrollBy(0,scrollY);
                            }
                        });
                        //layoutManager.scrollToPositionWithOffset(1, 0);
                    } else if (scrollY < headerListHeight || scrollY > headerListHeight) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                smoothScrollBy(0,scrollY-headerListHeight);
                            }
                        });
                     //   layoutManager.scrollToPositionWithOffset(1, headerListHeight);
                    }
                }
            }
        }
        return super.onTouchEvent(ev);
    }

}


