package com.renny.simplebrowser.view.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseDialogFragment;
import com.renny.simplebrowser.business.base.CommonAdapter;
import com.renny.simplebrowser.business.base.ViewHolder;
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.view.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renny on 2018/1/10.
 */

public class ListDialog extends BaseDialogFragment {
    private RecyclerView mRecyclerView;
    private int LocationX = 0;
    private int LocationY = 0;
    private OnItemClickListener mOnItemClickListener;
    final List<String> list = new ArrayList<>();
    private CommonAdapter<String> commonAdapter;
    private String result;

    public static ListDialog getInstance(int locationX, int locationY) {
        ListDialog listDialog = new ListDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("intX", locationX);
        bundle.putInt("intY", locationY);
        listDialog.setArguments(bundle);
        return listDialog;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.popup_list;
    }


    public void bindView(View rootView, Bundle savedInstanceState) {
        mRecyclerView = rootView.findViewById(R.id.popup_list);

    }


    public String getResult() {
        return result;
    }

    public void setShowZxing(String result) {
        this.result = result;
        Logs.base.d("识别图中二维码" + result);
        commonAdapter.addData("识别图中二维码");
        commonAdapter.notifyDataSetChanged();
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
        list.add("保存图片");
        commonAdapter = new CommonAdapter<String>(R.layout.item_popup_list, list) {
            @Override
            protected void convert(ViewHolder holder, final int position) {
                TextView tv = holder.getView(R.id.item_title);
                tv.setText(list.get(position));
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mItemClickListener.onItemClicked(position, view);
                    }
                });
            }
        };
        commonAdapter.setItemClickListener(new CommonAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(int position, View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(position);
                }
                dismiss();
            }
        });
        mRecyclerView.setAdapter(commonAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    protected void initParams(Bundle bundle) {
        LocationX = bundle.getInt("intX");
        LocationY = bundle.getInt("intY");
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                window.setGravity(Gravity.LEFT | Gravity.TOP);
                lp.x = LocationX;
                lp.y = LocationY;
                lp.width = UIHelper.dip2px(100);
                lp.dimAmount = 0.0f;
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }


}

