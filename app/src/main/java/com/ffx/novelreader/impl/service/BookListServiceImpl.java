package com.ffx.novelreader.impl.service;

import com.ffx.novelreader.inter.service.BookListService;
import com.ffx.novelreader.treader.db.BookList;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/12.
 */

public class BookListServiceImpl implements BookListService {

    @Override
    public List<BookList> find(String name, String author) {
        return DataSupport.where("bookname=? and bookauthor=?", name, author).find(BookList.class);
    }

}
