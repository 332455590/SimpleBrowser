package com.renny.simplebrowser.view.page;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
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
import com.renny.simplebrowser.business.helper.Folders;
import com.renny.simplebrowser.business.helper.ImgHelper;
import com.renny.simplebrowser.business.helper.KeyboardUtils;
import com.renny.simplebrowser.business.helper.SearchHelper;
import com.renny.simplebrowser.business.helper.UIHelper;
import com.renny.simplebrowser.business.helper.Validator;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.business.toast.ToastHelper;
import com.renny.simplebrowser.business.webview.X5DownloadListener;
import com.renny.simplebrowser.business.webview.X5WebChromeClient;
import com.renny.simplebrowser.business.webview.X5WebView;
import com.renny.simplebrowser.business.webview.X5WebViewClient;
import com.renny.simplebrowser.globe.helper.BitmapUtils;
import com.renny.simplebrowser.globe.helper.FileUtil;
import com.renny.simplebrowser.globe.task.ITaskWithResult;
import com.renny.simplebrowser.globe.task.TaskHelper;
import com.renny.simplebrowser.view.listener.OnItemClickListener;
import com.renny.simplebrowser.view.listener.SimpleTextWatcher;
import com.renny.simplebrowser.view.page.dialog.HandlePictureDialog;
import com.renny.simplebrowser.view.widget.pullrefresh.PullToRefreshBase;
import com.renny.simplebrowser.view.widget.pullrefresh.PullToRefreshWebView;
import com.renny.zxing.Activity.CaptureActivity;
import com.tencent.smtt.export.external.interfaces.IX5WebViewBase;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class WebViewFragment extends BaseFragment implements X5WebView.onSelectItemListener, OnItemClickListener {
    private X5WebView mWebView;
    PullToRefreshWebView pullToRefreshWebView;
    private String mUrl;
    String result;
    String extra;
    View search;
    HistoryDao mHistoryDao;
    TextView titleView;
    ImageView markBookImg;
    TextView mProgressView;

    EditText mEditText;
    ImageView forwardBtn, nextBtn;
    TextView searchInfo;
    View searchLayout;
    ViewGroup mViewGroup;
    BookMarkDao mMarkDao;

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
        mViewGroup = findViewById(R.id.webview_parent);
        titleView = findViewById(R.id.title);
        markBookImg = findViewById(R.id.mark);
        mProgressView = findViewById(R.id.progressView);
        markBookImg.setOnClickListener(this);
        titleView.setOnClickListener(this);
        search = findViewById(R.id.search_button);
        findViewById(R.id.search_button).setOnClickListener(this);
        mEditText = findViewById(R.id.search_edit);
        forwardBtn = findViewById(R.id.forward_btn);
        nextBtn = findViewById(R.id.next_btn);
        searchInfo = findViewById(R.id.text_info);
        searchLayout = findViewById(R.id.search_layout);
        forwardBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        findViewById(R.id.close_dialog).setOnClickListener(this);

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
                titleView.setText(title);
                if (mHistoryDao == null) {
                    mHistoryDao = new HistoryDao();
                }
                mHistoryDao.addEntity(new History(System.currentTimeMillis(), webView.getUrl(), title));
            }

            @Override
            public void onReceivedIcon(final WebView view, final Bitmap icon) {
                super.onReceivedIcon(view, icon);
                final String[] strings = view.getUrl().split("/");
                if (strings.length >= 2) {
                    String host = strings[2];
                    BitmapUtils.saveToFile(icon, Folders.icon.getFolder(), host.replace(".", ""));
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
        mWebView.setDownloadListener(new X5DownloadListener(this, mWebView));
        mWebView.setOnSelectItemListener(this);

        mEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(mEditText.getText().toString())) {
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
        String content=mEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            searchInfo.setVisibility(View.INVISIBLE);
        } else {
            mWebView.findAllAsync(content);
            mWebView.clearMatches();
            searchInfo.setVisibility(View.VISIBLE);
        }
        KeyboardUtils.showSoftInput(getContext(), mEditText);
        mWebView.setFindListener(new IX5WebViewBase.FindListener() {
            @Override
            public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches,
                                             boolean isDoneCounting) {
                if (isDoneCounting) {
                    if (numberOfMatches != 0) {
                        searchInfo.setText(String.format("%d/%d", (activeMatchOrdinal + 1), numberOfMatches));
                    } else {
                        searchInfo.setText("0/0");
                    }
                }
            }
        });
    }


    public void setMyProgress(int progress) {
        Logs.base.d("onDownloading2:  " + progress);
        mProgressView.setText(progress + "%");
        if (progress == 100) {
            mProgressView.setText(" ");
        }
    }

    @Override
    public void onImgSelected(int x, int y, int type, final String extra) {
        this.extra = extra;
        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("保存图片");
        final HandlePictureDialog handlePictureDialog = HandlePictureDialog.getInstance(x, y, itemList);
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
                        handlePictureDialog.addListData("识别图中二维码");
                    }
                }
            }
        });
    }

    @Override
    public void onTextSelected(int x, int y, int type, String extra) {
        ToastHelper.makeToast(extra);
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
                showSearchDialog();
                break;
            case R.id.forward_btn:
                mWebView.findNext(false);
                KeyboardUtils.hideSoftInput(getActivity(), mEditText);
                break;
            case R.id.next_btn:
                mWebView.findNext(true);
                KeyboardUtils.hideSoftInput(getActivity(), mEditText);
                break;
            case R.id.close_dialog:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    TransitionManager.beginDelayedTransition(mViewGroup, new Slide(Gravity.TOP));
                }
                searchLayout.setVisibility(View.GONE);
                KeyboardUtils.hideSoftInput(getActivity(), mEditText);
                mWebView.clearMatches();
                break;

        }
    }


}
