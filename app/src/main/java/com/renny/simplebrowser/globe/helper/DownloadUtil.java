package com.renny.simplebrowser.globe.helper;

import com.renny.simplebrowser.business.helper.Folders;
import com.renny.simplebrowser.business.task.SimpleTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
     */
    public File download(final String url, final String fileName, SimpleTask simpleTask) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
            try {
                is = response.body().byteStream();
                long total = response.body().contentLength();
                final File file = Folders.download.getFile(fileName);
                if (file.exists()) {
                    return file;
                }
                fos = new FileOutputStream(file);
                long sum = 0;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    sum += len;
                    final int progress = (int) (sum * 1.0f / total * 100);
                    simpleTask.postProgressInfo(progress);
                }
                fos.flush();
                return file;
            } finally {
                if (is != null)
                    is.close();
                if (fos != null)
                    fos.close();
            }
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
