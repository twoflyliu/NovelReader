package com.ffx.novelreader.inter.net.parser;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

import com.ffx.novelreader.entity.net.SearchResult;
import com.ffx.novelreader.entity.po.Chapter;

/**
 * NovelChapterParser 解析出章节页面中的章节内容
 */
public interface NovelChapterParser {

    /**
     * parseChapter 解析出章节页面中的章节内容
     * @param chapterPage  章节页面
     * @param searchResult 搜索结果
     * @return 章节内容
     */
    Chapter parseChapter(String chapterPage, SearchResult searchResult);

}
