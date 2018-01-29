package com.renny.simplebrowser.business.base;

import com.renny.simplebrowser.globe.thread.task.IGroup;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

/**
 * Created by Renny on 2018/1/29.
 */

public class BasePresenter<View extends IGroup> extends BaseTaskPresenter{
    //View接口类型的软引用
    protected Reference<View> mViewRef;

    public BasePresenter(View group) {
        super(group);
        //建立关系
        mViewRef = new SoftReference<>(group);
    }


    protected View getView() {
        return mViewRef.get();
    }

    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
        }
    }


}
