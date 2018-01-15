package com.renny.simplebrowser.view.page;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseFragment;
import com.renny.simplebrowser.business.helper.DeviceHelper;
import com.renny.simplebrowser.business.helper.Folders;
import com.renny.simplebrowser.business.helper.ImgHelper;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.business.toast.ToastHelper;
import com.renny.simplebrowser.business.webview.X5WebChromeClient;
import com.renny.simplebrowser.business.webview.X5WebView;
import com.renny.simplebrowser.business.webview.X5WebViewClient;
import com.renny.simplebrowser.globe.helper.DownloadUtil;
import com.renny.simplebrowser.globe.helper.FileUtil;
import com.renny.simplebrowser.globe.helper.ThreadHelper;
import com.renny.simplebrowser.globe.task.ITaskWithResult;
import com.renny.simplebrowser.globe.task.TaskHelper;
import com.renny.simplebrowser.view.dialog.HandlePictureDialog;
import com.renny.simplebrowser.view.widget.pullrefresh.PullToRefreshBase;
import com.renny.simplebrowser.view.widget.pullrefresh.PullToRefreshWebView;
import com.renny.zxing.Activity.CaptureActivity;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;


public class WebViewFragment extends BaseFragment implements X5WebView.onSelectItemListener, DownloadListener {
    private X5WebView mWebView;
    private String mUrl;
    private OnReceivedListener onReceivedTitleListener;
    private int preProgress = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_webview;
    }


    @Override
    protected void initParams(Bundle bundle) {
        mUrl = bundle.getString("url");
    }


    public WebView getWebView() {
        return mWebView;
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
        final PullToRefreshWebView pullToRefreshWebView = rootView.findViewById(R.id.refreshLayout);
        mWebView = pullToRefreshWebView.getRefreshableView();
        mWebView.getView().setOverScrollMode(View.OVER_SCROLL_NEVER);
        pullToRefreshWebView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<X5WebView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<X5WebView> refreshView) {
                mWebView.reload();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<X5WebView> refreshView) {

            }
        });
        pullToRefreshWebView.setPullLoadEnabled(false);
        WebChromeClient webChromeClient = new X5WebChromeClient(getActivity()) {
            @Override
            public void onReceivedTitle(WebView webView, String title) {
                super.onReceivedTitle(webView, title);
                if (onReceivedTitleListener != null) {
                    onReceivedTitleListener.onReceivedTitle(webView.getUrl(), title);

                }
            }
        };

        WebViewClient webViewClient = new X5WebViewClient(getActivity()) {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                pullToRefreshWebView.onPullDownRefreshComplete();
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                pullToRefreshWebView.onPullDownRefreshComplete();
            }
        };
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);
        mWebView.loadUrl(mUrl);
        mWebView.setDownloadListener(this);
        mWebView.setOnSelectItemListener(this);
    }


    @Override
    public void onAttach(Context context) {
        if (context instanceof OnReceivedListener) {
            onReceivedTitleListener = (OnReceivedListener) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onSelected(int x, int y, int type, final String extra) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        final HandlePictureDialog handlePictureDialog = HandlePictureDialog.getInstance(x, y, extra);
        handlePictureDialog.setWebView(mWebView);
        handlePictureDialog.show(fm);
        DeviceHelper.vibrate(30);
        TaskHelper.submitResult(new ITaskWithResult<File>() {
            @Override
            public File onBackground() throws Exception {
                File sourceFile = ImgHelper.syncLoadFile(extra);
                File file = Folders.gallery.getFile(System.currentTimeMillis() + ".jpg");
                FileUtil.copyFile(sourceFile, file);
                return file;
            }

            @Override
            public void onComplete(File file) {
                if (file != null && file.exists()) {
                    String result = CaptureActivity.handleResult(file.getPath());
                    Logs.base.d("xxxx--" + result);
                    if (!TextUtils.isEmpty(result)) {
                        handlePictureDialog.setShowZxing(result);
                    }
                }
            }
        });
    }

    @Override
    public void onDownloadStart(final String url, String userAgent, String contentDisposition,
                                final String mimetype, long contentLength) {

        TaskHelper.submitResult(new ITaskWithResult<String>() {
            @Override
            public String onBackground() throws Exception {
                Logs.base.d("download:  " + url);
                DownloadUtil.get().download(url, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(final boolean exist, final File file) {
                        ThreadHelper.postMain(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(mWebView, exist?"文件已存在，是否立即打开文件？":"下载成功，是否立即打开文件？", Snackbar.LENGTH_LONG)
                                        .setAction("打开", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                DeviceHelper.openFile(getActivity(),file,mimetype);
                                            }
                                        })
                                        .show();
                            }
                        });
                    }

                    @Override
                    public void onDownloadStart() {
                        ThreadHelper.postMain(new Runnable() {
                            @Override
                            public void run() {
                                ToastHelper.makeToast("开始下载");
                            }
                        });
                    }

                    @Override
                    public void onDownloading(final int progress) {
                        Logs.base.d("onDownloading:  " + progress);
                        if (progress != preProgress) {
                            preProgress = progress;
                            ((WebViewActivity) getActivity()).setMyProgress(progress);
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

    public interface OnReceivedListener {
        void onReceivedTitle(String url, String title);
    }
}
