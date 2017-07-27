package com.fugao.infusion.peiye;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.base.BaseScanTestTempleActivity;
import com.fugao.infusion.comonPage.TabFragmentAdapter;
import com.fugao.infusion.comonPage.TabHostFactory;
import com.fugao.infusion.comonPage.WorkloadFragment;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.dao.DataBaseInfo;
import com.fugao.infusion.dao.InfusionDetailDAO;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.setting.SettingFragment;
import com.fugao.infusion.utils.InfusionHelper;
import com.fugao.infusion.utils.PullXMLTools;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.view.PeiYeTabWidget;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.Log;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;

public class PeiYeActivity extends BaseScanTestTempleActivity implements
        PeiYeTabWidget.OnTabSelectedListener {
    private FragmentManager mFragmentManager;
//    private PeiYeTabWidget mTabWidget;
    private int mIndex = Constant.INDEX_2;
    public InfusionDetailDAO infusionDetailDAO;
    private PeiYeExecuteFragment peiyeiExecuteFragment;
    public PeiYePeopleFragment peiyePeopleFragment;
    public NewPeiYePeopleFragment newPeiYePeopleFragment;
    public XinhuaPeiyeFragment xinhuaPeiyeFragment;
    private SettingFragment settingFragment;
    private NoticeRefreshReciver noticeRefreshReciver;
    private WorkloadFragment workloadFragment;
    private ArrayList<Fragment> fragments;
    @InjectView(R.id.tabs_rg)
    RadioGroup tabRadioGroup;
    private TabFragmentAdapter tabFragmentAdapter;
    private long exitTime = 0;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_pei_ye);
    }

    @Override
    public void initView() {
        mFragmentManager = getSupportFragmentManager();
//        mTabWidget = (PeiYeTabWidget) findViewById(R.id.peiye_tab_widget);
//        mTabWidget.setOnTabSelectedListener(this);
//        onTabSelected(mIndex);
//        mTabWidget.setTabsDisplay(this, mIndex);
    }

    @Override
    public void initData() {
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(PeiYeActivity.this));
        initBoradCast();
        initHomeTab();
    }

    @Override
    public void initListener() {

    }
    private void initBoradCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.RETURNSUCCESS);
        noticeRefreshReciver = new NoticeRefreshReciver();
        this.registerReceiver(noticeRefreshReciver, intentFilter);
    }

    /**
     * 加载主页面布局
     */
    private void initHomeTab() {
        String string = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "main_menu");

        List<String> oriModuldes = Arrays.asList(string.split(","));
        ArrayList<String> moduldes = new ArrayList<String>();
        moduldes.addAll(oriModuldes);
            if (moduldes.contains("巡视")&&moduldes.contains("座位") &&moduldes.contains("列表"))
                moduldes.remove("列表");
                moduldes.remove("巡视");
                moduldes.remove("座位");

        int width = windowWidth / moduldes.size();
        fragments = new ArrayList<Fragment>();
        for (String moduleName : moduldes) {
            BaseFragmentV4 fragment = TabHostFactory.getFragmentByPeiYe(moduleName);
            if(moduleName.contains("列表")){
//                peiyePeopleFragment = (PeiYePeopleFragment) fragment;
//                newPeiYePeopleFragment= (NewPeiYePeopleFragment) fragment;
                xinhuaPeiyeFragment= (XinhuaPeiyeFragment) fragment;
            }
            fragments.add(fragment);
            RadioButton radioButton =
                    (RadioButton) LayoutInflater.from(this).inflate(R.layout.home_radiobutton, null);
            radioButton.setLayoutParams(
                    new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
            radioButton.setText(moduleName);

            Drawable drawable =
                    getResources().getDrawable(TabHostFactory.getTabIconByPeiYe(moduleName));
            radioButton.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);

            tabRadioGroup.addView(radioButton);
        }

        tabFragmentAdapter = new TabFragmentAdapter(this, fragments, R.id.center_layout, tabRadioGroup);
        tabFragmentAdapter.setOnRgsExtraCheckedChangedListener(
                new TabFragmentAdapter.OnRgsExtraCheckedChangedListener() {
                    @Override
                    public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                    }
                }
        );
    }
    private class NoticeRefreshReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            peiyePeopleFragment.getData();
        }
    }

    @Override
    public void initIntent() {

    }

    @Override
    protected void receiverPatientId(String patientId) {
        if(!StringUtils.StringIsEmpty(patientId))return;//配液界面列表暂时隐藏
        InfusionHelper.showWarningDialog(PeiYeActivity.this, "配液界面不能扫描病人信息!");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitApp();     //调用双击退出函数
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }


    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
//            LocalSetting.CurrentAccount = null;
            finish();
            System.exit(0);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(noticeRefreshReciver);
    }

    @Override
    protected void receiverBottleId(String patientId, String bottleId) {
        xinhuaPeiyeFragment.bottleIdExecute(bottleId);
//        if(!StringUtils.StringIsEmpty(bottleId))return;//配液界面列表暂时隐藏

//        BottleModel bottleModel =  infusionDetailDAO.getBottleById(bottleId);
//        if(bottleModel.BottleId!=null){
////            startActivity(bottleModel);
//            peiyePeopleFragment.redirect2Execute(bottleModel);
//        }else {
        /**
         * 此部分原有为配液
         */
//            showLoadingDialog("正在查找瓶贴...");
//            String url = ChuanCiApi.url_getBottleByBottleId(bottleId);
//            RestClient.get(url, new BaseAsyncHttpResponseHandler() {
//                @Override
//                public void onSuccess(int i, String s) {
//                    dismissLoadingDialog();
//                    BottleModel bottleModel = String2InfusionModel.string2BottleModel(s);
//                    if(bottleModel!=null&&!StringUtils.StringIsEmpty(bottleModel.BottleId)){
////                        peiyePeopleFragment.redirect2Execute(bottleModel);
//                        newPeiYePeopleFragment.redirect2Execute(bottleModel);
//                        Log.e("扫描瓶贴返回成功");
//                    }else {
//                        UIHepler.showToast(PeiYeActivity.this, "未查找到该瓶贴");
//                    }
//                }
//                @Override
//                public void onFailure(int i, Throwable throwable, String s) {
//                    Log.e("扫描瓶贴返回失败");
//                    dismissLoadingDialog();
//                    UIHepler.showToast(PeiYeActivity.this, "查找失败");
//                }
//            });
//        }
    }

    @Override
    public void onTabSelected(int index) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case Constant.INDEX_1:
                if (null == workloadFragment) {
                    workloadFragment = new WorkloadFragment();
                    transaction.add(R.id.center_layout, workloadFragment);
                } else {
                    transaction.show(workloadFragment);
                }
                break;
            case Constant.INDEX_2:
//                if (null == peiyePeopleFragment) {
//                    peiyePeopleFragment = new PeiYePeopleFragment();
//                    transaction.add(R.id.center_layout, peiyePeopleFragment);
//                } else {
//                    transaction.show(peiyePeopleFragment);
//                }
//                if (null == newPeiYePeopleFragment) {
//                    newPeiYePeopleFragment = new NewPeiYePeopleFragment();
//                    transaction.add(R.id.center_layout, newPeiYePeopleFragment);
//                } else {
//                    transaction.show(newPeiYePeopleFragment);
//                }
                if (null == xinhuaPeiyeFragment) {
                    xinhuaPeiyeFragment = new XinhuaPeiyeFragment();
                    transaction.add(R.id.center_layout, xinhuaPeiyeFragment);
                } else {
                    transaction.show(xinhuaPeiyeFragment);
                }
                break;
            case Constant.INDEX_3:
                if (null == settingFragment) {
                    settingFragment = new SettingFragment();
                    transaction.add(R.id.center_layout, settingFragment);
                } else {
                    transaction.show(settingFragment);
                }
                break;
            default:
                break;
        }
        mIndex = index;
        transaction.commitAllowingStateLoss();

    }
    private void hideFragments(FragmentTransaction transaction) {
        if (null != peiyeiExecuteFragment) {
            transaction.hide(peiyeiExecuteFragment);
        }
//        if (null != peiyePeopleFragment) {
//            transaction.hide(peiyePeopleFragment);
//        }
//        if (null != newPeiYePeopleFragment) {
//            transaction.hide(newPeiYePeopleFragment);
//        }
        if (null != xinhuaPeiyeFragment) {
            transaction.hide(xinhuaPeiyeFragment);
        }
        if (null != settingFragment) {
            transaction.hide(settingFragment);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // 自己记录fragment的位置,防止activity被系统回收时，fragment错乱的问题
        // super.onSaveInstanceState(outState);
        outState.putInt("index", mIndex);
    }
}
