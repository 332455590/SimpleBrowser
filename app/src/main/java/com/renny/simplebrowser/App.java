package com.renny.simplebrowser;

import android.app.Application;
import android.content.Context;

import com.renny.simplebrowser.business.log.Logs;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

/**
 * Created by Renny on 2018/1/2.
 *
 */

public class App extends Application {

    private static Context AppContext;

    public static Context getContext() {
        return AppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = getApplicationContext();
        Logs.h5.d("正在加载x5");
        QbSdk.PreInitCallback initCallback = new QbSdk.PreInitCallback() {

            @Override
            public void onCoreInitFinished() {
                Logs.h5.d("加载x5完成");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Logs.h5.d("加载x5结果:" + b);
            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Logs.h5.d("下载x5: onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
                Logs.h5.d("下载x5: onInstallFinish");
            }

            @Override
            public void onDownloadProgress(int i) {
                Logs.h5.d("下载x5: onDownloadProgress:" + i);
            }
        });
        QbSdk.initX5Environment(this, initCallback);
    }


}

