package com.ffx.novelreader.inter.net.engine;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

import com.ffx.novelreader.entity.po.Novel;

import java.util.List;


/**
 * SearchEngine 搜索引擎接口
 */
public interface SearchEngine {

    /**
     * search 进行搜索
     * @param novelName 小说名称
     * @return 符合条件的小说列表
     */
    List<Novel> search(String novelName);

}
