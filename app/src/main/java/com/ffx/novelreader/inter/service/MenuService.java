package com.ffx.novelreader.inter.service;

import com.ffx.novelreader.entity.po.Menu;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public interface MenuService {

    /**
     * 保存菜单
     * @param menu 菜单
     * @return 成功与否
     */
    boolean save(Menu menu);

    /**
     * 删除菜单
     * @param menu 菜单
     * @return 成功与否
     */
    boolean delete(Menu menu);

    /**
     * 更新菜单
     * @param menu 菜单
     * @return 成功与否
     */
    boolean update(Menu menu);

    /**
     * 根据小说id进行查找
     * @param novelId 小说id
     * @return 对应小说的菜单列表
     */
    List<Menu> findByNovelId(int novelId);
}
