package com.ffx.novelreader.factory.net;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

import com.ffx.novelreader.inter.net.SearchUrlGeneratorParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * SearchUrlParser 工厂
 */
public class SearchUrlFactory {
    private static SearchUrlFactory instance; //单例对象
    private Map<String, SearchUrlGeneratorParser> urlGeneratorParserMap; //管理SearchUrlParser容器

    /**
     * getInstance 单例方法
     * @return 单例对象
     */
    public static SearchUrlFactory getInstance() {
        if (null == instance) {
            instance = new SearchUrlFactory();
        }
        return instance;
    }

    /**
     * SearchUrlFactory 默认构造器
     */
    private SearchUrlFactory() {
        urlGeneratorParserMap = new HashMap<>();
    }

    /**
     * registerSearchUrlGeneratorParser 注册SearchUrlParser对象
     * @param host 主机名称
     * @param generatorParser SearchUrlParser对象
     */
    public void registerSearchUrlGeneratorParser(String host,
                                                 SearchUrlGeneratorParser generatorParser) {
        urlGeneratorParserMap.put(host, generatorParser);
    }

    /**
     * unregisterSearchUrlGeneratorParser 反注册SearchUrlParser对象
     * @param host 主机名称
     */
    public void unregisterSearchUrlGeneratorParser(String host) {
        urlGeneratorParserMap.remove(host);
    }

    /**
     * getAllSearchUrlGenerators 获取所有SearchUrlParser对象对象
     * @return 所有SearchUrlParser对象对象
     */
    public Collection<SearchUrlGeneratorParser> getAllSearchUrlGenerators() {
        return urlGeneratorParserMap.values();
    }
}
