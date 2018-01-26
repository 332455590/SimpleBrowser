package com.renny.simplebrowser.view.page.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseDialogFragment;
import com.renny.simplebrowser.business.helper.KeyboardUtils;
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.view.listener.SimpleTextWatcher;

/**
 * Created by Renny on 2018/1/10.
 */

public class SearchTextDialog extends BaseDialogFragment {

    private static String tag = SearchTextDialog.class.getName();

    EditText mEditText;
    ImageView forwardBtn, nextBtn;
    TextView searchInfo;
    private findWordListener mFindWordListener;

    public static SearchTextDialog getInstance(Context mContext, FragmentManager fm) {

        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = Fragment.instantiate(mContext, tag);
            SearchTextDialog dialogFragment = (SearchTextDialog) fragment;
            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);//设置取消标题栏
            dialogFragment.setCancelable(false);
            return dialogFragment;
        } else {
            return (SearchTextDialog) fragment;
        }
    }

    public void setFindWordListener(findWordListener findWordListener) {
        mFindWordListener = findWordListener;
    }

    public void show(FragmentManager manager) {
        if (!isShowing()) {
            show(manager, tag);
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_search;
    }


    public void bindView(View rootView, Bundle savedInstanceState) {
        mEditText = findViewById(R.id.search_edit);
        forwardBtn = findViewById(R.id.forward_btn);
        nextBtn = findViewById(R.id.next_btn);
        searchInfo = findViewById(R.id.text_info);
        forwardBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        findViewById(R.id.close_dialog).setOnClickListener(this);
    }


    public void afterViewBind(View rootView, Bundle savedInstanceState) {
        mEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if (!TextUtils.isEmpty(content) && mFindWordListener != null) {
                     mFindWordListener.findAll(content);
                }
            }
        });
        KeyboardUtils.showSoftInput(getContext(), mEditText);
    }

    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches) {
        if (numberOfMatches != 0) {
            searchInfo.setText(String.format("%d/%d", (activeMatchOrdinal + 1), numberOfMatches));
        }else {
            searchInfo.setText("0/0");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.forward_btn:
                mFindWordListener.findLast();
                break;
            case R.id.next_btn:
                mFindWordListener.findNext();
                break;
            case R.id.close_dialog:
                dismiss();
                break;
        }
        KeyboardUtils.hideSoftInput(getActivity(), mEditText);
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                window.setGravity(Gravity.LEFT | Gravity.TOP);
                lp.dimAmount = 0.0f;
                lp.windowAnimations = R.style.dialogAnim;
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = UIHelper.dip2px(50);
                window.setAttributes(lp);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(true);
            }
        }
    }

    public interface findWordListener {
        void findAll(String word);

        void findNext();

        void findLast();
    }

}

