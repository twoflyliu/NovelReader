package com.ffx.novelreader.impl.service;

import com.ffx.novelreader.entity.po.Menu;
import com.ffx.novelreader.factory.dao.DaoFactory;
import com.ffx.novelreader.inter.dao.MenuDao;
import com.ffx.novelreader.inter.service.MenuService;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public class MenuServiceImpl implements MenuService {
    private MenuDao menuDao = DaoFactory.getInstance().getMenuDao();

    @Override
    public boolean save(Menu menu) {
        return menuDao.save(menu);
    }

    @Override
    public boolean delete(Menu menu) {
        return menuDao.delete(menu);
    }

    @Override
    public boolean update(Menu menu) {
        return menuDao.update(menu);
    }

    @Override
    public List<Menu> findByNovelId(int novelId) {
        return menuDao.findByNovelId(novelId);
    }
}
