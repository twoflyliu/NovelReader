package com.ffx.novelreader.impl.service;

import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.net.SearchEngineFactory;
import com.ffx.novelreader.inter.net.engine.SearchEngine;
import com.ffx.novelreader.inter.service.SearchService;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class SearchServiceImpl implements SearchService {

    /**
     * 执行搜索
     * @param novelName 小说名称
     * @return 返回小说列表
     */
    public List<Novel> search(String novelName) {
        List<Novel> result = null;
        List<SearchEngine> searchEngineList =  SearchEngineFactory
                .getInstance().getSearchEngineList();

        for (SearchEngine searchEngine : searchEngineList) {
            result = searchEngine.search(novelName);

            if (result != null && result.size() > 0) {
                break;
            }
        }

        return result;
    }

}
