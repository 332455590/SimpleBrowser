package com.renny.simplebrowser.business.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.renny.simplebrowser.globe.task.TaskHelper;
import com.renny.simplebrowser.globe.thread.task.IGroup;

/**
 * Created by Renny on 2018/1/3.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener, IGroup {
    protected Activity mActivity;
    protected boolean mIsLoadedData = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            initParams(getParams(getArguments()));
        }
        initPresenter();
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
            bindView(rootView, savedInstanceState);
            afterViewBind(rootView, savedInstanceState);
        } else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    protected void initParams(Bundle bundle) {

    }

    protected void initPresenter() {

    }

    @Override
    public void onDestroy() {
        TaskHelper.cancelGroup(groupName());
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public String groupName() {
        return getClass().getSimpleName() + this.toString();
    }

    @Override
    public Context getContext() {
        if (mActivity != null) {
            return mActivity;
        }
        return super.getContext();
    }

    protected abstract int getLayoutId();

    public void bindView(View rootView, Bundle savedInstanceState) {
    }

    @Nullable
    public final <T extends View> T findViewById(@IdRes int id) {
        return rootView.findViewById(id);
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
    }

    private Bundle getParams(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        return bundle;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            handleOnVisibilityChangedToUser(isVisibleToUser);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(false);
        }
    }

    /**
     * 处理对用户是否可见
     *
     * @param isVisibleToUser
     */
    private void handleOnVisibilityChangedToUser(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            // 对用户可见
            if (!mIsLoadedData) {
                mIsLoadedData = true;
                onLazyLoadOnce();
            }
            onVisibleToUser();
        } else {
            // 对用户不可见
            onInvisibleToUser();
        }
    }

    /**
     * 懒加载一次。如果只想在对用户可见时才加载数据，并且只加载一次数据，在子类中重写该方法
     */
    protected void onLazyLoadOnce() {
    }

    /**
     * 对用户可见时触发该方法。如果只想在对用户可见时才加载数据，在子类中重写该方法
     */
    protected void onVisibleToUser() {
    }

    /**
     * 对用户不可见时触发该方法
     */
    protected void onInvisibleToUser() {
    }
}
