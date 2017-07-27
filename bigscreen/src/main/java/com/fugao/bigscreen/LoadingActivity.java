package com.fugao.bigscreen;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.fugao.bigscreen.activitys.BaseTempleActivity;
import com.fugao.bigscreen.utils.PollingService;
import com.jasonchen.base.utils.PollingUtils;


import butterknife.InjectView;

/** 加载界面
 * Created by liym on 2015/11/18.
 */
public class LoadingActivity extends BaseTempleActivity {
    /**
     * 数据库操作
     */
//    private DataBaseInfo dataBaseInfo;
    //学校信息表
//    private SchoolDao schoolDao;
//    List<SchoolBean> schoolBeans;
    @InjectView(R.id.splash_loading_item)
    ImageView splash_loading_item;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_loading_layout);

    }

    @Override
    public void initView() {
        PollingUtils.startPollingService(this, 100, PollingService.class,
                PollingService.ACTION);

    }

    @Override
    public void initData() {
        Animation translate = AnimationUtils.loadAnimation(this,
                R.anim.splash_loading);
        translate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                openActivity(MainActivity.class);
                overridePendingTransition(R.anim.splash_push_left_in,
                        R.anim.splash_push_left_out);
                LoadingActivity.this.finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        splash_loading_item.setAnimation(translate);

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {

    }
}
