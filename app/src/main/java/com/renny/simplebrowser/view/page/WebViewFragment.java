package com.renny.simplebrowser.view.page;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseFragment;
import com.renny.simplebrowser.business.db.dao.HistoryDao;
import com.renny.simplebrowser.business.db.entity.History;
import com.renny.simplebrowser.business.helper.DeviceHelper;
import com.renny.simplebrowser.business.helper.Folders;
import com.renny.simplebrowser.business.helper.ImgHelper;
import com.renny.simplebrowser.business.helper.SearchHelper;
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.business.helper.Validator;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.business.toast.ToastHelper;
import com.renny.simplebrowser.business.webview.X5DownloadListener;
import com.renny.simplebrowser.business.webview.X5WebChromeClient;
import com.renny.simplebrowser.business.webview.X5WebView;
import com.renny.simplebrowser.business.webview.X5WebViewClient;
import com.renny.simplebrowser.globe.helper.FileUtil;
import com.renny.simplebrowser.globe.task.ITaskWithResult;
import com.renny.simplebrowser.globe.task.TaskHelper;
import com.renny.simplebrowser.view.page.dialog.HandlePictureDialog;
import com.renny.simplebrowser.view.listener.OnItemClickListener;
import com.renny.simplebrowser.view.widget.pullrefresh.PullToRefreshBase;
import com.renny.simplebrowser.view.widget.pullrefresh.PullToRefreshWebView;
import com.renny.zxing.Activity.CaptureActivity;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;
import java.io.FileNotFoundException;


public class WebViewFragment extends BaseFragment implements X5WebView.onSelectItemListener, OnItemClickListener {
    private X5WebView mWebView;
    PullToRefreshWebView pullToRefreshWebView;
    private String mUrl;
    private OnReceivedListener onReceivedTitleListener;
    String result;
    String extra;
    HistoryDao mHistoryDao;

    public static WebViewFragment getInstance(String url) {
        WebViewFragment webViewFragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        webViewFragment.setArguments(args);
        return webViewFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_webview;
    }


    @Override
    protected void initParams(Bundle bundle) {
        mUrl = bundle.getString("url");
    }

    @Override
    public void bindView(View rootView, Bundle savedInstanceState) {
        super.bindView(rootView, savedInstanceState);
        pullToRefreshWebView = rootView.findViewById(R.id.refreshLayout);
        mWebView = pullToRefreshWebView.getRefreshableView();

    }

    public WebView getWebView() {
        return mWebView;
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
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
                if (mHistoryDao == null) {
                    mHistoryDao = new HistoryDao();
                }
                mHistoryDao.addEntity(new History(System.currentTimeMillis(), webView.getUrl(), title));
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
        mWebView.setDownloadListener(new X5DownloadListener((WebViewActivity) getActivity(), mWebView));
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
        this.extra = extra;
        final HandlePictureDialog handlePictureDialog = HandlePictureDialog.getInstance(x, y, extra);
        handlePictureDialog.show(getChildFragmentManager());
        DeviceHelper.vibrate(30);
        handlePictureDialog.setOnItemClickListener(this);
        TaskHelper.submitResult(new ITaskWithResult<File>() {
            @Override
            public File onBackground() throws Exception {
                File sourceFile = ImgHelper.syncLoadFile(extra);
                Logs.base.d("xxxx--" + extra);
                File file = Folders.temp.newTempFile(Validator.getNameFromUrl(extra), ".jpeg");
                FileUtil.copyFile(sourceFile, file);
                return file;
            }

            @Override
            public void onComplete(File file) {
                if (file != null && file.exists()) {
                    result = CaptureActivity.handleResult(file.getPath());
                    if (!TextUtils.isEmpty(result)) {
                        handlePictureDialog.setShowZxing(result);
                    }
                }
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        if (position == 0 && !TextUtils.isEmpty(extra)) {
            downLoad(extra);
        } else if (position == 1) {
            final String content = result;
            TextView textView = (TextView) (UIHelper.inflaterLayout(getActivity(), R.layout.item_textview));
            SpannableStringBuilder ssb = new SpannableStringBuilder(content);
            ssb.setSpan(new UnderlineSpan(), 0, content.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            textView.setText(ssb);
            if (Validator.checkUrl(content)) {
                new AlertDialog.Builder(getContext())
                        .setView(textView)
                        .setNegativeButton("复制", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UIHelper.clipContent(content);
                            }
                        })
                        .setPositiveButton("直接访问", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mWebView.loadUrl(content);
                            }
                        })
                        .show();
            } else {
                new AlertDialog.Builder(getContext())
                        .setView(textView)
                        .setMessage(content)
                        .setNegativeButton("复制", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UIHelper.clipContent(content);
                            }
                        })
                        .setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mWebView.loadUrl(SearchHelper.buildSearchUrl(content));
                            }
                        })
                        .show();

            }
        }
    }

    private void downLoad(final String imgUrl) {
        TaskHelper.submitResult(new ITaskWithResult<File>() {
            @Override
            public File onBackground() throws Exception {
                File sourceFile = ImgHelper.syncLoadFile(imgUrl);
                File file = Folders.Camera.getPublicFile(Environment.DIRECTORY_DCIM, Validator.getNameFromUrl(imgUrl), ".jpg");
                FileUtil.copyFile(sourceFile, file);
                Logs.common.d("getPath:" + file.getAbsolutePath());
                return file;
            }

            @Override
            public void onComplete(final File file) {
                if (file != null && file.exists()) {
                    try {
                        MediaStore.Images.Media.insertImage(getContext().getContentResolver(), file.getAbsolutePath(), "title", "description");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    new AlertDialog.Builder(getContext())
                            .setMessage("保存成功")
                            .setPositiveButton("查看", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DeviceHelper.openFile(getContext(), file);
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

    public interface OnReceivedListener {
        void onReceivedTitle(String url, String title);
    }
}
