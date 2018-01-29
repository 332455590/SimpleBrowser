package com.renny.simplebrowser.globe.http.callback;


import com.renny.simplebrowser.business.base.ILoading;
import com.renny.simplebrowser.globe.thread.task.ITaskCallback;

/**
 *
 */
public interface ITaskCallbackWithLoading<T> extends ITaskCallback<T> {
    void setLoading(ILoading loading);
}
