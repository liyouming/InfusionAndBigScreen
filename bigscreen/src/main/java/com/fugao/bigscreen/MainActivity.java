package com.fugao.bigscreen;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fugao.bigscreen.activitys.BaseTempleActivity;
import com.fugao.bigscreen.model.BottleModel;
import com.fugao.bigscreen.utils.PollingService;
import com.jasonchen.base.utils.PollingUtils;

import java.util.ArrayList;

public class MainActivity extends BaseTempleActivity {
    private StandardGridView standardGridView;
    private GridviewAdapter gridviewAdapter;
    public static Handler mainHandler;
    private ArrayList<BottleModel> bottleModels;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initView() {
        standardGridView= (StandardGridView) findViewById(R.id.standard_gridview);
        bottleModels=new ArrayList<>();
        gridviewAdapter=new GridviewAdapter(MainActivity.this,bottleModels);
        standardGridView.setAdapter(gridviewAdapter);
        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 4:
                        bottleModels= (ArrayList<BottleModel>) msg.obj;
                        gridviewAdapter.notifyadapter(bottleModels);
                        break;
                }
            }
        };
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PollingUtils.stopPollingService(this, PollingService.class,
                PollingService.ACTION);
    }
}
