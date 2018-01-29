package com.renny.simplebrowser.globe.helper;


import com.renny.simplebrowser.business.log.LogScheduler;

/**
 * Created by yh on 2016/5/5.
 */
public abstract class LogHelper {
    private static LogScheduler networkLog = LogScheduler.instance("network");
    private static LogScheduler taskLog = LogScheduler.instance("task");
    private static LogScheduler imgLog = LogScheduler.instance("img");
    private static LogScheduler utilLog = LogScheduler.instance("util");
    private static LogScheduler parseLog = LogScheduler.instance("parse");

    public static LogScheduler network() {
        return networkLog;
    }

    public static LogScheduler task() {
        return taskLog;
    }

    public static LogScheduler img() {
        return imgLog;
    }

    public static LogScheduler util() {
        return utilLog;
    }

    public static LogScheduler parse() {
        return parseLog;
    }
}
