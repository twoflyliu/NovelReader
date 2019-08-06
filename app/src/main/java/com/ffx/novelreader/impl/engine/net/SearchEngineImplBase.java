package com.ffx.novelreader.impl.engine.net;

import android.text.TextUtils;

import com.ffx.novelreader.entity.net.SearchResult;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.net.NovelParserFactory;
import com.ffx.novelreader.inter.net.SearchUrlGeneratorParser;
import com.ffx.novelreader.util.HttpUtil;
import com.ffx.novelreader.util.NovelParserUtil;
import com.ffx.novelreader.util.UrlStringUtil;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

/**
 * SearchEngineImplBase 内部执行搜索业务
 */
public class SearchEngineImplBase {

    /**
     * search 进行搜索
     * @param novelName 小说名称
     * @param searchUrlGeneratorParser  SearchUrlGenerat
     *                                  orParser对象
     * @param result 最终的搜索结果
     */
    protected  void search(String novelName, SearchUrlGeneratorParser searchUrlGeneratorParser, List<Novel> result) {
        // 1. 生成提取器
        String searchUrl = searchUrlGeneratorParser.generate(novelName);

        // 2. 下载搜索页面
        String searchPage = HttpUtil.download(searchUrl);
        if (TextUtils.isEmpty(searchPage)) {
            return;
        }

        // 3. 从搜索页面中提取出有用信息
        List<SearchResult> searchResultList = searchUrlGeneratorParser.parse(searchPage, novelName);

        for (SearchResult searchResult : searchResultList) {
            //4. 下载菜单页面
            String menuUrl = UrlStringUtil.urlJoin(searchUrl, searchResult.getUrl());
            String menuPage = HttpUtil.download(menuUrl);

            searchResult.setUrl(searchUrl);

            if (TextUtils.isEmpty(menuPage)) {
                continue;
            }

            //5. 从菜单页面提取出有用信息
            Novel novel = NovelParserUtil.parseNovelBasicInfo(menuPage, searchResult,
                    new NovelParserUtil.NovelParserOption(true),
                    NovelParserFactory.getInstance().getNovelParser(menuUrl));
            novel.setMenuUrl(menuUrl);

            result.add(novel);
        }

    }

}
