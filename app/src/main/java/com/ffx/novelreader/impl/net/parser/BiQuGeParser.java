package com.ffx.novelreader.impl.net.parser;

import android.text.TextUtils;

import com.ffx.novelreader.entity.net.SearchResult;
import com.ffx.novelreader.entity.po.Chapter;
import com.ffx.novelreader.entity.po.Menu;
import com.ffx.novelreader.inter.net.parser.NovelParser;
import com.ffx.novelreader.util.StringUtil;
import com.ffx.novelreader.util.UrlStringUtil;

import org.w3c.dom.Text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class BiQuGeParser implements NovelParser {
    // 小说名称
    private static final String NOVEL_NAME_PATTERN = "\\<div\\s+id=\"info\"[\\s\\S]+?\\<h1\\>([\\s\\S]+?)\\</h1\\>";
    private Pattern novelNamePattern = Pattern.compile(NOVEL_NAME_PATTERN);

    // 小说作者
    private static final String NOVEL_AUTHOR_PATTERN = "\\<div\\s+id=\"info\"[\\s\\S]+?\\<p\\>([\\s\\S]+?)\\</p\\>";
    private Pattern novelAuthorPattern = Pattern.compile(NOVEL_AUTHOR_PATTERN);

    // 最后更新时间 (注意)
    private static final String LAST_UPDATE_TIME = "\\<div\\s+id=\"info\"[\\s\\S]+?\\<p\\>[\\s\\S]+?\\<p\\>[\\s\\S]+?\\<p\\>([\\s\\S]+?)\\</p\\>";
    private Pattern lastUpdateTimePattern = Pattern.compile(LAST_UPDATE_TIME);

    // 小说描述信息
    private static final String NOVEL_DESCRIPTION = "\\<div\\s+id=\"intro\"\\>([\\s\\S]+?)\\</div\\>";
    private Pattern novelDescriptionPattern = Pattern.compile(NOVEL_DESCRIPTION);

    // 小说图标地址(注意)
    private static final String NOVEL_ICON_URL = "\\<div\\s+id=\"fmimg\"[\\s\\S]+?\\<img.*?src=\"(.*?)\"";
    private Pattern novelIconUrlPattern = Pattern.compile(NOVEL_ICON_URL);

    // 最新章节名称
    private static final String NEWEST_CHAPTER_NAME = "\\<div\\s+id=\"info\"[\\s\\S]+?\\<p\\>[\\s\\S]+?\\<p\\>[\\s\\S]+?\\<p\\>[\\s\\S]+?\\<p\\>[\\s\\S]+?\\<a[\\s\\S]+?\\>([\\s\\S]+?)\\</a\\>";
    private Pattern newestChapterNamePattern = Pattern.compile(NEWEST_CHAPTER_NAME);

    private static final String CHINESE_SEMICOLON = "：";

    @Override
    public String parseNovelName(String menuPage, SearchResult searchResult) {
        String result = searchResult.getName();
        if (TextUtils.isEmpty(result)) {
            result = StringUtil.match(menuPage, novelNamePattern);
        }
        return result;
    }

    @Override
    public String parseLastUpdateTime(String menuPage, SearchResult searchResult) {
        String result = StringUtil.match(menuPage, lastUpdateTimePattern);
        result = StringUtil.removeBefore(result, CHINESE_SEMICOLON);
        return result;
    }

    @Override
    public String parseAuthorName(String menuPage, SearchResult searchResult) {
        String result = searchResult.getAuthor();
        if (TextUtils.isEmpty(result)) {
            result = StringUtil.match(menuPage, novelAuthorPattern);
        }
        result = StringUtil.removeBefore(result, CHINESE_SEMICOLON);
        return result;
    }

    @Override
    public String parseDescription(String menuPage, SearchResult searchResult) {
        String result = StringUtil.match(menuPage, novelDescriptionPattern);
        return StringUtil.htmtlRemoveEscape(result);
    }

    @Override
    public String parseIconUrl(String menuPage, SearchResult searchResult) {
        String result = StringUtil.match(menuPage, novelIconUrlPattern);
        result = UrlStringUtil.urlJoin(searchResult.getUrl(), result);
        return result;
    }

    @Override
    public String parseNewestChapterName(String menuPage, SearchResult searchResult) {
        String result = StringUtil.match(menuPage, newestChapterNamePattern);
        return result;
    }

    @Override
    public Chapter parseChapter(String chapterPage, SearchResult searchResult) {
        return null;
    }

    @Override
    public List<Menu> parseMenuList(String menuPage, SearchResult searchResult) {
        return null;
    }
}
