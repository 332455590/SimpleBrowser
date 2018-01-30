package com.renny.simplebrowser.view.presenter;

import android.graphics.Bitmap;

import com.renny.simplebrowser.business.base.BasePresenter;
import com.renny.simplebrowser.business.helper.Folders;
import com.renny.simplebrowser.business.log.Logs;
import com.renny.simplebrowser.globe.helper.BitmapUtils;
import com.renny.simplebrowser.globe.task.SimpleTask;
import com.renny.simplebrowser.globe.task.TaskHelper;
import com.renny.simplebrowser.view.page.WebViewFragment;

import java.io.File;

/**
 * Created by Renny on 2018/1/29.
 */

public class WebViewPresenter extends BasePresenter<WebViewFragment> {

    public WebViewPresenter(WebViewFragment webViewFragment) {
        super(webViewFragment);
    }

    public void saveIcon(final String url, final Bitmap icon) {
        TaskHelper.submitTask("icon", new SimpleTask<File>() {
            @Override
            public File onBackground() throws Exception {
                File file = null;
                final String[] strings = url.split("/");
                if (strings.length >= 2) {
                    String host = strings[2].replace(".", "");
                    file = BitmapUtils.saveToFile(icon, Folders.icon.getFolder(), host + (int) (Math.random() * 1000));
                }
                return file;
            }

            @Override
            public void onComplete(File result) {
                super.onComplete(result);
                Logs.common.d("saveIcon--" + result.getAbsolutePath());
            }
        });
    }
}
