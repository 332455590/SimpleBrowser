package com.renny.simplebrowser.view.page.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseDialogFragment;
import com.renny.simplebrowser.view.adapter.DownloadAdapter;

/**
 * @author Created by Renny on on 2018/1/31
 */

public class DownloadDialogFragment extends BaseDialogFragment  {

    DownloadAdapter mDownloadAdapter;
    RecyclerView mRecyclerView;
    ImageView searchBtn;

    public static DownloadDialogFragment getInstance(Context mContext, FragmentManager fm) {
        String tag = DownloadDialogFragment.class.getName();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = Fragment.instantiate(mContext, tag);
            DownloadDialogFragment dialogFragment = (DownloadDialogFragment) fragment;
            dialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);//设置取消标题栏
            return dialogFragment;
        } else {
            return (DownloadDialogFragment) fragment;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), getTheme());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_download;
    }

    public void bindView(View rootView, Bundle savedInstanceState) {
        mRecyclerView = rootView.findViewById(R.id.history_list);
        searchBtn = rootView.findViewById(R.id.search_button);
    }

    @Override
    public void afterViewBind(View rootView, Bundle savedInstanceState) {
        mDownloadAdapter =new DownloadAdapter(mRecyclerView);

    }


}
