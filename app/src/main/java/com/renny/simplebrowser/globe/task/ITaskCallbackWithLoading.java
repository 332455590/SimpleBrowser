package com.renny.simplebrowser.globe.task;


import com.renny.simplebrowser.business.base.ILoading;
import com.renny.simplebrowser.globe.thread.task.ITaskCallback;

/**
 *
 */
public interface ITaskCallbackWithLoading<T> extends ITaskCallback<T> {
    void setLoading(ILoading loading);
}
