package com.renny.simplebrowser.view.page;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseFragment;
import com.renny.simplebrowser.business.db.dao.BookMarkDao;
import com.renny.simplebrowser.business.db.dao.HistoryDao;
import com.renny.simplebrowser.business.db.entity.BookMark;
import com.renny.simplebrowser.business.db.entity.History;
import com.renny.simplebrowser.business.helper.DeviceHelper;
import com.renny.simplebrowser.business.helper.EventHelper;
import com.renny.simplebrowser.business.helper.KeyboardUtils;
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.business.toast.ToastHelper;
import com.renny.simplebrowser.business.webview.X5DownloadListener;
import com.renny.simplebrowser.business.webview.X5WebChromeClient;
import com.renny.simplebrowser.business.webview.X5WebView;
import com.renny.simplebrowser.business.webview.X5WebViewClient;
import com.renny.simplebrowser.globe.helper.DateUtil;
import com.renny.simplebrowser.view.event.WebViewEvent;
import com.renny.simplebrowser.view.listener.OnItemClickListener;
import com.renny.simplebrowser.view.listener.SimpleTextWatcher;
import com.renny.simplebrowser.view.page.dialog.HandleListDialog;
import com.renny.simplebrowser.view.page.dialog.HandlePictureDialog;
import com.renny.simplebrowser.view.presenter.WebViewPresenter;
import com.renny.simplebrowser.view.widget.pullrefresh.PullToRefreshBase;
import com.renny.simplebrowser.view.widget.pullrefresh.PullToRefreshWebView;
import com.tencent.smtt.export.external.interfaces.IX5WebViewBase;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.Locale;


public class WebViewFragment extends BaseFragment implements X5WebView.onSelectItemListener {
    X5WebView mWebView;
    PullToRefreshWebView pullToRefreshWebView;
    TextView titleTv;
    ImageView markBookImg;
    TextView downloadTv;
    EditText searchEdit;
    TextView searchInfo;
    View searchLayout;
    ViewGroup mViewGroup;

    WebViewPresenter mWebViewPresenter;

    private HistoryDao mHistoryDao;
    private String targetUrl;
    private BookMarkDao mMarkDao;

    public static WebViewFragment getInstance(String url) {
        WebViewFragment webViewFragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        webViewFragment.setArguments(args);
        return webViewFragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_webview;
    }

    @Override
    protected void initPresenter() {
        mWebViewPresenter = new WebViewPresenter(this);
    }

    @Override
    protected void initParams(Bundle bundle) {
        targetUrl = bundle.getString("url");
    }

    @Override
    public void bindView(View rootView, Bundle savedInstanceState) {
        super.bindView(rootView, savedInstanceState);
        pullToRefreshWebView = rootView.findViewById(R.id.refreshLayout);
        mWebView = pullToRefreshWebView.getRefreshableView();
        mViewGroup = findViewById(R.id.webview_parent);
        titleTv = findViewById(R.id.title);
        markBookImg = findViewById(R.id.mark);
        downloadTv = findViewById(R.id.progressView);
        markBookImg.setOnClickListener(this);
        titleTv.setOnClickListener(this);

        searchEdit = findViewById(R.id.search_edit);
        searchInfo = findViewById(R.id.text_info);
        searchLayout = findViewById(R.id.search_layout);
        findViewById(R.id.search_button).setOnClickListener(this);
        findViewById(R.id.close_dialog).setOnClickListener(this);
        findViewById(R.id.forward_btn).setOnClickListener(this);
        findViewById(R.id.next_btn).setOnClickListener(this);

    }

    public WebView getWebView() {
        return mWebView;
    }

    public void afterViewBind(View rootView, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        }
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
                titleTv.setText(title);
                if (mHistoryDao == null) {
                    mHistoryDao = new HistoryDao();
                }
                String date = DateUtil.getDate();
                mHistoryDao.queryToheavy(date, webView.getUrl());
                mHistoryDao.addEntity(new History(DateUtil.getDate(), webView.getUrl(), title));

            }

            @Override
            public void onReceivedIcon(final WebView webView, final Bitmap icon) {
                super.onReceivedIcon(webView, icon);
                mWebViewPresenter.saveIcon(webView.getUrl(), webView.getFavicon());
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
                if (mMarkDao == null) {
                    mMarkDao = new BookMarkDao();
                }
                markBookImg.setSelected(mMarkDao.query(webView.getUrl()));
                pullToRefreshWebView.onPullDownRefreshComplete();
            }
        };
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);
        mWebView.loadUrl(targetUrl);
        mWebView.setDownloadListener(new X5DownloadListener(this, mWebView));
        mWebView.setOnSelectItemListener(this);

        searchEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(searchEdit.getText().toString())) {
                    searchInfo.setVisibility(View.INVISIBLE);
                } else {
                    searchInfo.setVisibility(View.VISIBLE);
                }
                String content = s.toString();
                if (!TextUtils.isEmpty(content)) {
                    mWebView.findAllAsync(content);
                }
            }
        });

    }

    public void showSearchDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition(mViewGroup, new Slide(Gravity.TOP));
        }
        searchLayout.setVisibility(View.VISIBLE);
        String content = searchEdit.getText().toString();
        if (TextUtils.isEmpty(content)) {
            searchInfo.setVisibility(View.INVISIBLE);
        } else {
            mWebView.findAllAsync(content);
            mWebView.clearMatches();
            searchInfo.setVisibility(View.VISIBLE);
        }
        KeyboardUtils.showSoftInput(getContext(), searchEdit);
        mWebView.setFindListener(new IX5WebViewBase.FindListener() {
            @Override
            public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches,
                                             boolean isDoneCounting) {
                if (isDoneCounting) {
                    if (numberOfMatches != 0) {
                        searchInfo.setText(String.format(Locale.CHINA, "%d/%d", (activeMatchOrdinal + 1), numberOfMatches));
                    } else {
                        searchInfo.setText("0/0");
                    }
                } else {
                    searchInfo.setText("");
                }
            }
        });
    }


    public void setMyProgress(int progress) {
        Logs.base.d("onDownloading2:  " + progress);
        downloadTv.setText(String.format("%s%%", String.valueOf(progress)));
        if (progress == 100) {
            downloadTv.setText(" ");
        }
    }

    @Override
    public void onImgSelected(int x, int y, int type, final String extra) {
        final HandlePictureDialog handlePictureDialog = HandlePictureDialog.getInstance(x, y, extra);
        handlePictureDialog.setWebView(mWebView);
        handlePictureDialog.show(getChildFragmentManager());
        DeviceHelper.vibrate(30);

    }

    @Override
    public void onLinkSelected(int x, int y, int type, final String extra) {
        ToastHelper.makeToast(extra);
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add("复制链接");
        titleList.add("在新标签页打开链接");
        HandleListDialog handleListDialog = HandleListDialog.getInstance(x, y, titleList);
        handleListDialog.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                switch (position) {
                    case 0:
                        UIHelper.clipContent(extra);
                        ToastHelper.makeToast("内容已复制");
                        break;
                    case 1:
                        EventHelper.post(new WebViewEvent(extra,true));
                        break;
                }
            }
        });
        handleListDialog.show(getChildFragmentManager());
    }

    public void loadUrl(String targetUrl) {
        mWebView.loadUrl(targetUrl);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String url = mWebView.getUrl();
        String title = mWebView.getTitle();
        switch (id) {
            case R.id.mark:
                if (mMarkDao == null) {
                    mMarkDao = new BookMarkDao();
                }
                if (!TextUtils.isEmpty(url)) {
                    if (markBookImg.isSelected()) {
                        mMarkDao.delete(url);
                        markBookImg.setSelected(false);
                    } else {
                        mMarkDao.addEntity(new BookMark(title, url));
                        markBookImg.setSelected(true);
                    }
                }
                break;
            case R.id.title:
                if (!TextUtils.isEmpty(url)) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra("url", url);
                    startActivityForResult(intent, 123);
                }
                break;
            case R.id.search_button:
                if (searchLayout.getVisibility() == View.VISIBLE) {
                    closeSearchDialog();
                } else {
                    showSearchDialog();
                }
                break;
            case R.id.forward_btn:
                mWebView.findNext(false);
                KeyboardUtils.hideSoftInput(getActivity(), searchEdit);
                break;
            case R.id.next_btn:
                mWebView.findNext(true);
                KeyboardUtils.hideSoftInput(getActivity(), searchEdit);
                break;
            case R.id.close_dialog:
                closeSearchDialog();
                break;

        }
    }

    private void closeSearchDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition(mViewGroup, new Slide(Gravity.TOP));
        }
        searchLayout.setVisibility(View.GONE);
        KeyboardUtils.hideSoftInput(getActivity(), searchEdit);
        mWebView.clearMatches();
    }
}
