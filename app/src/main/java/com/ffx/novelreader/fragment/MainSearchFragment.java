package com.ffx.novelreader.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ffx.novelreader.R;
import com.ffx.novelreader.adapter.NovelSearchResultAdapter;
import com.ffx.novelreader.application.AppContext;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.service.ServiceFactory;
import com.ffx.novelreader.inter.service.SearchService;

import java.util.ArrayList;
import java.util.List;

public class MainSearchFragment extends Fragment {
    private static final String TAG = "MainSearchFragment";

    private EditText novelNameEditText;
    private Button searchButton;

    private RecyclerView searchResultRecylerView;
    private NovelSearchResultAdapter searchResultAdapter;
    private List<Novel> searchedNovelList = new ArrayList<>();

    private TextView searchStatusTextView;

    private SearchService searchService;


    public MainSearchFragment() {
    }

    public static MainSearchFragment newInstance() {
        Log.d(TAG, "newInstance");
        MainSearchFragment fragment = new MainSearchFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchService = ServiceFactory.getInstance().getSearchService();
        novelNameEditText = (EditText)view.findViewById(R.id.novel_name);
        searchStatusTextView = (TextView)view.findViewById(R.id.search_status);

        initSearchResultRecyclerView(view);
        initSearchButton(view);

        updateSearchStatus(false);

        return view;
    }

    private void updateSearchStatus(boolean searching) {
        if (searching) {
            searchStatusTextView.setVisibility(View.VISIBLE);
        } else {
            searchStatusTextView.setVisibility(View.GONE);
        }
    }

    private void initSearchButton(View view) {
        searchButton = (Button)view.findViewById(R.id.search_btn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String novelName = novelNameEditText.getText().toString();
                if (TextUtils.isEmpty(novelName)) {
                    Toast.makeText(AppContext.applicationContext,
                            "小说名称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    updateSearchStatus(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final List<Novel> novelList = searchService.search(novelName);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                updateSearchStatus(false);
                                searchResultAdapter.refresh(novelList);
                                }
                            });
                        }
                    }).start();

                }
            }
        });
    }

    private void initSearchResultRecyclerView(View view) {
        // 初始化搜索结果RecyclerView
        Log.d(TAG, "onCreateView: recycler viewer item count = " + searchedNovelList.size());
        searchResultRecylerView = (RecyclerView)view.findViewById(R.id.search_result);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AppContext.applicationContext);
        searchResultRecylerView.setLayoutManager(layoutManager);
        searchResultAdapter = new NovelSearchResultAdapter(searchedNovelList);
        searchResultRecylerView.setAdapter(searchResultAdapter);
        searchResultRecylerView.setSelected(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: recycler viewer item count = " + searchedNovelList.size());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
