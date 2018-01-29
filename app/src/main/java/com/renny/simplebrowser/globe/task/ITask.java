package com.renny.simplebrowser.globe.task;

import com.renny.simplebrowser.globe.http.callback.ITaskCallbackWithLoading;
import com.renny.simplebrowser.globe.thread.task.ITaskBackground;

/**
 *
 */
public interface ITask<T> extends ITaskBackground<T>, ITaskCallbackWithLoading<T> {
}
