package com.ffx.novelreader.inter.net.parser;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

import com.ffx.novelreader.entity.net.SearchResult;

/**
 * NovelBaseInfoParser 解析菜单页面中的小说基本信息
 */
public interface NovelBaseInfoParser {

    /**
     * parseNovelName 解析菜单页面中的小说名称
     * @param menuPage 菜单页面
     * @param searchResult 搜索结果
     * @return 小说名称
     */
    String parseNovelName(String menuPage, SearchResult searchResult);

    /**
     * parseLastUpdateTime 解析菜单页面中的小说最后更新事件
     * @param menuPage 菜单页面
     * @param searchResult 搜索结果
     * @return 小说最后更新事件
     */
    String parseLastUpdateTime(String menuPage, SearchResult searchResult);

    /**
     * parseAuthorName 解析出菜单页面中的作者名称
     * @param menuPage 菜单页面
     * @param searchResult 搜索结果
     * @return 作者名称
     */
    String parseAuthorName(String menuPage, SearchResult searchResult);

    /**
     * parseDescription 解析出菜单页面中的小说描述信息
     * @param menuPage  菜单页面
     * @param searchResult 搜索结果
     * @return 小说描述信息
     */
    String parseDescription(String menuPage, SearchResult searchResult);

    /**
     * parseIconUrl 解析出菜单页面中的小说图标URL
     * @param menuPage 菜单页面
     * @param searchResult 搜索结果
     * @return 小说图标URL
     */
    String parseIconUrl(String menuPage, SearchResult searchResult);

    /**
     * parseNewestChapterName 解析出菜单页面中的最新章节名称
     * @param menuPage 菜单页面
     * @param searchResult 搜索结果
     * @return 最新章节名称
     */
    String parseNewestChapterName(String menuPage, SearchResult searchResult);
}
