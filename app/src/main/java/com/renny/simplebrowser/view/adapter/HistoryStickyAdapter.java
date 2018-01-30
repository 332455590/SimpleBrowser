package com.renny.simplebrowser.view.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.db.entity.History;
import com.renny.simplebrowser.business.helper.Folders;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;


/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/5/22 16:31
 * 描述:RecyclerView 吸顶效果适配器
 */
public class HistoryStickyAdapter extends BGARecyclerViewAdapter<History> implements Filterable {
    private List<History> needFilterList = new ArrayList<>();

    public HistoryStickyAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_history);
    }

    @Override
    public void setItemChildListener(final BGAViewHolderHelper helper, int viewType) {
        helper.setItemChildClickListener(R.id.word);

    }

    @Override
    public void fillData(BGAViewHolderHelper helper, int position, History data) {
        TextView textView = helper.getView(R.id.word);
        final ImageView icon = helper.getView(R.id.icon);
        textView.setText(TextUtils.isEmpty(data.getTitle()) ? data.getUrl() : data.getTitle());
        String[] strings = data.getUrl().split("/");
        if (strings.length >= 2) {
            String host = strings[2];
            String path = Folders.icon.getFolder().getAbsolutePath() + "/" + host.replace(".", "") + ".jpg";
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap != null) {
                icon.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void setData(List<History> data) {
        super.setData(data);
        needFilterList.addAll(data);
    }

    /**
     * 是否为该分类下的第一个条目
     *
     * @param position
     * @return
     */
    public boolean isCategoryFistItem(int position) {
        // 第一条数据是该分类下的第一个条目
        if (position == 0) {
            return true;
        }
        String currentTopc = getItem(position).getDate();
        String preTopc = getItem(position - 1).getDate();
        // 当前条目的分类和上一个条目的分类不相等时，当前条目为该分类下的第一个条目
        return !TextUtils.equals(currentTopc, preTopc);

    }

    public int getPositionForCategory(int category) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = getItem(i).getDate();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == category) {
                return i;
            }
        }
        return -1;
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
                setData((ArrayList<History>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }
}