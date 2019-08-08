package com.ffx.novelreader.impl.engine.net;

import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.service.ServiceFactory;
import com.ffx.novelreader.inter.net.engine.SearchEngine;
import com.ffx.novelreader.inter.service.NovelService;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public class LocalSearchEngineImpl implements SearchEngine {
    private NovelService novelService = ServiceFactory.getInstance().getNovelService();

    @Override
    public List<Novel> search(String novelName) {
        return novelService.find(novelName);
    }
}
