package com.renny.simplebrowser.view.page;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.CommonAdapter;
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.business.toast.ToastHelper;
import com.renny.simplebrowser.view.adapter.ExtendHeadAdapter;
import com.renny.simplebrowser.view.adapter.MyAdapter;
import com.renny.simplebrowser.view.widget.pullextend.ExtendListHeaderNew;
import com.renny.simplebrowser.view.widget.pullextend.HeaderListView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    HeaderListView mListView;
    ExtendListHeaderNew mExtendListHeader;
    RecyclerView listHeader;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mListView = findViewById(R.id.list);
        final View headView = LayoutInflater.from(this).inflate(R.layout.list_view_header_layout, mListView, false);
        final View footView = LayoutInflater.from(ListActivity.this).inflate(R.layout.list_view_footer_layout, mListView, false);
        mListView.addFooterView(footView, null, false);
        mExtendListHeader = headView.findViewById(R.id.extend_header);
        mListView.addHeaderView(headView);

        getData();
        final MyAdapter arrayAdapter = new MyAdapter(list, this);

     /*   @Override
        public void notifyDataSetChanged () {
            super.notifyDataSetChanged();
            Logs.h5.d("xxxx--" + footView.getTop());
            ViewGroup.LayoutParams lp = mExtendListHeader.getLayoutParams();
            lp.height = mListView.getHeight();
            ViewGroup.LayoutParams lp2 = footView.getLayoutParams();
            if (footView.getTop() != 0) {
                lp2.height = lp.height - footView.getTop();
            } else {
                lp2.height = 0;
            }
            footView.setLayoutParams(lp2);

        }*/

        mListView.setAdapter(arrayAdapter);
        mListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams lp = mExtendListHeader.getLayoutParams();
                lp.height = mListView.getHeight();
                mExtendListHeader.setLayoutParams(lp);
                if (arrayAdapter.getCount() * UIHelper.dip2px(40) < lp.height) {
                    ViewGroup.LayoutParams lp2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,lp.height - arrayAdapter.getCount() * UIHelper.dip2px(40)+UIHelper.dip2px(180));
                    footView.setLayoutParams(lp2);
                }
                mListView.setSelection(1);
                mListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                View firstVisibleItemView = mListView.getChildAt(1);
                if (firstVisibleItem != 0) {
                    boolean eq = firstVisibleItemView == headView;
                    mExtendListHeader.onReset();
                    Log.d("ListView", "##### 滚动到顶部 #####" + eq);
                }
                if (firstVisibleItem == 0 && firstVisibleItemView != null) {
                    Log.d("ListView", "###" + firstVisibleItemView.getTop());

                    if (firstVisibleItemView.getTop() >= 0) {
                        mExtendListHeader.onPull(firstVisibleItemView.getTop());
                    }
                    if (firstVisibleItemView.getTop() >= mExtendListHeader.getListSize()) {
                        mExtendListHeader.onArrivedListHeight();
                    }
                }
            }
        });

        mListView.setOnTouchUpListener(new HeaderListView.onTouchUpListener() {
            @Override
            public void onActionUp() {
                if (mListView.getFirstVisiblePosition() == 0) {
                    View firstVisibleItemView = mListView.getChildAt(0);
                    if (firstVisibleItemView != null) {
                        int scrollY = firstVisibleItemView.getBottom();
                        int headerListHeight = mExtendListHeader.getListSize();
                        if (scrollY < headerListHeight / 2) {
                            mListView.smoothScrollBy(scrollY, 500);
                        } else if (scrollY < headerListHeight || scrollY > headerListHeight) {
                            mListView.smoothScrollBy(scrollY - headerListHeight, 500);
                        }
                    }

                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastHelper.makeToast("点击了+第" + position);
            }
        });
        listHeader = mExtendListHeader.getRecyclerView();
        final List<String> mDatas = new ArrayList<>();
        mDatas.add("历史记录");
        mDatas.add("无痕浏览");
        mDatas.add("新建窗口");
        mDatas.add("无图模式");
        mDatas.add("夜间模式");
        mDatas.add("网页截图");
        mDatas.add("禁用JS");
        mDatas.add("下载内容");
        mDatas.add("查找");
        mDatas.add("拦截广告");
        mDatas.add("全屏浏览");
        mDatas.add("翻译");
        mDatas.add("切换UA");
        listHeader.setLayoutManager(new

                LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false));
        listHeader.setAdapter(new

                ExtendHeadAdapter(mDatas).

                setItemClickListener(new CommonAdapter.ItemClickListener() {
                    @Override
                    public void onItemClicked(int position, View view) {
                        ToastHelper.makeToast(mDatas.get(position) + " 功能待实现");
                    }
                }));

    }

    private List<String> getData() {
        for (int i = 0; i < 78; i++) {
            list.add("item+" + i);
        }
        return list;
    }


}
