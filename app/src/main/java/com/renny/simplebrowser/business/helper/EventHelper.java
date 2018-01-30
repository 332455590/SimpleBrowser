package com.renny.simplebrowser.business.helper;


import org.greenrobot.eventbus.EventBus;

/**
 * 事件帮助类
 */
public final class EventHelper {


    public static void register(Object subscriber) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber);
        }
    }

    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    public static void post(Object event) {
        EventBus.getDefault().post(event);
    }


}