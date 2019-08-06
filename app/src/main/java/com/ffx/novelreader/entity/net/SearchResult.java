package com.ffx.novelreader.entity.net;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

/**
 * 对应搜索页面的搜索结果
 */
public class SearchResult {
    private String name;
    private String author;
    private String url;
    private String menuUrl;

    public SearchResult() {}

    public SearchResult(String name, String author, String url, String menuUrl) {
        this.name = name;
        this.author = author;
        this.url = url;
        this.menuUrl = menuUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }
}
