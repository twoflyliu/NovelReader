package com.ffx.novelreader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.Serializable;


/**
 * Created by TwoFlyLiu on 2019/8/8.
 */

public abstract class BaseDeleteActivity  extends AppCompatActivity {
    public static final String EXTRA_ARGUMENT = "arguemnt";

    private static final String TAG = "BaseDeleteActivity";
    public static final String SELECT_ALL_TEXT = "全选";
    public static final String UNSELECT_ALL_TEXT = "取消全选";

    protected RecyclerView recyclerView;
    protected TextView selectAllTextView;
    protected Button deleteSelectButton;
    protected TextView backTextView;

    protected abstract RecyclerView.Adapter getRecyclerViewAdapter();
    protected abstract RecyclerView.LayoutManager getLayoutManager();

    protected abstract int getContentViewResId();

    protected abstract void doSelectAll();
    protected abstract void doUnselectAll();

    protected abstract void doDelete();

    /**
     * 启动此活动
     * @param fragment
     */
    public static void actionStart(int requestCode, Fragment fragment,
                                   Serializable arguemnt, Class clazz) {
        Vibrator vibrator = (Vibrator)fragment.getActivity().getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(250);
        } else {
            Log.d(TAG, "actionStart: no vibrator");
        }

        Intent intent = new Intent(fragment.getContext(), clazz);
        intent.putExtra(EXTRA_ARGUMENT, arguemnt);
        fragment.startActivityForResult(intent, requestCode);
    }

    public Serializable getActivityArgument() {
        return getIntent().getSerializableExtra(EXTRA_ARGUMENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewResId());

        initRecyclerView();
        initSelectAll();
        initDeleteSelect();
        initBack();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView)findViewById(R.id.recyler_view);
        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.setAdapter(getRecyclerViewAdapter());
    }

    private void initSelectAll() {
        selectAllTextView = (TextView)findViewById(R.id.select_all);
        selectAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SELECT_ALL_TEXT.equals(selectAllTextView.getText())) {
                    doSelectAll();
                    selectAllTextView.setText(UNSELECT_ALL_TEXT);
                } else {
                    doUnselectAll();
                    selectAllTextView.setText(SELECT_ALL_TEXT);
                }

            }
        });
    }

    private void initDeleteSelect() {
        deleteSelectButton = (Button)findViewById(R.id.delete_select);
        deleteSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDelete();
            }
        });
    }

    private void initBack() {
        backTextView = (TextView)findViewById(R.id.back);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
