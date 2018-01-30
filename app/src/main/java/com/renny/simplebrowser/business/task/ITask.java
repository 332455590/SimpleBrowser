package com.renny.simplebrowser.business.task;

import com.renny.simplebrowser.globe.thread.task.ITaskBackground;

/**
 *
 */
public interface ITask<T> extends ITaskBackground<T>, ITaskCallbackWithLoading<T> {
}
