package com.ffx.novelreader.treader.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/8 0008.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    /**
     * 初始化布局
     */
    public abstract int getLayoutRes();

    protected abstract void initData();

    protected abstract void initListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutRes());
        // 初始化View注入
        ButterKnife.bind(this);
        
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void showProgress(boolean flag, String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(flag);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setMessage(message);
        }
        mProgressDialog.show();
    }

    public void hideProgress() {
        if (mProgressDialog == null)
            return;

        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 检查是否拥有权限
     * @param thisActivity
     * @param permission
     * @param requestCode
     * @param errorText
     * @return 是否有权限
     **/
    protected boolean checkPermission (Activity thisActivity, String permission, int requestCode, String errorText) {
        //判断当前Activity是否已经获得了该权限
        if(ContextCompat.checkSelfPermission(thisActivity,permission) != PackageManager.PERMISSION_GRANTED) {
            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    permission)) {
                Toast.makeText(this,errorText, Toast.LENGTH_SHORT).show();
                //进行权限请求
                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{permission},
                        requestCode);
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{permission},
                        requestCode);
            }
            return false;
        } else {
            return true;
        }
    }

    protected boolean checkPermission (Activity thisActivity, String[] permissionArray, int requestCode, String errorText) {
        ArrayList<String> permissionList = new ArrayList<>();

        for (int i = 0; i < permissionArray.length; i++) {
            String permission = permissionArray[i];
            //判断当前Activity是否已经获得了该权限
            if (ContextCompat.checkSelfPermission(thisActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
                if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                        permission)) {
                    Toast.makeText(this, errorText, Toast.LENGTH_SHORT).show();
                }
                permissionList.add(permission);
            }
        }

        //进行权限请求
        if (permissionList.size() > 0) {
            String[] toRequestPermissionArray = new String[permissionList.size()];
            ActivityCompat.requestPermissions(thisActivity,
                    permissionList.toArray(toRequestPermissionArray), requestCode);
            return false;
        }
        return true;
    }
}
