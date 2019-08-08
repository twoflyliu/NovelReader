package com.ffx.novelreader.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ffx.novelreader.R;
import com.ffx.novelreader.application.AppContext;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.entity.vo.NovelDownloadProgressVo;
import com.ffx.novelreader.inter.service.NovelService;
import com.ffx.novelreader.util.UrlStringUtil;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/6.
 */

public class NovelDownloadProgressAdapter extends RecyclerView.Adapter<NovelDownloadProgressAdapter.ViewHolder>{
    private static final String TAG = "NovelDownloadProgressAd";

    private List<NovelDownloadProgressVo> novelDownloadProgressVos;
    private NovelService novelService;

    public NovelDownloadProgressAdapter(List<NovelDownloadProgressVo> novelDownloadProgressVos) {
        this.novelDownloadProgressVos = novelDownloadProgressVos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.download_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NovelDownloadProgressVo vo = novelDownloadProgressVos.get(position);
        Glide.with(AppContext.applicationContext).load(vo.getNovel().getIconUrl()).into(holder.novelIcon);

        holder.novelAuthor.setText(vo.getNovel().getAuthor());
        holder.novelName.setText(vo.getNovel().getName());
        holder.novelLastUpdateTime.setText(vo.getNovel().getLastUpdateTime());
        holder.novelNewestChapterName.setText(vo.getNovel().getNewestChapterName());
        holder.novelSource.setText(UrlStringUtil.getHostName(vo.getNovel().getMenuUrl()));

        int progress = vo.getProgress();
        holder.progressBar.setProgress(progress);
        //Log.d(TAG, "onBindViewHolder: progress=" + progress);
    }

    @Override
    public int getItemCount() {
        return novelDownloadProgressVos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView novelIcon;
        ProgressBar progressBar;
        TextView novelName;
        TextView novelAuthor;
        TextView novelNewestChapterName;
        TextView novelLastUpdateTime;
        TextView novelSource;

        public ViewHolder(View itemView) {
            super(itemView);
            novelIcon = (ImageView)itemView.findViewById(R.id.novel_ico);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progress);
            novelIcon = (ImageView)itemView.findViewById(R.id.novel_ico);
            novelName = (TextView)itemView.findViewById(R.id.novel_name);
            novelAuthor = (TextView)itemView.findViewById(R.id.novel_author);
            novelNewestChapterName = (TextView)itemView.findViewById(R.id.novel_newest_chapter);
            novelLastUpdateTime = (TextView)itemView.findViewById(R.id.novel_last_update_time);
            novelSource = (TextView)itemView.findViewById(R.id.novel_source);
        }
    }

    public void refresh(List<NovelDownloadProgressVo> novelList) {
        this.novelDownloadProgressVos = novelList;
        notifyDataSetChanged();
    }
}
