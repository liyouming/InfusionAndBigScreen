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

package com.fugao.infusion.zhongji;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseScanTestTempleActivity;
import com.fugao.infusion.chuaici.AreaInfusionEmptyActivity;
import com.fugao.infusion.chuaici.DrugAdapter;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.QueueStatusCategory;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.dao.DataBaseInfo;
import com.fugao.infusion.dao.InfusionDetailDAO;
import com.fugao.infusion.dao.UploadInfusionDetailDAO;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.DrugDetailModel;
import com.fugao.infusion.model.InfusionEventModel;
import com.fugao.infusion.model.PatrolModel;
import com.fugao.infusion.model.QueueModel;
import com.fugao.infusion.utils.BeepManager;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.InfusionUIHelper;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.WarningToast;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.view.ComboBox;
import com.fugao.infusion.view.PopMenu;
import com.fugao.infusion.view.ProgressGenerator;
import com.fugao.infusion.view.processbutton.iml.ActionProcessButton;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.DateUtils;
import com.jasonchen.base.utils.JacksonHelper;
import com.jasonchen.base.utils.ListViewUtils;
import com.jasonchen.base.utils.ResourceUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.utils.ViewUtils;
import com.jasonchen.base.view.UIHepler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @Description: TODO 穿刺和输液执行界面   单次执行
 */

public class ZhongjiChuancCiExecuteSingleActivity extends BaseScanTestTempleActivity {

    private static final String TAG = "Fast-ExecuteActivity";
    @InjectView(R.id.leftInfo)
    TextView leftInfoPatient;
    @InjectView(R.id.leftInfo2)
    TextView leftInfoDrug;
    @InjectView(R.id.rightInfo)
    TextView rightInfo;
    @InjectView(R.id.listView)
    ListView listView;
    @InjectView(R.id.btnOK)
    ActionProcessButton btnOK;
    @InjectView(R.id.btnCancel)
    ActionProcessButton btnCancel;
    @InjectView(R.id.divider)
    View divider;

    @InjectView(R.id.cbBottle)
    CheckBox cbBottle;
    @InjectView(R.id.cbBand)
    CheckBox cbBand;
    @InjectView(R.id.cbEnd)
    CheckBox cbEnd;
    @InjectView(R.id.cbSeat)
    CheckBox cbSeat;
    @InjectView(R.id.btnSeat)
    Button btnSeat;
    @InjectView(R.id.tvDs)
    TextView tvDs;
    @InjectView(R.id.tvUnit)
    TextView tvUnit;
    @InjectView(R.id.tvPc)
    TextView tvPc;
    @InjectView(R.id.tvYf)
    TextView tvYf;
    //  @InjectView(R.id.tvZh) TextView tvZh;
    /**
     * 显示时间
     */
    @InjectView(R.id.tvSj)
    TextView tvSj;

    @InjectView(R.id.tvEnd)
    TextView tvEnd;
    @InjectView(R.id.tvAllotSeat)
    TextView tvAllotSeat;

    @InjectView(R.id.lzzGcf)
    LinearLayout lzzGcf;
    @InjectView(R.id.lzz)
    TextView lzz;
    @InjectView(R.id.gcf)
    TextView gcf;
    @InjectView(R.id.lsh)
    TextView lsh;
    @InjectView(R.id.groupId)
    TextView groupId;
    @InjectView(R.id.callAginShow)
    LinearLayout callAginShow;
    @InjectView(R.id.cboEditReason)
    ComboBox cboEditReason;
    @InjectView(R.id.otherReason)
    LinearLayout otherReason;
    @InjectView(R.id.more)
    LinearLayout more;
    @InjectView(R.id.title_text_view)
    TextView mTitleTextView;
    @InjectView(R.id.tv_bottle)
    TextView mTvBottle;
    @InjectView(R.id.topLayout)
    LinearLayout mTopLayout;
    /* @InjectView(R.id.marqViewDrug)
     MarqueeView mMarqViewDrug;*/
    @InjectView(R.id.showMessage)
    TextView mShowMessage;
    @InjectView(R.id.contentLayout)
    ScrollView mContentLayout;
    @InjectView(R.id.handlerContent)
    LinearLayout mHandlerContent;
    @InjectView(R.id.action_call)
    LinearLayout mActionCall;
    @InjectView(R.id.action_print)
    LinearLayout mActionpPirnt;
    @InjectView(R.id.show)
    LinearLayout mShow;
    @InjectView(R.id.ssssss)
    RelativeLayout mSsssss;
    @InjectView(R.id.patient_status)
    RelativeLayout mPatient_status;
    @InjectView(R.id.checkInfo_layout)
    LinearLayout mCheckInfo_layout;
    @InjectView(R.id.excute_secces)
    TextView mExcute_secces;

    private String statusGroup;

    private BottleModel currentBottle;
    private MenuItem actionCallItem;
    private MenuItem actionSeatItem;
    private List<BottleModel> runningBottles;
    private BottleModel updateBottle;
    private QueueModel updateQueue;
    private boolean needEndOther = false;
    private MenuItem actionPrintSeatNo;//用于重打座位号
    private String deptID;
    private String areaID;
    public String[] Fq;
    public String divZone;
    private DrugAdapter drugAdapter;
    private SetNoReceiver setNoReceiver;
    private String punctureDeptId;//注射室
    private String[] inputReason;
    private InfusionDetailDAO infusionDetailDAO;
    private AlertDialog alertDialogOverDue;
    private PopMenu popMenu;
    private String[] strings;
    private boolean isExit;
//    private UnPutInfusionDetailDAO unPutInfusionDetailDAO;
    private UploadInfusionDetailDAO uploadInfusionDetailDAO;
    private boolean EXCUTEFLAG = true;
    private boolean OVERFLOWFLAG = false;
    private BottleModel loadModel;
    private boolean isUseSeatNo = false;//是否启用绑定座位
    /**
     * 用于装执行、过号了的瓶贴
     */
    private ArrayList<BottleModel> bottleModels = new ArrayList<BottleModel>();
    private BeepManager beepManager;//呼叫声音
    private String printModeString ="";
    private ProgressDialog loadingDialog;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_excute_zhongji_single);
        if(mDecodeManager ==null){
            Log.d("执行界面扫描服务mDecodeManager状态========================","销毁");
        }else {
            Log.d("执行界面扫描服务mDecodeManager状态========================","没有销毁");
        }
    }


    @Override
    public void initView() {
        printModeString = XmlDB.getInstance(ZhongjiChuancCiExecuteSingleActivity.this).getKeyString("printSetting","蓝牙打印");
        isUseSeatNo = XmlDB.getInstance(context).getKeyBooleanValue("isUseSeatNo", false);
        strings = ResourceUtils.getResouce4Arrays(ZhongjiChuancCiExecuteSingleActivity.this, R.array.infusionstatus_handexecute_chuanci);
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(ZhongjiChuancCiExecuteSingleActivity.this));
        InitCombox();
        deptID = XmlDB.getInstance(context).getKeyString("deptID", "100001");
        areaID = XmlDB.getInstance(context).getKeyString("areaID", "1");
        if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
            if(isUseSeatNo){
                mActionpPirnt.setVisibility(View.VISIBLE);
                if(printModeString.contains("蓝牙打印")){
                    InfoUtils.checkBlueTooth(ZhongjiChuancCiExecuteSingleActivity.this);
                }
                btnSeat.setEnabled(true);
            }
            cbEnd.setVisibility(View.GONE);
            cbSeat.setVisibility(View.GONE);
            tvEnd.setVisibility(View.GONE);
            tvAllotSeat.setVisibility(View.GONE);
//            btnCancel.setVisibility(QueueStatusCategory.FINISHED.getKey() == LocalSetting.CurrentBottle.PeopleInfo.Status ? View.GONE : View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
        }
        initBoradCast();
    }

    @Override
    public void initData() {
        loadingDialog = new ProgressDialog(ZhongjiChuancCiExecuteSingleActivity.this);
        beepManager = new BeepManager(ZhongjiChuancCiExecuteSingleActivity.this);
        punctureDeptId = XmlDB.getInstance(context).getKeyIntValue("punctureDeptId", 15) + "";
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(ZhongjiChuancCiExecuteSingleActivity.this));
        uploadInfusionDetailDAO = new UploadInfusionDetailDAO(DataBaseInfo.getInstance(ZhongjiChuancCiExecuteSingleActivity.this));
        statusGroup = InfoUtils.getAllStatusGroup(LocalSetting.RoleIndex);
        /**
         * 通过pid或者iid查找该人所有瓶贴
         */
        loadBottle(LocalSetting.CurrentBottle);

    }
    private void initEndAndSeatLayouts(BottleModel bottle) {
        runningBottles = new ArrayList<BottleModel>();
        /**
         * 如果没有输液中...隐藏 结束上一瓶
         */
        if (RoleCategory.SHUYE.getKey() == LocalSetting.RoleIndex) {
            List<BottleModel> allBottles = infusionDetailDAO.getBottleByPatid(bottle.PeopleInfo.PatId);
            for (BottleModel bo : allBottles) {
                if (BottleStatusCategory.INFUSIONG.getKey() == bo.BottleStatus) {
                    bo.BottleStatus = BottleStatusCategory.HADINFUSE.getKey();
                    bo.InfusionCore = LocalSetting.CurrentAccount.Id;
                    bo.InfusionDate = DateUtils.getCurrentDate("yyyyMMdd");
                    bo.InfusionTime = DateUtils.getCurrentDate("HHmmss");
                    runningBottles.add(bo);
                }
            }
            cbEnd.setChecked(runningBottles.size() > 0);

            /**
             * 是否是最后一次  最后一次自动释放座位
             */
            //if (finishedBottles.size() + 1 == allBottles.size()) {
            //  cbSeat.setChecked(false);
            //  cbSeat.setVisibility(View.GONE);
            //  tvAllotSeat.setVisibility(View.GONE);
            //} else {
            //  cbSeat.setChecked(true);
            //  cbSeat.setVisibility(View.VISIBLE);
            //  tvAllotSeat.setVisibility(View.VISIBLE);
            //}
        }
    }

    @Override
    public void initListener() {

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 执行提交操作，和服务器进行交互
                 */
                btnOK.setEnabled(false);
                excute();
//                excute();
//                btnOK.setEnabled(cbBottle.isChecked() && cbBand.isChecked());
//                WarningToast.newInstance(ChuancCiExecuteSingleActivity.this, "录入其他理由成功");
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        btnSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAllotSeat();//分配座位
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopMenu();
            }
        });
        mTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mActionCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //呼叫
//                doCallHer();
                doCallHerByHandle();
            }
        });
        mActionpPirnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isUseSeatNo){
                    if(printModeString.contains("蓝牙打印"))doPrintSeatNo();
                }
            }
        });
        cboEditReason.setListViewOnClickListener(new ComboBox.ListViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final PatrolModel patrolDetailBean = new PatrolModel();
                String inputText = cboEditReason.getEditText().getText().toString();

                if(!StringUtils.StringIsEmpty(inputText)){
                    patrolDetailBean.TargetContent = "穿刺异常";
                    patrolDetailBean.BottleId = LocalSetting.CurrentBottle.BottleId;
                    patrolDetailBean.PatrolerNo = LocalSetting.CurrentAccount.UserName;
                    patrolDetailBean.PatrolerName = LocalSetting.CurrentAccount.FullName;
                    patrolDetailBean.PatrolTime = DateUtils.getStandarCurrentDate();
                    patrolDetailBean.Content = inputText;
//                    patrolDetailBean.Expand = inputText;
                    patrolDetailBean.Status = -1; //异常巡视
                    patrolDetailBean.Type =2;
                    patrolDetailBean.AboutNo = LocalSetting.CurrentBottle.PeopleInfo.PatId;
                    ArrayList<PatrolModel> updatePatrol = new ArrayList<PatrolModel>();
                    updatePatrol.add(patrolDetailBean);
                    if(LocalSetting.CurrentBottle.AboutPatrols ==null){
                        LocalSetting.CurrentBottle.AboutPatrols =new ArrayList<PatrolModel>();
                        LocalSetting.CurrentBottle.AboutPatrols.addAll(updatePatrol);
                    }else {
                        LocalSetting.CurrentBottle.AboutPatrols.addAll(updatePatrol);
                    }
                }
                btnOK.setEnabled(true);
            }
        });
    }

    /**
     * 初始化combox
     */
    private void InitCombox() {
        if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex ||RoleCategory.ZHONGJI.getKey() == LocalSetting.RoleIndex) {
//            if (QueueStatusCategory.FINISHED.getKey() == LocalSetting.CurrentBottle.PeopleInfo.Status) {
                otherReason.setVisibility(View.VISIBLE);
                inputReason = ResourceUtils.getResouce4Arrays(context, R.array.arround_unnormal_reason);
                //sinputReason.
                cboEditReason.setFocusable(false);
                cboEditReason.getEditText().setEnabled(false);
                cboEditReason.getEditText().setTextColor(Color.BLACK);
                cboEditReason.setData(inputReason);
                //cboEditReason.setTextDirection();
//            } else {
//                otherReason.setVisibility(View.GONE);
//            }
        } else {
            otherReason.setVisibility(View.GONE);
        }

    }

    @Override
    public void initIntent() {

    }


    /**
     * 取消穿刺操作 释放座位 并 销毁页面
     */
    private void cancel() {

        final ProgressGenerator progressGenerator = new ProgressGenerator(btnCancel);
        progressGenerator.start();
        RestClient.get(ChuanCiApi.url_getPostCall(currentBottle.PeopleInfo.DepartmentId, currentBottle.InfusionId, punctureDeptId), new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                progressGenerator.fail();
                UIHepler.showToast(context, "过号成功!");
                mExcute_secces.setText(currentBottle.PeopleInfo.Name+"过号成功");
                leftInfoPatient.setTextColor(getResources().getColor(R.color.red));
                currentBottle.PeopleInfo.OverCall = 1;
                infusionDetailDAO.updatePeople(currentBottle);
//                excuteAfter(true);
                ZhongjiChuancCiExecuteSingleActivity.this.finish();
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                progressGenerator.fail();
                UIHepler.showToast(context, "过号失败!");
            }
        });
        //    InfoApi.releaseSeat(context, currentBottle.InfusionId, new RestListener<String>() {
        //      @Override public void onSuccess(String s) {
        //        progressGenerator.success();
        //        closeActivity();
        //      }
        //
        //      @Override public void onFailed(AppException error, String msg) {
        //        progressGenerator.fail();
        //        UIHelper.showToast("释放座位失败!");
        //      }
        //    });
    }

    /**
     * 执行提交操作，和服务器进行交互
     */
    private void excute() {

        final ProgressGenerator progressGenerator = new ProgressGenerator(btnOK);
        progressGenerator.start();
        final List<BottleModel> newBottles = new ArrayList<BottleModel>();
            currentBottle.BottleStatus = BottleStatusCategory.INFUSIONG.getKey();
            currentBottle.InfusionCore = LocalSetting.CurrentAccount.UserName;
            currentBottle.InfusionName = LocalSetting.CurrentAccount.FullName;
            currentBottle.InfusionDate = DateUtils.getCurrentDate("yyyyMMdd");
            currentBottle.InfusionTime = DateUtils.getCurrentDate("HHmmss");
            currentBottle.PeopleInfo.Status = QueueStatusCategory.FINISHED.getKey();
            newBottles.add(currentBottle);
//        infusionDetailDAO.updateBottleIsUpload((ArrayList)newBottles);
//        progressGenerator.fail();

        String acountId = XmlDB.getInstance(ZhongjiChuancCiExecuteSingleActivity.this).getKeyString("AcountId","");
        String url = ChuanCiApi.url_updateBottlesAndQueues(acountId);
        String postData = JacksonHelper.model2String(newBottles);
        RestClient.put(ZhongjiChuancCiExecuteSingleActivity.this, url,postData,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                progressGenerator.fail();
                infusionDetailDAO.updateLocalData((ArrayList<BottleModel>) newBottles);
                btnOK.setEnabled(true);
                UIHepler.showToast(context,"执行成功!");
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                progressGenerator.fail();
                btnOK.setEnabled(true);
                btnOK.setText("提交失败，请重试");
            }
        });
//        if (!currentBottle.GCF.equals("重症")) {
//            if (!StringUtils.StringIsEmpty(currentBottle.PeopleInfo.SeatNo)) {
//                if(isUseSeatNo){
//                    if(printModeString.contains("蓝牙打印")){
//                        if(StringUtils.StringIsEmpty(divZone)){
//                            divZone =LocalSetting.divZone; //重新穿刺的时候区域和座位为空
//                        }
//                        InfoUtils.bluetoothPrintSeatNo(context, divZone+":"+currentBottle.PeopleInfo.SeatNo);
//                    }
//                }
//            } else {
//                 // UIHelper.showToast("座位为空，无法打印！");
//            }
//        }else {
//                //  UIHelper.showToast("重症病人，无法打印座位！");
//        }
        finish();
    }



    private void loadBottle(BottleModel bottle) {
        currentBottle = bottle;
        if (currentBottle == null) {
            btnSeat.setEnabled(false);
        } else {
//            if (currentBottle.GCF.equals("重症")) {
//                btnSeat.setVisibility(View.GONE);
//            } else {
//                btnSeat.setVisibility(View.VISIBLE);
//            }
        }

        if (currentBottle == null) {
            btnSeat.setEnabled(false);
        } else {
            if (StringUtils.StringIsEmpty(bottle.PeopleInfo.SeatNo)) {
                if(!isUseSeatNo){
                    btnSeat.setVisibility(View.GONE);
                } else{
                    btnSeat.setText("还未分配床位");
                }
                btnSeat.setText("还未分配床位");
            } else {
                btnSeat.setText(bottle.PeopleInfo.SeatNo);
            }

            cbBottle.setChecked(LocalSetting.IsOpenByScan);
            leftInfoPatient.setText(
                    currentBottle.PeopleInfo.Name
                            + "("
                            + currentBottle.PeopleInfo.PatId
                            + ") "
                            + (currentBottle.PeopleInfo.Sex == 1 ? "男" : "女")
                            + " "
                            + currentBottle.PeopleInfo.Age
                            + " "
                            + currentBottle.PeopleInfo.Weight

            );
            if(currentBottle.PeopleInfo.OverCall == 1){
                leftInfoPatient.setTextColor(getResources().getColor(R.color.red));
            }else {
                leftInfoPatient.setTextColor(getResources().getColor(R.color.rbm_menu_background));
            }
            if (StringUtils.getString(currentBottle.GCF).equals("重症")
                    || !StringUtils.StringIsEmpty(currentBottle.LZZ)
                    || !StringUtils.StringIsEmpty(currentBottle.PeopleInfo.DrugAllergy)) {
                lzzGcf.setVisibility(View.VISIBLE);
            }
            if (StringUtils.getString(currentBottle.GCF).equals("重症")) {
                gcf.setText(currentBottle.GCF);
            }
            if (!StringUtils.StringIsEmpty(currentBottle.LZZ)) {
                lzz.setText(currentBottle.LZZ);
            }
            if (!StringUtils.StringIsEmpty(currentBottle.PeopleInfo.DrugAllergy)) {
                rightInfo.setText(currentBottle.PeopleInfo.DrugAllergy);

            }
            if (currentBottle.DrugDetails == null) {
                UIHepler.showToast(context, "加载失败，请返回后刷新重试！");

                Log.d(TAG, "currentBottle.DrugDetails==null");
                return;
            } else {
                lsh.setText("流水号" + StringUtils.getString(currentBottle.PeopleInfo.QueueNo));
                groupId.setText(StringUtils.getString(currentBottle.GroupId));
                //    rightInfo.setText(currentBottle.PeopleInfo.DrugAllergy);
                //    leftInfoDrug.setText(currentBottle.PeopleInfo.DrugAllergy);
                tvDs.setText(StringUtils.StringIsEmpty(currentBottle.TransfusionSpeed) ? "" : currentBottle.TransfusionSpeed);
                tvUnit.setText(StringUtils.StringIsEmpty(currentBottle.SpeedUnit) ? "" : currentBottle.SpeedUnit);
                tvPc.setText(StringUtils.StringIsEmpty(currentBottle.Frequency) ? "" : currentBottle.Frequency);
                tvYf.setText(StringUtils.StringIsEmpty(currentBottle.Way) ? "" : currentBottle.Way);
                //tvYf.setTextColor(Color.BLACK);
                tvSj.setText(StringUtils.StringIsEmpty(currentBottle.RegistrationDate) ? "" : DateUtils.getMMddHHmm(currentBottle.RegistrationDate));
                //tvSj.setTextColor(Color.BLACK);
                initEndAndSeatLayouts(bottle);
                drugAdapter = new DrugAdapter(context, bottle.DrugDetails);
                listView.setAdapter(drugAdapter);
                ListViewUtils.setListViewHeightBasedOnChildren(listView);
                if (RoleCategory.ZHONGJI.getKey() == LocalSetting.RoleIndex) {
                    if (QueueStatusCategory.WAITING.getKey() == currentBottle.PeopleInfo.Status) {
                        doCallHer();
                    } else if (QueueStatusCategory.CALLED.getKey() == currentBottle.PeopleInfo.Status) {
//                        doCallHer();第二次不掉接口
                    } else if (QueueStatusCategory.FINISHED.getKey() == currentBottle.PeopleInfo.Status) {
                        UIHepler.showToast(ZhongjiChuancCiExecuteSingleActivity.this, "重新穿刺病人"); //不呼叫
                    }
                }
            }

        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 蓝牙打印座位号
     */
    private void doPrintSeatNo() {
        InfoUtils.checkBlueTooth(ZhongjiChuancCiExecuteSingleActivity.this);
        if (StringUtils.StringIsEmpty(currentBottle.PeopleInfo.SeatNo)) {
            UIHepler.showToast(context, "还未分配床位，无法打印");
            return;
        }
//        if(StringUtils.StringIsEmpty(divZone)){
//            divZone =LocalSetting.divZone;
//        }
        String PeopleContent=currentBottle.PeopleInfo.Name +":" +currentBottle.PeopleInfo.PatId ;
        InfoUtils.bluetoothPrintSeatNo(context, "重急区" + ":" + currentBottle.PeopleInfo.SeatNo,PeopleContent);

    }

    /**
     * 手动录入其它信息
     */
    private void doManualOther() {
        if (cbBand.isChecked() && cbBottle.isChecked()) {
            UIHepler.showToast(context, "已核对成功,无需再次核对!");
        } else {
            showEditTextDialog(2, "请录入其它理由");
        }
    }

    /**
     * 手动录入腕带
     */
    private void doManualBand() {
        if (cbBand.isChecked()) {
            UIHepler.showToast(context, "已找到该人,无需重复录入其病人门诊号!");
        } else {
            showEditTextDialog(1, "请录入门诊号或流水号");
        }
    }

    /**
     * 手动录入拼贴号
     */
    private void doManualBottle() {
        if (cbBottle.isChecked()) {
            UIHepler.showToast(context, "已找到瓶贴,无需重复录入瓶贴ID!");
        } else {
            showEditTextDialog(0, "请录入瓶贴ID");
        }
    }

    /**
     * 弹出手动录入对话框 0-拼贴 1-腕带 2-其它
     */
    private void showEditTextDialog(final int type, String title) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_edittext_layout, null);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_edittext);
        LinearLayout view_height = (LinearLayout) view.findViewById(R.id.dialog_view);
        view_height.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40));
        if (type == 0 || type == 1) editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        new AlertDialog.Builder(context).setTitle(title)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputText = editText.getText().toString();
                        if (StringUtils.StringIsEmpty(inputText)) {
                            editText.setError("不能输入为空，请重新输入！");
                            dialogIsClose(dialog, false);
                        } else {
                            dialogIsClose(dialog, true);
                            switch (type) {
                                case 0:
                                    handlerManualDialog(dialog, inputText);
                                    if (inputText.equals(currentBottle.BottleId)) {
                                        cbBottle.setChecked(true);
                                        btnOK.setEnabled(cbBottle.isChecked() && cbBand.isChecked());
                                    } else {
                                        cbBottle.setChecked(false);
                                        InfusionUIHelper.showWarningDialogByCustom(context, "瓶贴ID录入错误!");
                                        //                    UIHelper.showWarningDialog(context, "瓶贴ID录入错误!");
                                    }
                                    break;
                                case 1:
                                    handlerManualDialog(dialog, inputText);
                                    if (!cbBottle.isChecked()) {
                                        UIHepler.showToast(context, "请先录入瓶贴ID!");
                                    } else {
                                        if (inputText.equals(currentBottle.PeopleInfo.QueueNo)
                                                || inputText.equals(currentBottle.PeopleInfo.PatId)) {

                                            cbBand.setChecked(true);
                                            if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
                                                if (QueueStatusCategory.FINISHED.getKey() == currentBottle.PeopleInfo.Status) {
                                                    cboEditReason.showPopuWindow();
                                                    cboEditReason.setListViewOnClickListener(new ComboBox.ListViewItemClickListener() {
                                                        @Override
                                                        public void onItemClick(int position) {
                                                            cboEditReason.getEditText().setText(inputReason[position]);
                                                            final PatrolModel patrolDetailBean = new PatrolModel();
                                                            String inputText = cboEditReason.getEditText().getText().toString();
                                                            patrolDetailBean.TargetContent = "穿刺异常";
                                                            patrolDetailBean.BottleId = LocalSetting.CurrentBottle.BottleId;
                                                            patrolDetailBean.PatrolerNo = LocalSetting.CurrentAccount.UserName;
                                                            patrolDetailBean.PatrolerName = LocalSetting.CurrentAccount.FullName;
                                                            patrolDetailBean.PatrolTime = DateUtils.getStandarCurrentDate();
                                                            patrolDetailBean.Content = inputText;
//                                                            patrolDetailBean.Expand = inputText;
                                                            patrolDetailBean.Status = -1; //正常巡视
                                                            patrolDetailBean.Type = 2;
                                                            patrolDetailBean.AboutNo = LocalSetting.CurrentBottle.PeopleInfo.PatId;
                                                            ArrayList<PatrolModel> updatePatrol = new ArrayList<PatrolModel>();
                                                            updatePatrol.add(patrolDetailBean);

                                                            String jsonString = String2InfusionModel.patrolModels2String(updatePatrol);
                                                            RestClient.put(context, InfoApi.POST_PATROL, jsonString, new BaseAsyncHttpResponseHandler() {
                                                                @Override
                                                                public void onStart() {
                                                                    super.onStart();
                                                                }

                                                                @Override
                                                                public void onSuccess(int i, String s) {
                                                                    UIHepler.showToast(context, "录入穿刺理由成功");
                                                                }

                                                                @Override
                                                                public void onFailure(int i, Throwable throwable, String s) {
                                                                    UIHepler.showToast(context, "录入穿刺理由失败");
                                                                }
                                                            });


                                                        }
                                                    });
                                                }
                                            }
                                            /**
                                             * 执行提交操作，和服务器进行交互  腕带扫描核对成功了之后就直接提交不让他手动点击
                                             */
                                            btnOK.setEnabled(false);
                                            excute();
                                            btnOK.setEnabled(cbBottle.isChecked() && cbBand.isChecked());
                                            WarningToast.newInstance(ZhongjiChuancCiExecuteSingleActivity.this, "核对病人信息成功");

                                        } else {
                                            cbBand.setChecked(false);
                                            InfusionUIHelper.showWarningDialogByCustom(context, "病人ID录入错误!");
                                            InfoUtils.warningComfirmError(ZhongjiChuancCiExecuteSingleActivity.this,beepManager);
                                            InfusionEventModel infusionEventModel = new InfusionEventModel();//输液扫描核对错误日志Model
                                            ArrayList<InfusionEventModel> infusionEventModels = new ArrayList<InfusionEventModel>();
                                            infusionEventModel.AdditionId =inputText;
                                            infusionEventModel.Item = "核对";
                                            infusionEventModel.Memo = "穿刺核对";
                                            infusionEventModel.OperateId = LocalSetting.CurrentAccount.UserName;
                                            infusionEventModel.OperateName = LocalSetting.CurrentAccount.FullName;
                                            infusionEventModel.Status = 0;
                                            infusionEventModel.TargetId = currentBottle.BottleId;
                                            infusionEventModels.add(infusionEventModel);
                                            InfoUtils.recordConfirmError(ZhongjiChuancCiExecuteSingleActivity.this,infusionEventModels);
                                            //UIHelper.showWarningDialog(context, "病人ID录入错误!");
                                        }
                                    }
                                    break;
                                case 2:
                                    if (currentBottle.DrugDetails == null) {
                                        UIHepler.showToast(context, "加载失败，请返回后刷新重试！");
                                        btnOK.setEnabled(false);
                                        return;
                                    } else {
                                        btnOK.setEnabled(true);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogIsClose(dialog, true);
                    }
                })
                .create()
                .show();
    }

    /**
     * 检测录入格式是否为数字
     *
     * @param input 原始录入
     */
    private void handlerManualDialog(DialogInterface dialog, String input) {
        if (StringUtils.isNumeric(input)) {
            dialogIsClose(dialog, true);
        } else {
            dialogIsClose(dialog, false);
            UIHepler.showToast(context, "输入的格式错误，请重新再输入！");
        }
    }

    /**
     * 点击对话框按钮是否消失
     */
    public void dialogIsClose(DialogInterface dialog, boolean isClose) {
        try {
            Field field = null;
            field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, isClose); // 设定为false,则不可以关闭对话框
            dialog.dismiss();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分配座位
     */
    private void doAllotSeat() {
        /**
         * 此if分支是自动分配座位
         */
        if (LocalSetting.AutoAllotSeat) {
            showLoadingDialog("正在分配座位...");
            String url = ChuanCiApi.url_updateSeat(LocalSetting.DepartmentID, currentBottle.InfusionId, "", 1, areaID);
            RestClient.get(url, new BaseAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, String s) {
                    dismissLoadingDialog();
                    String seatMessage = "";
                    if (s.startsWith("\"") && s.endsWith("\"")) {
                        seatMessage = s.replaceAll("\"", "");
                    }else {
                        seatMessage =s;
                    }
                    if ("none".equals(seatMessage)) {
                        //            UIHelper.showWarningDialog("没有剩余座位,请稍后重试!");
                    } else if("has".equals(seatMessage)){
                        //            UIHelper.showWarningDialog("该座位已经被占用,请重新分配!");
                    }else {
                        /**
                         * 更新数据库
                         */

                        UIHepler.showToastInCenter(context, "分配了" + seatMessage);
                        btnSeat.setText(seatMessage);
                        currentBottle.PeopleInfo.SeatNo = seatMessage;
                        infusionDetailDAO.updateBottle(currentBottle);
                    }
                }

                @Override
                public void onFailure(int i, Throwable throwable, String s) {
                    dismissLoadingDialog();
                }
            });
        } else {
            /**
             * 上面的if是自动分配座位
             * 这里是手动分配座位了
             * 加载空座位列表 选一分配
             */
            //      showLoadingDialog("正在加载区域空座位列表...");
            Intent intent = new Intent();
            intent.setClass(ZhongjiChuancCiExecuteSingleActivity.this, AreaInfusionEmptyActivity.class);
            startActivityForResult(intent, 100);
            //   openActivityForResult(new Intent();
            //        InfoApi.getEmptySeats(context, areaID,new RestListener<List<String>>() {
            //        @Override public void onSuccess(List<String> emptySeats) {
            //          dismissLoadingDialog();//从服务器中拿到了空座位信息后就取消加载的对话框
            //          showSeatList(emptySeats);//然后显示座位列表
            //        }
            //
            //        @Override public void onFailed(AppException error, String msg) {
            //          dismissLoadingDialog();
            //          UIHelper.showLongToast(context, "加载空座位列表失败:" + error.getToastInfo(context));
            //        }
            //      });
        }
    }


    /**
     * 护士主动呼叫其来穿刺
     * 逻辑：如果呼叫次数>设置的最大呼叫次数，则提示不能继续呼叫了
     */

    private void doCallHer() {
        /**
         *如果当前的人物信息状态是等待状态
         */
        if (QueueStatusCategory.WAITING.getKey() == currentBottle.PeopleInfo.Status) {
            updateQueue = currentBottle.PeopleInfo;
            updateQueue.Status = QueueStatusCategory.CALLED.getKey();
            String changeQueueNo = StringUtils.getString(updateQueue.QueueNo);
            String name = StringUtils.getString(updateQueue.Name);
//            showLoadingDialog(name + ":正在呼叫" + "[" + changeQueueNo + "]" + "...");
            String jsonString = String2InfusionModel.quequeModel2String(updateQueue);
            RestClient.put(context, ChuanCiApi.POST_INFUSIONQUEUETOFORZHONGJI, jsonString, new BaseAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, String s) {
                    dismissLoadingDialog();
//                    btnSeat.setVisibility(View.GONE);
                    if ("none".equals(s)) {
                        return;
                    }
                    if ("error".equals(s)) {
                        //  UIHelper.showToast("座位号分配失败");
                        return;
                    }
                    if(s !=null && s.contains("OK")){
                        currentBottle.PeopleInfo = updateQueue;
                        infusionDetailDAO.updatePeople(currentBottle);
                    }
                }

                @Override
                public void onFailure(int i, Throwable throwable, String s) {
                    UIHepler.showToast(ZhongjiChuancCiExecuteSingleActivity.this,"呼叫失败");
                    dismissLoadingDialog();
                }
            });

        } else if (QueueStatusCategory.CALLED.getKey() == currentBottle.PeopleInfo.Status) {
            updateQueue = currentBottle.PeopleInfo;
            //      if(currentBottle.PeopleInfo.callCount >2)return;
            ViewUtils.setVisible(callAginShow);
            RestClient.get(ChuanCiApi.url_getCallForOverOne(LocalSetting.DepartmentID,
                    currentBottle.InfusionId, punctureDeptId), new BaseAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, String s) {
                    dismissLoadingDialog();
                    if (!currentBottle.GCF.equals("重症")) {
                        //                if ("none".equals(s)) {(服务器返回的是““none””，所以永远不会相等)
                        if (s.contains("none")) {

                            //                    UIHelper.showToast("当前没有空座位,请稍后手动分配!");
                            //                    UIHelper.showWarningDialog("当前没有空座位,请稍后手动分配!");
                            return;
                        }
                        Fq = s.replace("\"", "").split(",");
                        String name = StringUtils.getString(updateQueue.Name);
                        String changeQueueNo = StringUtils.getString(updateQueue.QueueNo);
                        UIHepler.showToast(context, name + ": [" + changeQueueNo + "]" + "呼叫成功，分配了" + Fq[0] + "座位!");
                        /**
                         * 如果启用座位绑定人的话，就显示座位打印座位号
                         */
                        if(isUseSeatNo){
                            btnSeat.setText(Fq[0]);
                        }else {
                            btnSeat.setVisibility(View.GONE);
                        }
                        currentBottle.PeopleInfo.SeatNo = Fq[0].replace("号", " ").trim();//用于蓝牙打印座位，
                        divZone = Fq[1];
                        LocalSetting.divZone = Fq[1];
                        /**
                         * 数据库更新病人信息(状态及呼叫次数)
                         */
                        currentBottle.PeopleInfo = updateQueue;

                        infusionDetailDAO.updatePeople(currentBottle);
                    }
                }

                @Override
                public void onFailure(int i, Throwable throwable, String s) {
                    dismissLoadingDialog();
                }
            });

        }else {
            UIHepler.showToast(ZhongjiChuancCiExecuteSingleActivity.this,"当前病人已完成穿刺,无需再进行呼叫");
        }
    }
    /**ggf
     * 手动执行呼叫
     */
    private void doCallHerByHandle() {
        if (currentBottle.PeopleInfo.callCount > LocalSetting.CallMaxCount) {
            UIHepler.showToast(context, "超过最大呼叫次数,不能继续呼叫!");
            return;
        }
        if (currentBottle.PeopleInfo.callCount > 2) return;
        ViewUtils.setVisible(callAginShow);
        RestClient.get(ChuanCiApi.url_getCallByHand(LocalSetting.DepartmentID, currentBottle.InfusionId, punctureDeptId), new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                /**
                 * 如果呼叫成功，则拿去服务器返回的数据s  并将其转化为整数型
                 * 并以此来保存呼叫的次数
                 * 更新瓶贴信息
                 * 如果呼叫的次数=最大的呼叫次数
                 * 则提示不能呼叫了
                 */
                dismissLoadingDialog();
                currentBottle.PeopleInfo.callCount = Integer.parseInt(s);
                infusionDetailDAO.updateBottle(currentBottle);
                UIHepler.showToastInCenter(context, "再次呼叫成功!");
                if (currentBottle.PeopleInfo.callCount == LocalSetting.CallMaxCount) {
                    ViewUtils.setGone(mActionCall);
                    InfusionUIHelper.showWarningDialogByCustom(context, "不能继续呼叫了");
                    //            UIHelper.showWarningDialog("不能继续呼叫了");
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                dismissLoadingDialog();
            }
        });
    }

    @Override
    protected void receiverPatientId(String patientId) {
        /**
         * 核对病人信息
         */
        if (currentBottle.PeopleInfo.PatId.equals(patientId)) {
            cbBand.setChecked(true);

            if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
                if (QueueStatusCategory.FINISHED.getKey() == currentBottle.PeopleInfo.Status) {
                    cboEditReason.showPopuWindow();
                    cboEditReason.setListViewOnClickListener(new ComboBox.ListViewItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            cboEditReason.getEditText().setText(inputReason[position]);
                            final PatrolModel patrolDetailBean = new PatrolModel();
                            String inputText = cboEditReason.getEditText().getText().toString();
                            patrolDetailBean.TargetContent = "穿刺异常";
                            patrolDetailBean.BottleId = LocalSetting.CurrentBottle.BottleId;
                            patrolDetailBean.PatrolerNo = LocalSetting.CurrentAccount.UserName;
                            patrolDetailBean.PatrolerName = LocalSetting.CurrentAccount.FullName;
                            patrolDetailBean.PatrolTime = DateUtils.getStandarCurrentDate();
                            patrolDetailBean.Content = inputText;
//                            patrolDetailBean.Expand = inputText;
                            patrolDetailBean.Status = -1; //正常巡视
                            patrolDetailBean.Type =2;
                            patrolDetailBean.AboutNo = LocalSetting.CurrentBottle.PeopleInfo.PatId;
                            ArrayList<PatrolModel> updatePatrol = new ArrayList<PatrolModel>();
                            updatePatrol.add(patrolDetailBean);
                            String jsonString = String2InfusionModel.patrolModels2String(updatePatrol);
                            RestClient.put(context, InfoApi.POST_PATROL, jsonString, new BaseAsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int i, String s) {
                                    /**
                                     * 执行提交操作，和服务器进行交互  腕带扫描核对成功了之后就直接提交不让他手动点击
                                     */
                                    btnOK.setEnabled(false);
                                    excute();
                                    UIHepler.showToast(context, "录入穿刺理由成功");
                                }

                                @Override
                                public void onFailure(int i, Throwable throwable, String s) {
                                    UIHepler.showToast(context, "录入穿刺理由失败");
                                }
                            });
                        }
                    });
                } else {

                    /**
                     * 执行提交操作，和服务器进行交互  腕带扫描核对成功了之后就直接提交不让他手动点击
                     */
                    btnOK.setEnabled(false);
                    excute();
                    btnOK.setEnabled(cbBottle.isChecked() && cbBand.isChecked());
                    WarningToast.newInstance(ZhongjiChuancCiExecuteSingleActivity.this, "核对病人信息成功");
                }
            } else {
                /**
                 * 执行提交操作，和服务器进行交互  腕带扫描核对成功了之后就直接提交不让他手动点击
                 */
                btnOK.setEnabled(false);
                excute();
                btnOK.setEnabled(cbBottle.isChecked() && cbBand.isChecked());
                WarningToast.newInstance(ZhongjiChuancCiExecuteSingleActivity.this, "核对病人信息成功");
            }
        } else {
            cbBand.setChecked(false);
            TextView textView = new TextView(context);
            textView.setTextColor(Color.RED);
            textView.setTextSize(22);
            textView.setGravity(Gravity.CENTER);
            textView.setMinHeight(80);
            textView.setText("病人信息核对有误");
            alertDialogOverDue = new AlertDialog.Builder(context)
                    .setTitle("提醒！")
                    .setIcon(R.drawable.warning)
                    .setView(textView)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            alertDialogOverDue.show();
            InfoUtils.warningComfirmError(ZhongjiChuancCiExecuteSingleActivity.this,beepManager);
            InfusionEventModel infusionEventModel = new InfusionEventModel();//输液扫描核对错误日志Model
            ArrayList<InfusionEventModel> infusionEventModels = new ArrayList<InfusionEventModel>();
            infusionEventModel.AdditionId =patientId;
            infusionEventModel.Item = "核对";
            infusionEventModel.Memo = "穿刺核对";
            infusionEventModel.OperateId = LocalSetting.CurrentAccount.UserName;
            infusionEventModel.OperateName = LocalSetting.CurrentAccount.FullName;
            infusionEventModel.Status = 0;
            infusionEventModel.TargetId = currentBottle.BottleId;
            infusionEventModels.add(infusionEventModel);
            InfoUtils.recordConfirmError(ZhongjiChuancCiExecuteSingleActivity.this,infusionEventModels);
        }

        /**
         * 只有瓶贴和病人信息都打勾才能提交
         */
        btnOK.setEnabled(cbBottle.isChecked() && cbBand.isChecked());
    }

    @Override
    protected void receiverBottleId(String patientId, String bottleId) {

        /*if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
            UIHepler.showToast(context, "不能在此界面扫描拼贴,请退出到主界面再试!");
            return;
        }*/
        if (currentBottle.BottleId.equals(bottleId)) {
            UIHepler.showToast(context, "请不要重复扫描");
            return;
        }
        /**
         * 此医嘱执行界面可以继续扫拼贴 重新加载即可
         *
         * 清空pid,bid,iid
         */
        btnOK.setEnabled(false);
        cbBottle.setChecked(false);
        cbBand.setChecked(false);
        cbEnd.setChecked(false);
        cbSeat.setChecked(false);
        BottleModel bottleModel = infusionDetailDAO.getBottleById(bottleId);
        if (bottleModel.BottleId != null) {
            loadBottle(bottleModel);
            excuteAfter(false);
            isClick();
        } else {
            showLoadingDialog("正在查找瓶贴...");
            String url = ChuanCiApi.url_getBottleByBottleId(bottleId);
            RestClient.get(url, new BaseAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, String s) {
                    dismissLoadingDialog();
                    BottleModel bottleModel = String2InfusionModel.string2BottleModel(s);
                    if (bottleModel != null) {
                        infusionDetailDAO.saveToInfusionDetail(bottleModel);
                        loadBottle(bottleModel);
                        isClick();
                        excuteAfter(false);
                    } else {
                        UIHepler.showToast(ZhongjiChuancCiExecuteSingleActivity.this, "没有查找带该瓶贴！");
                    }

                }

                @Override
                public void onFailure(int i, Throwable throwable, String s) {
                    dismissLoadingDialog();
                    UIHepler.showToast(ZhongjiChuancCiExecuteSingleActivity.this, "查找失败");
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }


    /**
     * 判断座位是否在服务器被占用
     *
     * @param data
     */
    private void judgeSeat(Intent data) {
        showLoadingDialog("座位号验证中...");
        final String emptySeatNO1 = data.getStringExtra("No");
        final String emptySeatNO = emptySeatNO1.replace("+", "_");

        RestClient.get(InfoApi.url_updateSeat(LocalSetting.DepartmentID, currentBottle.PeopleInfo.InfusionId,
                emptySeatNO, 0, areaID), new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                dismissLoadingDialog();
                String seatMessage = "";
                if (s.startsWith("\"") && s.endsWith("\"")) {
                    seatMessage = s.replaceAll("\"", "");
                }else {
                    seatMessage =s;
                }
                if ("none".equals(seatMessage)) {

                    //                            UIHelper.showToast("没有剩余座位,请稍后重试!");
                    //                            UIHelper.showWarningDialog("没有剩余座位,请稍后重试!");
                } else if ("has".equals(seatMessage)) {
                    //final String emptySeatNO2=emptySeatNO.replace("_","+");

                    //                            UIHelper.showToast("emptySeatNO1 + \"座位被占用，请选择其他座位！");
                    //                            UIHelper.showWarningDialog(emptySeatNO1 + "座位被占用，请选择其他座位！");
                } else {
                    Fq = seatMessage.split(",");
                    currentBottle.PeopleInfo.SeatNo = Fq[0].replace("号", " ").trim();
                    divZone = "";
                    divZone = Fq[1];
                    LocalSetting.divZone = Fq[1];
                    infusionDetailDAO.updateBottle(currentBottle);
                    btnSeat.setText(emptySeatNO1);
                    //                            UIHelper.showToast("分配成功,座位号："+emptySeatNO1);
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                dismissLoadingDialog();
                UIHepler.showToastInCenter(context, "分配失败");
            }
        });
    }

    private void initBoradCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.SETNO);
        setNoReceiver = new SetNoReceiver();
        this.registerReceiver(setNoReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(setNoReceiver);
        super.onDestroy();
    }

    private class SetNoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.SETNO)) {
                String No = intent.getStringExtra("No");
                btnSeat.setText(No);
                judgeSeat(intent);
            }
        }
    }

    /**
     * 下拉弹出框
     *
     * @Title: showPopMenu
     * @Description: TODO
     * @return: void
     */
    protected void showPopMenu() {
        if (popMenu == null) {
            popMenu = new PopMenu(ZhongjiChuancCiExecuteSingleActivity.this);
            popMenu.addItems(strings);
            popMenu.setOnItemClickListener(new PopMenu.OnItemClickListener() {
                @Override
                public void onItemClick(int index) {
                    String slectValue = strings[index];
                    if ("病人ID".equals(slectValue)) {
                        doManualBand();
                    } else if ("其它".equals(slectValue)) {
                        doManualOther();
                    }
                }
            });
        }
        popMenu.showAsDropDown(more, -30, 0);
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次返回选角色界面", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            Intent intent = new Intent();
            intent.setClass(ChuancCiExecuteSingleActivity.this, RoleChoiceActivity.class);
            startActivity(intent);
            finish();
            //            AppManager.getInstance().appExit(ChuanCiActivity.this);
        }
    }*/

    private void excuteAfter(boolean flag){
        //执行、过号
        if(flag){
            ViewUtils.setGone(mTopLayout);
            ViewUtils.setGone(mContentLayout);
            ViewUtils.setGone(mHandlerContent);
            ViewUtils.setVisible(mCheckInfo_layout);
            mActionCall.setClickable(false);
            more.setClickable(false);
            btnOK.setClickable(false);
            btnCancel.setClickable(false);
        }else {  //重新扫描
            ViewUtils.setVisible(mTopLayout);
            ViewUtils.setVisible(mContentLayout);
            ViewUtils.setVisible(mHandlerContent);
            ViewUtils.setGone(mCheckInfo_layout);
        }

    }

    /**
     * 当界面的时候按钮可以点击
     */
    private void isClick(){
        mActionCall.setClickable(true);
        more.setClickable(true);
        btnOK.setClickable(true);
        btnCancel.setClickable(true);
    }

    private void addBottle(BottleModel bottleModel){
        if(bottleModels.size()==10){
            bottleModels.remove(0);
            bottleModels.add(bottleModel);
        }else {
            bottleModels.add(bottleModel);
        }
        loadBottle2(bottleModel);
    }

    private void UpOrNextBottle(String str){
        int loadIndex = bottleModels.indexOf(loadModel);
        if("up".equals(str)){
            if(loadIndex>0){
                loadBottle2(bottleModels.get(loadIndex-1));
            }else {
                UIHepler.showToast(ZhongjiChuancCiExecuteSingleActivity.this,"没有上一个了");
            }
        }else if("next".equals(str)){
            if(loadIndex<bottleModels.size()-1){
                loadBottle2(bottleModels.get(loadIndex+1));
            }else {
                UIHepler.showToast(ZhongjiChuancCiExecuteSingleActivity.this,"没有下一个了");
            }
        }
    }
    private void loadBottle2(BottleModel bottle) {
        loadModel = bottle;
            cbBottle.setChecked(LocalSetting.IsOpenByScan);
            leftInfoPatient.setText("");
            if (StringUtils.getString(bottle.GCF).equals("重症")
                    || !StringUtils.StringIsEmpty(bottle.LZZ)
                        ) {
                lzzGcf.setVisibility(View.VISIBLE);
            }
            if (StringUtils.getString(bottle.GCF).equals("重症")) {
                gcf.setText(bottle.GCF);
            }
            if (!StringUtils.StringIsEmpty(bottle.LZZ)) {
                lzz.setText(bottle.LZZ);
            }
            groupId.setText(StringUtils.getString(bottle.GroupId));
            tvDs.setText(StringUtils.StringIsEmpty(bottle.TransfusionSpeed) ? "" : bottle.TransfusionSpeed);
            tvUnit.setText(StringUtils.StringIsEmpty(bottle.SpeedUnit) ? "" : bottle.SpeedUnit);
            tvPc.setText(StringUtils.StringIsEmpty(bottle.Frequency) ? "" : bottle.Frequency);
            tvYf.setText(StringUtils.StringIsEmpty(bottle.Way) ? "" : bottle.Way);
            tvSj.setText(StringUtils.StringIsEmpty(bottle.RegistrationDate) ? "" : DateUtils.getMMddHHmm(bottle.RegistrationDate));
            drugAdapter = new DrugAdapter(context, new ArrayList<DrugDetailModel>());
            listView.setAdapter(drugAdapter);
//          ListViewUtils.setListViewHeightBasedOnChildren(listView);
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
