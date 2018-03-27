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
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.CommonAdapter;
import com.renny.simplebrowser.business.base.ViewHolder;
import com.renny.simplebrowser.business.toast.ToastHelper;
import com.renny.simplebrowser.view.adapter.ExtendHeadAdapter;
import com.renny.simplebrowser.view.widget.MyRecyclerView;
import com.renny.simplebrowser.view.widget.pullextend.ExtendListHeaderNew;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.List;

public class Recycler2Activity extends AppCompatActivity {

    MyRecyclerView mRecyclerView;
    ExtendListHeaderNew mExtendListHeader;
    RecyclerView listHeader;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler2);
        mRecyclerView = findViewById(R.id.recycler);
        getData();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        final CommonAdapter adapter = new CommonAdapter<String>(R.layout.list_item, list) {
            @Override
            protected void convert(ViewHolder holder, final int position) {
                String data = list.get(position);
                TextView textView = holder.getView(R.id.text1);
                textView.setText(data);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastHelper.makeToast("  点击了" + position);
                    }
                });
            }
        };
        final HeaderAndFooterWrapper mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(adapter);
        final View headView = LayoutInflater.from(this).inflate(R.layout.list_view_header_layout, mRecyclerView, false);
        final View footView = LayoutInflater.from(this).inflate(R.layout.list_view_footer_layout, mRecyclerView, false);

        mHeaderAndFooterWrapper.addHeaderView(headView);
        mHeaderAndFooterWrapper.addFootView(footView);
        mRecyclerView.setAdapter(mHeaderAndFooterWrapper);
        mHeaderAndFooterWrapper.notifyDataSetChanged();
        mExtendListHeader = headView.findViewById(R.id.extend_header);
        mRecyclerView.setExtendListHeader(mExtendListHeader);
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams lp = mExtendListHeader.getLayoutParams();
                lp.height = mRecyclerView.getHeight();
                mExtendListHeader.setLayoutParams(lp);

                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        layoutManager.scrollToPositionWithOffset(1, 0);
                    }
                });
                mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                View firstVisibleItemView = layoutManager.getChildAt(1);
                if (firstVisiblePosition != 0) {
                    boolean eq = firstVisibleItemView == headView;
                    mExtendListHeader.onReset();
                    Log.d("ListView", "##### 滚动到顶部 #####" + eq);
                }
                if (firstVisiblePosition == 0 && firstVisibleItemView != null) {
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
        listHeader.setLayoutManager(new LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false));
        listHeader.setAdapter(new ExtendHeadAdapter(mDatas).setItemClickListener(new CommonAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(int position, View view) {
                ToastHelper.makeToast(mDatas.get(position) + " 功能待实现");
            }
        }));

    }

    private void getData() {
        for (int i = 0; i < 28; i++) {
            list.add("item+" + i);
        }
    }
}
