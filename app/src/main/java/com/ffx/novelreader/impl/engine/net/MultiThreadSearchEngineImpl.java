package com.ffx.novelreader.impl.engine.net;

import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.net.SearchUrlFactory;
import com.ffx.novelreader.inter.net.SearchUrlGeneratorParser;
import com.ffx.novelreader.inter.net.engine.SearchEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class MultiThreadSearchEngineImpl extends SearchEngineImplBase implements SearchEngine {

    @Override
    public List<Novel> search(final String novelName) {
        Collection<SearchUrlGeneratorParser> list = SearchUrlFactory.getInstance().getAllSearchUrlGenerators();
        final List<Novel> result = new Vector<>();
        List<Thread> threadList = new ArrayList<>();

        for (final SearchUrlGeneratorParser generatorParser : list) {
           Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    search(novelName, generatorParser, result);
                }
            });
            thread.start();
            threadList.add(thread);
        }

        // 等待所有线程搜索完毕
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

}
