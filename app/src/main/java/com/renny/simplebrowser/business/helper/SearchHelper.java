package com.renny.simplebrowser.business.helper;

/**
 * Created by Renny on 2018/1/15.
 */

public class SearchHelper {

    public static String buildSearchUrl(String content) {
        return "http://wap.baidu.com/s?wd=" + content;
    }
}
