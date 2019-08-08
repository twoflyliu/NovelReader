package com.ffx.novelreader.impl.dao;

import com.ffx.novelreader.entity.po.Chapter;
import com.ffx.novelreader.entity.po.Menu;
import com.ffx.novelreader.inter.dao.ChapterDao;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public class ChapterDaoImpl implements ChapterDao {
    @Override
    public boolean save(Chapter chapter) {
        return chapter.save();
    }

    @Override
    public boolean delete(Chapter chapter) {
        return chapter.delete() > 0;
    }

    @Override
    public boolean update(Chapter chapter) {
        return chapter.save();
    }

    @Override
    public Chapter findByMenuId(int menuId) {
        List<Chapter> chapters = DataSupport.where("menuid=?", String.valueOf(menuId)).find(Chapter.class);
        return chapters.size() > 0 ? chapters.get(0) : null;
    }

    @Override
    public List<Chapter> findByMenuList(List<Menu> menuList) {
        List<Chapter> chapters = new ArrayList<>();
        for (Menu menu : menuList) {
            Chapter chapter = findByMenuId(menu.getId());
            if (chapter != null) {
                chapters.add(chapter);
            }
        }
        return chapters;
    }
}
