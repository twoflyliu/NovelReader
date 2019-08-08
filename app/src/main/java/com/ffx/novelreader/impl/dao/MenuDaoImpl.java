package com.ffx.novelreader.impl.dao;

import com.ffx.novelreader.entity.po.Menu;
import com.ffx.novelreader.inter.dao.MenuDao;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public class MenuDaoImpl implements MenuDao {
    @Override
    public boolean save(Menu menu) {
        return menu.save();
    }

    @Override
    public boolean delete(Menu menu) {
        int rowsAffected = menu.delete();
        return rowsAffected > 0;
    }

    @Override
    public boolean update(Menu menu) {
        return menu.save();
    }

    @Override
    public List<Menu> findByNovelId(int novelId) {
        return DataSupport.where("novelid=?", String.valueOf(novelId)).find(Menu.class);
    }

    @Override
    public boolean saveAll(List<Menu> menuList) {
        boolean result = true;

        try {
            DataSupport.saveAll(menuList);
        } catch(Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }
}
