package com.fugao.infusion.zhongji;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.base.BaseScanTestTempleActivity;
import com.fugao.infusion.comonPage.RealeseSeatFragment;
import com.fugao.infusion.comonPage.TabFragmentAdapter;
import com.fugao.infusion.comonPage.TabHostFactory;
import com.fugao.infusion.comonPage.WorkloadFragment;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.dao.DataBaseInfo;
import com.fugao.infusion.dao.InfusionDetailDAO;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.InfusioningModel;
import com.fugao.infusion.setting.SettingFragment;
import com.fugao.infusion.utils.InfusionHelper;
import com.fugao.infusion.utils.PullXMLTools;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.view.XunShiTabWidget;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;

/**
 * ToDo 重症急诊留观室 他们既有穿刺的功能 也有巡视的功能
 */
public class ZhongjiXunshiActivity extends BaseScanTestTempleActivity implements
        XunShiTabWidget.OnTabSelectedListener {
    private XunShiTabWidget mTabWidget;
    public ZhongJiXunshiPeopleFragment zhongjiXunshiPeopleFragment;
    public ZhongJiChuanCiPeopleFragment zhongJiChuanCiPeopleFragment;
    public RealeseSeatFragment realeseSeatFragment;
    public ZhongJIArroundFragment zhongJIArroundFragment;
    private SettingFragment settingFragment;
    private int mIndex = Constant.INDEX_2;
    private FragmentManager mFragmentManager;
    private FragmentTransaction transaction;
    private WorkloadFragment workloadFragment;

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;
    public InfusionDetailDAO infusionDetailDAO;
    private NoticeRefreshReciver noticeRefreshReciver;

    private ArrayList<Fragment> fragments;
    @InjectView(R.id.tabs_rg)
    RadioGroup tabRadioGroup;
    private TabFragmentAdapter tabFragmentAdapter;
    private long exitTime = 0;
    private ProgressDialog loadingDialog;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_xunshi);
    }

    @Override
    public void initView() {
        mFragmentManager = getSupportFragmentManager();
//        mTabWidget = (XunShiTabWidget) findViewById(R.id.xun_shi_tab_widget);
//        mTabWidget.setOnTabSelectedListener(this);
//        onTabSelected(mIndex);
//        mTabWidget.setTabsDisplay(this, mIndex);


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(noticeRefreshReciver);
//        zhongjiPeopleFragment.cycleResourceBitmap();
    }
    @Override
    public void initData() {
        loadingDialog = new ProgressDialog(ZhongjiXunshiActivity.this);
        initBoradCast();
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(ZhongjiXunshiActivity.this));
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
        String string = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "zhongji_main_menu");

        List<String> oriModuldes = Arrays.asList(string.split(","));
        ArrayList<String> moduldes = new ArrayList<String>();
        moduldes.addAll(oriModuldes);
//        if (moduldes.contains("巡视")) moduldes.remove("巡视");
        int width = windowWidth / moduldes.size();
        fragments = new ArrayList<Fragment>();
        for (String moduleName : moduldes) {
            BaseFragmentV4 fragment = TabHostFactory.getFragmentByZhongJiXunShi(moduleName);
            if (moduleName.contains("输液巡视")) {
                zhongjiXunshiPeopleFragment = (ZhongJiXunshiPeopleFragment) fragment;
            }else if(moduleName.contains("穿刺")){
                zhongJiChuanCiPeopleFragment = (ZhongJiChuanCiPeopleFragment) fragment;
            }
            if(!fragments.contains(fragment))fragments.add(fragment);
            RadioButton radioButton =
                    (RadioButton) LayoutInflater.from(this).inflate(R.layout.home_radiobutton, null);
            radioButton.setLayoutParams(
                    new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
            radioButton.setText(moduleName);

            Drawable drawable =
                    getResources().getDrawable(TabHostFactory.getTabIconByZhongJiXunShi(moduleName));
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
            zhongjiXunshiPeopleFragment.getData();
        }
    }
    private void initBoradCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.RETURNSUCCESS);
        noticeRefreshReciver = new NoticeRefreshReciver();
        this.registerReceiver(noticeRefreshReciver, intentFilter);
    }
    @Override
    protected void receiverPatientId(String patientId) {
        if(zhongjiXunshiPeopleFragment !=null && zhongjiXunshiPeopleFragment.isVisible()){
            InfusionHelper.showWarningDialog(ZhongjiXunshiActivity.this, "巡视界面不能扫描病人信息!");
        }else if(zhongJiChuanCiPeopleFragment !=null && zhongJiChuanCiPeopleFragment.isVisible()){
            InfusionHelper.showWarningDialog(ZhongjiXunshiActivity.this, "穿刺界面不能扫描病人信息!");
        }
    }

    @Override
    protected void receiverBottleId(String patientId, String bottleId) {
//        BottleModel bottleModel =  infusionDetailDAO.getBottleById(bottleId);
//        if(bottleModel.BottleId!=null){
//            zhongjiPeopleFragment.redirect2ExecuteSingleByClick(bottleModel);
//        }
        if(zhongJiChuanCiPeopleFragment !=null && zhongJiChuanCiPeopleFragment.isVisible()){
            if(mDecodeManager ==null){
                Log.d("列表界面扫描服务mDecodeManager状态========================","销毁");
            }else {
                Log.d("列表界面扫描服务mDecodeManager状态========================","没有销毁");
            }
            BottleModel bottleModel =  infusionDetailDAO.getBottleById(bottleId);
            if(bottleModel.BottleId!=null){
                zhongJiChuanCiPeopleFragment.redirect2ExecuteSingleByClick(bottleModel);
            }else {
                showLoadingDialog("正在查找瓶贴...");
                String url = ChuanCiApi.url_getBottleByBottleId(bottleId);
                RestClient.get(url,new BaseAsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, String s) {
                        dismissLoadingDialog();
                        BottleModel bottleModel = String2InfusionModel.string2BottleModel(s);
                        if(bottleModel!=null){
                            zhongJiChuanCiPeopleFragment.redirect2ExecuteSingleByClick(bottleModel);
                        }else {
                            UIHepler.showToast(ZhongjiXunshiActivity.this,"没有查找到该瓶贴");
                        }
                        //                    Log.e("扫描瓶贴返回成功");
                    }

                    @Override
                    public void onFailure(int i, Throwable throwable, String s) {
                        //                    Log.e("扫描瓶贴返回失败");
                        dismissLoadingDialog();
                        UIHepler.showToast(ZhongjiXunshiActivity.this,"查找失败");
                    }
                });
            }
        }else if(zhongjiXunshiPeopleFragment !=null && zhongjiXunshiPeopleFragment.isVisible()){
            showLoadingDialog("正在查找瓶贴...");
            String url = InfoApi.url_infusioningCount(bottleId,patientId);
            Log.d("=========================正在查找瓶贴", "");
            final long l1 = System.currentTimeMillis();
            RestClient.get(url, new BaseAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, String s) {
                    long l2 = System.currentTimeMillis();
                    dismissLoadingDialog();
                    Log.d("查询瓶贴成功返回耗时================",""+(l2-l1));
                    long l3 = System.currentTimeMillis();
                    InfusioningModel infusioningModel = String2InfusionModel.string2InfusioningModel(s);
                    Log.d("客户端解析耗时耗时================", "" + (l3 - l2));
                    if(infusioningModel.bottle!=null){
                        long l4 = System.currentTimeMillis();
                        zhongjiXunshiPeopleFragment.redirect2ExecuteSingleByClick(infusioningModel.bottle);
                        long l5 = System.currentTimeMillis();
                        Log.d("跳转到执行界面耗时================",""+(l5-l4));
                        LocalSetting.DoingCount =infusioningModel.DoingCount;
                        LocalSetting.TodoCount =infusioningModel.TodoCount;
                    }else {
                        UIHepler.showToast(ZhongjiXunshiActivity.this, "没有查询到该瓶贴");
                    }
                    Log.e("扫描瓶贴返回成功","");

                }

                @Override
                public void onFailure(int i, Throwable throwable, String s) {
                    Log.e("扫描瓶贴返回失败","");
                    dismissLoadingDialog();
                    UIHepler.showToast(ZhongjiXunshiActivity.this, "查找失败");
                }
            });
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
            case Constant.INDEX_2:
                if (null == zhongJiChuanCiPeopleFragment) {
                    zhongJiChuanCiPeopleFragment = new ZhongJiChuanCiPeopleFragment();
                    transaction.add(R.id.center_layout, zhongJiChuanCiPeopleFragment);
                } else {
                    transaction.show(zhongJiChuanCiPeopleFragment);
                }
                break;
            case Constant.INDEX_1:
                if (null == zhongjiXunshiPeopleFragment) {
                    zhongjiXunshiPeopleFragment = new ZhongJiXunshiPeopleFragment();
                    transaction.add(R.id.center_layout, zhongjiXunshiPeopleFragment);
                } else {
                    transaction.show(zhongjiXunshiPeopleFragment);
                }
                break;
            case Constant.INDEX_5:
                if (null == settingFragment) {
                    settingFragment = new SettingFragment();
                    transaction.add(R.id.center_layout, settingFragment);
                } else {
                    transaction.show(settingFragment);
                }
                break;
            case Constant.INDEX_3:
                if (null == realeseSeatFragment) {
                    realeseSeatFragment = new RealeseSeatFragment();
                    transaction.add(R.id.center_layout, realeseSeatFragment);
                } else {
                    transaction.show(realeseSeatFragment);
                }
                break;
            case Constant.INDEX_4:
                if (null == zhongJIArroundFragment) {
                    zhongJIArroundFragment = new ZhongJIArroundFragment();
                    transaction.add(R.id.center_layout, zhongJIArroundFragment);
                } else {
                    transaction.show(zhongJIArroundFragment);
                }
                break;
            default:
                break;
        }
        mIndex = index;
        transaction.commitAllowingStateLoss();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (null != zhongjiXunshiPeopleFragment) {
            transaction.hide(zhongjiXunshiPeopleFragment);
        }
        if (null != zhongJiChuanCiPeopleFragment) {
            transaction.hide(zhongJiChuanCiPeopleFragment);
        }
        if (null != settingFragment) {
            transaction.hide(settingFragment);
        }
        if (null != zhongJIArroundFragment) {
            transaction.hide(zhongJIArroundFragment);
        }
        if (null != realeseSeatFragment) {
            transaction.hide(realeseSeatFragment);
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
            InfusionHelper.exite(ZhongjiXunshiActivity.this);
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