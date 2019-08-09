package com.ffx.novelreader.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ffx.novelreader.NovelReaderActivity;
import com.ffx.novelreader.R;
import com.ffx.novelreader.entity.po.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NovelMenuFragment extends Fragment {
    private ListView listView;

    private List<Menu> menuList;
    private ArrayAdapter<Menu> adapter;

    public NovelMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_novel_menu, container, false);

        initListView(view);

        return view;
    }

    private void initListView(View view) {
        menuList = new ArrayList<>();
        listView = (ListView)view.findViewById(R.id.menu_list);
        adapter = new ArrayAdapter<Menu>(getContext(), android.R.layout.simple_list_item_1,
                menuList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getActivity() instanceof NovelReaderActivity) {
                    NovelReaderActivity activity = (NovelReaderActivity)getActivity();
                    activity.closeChapterMenu();
                    activity.loadChapter(position, true);
                }
            }
        });
    }

    public void refresh(List<Menu> menuList) {
        this.menuList.clear();
        this.menuList.addAll(menuList);
        this.adapter.notifyDataSetChanged();
    }

    public int getMenuCount() {
        return this.menuList.size();
    }

}
