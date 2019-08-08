package com.ffx.novelreader.impl.service;

import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.dao.DaoFactory;
import com.ffx.novelreader.inter.dao.NovelDao;
import com.ffx.novelreader.inter.service.NovelService;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public class NovelServiceImpl implements NovelService {
    private NovelDao novelDao = DaoFactory.getInstance().getNovelDao();

    @Override
    public boolean save(Novel novel) {
        return novelDao.save(novel);
    }

    @Override
    public boolean delete(Novel novel) {
        return novelDao.delete(novel);
    }

    @Override
    public boolean update(Novel novel) {
        return novelDao.update(novel);
    }

    @Override
    public List<Novel> find(String name, String author) {
        return novelDao.find(name, author);
    }

    @Override
    public List<Novel> find(String name) {
        return novelDao.find(name);
    }

    @Override
    public List<Novel> findAll() {
        return novelDao.findAll();
    }
}
