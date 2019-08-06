package com.ffx.novelreader.impl.net;

import android.util.Log;

import com.ffx.novelreader.entity.net.SearchResult;
import com.ffx.novelreader.inter.net.SearchUrlGeneratorParser;
import com.ffx.novelreader.util.UrlStringUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class BiQuGeSearchUrlGeneratorParser implements SearchUrlGeneratorParser {
    public static final String SEARCH_PAGE_NOVEL_URL_PATTEN_STRING = "<a href=\"([^\"]+)\" target=\"_blank\">\\s*%s\\s*</a>";

    private static final String TAG = "BiQuGeSearchUrlGenerato";

    @Override
    public String generate(String novelName) {
        return "https://www.37zw.net/s/so.php?type=articlename&s="
                + UrlStringUtil.toGBKAsciiString(novelName);
    }

    @Override
    public List<SearchResult> parse(String searchPage, String novelName) {
        List<SearchResult> searchResultList = new ArrayList<>();

        //Pattern pattern = Pattern.compile("<a href=\"([^\"]+)\" target=\"_blank\">\\s*" + novelName + "\\s*</a>");

        Pattern pattern = Pattern.compile(String.format(SEARCH_PAGE_NOVEL_URL_PATTEN_STRING, novelName));
        Matcher matcher = pattern.matcher(searchPage);

        while (matcher.find()) {
            SearchResult searchResult = new SearchResult();
            searchResult.setName(novelName);
            searchResult.setUrl(matcher.group(1));
            searchResultList.add(searchResult);
        }

        Log.d(TAG, "parse: len(SearchResult) = " + searchResultList.size());
        return searchResultList;
    }
}
