/*
 * Copyright (C) 2014 loQua.Xee <loquaciouser@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fugao.infusion.peiye;

import android.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseScanTestTempleActivity;
import com.fugao.infusion.chuaici.BottleAdapter;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.dao.DataBaseInfo;
import com.fugao.infusion.dao.InfusionDetailDAO;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.DrugDetailModel;
import com.fugao.infusion.model.QueueModel;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.InfusionUIHelper;
import com.fugao.infusion.view.ProgressGenerator;
import com.fugao.infusion.view.processbutton.iml.ActionProcessButton;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.DateUtils;
import com.jasonchen.base.utils.JacksonHelper;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * @Description: TODO 排药和配药执行界面  批量执行

 */

public class PeiYeExecuteBatchActivity extends BaseScanTestTempleActivity {
    private static final String TAG = "Fast-ExecuteActivity";
    @InjectView(R.id.leftInfo)
    TextView leftInfo;
    @InjectView(R.id.rightInfo)
    TextView rightInfo;
    @InjectView(R.id.listView)
    ListView listView;
    @InjectView(R.id.btnOK)
    ActionProcessButton btnOK;
    @InjectView(R.id.lzzGcf)
    LinearLayout lzzGcf;
    @InjectView(R.id.lzz)
    TextView lzz;
    @InjectView(R.id.gcf)
    TextView gcf;
    public List<BottleModel> currentBottles;
    private List<BottleModel> updateBottles;
    private String bid;
    private BottleModel CurrentBottle;//当前瓶贴
    private BottleAdapter bottleAdapter = null;
    private  ActionBar actionBar;
    private Map<Integer, Boolean> isCurrentConfigMaps;
    public int bottleTime;
    private String bottleTime1;
    private InfusionDetailDAO infusionDetailDAO;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_excute_batch);
    }

    @Override
    public void initView() {
        CurrentBottle = LocalSetting.CurrentBottle;
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(context));
        currentBottles = new ArrayList<BottleModel>();
        updateBottles = new ArrayList<BottleModel>();
        bid = LocalSetting.CurrentBottle.BottleId;
        currentBottles = preHandlerData(LocalSetting.CurrentBottle);
        loadData(currentBottles);
    }

    @Override
    public void initListener() {
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnOK.setEnabled(false);
                /**
                 * 执行提交操作，和服务器进行交互
                 */
                excute();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i,
                                           long l) {
                return true;
            }
        });
    }

    @Override
    public void initIntent() {

    }

    /**
     * 执行提交操作，和服务器进行交互
     */
    private void excute() {
        final ProgressGenerator progressGenerator = new ProgressGenerator(btnOK);
        progressGenerator.start();
            for (int i = 0; i < currentBottles.size(); i++) {
                BottleModel each = currentBottles.get(i);
                each.BottleStatus = BottleStatusCategory.WAITINGINFUSE.getKey();
                each.LiquorCore = LocalSetting.CurrentAccount.UserName;
                each.LiquorName = LocalSetting.CurrentAccount.FullName;
                each.LiquorDate = DateUtils.getCurrentDate("yyyyMMdd");
                each.LiquorTime = DateUtils.getCurrentDate("HHmmss");
        }
        String url = ChuanCiApi.url_updateBottles();
        String Json = JacksonHelper.model2String(currentBottles);
        RestClient.put(PeiYeExecuteBatchActivity.this,url,Json,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                /**
                 * 更新本地信息
                 */
                infusionDetailDAO.updateLocalData((ArrayList<BottleModel>) updateBottles);
                progressGenerator.fail();
                btnOK.setEnabled(true);
                UIHepler.showToast(PeiYeExecuteBatchActivity.this,"执行成功!");
                InfoUtils.sendSuccessBroadcast(context);
                finish();
            }
            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                progressGenerator.fail();
                btnOK.setText("提交失败,请重试!");
                btnOK.setEnabled(true);
            }
        });
    }
    @Override
    public  void initData() {

    }
    /**
     * 是否批量扫描载入
     */
    private List<BottleModel> preHandlerData(BottleModel currentBottle) {
        List<BottleModel> bos = new ArrayList<BottleModel>();
        if (LocalSetting.BatchScanPeiye) {
            bos.addAll(infusionDetailDAO.getBottleByPatid(currentBottle.PeopleInfo.PatId));
        } else {
            bos.add(currentBottle);
        }
        return bos;
    }

    /**
     * 排药 1-2 筛除2
     * 配液 2-3 筛除3
     *
     * @param bottles 处理前的拼贴集合
     * @return 处理后的拼贴集合
     */

    private List<BottleModel> handlerBottles(List<BottleModel> bottles) {
        List<BottleModel> bos = new ArrayList<BottleModel>();
        for (BottleModel bo : bottles) {
            if (RoleCategory.PEIYE.getKey() == LocalSetting.RoleIndex) {
                if (BottleStatusCategory.HADHANDLE.getKey() == bo.BottleStatus) {
                    bos.add(bo);
                }
            }
        }
        return bos;
    }

    /**
     * 加载信息到UI
     *
     * @param bottles 符合条件的拼贴集合
     */
    private void loadData(List<BottleModel> bottles) {

        if (bottles.size() > 0) {
            this.btnOK.setEnabled(true);
            QueueModel temQue = bottles.get(0).PeopleInfo;
            leftInfo.setText(temQue.Name
                            + "("
                            + temQue.PatId
                            + ") "
                            + (temQue.Sex == 1 ? "男" : "女")
                            + " "
                            + temQue.Age
                            + " "
                            + temQue.Weight
            );
            if (StringUtils.getString(CurrentBottle.GCF).equals("重症")
                    || !StringUtils.StringIsEmpty(CurrentBottle.LZZ)
                    || !StringUtils.StringIsEmpty(temQue.DrugAllergy)) {
                lzzGcf.setVisibility(View.VISIBLE);
            }
            if (StringUtils.getString(CurrentBottle.GCF).equals("重症")) {
                gcf.setText(CurrentBottle.GCF);
            }
            if (!StringUtils.StringIsEmpty(CurrentBottle.LZZ)) {
                lzz.setText(CurrentBottle.LZZ);
            }
            if (!StringUtils.StringIsEmpty(temQue.DrugAllergy)) {
                rightInfo.setText(temQue.DrugAllergy);
//                rightInfo.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            }
//            groupId.setText(CurrentBottle.GroupId);
            /*if(RoleCategory.PAIYAO.getKey()==LocalSetting.RoleIndex){
                tvSj.setText(StringUtils.StringIsEmpty(CurrentBottle.PillDate)? "" : CurrentBottle.PillDate);
            }
//                if(RoleCategory.PEIYE.getKey()==LocalSetting.RoleIndex){
////                    tvSj.setText(StringUtils.StringIsEmpty(currentBottle.InfusionDate) ? "" : currentBottle.InfusionDate);
            tvSj.setText(StringUtils.StringIsEmpty(CurrentBottle.LiquorDate)? "" : CurrentBottle.LiquorDate);*/
           /* if(RoleCategory.PEIYE.getKey()==LocalSetting.RoleIndex) {
                if (CurrentBottle.RegistrationDate != null) {
                    tvSj.setText(DateUtils.getMMddHHmm(CurrentBottle.RegistrationDate));
                }
            }*/
        }
            isCurrentConfigMaps = new HashMap<Integer, Boolean>();

            /**
             * 首先,默认情况下,所有项目都是没有选中的.这里进行初始化
             */
            for (int i = 0; i < currentBottles.size(); i++) {
                BottleModel currentBottleModel = currentBottles.get(i);
                boolean flag = true;
                if(currentBottleModel.DrugDetails==null){
                    UIHepler.showToast(context,"加载失败，请返回后刷新重试！");
                    btnOK.setEnabled(false);
                    Log.d(TAG,"currentBottleModel.DrugDetails==null");
                    return;
                }else {
                    for (int j = 0; j < currentBottleModel.DrugDetails.size() && flag; j++) {
                        DrugDetailModel drugDetailModel = currentBottleModel.DrugDetails.get(j);
                        if ((drugDetailModel != null && 1 == drugDetailModel.ReturnFlag) || currentBottles.get(i).BottleStatus >=2 ) {
                            flag = false;
                            break;
                        }
                    }
                    currentBottles.get(i).IsReturn = flag;
                    isCurrentConfigMaps.put(i, flag);
                }

            }
//                    if(RoleCategory.PAIYAO.getKey() == LocalSetting.RoleIndex){
//                        // 设置药品日期帅选。生成一个SpinnerAdapter
//                        SpinnerAdapter adapter = ArrayAdapter.createFromResource(this, R.array.check_exitdrug, android.R.layout.simple_spinner_dropdown_item);
//                        // 将ActionBar的操作模型设置为NAVIGATION_MODE_LIST
//                        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//                        // 为ActionBar设置下拉菜单和监听器
//                        actionBar.setListNavigationCallbacks(adapter, new DropDownListenser(bottles));
//                    }else {
//                        bottleAdapter = new BottleAdapter(context, bottles, bid, isCurrentConfigMaps);
//                    }
            bottleAdapter = new BottleAdapter(context, bottles, bid, isCurrentConfigMaps);
            listView.setAdapter(bottleAdapter);
        }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.bottle, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                finish();
                //overridePendingTransition(R.anim.dialog_out, R.anim.dialog_in);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void receiverPatientId(String patientId) {
        /**
         * 排药，穿刺界面不能扫描拼贴
         */
        InfusionUIHelper.showWarningDialog(context, "不能在此界面扫描病人信息!");
    }

    @Override
    protected void receiverBottleId(final String patientId, final String bottleId) {

        /**
         * 此医嘱执行界面可以继续扫拼贴 重新加载即可
         *
         * 清空pid,bid,iid
         */
        bid = bottleId;
        currentBottles = new ArrayList<BottleModel>();
        updateBottles = new ArrayList<BottleModel>();
        btnOK.setEnabled(false);

        BottleModel tempBottle = infusionDetailDAO.getBottleById(bid);

        if (tempBottle != null) {
            if(RoleCategory.PAIYAO.getKey() == LocalSetting.RoleIndex){
                if(tempBottle.BottleStatus == BottleStatusCategory.WAITINGHANDLE.getKey()){
                    currentBottles = preHandlerData(tempBottle);
                    loadData(currentBottles);
                }else {
                    InfusionUIHelper.showWarningDialog(context,"该组药已排药!");
                }
            }else {
                if(tempBottle.BottleStatus == BottleStatusCategory.HADHANDLE.getKey()){
                    currentBottles = preHandlerData(tempBottle);
                    loadData(currentBottles);
                }else {
                    InfusionUIHelper.showWarningDialog(context,"该组药已配液!");
                }
            }

        } else {
//            showLoadingDialog("正在查找瓶贴...");
//            if(RoleCategory.PAIYAO.getKey() == LocalSetting.RoleIndex){
//                InfoApi.getBottlesByPid(context, patientId,
//                        new RestListener<List<BottleModel>>() {
//                            @Override
//                            public void onSuccess(List<BottleModel> bottleModels) {
//                                dismissLoadingDialog();
//                                if (bottleModels.size() > 0) {
//                                    bottleDataHelper.clear();
//                                    bottleDataHelper.bulkInsert(bottleModels);
//
//                                    BottleModel tempBo = bottleDataHelper.getBottleById(bid);
//                                    if (tempBo != null) {
//                                        //currentBottles = handlerBottles(preHandlerData(tempBo));
//                                        if(tempBo.BottleStatus == BottleStatusCategory.WAITINGHANDLE.getKey()){
//                                            currentBottles = preHandlerData(tempBo);
//                                            loadData(currentBottles);
//                                        }else {
//                                            UIHelper.showWarningDialog("该组药已排药!");
//                                        }
//                                    } else {
//                                        UIHelper.showWarningDialog("未找到该瓶贴!");
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onFailed(AppException error, String msg) {
//                                dismissLoadingDialog();
//                                error.makeToast(context);
//                            }
//                        } );
//            }else {
//                InfoApi.GetBottleByBottleId(context, bottleId, new RestListener<BottleModel>() {
//                    @Override
//                    public void onSuccess(BottleModel bottleModel) {
//                        dismissLoadingDialog();
//                        if (bottleModel != null) {
//                            //            if(!bottleDataHelper.validate(bottleModel.BottleId))
//                            bottleDataHelper.removeById(bottleModel.BottleId);
//                            bottleDataHelper.insert(bottleModel);
//                            BottleModel tempBo = bottleDataHelper.getBottleById(bid);
//                            if (tempBo != null) {
//                                if(tempBo.BottleStatus == BottleStatusCategory.HADHANDLE.getKey()){
//                                    currentBottles = preHandlerData(tempBo);
//                                    loadData(currentBottles);
//                                }else {
//                                    UIHelper.showWarningDialog("该组药已配液!");
//                                }
//                            } else {
//                                UIHelper.showWarningDialog("未找到该瓶贴!");
//                            }
//                        } else {
//                            UIHelper.showLongToast("未获取到数据,请重试!");
//                        }
//                    }
//
//                    @Override
//                    public void onFailed(AppException error, String msg) {
//                        dismissLoadingDialog();
//                        error.makeToast(context);
//                    }
//                });
//            }
        }
    }

    @Override
    public void onDestroy() {
        LocalSetting.CurrentBottle = null;
        super.onDestroy();
    }
}
