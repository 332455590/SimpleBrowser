package com.renny.simplebrowser.business.webview;

import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * @author Created by Renny on on 2018/2/1
 */

public final class InJavaScriptLocalObj {
    @JavascriptInterface
    public void showSource(String html) {
        getHtmlContent(html);
    }


    private void getHtmlContent(final String html){
        Log.d("LOGCAT","网页内容:"+html);

    }
}

