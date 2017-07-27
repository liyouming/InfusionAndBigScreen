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

package com.fugao.infusion.xunshi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseScanTestTempleActivity;
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
import com.fugao.infusion.model.BottleModel;
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
import com.jasonchen.base.utils.ListViewUtils;
import com.jasonchen.base.utils.ResourceUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.utils.ViewUtils;
import com.jasonchen.base.view.UIHepler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * @Description: TODO 穿刺和输液执行界面   单次执行
 */

public class NewXunshiExecuteSingleActivity extends BaseScanTestTempleActivity {


    @InjectView(R.id.title_text_view)
    TextView titleTextView;
    @InjectView(R.id.tv_excute_bottle)
    TextView tv_excute_bottle;
    @InjectView(R.id.patient_name)
    TextView patient_name;
    @InjectView(R.id.more)
    LinearLayout more;
    @InjectView(R.id.title)
    RelativeLayout title;
    @InjectView(R.id.cbBottle)
    CheckBox cbBottle;
    @InjectView(R.id.tv_bottle)
    TextView tvBottle;
    @InjectView(R.id.cbBand)
    CheckBox cbBand;
    @InjectView(R.id.btnSeat)
    Button btnSeat;
    @InjectView(R.id.patient_status)
    RelativeLayout patientStatus;
    @InjectView(R.id.cboEditReason)
    ComboBox cboEditReason;
    @InjectView(R.id.cbEnd)
    CheckBox cbEnd;
    @InjectView(R.id.cbSeat)
    CheckBox cbSeat;
    @InjectView(R.id.tvEnd)
    TextView tvEnd;
    @InjectView(R.id.tvAllotSeat)
    TextView tvAllotSeat;
    @InjectView(R.id.lll)
    LinearLayout lll;
    @InjectView(R.id.otherReason)
    LinearLayout otherReason;
    @InjectView(R.id.cbExcuteBottle)
    CheckBox cbExcuteBottle;
    @InjectView(R.id.infusioningBottle)
    RelativeLayout infusioningBottle;
    @InjectView(R.id.leftInfo)
    TextView leftInfoPatient;
    @InjectView(R.id.rightInfo)
    TextView rightInfo;
    @InjectView(R.id.lzz)
    TextView lzz;
    @InjectView(R.id.gcf)
    TextView gcf;
    @InjectView(R.id.lzzGcf)
    LinearLayout lzzGcf;
    @InjectView(R.id.lsh)
    TextView lsh;
    @InjectView(R.id.groupId)
    TextView groupId;
    @InjectView(R.id.tvSj)
    TextView tvSj;
    @InjectView(R.id.show)
    LinearLayout show;
    @InjectView(R.id.topLayout)
    LinearLayout topLayout;
    @InjectView(R.id.listView)
    ListView listView;
    @InjectView(R.id.durg_layout)
    LinearLayout durgLayout;
    @InjectView(R.id.tvPc)
    TextView tvPc;
    @InjectView(R.id.tvYf)
    TextView tvYf;
    @InjectView(R.id.tvUnit)
    TextView tvUnit;
    @InjectView(R.id.tvDs)
    TextView tvDs;
    @InjectView(R.id.ssssss)
    RelativeLayout ssssss;
    @InjectView(R.id.showMessage)
    TextView showMessage;
    @InjectView(R.id.callAginShow)
    LinearLayout callAginShow;
    @InjectView(R.id.btnCancel)
    ActionProcessButton btnCancel;
    @InjectView(R.id.divider)
    View divider;
    @InjectView(R.id.btnOK)
    ActionProcessButton btnOK;
    @InjectView(R.id.handlerContent)
    LinearLayout handlerContent;
    private List<BottleModel> oldBottles;
    private List<BottleModel> newBottles;
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
    private String patientId;
    private BeepManager beepManager;//呼叫声音
    private ProgressDialog loadingDialog;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_excute_single_xunshi_new);
    }

    @Override
    public void initView() {
        strings = ResourceUtils.getResouce4Arrays(NewXunshiExecuteSingleActivity.this, R.array.infusionstatus_handexecute_chuanci);
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(NewXunshiExecuteSingleActivity.this));
        //        initActionBar();
        InitCombox();
        initCheckBox();
        //    initMargViews();
        deptID = XmlDB.getInstance(context).getKeyString("deptID", "100001");
        areaID = XmlDB.getInstance(context).getKeyString("areaID", "1");
//        btnOK.setEnabled(true);
        if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
            //      InfoUtils.in    itBluetoothSetting(context);
            InfoUtils.checkBlueTooth(context);
            cbEnd.setVisibility(View.GONE);
            cbSeat.setVisibility(View.GONE);
            tvEnd.setVisibility(View.GONE);
            tvAllotSeat.setVisibility(View.GONE);
            btnCancel.setVisibility(QueueStatusCategory.FINISHED.getKey() == LocalSetting.CurrentBottle.PeopleInfo.Status ? View.GONE : View.VISIBLE);
            //      btnCancel.setVisibility(View.VISIBLE);
            btnSeat.setEnabled(true);
            divider.setVisibility(View.VISIBLE);
        } else if (RoleCategory.SHUYE.getKey() == LocalSetting.RoleIndex) {
            callAginShow.setVisibility(View.GONE);
            if (BottleStatusCategory.HADINFUSE.getKey() == LocalSetting.CurrentBottle.BottleStatus) {
                btnCancel.setText("取消完成");
                ViewUtils.setGone(btnOK);
            } else {
                btnCancel.setVisibility(View.GONE);
            }
            divider.setVisibility(View.GONE);
            btnSeat.setEnabled(false);
            if (LocalSetting.AutoEndOthers) {
                cbEnd.setVisibility(View.GONE);
                tvEnd.setVisibility(View.GONE);
            }
        }
        initBoradCast();

    }

    /**
     * 初始化combox
     */
    private void InitCombox() {
        if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
            if (QueueStatusCategory.FINISHED.getKey() == LocalSetting.CurrentBottle.PeopleInfo.Status) {
                otherReason.setVisibility(View.VISIBLE);
                inputReason = ResourceUtils.getResouce4Arrays(context, R.array.arround_unnormal_reason);
                cboEditReason.setFocusable(false);
                cboEditReason.getEditText().setTextColor(Color.BLACK);
                cboEditReason.getEditText().setEnabled(false);
                cboEditReason.setData(inputReason);
            } else {
                otherReason.setVisibility(View.GONE);
            }
        } else {
            //otherReason.setVisibility(View.GONE);
            otherReason.setVisibility(View.VISIBLE);
            inputReason = ResourceUtils.getResouce4Arrays(context, R.array.arround_unnormal_reason);
            cboEditReason.setFocusable(false);
            cboEditReason.getEditText().setEnabled(false);
            cboEditReason.setData(inputReason);
        }

    }

    /**
     * 初始化当前正在执行输液瓶贴的checkboxi
     */
    private void  initCheckBox() {
        if(LocalSetting.DoingCount <= 0){
            infusioningBottle.setVisibility(View.GONE);
        }else {
            tv_excute_bottle.setText("还有"+LocalSetting.DoingCount+"组药正在执行，是否结束");
        }
    }
    private void initEndAndSeatLayouts(BottleModel bottle) {
        runningBottles = new ArrayList<BottleModel>();
        /**
         * 如果没有输液中...隐藏 结束上一瓶
         */
        if (RoleCategory.SHUYE.getKey() == LocalSetting.RoleIndex) {
            //List<BottleModel> finishedBottles = new ArrayList<BottleModel>();
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
    public void initData() {
        loadingDialog = new ProgressDialog(NewXunshiExecuteSingleActivity.this);
        beepManager = new BeepManager(NewXunshiExecuteSingleActivity.this);
        punctureDeptId = XmlDB.getInstance(context).getKeyIntValue("punctureDeptId", 15) + "";
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(NewXunshiExecuteSingleActivity.this));
        oldBottles = new ArrayList<BottleModel>();
        newBottles = new ArrayList<BottleModel>();
        statusGroup = InfoUtils.getAllStatusGroup(LocalSetting.RoleIndex);

        /**
         * 通过pid或者iid查找该人所有瓶贴
         */
        loadBottle(LocalSetting.CurrentBottle);

        if (RoleCategory.SHUYE.getKey() == LocalSetting.RoleIndex) {
            if (currentBottle == null) {
                btnSeat.setEnabled(false);
            } else {
                //                int runningCount =
                //                        infusionDetailDAO.getInfusioningCountByPatid(LocalSetting.CurrentBottle.PeopleInfo.PatId);
                if (LocalSetting.DoingCount > 0 && LocalSetting.CurrentBottle.BottleStatus < 5) {

                    if(!cbExcuteBottle.isChecked()&&cbExcuteBottle.getVisibility()==View.VISIBLE){
                        cbExcuteBottle.setChecked(true);
                        needEndOther = true;
                    }
//                    new AlertDialog.Builder(context).setMessage("还有" + LocalSetting.DoingCount + "组药正在执行中,是否结束?")
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    //                            needEndOther = true;
//                                    String patId = LocalSetting.CurrentBottle.PeopleInfo.PatId;
//                                    String fullName = LocalSetting.CurrentAccount.FullName;
//                                    String userName = LocalSetting.CurrentAccount.UserName;
//                                    String url = InfoApi.url_finshinfusionRuning(patId, fullName, userName);
//                                    RestClient.get(url, new BaseAsyncHttpResponseHandler() {
//                                        @Override
//                                        public void onSuccess(int i, String s) {
//                                            if (Integer.parseInt(s) == 1) {
//                                                UIHepler.showToast(NewXunshiExecuteSingleActivity.this, "结束正在输液成功");
//                                            } else {
//                                                UIHepler.showToast(NewXunshiExecuteSingleActivity.this, "结束正在输液失败");
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onFailure(int i, Throwable throwable, String s) {
//                                            com.jasonchen.base.utils.Log.e("结束正在执行的瓶贴失败");
//                                            UIHepler.showToast(NewXunshiExecuteSingleActivity.this, "结束正在输液失败");
//
//                                        }
//                                    });
//                                }
//                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            //                            needEndOther = false;
//
//                        }
//                    }).create().show();

                }
            }

        }
    }

    @Override
    public void initListener() {
        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnOK.setEnabled(false);
                excute();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelFinishInfusion();
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopMenu();
            }
        });
        cboEditReason.setListViewOnClickListener(new ComboBox.ListViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                btnOK.setEnabled(true);
            }
        });
        cboEditReason.setListViewOnClickListener(new ComboBox.ListViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final PatrolModel patrolDetailBean = new PatrolModel();
                String inputText = cboEditReason.getEditText().getText().toString();

                if(!StringUtils.StringIsEmpty(inputText)){
                    patrolDetailBean.TargetContent = "输液异常";
                    patrolDetailBean.BottleId = LocalSetting.CurrentBottle.BottleId;
                    patrolDetailBean.PatrolerNo = LocalSetting.CurrentAccount.UserName;
                    patrolDetailBean.PatrolerName = LocalSetting.CurrentAccount.FullName;
                    patrolDetailBean.PatrolTime = DateUtils.getStandarCurrentDate();
                    patrolDetailBean.Content = inputText;
//                    patrolDetailBean.Expand = inputText;
                    patrolDetailBean.Status = -1; //异常巡视
                    patrolDetailBean.Type =1;
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
     * 取消穿刺操作 释放座位 并 销毁页面
     */
    private void cancel() {

        final ProgressGenerator progressGenerator = new ProgressGenerator(btnCancel);
        progressGenerator.start();
        RestClient.get(ChuanCiApi.url_getPostCall(currentBottle.PeopleInfo.DepartmentId, currentBottle.InfusionId, punctureDeptId), new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                progressGenerator.success();
                NewXunshiExecuteSingleActivity.this.finish();
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
     * 执行提交操作，和服务器进行交互  这两个url写法不好
     * 但是第一个结束正在执行的瓶贴的url不一定会执行，如果失败就把执行按钮显示出来重新执行，此时不会执行提交瓶贴的操作
     */
//    private void excute() {
//        if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
//            final ProgressGenerator progressGenerator = new ProgressGenerator(btnOK);
//            progressGenerator.start();
//            oldBottles.add(currentBottle);
//            for (BottleModel each : oldBottles) {
//                each.BottleStatus = BottleStatusCategory.INFUSIONG.getKey();
//                each.InfusionCore = LocalSetting.CurrentAccount.UserName;
//                each.InfusionDate = DateUtils.getCurrentDate("yyyyMMdd");
//                each.InfusionTime = DateUtils.getCurrentDate("HHmmss");
//                newBottles.add(each);
//            }
//            if (infusionDetailDAO.getBottleById(currentBottle.BottleId).BottleId != null) {
//                infusionDetailDAO.updateBottle(currentBottle);
//            } else {
//                infusionDetailDAO.saveExecuteSingBottleModel(currentBottle);
//            }
//            finish();
//        } else {
//            final ProgressGenerator progressGenerator = new ProgressGenerator(btnOK);
//            progressGenerator.start();
//            oldBottles.add(currentBottle);
//            if(cbExcuteBottle.isChecked() && cbExcuteBottle.getVisibility()==View.VISIBLE){
//
//            //needEndOther = true;
//            String patId = LocalSetting.CurrentBottle.PeopleInfo.PatId;
//            String fullName = LocalSetting.CurrentAccount.FullName;
//            String userName = LocalSetting.CurrentAccount.UserName;
//            String url = InfoApi.url_finshinfusionRuning(patId, userName,fullName);
//            RestClient.get(url, new BaseAsyncHttpResponseHandler() {
//                @Override
//                public void onSuccess(int i, String s) {
//                    if (Integer.parseInt(s) == 1) {
////                        UIHepler.showToast(NewXunshiExecuteSingleActivity.this, "结束正在输液成功");
//                    } else {
//                        UIHepler.showToast(NewXunshiExecuteSingleActivity.this, "结束正在输液失败");
//                    }
//                }
//
//                @Override
//                public void onFailure(int i, Throwable throwable, String s) {
////                    com.jasonchen.base.utils.Log.e("结束正在执行的瓶贴失败");
////                    UIHepler.showToast(NewXunshiExecuteSingleActivity.this, "结束正在输液失败");
//                    progressGenerator.fail();
//                    btnOK.setText("提交失败,请重试!");                    btnOK.setEnabled(true);
//
//                }
//            });
//            }
//            for (BottleModel each : oldBottles) {
//                each.BottleStatus = BottleStatusCategory.INFUSIONG.getKey();
//                each.EndCore = LocalSetting.CurrentAccount.UserName;
//                each.EndDate = DateUtils.getCurrentDate("yyyyMMdd");
//                each.EndTime = DateUtils.getCurrentDate("HHmmss");
//                newBottles.add(each);
//            }
//            if (needEndOther) {
//                if (runningBottles.size() > 0) newBottles.addAll(runningBottles);
//            }
//            String jsonString = String2InfusionModel.bottleModles2String((ArrayList<BottleModel>) newBottles);
//            RestClient.put(context, ChuanCiApi.url_updateBottles(), jsonString, new BaseAsyncHttpResponseHandler() {
//                @Override
//                public void onSuccess(int i, String s) {
//                    progressGenerator.fail();
//                    btnOK.setEnabled(true);
//                    /**
//                     * 更新本地信息
//                     */
//                    infusionDetailDAO.updateLocalData((ArrayList<BottleModel>) newBottles);
//                    drugAdapter.notifyDataSetChanged();
//                    UIHepler.showToast(context, "执行成功!");
//                    callAginShow.setVisibility(View.GONE);
////                    InfoUtils.sendSuccessBroadcast(context);
//                    finish();
//                }
//
//                @Override
//                public void onFailure(int i, Throwable throwable, String s) {
//                    progressGenerator.fail();
//                    btnOK.setText("提交失败,请重试!");
//                    btnOK.setEnabled(true);
//                }
//            });
//
//        }
//    }

    private void excute() {
            final ProgressGenerator progressGenerator = new ProgressGenerator(btnOK);
            progressGenerator.start();
            oldBottles.add(currentBottle);
            for (BottleModel each : oldBottles) {
                each.BottleStatus = BottleStatusCategory.INFUSIONG.getKey();
                each.InfusionCore = LocalSetting.CurrentAccount.UserName;
                each.InfusionDate = DateUtils.getCurrentDate("yyyyMMdd");
                each.InfusionTime = DateUtils.getCurrentDate("HHmmss");
                each.InfusionName = LocalSetting.CurrentAccount.FullName;
                newBottles.add(each);
            }
            if (needEndOther) {
                if (runningBottles.size() > 0) newBottles.addAll(runningBottles);
            }
            if(cbExcuteBottle.isChecked() && cbExcuteBottle.getVisibility()==View.VISIBLE){
                patientId = currentBottle.PeopleInfo.PatId;
            }else {
                patientId = "0";
            }
            String jsonString = String2InfusionModel.bottleModles2String((ArrayList<BottleModel>) newBottles);
            RestClient.put(context, InfoApi.url_updateBottleAndfinishRuning(patientId), jsonString, new BaseAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, String s) {
                    progressGenerator.fail();
                    btnOK.setEnabled(true);
                    /**
                     * 更新本地信息
                     */
                    infusionDetailDAO.updateLocalData((ArrayList<BottleModel>) newBottles);
                    drugAdapter.notifyDataSetChanged();
                    UIHepler.showToast(context, "执行成功!");
                    callAginShow.setVisibility(View.GONE);
                    //                    InfoUtils.sendSuccessBroadcast(context);
                    finish();
                    InfoUtils.sendSuccessBroadcast(context);
                }

                @Override
                public void onFailure(int i, Throwable throwable, String s) {
                    progressGenerator.fail();
                    btnOK.setText("提交失败,请重试!");
                    btnOK.setEnabled(true);
                }
            });

    }
    /**
     * 取消完成输液
     */
    private void cancelFinishInfusion() {
        showLoadingDialog("正在取消完成...");
        currentBottle.BottleStatus = BottleStatusCategory.INFUSIONG.getKey();
        currentBottle.PeopleInfo.Status = QueueStatusCategory.FINISHED.getKey();
        String json = String2InfusionModel.bottleModel2String(currentBottle);

        RestClient.put(context, ChuanCiApi.url_getCancelInfusion(1), json, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int i, String s) {
                dismissLoadingDialog();
                UIHepler.showToast(context, "取消完成输液成功");
                InfoUtils.sendSuccessBroadcast(context);
                NewXunshiExecuteSingleActivity.this.finish();
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                dismissLoadingDialog();
                UIHepler.showToast(context, "取消完成输液失败");
            }
        });

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
            popMenu = new PopMenu(NewXunshiExecuteSingleActivity.this);
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
                                                            patrolDetailBean.Type = 1;
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
//                                            btnOK.setEnabled(true);
                                            if (currentBottle.BottleStatus != BottleStatusCategory.HADINFUSE.getKey())
                                                WarningToast.newInstance(NewXunshiExecuteSingleActivity.this, "核对病人信息成功");

                                        } else {
                                            cbBand.setChecked(false);
                                            InfusionUIHelper.showWarningDialogByCustom(context, "病人ID录入错误!");
                                            InfoUtils.warningComfirmError(NewXunshiExecuteSingleActivity.this,beepManager);
                                            //                      UIHelper.showWarningDialog(context, "病人ID录入错误!");
                                        }
                                    }
                                    break;
                                case 2:
                                    if (currentBottle.DrugDetails == null) {
                                        UIHepler.showToast(context, "加载失败，请返回后刷新重试！");
                                        btnOK.setEnabled(false);
//                                        btnOK.setEnabled(true);
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

    @Override
    public void initIntent() {

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
                            patrolDetailBean.Content = "输液异常";
                            patrolDetailBean.Expand = inputText;
                            patrolDetailBean.Status = -1; //正常巡视
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
//                                    btnOK.setEnabled(true);
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
//                    btnOK.setEnabled(true);
                    if (currentBottle.BottleStatus != BottleStatusCategory.HADINFUSE.getKey())
                        WarningToast.newInstance(NewXunshiExecuteSingleActivity.this, "核对病人信息成功");
                }
            } else {
                /**
                 * 执行提交操作，和服务器进行交互  腕带扫描核对成功了之后就直接提交不让他手动点击
                 */
                btnOK.setEnabled(false);
                excute();
                btnOK.setEnabled(cbBottle.isChecked() && cbBand.isChecked());
//                btnOK.setEnabled(true);
                if (currentBottle.BottleStatus != BottleStatusCategory.HADINFUSE.getKey())
                    WarningToast.newInstance(NewXunshiExecuteSingleActivity.this, "核对病人信息成功");
            }
        } else {
            cbBand.setChecked(false);

            //      UIHelper.showInfoDilalog(ExecuteSingleActivity.this,"病人信息核对有误!");
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
            InfoUtils.warningComfirmError(NewXunshiExecuteSingleActivity.this,beepManager);
            InfusionEventModel infusionEventModel = new InfusionEventModel();//输液扫描核对错误日志Model
            ArrayList<InfusionEventModel> infusionEventModels = new ArrayList<InfusionEventModel>();
            infusionEventModel.AdditionId =patientId;
            infusionEventModel.Item = "核对";
            infusionEventModel.Memo = "输液巡视核对";
            infusionEventModel.OperateId = LocalSetting.CurrentAccount.UserName;
            infusionEventModel.OperateName = LocalSetting.CurrentAccount.FullName;
            infusionEventModel.Status = 0;
            infusionEventModel.TargetId = currentBottle.BottleId;
            infusionEventModels.add(infusionEventModel);
            InfoUtils.recordConfirmError(NewXunshiExecuteSingleActivity.this,infusionEventModels);
            //      UIHelper.showWarningDialog(context, "病人信息核对有误!");
        }

        /**
         * 只有瓶贴和病人信息都打勾才能提交
         */
        btnOK.setEnabled(cbBottle.isChecked() && cbBand.isChecked());
//        btnOK.setEnabled(true);
    }

    @Override
    protected void receiverBottleId(String patientId, String bottleId) {

        if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
            UIHepler.showToast(context, "不能在此界面扫描拼贴,请退出到主界面再试!");
            return;
        }
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
//        btnOK.setEnabled(true);
        cbBottle.setChecked(false);
        cbBand.setChecked(false);
        cbEnd.setChecked(false);
        cbSeat.setChecked(false);

        //BottleModel tempBottle = bottleDataHelper.getBottleById(bottleId);

        //if (tempBottle != null) {
        //  loadBottle(tempBottle);
        //} else {
        /**
         * 刷新取数据
         */
        showLoadingDialog("正在获取瓶贴...");
        String url = ChuanCiApi.url_getBottleByBottleId(bottleId);
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                dismissLoadingDialog();
                BottleModel bottleModel = String2InfusionModel.string2BottleModel(s);
                if (bottleModel != null) {
                    //            if(!bottleDataHelper.validate(bottleModel.BottleId))
                    infusionDetailDAO.deleteBottleById(bottleModel.BottleId);
                    infusionDetailDAO.saveToInfusionDetail(bottleModel);
                    BottleModel tempBo = infusionDetailDAO.getBottleById(currentBottle.BottleId);
                    if (tempBo != null) {
                        if (tempBo.BottleStatus == BottleStatusCategory.WAITINGINFUSE.getKey()) {
                            loadBottle(tempBo);
                        } else {
                            UIHepler.showToast(context, "该组药已正在输液!");
                        }
                    } else {
                        UIHepler.showToast(context, "未找到该瓶贴!");
                    }
                } else {
                    UIHepler.showToast(context, "未获取到数据,请重试!");
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                dismissLoadingDialog();

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
     * 判断座位是否在服务器被占用
     *
     * @param data
     */
    private void judgeSeat(Intent data) {
        showLoadingDialog("座位号验证中...");
        final String emptySeatNO1 = data.getStringExtra("No");
        final String emptySeatNO = emptySeatNO1.replace("+", "_");

        RestClient.get(ChuanCiApi.url_updateSeat(LocalSetting.DepartmentID, currentBottle.PeopleInfo.InfusionId,
                emptySeatNO, 1, areaID), new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {

                dismissLoadingDialog();
                if ("none".equals(s)) {

                    //                            UIHelper.showToast("没有剩余座位,请稍后重试!");
                    //                            UIHelper.showWarningDialog("没有剩余座位,请稍后重试!");
                } else if ("has".equals(s)) {
                    //final String emptySeatNO2=emptySeatNO.replace("_","+");

                    //                            UIHelper.showToast("emptySeatNO1 + \"座位被占用，请选择其他座位！");
                    //                            UIHelper.showWarningDialog(emptySeatNO1 + "座位被占用，请选择其他座位！");
                } else {
                    Fq = s.split(",");
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

    private void loadBottle(BottleModel bottle) {
        currentBottle = bottle;
        if (currentBottle == null) {
            btnSeat.setEnabled(false);
        } else {
            if (StringUtils.getString(currentBottle.GCF).equals("重症")) {
                btnSeat.setVisibility(View.GONE);

                //              btnSeat.setEnabled(false);
                //              btnSeat.setText("重症");
            } else {
                btnSeat.setVisibility(View.VISIBLE);
            }
        }

        if (currentBottle == null) {
            btnSeat.setEnabled(false);
        } else {
            if (StringUtils.StringIsEmpty(bottle.PeopleInfo.SeatNo)) {
                //              if(currentBottle.GCF.equals("重症")){
                //             //  btnSeat.setText("还未分配座位");
                //             btnSeat.setText("重症");}else{
                btnSeat.setText("还未分配座位");
                //              }
            } else {
                btnSeat.setText(bottle.PeopleInfo.SeatNo);
            }

            cbBottle.setChecked(LocalSetting.IsOpenByScan);
            patient_name.setText(StringUtils.getString( currentBottle.PeopleInfo.Name));
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
                tvSj.setText(StringUtils.StringIsEmpty(currentBottle.RegistrationDate) ? "" : DateUtils.getMMddHHmm(currentBottle.RegistrationDate));

                initEndAndSeatLayouts(bottle);
                drugAdapter = new DrugAdapter(context, bottle.DrugDetails);
                listView.setAdapter(drugAdapter);


                ListViewUtils.setListViewHeightBasedOnChildren(listView);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
