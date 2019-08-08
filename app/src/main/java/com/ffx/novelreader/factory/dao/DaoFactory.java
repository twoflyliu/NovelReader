package com.ffx.novelreader.factory.dao;

import com.ffx.novelreader.impl.dao.ChapterDaoImpl;
import com.ffx.novelreader.impl.dao.MenuDaoImpl;
import com.ffx.novelreader.impl.dao.NovelDaoImpl;
import com.ffx.novelreader.inter.dao.ChapterDao;
import com.ffx.novelreader.inter.dao.MenuDao;
import com.ffx.novelreader.inter.dao.NovelDao;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public class DaoFactory {
    private NovelDao novelDao = new NovelDaoImpl();
    private MenuDao menuDao = new MenuDaoImpl();
    private ChapterDao chapterDao = new ChapterDaoImpl();

    private static  DaoFactory instance;

    public static DaoFactory getInstance() {
        if (null == instance) {
            instance = new DaoFactory();
        }
        return instance;
    }

    public NovelDao getNovelDao() {
        return novelDao;
    }

    public MenuDao getMenuDao() {
        return menuDao;
    }

    public ChapterDao getChapterDao() {
        return chapterDao;
    }
}
