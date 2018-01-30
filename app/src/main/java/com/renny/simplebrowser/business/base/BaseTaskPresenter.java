package com.renny.simplebrowser.business.base;

import android.content.ContentResolver;

import com.renny.simplebrowser.globe.helper.ContextHelper;
import com.renny.simplebrowser.globe.http.request.Api;
import com.renny.simplebrowser.business.task.ITask;
import com.renny.simplebrowser.business.task.TaskHelper;
import com.renny.simplebrowser.globe.thread.task.AsyncTaskInstance;
import com.renny.simplebrowser.globe.thread.task.IGroup;
import com.renny.simplebrowser.globe.thread.task.ITaskBackground;
import com.renny.simplebrowser.globe.thread.task.ITaskCallback;

import java.io.File;
import java.util.Map;

/**
 * Created by yh on 2016/4/7.
 */
public abstract class BaseTaskPresenter {
    private IGroup group;

    public BaseTaskPresenter(IGroup group) {
        this.group = group;
    }

    protected final String getNameFromTrace(StackTraceElement[] traceElements, int place) {
        String taskName;
        String className = getClass().getName();
        if (traceElements != null && traceElements.length > place) {
            StackTraceElement traceElement = traceElements[place];
            taskName = className + ":" + traceElement.getLineNumber();
        } else {
            taskName = className + ":task-" + System.currentTimeMillis();
        }
        return taskName;
    }

    protected final <T> void submitTask(ITask<T> task) {
        String taskName = getNameFromTrace(Thread.currentThread().getStackTrace(), 3);
        submitTask(taskName, task);
    }

    public final <T> void submitTask(String taskName, ITask<T> task) {
        TaskHelper.submitTask(taskName, group.groupName(), task, task);
    }

    protected final <T> void submitTask(String taskName, ITaskBackground<T> backgroudTask, ITaskCallback<T> callBack) {
        TaskHelper.submitTask(taskName, group.groupName(), backgroudTask, callBack);
    }


    protected final <T> AsyncTaskInstance<T> submitTask(ITaskBackground<T> onBack, ITaskCallback<T> callback) {
        String taskName = getNameFromTrace(Thread.currentThread().getStackTrace(), 3);
        return TaskHelper.submitTask(taskName, group.groupName(), onBack, callback);
    }

    protected final <T> AsyncTaskInstance<T> submitTaskSerial(ITaskBackground<T> onBack, ITaskCallback<T> callback) {
        String taskName = getNameFromTrace(Thread.currentThread().getStackTrace(), 3);
        return TaskHelper.submitTaskSerial(taskName, group.groupName(), onBack, callback);
    }


    public final <T> void apiCall(Api api, ITaskCallback<T> apiCallback) {
        TaskHelper.apiCall(group, api, null, apiCallback);
    }

    public final <T> void apiCall(Api api, Map<String, String> params, ITaskCallback<T> apiCallback) {
        TaskHelper.apiCall(group, api, params, apiCallback);
    }

    protected String getString(int strId, Object... formatArgs) {
        return ContextHelper.getAppContext().getString(strId, formatArgs);
    }

    protected ContentResolver getContentResolver() {
        return ContextHelper.getAppContext().getContentResolver();
    }

    public File getExternalCacheDir() {
        return ContextHelper.getAppContext().getExternalCacheDir();
    }
}
