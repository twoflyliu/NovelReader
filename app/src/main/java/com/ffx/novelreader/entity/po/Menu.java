package com.ffx.novelreader.entity.po;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class Menu extends DataSupport implements Serializable {
    private int id;
    private String title;
    private String url;
    private int contentLength;
    private int novelId;

    public Menu() {}

    public Menu(int id, String title, String url, int contentLength, int novelId) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.contentLength = contentLength;
        this.novelId = novelId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNovelId() {
        return novelId;
    }

    public void setNovelId(int novelId) {
        this.novelId = novelId;
    }

    @Override
    public String toString() {
        return title;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
}
