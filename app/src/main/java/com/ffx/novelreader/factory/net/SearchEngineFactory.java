package com.ffx.novelreader.factory.net;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

import com.ffx.novelreader.inter.net.engine.SearchEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchEngineFactory 搜索引擎工厂类（维护类）
 */
public class SearchEngineFactory {
    private static SearchEngineFactory instance; //单例对象
    List<SearchEngine> searchEngineList;

    /**
     * getInstance 工厂方法
     * @return 单例对象
     */
    public static SearchEngineFactory getInstance() {
        if (null == instance) {
            instance = new SearchEngineFactory();
        }
        return instance;
    }

    /**
     * SearchEngineFactory 默认构造器
     */
    private SearchEngineFactory() {
        searchEngineList = new ArrayList<>();
    }

    /**
     * 注册搜索引擎对象
     * @param searchEngine SearchEngine对象
     */
    public void registerSearchEngine(SearchEngine searchEngine) {
        searchEngineList.add(searchEngine);
    }

    /**
     * 反注册搜索引擎对象
     * @param searchEngine
     */
    public void unregisterSearchEngine(SearchEngine searchEngine) {
        searchEngineList.remove(searchEngine);
    }

    /**
     * 获取所有搜索引擎
     * @return 搜索引擎列表
     */
    public List<SearchEngine> getSearchEngineList() {
        return searchEngineList;
    }
}
