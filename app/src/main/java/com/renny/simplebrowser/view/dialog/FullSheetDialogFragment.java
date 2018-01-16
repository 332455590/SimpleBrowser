package com.renny.simplebrowser.view.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.db.dao.HistoryDao;
import com.renny.simplebrowser.business.db.entity.History;
import com.renny.simplebrowser.view.adapter.HistoryAdapter;

import java.util.List;

/**
 * Created by Renny on 2018/1/16.
 */

public class FullSheetDialogFragment extends BottomSheetDialogFragment {
    private BottomSheetBehavior mBehavior;
    HistoryDao mHistoryDao;
    View rootView;
    RecyclerView mRecyclerView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        rootView = View.inflate(getContext(), R.layout.dialog_history, null);
        dialog.setContentView(rootView);
        bindView(rootView, savedInstanceState);
        afterViewBind(rootView, savedInstanceState);
        mBehavior = BottomSheetBehavior.from((View) rootView.getParent());
        return dialog;
    }

    public void bindView(View rootView, Bundle savedInstanceState) {
        mRecyclerView = rootView.findViewById(R.id.history_list);

    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
        mHistoryDao = new HistoryDao();
        List<History> list = mHistoryDao.queryForAll();
        HistoryAdapter historyAdapter = new HistoryAdapter(list);
        mRecyclerView.setAdapter(historyAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onStart() {
        super.onStart();
        //默认全屏展开
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.dimAmount = 0.0f;
                window.setAttributes(lp);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    public void doclick(View v) {
        //点击任意布局关闭
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }


}
