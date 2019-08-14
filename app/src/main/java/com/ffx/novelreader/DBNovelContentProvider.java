package com.ffx.novelreader;

import android.util.Log;

import com.ffx.novelreader.entity.po.Chapter;
import com.ffx.novelreader.entity.po.Menu;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.service.ServiceFactory;
import com.ffx.novelreader.inter.service.ChapterService;
import com.ffx.novelreader.inter.service.MenuService;
import com.ffx.novelreader.inter.service.NovelService;
import com.ffx.novelreader.treader.db.BookCatalogue;
import com.ffx.novelreader.treader.db.BookList;
import com.ffx.novelreader.treader.util.ContentProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/12.
 */

public class DBNovelContentProvider implements ContentProvider {
    private static final String TAG = "DBNovelContentProvider";

    private long position = -1;
    private static final long INDENT_PARAGRAPH_CHAR_COUNT = 2L;
    private static final char INDENT_CHAR = '\u3000';

    private MenuService menuService = ServiceFactory.getInstance().getMenuService();
    private NovelService novelService = ServiceFactory.getInstance().getNovelService();
    private ChapterService chapterService = ServiceFactory.getInstance().getChapterService();

    private List<BookCatalogue> bookCatalogueList;

    private BookList bookList;
    private Novel novel;
    private List<Menu> menuList;

    private List<Range> chapterRangeList;

    private int bookLen;
    private PageBufferManager pageBufferManager = new PageBufferManager();

    @Override
    public long getBookLen() {
        return this.bookLen;
    }

    @Override
    public List<BookCatalogue> getDirectoryList() {
        if (null == bookCatalogueList) {
            bookCatalogueList = new ArrayList<>();
            chapterRangeList = new ArrayList<>();
            this.menuList = menuService.findByNovelId(novel.getId());
            bookLen = 0;
            int preLen = 0;

            for (Menu menu : menuList) {
                BookCatalogue bookCatalogue = new BookCatalogue();
                bookCatalogue.setBookCatalogueStartPos(bookLen);
                bookCatalogue.setBookCatalogue(menu.getTitle());
                bookCatalogueList.add(bookCatalogue);

                bookLen += menu.getContentLength() + INDENT_PARAGRAPH_CHAR_COUNT
                        + menu.getTitle().length() + 2; //+2因为\r\n
                chapterRangeList.add(new Range(preLen, bookLen));
                preLen = bookLen;
            }
        }
        return bookCatalogueList;
    }

    @Override
    public void openBook(BookList bookList) throws IOException {
        pageBufferManager.reset();
        List<Novel> novelList = novelService.find(bookList.getBookname(), bookList.getBookAuthor());
        if (novelList.size() > 0) {
            openBook(novelList.get(0));
        }
    }

    public void openBook(Novel novel) {
        Log.i(TAG, "openBook: name=" + novel.getName() + ", author=" + novel.getAuthor());
        this.novel = novel;

        getDirectoryList(); //获取目录
    }

    // 下一个字符
    // 如果back=true, 则当前位置不变
    @Override
    public int next(boolean back){
        position += 1;
        if (position > bookLen){
            position = bookLen;
            return -1;
        }
        char result = current();
        if (back) {
            position -= 1;
        }
        return result;
    }

    // 获取前一个字符
    // 如果back为true，则当前字符位置不变
    @Override
    public int pre(boolean back){
        position -= 1;
        if (position < 0){
            position = 0;
            return -1;
        }
        char result = current();
        if (back) {
            position += 1;
        }
        return result;
    }

    // 当前position对应的字符
    @Override
    public char current() {
        if (position < 0 || position >= bookLen) {
            return (char)-1;
        }

        pageBufferManager.setPosition(position);
        Chapter currentChapter = pageBufferManager.getCurrentChapter();
        int currentChapterIndex = pageBufferManager.getCurrentChapterIndex();

        long offsetPos = position - chapterRangeList.get(currentChapterIndex).getBegin();

        char currChar = '错';
        if (offsetPos < INDENT_PARAGRAPH_CHAR_COUNT) {
            currChar = INDENT_CHAR;
        } else if (offsetPos < INDENT_PARAGRAPH_CHAR_COUNT + currentChapter.getTitle().length()) {
            currChar = currentChapter.getTitle().toCharArray()[(int)(offsetPos - INDENT_PARAGRAPH_CHAR_COUNT)];
        } else if (offsetPos <= INDENT_PARAGRAPH_CHAR_COUNT + currentChapter.getTitle().length() + 1) {
            currChar = '\r';
        } else if (offsetPos <= INDENT_PARAGRAPH_CHAR_COUNT + currentChapter.getTitle().length() + 2) {
            currChar = '\n';
        } else {
            int index = (int)(offsetPos - INDENT_PARAGRAPH_CHAR_COUNT - currentChapter.getTitle().length() - 2);
            if (index >= 0 && index < currentChapter.getContent().length()) {
                currChar = currentChapter.getContent().charAt(index);
            }
        }

        return currChar;
    }

    private int calcChapterIndex(long position) {
        for (int i = 0; i < chapterRangeList.size(); i++) {
            Range range = chapterRangeList.get(i);
            if (position >= range.getBegin() && position < range.getEnd()) {
                return i;
            }
        }
        return -1;
    }

    // 当前位置到下一行内容(不包含换行符号)
    @Override
    public char[] nextLine(){
        if (position >= bookLen){
            return null;
        }
        String line = "";
        while (position < bookLen){
            int word = next(false);
            if (word == -1){
                break;
            }
            char wordChar = (char) word;
            if ((wordChar + "").equals("\r") && (((char)next(true)) + "").equals("\n")){
                next(false);
                break;
            }
            line += wordChar;
        }
        return line.toCharArray();
    }

    // 当前位置到前一行内容(不包含换行符号)
    @Override
    public char[] preLine(){
        if (position <= 0){
            return null;
        }
        String line = "";
        while (position >= 0){
            int word = pre(false);
            if (word == -1){
                break;
            }
            char wordChar = (char) word;
            if ((wordChar + "").equals("\n") && (((char)pre(true)) + "").equals("\r")){
                pre(false);
//                line = "\r\n" + line;
                break;
            }
            line = wordChar + line;
        }
        return line.toCharArray();
    }

    @Override
    public long getPosition() {
        return this.position;
    }

    @Override
    public void setPostition(long position) {
        this.position = position;
        pageBufferManager.setPosition(position);
    }

    class Range {
        int begin;
        int end;

        public Range() {}

        public Range(int begin, int end) {
            this.begin = begin;
            this.end = end;
        }

        public int getBegin() {
            return begin;
        }

        public void setBegin(int begin) {
            this.begin = begin;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }
    }

    class PageBufferManager {
        private static final String TAG = "PageBufferManager";
        private Chapter currentChapter;
        private Chapter prevChapter;
        private Chapter nextChapter;

        private int currentChapterIndex;

        public Chapter getCurrentChapter() {
            return currentChapter;
        }

        public void setPosition(long position) {
            if (position < 0) {
                position = 0;
            }

            if (position >= bookLen) {
                position = bookLen - 1;
            }

            int chapterIndex = calcChapterIndex(position);

            if (null == currentChapter) {
                initBufferChapter(chapterIndex);
            } else {
                updateBufferChapter(chapterIndex);
            }

            this.currentChapterIndex = chapterIndex;
        }

        private void updateBufferChapter(final int chapterIndex) {
//            Log.i(TAG, "updateBufferChapter: this.position="
//                    + position + ",chapterIndex=" + chapterIndex
//                    + ", currentChapterIndex=" + currentChapterIndex);
            if (this.currentChapterIndex == chapterIndex) return;

            if (chapterIndex == this.currentChapterIndex - 1) { //向前(向左）
                nextChapter = currentChapter;
                currentChapter = prevChapter;
                Log.i(TAG, "updateBufferChapter: hit prev chapter(" + chapterIndex + ")");

                if (chapterIndex > 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 加载前一页
                            Log.i(TAG, "updateBufferChapter(async): update chapter(" + (chapterIndex - 1) + ")");
                            prevChapter = chapterService.findByMenuId(menuList.get(chapterIndex - 1).getId());
                        }
                    }).start();
                }
            } else if (chapterIndex == this.currentChapterIndex + 1) { //向后（向右）
                prevChapter = currentChapter;
                currentChapter = nextChapter;
                Log.i(TAG, "updateBufferChapter: hit next chapter(" + chapterIndex + ")");

                if (chapterIndex + 1 < menuList.size()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 加载下一页
                            Log.i(TAG, "updateBufferChapter(async): update chapter(" + (chapterIndex + 1) + ")");
                            nextChapter = chapterService.findByMenuId(menuList.get(chapterIndex + 1).getId());
                        }
                    }).start();
                }
            } else { //点击菜单进行章节切换，则重新加载(用这个算法会卡的）
                Log.i(TAG, "updateBufferChapter: call initBufferChapter");
                initBufferChapter(chapterIndex);
            }
        }

        private void initBufferChapter(int chapterIndex) {
            if (-1 == chapterIndex) {
                chapterIndex = 0;
            }

            if (chapterIndex > 0) {
                Log.i(TAG, "initBufferChapter(sync): init prev chapter(" + (chapterIndex - 1) + ")");
                prevChapter = chapterService.findByMenuId(menuList.get(chapterIndex - 1).getId());
            }

            Log.i(TAG, "initBufferChapter(sync): init current chapter(" + (chapterIndex) + ")");
            currentChapter = chapterService.findByMenuId(menuList.get(chapterIndex).getId());

            if (chapterIndex + 1 < menuList.size()) {
                Log.i(TAG, "initBufferChapter(sync): init next chapter(" + (chapterIndex + 1) + ")");
                nextChapter = chapterService.findByMenuId(menuList.get(chapterIndex + 1).getId());
            }
        }

        public int getCurrentChapterIndex() {
            return currentChapterIndex;
        }

        public void reset() {
            currentChapter = null;
            prevChapter = null;
            nextChapter = null;

            currentChapterIndex = -1;
        }
    }
}
