package com.ffx.novelreader.inter.service;

import com.ffx.novelreader.entity.po.Novel;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public interface NovelService {
    /**
     * 保存小说
     * @param novel 小说
     * @return 成功与否
     */
    boolean save(Novel novel);

    /**
     * 删除Novel, 返回成功与否
     * @param novel 小说
     * @return 成功与否
     */
    boolean delete(Novel novel);

    /**
     * 更新Novel, 返回成功与否
     * @param novel
     * @return 成功与否
     */
    boolean update(Novel novel);

    /**
     * 根据名称和作者进行查找
     * @param name  小说名称
     * @param author 小说作者
     * @return 匹配列表
     */
    List<Novel> find(String name, String author);

    /**
     * 根据小说名称进行查找
     * @param name 小说名称
     * @return 匹配列表
     */
    List<Novel> find(String name);

    /**
     * 查找所有小说
     * @return 所有小说
     */
    List<Novel> findAll();
}
