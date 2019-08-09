package com.ffx.novelreader;

import android.content.Intent;
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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

    public static final String NOVEL_KEY = "novel";
    public static final int LOAD_CHAPTER_COUNT = 20; //以当前页为基准，上下一半章节

    private TextView contentTextView;
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

    private int currentChapterindex = 0;

    boolean loadingNextPage = false;
    boolean loadingPrevPage = false;


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

        loadChapter(currentChapterindex);
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
                        >= (contentTextView.getMeasuredHeight()) && !loadingNextPage) {
                    loadNextChapter();
                }
            }
        });
    }

    /**
     * 加载下一个章
     */
    private void loadNextChapter() {
        if (currentChapterindex + 1 < menuList.size()) {
            loadingNextPage = true;
            loadChapter(currentChapterindex + 1);
        }
    }

    /**
     * 加载前一章
     */
    private void loadPrevChapter() {
        if (currentChapterindex > 0) {
            loadingPrevPage = true;
            loadChapter(currentChapterindex - 1);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void initService() {
        this.novelService = ServiceFactory.getInstance().getNovelService();
        this.menuService = ServiceFactory.getInstance().getMenuService();
        this.chapterService = ServiceFactory.getInstance().getChapterService();
    }

    public void loadChapter(int chapterIndex) {
        loadChapter(chapterIndex, false);
    }

    public void closeChapterMenu() {
        drawerLayout.closeDrawers();
    }

    public void loadChapter(final int chapterIndex, final boolean reload) {
        if (reload) {
            contentTextView.setText("");
            loadingPrevPage = false;
            loadingNextPage = false;
            showStatus();
        }
        currentChapterindex = chapterIndex;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (menuList == null) {
                    menuList = menuService.findByNovelId(novel.getId());
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder sb = new StringBuilder();
                            sb.append(chapter.getTitle())
                                    .append("\n").append(chapter.getContent());;

//                            if (reload) {
//                                if (contentTextView.getText().length() > 10) {
//                                    Log.d(TAG, "run: first 10 char content is " + contentTextView.getText().toString().substring(0, 10));
//                                } else {
//                                    Log.d(TAG, "run: first 10 char content is " + contentTextView.getText().toString());
//                                }
//                                Log.d(TAG, "run: first 10 char content(StringBuffer) is " + sb.toString().substring(0, 10));
//                            }

                            if (!loadingPrevPage) {
                                contentTextView.setText(contentTextView.getText() + sb.toString());
                            } else {
                                contentTextView.setText(sb.toString() + contentTextView.getText());
                            }

                            hideStatus();
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
        if (!loadingNextPage && !loadingPrevPage) {
            statusTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hideStatus() {
        statusTextView.setVisibility(View.GONE);

        if (loadingNextPage) {
            loadingNextPage = false;
        }

        if (loadingPrevPage) {
            swipeRefreshLayout.setRefreshing(false);
            loadingPrevPage = false;
        }
    }

    private void initContent() {
        novel = (Novel) getIntent().getSerializableExtra(NOVEL_KEY);
        contentTextView = (TextView)findViewById(R.id.content);
        contentTextView.setText(novel.getName());
    }
}
