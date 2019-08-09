package com.ffx.novelreader.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ffx.novelreader.MainActivity;
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
    private static final int REQUEST_CODE = 1;

    private static BookShelfFragment instance;

    private RecyclerView bookShelfRecyclerView;
    private BookShelfAdapter bookShelfAdapter;
    private List<Novel> novelList;

    private TextView bookShelfStatus;
    private Button switchToSearchBtn;

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
        initSwitchToSearch(view);
        initBookShelfStatus(view);

        refreshBookShelf();
        return view;
    }

    private void initBookShelfStatus(View view) {
        bookShelfStatus = (TextView)view.findViewById(R.id.bookshelf_status);
    }

    private void initSwitchToSearch(View view) {
        switchToSearchBtn = (Button)view.findViewById(R.id.switch_to_search);
        switchToSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                if (activity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity)activity;
                    mainActivity.switchToSearch();
                }
            }
        });
    }

    public void refreshBookShelf() {
        novelList = novelService.findAll();

        if (novelList.size() == 0) {
            bookShelfStatus.setVisibility(View.VISIBLE);
        } else {
            bookShelfStatus.setVisibility(View.GONE);
        }

        bookShelfAdapter.refresh(novelList);
    }


    private void initBookShelfRecyclerView(View view) {
        bookShelfRecyclerView = (RecyclerView) view.findViewById(R.id.bookshelf_recyler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(AppContext.applicationContext, 3);
        bookShelfRecyclerView.setLayoutManager(layoutManager);
        novelList = new ArrayList<>();
        bookShelfAdapter = new BookShelfAdapter(novelList, REQUEST_CODE, this);
        bookShelfRecyclerView.setAdapter(bookShelfAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    refreshBookShelf();
                }
                break;
            default:
                break;
        }
    }
}
