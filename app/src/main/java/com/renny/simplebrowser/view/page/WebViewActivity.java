package com.renny.simplebrowser.view.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.ViewDragHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseActivity;
import com.renny.simplebrowser.business.db.dao.BookMarkDao;
import com.renny.simplebrowser.business.db.entity.BookMark;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.view.event.WebviewEvent;
import com.renny.simplebrowser.view.listener.GoPageListener;
import com.renny.simplebrowser.view.widget.GestureLayout;
import com.tencent.smtt.sdk.WebBackForwardList;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by Renny on 2018/1/2.
 */
public class WebViewActivity extends BaseActivity implements WebViewFragment.OnReceivedListener {
    WebViewFragment webViewFragment;
    HomePageFragment mHomePageFragment;
    TextView titleView;
    ImageView markBookImg;
    GestureLayout mGestureLayout;
    TextView mProgressView;
    FragmentManager mFragmentManager;
    private boolean isOnHomePage = false;
    private boolean fromBack = false;
    private long mExitTime = 0;
    String url, title;
    BookMarkDao mMarkDao;
    View search;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void bindView(Bundle savedInstanceState) {
        titleView = findViewById(R.id.title);
        mGestureLayout = findViewById(R.id.gesture_layout);
        markBookImg = findViewById(R.id.mark);
        mProgressView = findViewById(R.id.progressView);
        search = findViewById(R.id.search_button);
        findViewById(R.id.search_button).setOnClickListener(this);
        markBookImg.setOnClickListener(this);
        titleView.setOnClickListener(this);
    }

    public void setMyProgress(int progress) {
        Logs.base.d("onDownloading2:  " + progress);
        mProgressView.setText(progress + "%");
        if (progress == 100) {
            mProgressView.setText(" ");
        }
    }

    @Override
    protected void afterViewBind(Bundle savedInstanceState) {
        super.afterViewBind(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        mMarkDao = new BookMarkDao();
        List<BookMark> markList = mMarkDao.queryForAll();
        if (markList == null || markList.isEmpty()) {
            mMarkDao.addEntity(new BookMark("我的掘金主页", "https://juejin.im/user/5795bb80d342d30059f14b1c"));
            mMarkDao.addEntity(new BookMark("GitHub地址", "https://github.com/renjianan/SimpleBrowser"));
            mMarkDao.addEntity(new BookMark("百度", "https://www.baidu.com/"));
        }
        goHomePage();
        mGestureLayout.setGestureListener(new GestureLayout.GestureListener() {
            @Override
            public boolean dragStartedEnable(int edgeFlags, ImageView view) {
                if (webViewFragment == null) {
                    return false;
                }
                WebView webView = webViewFragment.getWebView();
                if (webView == null) {
                    return false;
                }
                WebBackForwardList list = webView.copyBackForwardList();
                int size = list.getSize();
                if (edgeFlags == ViewDragHelper.EDGE_LEFT) {
                    return webView.canGoBack() || !isOnHomePage || fromBack;
                } else if (edgeFlags == ViewDragHelper.EDGE_RIGHT) {
                    if (isOnHomePage) {
                        return size > 0;
                    } else {
                        return webView.canGoForward();
                    }
                } else if (edgeFlags == ViewDragHelper.EDGE_BOTTOM) {
                    return !isOnHomePage;
                }
                return false;
            }

            @Override
            public void onViewMaxPositionReleased(int edgeFlags, ImageView view) {
                if (edgeFlags == ViewDragHelper.EDGE_LEFT) {
                    returnLastPage();
                } else if (edgeFlags == ViewDragHelper.EDGE_RIGHT) {
                    if (isOnHomePage) {
                        goWebView(null);
                    } else {
                        goNextPage();
                    }
                } else if (edgeFlags == ViewDragHelper.EDGE_BOTTOM) {
                    fromBack = true;
                    goHomePage();
                }
            }

            @Override
            public void onViewMaxPositionArrive(int edgeFlags, ImageView view) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.mark:
                if (!TextUtils.isEmpty(url)) {
                    if (markBookImg.isSelected()) {
                        mMarkDao.delete(url);
                        markBookImg.setSelected(false);
                    } else {
                        mMarkDao.addEntity(new BookMark(title, url));
                        markBookImg.setSelected(true);
                    }
                    mHomePageFragment.reloadMarkListData();
                }
                break;
            case R.id.title:
                String content = titleView.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    goSearchPage(url);
                }
                break;
            case R.id.search_button:
                if (!isOnHomePage) {
                    webViewFragment.showSearchDialog();
                }
        }
    }


    private void goWebView(String url) {
        search.setVisibility(View.VISIBLE);
        if (webViewFragment == null || !TextUtils.isEmpty(url)) {
            webViewFragment = WebViewFragment.getInstance(url);
        }
        mFragmentManager.beginTransaction().replace(R.id.container,
                webViewFragment).commit();
        isOnHomePage = false;
        fromBack = false;
        WebView webView = webViewFragment.getWebView();
        if (webView != null) {
            onReceivedTitle(webView.getUrl(), webView.getTitle());
        }
    }

    private void goHomePage() {
        search.setVisibility(View.INVISIBLE);
        if (mHomePageFragment == null) {
            mHomePageFragment = new HomePageFragment();
            mHomePageFragment.setGoPageListener(new GoPageListener() {
                @Override
                public void onGoPage(String url) {
                    if (!TextUtils.isEmpty(url)) {
                        goWebView(url);
                    } else {
                        goHomePage();
                    }
                }
            });
            mFragmentManager.beginTransaction().add(R.id.container, mHomePageFragment).commit();
        } else {
            mFragmentManager.beginTransaction().replace(R.id.container,
                    mHomePageFragment).commit();
        }
        titleView.setText("");
        markBookImg.setVisibility(View.INVISIBLE);
        isOnHomePage = true;
    }


    public void goSearchPage() {
        startActivityForResult(new Intent(WebViewActivity.this, SearchActivity.class), 123);
    }

    public void goSearchPage(String content) {
        Intent intent = new Intent(WebViewActivity.this, SearchActivity.class);
        intent.putExtra("url", content);
        startActivityForResult(intent, 123);
    }

    private void returnLastPage() {
        if (fromBack && isOnHomePage) {
            goWebView(null);
        } else {
            WebView webView = webViewFragment.getWebView();
            if (webView.canGoBack()) {
                webView.goBack();
                onReceivedTitle(webView.getUrl(), webView.getTitle());
            } else {
                goHomePage();
            }
        }
    }

    private void goNextPage() {
        WebView webView = webViewFragment.getWebView();
        if (webView.canGoForward()) {
            webView.goForward();
            onReceivedTitle(webView.getUrl(), webView.getTitle());
        }
    }

    @Override
    public void onBackPressed() {
        if (!isOnHomePage) {
            WebView webView = webViewFragment.getWebView();
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                goHomePage();
            }
        } else {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
        }
    }

    @Override
    public void onReceivedTitle(String url, String title) {
        Logs.base.d("onReceivedTitle:  " + title + "   " + url);
        markBookImg.setVisibility(View.VISIBLE);
        this.url = url;
        this.title = title;
        markBookImg.setSelected(mMarkDao.query(url));
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        } else {
            titleView.setText(url);
        }
        if (isOnHomePage) {
            titleView.setText("");
            markBookImg.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && data != null) {
            String result = data.getStringExtra("url");
            if (!TextUtils.isEmpty(result)) {
                goWebView(result);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebviewEvent(WebviewEvent event) {
        Logs.event.d("event--" + event.url);
        goWebView(event.url);
    }
}
