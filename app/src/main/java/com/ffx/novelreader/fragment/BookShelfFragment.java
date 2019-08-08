package com.ffx.novelreader.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ffx.novelreader.R;
import com.ffx.novelreader.adapter.BookShelfAdapter;
import com.ffx.novelreader.application.AppContext;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.service.ServiceFactory;
import com.ffx.novelreader.inter.service.NovelService;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookShelfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookShelfFragment extends Fragment {
    private static final String TAG = "BookShelfFragment";
    private static BookShelfFragment instance;

    private RecyclerView bookShelfRecyclerView;
    private BookShelfAdapter bookShelfAdapter;
    private List<Novel> novelList;

    private TextView bookShelfStatus;

    private NovelService novelService;

    public BookShelfFragment() {
        // Required empty public constructor
    }

    public static BookShelfFragment getInstance() {
        return instance;
    }

    public static BookShelfFragment newInstance() {
        BookShelfFragment fragment = new BookShelfFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        instance = this;
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_bookshelf, container, false);

        novelService = ServiceFactory.getInstance().getNovelService();

        initBookShelfRecyclerView(view);
        refreshBookShelf();

        //bookShelfStatus = (TextView)view.findViewById(R.id.bookshelf_status);
        return view;
    }

    public void refreshBookShelf() {
        novelList = novelService.findAll();
        bookShelfAdapter.refresh(novelList);
    }


    private void initBookShelfRecyclerView(View view) {
        bookShelfRecyclerView = (RecyclerView) view.findViewById(R.id.bookshelf_recyler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(AppContext.applicationContext, 3);
        bookShelfRecyclerView.setLayoutManager(layoutManager);
        novelList = new ArrayList<>();
        bookShelfAdapter = new BookShelfAdapter(novelList);
        bookShelfRecyclerView.setAdapter(bookShelfAdapter);
        bookShelfRecyclerView.setSelected(true);
    }
}
