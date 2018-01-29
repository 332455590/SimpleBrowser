package com.renny.simplebrowser.globe.thread.task;

public interface IPriorityTask extends Comparable<IPriorityTask> {
    /**
     * 高优先级
     */
    int PRIOR_HIGH = 256;
    int PRIOR_UI = 32;
    /**
     * 一般优先级
     */
    int PRIOR_NORMAL = 1;

    int getPriority();

//    void setPriority(int priority);

}
