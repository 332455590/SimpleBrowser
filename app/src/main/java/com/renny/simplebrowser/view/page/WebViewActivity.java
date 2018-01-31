package com.renny.simplebrowser.view.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.ViewDragHelper;
import android.widget.ImageView;
import android.widget.Toast;

import com.renny.simplebrowser.R;
import com.renny.simplebrowser.business.base.BaseActivity;
import com.renny.simplebrowser.view.bean.db.dao.BookMarkDao;
import com.renny.simplebrowser.view.bean.db.entity.BookMark;
import com.renny.simplebrowser.business.helper.EventHelper;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.view.event.WebViewEvent;
import com.renny.simplebrowser.view.listener.GoPageListener;
import com.renny.simplebrowser.view.widget.GestureLayout;
import com.tencent.smtt.sdk.WebBackForwardList;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renny on 2018/1/2.
 */
public class WebViewActivity extends BaseActivity {
    List<Fragment> mFragmentList = new ArrayList<>();
    WebViewFragment currentFragment;
    HomePageFragment mHomePageFragment;
    GestureLayout mGestureLayout;
    FragmentManager mFragmentManager;
    private boolean isOnHomePage = false;
    private boolean fromBack = false;
    private long mExitTime = 0;

    BookMarkDao mMarkDao;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void bindView(Bundle savedInstanceState) {
        mGestureLayout = findViewById(R.id.gesture_layout);
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
                if (currentFragment == null) {
                    return false;
                }
                WebView webView = currentFragment.getWebView();
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
                        goWebView();
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


    private void goWebView() {
        mFragmentManager.beginTransaction().replace(R.id.container,
                currentFragment).commit();
        isOnHomePage = false;
        fromBack = false;
    }

    private void goWebView(String url, boolean newBlock) {
        goWebView(url, newBlock, false);
    }

    private void goWebView(String url, boolean newBlock, boolean needClearHistory) {
        if (currentFragment == null || newBlock) {
            WebViewFragment webViewFragment = WebViewFragment.getInstance(url);
            mFragmentList.add(webViewFragment);
            currentFragment = webViewFragment;
        } else {
            currentFragment.loadUrl(url, needClearHistory);
        }
        mFragmentManager.beginTransaction().replace(R.id.container,
                currentFragment).commit();
        isOnHomePage = false;
        fromBack = false;
    }

    private void goHomePage() {
        if (mHomePageFragment == null) {
            mHomePageFragment = new HomePageFragment();
            mHomePageFragment.setGoPageListener(new GoPageListener() {
                @Override
                public void onGoPage(String url) {
                    goWebView(url, false, true);
                }
            });
            mFragmentManager.beginTransaction().add(R.id.container, mHomePageFragment).commit();
        } else {
            mFragmentManager.beginTransaction().replace(R.id.container,
                    mHomePageFragment).commit();
        }
        isOnHomePage = true;
    }


    public void goSearchPage() {
        startActivityForResult(new Intent(WebViewActivity.this, SearchActivity.class), 123);
    }

    private void returnLastPage() {
        if (fromBack && isOnHomePage) {
            goWebView();
        } else {
            WebView webView = currentFragment.getWebView();
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                goHomePage();
            }
        }
    }

    private void goNextPage() {
        WebView webView = currentFragment.getWebView();
        if (webView.canGoForward()) {
            webView.goForward();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isOnHomePage) {
            WebView webView = currentFragment.getWebView();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && data != null) {
            String result = data.getStringExtra("url");
            goWebView(result, false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventHelper.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventHelper.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWebviewEvent(WebViewEvent event) {
        Logs.event.d("event--" + event.url);

        goWebView(event.url, event.newBlock);

    }


}
