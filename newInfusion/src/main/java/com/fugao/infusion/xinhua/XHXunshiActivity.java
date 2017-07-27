package com.fugao.infusion.xinhua;

import android.app.ProgressDialog;
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
import com.fugao.infusion.chuaici.ChuanCiActivity;
import com.fugao.infusion.comonPage.TabFragmentAdapter;
import com.fugao.infusion.comonPage.TabHostFactory;
import com.fugao.infusion.comonPage.WorkloadFragment;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.setting.SettingFragment;
import com.fugao.infusion.utils.InfusionHelper;
import com.fugao.infusion.utils.PullXMLTools;
import com.fugao.infusion.view.XunShiTabWidget;
import com.fugao.infusion.xunshi.XunshiActivity;
import com.fugao.infusion.xunshi.XunshiPeopleFragment;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;

/** 新的逻辑巡视界面
 * Created by li on 2017/3/29.
 */

public class XHXunshiActivity extends BaseScanTestTempleActivity implements
        XunShiTabWidget.OnTabSelectedListener{
    private XunShiTabWidget mTabWidget;
    public XunshiPeopleFragment xunshiPeopleFragment;
    public XHXunshiPeopleFragment xhXunshiPeopleFragment;
    private SettingFragment settingFragment;
    private int mIndex = Constant.INDEX_2;
    private FragmentManager mFragmentManager;
    private FragmentTransaction transaction;
    private WorkloadFragment workloadFragment;

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private ArrayList<Fragment> fragments;
    @InjectView(R.id.tabs_rg)
    RadioGroup tabRadioGroup;
    private TabFragmentAdapter tabFragmentAdapter;
    private long exitTime = 0;
    private ProgressDialog loadingDialog;
    private NoticeRefreshReciver noticeRefreshReciver;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_xunshi);
    }

    @Override
    public void initView() {
        mFragmentManager = getSupportFragmentManager();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(noticeRefreshReciver);
    }
    @Override
    public void initData() {
        loadingDialog = new ProgressDialog(XHXunshiActivity.this);
        Constant.CURRENTPATIENT="";
        initBoradCast();
        initHomeTab();
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {

    }

    /**
     * 加载主页面布局
     */
    private void initHomeTab() {
        String string = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "main_menu");

        List<String> oriModuldes = Arrays.asList(string.split(","));
        ArrayList<String> moduldes = new ArrayList<String>();
        moduldes.addAll(oriModuldes);
//        if (moduldes.contains("巡视")) moduldes.remove("巡视");
        int width = windowWidth / moduldes.size();
        fragments = new ArrayList<Fragment>();
        for (String moduleName : moduldes) {
            BaseFragmentV4 fragment = TabHostFactory.getFragmentByXunShi(moduleName);
            if (moduleName.contains("列表")) {
                xhXunshiPeopleFragment = (XHXunshiPeopleFragment) fragment;
            }
            if(!fragments.contains(fragment))fragments.add(fragment);
            RadioButton radioButton =
                    (RadioButton) LayoutInflater.from(this).inflate(R.layout.home_radiobutton, null);
            radioButton.setLayoutParams(
                    new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
            radioButton.setText(moduleName);

            Drawable drawable =
                    getResources().getDrawable(TabHostFactory.getTabIconByXunShi(moduleName));
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
    @Override
    protected void receiverPatientId(String patientId) {
        xhXunshiPeopleFragment.getData(patientId);

    }

    private class NoticeRefreshReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            xhXunshiPeopleFragment.getData(Constant.CURRENTPATIENT);
        }
    }
    private void initBoradCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.RETURNSUCCESS);
        noticeRefreshReciver = new NoticeRefreshReciver();
        this.registerReceiver(noticeRefreshReciver, intentFilter);
    }
    @Override
    protected void receiverBottleId(String patientId, String bottleId) {
        if(StringUtils.StringIsEmpty(Constant.CURRENTPATIENT)){
//            xhXunshiPeopleFragment.getData(patientId);
            UIHepler.showToast(XHXunshiActivity.this,"请先扫腕带");
        }else if(patientId.equals(Constant.CURRENTPATIENT)){
            xhXunshiPeopleFragment.redirect2ExecuteBottleId(bottleId);
        }else{
//            xhXunshiPeopleFragment.getData(patientId);
            UIHepler.showToast(XHXunshiActivity.this,"请先扫腕带");
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
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
                if (null == xhXunshiPeopleFragment) {
                    xhXunshiPeopleFragment = new XHXunshiPeopleFragment();
                    transaction.add(R.id.center_layout, xhXunshiPeopleFragment);
                } else {
                    transaction.show(xhXunshiPeopleFragment);
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
        if (null != xhXunshiPeopleFragment) {
            transaction.hide(xhXunshiPeopleFragment);
        }
        if (null != xhXunshiPeopleFragment) {
            transaction.hide(xhXunshiPeopleFragment);
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // super.onRestoreInstanceState(savedInstanceState);
        mIndex = savedInstanceState.getInt("index");
    }
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
//            exitApp();     //调用双击退出函数
            InfusionHelper.exite(XHXunshiActivity.this);
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
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            System.exit(0);
        }
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
