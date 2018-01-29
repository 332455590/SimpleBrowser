package com.renny.simplebrowser.globe.thread.task;

/**
 *
 */
public interface ITaskBackground<T> {

    /**
     * Callable.call的别名，表明该方法在后台线程中执行
     *
     * @return
     * @throws Exception
     */
     T onBackground() throws Exception;
}
