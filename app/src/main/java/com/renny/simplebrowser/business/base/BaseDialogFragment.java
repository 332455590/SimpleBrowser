package com.renny.simplebrowser.business.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Renny on 2018/1/11.
 */

public abstract class BaseDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {
    protected View rootView;
    protected Context mContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            initParams(getArguments());
        }
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
            bindView(rootView, savedInstanceState);
            afterViewBind(rootView, savedInstanceState);
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;//保存Context引用
    }

    protected abstract int getLayoutId();

    public void bindView(View rootView, Bundle savedInstanceState) {
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
    }

    protected void initParams(Bundle bundle) {
    }

    @Nullable
    @Override
    public Context getContext() {
        if (mContext != null) {
            return mContext;
        }
        return super.getContext();
    }

    public BaseActivity getBaseActivity() {
        if (mContext != null) {
            return (BaseActivity) mContext;
        }
        return (BaseActivity) getActivity();
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    public void dismiss(boolean isResume) {
        if (isResume) {
            dismiss();
        } else {
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void dismissAllowingStateLoss() {
        if (isShowing()) {
            super.dismissAllowingStateLoss();
        }
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            super.dismiss();
        }
    }
}
