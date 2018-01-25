package com.renny.simplebrowser.view.page.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.view.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;

/**
 * Created by Renny on 2018/1/10.
 */

public class HandlePictureDialog extends BaseDialogFragment {
    private RecyclerView mRecyclerView;
    private int LocationX = 0;
    private int LocationY = 0;
     List<String> listData = new ArrayList<>();
    private listAdapter commonAdapter;
    OnItemClickListener mOnItemClickListener;

    public static HandlePictureDialog getInstance(int locationX, int locationY,ArrayList<String> listData) {
        HandlePictureDialog listDialog = new HandlePictureDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("intX", locationX);
        bundle.putInt("intY", locationY);
        bundle.putStringArrayList("list", listData);
        listDialog.setArguments(bundle);
        return listDialog;
    }

    public void show(FragmentManager manager) {
        show(manager, HandlePictureDialog.class.getSimpleName());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    protected void initParams(Bundle bundle) {
        LocationX = bundle.getInt("intX");
        LocationY = bundle.getInt("intY");
        listData = bundle.getStringArrayList("list");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_list;
    }


    public void bindView(View rootView, Bundle savedInstanceState) {
        mRecyclerView = rootView.findViewById(R.id.popup_list);
    }


    public void addListData(List<String> list) {
        commonAdapter.addMoreData(list);
    }

    public void addListData(String data) {
        commonAdapter.addLastItem(data);
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
        commonAdapter = new listAdapter(mRecyclerView);
        commonAdapter.setOnItemChildClickListener(new BGAOnItemChildClickListener() {
            @Override
            public void onItemChildClick(ViewGroup parent, View childView, int position) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(position);
                }
                dismiss();
            }
        });
        commonAdapter.setData(listData);
        mRecyclerView.setAdapter(commonAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

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
                lp.windowAnimations = R.style.dialogAnim;
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }
    public static class listAdapter extends BGARecyclerViewAdapter<String> {
        public listAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_popup_list);
        }
        @Override
        public void setItemChildListener(final BGAViewHolderHelper helper, int viewType) {
            helper.setItemChildClickListener(R.id.item_title);
        }

        @Override
        public void fillData(BGAViewHolderHelper helper, int position, String model) {
            TextView textView=helper.getView(R.id.item_title);
            textView.setText(model);
        }
    }

}

