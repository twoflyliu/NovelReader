package com.ffx.novelreader.inter.net.parser;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

import com.ffx.novelreader.entity.net.SearchResult;
import com.ffx.novelreader.entity.po.Menu;

import java.util.List;

/**
 * NovelMenuParser 主要解析出菜单页面中章节列表区域内容
 */
public interface NovelMenuParser {

    /**
     * parseMenuList 解析出菜单页面中章节列表区域内容
     * @param menuContent  菜单页面
     * @param searchResult 搜索结果
     * @return 菜单页面中章节列表区域中菜单列表（章节标题和章节URL）
     */
    List<Menu> parseMenuList(String menuContent, SearchResult searchResult);
}
