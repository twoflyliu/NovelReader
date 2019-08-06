package com.ffx.novelreader.inter.service;

import com.ffx.novelreader.entity.po.Novel;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public interface SearchService {

    /**
     * 执行搜索
     * @param novelName 小说名称
     * @return 返回小说列表
     */
    List<Novel> search(String novelName);

}
