package com.ffx.novelreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ffx.novelreader.adapter.BookShelfDeleteAdapter;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.entity.vo.CheckedEntityVo;
import com.ffx.novelreader.factory.service.ServiceFactory;
import com.ffx.novelreader.inter.service.NovelService;

import java.io.Serializable;
import java.util.List;

public class BookShelfDeleteActivity extends BaseDeleteActivity {
    private static final String TAG = "BookShelfDeleteActivity";
    private BookShelfDeleteAdapter bookShelfAdapter;

    private NovelService novelService;

    public static void actionStart(int requestCode, Fragment fragment,
                                   Serializable arguemnt) {
        BaseDeleteActivity.actionStart(requestCode, fragment, arguemnt,
                BookShelfDeleteActivity.class);
    }

    @Override
    protected RecyclerView.Adapter getRecyclerViewAdapter() {
        List<Novel> novelList = ServiceFactory.getInstance().getNovelService().findAll();

        bookShelfAdapter = new BookShelfDeleteAdapter(novelList);
        // 监听checked数目变化
        bookShelfAdapter.setOnCheckedItemNumChangedListener(new BookShelfDeleteAdapter.OnCheckedItemNumChangedListener() {
            @Override
            public void onCheckedItemNumChanged(int checkedItemCount) {
                String text = "删除所选 (" + checkedItemCount + ")";
                deleteSelectButton.setText(text);
            }
        });

        return bookShelfAdapter;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(this, 3);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_bookshelf_delete;
    }

    @Override
    protected void doSelectAll() {
        bookShelfAdapter.selectAll();
    }

    @Override
    protected void doUnselectAll() {
        bookShelfAdapter.unselectAll();
    }

    @Override
    protected void doDelete() {
        List<CheckedEntityVo<Novel>> checkedEntityVoList
                = bookShelfAdapter.getCheckedEntityVoList();
        for (CheckedEntityVo<Novel> checkedEntityVo : checkedEntityVoList) {
            if (checkedEntityVo.isChecked()) {
                novelService.deepDelete(checkedEntityVo.getEntity());
            }
        }
        //Toast.makeText(BookShelfDeleteActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        novelService = ServiceFactory.getInstance().getNovelService();
    }
}
