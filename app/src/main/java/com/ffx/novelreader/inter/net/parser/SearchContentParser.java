package com.ffx.novelreader.inter.net.parser;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

import com.ffx.novelreader.entity.net.SearchResult;

import java.util.List;

/**
 * SearchContentParser 此接口主要用于解析搜索页面
 */
public interface SearchContentParser {

    /**
     * 解析搜索页面
     * @param searchPage 搜索页面
     * @param novelName 小说名称
     * @return  解析后的搜索结果
     */
    List<SearchResult> parse(String searchPage, String novelName);

}
