package com.renny.simplebrowser.business.webview;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.view.View;
import android.webkit.URLUtil;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.helper.DeviceHelper;
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.business.toast.ToastHelper;
import com.renny.simplebrowser.globe.helper.DownloadUtil;
import com.renny.simplebrowser.globe.task.ITaskWithResult;
import com.renny.simplebrowser.globe.task.TaskHelper;
import com.renny.simplebrowser.view.page.WebViewActivity;
import com.tencent.smtt.sdk.WebView;

import java.io.File;

/**
 * Created by Renny on 2018/1/15.
 */

public class X5DownloadListener implements com.tencent.smtt.sdk.DownloadListener {
    private WebViewActivity mActivity;
    private WebView mWebView;

    public X5DownloadListener(WebViewActivity activity, WebView webView) {
        mActivity = activity;
        mWebView = webView;
    }

    private int preProgress = 0;

    @Override
    public void onDownloadStart(final String url, final String userAgent, final String contentDisposition,
                                final String mimetype, long contentLength) {
        final String downloadSize;
        final String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        if (contentLength > 0) {
            downloadSize = Formatter.formatFileSize(mActivity, contentLength);
        } else {
            downloadSize = UIHelper.getString(R.string.unknown_size);
        }
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        downloadStart(url, contentDisposition, mimetype);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity); // dialog
        String message = mActivity.getString(R.string.dialog_download, downloadSize);
        builder.setTitle(fileName)
                .setMessage(message)
                .setPositiveButton(mActivity.getResources().getString(R.string.action_download),
                        dialogClickListener)
                .setNegativeButton(mActivity.getResources().getString(R.string.action_cancel),
                        dialogClickListener).show();
    }

    private void downloadStart(final String url, String contentDisposition, final String mimetype) {
        final String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        TaskHelper.submitResult(new ITaskWithResult<String>() {
            @Override
            public String onBackground() throws Exception {
                Logs.base.d("download:  " + url);
                DownloadUtil.get().download(url, fileName, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(final boolean exist, final File file) {
                        Snackbar.make(mActivity.getWindow().getDecorView().findViewById(android.R.id.content), exist ? "文件已存在，是否立即打开文件？" : "下载成功，是否立即打开文件？", Snackbar.LENGTH_LONG)
                                .setAction("打开", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DeviceHelper.openFile(mActivity, file, mimetype);
                                    }
                                }).show();
                    }

                    @Override
                    public void onDownloadStart() {
                        ToastHelper.makeToast("开始下载");
                    }

                    @Override
                    public void onDownloading(final int progress) {
                        Logs.base.d("onDownloading:  " + progress);
                        if (progress != preProgress) {
                            preProgress = progress;
                            mActivity.setMyProgress(progress);
                        }
                    }

                    @Override
                    public void onDownloadFailed() {
                    }
                });
                return null;
            }
        });
    }
}
