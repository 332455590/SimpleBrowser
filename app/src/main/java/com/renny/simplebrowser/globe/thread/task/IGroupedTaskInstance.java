package com.renny.simplebrowser.globe.thread.task;

/**
 * 分组任务
 */
public interface IGroupedTaskInstance extends ITaskInstance, IGroup {

    String DEFAULT_TASK_NAME = "at";//anonymous-task

    /**
     * 丢弃新任务
     */
    int DISCARD_NEW = 0;

    /**
     * 取消老任务
     */
    int CANCEL_OLD = 1;

    /**
     * 强制提交，任务可重复
     */
    int FORCE_SUBMIT = 2;

    /**
     * 任务名称
     *
     * @return
     */
    String taskName();

    /**
     * 是否线性执行
     *
     * @return
     */
    boolean serialExecute();

    /**
     * 任务去重策略
     *
     * @return
     */
    int dualPolicy();
}
