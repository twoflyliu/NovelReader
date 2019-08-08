package com.ffx.novelreader.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TwoFlyLiu on 2019/8/6.
 */

public class RadioTitleLayout extends LinearLayout {
    private static final String TAG = "RadioTitleLayout";
    private static final int DEFAULT_TAB_SELECTED_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_TAB_UNSELECTED_COLOR = 0xFFBBBBBB;

    private static int tabSelectedTextColor;
    private static int tabUnselectedTextColor;
    private int currentSelectIndex;

    private List<TextView> textViews;

    OnTabCurrentItemChangedListener listener;

    public RadioTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        tabSelectedTextColor = DEFAULT_TAB_SELECTED_COLOR;
        tabUnselectedTextColor = DEFAULT_TAB_UNSELECTED_COLOR;
        currentSelectIndex = -1;

        textViews = new ArrayList<>();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow: " + getChildCount());
        bingClickListeners();
    }

    private void bingClickListeners() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof TextView) {
                bindTextViewClickListener((TextView)child);
            }
        }
        unselectAll();
    }

    private void bindTextViewClickListener(TextView child) {
        textViews.add(child);
        child.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem((TextView)v);
            }
        });
    }

    public void selectItem(int index) {
        if (index < 0 && index >= textViews.size()) {
            throw new IndexOutOfBoundsException("index = " + index + ", total = " + textViews.size());
        }
        selectItem(textViews.get(index));
    }

    public void selectItem(TextView item) {
        int oldSelectedIndex = currentSelectIndex;
        int newSelectedIndex = textViews.indexOf(item);
        if (newSelectedIndex != -1 && newSelectedIndex != oldSelectedIndex) {
            for (int i = 0; i < textViews.size(); i++) {
                TextView view = textViews.get(i);
                if (view == item) {
                    view.setTextColor(tabSelectedTextColor);
                    currentSelectIndex = i;
                } else {
                    view.setTextColor(tabUnselectedTextColor);
                }
            }

            if (listener != null) {
                listener.onTabCurrentItemChanged(newSelectedIndex, oldSelectedIndex);
            }
        }
    }

    private void unselectAll() {
        for (TextView view : textViews) {
            view.setTextColor(tabUnselectedTextColor);
        }
        currentSelectIndex = -1;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow: " + getChildCount());
        currentSelectIndex = -1;
        textViews.clear();
    }

    public int getCurrentSelectIndex() {
        return currentSelectIndex;
    }

    public TextView getCurrentItem() {
        if (-1 != currentSelectIndex) {
            return textViews.get(currentSelectIndex);
        }
        return null;
    }

    public interface OnTabCurrentItemChangedListener {
        void onTabCurrentItemChanged(int currentSelectedIndex, int oldSelectedIndex);
    }

    public  void setOnTabCurrentItemChangedListener(OnTabCurrentItemChangedListener listener) {
        this.listener = listener;
    }

}
