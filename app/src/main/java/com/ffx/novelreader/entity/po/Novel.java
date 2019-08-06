package com.ffx.novelreader.entity.po;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

/**
 * Novel 表示小说
 */
public class Novel {
    private int id;
    private String name;
    private String lastUpdateTime;
    private String author;
    private String menuUrl;
    private String iconUrl;
    private String newestChapterName;
    private String description;

    public Novel() {}

    public Novel(int id, String name, String lastUpdateTime, String author, String menuUrl,
                 String iconUrl, String newestChapterName, String description) {
        this.id = id;
        this.name = name;
        this.lastUpdateTime = lastUpdateTime;
        this.author = author;
        this.menuUrl = menuUrl;
        this.iconUrl = iconUrl;
        this.newestChapterName = newestChapterName;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getNewestChapterName() {
        return newestChapterName;
    }

    public void setNewestChapterName(String newestChapterName) {
        this.newestChapterName = newestChapterName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Novel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", author='" + author + '\'' +
                ", menuUrl='" + menuUrl + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", newestChapterName='" + newestChapterName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
