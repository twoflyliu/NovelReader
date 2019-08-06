package com.ffx.novelreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ffx.novelreader.adapter.NovelSearchResultAdapter;
import com.ffx.novelreader.application.AppContext;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.service.ServiceFactory;
import com.ffx.novelreader.inter.service.SearchService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private EditText novelNameEditText;
    private Button searchButton;

    private RecyclerView searchResultRecylerView;
    private NovelSearchResultAdapter searchResultAdapter;
    private List<Novel> searchedNovelList;

    private SearchService searchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchService = ServiceFactory.getInstance().getSearchService();

        novelNameEditText = (EditText)findViewById(R.id.novel_name);

        // 初始化搜索结果RecyclerView
        searchedNovelList = new ArrayList<>();
        searchResultRecylerView = (RecyclerView)findViewById(R.id.search_result);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        searchResultRecylerView.setLayoutManager(layoutManager);
        searchResultAdapter = new NovelSearchResultAdapter(searchedNovelList);
        searchResultRecylerView.setAdapter(searchResultAdapter);

        searchButton = (Button)findViewById(R.id.search_btn);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String novelName = novelNameEditText.getText().toString();
                if (TextUtils.isEmpty(novelName)) {
                    Toast.makeText(AppContext.applicationContext,
                            "小说名称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final List<Novel> novelList = searchService.search(novelName);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    for (int i = 0; i < 100; i++) {
                                        novelList.add(novelList.get(0));
                                    }

                                    searchResultAdapter.refresh(novelList);
                                    searchResultRecylerView.setSelected(true);
                                }
                            });
                        }
                    }).start();

                }
            }
        });
    }
}
