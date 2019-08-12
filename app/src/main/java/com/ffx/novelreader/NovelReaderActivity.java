package com.ffx.novelreader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ffx.novelreader.entity.po.Chapter;
import com.ffx.novelreader.entity.po.Menu;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.service.ServiceFactory;
import com.ffx.novelreader.fragment.NovelMenuFragment;
import com.ffx.novelreader.inter.service.ChapterService;
import com.ffx.novelreader.inter.service.MenuService;
import com.ffx.novelreader.inter.service.NovelService;

import java.util.List;

public class NovelReaderActivity extends AppCompatActivity {
    private static final String TAG = "NovelReaderActivity";
    private static final int AHEAD_OF_LAOD_PAGE_COUNT = 10;

    private static final String CHAPTER_KEY = "chapter";
    private static final String SCROLLY_KEY = "scrollY";

    private static final String CMD_LOAD_NEXT_CHAPTER = "LoadNextChapter";
    private static final String CMD_LOAD_PREV_CHAPTER = "LoadPrevChapter";
    private static final String CMD_LOAD_SPEC_CHAPTER = "LoadSpecChapter";
    private static final String CMD_NONE = "";
    private static String cmd = CMD_NONE;

    public static final String NOVEL_KEY = "novel";

    private TextView contentTextView;
    //private CBAlignTextView contentTextView;

    private TextView statusTextView;
    private ScrollView scrollView;
    private NovelMenuFragment novelMenuFragment;
    private DrawerLayout drawerLayout;

    private Novel novel;
    private List<Menu> menuList;

    private NovelService novelService;
    private MenuService menuService;
    private ChapterService chapterService;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int baselineChapterindex = 0;
    private int nextChapterCount = 0;
    private int prevChapterCount = 0;

    private int scrollY = 0;
    private int needScrollTo = -1;
    private ChapterScrollYManager scrollYManager = new ChapterScrollYManager();

    private int oldReadChapterIndex = -1;

    public static void actionStart(Fragment fragment, Novel novel) {
        Intent intent = new Intent(fragment.getActivity(), NovelReaderActivity.class);
        intent.putExtra(NOVEL_KEY, novel);
        fragment.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSystemStatusBar();

        setContentView(R.layout.activity_novel_reader);

        initService();

        initStatus();
        initContent();
        initScrollView();
        initSwipeRefresh();
        initNovelMenuFragment();
        initDrawerLayout();

        //loadChapter(baselineChapterindex);
        loadHistoryRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveHistroyRecord();
    }

    private String historyKeyPrefix(Novel novel) {
        return novel.getName() + "_" + novel.getAuthor() + "_";
    }

    private void saveHistroyRecord() {
        int curChapterIndex = scrollYManager.getChapterIndex(this.scrollY);
        int offsetY = this.scrollY - scrollYManager.getRange(curChapterIndex).start;

        Log.i(TAG, "onDestroy: curChapterIndex=" + curChapterIndex + ", offset=" + offsetY
                + " (" + menuList.get(curChapterIndex).getTitle() + ")");

        String key_prefix = historyKeyPrefix(novel);
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();

        editor.putInt(key_prefix + CHAPTER_KEY, curChapterIndex);
        editor.putInt(key_prefix + SCROLLY_KEY, offsetY);

        editor.apply();
    }

    private void loadHistoryRecord() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String key_prefix = historyKeyPrefix(novel);

        int curChapterIndex = prefs.getInt(key_prefix + CHAPTER_KEY, 0);
        int offsetY = prefs.getInt(key_prefix + SCROLLY_KEY, 0);
        Log.i(TAG, "loadHistoryRecord: curChapterIndex=" + curChapterIndex + ", offsetY=" + offsetY);

        baselineChapterindex = curChapterIndex;
        needScrollTo = offsetY;
        loadChapter(baselineChapterindex);
    }

    private void initDrawerLayout() {
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
    }

    private void initNovelMenuFragment() {
        novelMenuFragment = new NovelMenuFragment(); //动态加载
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.chapter_menu, novelMenuFragment);
        transaction.commit();
    }

    private void initSystemStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initSwipeRefresh() {
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPrevChapter();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initScrollView() {
        scrollView = (ScrollView)findViewById(R.id.scrollview);
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY + (AHEAD_OF_LAOD_PAGE_COUNT * scrollView.getMeasuredHeight())
                        >= (contentTextView.getMeasuredHeight()) && (CMD_NONE == cmd)) {
                    loadNextChapter();
                }

                if (CMD_NONE == cmd) {
                    NovelReaderActivity.this.scrollY = scrollY;
                    //Log.d(TAG, "onScrollChange: NovelReaderActivity.this.scrollY = " + scrollY);
                }

                int curReadChapterIndex = scrollYManager.getChapterIndex(scrollY);
                if (oldReadChapterIndex != curReadChapterIndex) {
                    Log.i(TAG, "onScrollChange: curReadChapterIndex=" + curReadChapterIndex);
                    oldReadChapterIndex = curReadChapterIndex;

                    onCurrentChapterChanged(curReadChapterIndex);
                }
            }
        });
    }

    private void onCurrentChapterChanged(int curReadChapterIndex) {
        novelMenuFragment.selectItem(curReadChapterIndex);
    }

    /**
     * 加载下一个章
     */
    private void loadNextChapter() {
        if (baselineChapterindex + nextChapterCount + 1 < menuList.size()) {
            nextChapterCount += 1;
            cmd = CMD_LOAD_NEXT_CHAPTER;
            doLoadChapter(baselineChapterindex + nextChapterCount);
        }
    }

    /**
     * 加载前一章
     */
    private void loadPrevChapter() {
        if (baselineChapterindex - prevChapterCount - 1 >= 0) {
            cmd = CMD_LOAD_PREV_CHAPTER;
            prevChapterCount += 1;
            doLoadChapter(baselineChapterindex - prevChapterCount);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void loadChapter(int chapterIndex) {
        cmd = CMD_LOAD_SPEC_CHAPTER;
        doLoadChapter(chapterIndex);
    }

    private void initService() {
        this.novelService = ServiceFactory.getInstance().getNovelService();
        this.menuService = ServiceFactory.getInstance().getMenuService();
        this.chapterService = ServiceFactory.getInstance().getChapterService();
    }

    public void closeChapterMenu() {
        drawerLayout.closeDrawers();
    }

    public void doLoadChapter(final int chapterIndex) {
        if (CMD_LOAD_SPEC_CHAPTER == cmd) {
            contentTextView.setText("");

            baselineChapterindex = chapterIndex;
            nextChapterCount = 0;
            prevChapterCount = 0;

            showStatus();
        }
        Log.d(TAG, "loadChapter: load chapter" + (chapterIndex+1));

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (menuList == null) {
                    menuList = menuService.findByNovelId(novel.getId());
                    Log.i(TAG, "run: find [" + novel.getName() + "] all menus" );
                }

                if (novelMenuFragment != null && novelMenuFragment.getMenuCount() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            novelMenuFragment.refresh(menuList);
                        }
                    });
                }

                if (menuList.size() > 0) {
                    final Chapter chapter = chapterService.findByMenuId(menuList.get(chapterIndex).getId());
                    Log.i(TAG, "run: found chapter [" + chapter.getTitle() + "]");

                    StringBuilder sb = new StringBuilder();

                    // 检测chapter中是否存在标题
                    String tmpContent = null;
                    String title = chapter.getTitle();

                    int index = chapter.getContent().indexOf('\n');
                    String firstParagraph = "";
                    if (index != -1) {
                        firstParagraph = chapter.getContent().substring(0, index);
                    }

                    if (firstParagraph.contains(title)
                            || firstParagraph.contains(title.replaceAll(" ", ""))) {
                        tmpContent = chapter.getContent();
                    } else {
                        tmpContent = sb.append(chapter.getTitle())
                                .append("\n\n").append(chapter.getContent()).toString();
                    }

                    Log.i(TAG, "run: update chapter [" + chapter.getTitle() + "] content");

                    final String newContent = tmpContent.replaceFirst(";$", "").replaceAll("([,\\.;:'\"`` ])", "$1 ");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!CMD_LOAD_PREV_CHAPTER.equals(cmd)) {
                                contentTextView.setText(contentTextView.getText() + newContent);
                            } else {
                                contentTextView.setText(newContent + contentTextView.getText());
                            }

                            if (CMD_LOAD_PREV_CHAPTER.equals(cmd)) {
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            final String finalCmd = cmd;
                            getWindow().getDecorView().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // 更新章节页面信息(等待一段时间，下面信息才能获取正确)
                                    Log.i(TAG, "run: contentTextView.getMeasuredHeight=" + contentTextView.getMeasuredHeight());

                                    if (CMD_LOAD_PREV_CHAPTER.equals(finalCmd)) {
                                        scrollYManager.update(chapterIndex, contentTextView.getMeasuredHeight(), false);
                                    } else if (CMD_LOAD_NEXT_CHAPTER.equals(finalCmd)) {
                                        scrollYManager.update(chapterIndex, contentTextView.getMeasuredHeight(), false);
                                    } else if (CMD_LOAD_SPEC_CHAPTER.equals(finalCmd)) {
                                        scrollYManager.update(chapterIndex, contentTextView.getMeasuredHeight(), true);
                                    }

                                    Log.i(TAG, "run: currentChapterIndex=" + scrollYManager.getChapterIndex(scrollY)
                                        + ", scrollYManager size=" + scrollYManager.size());

                                    if (-1 != needScrollTo) {
                                        scrollView.scrollTo(0, needScrollTo);
                                        novelMenuFragment.selectItem(chapterIndex);
                                        hideStatus();
                                        needScrollTo = -1;
                                    }
                                }
                            }, 300);

                            if (-1 == needScrollTo) {
                                hideStatus();
                            }
                        }
                    });
                }
            }
        }).start();
    }


    private void initStatus() {
        statusTextView = (TextView)findViewById(R.id.status);
    }

    private void showStatus() {
        Log.i(TAG, "showStatus: cmd=" + cmd);
        if (CMD_LOAD_SPEC_CHAPTER == cmd) {
            statusTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hideStatus() {
        Log.i(TAG, "hideStatus: cmd=" + cmd);
        statusTextView.setVisibility(View.GONE);
        cmd = CMD_NONE;
    }

    private void initContent() {
        novel = (Novel) getIntent().getSerializableExtra(NOVEL_KEY);
        //contentTextView = (CBAlignTextView) findViewById(R.id.content);
        contentTextView = (TextView) findViewById(R.id.content);
        contentTextView.setText(novel.getName() + "\n");
    }

    /**
     * 此类注意主要实现通过像素来获取当前章节索引
     */
    class ChapterScrollYManager {
        //private static final String TAG = "ChapterScrollYManager";

        static final int MAX_CHAPTER_COUNT = 5000;
        Range[] ranges = new Range[MAX_CHAPTER_COUNT];

        int lowerIndex = -1;
        int upperIndex = -1;

        {
            for (int i = 0; i < ranges.length; i++) {
                ranges[i] = new Range();
            }
        }

        private void reset() {
            for (int i = 0; i < ranges.length; i++) {
                ranges[i].reset();
            }
            lowerIndex = -1;
            upperIndex = -1;
        }

        public int size() {
            return upperIndex - lowerIndex + 1;
        }

        public void update(int chapterIndex, int scrollY, boolean reset) {
            if (-1 == lowerIndex || -1 == upperIndex || reset) {
                set(chapterIndex, scrollY);
            } else if (chapterIndex == (lowerIndex - 1)) {
                updateLower(chapterIndex, scrollY);
            } else if (chapterIndex == (upperIndex + 1)) {
                updateUpper(chapterIndex, scrollY);
            }
        }

        private void updateUpper(int chapterIndex, int scrollY) {
            upperIndex = chapterIndex;
            ranges[upperIndex].start = ranges[upperIndex-1].end;
            ranges[upperIndex].end = scrollY;
        }

        private void updateLower(int chapterIndex, int scrollY) {
            lowerIndex = chapterIndex;

            ranges[lowerIndex].start = 0;
            ranges[lowerIndex].end = scrollY - ranges[upperIndex].end;

            int offset = ranges[lowerIndex].end;

            for (int i= lowerIndex + 1; i <= upperIndex; i++) {
                ranges[i].start += offset;
                ranges[i].end += offset;
            }
        }

        private void set(int chapterIndex, int scrollY) {
            reset();

            lowerIndex = chapterIndex;
            upperIndex = chapterIndex;
            ranges[chapterIndex].start = 0;
            ranges[chapterIndex].end = scrollY;
        }

        public int getChapterIndex(int scrollY) {
            //Log.i(TAG, "getChapterIndex: lowerIndex=" + lowerIndex + ", upperIndex=" + upperIndex);
            if (-1 == lowerIndex || -1 == upperIndex) {
                return -1;
            }

            for (int i = lowerIndex; i <= upperIndex; i++) {
                if (ranges[i].isIn(scrollY)) {
                    return i;
                }
            }
            return -1;
        }

        public Range getRange(int chapterIndex) {
            if (chapterIndex >= 0 && chapterIndex < menuList.size()) {
                return ranges[chapterIndex];
            }
            return null;
        }

        class Range {
            int start = 0;
            int end = 0;

            public Range() {}

            public Range(int start, int end) {
                this.start = start;
                this.end = end;
            }

            boolean isIn(int data) {
                //Log.i(TAG, "isIn: scrollY=" + data + ", (start=" + start + ", end=" + end + ")");
                return data >= start && data < end;
            }

            void reset() {
                start = -1;
                end = -1;
            }

            @Override
            public String toString() {
                return "Range(" + start + ", " + end + ")";
            }
        }
    }
}
