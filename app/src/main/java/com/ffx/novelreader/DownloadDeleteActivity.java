package com.ffx.novelreader;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ffx.novelreader.adapter.NovelDownloadDeleteAdapter;
import com.ffx.novelreader.entity.vo.CheckedEntityVo;
import com.ffx.novelreader.entity.vo.NovelDownloadProgressVo;
import com.ffx.novelreader.fragment.DownloadFragment;
import com.ffx.novelreader.util.FileUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DownloadDeleteActivity extends BaseDeleteActivity {
    private static final String TAG = "DownloadDeleteActivity";
    NovelDownloadDeleteAdapter adapter;

    public static void actionStart(int requestCode, Fragment fragment,
                                    Serializable arguemnt) {
         BaseDeleteActivity.actionStart(requestCode, fragment, arguemnt,
                 DownloadDeleteActivity.class);
     }

    private List<NovelDownloadProgressVo> getLocalDownloadHistory() {
        Object obj = FileUtil.readObjectFromFile(DownloadFragment.DOWNPOAD_PROGRESS_LIST);
        if (obj != null && (obj instanceof List)) {
            return (List<NovelDownloadProgressVo>)(obj);
        }
        return null;
    }

    @Override
    protected RecyclerView.Adapter getRecyclerViewAdapter() {
        if (null == adapter) {
            List<NovelDownloadProgressVo> vos = getLocalDownloadHistory();
            List<CheckedEntityVo<NovelDownloadProgressVo>> adapterData = new ArrayList<>();

            if (vos != null) {
                for (NovelDownloadProgressVo vo : vos) {
                    adapterData.add(new CheckedEntityVo<NovelDownloadProgressVo>(vo, false));
                }
            }

            this.adapter = new NovelDownloadDeleteAdapter(adapterData);
            this.adapter.setOnCheckedItemNumChangedListener(new NovelDownloadDeleteAdapter.OnCheckedItemNumChangedListener() {
                @Override
                public void onCheckedItemNumChanged(int checkedItemCount) {
                    String text = "删除所选 (" + checkedItemCount + ")";
                    deleteSelectButton.setText(text);
                }
            });
        }

        return this.adapter;
    }


    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_download_history_delete;
    }

    @Override
    protected void doSelectAll() {
        adapter.selectAll();
    }

    @Override
    protected void doUnselectAll() {
        adapter.unselectAll();
    }

    @Override
    protected void doDelete() {
        Log.d(TAG, "doDelete: ");
        List<CheckedEntityVo<NovelDownloadProgressVo>> checkedVos = adapter.getNovelDownloadProgressVos();
        for (int i = checkedVos.size() - 1; i >= 0; i--) {
            if (checkedVos.get(i).isChecked()) {
                checkedVos.remove(i);
            }
        }

        List<NovelDownloadProgressVo> vos = new ArrayList<>();
        for (CheckedEntityVo<NovelDownloadProgressVo> checkedVo : checkedVos) {
            vos.add(checkedVo.getEntity());
        }

        FileUtil.writeObjectToFile(DownloadFragment.DOWNPOAD_PROGRESS_LIST, vos);
        setResult(RESULT_OK);
        finish();
    }
}
