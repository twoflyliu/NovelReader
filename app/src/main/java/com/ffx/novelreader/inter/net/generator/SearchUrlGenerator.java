package com.ffx.novelreader.inter.net.generator;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

import java.net.MalformedURLException;

/**
 * SearchUrlGenerator 根据小说名称生成对应网站的搜索URL
 */
public interface SearchUrlGenerator {

    /**
     * 根据小说名称生成对应的搜索URL
     * @param novelName 小说名称
     * @return 搜索URL
     */
    String generate(String novelName);
}
