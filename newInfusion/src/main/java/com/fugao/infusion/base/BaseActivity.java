/*
 * Copyright (C) 2014 loQua.Xee <loquaciouser@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fugao.infusion.base;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.jasonchen.base.utils.AppManager;

import butterknife.ButterKnife;


public abstract class BaseActivity extends FragmentActivity {
    /**
     * 上下文
     */
    public Context context;

    public Activity currentActivity;

    public LayoutInflater inflater;
    /**
     * 屏幕宽度,高度
     */
    public int windowWidth;
    public int windowHeight;
    /**
     * 是否允许全屏
     */
    private boolean allowFullScreen = true;
    /**
     * 是否隐藏ActionBar
     */
    private boolean hiddenActionBar = false;
    /**
     * 点击退出是否启用框架的退出界面(默认弹出dialog确认)
     */
    private boolean backListener = true;
    /**
     * 屏幕方向
     */
    private ScreenOrientation orientation = ScreenOrientation.VERTICAL;
    private DisplayMetrics displayMetrics = new DisplayMetrics();

    private ProgressDialog loadingDialog;


    /**
     * Activity显示方向
     */
    public static enum ScreenOrientation {
        HORIZONTAL, VERTICAL, AUTO
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (orientation) {
            case HORIZONTAL:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case VERTICAL:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case AUTO:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                break;
        }
        if (hiddenActionBar) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        } else {
            ActionBar a = getActionBar();
            if (a != null) a.show();
        }
        if (allowFullScreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        windowWidth = displayMetrics.widthPixels;
        windowHeight = displayMetrics.heightPixels;

        context = this;
        currentActivity = this;
        inflater = getLayoutInflater();

        // loadingDialog = UIHepler.createProgressDialog(context);

        AppManager.getInstance().addActivity(this);
        setRootView();
        ButterKnife.inject(this);
        initialize();
    }

    /**
     * 初始化界面根布局
     */
    protected abstract void setRootView();

    private void initialize() {
        initIntent();
        initView();
        initData();
        initListener();
    }

    /**
     * 初始化布局控件
     */
    protected void initIntent() {
    }

    /**
     * 初始化布局控件
     */
    protected void initView() {
    }

    /**
     * 同步初始化数据
     */
    protected void initData() {
    }

    /**
     * 初始化时间监听
     */
    protected void initListener() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
    }

    /**
     * 是否全屏显示本Activity，全屏后将隐藏状态栏，默认不全屏
     *
     * @param allowFullScreen 是否全屏
     */
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.allowFullScreen = allowFullScreen;
    }

    /**
     * 是否隐藏ActionBar，默认隐藏
     *
     * @param hiddenActionBar 是否隐藏ActionBar
     */
    public void setHiddenActionBar(boolean hiddenActionBar) {
        this.hiddenActionBar = hiddenActionBar;
    }

    /**
     * 修改屏幕显示方向，默认竖屏锁定
     *
     * @param orientation 屏幕方向
     */
    public void setScreenOrientation(ScreenOrientation orientation) {
        this.orientation = orientation;
    }

    /**
     * 是否启用返回键监听，若启用，则在显示最后一个Activity时将弹出退出对话框。默认启用（若修改必须在构造方法中调用）
     */
    public void setBackListener(boolean openBackListener) {
        this.backListener = openBackListener;
    }

    /**
     * 含有Bundle通过Class跳转界面
     */
    public void openActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        //    /**
        //     * 跳转动画,从左进如
        //     */
        //    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 含有Bundle通过Class跳转界面
     */
    public void openActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        /**
         * 跳转动画,从左进如
         */
        //   overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 通过Class跳转界面
     */
    public void openActivity(Class<?> cls) {
        openActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     */
    public void openActivityForResult(Class<?> cls, int requestCode) {
        openActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Action跳转界面
     */
    public void openActivity(String action, Bundle bundle) {
        Intent intent = new Intent(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        /**
         * 跳转动画,从左进如
         */
        //  overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 通过Action跳转界面
     */
    public void openActivity(String action) {
        openActivity(action, null);
    }

    /**
     * 销毁,附加动画(销毁的时候需要手动立即销毁堆栈中的activity)
     */
    public void closeActivityWithAnim() {
        //        super.finish();
        AppManager.getInstance().finishActivity(this);
        // overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 销毁,不附加动画
     */
    public void closeActivity() {
        //        super.finish();
        AppManager.getInstance().finishActivity(this);
    }

    /**
     * 监听返回事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (backListener && AppManager.getInstance().getCount() < 2) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //UIHepler.create().getExitDialog(this);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示加载进度对话框
     */
    public void showLoadingDialog(String message) {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(context);
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingDialog.setIndeterminate(false);
            loadingDialog.setCancelable(true);
            loadingDialog.setCanceledOnTouchOutside(false);
        }
        loadingDialog.setMessage(message);
        loadingDialog.show();
    }

    /**
     * 关闭对话框
     */
    public void dismissLoadingDialog() {
        if(!this.isFinishing() && loadingDialog !=null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

}
