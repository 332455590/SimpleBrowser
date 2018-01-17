package com.renny.simplebrowser.view.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseDialogFragment;
import com.renny.simplebrowser.business.db.dao.HistoryDao;
import com.renny.simplebrowser.business.db.entity.History;
import com.renny.simplebrowser.view.adapter.HistoryAdapter;
import com.renny.simplebrowser.view.event.WebviewEvent;
import com.renny.simplebrowser.view.listener.SimpleTextWatcher;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Renny on 2018/1/16.
 */

public class FullSheetDialogFragment extends BaseDialogFragment {
    private BottomSheetBehavior mBehavior;
    HistoryDao mHistoryDao;
    RecyclerView mRecyclerView;
    EditText mEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), getTheme());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_history;
    }

    public void bindView(View rootView, Bundle savedInstanceState) {
        mRecyclerView = rootView.findViewById(R.id.history_list);
        mEditText = rootView.findViewById(R.id.search_edit);
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
        mHistoryDao = new HistoryDao();
        final List<History> list = mHistoryDao.queryForAll();
        final HistoryAdapter historyAdapter = new HistoryAdapter(list);
        historyAdapter.setOnClickListener(new HistoryAdapter.OnClickListener() {
            @Override
            public void onUrlClick(int position, View view) {
                EventBus.getDefault().post(new WebviewEvent(list.get(position).getUrl()));
                mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onGoClick(int position, View view) {

            }
        });
        mRecyclerView.setAdapter(historyAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable sequence) {
                historyAdapter.getFilter().filter(mEditText.getText().toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior = BottomSheetBehavior.from((View) rootView.getParent());
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


}
