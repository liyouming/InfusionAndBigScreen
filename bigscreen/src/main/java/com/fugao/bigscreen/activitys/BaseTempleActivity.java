package com.fugao.bigscreen.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;


import com.fugao.bigscreen.utils.AppManager;

import butterknife.ButterKnife;

/** 基础类
 * Created by li on 2015/9/17.
 */
public abstract class BaseTempleActivity extends FragmentActivity{
    public DisplayMetrics displayMetrics = new DisplayMetrics();
    public int windowWidth;
    public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        windowWidth = displayMetrics.widthPixels;
        context=this;
        AppManager.getInstance().addActivity(this);
        setContentView();
        ButterKnife.inject(this);
        initIntent();
        initView();
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
        AppManager.getInstance().finishActivity(this);
    }

    /**
     * 通过类名启动Activity
     *
     * @param pClass
     */
    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    /**
     * 通过类名启动Activity，并且含有Bundle数据
     *
     * @param pClass
     * @param pBundle
     */
    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }
    /**
     * 通过Action启动Activity
     *
     * @param pAction
     */
    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    /**
     * 通过Action启动Activity，并且含有Bundle数据
     *
     * @param pAction
     * @param pBundle
     */
    protected void openActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    public abstract void setContentView();


    public abstract void initView();

    public abstract void initData();


    public abstract void initListener();

    public abstract void initIntent();
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
