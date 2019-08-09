package com.ffx.novelreader.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ffx.novelreader.BookShelfDeleteActivity;
import com.ffx.novelreader.NovelReaderActivity;
import com.ffx.novelreader.R;
import com.ffx.novelreader.application.AppContext;
import com.ffx.novelreader.entity.po.Novel;

import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/6.
 */

public class BookShelfAdapter extends RecyclerView.Adapter<BookShelfAdapter.ViewHolder>{
    private static final String TAG = "BookShelfAdapter";

    private List<Novel> novelList;
    private int requestCode;
    private Fragment fragment;

    public BookShelfAdapter(List<Novel> novelList, int requestCode, Fragment fragment) {
        this.novelList = novelList;
        this.requestCode = requestCode;
        this.fragment = fragment;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookshelf_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.novelIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Novel novel = novelList.get(holder.getAdapterPosition());
                Log.d(TAG, "onLongClick: name=" + novel.getName());
                BookShelfDeleteActivity.actionStart(requestCode, fragment, novel);
                return true;
            }
        });

        holder.novelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: name=" + novelList.get(holder.getAdapterPosition()).getName());
                Novel novel = novelList.get(holder.getAdapterPosition());
                NovelReaderActivity.actionStart(fragment, novel);
            }
        });

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
