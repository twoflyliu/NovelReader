package com.ffx.novelreader.util;

import com.ffx.novelreader.entity.net.SearchResult;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.inter.net.parser.NovelParser;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

/**
 * NovelParserUtil 解析Novel相关内容
 */
public class NovelParserUtil {

    public static class NovelParserOption {
        private boolean needParseName;
        private boolean needParseLastUpdateTime;
        private boolean needParseAuthor;
        private boolean needParseMenuUrl;
        private boolean needParseIconUrl;
        private boolean needParseNewestChapterName;
        private boolean needParseDescription;

        public NovelParserOption(boolean option) {
            needParseName = option;
            needParseLastUpdateTime = option;
            needParseAuthor = option;
            needParseMenuUrl = option;
            needParseIconUrl = option;
            needParseNewestChapterName = option;
            needParseDescription = option;
        }

        public boolean needParse() {
            return needParseName && needParseLastUpdateTime && needParseAuthor && needParseMenuUrl
                    && needParseIconUrl && needParseNewestChapterName && needParseDescription;
        }

        public boolean isNeedParseName() {
            return needParseName;
        }

        public NovelParserOption setNeedParseName(boolean needParseName) {
            this.needParseName = needParseName;
            return this;
        }

        public boolean isNeedParseLastUpdateTime() {
            return needParseLastUpdateTime;
        }

        public NovelParserOption setNeedParseLastUpdateTime(boolean needParseLastUpdateTime) {
            this.needParseLastUpdateTime = needParseLastUpdateTime;
            return this;
        }

        public boolean isNeedParseAuthor() {
            return needParseAuthor;
        }

        public NovelParserOption setNeedParseAuthor(boolean needParseAuthor) {
            this.needParseAuthor = needParseAuthor;
            return this;
        }

        public boolean isNeedParseMenuUrl() {
            return needParseMenuUrl;
        }

        public NovelParserOption setNeedParseMenuUrl(boolean needParseMenuUrl) {
            this.needParseMenuUrl = needParseMenuUrl;
            return this;
        }

        public boolean isNeedParseIconUrl() {
            return needParseIconUrl;
        }

        public NovelParserOption setNeedParseIconUrl(boolean needParseIconUrl) {
            this.needParseIconUrl = needParseIconUrl;
            return this;
        }

        public boolean isNeedParseNewestChapterName() {
            return needParseNewestChapterName;
        }

        public NovelParserOption setNeedParseNewestChapterName(boolean needParseNewestChapterName) {
            this.needParseNewestChapterName = needParseNewestChapterName;
            return this;
        }

        public boolean isNeedParseDescription() {
            return needParseDescription;
        }

        public NovelParserOption setNeedParseDescription(boolean needParseDescription) {
            this.needParseDescription = needParseDescription;
            return this;
        }
    }

    /**
     * parseNovelBasicInfo 解析novel基本信息
     * @param menuPage  菜单页
     * @param searchResult 搜索结果
     * @param option novel解析选项
     * @param parser 解析器
     * @return 返回Novel对象
     */
    public static Novel parseNovelBasicInfo(String menuPage, SearchResult searchResult,
                                            NovelParserOption option, NovelParser parser) {
        Novel novel = null;
        if (null == option || null == parser) return null;

        if (option.needParse()) {
            novel = new Novel();

            if (option.isNeedParseName()) {
                novel.setName(parser.parseNovelName(menuPage, searchResult));
            }

            if (option.isNeedParseAuthor()) {
                novel.setAuthor(parser.parseAuthorName(menuPage, searchResult));
            }

            if (option.isNeedParseDescription()) {
                novel.setDescription(parser.parseDescription(menuPage, searchResult));
            }

            if (option.isNeedParseMenuUrl()) {
                novel.setMenuUrl(searchResult.getMenuUrl());
            }

            if (option.isNeedParseIconUrl()) {
                novel.setIconUrl(parser.parseIconUrl(menuPage, searchResult));
            }

            if (option.isNeedParseNewestChapterName()) {
                novel.setNewestChapterName(parser.parseNewestChapterName(menuPage, searchResult));
            }

            if (option.isNeedParseLastUpdateTime()) {
                novel.setLastUpdateTime(parser.parseLastUpdateTime(menuPage, searchResult));
            }

        }

        return novel;
    }

}
