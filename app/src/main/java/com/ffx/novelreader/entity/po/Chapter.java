package com.ffx.novelreader.entity.po;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class Chapter {
    private String title;
    private String content;
    private int menuId;

    public Chapter() {}

    public Chapter(String title, String content, int menuId) {
        this.title = title;
        this.content = content;
        this.menuId = menuId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}
