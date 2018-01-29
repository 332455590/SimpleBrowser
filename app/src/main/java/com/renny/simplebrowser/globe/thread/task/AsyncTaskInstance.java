package com.renny.simplebrowser.globe.thread.task;

import android.support.annotation.NonNull;

/**
 * 自定义异步任务，支持分组，串行并行执行，优先级策略
 * 支持取消和回调，回调方法在主线程执行
 */
public class AsyncTaskInstance<Result> extends AbstractTaskInstance<Result> {

    /**
     * 创建有回调的异步任务
     *
     * @param callable 待执行的任务
     * @param callback 回调，一般是处理 callable的返回值
     */
    private AsyncTaskInstance(ITaskBackground<Result> callable, ITaskCallback<Result> callback) {
        super(callable, callback);
    }

    private AsyncTaskInstance(Runnable runnable) {
        super(runnable);
    }

    /**
     * 设置任务名称
     * 同一个组内任务名称不能重复，
     * 同一个组内同一个名称的任务，同一时刻只能有一个待只执行的实例
     * 后续提交的同名任务将被丢弃
     *
     * @param taskName
     * @return
     */
    public AsyncTaskInstance<Result> taskName(@NonNull String taskName) {
        this.taskName = taskName;
        return this;
    }

    /**
     * 任务分组
     * 可以按组取消任务，设置同一组的任务的串行，默认并行
     *
     * @param groupName
     * @return
     */
    public AsyncTaskInstance<Result> groupName(@NonNull String groupName) {
        this.groupName = groupName;
        return this;
    }

    /**
     * 设置任务是串行还是并行，默认并行
     * 只在同一个组内有效，不同组的任务还是并行的
     *
     * @param serialExecute true 串行
     * @return
     */
    public AsyncTaskInstance<Result> serialExecute(boolean serialExecute) {
        this.serialExecute = serialExecute;
        return this;
    }

    /**
     * 优先级
     * {@link #PRIOR_NORMAL}
     * {@link #PRIOR_UI}
     * {@link #PRIOR_HIGH}
     *
     * @param priority
     * @return
     */
    public AsyncTaskInstance<Result> priority(int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * 去重策略
     * {@link #DISCARD_NEW}
     * {@link #CANCEL_OLD}
     * {@link #FORCE_SUBMIT}
     *
     * @param dualPolicy
     * @return
     */
    public AsyncTaskInstance<Result> dualPolicy(int dualPolicy) {
        this.dualPolicy = dualPolicy;
        return this;
    }

    public static AsyncTaskInstance<Void> build(Runnable runnable) {
        return new AsyncTaskInstance<>(runnable);
    }

    public static <Result> AsyncTaskInstance<Result> build(ITaskBackground<Result> callable, ITaskCallback<Result> callback) {
        return new AsyncTaskInstance<>(callable, callback);
    }

}
