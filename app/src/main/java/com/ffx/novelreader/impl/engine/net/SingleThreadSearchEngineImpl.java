package com.ffx.novelreader.impl.engine.net;

import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.net.SearchUrlFactory;
import com.ffx.novelreader.inter.net.SearchUrlGeneratorParser;
import com.ffx.novelreader.inter.net.engine.SearchEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class SingleThreadSearchEngineImpl extends SearchEngineImplBase implements SearchEngine  {
    @Override
    public List<Novel> search(String novelName) {

        Collection<SearchUrlGeneratorParser> list = SearchUrlFactory.getInstance().getAllSearchUrlGenerators();
        List<Novel> result = new ArrayList<>();

        for (SearchUrlGeneratorParser generatorParser : list) {
            search(novelName, generatorParser, result);
        }

        return result;
    }
}
