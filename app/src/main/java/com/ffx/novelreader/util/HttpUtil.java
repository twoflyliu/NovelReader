package com.ffx.novelreader.util;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class HttpUtil {
    private static final String TAG = "HttpUtil";
    private static int id = 0;

    /**
     * download 下载指定页面
     * @param url 页面地址
     * @return 页面内容
     */
    public static String download(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36")
                .build();
        String result = null;

        try {
            Log.d(TAG, "download: downloading " + url);
            Response response = client.newCall(request).execute();

            byte[] bytes = response.body().bytes();
            String charset = FileUtil.getCharset(bytes);
            result = new String(bytes, charset);

            Log.d(TAG, "download: url=" + url + ", content size = " + result.length() + ", charset=" + charset);

            if (result.length() >= 7) {
                Log.d(TAG, "download: last content = " + result.substring(result.length() - 7));
            }

            //FileUtil.writeFile(TAG + nextId() + ".txt", result);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "download: url=" + url + ",content size = 0");
        }

        return result;
    }

    public static synchronized int nextId() {
        id ++;
        return id;
    }

}
