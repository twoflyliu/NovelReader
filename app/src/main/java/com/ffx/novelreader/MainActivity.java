package com.ffx.novelreader;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ffx.novelreader.fragment.MainFragmentAdapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTabPageViewer();
    }

    private void initTabPageViewer() {
        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        viewPager = (ViewPager)findViewById(R.id.viewpager);

        viewPager.setAdapter(new MainFragmentAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2); //防止销毁页面
    }

    public void switchToSearch() {
        if (viewPager != null) {
            viewPager.setCurrentItem(1, true);
        }
    }

    public void switchToDownload() {
        if (viewPager != null) {
            viewPager.setCurrentItem(2, true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requstCode=" + requestCode);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ( (System.currentTimeMillis() - exitTime) >= 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
