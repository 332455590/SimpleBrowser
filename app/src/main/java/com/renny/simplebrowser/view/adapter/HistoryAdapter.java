package com.renny.simplebrowser.view.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.CommonAdapter;
import com.renny.simplebrowser.business.base.ViewHolder;
import com.renny.simplebrowser.business.db.entity.History;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renny on 2018/1/3.
 */

public class HistoryAdapter extends CommonAdapter<History> implements Filterable {

    private OnClickListener mOnClickListener;
    private List<History> needFilterList = new ArrayList<>();

    public HistoryAdapter(List<History> datas) {
        super(R.layout.item_history, datas);
        needFilterList = datas;
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
                    mOnClickListener.onUrlClick(position, view);
                }
            }
        });
    }

    //重写getFilter()方法
    @Override
    public Filter getFilter() {
        return new Filter() {
            //执行过滤操作
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<History> filteredList = new ArrayList<>();
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    filteredList = needFilterList;
                } else {
                    for (History history : needFilterList) {
                        if (!TextUtils.isEmpty(history.getTitle()) && history.getTitle().contains(charString)) {
                            //这里根据需求，添加匹配规则
                            filteredList.add(history);
                            continue;
                        }
                        if (!TextUtils.isEmpty(history.getUrl()) && history.getUrl().contains(charString)) {
                            //这里根据需求，添加匹配规则
                            filteredList.add(history);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            //把过滤后的值返回出来
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataList = (ArrayList<History>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public interface OnClickListener {
        void onUrlClick(int position, View view);

        void onGoClick(int position, View view);
    }

}
