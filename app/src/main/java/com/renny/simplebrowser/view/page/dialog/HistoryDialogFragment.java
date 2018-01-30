package com.renny.simplebrowser.view.page.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.view.MotionEvent;
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
import com.renny.simplebrowser.business.helper.EventHelper;
import com.renny.simplebrowser.business.helper.Folders;
import com.renny.simplebrowser.business.helper.KeyboardUtils;
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.view.adapter.HistoryStickyAdapter;
import com.renny.simplebrowser.view.event.WebViewEvent;
import com.renny.simplebrowser.view.listener.SimpleTextWatcher;

import java.util.List;

import cn.bingoogolapple.baseadapter.BGADivider;
import cn.bingoogolapple.baseadapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.baseadapter.BGAOnRVItemChildTouchListener;
import cn.bingoogolapple.baseadapter.BGARVVerticalScrollHelper;
import cn.bingoogolapple.baseadapter.BGARecyclerViewHolder;

/**
 * Created by Renny on 2018/1/16.
 */

public class HistoryDialogFragment extends BaseDialogFragment implements BGAOnRVItemChildTouchListener {
    private BottomSheetBehavior mBehavior;
     List<History> list;
    HistoryDao mHistoryDao;

    RecyclerView mRecyclerView;
    EditText mEditText;
    TextView mTextView;
    RelativeLayout mSearchLayout;
    ImageView searchBtn, deleteBtn;
    HistoryStickyAdapter historyAdapter;
    private BGARVVerticalScrollHelper mRecyclerViewScrollHelper;
    private ItemTouchHelper mItemTouchHelper;
    public static HistoryDialogFragment getInstance(Context mContext, FragmentManager fm) {
        String tag = HistoryDialogFragment.class.getName();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = Fragment.instantiate(mContext, tag);
            HistoryDialogFragment dialogFragment = (HistoryDialogFragment) fragment;
            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);//设置取消标题栏
            return dialogFragment;
        } else {
            return (HistoryDialogFragment) fragment;
        }
    }

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
        mSearchLayout = rootView.findViewById(R.id.title_lay);
        searchBtn = rootView.findViewById(R.id.search_button);
        deleteBtn = rootView.findViewById(R.id.search_delete);
        mTextView = rootView.findViewById(R.id.search_text);
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
        mHistoryDao = new HistoryDao();
        list = mHistoryDao.queryForAll();
        historyAdapter = new HistoryStickyAdapter(mRecyclerView);
        historyAdapter.setData(list);
        initStickyDivider();
        historyAdapter.setOnItemChildClickListener(new BGAOnItemChildClickListener() {
            @Override
            public void onItemChildClick(ViewGroup parent, View childView, int position) {
                KeyboardUtils.hideSoftInput(getActivity(), mEditText);
                EventHelper.post(new WebViewEvent(list.get(position).getUrl()));
                mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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

        deleteBtn.setOnClickListener(this);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                mEditText.setVisibility(View.VISIBLE);
                KeyboardUtils.showSoftInput(getActivity(), mEditText);
            }
        });
        // 初始化拖拽排序和滑动删除
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback());
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        historyAdapter.setOnRVItemChildTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        switch (id) {
            case R.id.search_delete:
                mHistoryDao.deleteAll();
                historyAdapter.clear();
                Folders.icon.cleanFolder();
                break;
        }

    }

    private void initStickyDivider() {
        final BGADivider.StickyDelegate stickyDelegate = new BGADivider.StickyDelegate() {
            @Override
            public void initCategoryAttr() {
            }

            @Override
            protected boolean isCategoryFistItem(int position) {
                return historyAdapter.isCategoryFistItem(position);
            }

            @Override
            protected String getCategoryName(int position) {
                return historyAdapter.getItem(position).getDate();
            }

            @Override
            protected int getFirstVisibleItemPosition() {
                return mRecyclerViewScrollHelper.findFirstVisibleItemPosition();
            }
        };

        mRecyclerView.addItemDecoration(BGADivider.newDrawableDivider(R.drawable.shape_divider)
                .setStartSkipCount(0)
                .setMarginLeftResource(R.dimen.size_level3)
                .setMarginRightResource(R.dimen.size_level9)
                .setDelegate(stickyDelegate));

        mRecyclerViewScrollHelper = BGARVVerticalScrollHelper.newInstance(mRecyclerView, new BGARVVerticalScrollHelper.SimpleDelegate() {
            @Override
            public int getCategoryHeight() {
                return stickyDelegate.getCategoryHeight();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior = BottomSheetBehavior.from((View) rootView.getParent());
        //默认全屏展开
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Logs.common.d("newState" + newState);
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        KeyboardUtils.hideSoftInput(getActivity(), mEditText);
                        dismiss(isResumed());
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        expand();
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        KeyboardUtils.hideSoftInput(getActivity(), mEditText);
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
        LayoutParams.width = mSearchLayout.getWidth() - UIHelper.dip2px(108);
        mEditText.setLayoutParams(LayoutParams);
        //开始动画
        beginDelayedTransition(mSearchLayout);
    }

    private void reduce() {
        //设置收缩状态时的布局
        mEditText.setVisibility(View.INVISIBLE);
        mTextView.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) mEditText.getLayoutParams();
        LayoutParams.width = UIHelper.dip2px(50);
        mEditText.setLayoutParams(LayoutParams);
        //开始动画
        beginDelayedTransition(mSearchLayout);
    }

    void beginDelayedTransition(ViewGroup view) {
        TransitionSet set = new AutoTransition();
        set.setDuration(300);
        TransitionManager.beginDelayedTransition(view, set);
    }
    @Override
    public boolean onRvItemChildTouch(BGARecyclerViewHolder holder, View childView, MotionEvent event) {
        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
            mItemTouchHelper.startDrag(holder);
        }
        return false;
    }

    /**
     * 该类参考：https://github.com/iPaulPro/Android-ItemTouchHelper-Demo
     */
    private class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
        public static final float ALPHA_FULL = 1.0f;

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            historyAdapter.moveItem(source, target);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            History history=list.get(viewHolder.getAdapterPosition());
            mHistoryDao.delete(history.getUrl());
            historyAdapter.removeItem(viewHolder);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;
                float alpha = ALPHA_FULL - Math.abs(dX) / (float) itemView.getWidth();
                ViewCompat.setAlpha(viewHolder.itemView, alpha);
            }
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder.itemView.setSelected(true);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            ViewCompat.setAlpha(viewHolder.itemView, ALPHA_FULL);
            viewHolder.itemView.setSelected(false);
        }
    }
}
