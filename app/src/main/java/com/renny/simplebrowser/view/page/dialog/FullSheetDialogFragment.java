package com.renny.simplebrowser.view.page.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseDialogFragment;
import com.renny.simplebrowser.business.db.dao.HistoryDao;
import com.renny.simplebrowser.business.db.entity.History;
import com.renny.simplebrowser.business.helper.KeyboardUtils;
import com.renny.simplebrowser.business.helper.UIHelper;
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
    TextView mTextView;
    RelativeLayout mSearchLayout;
    ImageView searchBtn,deleteBtn;

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
        mSearchLayout=rootView.findViewById(R.id.title_lay);
        searchBtn=rootView.findViewById(R.id.search_button);
        deleteBtn=rootView.findViewById(R.id.search_delete);
        mTextView=rootView.findViewById(R.id.search_text);
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

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHistoryDao.deleteAll();
                historyAdapter.clear();
            }
        });
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                mEditText.setVisibility(View.VISIBLE);
                KeyboardUtils.showSoftInput(getActivity(),mEditText);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id =v.getId();

    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior = BottomSheetBehavior.from((View) rootView.getParent());
        //默认全屏展开
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        // Capturing the callbacks for bottom sheet
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                // Check Logs to see how bottom sheets behaves
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        dismiss();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        expand();
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        reduce();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                window.setAttributes(lp);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }

    private void expand() {
        //设置伸展状态时的布局
        mEditText.setHint("搜索历史记录");
        mEditText.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) mEditText.getLayoutParams();
        LayoutParams.width = mSearchLayout.getWidth()-UIHelper.dip2px(108);
        mEditText.setLayoutParams(LayoutParams);
        //开始动画
        beginDelayedTransition(mSearchLayout);
    }

    private void reduce() {
        //设置收缩状态时的布局
        mEditText.setHint("搜索");
        mEditText.setVisibility(View.INVISIBLE);
        mTextView.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) mEditText.getLayoutParams();
        LayoutParams.width = UIHelper.dip2px(100);
        mEditText.setLayoutParams(LayoutParams);
        //开始动画
        beginDelayedTransition(mSearchLayout);
    }

    void beginDelayedTransition(ViewGroup view) {
        TransitionSet set = new AutoTransition();
        set.setDuration(300);
        TransitionManager.beginDelayedTransition(view, set);
    }

}
