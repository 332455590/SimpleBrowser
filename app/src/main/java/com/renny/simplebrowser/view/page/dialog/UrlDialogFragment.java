package com.renny.simplebrowser.view.page.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseDialogFragment;
import com.renny.simplebrowser.business.base.CommonAdapter;
import com.renny.simplebrowser.business.base.ViewHolder;
import com.renny.simplebrowser.view.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renny on 2018/1/10.
 */

public class UrlDialogFragment extends BaseDialogFragment {
    private RecyclerView mRecyclerView;
    private String codeContent = "";
    private int LocationY = 0;
    OnItemClickListener mOnItemClickListener;
    final List<String> list = new ArrayList<>();
    CommonAdapter<String> commonAdapter;
    String result;

    public static UrlDialogFragment getInstance(String codeContent) {
        UrlDialogFragment longClickDialogFragment = new UrlDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("codeContent", codeContent);
        longClickDialogFragment.setArguments(bundle);
        return longClickDialogFragment;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            initParams(getArguments());
        }
        View rootView = inflater.inflate(R.layout.popup_list, container, false);
        bindView(rootView, savedInstanceState);
        afterViewBind(rootView, savedInstanceState);
        return rootView;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }


    public void bindView(View rootView, Bundle savedInstanceState) {
        mRecyclerView = rootView.findViewById(R.id.popup_list);

    }


    public String getResult() {
        return result;
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    protected void initParams(Bundle bundle) {
        codeContent = bundle.getString("codeContent");
        LocationY = bundle.getInt("intY");
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                window.setGravity(Gravity.CENTER);
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }


}

