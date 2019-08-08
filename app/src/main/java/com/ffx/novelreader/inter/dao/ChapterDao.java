package com.ffx.novelreader.inter.dao;

import com.ffx.novelreader.entity.po.Chapter;
import com.ffx.novelreader.entity.po.Menu;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public interface ChapterDao {

    /**
     * 保存章节内容
     * @param chapter 章节内容
     * @return 成功与否
     */
    boolean save(Chapter chapter);

    /**
     * 删除章节
     * @param chapter 章节内容
     * @return 成功与否
     */
    boolean delete(Chapter chapter);

    /**
     * 更新章节内容
     * @param chapter 章节内容
     * @return 成功与否
     */
    boolean update(Chapter chapter);

    /**
     * 根据菜单id进行查找
     * @param menuId 菜单i
     * @return 返回指定菜单id对应的章节
     */
    Chapter findByMenuId(int menuId);

    /**
     * 根据菜单列表来查找章节列表
     * @param menuList 菜单列表
     * @return 章节列表
     */
    List<Chapter> findByMenuList(List<Menu> menuList);
}
