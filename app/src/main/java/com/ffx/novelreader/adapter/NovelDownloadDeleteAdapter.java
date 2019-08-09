package com.ffx.novelreader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ffx.novelreader.R;
import com.ffx.novelreader.application.AppContext;
import com.ffx.novelreader.entity.vo.CheckedEntityVo;
import com.ffx.novelreader.entity.vo.NovelDownloadProgressVo;
import com.ffx.novelreader.util.UrlStringUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by TwoFlyLiu on 2019/8/6.
 */

public class NovelDownloadDeleteAdapter extends RecyclerView.Adapter<NovelDownloadDeleteAdapter.ViewHolder>{
    private static final String TAG = "NovelDownloadProgressDe";

    private List<CheckedEntityVo<NovelDownloadProgressVo>> novelDownloadProgressVos;
    private Set<CheckedEntityVo<NovelDownloadProgressVo>> checkedSet;

    private NovelDownloadDeleteAdapter.OnCheckedItemNumChangedListener listener;

    public NovelDownloadDeleteAdapter(List<CheckedEntityVo<NovelDownloadProgressVo>> novelDownloadProgressVos) {
        this.novelDownloadProgressVos = novelDownloadProgressVos;
        checkedSet = new HashSet<>();
    }

    public List<CheckedEntityVo<NovelDownloadProgressVo>> getNovelDownloadProgressVos() {
        return novelDownloadProgressVos;
    }

    public void setOnCheckedItemNumChangedListener(NovelDownloadDeleteAdapter.OnCheckedItemNumChangedListener listener) {
        this.listener = listener;
    }

    private void notifyListener() {
        if (listener != null) {
            listener.onCheckedItemNumChanged(checkedSet.size());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.download_checked_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckedEntityVo<NovelDownloadProgressVo> checkedVo
                        = novelDownloadProgressVos.get(holder.getAdapterPosition());
                checkedVo.setChecked(isChecked);
                if (isChecked) {
                    checkedSet.add(checkedVo);
                } else {
                    checkedSet.remove(checkedVo);
                }
                notifyListener();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CheckedEntityVo<NovelDownloadProgressVo> checkedVo = novelDownloadProgressVos.get(position);
        NovelDownloadProgressVo vo = checkedVo.getEntity();
        Glide.with(AppContext.applicationContext).load(vo.getNovel().getIconUrl()).into(holder.novelIcon);

        holder.novelAuthor.setText(vo.getNovel().getAuthor());
        holder.novelName.setText(vo.getNovel().getName());
        holder.novelLastUpdateTime.setText(vo.getNovel().getLastUpdateTime());
        holder.novelNewestChapterName.setText(vo.getNovel().getNewestChapterName());
        holder.novelSource.setText(UrlStringUtil.getHostName(vo.getNovel().getMenuUrl()));

        int progress = vo.getProgress();
        holder.progressBar.setProgress(progress);
        holder.checkBox.setChecked(checkedVo.isChecked());
        //Log.d(TAG, "onBindViewHolder: progress=" + progress);
    }

    public void selectAll() {
        updateSelect(true);
    }

    public void unselectAll() {
        updateSelect(false);
    }

    public void updateSelect(boolean selected) {
        for (CheckedEntityVo<NovelDownloadProgressVo> vo : novelDownloadProgressVos) {
            vo.setChecked(selected);
        }
        notifyDataSetChanged();
    }

    public int getCheckedCount() {
        return checkedSet.size();
    }

    @Override
    public int getItemCount() {
        return novelDownloadProgressVos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemContainer;
        ImageView novelIcon;
        ProgressBar progressBar;
        TextView novelName;
        TextView novelAuthor;
        TextView novelNewestChapterName;
        TextView novelLastUpdateTime;
        TextView novelSource;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            itemContainer = (LinearLayout)itemView.findViewById(R.id.download_item_container);
            novelIcon = (ImageView)itemView.findViewById(R.id.novel_ico);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
            novelIcon = (ImageView)itemView.findViewById(R.id.novel_ico);
            novelName = (TextView)itemView.findViewById(R.id.novel_name);
            novelAuthor = (TextView)itemView.findViewById(R.id.novel_author);
            novelNewestChapterName = (TextView)itemView.findViewById(R.id.novel_newest_chapter);
            novelLastUpdateTime = (TextView)itemView.findViewById(R.id.novel_last_update_time);
            novelSource = (TextView)itemView.findViewById(R.id.novel_source);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkbox);
        }
    }

    public interface OnCheckedItemNumChangedListener {
        void onCheckedItemNumChanged(int checkedItemCount);
    }
}
