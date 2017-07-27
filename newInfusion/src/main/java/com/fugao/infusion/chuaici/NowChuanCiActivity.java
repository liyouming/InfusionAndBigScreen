package com.fugao.infusion.chuaici;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
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
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.dao.DataBaseInfo;
import com.fugao.infusion.dao.InfusionDetailDAO;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.setting.SettingFragment;
import com.fugao.infusion.utils.InfusionHelper;
import com.fugao.infusion.utils.PullXMLTools;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.view.ChuaiCiTabWidget;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.InjectView;

/**
 *
 */
public class NowChuanCiActivity extends BaseScanTestTempleActivity implements
        ChuaiCiTabWidget.OnTabSelectedListener {
    private ChuaiCiTabWidget mTabWidget;
    private ChuaiCiExecuteFragment chuaiCiExecuteFragment;
    public ChuanCiPeopleFragment chuanCiPeopleFragment;
    private SettingFragment settingFragment;
    private int mIndex = Constant.INDEX_2;
    private FragmentManager mFragmentManager;
    private FragmentTransaction transaction;
    private WorkloadFragment workloadFragment;
    private ArrayList<Fragment> fragments;
    @InjectView(R.id.tabs_rg)
    RadioGroup tabRadioGroup;
    private TabFragmentAdapter tabFragmentAdapter;

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;
    public InfusionDetailDAO infusionDetailDAO;
    private long exitTime = 0;
    private ProgressDialog loadingDialog;
    private ProgressDialog progressDialog;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_chuan_ci);
    }

    @Override
    public void initView() {
        mFragmentManager = getSupportFragmentManager();
//        mTabWidget = (ChuaiCiTabWidget) findViewById(R.id.chuan_ci_tab_widget);
//        mTabWidget.setOnTabSelectedListener(this);
//        onTabSelected(mIndex);
//        mTabWidget.setTabsDisplay(this, mIndex);


    }

    @Override
    public void initData() {
        loadingDialog = new ProgressDialog(NowChuanCiActivity.this);
        loadingDialog.getWindow().setGravity(Gravity.CENTER);
        progressDialog=new ProgressDialog(NowChuanCiActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("取消中...");
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        infusionDetailDAO=new InfusionDetailDAO(DataBaseInfo.getInstance(context));
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
        if (RoleCategory.SHUYE.getKey() != LocalSetting.RoleIndex) {
            if (moduldes.contains("座位")) {
//                moduldes.remove("巡视");
                moduldes.remove("座位");
            }else if(moduldes.contains("巡视")){
                moduldes.remove("巡视");
            }

        }

        int width = windowWidth / moduldes.size();
        fragments = new ArrayList<Fragment>();
        for (String moduleName : moduldes) {
            BaseFragmentV4 fragment = TabHostFactory.getFragmentByChuanCi(moduleName);
            if(moduleName.contains("列表")){
                chuanCiPeopleFragment = (ChuanCiPeopleFragment) fragment;
            }
            fragments.add(fragment);
            RadioButton radioButton =
                    (RadioButton) LayoutInflater.from(this).inflate(R.layout.home_radiobutton, null);
            radioButton.setLayoutParams(
                    new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
            radioButton.setText(moduleName);

            Drawable drawable =
                    getResources().getDrawable(TabHostFactory.getTabIconByChuanCi(moduleName));
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
      InfusionHelper.showWarningDialog(NowChuanCiActivity.this, "穿刺界面不能扫描病人信息!");
//    chuaiCiExecuteFragment.getData(patientId);
    }

    @Override
    protected void receiverBottleId(String patientId, String bottleId) {
//        if(mDecodeManager ==null){
//            Log.d("列表界面扫描服务mDecodeManager状态========================","销毁");
//        }else {
//            Log.d("列表界面扫描服务mDecodeManager状态========================","没有销毁");
//        }
        final BottleModel bottleModel =  infusionDetailDAO.getBottleById(bottleId);
        if(bottleModel.BottleId!=null){
            if(bottleModel.BottleStatus>3){
                AlertDialog alertDialog=new AlertDialog.Builder(NowChuanCiActivity.this).setTitle("温馨提醒！").setCancelable(false)
                        .setMessage("当前瓶贴已穿刺！请选择操作").
                            setPositiveButton("撤销穿刺", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                String url= ChuanCiApi.cancleChuanCiByBottleId();
                                String json= String2InfusionModel.bottleModel2String(bottleModel);
                                progressDialog.show();
                                RestClient.put(NowChuanCiActivity.this, url, json, new BaseAsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int i, String s) {
                                        progressDialog.dismiss();
                                        UIHepler.showToast(NowChuanCiActivity.this,"取消穿刺成功");
                                        chuanCiPeopleFragment.onRefresh();
                                    }

                                    @Override
                                    public void onFailure(int i, Throwable throwable, String s) {
                                        progressDialog.dismiss();
                                        UIHepler.showToast(NowChuanCiActivity.this,"取消穿刺失败，请重试!");
                                    }
                                });

                            }
                        }).
                        setNegativeButton("重新穿刺", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                chuanCiPeopleFragment.redirect2ExecuteSingleByClick(bottleModel);
                            }
                        }).create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
            }else{
                chuanCiPeopleFragment.redirect2ExecuteSingleByClick(bottleModel);
            }
        }else {
            showLoadingDialog("正在查找瓶贴...");
            String url = ChuanCiApi.url_getBottleByBottleId(bottleId);
            RestClient.get(url,new BaseAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, String s) {
                    dismissLoadingDialog();
                    final BottleModel bottleModel = String2InfusionModel.string2BottleModel(s);
                    if(bottleModel!=null){
                        if(bottleModel.BottleStatus>3){
                            AlertDialog alertDialog=new AlertDialog.Builder(NowChuanCiActivity.this).setTitle("温馨提醒！").setCancelable(false)
                                    .setMessage("当前瓶贴已穿刺！请选择操作").
                                            setPositiveButton("撤销穿刺", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    String url= ChuanCiApi.cancleChuanCiByBottleId();
                                                    String json= String2InfusionModel.bottleModel2String(bottleModel);
                                                    progressDialog.show();
                                                    RestClient.put(NowChuanCiActivity.this, url, json, new BaseAsyncHttpResponseHandler() {
                                                        @Override
                                                        public void onSuccess(int i, String s) {
                                                            progressDialog.dismiss();
                                                            UIHepler.showToast(NowChuanCiActivity.this,"取消穿刺成功");
                                                            chuanCiPeopleFragment.onRefresh();
                                                        }

                                                        @Override
                                                        public void onFailure(int i, Throwable throwable, String s) {
                                                            progressDialog.dismiss();
                                                            UIHepler.showToast(NowChuanCiActivity.this,"取消穿刺失败，请重试!");
                                                        }
                                                    });

                                                }
                                            }).
                                            setNegativeButton("重新穿刺", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    chuanCiPeopleFragment.redirect2ExecuteSingleByClick(bottleModel);
                                                }
                                            }).create();
                            alertDialog.setCanceledOnTouchOutside(true);
                            alertDialog.show();
                        }else {
                            chuanCiPeopleFragment.redirect2ExecuteSingleByClick(bottleModel);
                        }
                    }else {
                        UIHepler.showToast(NowChuanCiActivity.this,"没有查找到该瓶贴");
                    }
//                    Log.e("扫描瓶贴返回成功");
                }

                @Override
                public void onFailure(int i, Throwable throwable, String s) {
//                    Log.e("扫描瓶贴返回失败");
                    dismissLoadingDialog();
                    UIHepler.showToast(NowChuanCiActivity.this,"查找失败");
                }
            });
        }


        if(mIndex==2){

           // BottleModel bottleById1 = infusionDetailDAO.getBottleById(bottleId);

//            if(bottleById1 !=null &&bottleById1.BottleId !=null){
//                preExcuteBottle(bottleById1);
//            }else {
//                showLoadingDialog("正在查找瓶贴...");
//                InfoApi.GetBottleByBottleId(context, bottleId, new RestListener<BottleModel>() {
//                    @Override
//                    public void onSuccess(BottleModel bottleModel) {
//                        dismissLoadingDialog();
//                        Log.e("扫描瓶贴返回成功");
//                        if (bottleModel != null) {
//
//                            bottleDataHelper.insert(bottleModel);
//                            preExcuteBottle(bottleModel);
//                            //                      }
//                        } else {
//                            UIHelper.showLongToast("未获取到数据,请重试!");
//                        }
//                    }
//
//                    @Override
//                    public void onFailed(AppException error, String msg) {
//                        dismissLoadingDialog();
//                        Log.e("扫描瓶贴返回失败");
//                        error.makeToast(context);
//                    }
//                });
//
//                RestClient.get("", new BaseAsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int i, String s) {
//                        dismissLoadingDialog();
//                        ArrayList<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
//                        if (bottleModels.size() > 0) {
//                        // infusionDetailDAO.deleteAllById;
//                            infusionDetailDAO.save(bottleModels);
//
//                            BottleModel tempBo = infusionDetailDAO.getBottleById(bid);
//                            if (tempBo != null) {
//                                //currentBottles = handlerBottles(preHandlerData(tempBo));
//                                if (tempBo.BottleStatus == BottleStatusCategory.WAITINGHANDLE.getKey()) {
//                                    currentBottles = preHandlerData(tempBo);
//                                    loadData(currentBottles);
//                                } else {
//                                    InfusionUIHelper.showWarningDialog(context, "该组药已排药!");
//                                }
//                            } else {
//                                InfusionUIHelper.showWarningDialog(context, "未找到该瓶贴!");
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int i, Throwable throwable, String s) {
//
//                    }
//                });
  //          }
        }
    }


    private void startActivity(BottleModel bottleModel){
        Intent intent = new Intent();
        intent.setClass(NowChuanCiActivity.this,ChuancCiExecuteSingleActivity.class);
        LocalSetting.CurrentBottle=bottleModel;
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (RestClient.BASE_URL.equals("")) {
//            ChuanCiActivity.this.finish();
//        }
    }

    @Override
    public void onTabSelected(int index) {
         transaction = mFragmentManager.beginTransaction();
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
                if (null == chuanCiPeopleFragment) {
                    chuanCiPeopleFragment = new ChuanCiPeopleFragment();
                    transaction.add(R.id.center_layout, chuanCiPeopleFragment);
                } else {
                    transaction.show(chuanCiPeopleFragment);
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
        if (null != chuaiCiExecuteFragment) {
            transaction.hide(chuaiCiExecuteFragment);
        }
        if (null != chuanCiPeopleFragment) {
            transaction.hide(chuanCiPeopleFragment);
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
//            exitApp();     //调用双击退出函数
            InfusionHelper.exite(NowChuanCiActivity.this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
//        chuanCiPeopleFragment.cycleResourceBitmap();//回收native图片资源

    }
}
