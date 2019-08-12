package com.ffx.novelreader.inter.service;

import com.ffx.novelreader.treader.db.BookList;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/12.
 */

public interface BookListService {
    /**
     * 根据名称，作者进行查找
     * @param name      小说名称
     * @param author    小说作者
     * @return 返回匹配的BookList
     */
    List<BookList> find(String name, String author);
}
