package com.renny.simplebrowser.business.webview;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by Renny on 2018/1/8.
 */

public class X5WebChromeClient extends WebChromeClient {
    private Context mContext;
    private View myVideoView;
    private View myNormalView;
    IX5WebChromeClient.CustomViewCallback callback;

    public X5WebChromeClient(Context context) {
        mContext = context;
    }

    @Override
    public void onHideCustomView() {
        if (callback != null) {
            callback.onCustomViewHidden();
            callback = null;
        }
        if (myVideoView != null) {
            ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
            viewGroup.removeView(myVideoView);
            viewGroup.addView(myNormalView);
        }
    }

    @Override
    public boolean onJsAlert(final WebView view, String url, final String message, JsResult result) {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage(message)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                view.reload();//重写刷新页面

                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
        result.confirm();//这里必须调用，否则页面会阻塞造成假死
        return true;
    }

    //获取图标
    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTitle(WebView arg0, final String arg1) {
        super.onReceivedTitle(arg0, arg1);
        Log.i("app", "webpage title is " + arg1);

    }
}
