package com.ffx.novelreader.impl.service;

import com.ffx.novelreader.entity.po.Chapter;
import com.ffx.novelreader.entity.po.Menu;
import com.ffx.novelreader.factory.dao.DaoFactory;
import com.ffx.novelreader.inter.dao.ChapterDao;
import com.ffx.novelreader.inter.service.ChapterService;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public class ChapterServiceImpl implements ChapterService {
    private ChapterDao chapterDao = DaoFactory.getInstance().getChapterDao();

    @Override
    public boolean save(Chapter chapter) {
        return chapterDao.save(chapter);
    }

    @Override
    public boolean delete(Chapter chapter) {
        return chapterDao.delete(chapter);
    }

    @Override
    public boolean update(Chapter chapter) {
        return chapterDao.update(chapter);
    }

    @Override
    public Chapter findByMenuId(int menuId) {
        return chapterDao.findByMenuId(menuId);
    }

    @Override
    public List<Chapter> findByMenuList(List<Menu> menuList) {
        return chapterDao.findByMenuList(menuList);
    }
}
