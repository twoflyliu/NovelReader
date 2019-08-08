package com.ffx.novelreader.factory.service;

import com.ffx.novelreader.adapter.NovelSearchResultAdapter;
import com.ffx.novelreader.impl.service.ChapterServiceImpl;
import com.ffx.novelreader.impl.service.DownloadServiceImpl;
import com.ffx.novelreader.impl.service.MenuServiceImpl;
import com.ffx.novelreader.impl.service.NovelServiceImpl;
import com.ffx.novelreader.impl.service.SearchServiceImpl;
import com.ffx.novelreader.inter.service.ChapterService;
import com.ffx.novelreader.inter.service.DownloadService;
import com.ffx.novelreader.inter.service.MenuService;
import com.ffx.novelreader.inter.service.NovelService;
import com.ffx.novelreader.inter.service.SearchService;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class ServiceFactory {
    private static ServiceFactory instance;

    private SearchService searchService = new SearchServiceImpl();
    private DownloadService downloadService = new DownloadServiceImpl();

    private NovelService novelService = new NovelServiceImpl();
    private MenuService menuService = new MenuServiceImpl();
    private ChapterService chapterService = new ChapterServiceImpl();

    public static ServiceFactory getInstance() {
        if (null == instance) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    private ServiceFactory() {

    }

    public SearchService getSearchService() {
        return searchService;
    }

    public NovelService getNovelService() {
        return novelService;
    }

    public MenuService getMenuService() {
        return menuService;
    }

    public ChapterService getChapterService() {
        return chapterService;
    }

    public DownloadService getDownloadService() {
        return downloadService;
    }
}
