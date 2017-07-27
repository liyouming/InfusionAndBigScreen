package com.fugao.infusion.setting;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fugao.infusion.LoginActivity;
import com.fugao.infusion.LoginDialogActivity;
import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.constant.LocalSetting;

import butterknife.InjectView;

/**
 * A simple {@link android.app.Fragment} subclass. TODO 更多界面
 */
public class SettingFragment extends BaseFragmentV4 {


//    @InjectView(R.id.title_text_view)
//    TextView mTitleTextView;
    @InjectView(R.id.cbGm)
    ImageView mCbGm;
    @InjectView(R.id.tvGm)
    TextView mTvGm;
    @InjectView(R.id.contentNormal)
    RelativeLayout mContentNormal;
    @InjectView(R.id.cbPf)
    ImageView mCbPf;
    @InjectView(R.id.tvPf)
    TextView mTvPf;
    @InjectView(R.id.contentSpeed)
    RelativeLayout mContentSpeed;
    @InjectView(R.id.iv_restart_wifi)
    ImageView mIvRestartWifi;
    @InjectView(R.id.tv_restart_wifi)
    TextView mTvRestartWifi;
    @InjectView(R.id.contentSetting)
    RelativeLayout mContentSetting;
    @InjectView(R.id.btnLogout)
    Button mBtnLogout;
    @InjectView(R.id.linearlayout_user_main_scroll_content)
    LinearLayout mLinearlayoutUserMainScrollContent;
    private static Boolean isExit = false;


    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void initView(View currentView) {

    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
        mContentNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent=new Intent();
                    intent.setClass(fatherActivity,RoleChoiceActivity.class);
                    startActivity(intent);
                    fatherActivity.finish();
            }
        });
        mContentSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent();
                intent.setClass(fatherActivity,LoginDialogActivity.class);
                startActivity(intent);
            }
        });
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                InfusionHelper.exite(fatherActivity);
                Intent intent=new Intent();
                intent.setClass(fatherActivity,LoginActivity.class);
                startActivity(intent);
                LocalSetting.CurrentBottle = null;
                LocalSetting.CurrentAccount = null;
                LocalSetting.CurrentGroupBottle = null;
                fatherActivity.finish();
            }
        });
    }

}
