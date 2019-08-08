package com.ffx.novelreader.impl.dao;

import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.inter.dao.NovelDao;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public class NovelDaoImpl implements NovelDao {
    @Override
    public boolean save(Novel novel) {
        return novel.save();
    }

    @Override
    public boolean delete(Novel novel) {
        int rowsAffected = novel.delete();
        return rowsAffected > 0;
    }

    @Override
    public boolean update(Novel novel) {
        return novel.save();
    }

    @Override
    public List<Novel> find(String name, String author) {
        return DataSupport.where("name=? and author =?", name, author).find(Novel.class);
    }

    @Override
    public List<Novel> find(String name) {
        return DataSupport.where("name=?", name).find(Novel.class);
    }

    @Override
    public List<Novel> findAll() {
        return DataSupport.findAll(Novel.class);
    }
}
