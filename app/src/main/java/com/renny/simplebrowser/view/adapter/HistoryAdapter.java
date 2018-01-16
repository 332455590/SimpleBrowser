package com.renny.simplebrowser.view.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.CommonAdapter;
import com.renny.simplebrowser.business.base.ViewHolder;
import com.renny.simplebrowser.business.db.entity.History;

import java.util.List;

/**
 * Created by Renny on 2018/1/3.
 */

public class HistoryAdapter extends CommonAdapter<History> {
    private OnClickListener mOnClickListener;

    public HistoryAdapter(List<History> datas) {
        super(R.layout.item_history, datas);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    protected void convert(ViewHolder holder, final int position) {
        History data = getData(position);
        TextView textView = holder.getView(R.id.word);
        textView.setText(TextUtils.isEmpty(data.getTitle()) ? data.getUrl() : data.getTitle());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickListener != null) {
                    mOnClickListener.onWordClick(position, view);
                }
            }
        });
    }

    public interface OnClickListener {
        void onWordClick(int position, View view);

        void onGoClick(int position, View view);
    }

}
