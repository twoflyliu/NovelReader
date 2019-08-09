package com.ffx.novelreader.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ffx.novelreader.R;
import com.ffx.novelreader.application.AppContext;
import com.ffx.novelreader.entity.po.Novel;
import com.ffx.novelreader.entity.vo.CheckedEntityVo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by TwoFlyLiu on 2019/8/6.
 */

public class BookShelfDeleteAdapter extends RecyclerView.Adapter<BookShelfDeleteAdapter.ViewHolder>{
    private static final String TAG = "BookShelfDeleteAdapter";

    private List<CheckedEntityVo<Novel>> checkedEntityVoList;
    private Set<CheckedEntityVo<Novel>> checkedSet = new HashSet<>();

    private OnCheckedItemNumChangedListener listener;

    public BookShelfDeleteAdapter(List<Novel> novelList) {
        checkedEntityVoList = new ArrayList<>();

        for (Novel novel : novelList) {
            CheckedEntityVo<Novel> checkedEntityVo = new CheckedEntityVo<>();
            checkedEntityVo.setEntity(novel);
            checkedEntityVo.setChecked(false);
            checkedEntityVoList.add(checkedEntityVo);
        }
    }

    public void setOnCheckedItemNumChangedListener(OnCheckedItemNumChangedListener listener) {
        this.listener = listener;
    }

    /**
     * 返回被选中的元素数目
     * @return
     */
    public int getCheckedCount() {
        return checkedSet.size();
    }

    public List<CheckedEntityVo<Novel>> getCheckedEntityVoList() {
        return checkedEntityVoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookshelf_checked_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckedEntityVo<Novel> checkedEntityVo = checkedEntityVoList.get(holder.getAdapterPosition());
                checkedEntityVo.setChecked(isChecked);
                if (isChecked) {
                    checkedSet.add(checkedEntityVo);
                } else {
                    checkedSet.remove(checkedEntityVo);
                }
                notifyListener();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CheckedEntityVo<Novel> novel = checkedEntityVoList.get(position);
        Glide.with(AppContext.applicationContext).load(novel.getEntity().getIconUrl()).into(holder.novelIcon);
        holder.novelName.setText(novel.getEntity().getName());
        holder.checkBox.setChecked(novel.isChecked());
        Log.d(TAG, "onBindViewHolder: show " + position + " item");
    }

    @Override
    public int getItemCount() {
        return checkedEntityVoList.size();
    }

    /**
     * 选择所有项
     */
    public void selectAll() {
        updateAllSelect(true);
    }

    private void notifyListener() {
        if (listener != null) {
            listener.onCheckedItemNumChanged(checkedSet.size());
        }
    }

    /**
     * 取消所有选中
     */
    public void unselectAll() {
        updateAllSelect(false);
    }

    private void updateAllSelect(boolean selected) {
        for (CheckedEntityVo<Novel> checkedEntityVo : checkedEntityVoList) {
            checkedEntityVo.setChecked(selected);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView novelIcon;
        TextView novelName;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            novelIcon = (ImageView)itemView.findViewById(R.id.novel_ico);
            novelName = (TextView)itemView.findViewById(R.id.novel_name);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        checkedSet.clear();
    }

    public interface OnCheckedItemNumChangedListener {
        void onCheckedItemNumChanged(int checkedItemCount);
    }
}
