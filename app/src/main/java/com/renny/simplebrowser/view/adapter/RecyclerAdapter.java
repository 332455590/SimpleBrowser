package com.renny.simplebrowser.view.adapter;

import android.support.v7.widget.RecyclerView;

import com.renny.simplebrowser.R;

import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * @author Created by Renny on on 2018/3/26
 */

public class RecyclerAdapter extends BGARecyclerViewAdapter<String> {
    public RecyclerAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.list_list_item);
    }



    @Override
    protected void setItemChildListener(BGAViewHolderHelper helper, int viewType) {
        super.setItemChildListener(helper, viewType);
        helper.setItemChildClickListener(R.id.text1);
    }

    @Override
    public void setOnItemChildClickListener(BGAOnItemChildClickListener onItemChildClickListener) {
        super.setOnItemChildClickListener(onItemChildClickListener);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, String model) {
        helper.setText(R.id.text1, model);
    }
}
