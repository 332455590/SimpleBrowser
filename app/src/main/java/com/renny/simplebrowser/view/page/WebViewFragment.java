package com.renny.simplebrowser.view.page;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseFragment;
import com.renny.simplebrowser.business.helper.Folders;
import com.renny.simplebrowser.business.helper.ImgHelper;
import com.renny.simplebrowser.business.helper.Validator;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.business.toast.ToastHelper;
import com.renny.simplebrowser.business.webview.X5WebChromeClient;
import com.renny.simplebrowser.business.webview.X5WebView;
import com.renny.simplebrowser.business.webview.X5WebViewClient;
import com.renny.simplebrowser.globe.helper.BitmapUtils;
import com.renny.simplebrowser.globe.helper.FileUtil;
import com.renny.simplebrowser.globe.task.ITaskWithResult;
import com.renny.simplebrowser.globe.task.TaskHelper;
import com.renny.simplebrowser.view.dialog.ListDialog;
import com.renny.simplebrowser.view.listener.OnItemClickListener;
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
        final ListDialog bottomDialogFragment = ListDialog.getInstance(x, y);
        bottomDialogFragment.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                if (position == 0) {
                    downLoad(extra);
                } else if (position == 1) {
                    final String content = bottomDialogFragment.getResult();
                    if (Validator.checkUrl(content)) {
                        new AlertDialog.Builder(getContext())
                                .setMessage(content)
                                .setPositiveButton("复制", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setNegativeButton("直接访问", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mWebView.loadUrl(content);
                                    }
                                })
                                .show();
                     ;
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setMessage(content)
                                .setPositiveButton("复制", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setNegativeButton("搜索", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                      mWebView.loadUrl("http://wap.baidu.com/s?wd=" + content);
                                    }
                                })
                                .show();

                    }
                }
            }
        });
        bottomDialogFragment.show(fm, "fragment_bottom_dialog");
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
                    Logs.base.d("xxxx--"+result);
                    if (!TextUtils.isEmpty(result)) {
                        bottomDialogFragment.setShowZxing(result);
                    }
                }
            }
        });
    }
    private void downLoad(final String imgUrl) {
        ToastHelper.makeToast("开始保存");
        TaskHelper.submitResult(new ITaskWithResult<File>() {
            @Override
            public File onBackground() throws Exception {
                return ImgHelper.syncLoadFile(imgUrl);
            }

            @Override
            public void onComplete(File sourceFile) {
                if (sourceFile != null && sourceFile.exists()) {
                    File file = Folders.gallery.getFile(System.currentTimeMillis() + ".jpg");
                    FileUtil.copyFile(sourceFile, file);
                    BitmapUtils.displayToGallery(getActivity(), file);
                    ToastHelper.makeToast("保存成功");

                    new AlertDialog.Builder(getContext())
                            .setMessage("保存成功")
                            .setPositiveButton("打开相册", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                    ToastHelper.makeToast("保存失败");
                }
            }
        });
    }
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                String mimetype, long contentLength) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        getActivity().startActivity(intent);
    }

    public interface OnReceivedListener {
        void onReceivedTitle(String url, String title);
    }
}
