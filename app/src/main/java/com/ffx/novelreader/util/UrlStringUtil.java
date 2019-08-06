package com.ffx.novelreader.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class UrlStringUtil {
    private static final String TAG = "UrlStringUtil";

    public static String toAsciiString(String chinese, String encoding) {
        String result = "";

        try {
            StringBuilder sb = new StringBuilder();
            byte[] gbkBytes = chinese.getBytes(encoding);
            for (byte ch : gbkBytes) {
                sb.append('%').append(Integer.toHexString(ch & 0xff));
            }
            result = sb.toString().toUpperCase();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String toGBKAsciiString(String chinese) {
        return toAsciiString(chinese, "GBK");
    }

    public static String toUTF8AsciiString(String chinese) {
        return toAsciiString(chinese, "UTF-8");
    }

    /**
     * 连接两个URL
     * @param host 主机URL地址
     * @param path PATH涮了地址
     * @return
     */
    public static String urlJoin(String host, String path) {
        String result = "";

        try {
            URL hostURL = new URL(host);
            URL absoluteURL = new URL(hostURL, path);
            result = absoluteURL.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "urlJoin: UrlStringUtil.urlJoin(\"" + host + "\", \"" + path + "\") = " + result);
        return result;
    }

    public static String getHostName(String host) {
        String result = "";
        try {
            URL url = new URL(host);
            result = url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
