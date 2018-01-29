package com.renny.simplebrowser.globe.lang;

/**
 * Created by yh on 2016/8/3.
 */
public interface Times {
    //毫秒级别的时间
    /**
     * 秒
     */
    long SECOND_MILLS = 1000;
    /**
     * 分钟
     */
    long MINUTE_MILLS = SECOND_MILLS * 60;
    /**
     * 半小时
     */
    long HALF_HOUR_MILLS = MINUTE_MILLS * 30;
    /**
     * 1小时
     */
    long HOUR_MILLS = MINUTE_MILLS * 60;
    /**
     * 一天
     */
    long HALF_DAY_MILLS = HOUR_MILLS * 12;
    /**
     * 一天
     */
    long DAY_MILLS = HOUR_MILLS * 24;
    /**
     * 一周
     */
    long WEEK_MILLS = DAY_MILLS * 7;
    /**
     * 普通30天的月份
     */
    long MONTH_MILLS = DAY_MILLS * 30;
    /**
     * 365天的年份
     */
    long YEAR_MILLS = DAY_MILLS * 365;
}
