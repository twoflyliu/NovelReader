package com.ffx.novelreader.factory.net;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

import com.ffx.novelreader.inter.net.parser.NovelParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * NovelParserFactory NovelParser工厂类
 *
 */
public class NovelParserFactory {

    private static NovelParserFactory instance;         //单例对象
    private Map<String, NovelParser> novelParserMap;    //管理注册进来的NovelParser

    /**
     * getInstance 单例工厂方法
     *
     * 注意：此方法不是线程安全的
     *
     * @return 单例对象
     */
    public static NovelParserFactory getInstance() {
        if (null == instance) {
            instance = new NovelParserFactory();
        }
        return instance;
    }

    private NovelParserFactory() {
        novelParserMap = new HashMap<>();
    }

    /**
     * registerNovelParser 注册NovelParser
     * @param host      主机名
     * @param parser    NovelParser对象
     */
    public void registerNovelParser(String host, NovelParser parser) {
        novelParserMap.put(host, parser);
    }

    /**
     * unregisterNovelParser 反注册NovelParser
     * @param host 主机名
     */
    public void unregisterNovelParser(String host) {
        novelParserMap.remove(host);
    }

    /**
     * getNovelParser 根据url获取注册进来的NovelParser
     * @param url url字符串
     * @return url主机名对应的NovelParser对象
     */
    public NovelParser getNovelParser(String url) {
        NovelParser result = null;

        try {
            URL aUrl = new URL(url);
            result = novelParserMap.get(aUrl.getHost());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
