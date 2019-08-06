package com.ffx.novelreader.factory.service;

import com.ffx.novelreader.impl.service.SearchServiceImpl;
import com.ffx.novelreader.inter.service.SearchService;

/**
 * Created by TwoFlyLiu on 2019/8/5.
 */

public class ServiceFactory {
    private static ServiceFactory instance;
    private SearchService searchService = new SearchServiceImpl();

    public static ServiceFactory getInstance() {
        if (null == instance) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    private ServiceFactory() {

    }

    public SearchService getSearchService() {
        return searchService;
    }

}
