package com.ffx.novelreader.util;

import android.text.TextUtils;
import android.util.Log;

import com.ffx.novelreader.application.AppContext;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class HttpUtil {
    private static final int CONNECT_TIMEOUT = 6; //6s
    private static final int READ_TIMEOUT = 6;
    private static final int WRITE_TIMEOUT = 6;

    private static final int FAIL_RETRY_COUNT = 100;
    private static final String TAG = "HttpUtil";
    private static int id = 0;

    private static OkHttpClient client;

    private static OkHttpClient newCacheClient() {
        int cacheSize = 10 * 1024 * 1024; //10mb
        Cache cache = new Cache(AppContext.applicationContext.getCacheDir(), cacheSize);
        return new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .cache(cache).build();
    }

    /**
     * downloadAndSave 下载指定页面
     * @param url 页面地址
     * @return 页面内容
     */
    public static String download(String url) {
        return downloadFailRetry(url, FAIL_RETRY_COUNT);
    }

    public static String downloadFailRetry(String url, int failRetryCount) {
        String result = null;

        while (failRetryCount > 0) {
            result = doDownload(url);
            if (TextUtils.isEmpty(result)) {
                failRetryCount--;
            } else {
                break;
            }
        }

        return result;
    }

    private static String doDownload(String url) {
        //OkHttpClient client = new OkHttpClient();

        if (client == null) {
            client = newCacheClient();
        }

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36")
                .build();
        String result = null;

        try {
            Log.d(TAG, "downloadAndSave: downloading " + url);
            Response response = client.newCall(request).execute();

            byte[] bytes = response.body().bytes();
            String charset = FileUtil.getCharset(bytes);
            result = new String(bytes, charset);

            Log.d(TAG, "downloadAndSave: url=" + url + ", content size = " + result.length() + ", charset=" + charset);

            if (result.length() >= 7) {
                Log.d(TAG, "downloadAndSave: last content = " + result.substring(result.length() - 7));
            }

            //FileUtil.writeFile(TAG + nextId() + ".txt", result);
        } catch (IOException e) {
            //e.printStackTrace();
            //Log.d(TAG, "downloadAndSave: url=" + url + ",content size = 0");
        }

        return result;
    }

    public static synchronized int nextId() {
        id ++;
        return id;
    }

}
