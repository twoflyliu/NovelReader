package com.ffx.novelreader.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ffx.novelreader.R;
import com.ffx.novelreader.application.AppContext;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.factory.service.ServiceFactory;
import com.ffx.novelreader.fragment.DownloadFragment;
import com.ffx.novelreader.inter.service.NovelService;
import com.ffx.novelreader.util.UrlStringUtil;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/6.
 */

public class NovelSearchResultAdapter extends RecyclerView.Adapter<NovelSearchResultAdapter.ViewHolder>{
    public static final String DOWNLOAD = "下载";
    public static final String UPDATE = "更新";

    private List<Novel> novelList;

    private NovelService novelService;

    public NovelSearchResultAdapter(List<Novel> novelList) {
        novelService = ServiceFactory.getInstance().getNovelService();
        this.novelList = novelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.downloadOrUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Novel novel = novelList.get(position);

                String btnText = holder.downloadOrUpdateBtn.getText().toString();
                if (btnText.equals(DOWNLOAD)) {
                    DownloadFragment.getInstance().download(novel);
                } else if (btnText.equals(UPDATE)) {
                    DownloadFragment.getInstance().update(novel);
                }

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Novel novel = novelList.get(position);
        Glide.with(AppContext.applicationContext).load(novel.getIconUrl()).into(holder.novelIcon);
        holder.novelAuthor.setText(novel.getAuthor());
        holder.novelName.setText(novel.getName());
        holder.novelLastUpdateTime.setText(novel.getLastUpdateTime());
        holder.novelNewestChapterName.setText(novel.getNewestChapterName());
        holder.novelSource.setText(UrlStringUtil.getHostName(novel.getMenuUrl()));
        holder.novelDesc.setText(novel.getDescription());

        if (novelService.find(novel.getName(), novel.getAuthor()).size() > 0) {
            holder.downloadOrUpdateBtn.setText(UPDATE);
        } else {
            holder.downloadOrUpdateBtn.setText(DOWNLOAD);
        }
    }

    @Override
    public int getItemCount() {
        return novelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView novelIcon;
        TextView novelName;
        TextView novelAuthor;
        TextView novelNewestChapterName;
        TextView novelLastUpdateTime;
        TextView novelSource;
        TextView novelDesc;
        Button downloadOrUpdateBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            novelIcon = (ImageView)itemView.findViewById(R.id.novel_ico);
            novelName = (TextView)itemView.findViewById(R.id.novel_name);
            novelAuthor = (TextView)itemView.findViewById(R.id.novel_author);
            novelNewestChapterName = (TextView)itemView.findViewById(R.id.novel_newest_chapter);
            novelLastUpdateTime = (TextView)itemView.findViewById(R.id.novel_last_update_time);
            novelSource = (TextView)itemView.findViewById(R.id.novel_source);
            novelDesc = (TextView)itemView.findViewById(R.id.novel_desc);
            downloadOrUpdateBtn = (Button)itemView.findViewById(R.id.download_or_update);
        }
    }

    public void refresh(List<Novel> novelList) {
        this.novelList = novelList;
        notifyDataSetChanged();
    }
}
