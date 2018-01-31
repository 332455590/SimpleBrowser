package com.renny.simplebrowser.globe.helper;

import com.renny.simplebrowser.business.helper.Folders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Renny on 2018/1/12.
 */

public class DownloadUtil {

    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * @param url      下载连接
     * @param listener 下载监听
     */
    public void download(final String url, final String fileName, final OnDownloadListener listener) {

        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    final File file = Folders.download.getFile(fileName);

                    if (file.exists()) {
                        ThreadHelper.postMain(new Runnable() {
                            @Override
                            public void run() {
                                listener.onDownloadSuccess(true,file);
                            }
                        });
                        return;
                    }
                    ThreadHelper.postMain(new Runnable() {
                        @Override
                        public void run() {
                            listener.onDownloadStart();
                        }
                    });

                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        final int progress = (int) (sum * 1.0f / total * 100);
                        ThreadHelper.postMain(new Runnable() {
                            @Override
                            public void run() {
                                // 下载中
                                listener.onDownloading(progress);
                            }
                        });
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess(false,file);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onDownloadFailed();
                } finally {
                    if (is != null)
                        is.close();
                    if (fos != null)
                        fos.close();

                }
            }
        });
    }



    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(boolean exist,File file);
        /**
         * 下载成功
         */
        void onDownloadStart();
        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }
}
