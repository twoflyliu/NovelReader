package com.ffx.novelreader.inter.dao;

import com.ffx.novelreader.entity.po.Menu;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public interface MenuDao {

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
     * 根据小说id来执行删除
     * @param novelId 小说id
     * @return 成功与否
     */
    boolean deleteByNovelId(int novelId);

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

    /**
     * 保存菜单列表到数据中
     * @param menuList 菜单列表
     * @return 成功与否
     */
    boolean saveAll(List<Menu> menuList);

}
