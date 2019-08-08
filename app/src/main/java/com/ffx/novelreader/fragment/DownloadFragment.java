package com.ffx.novelreader.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ffx.novelreader.R;
import com.ffx.novelreader.adapter.NovelDownloadProgressAdapter;
import com.ffx.novelreader.application.AppContext;
import com.ffx.novelreader.custom.RadioTitleLayout;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.entity.vo.NovelDownloadProgressVo;
import com.ffx.novelreader.factory.service.ServiceFactory;
import com.ffx.novelreader.inter.service.DownloadService;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DownloadFragment extends Fragment {
    private static final String TAG = "DownloadFragment";

    private static final String ALL_DOWNLOAD = "所有下载";
    private static final String DOWNLOAD_DONE = "已经完成";
    private static final String DOWNLOADING = "正在下载";


    private static DownloadFragment instance = null;
    private Handler uiHandler = new Handler();

    private DownloadService downloadService;

    private TextView downloadStatusTextView;
    private RadioTitleLayout radioTitleLayout;
    private RecyclerView downloadProgressRecyclerView;

    private String pageType;

    private List<NovelDownloadProgressVo> allNovelDownloadProgressVoList;
    private List<NovelDownloadProgressVo> novelDownloadProgressVoList;
    private NovelDownloadProgressAdapter novelDownloadProgressAdapter;

    public DownloadFragment() {
        instance = this;
    }

    public static DownloadFragment newInstance() {
        DownloadFragment fragment = new DownloadFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);

        downloadService = ServiceFactory.getInstance().getDownloadService();
        downloadService.setOnDownloadProgressChangeListener(new DownloadService.OnDownloadProgressChangeListener() {
            @Override
            public void onDownloadProgressChange(Novel novel, int currentValue, int totalValue) {
                updateProgress(novel, currentValue, totalValue);
            }

            @Override
            public void onDownloadDone(Novel novel) {
                updateProgress(novel, 1, 1);
                refreshBookShelf();
            }
        });

        novelDownloadProgressVoList = new ArrayList<>();
        downloadStatusTextView = (TextView)view.findViewById(R.id.download_status);
        radioTitleLayout = (RadioTitleLayout)view.findViewById(R.id.radio_title_layout);
        radioTitleLayout.setOnTabCurrentItemChangedListener(new RadioTitleLayout.OnTabCurrentItemChangedListener() {
            @Override
            public void onTabCurrentItemChanged(int currentSelectedIndex, int oldSelectedIndex) {
                Log.d(TAG, "onTabCurrentItemChanged: current=" + currentSelectedIndex + ", old=" + oldSelectedIndex);
                updatePage(radioTitleLayout.getCurrentItem().getText().toString(), false);
            }
        });

        pageType = ALL_DOWNLOAD;
        allNovelDownloadProgressVoList = new ArrayList<>();
        downloadProgressRecyclerView = (RecyclerView)view.findViewById(R.id.download_progress_recyler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AppContext.applicationContext);
        downloadProgressRecyclerView.setLayoutManager(layoutManager);
        novelDownloadProgressAdapter = new NovelDownloadProgressAdapter(novelDownloadProgressVoList);
        downloadProgressRecyclerView.setAdapter(novelDownloadProgressAdapter);
        downloadProgressRecyclerView.setSelected(true);

        return view;
    }

    public static DownloadFragment getInstance() {
        return instance;
    }

    public void download(Novel novel) {
        Log.d(TAG, "downloadAndSave: Starting downloadAndSave novel[" + novel.getName() + ", " + novel.getAuthor() + "]");

        downloadStatusTextView.setVisibility(View.GONE);

        NovelDownloadProgressVo vo = find(novel, allNovelDownloadProgressVoList);

        if (null == vo) {
            allNovelDownloadProgressVoList.add(new NovelDownloadProgressVo(novel, 0));
        } else {
            vo.setProgress(0);
        }
        updatePage(this.pageType, true);

        downloadService.downloadAndSave(novel);
    }

    public void update(Novel novel) {
        Log.d(TAG, "downloadAndSave: Starting downloadAndSave novel[" + novel.getName() + ", " + novel.getAuthor() + "]");

        downloadStatusTextView.setVisibility(View.GONE);

        NovelDownloadProgressVo vo = find(novel, allNovelDownloadProgressVoList);

        if (null == vo) {
            allNovelDownloadProgressVoList.add(new NovelDownloadProgressVo(novel, 0));
        } else {
            vo.setProgress(0);
        }
        updatePage(this.pageType, true);

        downloadService.updateAndSave(novel);
    }

    private NovelDownloadProgressVo find(Novel novel, List<NovelDownloadProgressVo> novelList) {
        for (NovelDownloadProgressVo vo : novelList) {
            if (vo.getNovel() == novel) {
                return vo;
            }
        }
        return null;
    }


    public synchronized void updateProgress(final Novel novel, final int currentValue, final int totalValue) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
//                DecimalFormat format = new DecimalFormat("0.00");
//                downloadStatusTextView.setText(format.format(currentValue * 100.0 / totalValue) + "%");
                int progress = (int)(currentValue * 100.0 / totalValue);
                NovelDownloadProgressVo vo = find(novel, allNovelDownloadProgressVoList);
                vo.setProgress(progress);
                novelDownloadProgressAdapter.notifyDataSetChanged();
            }
        });
    }

    public synchronized void refreshBookShelf() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BookShelfFragment.getInstance().refreshBookShelf();
            }
        });
    }


    public void updatePage(String type, boolean force) {
        if ((this.pageType != type) || force) {
            this.pageType = type;

            if (this.pageType.equals(ALL_DOWNLOAD)) {
                novelDownloadProgressVoList = allNovelDownloadProgressVoList;
            } else if (this.pageType.equals(DOWNLOAD_DONE)) {
                novelDownloadProgressVoList = new ArrayList<>();
                for (NovelDownloadProgressVo vo : allNovelDownloadProgressVoList) {
                    if (100 == vo.getProgress()) {
                        novelDownloadProgressVoList.add(vo);
                    }
                }
            } else if (this.pageType.equals(DOWNLOADING)) {
                novelDownloadProgressVoList = new ArrayList<>();
                for (NovelDownloadProgressVo vo : allNovelDownloadProgressVoList) {
                    if (100 != vo.getProgress()) {
                        novelDownloadProgressVoList.add(vo);
                    }
                }
            }
        }
        novelDownloadProgressAdapter.refresh(novelDownloadProgressVoList);
    }
}
