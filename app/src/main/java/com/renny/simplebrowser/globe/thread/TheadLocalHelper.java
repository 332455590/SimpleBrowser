package com.renny.simplebrowser.globe.thread;

/**
 * Created by yh on 2016/5/4.
 */
public class TheadLocalHelper {
    private static ThreadLocal<TaskInfo> threadLocal = new ThreadLocal<>();

    public static TaskInfo getTaskInfo() {
        return threadLocal.get();
    }

    public static void setTaskInfo(String groupName, String taskName) {
        threadLocal.set(new TaskInfo(groupName, taskName));
    }

    public static class TaskInfo {
        public String taskName;
        public String groupName;

        public TaskInfo(String groupName, String taskName) {
            this.taskName = taskName;
            this.groupName = groupName;
        }
    }
}
