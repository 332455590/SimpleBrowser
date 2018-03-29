package com.renny.simplebrowser.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.renny.simplebrowser.R;

import java.util.List;

/**
 * @author Created by Renny on on 2018/2/26
 */

public class MyAdapter extends BaseAdapter {

    private List<String> stuList;
    private LayoutInflater inflater;

    public MyAdapter() {
    }

    public MyAdapter(List<String> stuList, Context context) {
        this.stuList = stuList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return stuList == null ? 0 : stuList.size();
    }

    @Override
    public String getItem(int position) {
        return stuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view = inflater.inflate(R.layout.list_item, null);
        String student = getItem(position);
        TextView tv_name = view.findViewById(R.id.text1);
        tv_name.setText(student);
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }
}