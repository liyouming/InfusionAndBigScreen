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
package com.fugao.infusion.paiyao;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.fugao.infusion.model.InfusionEventModel;
import com.fugao.infusion.model.PatrolModel;
import com.fugao.infusion.model.QueueModel;
import com.fugao.infusion.utils.BeepManager;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.UIHelper;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.view.ComboBox;
import com.fugao.infusion.view.ProgressGenerator;
import com.fugao.infusion.view.processbutton.iml.ActionProcessButton;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.DateUtils;
import com.jasonchen.base.utils.ResourceUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import butterknife.InjectView;
/**
 * @Description: TODO 排药和配药执行界面  批量执行

 */

public class PaiyaoExecuteBatchActivity extends BaseScanTestTempleActivity {
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
    @InjectView(R.id.title)
    RelativeLayout title;
    @InjectView(R.id.cboEditReason)
    ComboBox cboEditReason;
    @InjectView(R.id.lll)
    LinearLayout lll;
    public List<BottleModel> currentBottles;
//    private List<BottleModel> updateBottles;
    private String bid;
    private BottleModel CurrentBottle;//当前瓶贴
    private PaiYaoBottleAdapter bottleAdapter = null;
    private BottleAdapter bottleAdapterByBatch = null;
    private  ActionBar actionBar;
//    private Map<Integer, Boolean> isCurrentConfigMaps;
    public int bottleTime;
    private String bottleTime1;
    private InfusionDetailDAO infusionDetailDAO;
    private String lastPatid =null;
    private ArrayList<String> bottleIds;
    private String[] inputReason;
//    public List<BottleModel> paiYaoBottle;
    private String inputText =null;
    private boolean isUseBatch =false;//是否使用批量排药
    private  Map<Integer, Boolean> checkedMap;
    private BeepManager beepManager;//呼叫声音

    private ProgressDialog loadingDialog;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_excute_batch);
    }

    @Override
    public void initView() {
        loadingDialog = new ProgressDialog(PaiyaoExecuteBatchActivity.this);
        beepManager = new BeepManager(PaiyaoExecuteBatchActivity.this);
        isUseBatch = XmlDB.getInstance(PaiyaoExecuteBatchActivity.this).getKeyBooleanValue("isUseBatch",false);
        CurrentBottle = LocalSetting.CurrentBottle;
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(context));
        currentBottles = new ArrayList<BottleModel>();
//        updateBottles = new ArrayList<BottleModel>();
        bid = LocalSetting.CurrentBottle.BottleId;
        bottleIds = new ArrayList<String>();
        checkedMap = new WeakHashMap<Integer, Boolean>();
        currentBottles = preHandlerData(LocalSetting.CurrentBottle);
        if(isUseBatch){
            lll.setVisibility(View.GONE);
        }else {
//            paiYaoBottle = new ArrayList<BottleModel>();
            lastPatid = LocalSetting.CurrentBottle.PeopleInfo.PatId;
            bottleIds.add(bid);
//            isCurrentConfigMaps = new HashMap<Integer, Boolean>();//保存扫描的瓶贴
            InitCombox();
        }
        loadData(currentBottles);
    }

    /**
     * 初始化combox
     */
    private void InitCombox() {
        if (RoleCategory.PAIYAO.getKey() == LocalSetting.RoleIndex) {
            inputReason = ResourceUtils.getResouce4Arrays(context, R.array.excute_handler_reason);
            cboEditReason.setFocusable(false);
            cboEditReason.getEditText().setEnabled(true);
            cboEditReason.getEditText().setTextColor(Color.BLACK);
            cboEditReason.setData(inputReason);
        }

    }
    @Override
    public void initListener() {
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

        /**
         * TODO 排药这个地方手动执行填写理由只要把第一个瓶贴加上上传的理由即可
         */
        cboEditReason.setListViewOnClickListener(new ComboBox.ListViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                inputText = cboEditReason.getEditText().getText().toString();
                if(!StringUtils.StringIsEmpty(inputText)){
                    btnOK.setEnabled(true);
                }else {
                    btnOK.setEnabled(false);
                }
            }
        });
        cboEditReason.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(StringUtils.StringIsEmpty(cboEditReason.getEditText().getText().toString())){
                    btnOK.setEnabled(false);
                }else {
                    btnOK.setEnabled(true);
                }
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
        String url ="";
        String Json ="";
        Map<Integer, Boolean> currentCheckMap =null;
        final List<BottleModel> updateBottles = new ArrayList<BottleModel>();
//        updateBottles.clear();
        final ProgressGenerator progressGenerator = new ProgressGenerator(btnOK);
        progressGenerator.start();
        ArrayList<BottleModel> isCheckBottles = new ArrayList<BottleModel>();
        for(int i = 0;i<currentBottles.size();i++){
            for(String bottleid:bottleIds){
                if(currentBottles.get(i).BottleId.equals(bottleid)){
                    isCheckBottles.add(currentBottles.get(i));
                }
            }
        }
        if(isUseBatch){
            currentCheckMap =bottleAdapterByBatch.getCheckMap();
            for (int i = 0; i < currentBottles.size(); i++) {
                if (currentCheckMap.size()>0 &&currentCheckMap.get(i)==true) {
                    BottleModel each = currentBottles.get(i);
                    each.BottleStatus = BottleStatusCategory.HADHANDLE.getKey();
                    each.PillCore = LocalSetting.CurrentAccount.UserName;
                    each.PillName = LocalSetting.CurrentAccount.FullName;
                    each.PillDate = DateUtils.getCurrentDate("yyyyMMdd");
                    each.PillTime = DateUtils.getCurrentDate("HHmmss");
                    each.CheckCore=LocalSetting.CurrentCheck.UserName;
                    each.CheckName=LocalSetting.CurrentCheck.FullName;
                    each.CheckDate = DateUtils.getCurrentDate("yyyyMMdd");
                    each.CheckTime = DateUtils.getCurrentDate("HHmmss");
                    updateBottles.add(each);
                }
            }
        }else {
            for (int i = 0; i < isCheckBottles.size(); i++) {
                BottleModel each = isCheckBottles.get(i);
                each.BottleStatus = BottleStatusCategory.HADHANDLE.getKey();
                each.PillCore = LocalSetting.CurrentAccount.UserName;
                each.PillName = LocalSetting.CurrentAccount.FullName;
                each.PillDate = DateUtils.getCurrentDate("yyyyMMdd");
                each.PillTime = DateUtils.getCurrentDate("HHmmss");
                each.CheckCore=LocalSetting.CurrentCheck.UserName;
                each.CheckName=LocalSetting.CurrentCheck.FullName;
                each.CheckDate = DateUtils.getCurrentDate("yyyyMMdd");
                each.CheckTime = DateUtils.getCurrentDate("HHmmss");
                updateBottles.add(each);
            }
        }
        if(updateBottles.size()>0){
            inputText = cboEditReason.getEditText().getText().toString();
            if(!isUseBatch && !StringUtils.StringIsEmpty(inputText)){
                PatrolModel patrolDetailBean = new PatrolModel();
                patrolDetailBean.Content = inputText;
                patrolDetailBean.PatrolerNo = LocalSetting.CurrentAccount.UserName;
                patrolDetailBean.PatrolerName = LocalSetting.CurrentAccount.FullName;
                patrolDetailBean.PatrolTime = DateUtils.getStandarCurrentDate();
                patrolDetailBean.TargetContent = "排药异常";
                patrolDetailBean.Expand = updateBottles.get(0).InfusionId;
                patrolDetailBean.Status = -1; //异常巡视
                patrolDetailBean.Type = 3;
                patrolDetailBean.AboutNo = updateBottles.get(0).PeopleInfo.PatId;
                ArrayList<PatrolModel> updatePatrol = new ArrayList<PatrolModel>();
                updatePatrol.add(patrolDetailBean);
                if(updateBottles.get(0).AboutPatrols ==null){
                    updateBottles.get(0).AboutPatrols =new ArrayList<PatrolModel>();
                    updateBottles.get(0).AboutPatrols.addAll(updatePatrol);
                }else {
                    updateBottles.get(0).AboutPatrols.addAll(updatePatrol);
                }
            }
            url = ChuanCiApi.url_updateBottles();
            Json = String2InfusionModel.bottleModles2String((ArrayList) updateBottles);
        }else {
            UIHepler.showToast(PaiyaoExecuteBatchActivity.this,"请选择需要排的药");
            progressGenerator.fail();
            btnOK.setEnabled(true);
            return;
        }
        RestClient.put(PaiyaoExecuteBatchActivity.this,url,Json,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                /**
                 * 更新本地信息
                 */
                infusionDetailDAO.updateLocalData((ArrayList<BottleModel>) updateBottles);
                progressGenerator.fail();
                btnOK.setEnabled(true);
                UIHepler.showToast(PaiyaoExecuteBatchActivity.this,"执行成功!");
                System.gc();//上传完成后回收垃圾
//                InfoUtils.sendSuccessBroadcast(context);
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
        List<BottleModel> bos = infusionDetailDAO.getBottleByPatid(currentBottle.PeopleInfo.PatId);
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
            if (BottleStatusCategory.WAITINGHANDLE.getKey() == bo.BottleStatus) {
                bos.add(bo);
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
           if(isUseBatch){
               btnOK.setEnabled(true);
           }else {
               btnOK.setEnabled(false);
           }
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
            }
        }
        if(isUseBatch){
            Map<Integer, Boolean> isCurrentConfigMaps = new HashMap<Integer, Boolean>();

            /**
             * 首先,默认情况下,所有项目都是没有选中的.这里进行初始化
             */
            for (int i = 0; i < currentBottles.size(); i++) {
                BottleModel currentBottleModel = currentBottles.get(i);
                boolean flag = true;//本来默认是选中的，这里要求都不选中，只有选中扫描到瓶贴
                if (currentBottleModel.DrugDetails == null) {
                    UIHepler.showToast(context, "加载失败，请返回后刷新重试！");
                    btnOK.setEnabled(false);
                    Log.d(TAG, "currentBottleModel.DrugDetails==null");
                    return;
                } else {
                    for (int j = 0; j < currentBottleModel.DrugDetails.size() && flag; j++) {
                        DrugDetailModel drugDetailModel = currentBottleModel.DrugDetails.get(j);
                        if ((drugDetailModel != null && 1 == drugDetailModel.ReturnFlag) || currentBottles.get(i).BottleStatus >= 2) {
                            flag = false;
                            break;
                        }
                    }
                    currentBottles.get(i).IsReturn = flag;
                    isCurrentConfigMaps.put(i, flag);
                }
                bottleAdapterByBatch = new BottleAdapter(context, bottles, bid, isCurrentConfigMaps);
                listView.setAdapter(bottleAdapterByBatch);
            }
        }else {
            setReturnDurgBottle(currentBottles);
            getCheckedMap(bottles);
            bottleAdapter = new PaiYaoBottleAdapter(context, bottles, bottleIds,checkedMap);
            listView.setAdapter(bottleAdapter);
        }
            }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void receiverPatientId(String patientId) {
        /**
         * 排药，穿刺界面不能扫描拼贴
         */
//        InfusionUIHelper.showWarningDialog(context, "不能在此界面扫描病人信息!");
        UIHelper.showWarningDialogByCustom(PaiyaoExecuteBatchActivity.this,"不能在此界面扫描病人信息!");
    }

    @Override
    protected void receiverBottleId(String patientId, String bottleId) {
        /**
         * 此医嘱执行界面可以继续扫拼贴 重新加载即可
         *
         * 清空pid,bid,iid
         */
        bid = bottleId;
        if(!isUseBatch){
            /**
             * 这个地方保存一下当前的门诊号与扫描的门诊号对比，如果不是同一个人
             * 的话就把保存的isCurrentConfigMaps清除一下
             */
            if(StringUtils.StringIsEmpty(lastPatid)){
                lastPatid = patientId;
            }
            if((!TextUtils.equals(lastPatid,patientId))){
                //                isCurrentConfigMaps.clear();
                //                bottleIds.clear();
                UIHelper.showWarningDialogByCustom(PaiyaoExecuteBatchActivity.this, "该瓶贴与当前患者处方信息不一致！");
                InfoUtils.warningComfirmError(PaiyaoExecuteBatchActivity.this,beepManager);
                InfusionEventModel infusionEventModel = new InfusionEventModel();//输液扫描核对错误日志Model
                ArrayList<InfusionEventModel> infusionEventModels = new ArrayList<InfusionEventModel>();
                infusionEventModel.AdditionId =lastPatid;
                infusionEventModel.Item = "核对";
                infusionEventModel.Memo = "排药核对";
                infusionEventModel.OperateId = LocalSetting.CurrentAccount.UserName;
                infusionEventModel.OperateName = LocalSetting.CurrentAccount.FullName;
                infusionEventModel.Status = 0;
                infusionEventModel.TargetId = bottleId;
                infusionEventModels.add(infusionEventModel);
                InfoUtils.recordConfirmError(PaiyaoExecuteBatchActivity.this,infusionEventModels);
                return;
            }
            if(!bottleIds.contains(bid)){
                bottleIds.add(bid);
            }
        }else {
            currentBottles = new ArrayList<BottleModel>();
//            List<BottleModel> updateBottles = new ArrayList<BottleModel>();
        }
        btnOK.setEnabled(false);
        BottleModel tempBottle = infusionDetailDAO.getBottleById(bid);
        if (tempBottle.BottleId != null) {
                if(tempBottle.BottleStatus == BottleStatusCategory.WAITINGHANDLE.getKey()){
//                    setReturnDurgBottle(currentBottles);
                    if(isUseBatch){
                        currentBottles = preHandlerData(tempBottle);
                        loadData(currentBottles);
                    }else {
                        getCheckedMap(currentBottles);
                        bottleAdapter.notifyDataSetChanged();
                        isCheckAll();
                    }
                }else {
                    UIHepler.showToast(context,"该组药已排药!");
            }

        } else {
            showLoadingDialog("正在查找瓶贴...");
            String url = ChuanCiApi.Url_GetBottlesByPid(patientId);
            RestClient.get(url,new BaseAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, String s) {
                    dismissLoadingDialog();
                    ArrayList<BottleModel> bottleModels = null;
                    try {
                        bottleModels = InfoUtils.jsonStringTOModel(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    List<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
                    if (bottleModels.size() > 0) {
                        BottleModel bottleModel = null;
                        for(BottleModel bottle :bottleModels ){
                            if(bid.equals(bottle.BottleId)){
                                bottleModel = bottle;
                            }
                        }
                        if(bottleModel !=null){
                            if(bottleModel.BottleStatus == BottleStatusCategory.WAITINGHANDLE.getKey()){
//                                setReturnDurgBottle(currentBottles);
                                if(isUseBatch){
                                    currentBottles = preHandlerData(bottleModel);
                                    loadData(currentBottles);
                                }else {
                                    getCheckedMap(currentBottles);
                                    bottleAdapter.notifyDataSetChanged();
                                    isCheckAll();
                                }
                            }else {
                                UIHepler.showToast(PaiyaoExecuteBatchActivity.this, "该组药已排药!");
                            }


                        }else {
                            UIHepler.showToast(PaiyaoExecuteBatchActivity.this,"未找到该瓶贴!");
                        }

                    }
                }

                @Override
                public void onFailure(int i, Throwable throwable, String s) {
                    dismissLoadingDialog();
                    UIHepler.showToast(PaiyaoExecuteBatchActivity.this,"查找失败");
                }
            });
        }
    }

    /**
     * 检查是否每组药都已经勾选上
     * @return
     */
    public void isCheckAll(){
        if(bottleIds.size()==getPaiYaoBottle()){
           btnOK.setEnabled(true);
            excute();
        }else {
            if(!StringUtils.StringIsEmpty(inputText)){
                btnOK.setEnabled(true);
            }else {
                btnOK.setEnabled(false);
            }
        }
    }
    /**
     * 获取这个人瓶贴状态为1的瓶贴集合
     */
    private int getPaiYaoBottle() {
//        paiYaoBottle.clear();
//        paiYaoBottle = null;
        int totleByStatus;
        List<BottleModel> paiYaoBottle = new ArrayList<BottleModel>();
        for(BottleModel each: currentBottles){
            if(each.BottleStatus == BottleStatusCategory.WAITINGHANDLE.getKey()){
                paiYaoBottle.add(each);
            }
        }
        totleByStatus = paiYaoBottle.size();
        return totleByStatus;
    }

    /**
     * 获得瓶贴选中的状态传进adapter
     */

    public Map<Integer,Boolean> getCheckedMap(List<BottleModel> bottles){
//        checkedMap.clear();
        for(int i = 0;i<bottles.size();i++){
            for(String bottleid:bottleIds){
                if(bottles.get(i).BottleId.equals(bottleid)){
                    if(bottles.get(i).IsReturn){
                        checkedMap.put(i,false);
                    }else {
                        checkedMap.put(i,true);
                    }
                    break;
                }else {
                    checkedMap.put(i,false);
                }
            }
        }
        return checkedMap;
    }

    /**
     * 如果该瓶贴药品中有退药就设置该瓶贴为退药瓶贴
     * @param currentBottles
     */
    public void setReturnDurgBottle(List<BottleModel> currentBottles){
        for (int i = 0; i < currentBottles.size(); i++) {
            BottleModel currentBottleModel = currentBottles.get(i);
            boolean flag = false;//本来默认是选中的，这里要求都不选中，只有选中扫描到瓶贴
            for (int j = 0; j < currentBottleModel.DrugDetails.size() && !flag; j++) {
                DrugDetailModel drugDetailModel = currentBottleModel.DrugDetails.get(j);
                if ((drugDetailModel != null && 1 == drugDetailModel.ReturnFlag) || currentBottles.get(i).BottleStatus >= 2) {
                    flag = true;
                    break;
                }
            }
            currentBottles.get(i).IsReturn = flag;
        }
    }

    @Override
    public void onDestroy() {
        checkedMap = null;
//      isCurrentConfigMaps =null;
        bottleIds.clear();
        bottleIds = null;
        currentBottles.clear();
        currentBottles= null;
        super.onDestroy();
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
