package com.ffx.novelreader.treader.util;

import com.ffx.novelreader.treader.db.BookCatalogue;
import com.ffx.novelreader.treader.db.BookList;

import java.io.IOException;
import java.util.List;

/**
 * 提供小说内容
 */
public interface ContentProvider {

    /**
     * 获取书本字节长度
     * @return 书本字节长度
     */
    long getBookLen();

    /**
     * 获取书本目录列表
     * @return 书本目录列表
     */
    List<BookCatalogue> getDirectoryList();

    /**
     * 打开书本
     * @param bookList
     */
    void openBook(BookList bookList)  throws IOException;

    /**
     * 获取下一个字符
     * @param back 如果为true, 则不会改变当前位置
     * @return 如果返回-1，则已经读到结尾了，否则返回下一个字符
     */
    int next(boolean back);

    /**
     * 获取前一个字符
     * @param back 如果为true, 则不会改变当前位置
     * @return 如果返回-1，则表示已经读到开头了，否则返回前一个字符
     */
    int pre(boolean back);

    /**
     * 获取当前字符
     * @return 如果返回-1，则表示已经读到末尾了，否则返回当前字符
     */
    char current();

    /**
     * 获取当前位置到下一行内容（不包含回车，换行）
     * @return 当前位置到下一行内容（不包含回车，换行）
     */
    char[] nextLine();

    /**
     * 获取当前位置到前一行内容（不包含回车，换行）
     * @return 当前位置到前一行内容（不包含回车，换行）
     */
    char[] preLine();

    /**
     * 获取当前位置
     * @return 当前位置
     */
    long getPosition();

    /**
     * 设置当前位置
     * @param position 要设置的当前位置
     */
    void setPostition(long position);
}
