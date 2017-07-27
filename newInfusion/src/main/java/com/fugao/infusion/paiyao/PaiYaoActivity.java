package com.fugao.infusion.paiyao;

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
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.Log;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;

/**
 *
 */
public class PaiYaoActivity extends BaseScanTestTempleActivity {

//    private PaiYaoTabWidget mTabWidget;
    private PaiYaoExecuteFragment paiYaoiExecuteFragment;
    private WorkloadFragment workloadFragment;
    private SettingFragment settingFragment;
    private int mIndex = Constant.INDEX_2;
    private FragmentManager mFragmentManager;
    private FragmentTransaction transaction;
    public InfusionDetailDAO infusionDetailDAO;
    public PaiYaoPeopleFragment paiYaoPeopleFragment;
    private ArrayList<Fragment> fragments;
    @InjectView(R.id.tabs_rg)
    RadioGroup tabRadioGroup;
    private TabFragmentAdapter tabFragmentAdapter;
    private long exitTime = 0;

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;
    private NoticeRefreshReciver noticeRefreshReciver;
    private ProgressDialog loadingDialog;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_pai_yao);
    }

    @Override
    public void initView() {
        mFragmentManager = getSupportFragmentManager();
//        mTabWidget = (PaiYaoTabWidget) findViewById(R.id.paiyao_tab_widget);
//        mTabWidget.setOnTabSelectedListener(this);
//        onTabSelected(mIndex);
//        mTabWidget.setTabsDisplay(this, mIndex);
    }
    @Override
    public void initData() {
        loadingDialog = new ProgressDialog(PaiYaoActivity.this);
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(PaiYaoActivity.this));
        initBoradCast();
        initHomeTab();
    }
    private void initBoradCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.RETURNSUCCESS);
        noticeRefreshReciver = new NoticeRefreshReciver();
        this.registerReceiver(noticeRefreshReciver, intentFilter);
    }
    @Override
    public void initListener() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(noticeRefreshReciver);
//        paiYaoPeopleFragment.cycleResourceBitmap();//回收native图片资源

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
                     if (moduldes.contains("巡视")&&moduldes.contains("座位"))
                         moduldes.remove("巡视");
                         moduldes.remove("座位");

                 int width = windowWidth / moduldes.size();
                 fragments = new ArrayList<Fragment>();
                 for (String moduleName : moduldes) {
                     BaseFragmentV4 fragment = TabHostFactory.getFragmentByPaiYao(moduleName);
                     if(moduleName.contains("列表")){
                         paiYaoPeopleFragment = (PaiYaoPeopleFragment) fragment;
                     }
                     fragments.add(fragment);
                     RadioButton radioButton =
                             (RadioButton) LayoutInflater.from(this).inflate(R.layout.home_radiobutton, null);
                     radioButton.setLayoutParams(
                             new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
                     radioButton.setText(moduleName);

                     Drawable drawable =
                             getResources().getDrawable(TabHostFactory.getTabIconByPaiYao(moduleName));
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
            paiYaoPeopleFragment.getData();
        }
    }

    @Override
    protected void receiverPatientId(String patientId) {
        InfusionHelper.showWarningDialog(PaiYaoActivity.this, "排药界面不能扫描病人信息!");
    }

    @Override
    protected void receiverBottleId(final String patientId, final String bottleId) {
        List<BottleModel> bottlesByPid  =  infusionDetailDAO.getBottleByPatid(patientId);
        if(bottlesByPid !=null&bottlesByPid.size()>0){
           dismissLoadingDialog();
            for (BottleModel bo : bottlesByPid) {
                if (bo.BottleId.equals(bottleId)) {
                    paiYaoPeopleFragment.redirect2ExecuteSingleByClick(bo);
                    break;
                }
            }
        }else {
            showLoadingDialog("正在查找瓶贴...");
            String url = ChuanCiApi.Url_GetBottlesByPid(patientId);
            RestClient.get(url, new BaseAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, String s) {
                    dismissLoadingDialog();
                    List<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
                    if (bottleModels.size() > 0) {
                        infusionDetailDAO.deleteBottlesByPatid(patientId);
                        infusionDetailDAO.saveToInfusionDetail(bottleModels);
                        for (BottleModel bo : bottleModels) {
                            if (bo.BottleId.equals(bottleId)) {
                                paiYaoPeopleFragment.redirect2ExecuteSingleByClick(bo);
                                break;
                            }
                        }
                    } else {
                        UIHepler.showToast(PaiYaoActivity.this,"未获取到数据,请重试!");
                    }
                    Log.e("扫描瓶贴返回成功");
                }
                @Override
                public void onFailure(int i, Throwable throwable, String s) {
                    Log.e("扫描瓶贴返回失败");
                    dismissLoadingDialog();
                    UIHepler.showToast(PaiYaoActivity.this, "查找失败");
                }
            });
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
    }
//    @Override
//    public void onTabSelected(int index) {
//        FragmentTransaction transaction = mFragmentManager.beginTransaction();
//        hideFragments(transaction);
//        switch (index) {
//            case Constant.INDEX_1:
//                if (null == workloadFragment) {
//                    workloadFragment = new WorkloadFragment();
//                    transaction.add(R.id.center_layout, workloadFragment);
//                } else {
//                    transaction.show(workloadFragment);
//                }
//                break;
//            case Constant.INDEX_2:
//                if (null == paiYaoPeopleFragment) {
//                    paiYaoPeopleFragment = new PaiYaoPeopleFragment();
//                    transaction.add(R.id.center_layout, paiYaoPeopleFragment);
//                } else {
//                    transaction.show(paiYaoPeopleFragment);
//                }
//                break;
//            case Constant.INDEX_3:
//                if (null == settingFragment) {
//                    settingFragment = new SettingFragment();
//                    transaction.add(R.id.center_layout, settingFragment);
//                } else {
//                    transaction.show(settingFragment);
//                }
//                break;
//            default:
//                break;
//        }
//        mIndex = index;
//        transaction.commitAllowingStateLoss();
//    }
    private void hideFragments(FragmentTransaction transaction) {
        if (null != paiYaoiExecuteFragment) {
            transaction.hide(paiYaoiExecuteFragment);
        }
        if (null != paiYaoPeopleFragment) {
            transaction.hide(paiYaoPeopleFragment);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == IntentCode.request4IndexOrder2Login && resultCode == IntentCode.response4LoginScuess) {
//            onTabSelected(Constant.INDEX_3);
//            mTabWidget.setTabsDisplay(this, Constant.INDEX_3);
//        }else if(requestCode == IntentCode.request4IndexPerson2Login && resultCode == IntentCode.response4LoginScuess){
//            onTabSelected(Constant.INDEX_2);
//            mTabWidget.setTabsDisplay(this, Constant.INDEX_2);
//        }else if(resultCode== PublicConstant.RESONSE_SCAN_QR){
//            UIHepler.showDilalog(IndexActivity.this,data.getStringExtra(PublicConstant.QRCODE_SCAN_RESULT_KEY));
//        }
    }
    public void onPause() {
        super.onPause();
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
            InfusionHelper.exite(PaiYaoActivity.this);
//            finish();
//            System.exit(0);
        }
    }

    /**
     * 如果子水平是list 点击标题栏空白处滑动到顶部
     */
    public static interface ScrollableListFragment {
        public void scrollToTop();
    }

    /**
     * 顶部刷新接口
     *
     */
    public static interface RefreshFragment {
        public void topRefresh();
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

