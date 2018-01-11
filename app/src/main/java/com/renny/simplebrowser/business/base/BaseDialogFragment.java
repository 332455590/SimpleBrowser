package com.renny.simplebrowser.business.base;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.renny.simplebrowser.R;

/**
 * Created by Renny on 2018/1/11.
 */

public abstract class BaseDialogFragment extends DialogFragment {

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

    protected abstract int getLayoutId();

    public void bindView(View rootView, Bundle savedInstanceState) {
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
    }

    protected void initParams(Bundle bundle) {
    }

}
