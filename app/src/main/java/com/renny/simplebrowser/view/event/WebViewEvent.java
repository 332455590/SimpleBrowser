package com.renny.simplebrowser.view.event;

/**
 * Created by Renny on 2018/1/16.
 */

public class WebViewEvent {

    public String url;
    public boolean newBlock = false;

    public WebViewEvent(String url) {
        this.url = url;
    }

    public WebViewEvent(String url, boolean newBlock) {
        this.url = url;
        this.newBlock = newBlock;
    }
}
