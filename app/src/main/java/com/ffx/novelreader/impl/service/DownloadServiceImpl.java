package com.ffx.novelreader.impl.service;

import android.text.TextUtils;
import android.util.Log;

import com.ffx.novelreader.entity.net.SearchResult;
import com.ffx.novelreader.entity.po.Chapter;
import com.ffx.novelreader.entity.po.Menu;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.dao.DaoFactory;
import com.ffx.novelreader.factory.net.NovelParserFactory;
import com.ffx.novelreader.inter.dao.ChapterDao;
import com.ffx.novelreader.inter.dao.MenuDao;
import com.ffx.novelreader.inter.dao.NovelDao;
import com.ffx.novelreader.inter.net.parser.NovelParser;
import com.ffx.novelreader.inter.service.DownloadService;
import com.ffx.novelreader.util.HttpUtil;
import com.ffx.novelreader.util.NovelParserUtil;
import com.ffx.novelreader.util.SafeCounter;
import com.ffx.novelreader.util.UrlStringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/7.
 */

public class DownloadServiceImpl implements DownloadService {
    private static final String TAG = "DownloadServiceImpl";
    private static final int MAX_THREAD_COUNT = 50;

    private NovelDao novelDao = DaoFactory.getInstance().getNovelDao();
    private MenuDao menuDao = DaoFactory.getInstance().getMenuDao();
    private ChapterDao chapterDao = DaoFactory.getInstance().getChapterDao();

    private OnDownloadProgressChangeListener downloadProgressChangeListener;


    public void setOnDownloadProgressChangeListener(OnDownloadProgressChangeListener listener) {
        this.downloadProgressChangeListener = listener;
    }

    @Override
    public void downloadAndSave(final Novel novel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                doDownloadAndSave(novel);
            }
        }).start();
    }

    @Override
    public void updateAndSave(final Novel novel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                doUpdateAndSave(novel);
            }
        }).start();
    }

    private void doUpdateAndSave(Novel novel) {
        String novelIdentifier = "novel[" + novel.getName() + ", " + novel.getAuthor() + ", " + novel.getMenuUrl() + "]";

        //1. 小小说基本信息中提取url
        String menuUrl = novel.getMenuUrl();

        //2. 从源站上下载基本信息
        String menuPage = HttpUtil.download(menuUrl);

        //3. 解析出最后更新时间
        NovelParser parser = NovelParserFactory.getInstance().getNovelParser(menuUrl);
        SearchResult searchResult = new SearchResult();
        searchResult.setMenuUrl(menuUrl);
        searchResult.setName(novel.getName());

        String lastUpdateTime = parser.parseLastUpdateTime(menuPage, searchResult);

        //4. 检测时间是否有变化
        if (!novel.getLastUpdateTime().equals(lastUpdateTime)) {
            Log.i(TAG, "doUpdateAndSave: update " + novelIdentifier);
            //5. 提取出最新的章节列表
            List<Menu> newestMenuList = parser.parseMenuList(menuPage, searchResult);
            List<Menu> menuList = menuDao.findByNovelId(novel.getId());

            if (newestMenuList.size() >  menuList.size()) {
                List<Menu> tmpMenuList = new ArrayList<>();
                for (int i = menuList.size(); i < newestMenuList.size(); i++) {
                    Menu menu = newestMenuList.get(i);
                    menu.setNovelId(novel.getId());
                    menu.setUrl(UrlStringUtil.urlJoin(menuUrl, menu.getUrl()));
                    tmpMenuList.add(menu);
                }
                newestMenuList = tmpMenuList;

                //6. 保存最新的章节列表
                menuDao.saveAll(newestMenuList);
                downloadAndSaveChapterList(newestMenuList, parser, novel, new SafeCounter());
            }
        } else {
            Log.i(TAG, "doUpdateAndSave: detect " + novelIdentifier + " already newest");
            if (this.downloadProgressChangeListener != null) {
                this.downloadProgressChangeListener.onDownloadDone(novel);
            }
        }

    }

    boolean doDownloadAndSave(Novel novel) {
        //1. 小小说基本信息中提取url
        String menuUrl = novel.getMenuUrl();

        //2. 保存小说基本信息到数据库中
        String novelIdentifier = "novel[" + novel.getName() + ", " + novel.getAuthor() + ", " + novel.getMenuUrl() + "]";
        Log.i(TAG, "downloadAndSave: saveing " + novelIdentifier + " basic information");

        boolean saveNovelOk = novelDao.save(novel);
        Log.i(TAG, "downloadAndSave: save " + novelIdentifier + " basic information " + (saveNovelOk ? "success" : "fail"));

        //3. 下载菜单列表，提取菜单url
        String menuPage = HttpUtil.download(menuUrl);
        if (TextUtils.isEmpty(menuPage)) {
            Log.e(TAG, "doDownloadAndSave: download " + novelIdentifier + " fail");
            return false;
        }

        NovelParser parser = NovelParserFactory.getInstance().getNovelParser(menuUrl);

        SearchResult searchResult = new SearchResult();
        searchResult.setMenuUrl(menuUrl);
        List<Menu> menuList = parser.parseMenuList(menuPage, searchResult);
        Log.i(TAG, "downloadAndSave: saving " + novelIdentifier + " menu list");

        boolean saveMenuOk = true;
        for (Menu menu : menuList) {
            menu.setNovelId(novel.getId());
        }
        saveMenuOk = menuDao.saveAll(menuList);

        if (saveMenuOk) {
            Log.i(TAG, "downloadAndSave: save " + novelIdentifier + " menu list success");
        }

        //4. 根据章节列表下载所有章节内容到，并且同步到数据库中
        boolean downloadAndSaveChapterListOk = downloadAndSaveChapterList(menuList, parser, novel, new SafeCounter());

        return saveNovelOk && saveMenuOk && downloadAndSaveChapterListOk;
    }

    private boolean downloadAndSaveChapterList(final List<Menu> menuList, final NovelParser parser, final Novel novel, final SafeCounter completeChapterNum) {
        final List<Thread> threadList = new ArrayList<>();

        if (menuList.size() < MAX_THREAD_COUNT) {
            for (int i = 0; i < menuList.size(); i++) {
                final int index = i;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadAndSaveChapter(menuList, index, 1, parser, novel, completeChapterNum);
                    }
                });
                threadList.add(thread);
                thread.start();
            }
        } else {
            final int taskPerThread = menuList.size() / MAX_THREAD_COUNT;
            for (int i = 0; i < MAX_THREAD_COUNT; i++) {
                final int index = i;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadAndSaveChapter(menuList, index * taskPerThread, taskPerThread,
                                parser, novel, completeChapterNum);
                    }
                });
                threadList.add(thread);
                thread.start();
            }

            final int leftTaskCount = menuList.size() % MAX_THREAD_COUNT;
            if (leftTaskCount > 0) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadAndSaveChapter(menuList, MAX_THREAD_COUNT * taskPerThread,
                                leftTaskCount, parser, novel, completeChapterNum);
                    }
                });
                threadList.add(thread);
                thread.start();
            }
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (this.downloadProgressChangeListener != null) {
            this.downloadProgressChangeListener.onDownloadDone(novel);
        }
        Log.i(TAG, "downloadAndSaveChapterList: done - debugTaskCount=" + completeChapterNum);

        return true;
    }

    private boolean downloadAndSaveChapter(List<Menu> menuList, int start, int count, NovelParser parser, Novel novel, SafeCounter completeChapterNum) {
        boolean result = true;

        for (int i = 0; i < count; i++) {

            completeChapterNum.increase(1);

            Menu menu = menuList.get(start + i);

            // 下载章节内容页面
            String chapterPage = HttpUtil.download(menu.getUrl());
            if (null == chapterPage) {
                Log.e(TAG, "downloadAndSaveChapter: download '" + menu.getUrl() + "' fail");
                continue;
            }

            // 提取章节信息
            Chapter chapter = parser.parseChapter(chapterPage, null);
            chapter.setMenuId(menu.getId());
            if (chapter != null) {
                boolean ok = chapterDao.save(chapter);
                if (ok) {
                    Log.i(TAG, "downloadAndSaveChapter: save chapter(" + chapter.getTitle() +") success");

                    menu.setContentLength(chapter.getContent().length());
                    if (menuDao.update(menu)) {
                        Log.i(TAG, "downloadAndSaveChapter: update menu(" + menu.getTitle() + ") content length success");
                    } else {
                        Log.i(TAG, "downloadAndSaveChapter: update menu(" + menu.getTitle() + ") content length fail");
                    }
                } else {
                    result = false;
                    Log.e(TAG, "downloadAndSaveChapter: save chapter(" + chapter.getTitle() +") fail");
                }
            } else {
                Log.e(TAG, "downloadAndSaveChapter: parse chapter(" + chapter.getTitle() + ") fail");
                result = false;
            }

            if (downloadProgressChangeListener != null) {
                downloadProgressChangeListener.onDownloadProgressChange(novel, completeChapterNum.getCount(), menuList.size());
            }
        }
        return result;
    }
}
