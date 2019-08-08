package com.ffx.novelreader.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class BookShelfAdapter extends RecyclerView.Adapter<BookShelfAdapter.ViewHolder>{
    private static final String TAG = "BookShelfAdapter";

    private List<Novel> novelList;

    public BookShelfAdapter(List<Novel> novelList) {
        this.novelList = novelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookshelf_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Novel novel = novelList.get(position);
        Glide.with(AppContext.applicationContext).load(novel.getIconUrl()).into(holder.novelIcon);
        holder.novelName.setText(novel.getName());
        Log.d(TAG, "onBindViewHolder: show " + position + " item");
    }

    @Override
    public int getItemCount() {
        return novelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView novelIcon;
        TextView novelName;

        public ViewHolder(View itemView) {
            super(itemView);
            novelIcon = (ImageView)itemView.findViewById(R.id.novel_ico);
            novelName = (TextView)itemView.findViewById(R.id.novel_name);
        }
    }

    public void refresh(List<Novel> novelList) {
        this.novelList = novelList;
        notifyDataSetChanged();
    }
}
