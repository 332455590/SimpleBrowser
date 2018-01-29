package com.renny.simplebrowser.globe.lang;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yh on 2016/5/19.
 */
public abstract class Maps {
    public static Map mapNull = null;

    public static Map newHash() {
        return new HashMap<>();
    }
}
