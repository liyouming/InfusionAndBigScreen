package com.fugao.newbigscreen;


import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.fugao.newbigscreen.activitys.BaseTempleActivity;
import com.fugao.newbigscreen.activitys.InjectionWaitingActivity;
import com.fugao.newbigscreen.activitys.SeatActivity;
import com.fugao.newbigscreen.activitys.SeatCallActivity;
import com.jasonchen.base.utils.RestClient;


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
    ImageView splash_loading_item;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_loading_layout);

    }

    @Override
    public void initView() {
        splash_loading_item=(ImageView)findViewById(R.id.splash_loading_item);

    }

    @Override
    public void initData() {
//        RestClient.BASE_URL="http://10.0.5.23:8007/";
        RestClient.BASE_URL="http://10.0.2.13:8007/";
//        RestClient.BASE_URL="http://192.168.11.20:8011/";
        Animation translate = AnimationUtils.loadAnimation(this,
                R.anim.splash_loading);
        translate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                openActivity(InjectionWaitingActivity.class);
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
