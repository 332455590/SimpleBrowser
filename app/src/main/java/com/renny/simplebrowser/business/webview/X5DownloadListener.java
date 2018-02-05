package com.renny.simplebrowser.business.webview;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.helper.DeviceHelper;
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.business.task.SimpleTask;
import com.renny.simplebrowser.business.task.TaskHelper;
import com.renny.simplebrowser.business.toast.ToastHelper;
import com.renny.simplebrowser.globe.helper.DownloadUtil;
import com.renny.simplebrowser.view.page.WebViewFragment;
import com.tencent.smtt.sdk.WebView;

import java.io.File;

/**
 * Created by Renny on 2018/1/15.
 */

public class X5DownloadListener implements com.tencent.smtt.sdk.DownloadListener {
    private WebViewFragment webViewFragment;
    private WebView mWebView;

    public X5DownloadListener(WebViewFragment webViewFragment, WebView webView) {
        this.webViewFragment = webViewFragment;
        mWebView = webView;
    }

    private int preProgress = 0;

    @Override
    public void onDownloadStart(final String url, final String userAgent, final String contentDisposition,
                                final String mimetype, long contentLength) {
        final String downloadSize;
        final String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        if (contentLength > 0) {
            downloadSize = Formatter.formatFileSize(webViewFragment.getActivity(), contentLength);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(webViewFragment.getActivity()); // dialog
        String message = UIHelper.getString(R.string.dialog_download, downloadSize);
        builder.setTitle(fileName)
                .setMessage(message)
                .setPositiveButton(UIHelper.getString(R.string.action_download),
                        dialogClickListener)
                .setNegativeButton(UIHelper.getString(R.string.action_cancel),
                        dialogClickListener).show();
    }

    private void downloadStart(final String url, String contentDisposition, final String mimetype) {
        final String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        TaskHelper.submitTask("download", new SimpleTask<File>() {
            @Override
            public void onProgressInfo(Object progressInfo) {
                int progress = (int) progressInfo;
                if (progress != preProgress) {
                    preProgress = progress;
                    webViewFragment.setMyProgress(progress);
                }
            }

            @Override
            public File onBackground() throws Exception {
                return DownloadUtil.get().download(url, fileName, this);
            }

            @Override
            public void onException(Throwable throwable) {
                super.onException(throwable);
                ToastHelper.makeToast("下载失败");
            }

            @Override
            public void onComplete(final File result) {
                String content = "下载成功，是否立即打开文件？";
                Snackbar snackbar = Snackbar.make(mWebView, content, Snackbar.LENGTH_LONG)
                        .setAction("打开", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DeviceHelper.openFile(webViewFragment.getActivity(), result, mimetype);
                            }
                        });
                snackbar.setText(content);
                View view = snackbar.getView();//获取Snackbar的view
                ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(UIHelper.getColor(R.color.white));//获取Snackbar的message控件，修改字体颜色
                snackbar.setActionTextColor(Color.parseColor("#FFFFFF"));  //设置Snackbar上的字体颜色
                snackbar.show();
                super.onComplete(result);
            }
        });

    }
}
