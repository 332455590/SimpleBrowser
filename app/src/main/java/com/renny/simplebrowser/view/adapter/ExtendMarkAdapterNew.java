package com.renny.simplebrowser.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.view.bean.db.entity.BookMark;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * Created by Renny on 2018/1/3.
 */

public class ExtendMarkAdapterNew extends BGARecyclerViewAdapter<BookMark> {

    public ExtendMarkAdapterNew(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_mark);
    }
    @Override
    public void setItemChildListener(final BGAViewHolderHelper helper, int viewType) {
        helper.setItemChildClickListener(R.id.item_title);
        helper.setItemChildLongClickListener(R.id.item_title);
    }

    @Override
    public void fillData(BGAViewHolderHelper helper, int position, BookMark model) {
        TextView textView=helper.getView(R.id.item_title);
        textView.setText(model.getTitle());
    }




}
