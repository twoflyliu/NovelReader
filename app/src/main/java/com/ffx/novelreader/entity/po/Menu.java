package com.ffx.novelreader.entity.po;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class Menu {
    private int id;
    private String title;
    private String url;
    private int novelId;

    public Menu() {}

    public Menu(int id, String title, String url, int novelId) {
        this.id = id;
        this.title = title;
        this.url = url;
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
}
