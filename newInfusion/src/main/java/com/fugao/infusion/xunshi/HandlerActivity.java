package com.fugao.infusion.xunshi;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseTempleActivity;
import com.fugao.infusion.chuaici.DrugAdapter;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.QueueStatusCategory;
import com.fugao.infusion.dao.DataBaseInfo;
import com.fugao.infusion.dao.InfusionDetailDAO;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.InfusionStatus;
import com.fugao.infusion.model.PatrolModel;
import com.fugao.infusion.utils.DateUtils;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.InfusionHelper;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.view.ComboBox;
import com.fugao.infusion.view.processbutton.iml.ActionProcessButton;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.JacksonHelper;
import com.jasonchen.base.utils.ListViewUtils;
import com.jasonchen.base.utils.ResourceUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import org.codehaus.jackson.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastApps
 * @Location: com.android.fastinfusion.ui.activity.HandlerActivity
 * @Description: TODO 统一操作界面  当输液操作完成后执行变成已输后再点击已输后进入的页面
 * 在巡视中扫描瓶贴也可进入此界面、呼叫界面(巡视输液时长按后进入呼叫的界面)
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/10/13 23:11
 * @Modify-date: 修改日期
 * @Modify-description: TODO 修改说明 巡视操作界面(暂时没有用)
 * @Modify-author: 修改人
 * @version: V1.0
 */

public class HandlerActivity extends BaseTempleActivity
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "Fast-HandlerActivity";

    @InjectView(R.id.btnArround)
    ActionProcessButton btnArround;
    @InjectView(R.id.btnException)
    ActionProcessButton btnException;
    @InjectView(R.id.btnAllotSeat)
    ActionProcessButton btnAllotSeat;
    @InjectView(R.id.btnFinish)
    ActionProcessButton btnFinish;
    @InjectView(R.id.btnGameOver)
    ActionProcessButton btnGameOver;

    @InjectView(R.id.cbNormal)
    CheckBox cbNormal;
    @InjectView(R.id.cbSpeed)
    CheckBox cbSpeed;
    @InjectView(R.id.etSpeed)
    EditText etSpeed;
    @InjectView(R.id.cboReason)
    ComboBox cboReason;

   /* @InjectView(R.id.marqViewPatient)
    MarqueeView mvPatient;*/
    @InjectView(R.id.leftInfo)
    TextView leftInfoPatient;
    @InjectView(R.id.rightInfo)
    TextView rightInfo;

    @InjectView(R.id.listView)
    ListView listView;
    /*@InjectView(R.id.marqViewDrug)
    MarqueeView mvDrug;*/
    @InjectView(R.id.drugInfo)
    TextView leftInfoDrug;

    @InjectView(R.id.contentNormal)
    RelativeLayout contentNormal;
    @InjectView(R.id.contentSpeed)
    RelativeLayout contentSpeed;
    @InjectView(R.id.tvNormal)
    TextView tvNormal;
    @InjectView(R.id.tvSpeed)
    TextView tvSpeed;
    @InjectView(R.id.op)
    TextView op;
    @InjectView(R.id.opOther)
    TextView opOther;
    @InjectView(R.id.lll)
    LinearLayout lll;
    @InjectView(R.id.title_text_view)
    TextView title_text_view;

    private List<ActionProcessButton> processButtonList;
    /**
     * 控制按钮进度
     */
    //private ProgressGenerator progressGenerator;

    private BottleModel currentBottle;
    private String otherReason;
    private String[] reasons;
    private TextView titleText;
    /*private MarqueeView titleMarqView;*/
    private boolean isHide;

    private InfusionDetailDAO infusionDetailDAO;
    private String areaID;
    public String[] Fq;
    public String divZone;
    private SetNoReceiver setNoReceiver;



    @Override
    public void setContentView() {
        setContentView(R.layout.activity_handler);
    }

    @Override
    public void initView() {
        areaID = XmlDB.getInstance(context).getKeyString("areaID", "1");
        currentBottle = LocalSetting.CurrentBottle;
        cboReason.getEditText().setTextColor(getResources().getColor(R.color.black));
        cboReason.getEditText().setTextSize(18);
        //cboReason.getEditText().setBackgroundColor(getResources().getColor(R.color.white));
        reasons = ResourceUtils.getResouce4Arrays(context, R.array.arround_unnormal_reason);
        if (isHide) {
            btnAllotSeat.setVisibility(View.GONE);
            btnFinish.setVisibility(View.GONE);
            btnGameOver.setVisibility(View.GONE);

            opOther.setVisibility(View.GONE);

        }
    }

    @Override
    public void initData() {
        initBoradCast();
        initProcessButtonList();
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(HandlerActivity.this));
        cboReason.setData(reasons);
        ListViewUtils.setListViewHeightBasedOnChildren(listView);

        listView.setAdapter(new DrugAdapter(context, currentBottle.DrugDetails));
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
        rightInfo.setText(currentBottle.PeopleInfo.DrugAllergy);
        leftInfoDrug.setText(currentBottle.PeopleInfo.DrugAllergy);
        etSpeed.setText(currentBottle.TransfusionSpeed);
    }

    @Override
    public void initListener() {
        btnArround.setOnClickListener(this);
        btnException.setOnClickListener(this);
        btnAllotSeat.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        btnGameOver.setOnClickListener(this);
        contentNormal.setOnClickListener(this);
        contentSpeed.setOnClickListener(this);
        tvNormal.setOnClickListener(this);
        //        tvSpeed.setOnClickListener(this);
        //        etSpeed.setOnClickListener(this);

        cbNormal.setOnCheckedChangeListener(this);
        //        cbSpeed.setOnCheckedChangeListener(this);

        cboReason.setListViewOnClickListener(new ComboBox.ListViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                cboReason.getEditText().setText(reasons[position]);
                cboReason.getEditText().setEnabled(position == reasons.length - 1);
                //if (position == reasons.length - 1) {
                //  cboReason.getEditText().setBackgroundColor(getResources().getColor(R.color.light_grey));
                //} else {
                //  cboReason.getEditText().setBackgroundColor(getResources().getColor(R.color.white));
                //}
            }
        });
        cboReason.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                otherReason = editable.toString();
                btnException.setEnabled(!StringUtils.StringIsEmpty(otherReason));
            }
        });
        title_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initIntent() {
        isHide = getIntent().getBooleanExtra("liuqq", false);
    }

    private void initProcessButtonList() {
        processButtonList = new ArrayList<ActionProcessButton>();
        processButtonList.add(btnArround);
        processButtonList.add(btnAllotSeat);
        processButtonList.add(btnFinish);
        processButtonList.add(btnGameOver);
    }

    /**
     * 每次执行一个操作 屏蔽其他操作
     *
     * @param processButton 当前点击按钮
     */
    private void handleMutex(ActionProcessButton processButton) {
        if (processButtonList.contains(processButton)) {
            processButtonList.remove(processButton);
        }
        for (ActionProcessButton btn : processButtonList) {
            if (btn == btnArround) shieldAround(true);
            btn.setEnabled(false);
        }
    }

    private void realeaseAllButtons() {
        btnArround.setEnabled(true);
        btnAllotSeat.setEnabled(true);
        btnFinish.setEnabled(true);
        btnGameOver.setEnabled(true);
    }

    /**
     * 屏蔽巡视组件
     */
    private void shieldAround(boolean shield) {
        cbNormal.setEnabled(shield);
        cbSpeed.setEnabled(shield);
        etSpeed.setEnabled(shield);
        cboReason.setEnabled(shield);
    }

    /**
     * processButton点击事件
     *
     * @param view 当前对象
     */
    @Override
    public void onClick(View view) {
        //ActionProcessButton currentButton = (ActionProcessButton) view;
        //
        //if (!processButtonList.contains(currentButton)) {
        //  processButtonList.add(currentButton);
        //}

        //progressGenerator = new ProgressGenerator(currentButton);
        //handleMutex(currentButton);
        //progressGenerator.start();

        switch (view.getId()) {
            case R.id.btnArround:
                doArround();
                break;
            case R.id.btnException:
                doException();
                break;
            case R.id.btnAllotSeat:
//                doAllotSeat();
                break;
            case R.id.btnFinish:
                doFinish();
                break;
            case R.id.btnGameOver:
                doGameOver();
                break;
            case R.id.contentNormal:
            case R.id.tvNormal:
                doCheckNormal();
                break;
            case R.id.contentSpeed:
            case R.id.tvSpeed:
            case R.id.etSpeed:
                //                doCheckSpeed();
                break;
            default:
                break;
        }
    }

    /**
     * 处理异常
     */
    private void doException() {
        List<PatrolModel> aboutPatrols = null;
        PatrolModel patrolDetailBean = new PatrolModel();
        patrolDetailBean.BottleId = currentBottle.BottleId;
        patrolDetailBean.PatrolerNo = LocalSetting.CurrentAccount.UserName;
        patrolDetailBean.PatrolerName = LocalSetting.CurrentAccount.FullName;
        patrolDetailBean.PatrolTime = DateUtils.getContainerTCurrentDate();

        patrolDetailBean.Content = "输液异常";
        patrolDetailBean.Status = -1;
        patrolDetailBean.AboutNo = currentBottle.PeopleInfo.PatId;
        patrolDetailBean.TargetContent = otherReason;
        if(otherReason==null){
            UIHepler.showToast(HandlerActivity.this,"请输入异常现象");
            return;
        }
        if (aboutPatrols != null) {
            aboutPatrols.add(patrolDetailBean);
        } else {
            aboutPatrols = new ArrayList<PatrolModel>();
            aboutPatrols.add(patrolDetailBean);
        }

        List<BottleModel> updateBottles = new ArrayList<BottleModel>();
        updateBottles.add(currentBottle);

        showLoadingDialog("正在添加巡视记录...");
        String url = ChuanCiApi.url_addPatorl();
        String Json = String2InfusionModel.patrolModels2String((ArrayList) aboutPatrols);
        RestClient.put(HandlerActivity.this,url,Json,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                dismissLoadingDialog();
                realeaseAllButtons();
                UIHepler.showToast(HandlerActivity.this,"添加巡视记录成功");
                openActivity(XunshiActivity.class);
                InfoUtils.sendSuccessBroadcast(context);
                finish();
            }
            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                dismissLoadingDialog();
                realeaseAllButtons();
                btnArround.setText("执行失败,请重试!");
            }
        });
    }

    private void doCheckSpeed() {
        cbSpeed.setChecked(!cbSpeed.isChecked());
        etSpeed.setEnabled(cbSpeed.isChecked());
        if (cbSpeed.isChecked()) cbNormal.setChecked(false);
        btnArround.setEnabled(cbNormal.isChecked() || cbSpeed.isChecked());
    }

    private void doCheckNormal() {
        cbNormal.setChecked(!cbNormal.isChecked());
        if (cbNormal.isChecked()) cbSpeed.setChecked(false);
        btnArround.setEnabled(cbNormal.isChecked() || cbSpeed.isChecked());
    }

    /**
     * 增加巡视记录
     */

    private void doArround() {
        List<PatrolModel> aboutPatrols = null;

        PatrolModel patrolDetailBean = new PatrolModel();
        patrolDetailBean.BottleId = currentBottle.BottleId;
        patrolDetailBean.PatrolerNo = LocalSetting.CurrentAccount.UserName;
        patrolDetailBean.PatrolerName = LocalSetting.CurrentAccount.FullName;
        patrolDetailBean.PatrolTime = DateUtils.getContainerTCurrentDate();

        String content = "";
        int status = 0;
        if (cbNormal.isChecked()) {
            content = "巡视正常";
        } else if (cbSpeed.isChecked()) {
            content = "修改滴速";
            status = 1;
        }
        if(!cbNormal.isChecked()){
            UIHepler.showToast(HandlerActivity.this,"请选择输液巡视！");
            return;
        }
        patrolDetailBean.Content = content;
        patrolDetailBean.Status = status;
        patrolDetailBean.AboutNo = currentBottle.PeopleInfo.PatId;
        if (cbSpeed.isChecked())
            patrolDetailBean.DrippingSpeedUnit = currentBottle.SpeedUnit;

        if (!StringUtils.StringIsEmpty(etSpeed.getText().toString())) {
            patrolDetailBean.DrippingSpeed = etSpeed.getText().toString();
        }

        if (aboutPatrols != null) {
            aboutPatrols.add(patrolDetailBean);
        } else {
            aboutPatrols = new ArrayList<PatrolModel>();
            aboutPatrols.add(patrolDetailBean);
        }

        List<BottleModel> updateBottles = new ArrayList<BottleModel>();
        updateBottles.add(currentBottle);

        showLoadingDialog("正在添加巡视记录...");
        String url = ChuanCiApi.url_addPatorl();
        String Json = String2InfusionModel.patrolModels2String((ArrayList) aboutPatrols);
        RestClient.put(HandlerActivity.this,url,Json,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                dismissLoadingDialog();
                realeaseAllButtons();
                UIHepler.showToast(HandlerActivity.this,"添加巡视记录成功");
                openActivity(XunshiActivity.class);
                finish();
                InfoUtils.sendSuccessBroadcast(context);
            }
            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                dismissLoadingDialog();
                realeaseAllButtons();
                btnArround.setText("执行失败,请重试!");
            }
        });
    }


//    /**
//     * 重新分配座位
//     */
//    private void doAllotSeat() {
//        if (LocalSetting.AutoAllotSeat) {
//            showLoadingDialog("正在自动分配座位");
//            InfoApi.autoAllotSeat(context, currentBottle.InfusionId, areaID,new RestListener<String>() {
//                @Override
//                public void onSuccess(String s) {
//                    realeaseAllButtons();
//                    dismissLoadingDialog();
//                    //                    if ("none".equals(s)) {
//                    if(s.contains("none")){
//                        UIHelper.showWarningDialog("没有剩余座位,请稍后重试!");
//                    } else if(s.contains("has")){
//                        UIHelper.showWarningDialog("该座位已经被占用,请重新分配");
//                    }else {
//                        /**
//                         * 更新数据库
//                         */
//                        InfoUtils.sendSuccessBroadcast(context);
//                        currentBottle.PeopleInfo.SeatNo = s;
//                        UIHelper.showLongToast("分配了" + s);
//                        finish();
//                    }
//                }
//
//                @Override
//                public void onFailed(AppException error, String msg) {
//                    dismissLoadingDialog();
//                    realeaseAllButtons();
//                    error.makeToast(context);
//                }
//            });
//        } else {
//            //            /**
//            //             * 加载空座位列表 选一分配
//            //             */
//            //            doShowEmptySeats();
//            openActivityForResult(AreaInfusionEmptyActivity.class,100);
//        }
//    }
    /**
     * 结束本次输液
     */
    private void doFinish() {   //先判断有多少未输液
        String url = ChuanCiApi.Url_overInfusion(currentBottle.PeopleInfo.PatId,"1");
        RestClient.get(url,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                InfusionStatus infusionStatus = JacksonHelper.getObject(s,new TypeReference<InfusionStatus>() {});
                if(infusionStatus.TodoCount>0){
                    /**
                     * 还有未输液的情况提醒
                     */
                    InfusionHelper.showConfirmDilalog(context, "还有" + infusionStatus.TodoCount + "组药未输液!是否完成此瓶输液?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finshCurrent();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    realeaseAllButtons();
                                    dialogInterface.dismiss();
                                }
                            }
                    );
                }else if(infusionStatus.DoingCount <=1){
                    /**
                     * 最后一组的情况，结束全部
                     */
                    InfusionHelper.showConfirmDilalog(context, "此为最后" + infusionStatus.DoingCount + "组输液!是否结束今天输液?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    doGameOver();
                                    handlerGameOver();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    realeaseAllButtons();
                                    dialogInterface.dismiss();
                                }
                            }
                    );
                }else {
                    /**
                     * 不是最后一组，而且有多个在输
                     */
                    finshCurrent();
                }
            }
            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                UIHepler.showToast(HandlerActivity.this,"判断几组未输失败");
            }
        });
    }

    private void finshCurrent(){
        final List<BottleModel> bos = new ArrayList<BottleModel>();
        currentBottle.BottleStatus = BottleStatusCategory.HADINFUSE.getKey();
        currentBottle.EndCore = LocalSetting.CurrentAccount.UserName;
        currentBottle.EndDate = DateUtils.getCurrentDate("yyyyMMdd");
        currentBottle.EndTime = DateUtils.getCurrentDate("HHmmss");
        currentBottle.SeatNo = currentBottle.PeopleInfo.SeatNo;
        bos.add(currentBottle);
        showLoadingDialog("正在结束本次输液...");
        String url = ChuanCiApi.Url_finishCurrentinfuion(1);
        String Json = String2InfusionModel.bottleModles2String((ArrayList<BottleModel>) bos);
        RestClient.put(HandlerActivity.this,url,Json,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                dismissLoadingDialog();
                List<String> strings = JacksonHelper.getObjects(s,new TypeReference<List<String>>() {});
                if(strings!= null && strings.size()>0 ){   //释放座位， 不管
                }else {
                    realeaseAllButtons();
                    InfoUtils.sendSuccessBroadcast(context);
                    infusionDetailDAO.updateBottle(currentBottle);
                    int unBottlesCount = infusionDetailDAO.getUnBottlesCount(currentBottle.PeopleInfo.PatId);
                    UIHepler.showToast(HandlerActivity.this, "结束本次输液成功");
                    if (unBottlesCount > 0) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(HandlerActivity.this);
                        dialog.setMessage("还有" + unBottlesCount + "组药未输!")

                                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).create().show();
                    } else {
                        finish();
                    }
                }
            }
            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                dismissLoadingDialog();
                realeaseAllButtons();
                currentBottle.BottleStatus = BottleStatusCategory.INFUSIONG.getKey();
                UIHepler.showToast(HandlerActivity.this,"结束本次输液失败");
            }
        });

    }
    /**
     * 结束全部输液
     */
    private void doGameOver() {
        String url = ChuanCiApi.Url_overInfusion(currentBottle.PeopleInfo.PatId,"1");
        RestClient.get(url,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                InfusionStatus infusionStatus = JacksonHelper.getObject(s,new TypeReference<InfusionStatus>() {});
                if(infusionStatus.TodoCount>0){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(HandlerActivity.this);
                    dialog.setMessage("还有" + infusionStatus.TodoCount + "组药未输!是否确认结束全部输液?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    handlerGameOver();
                                }
                            }).setNegativeButton("取消",null).create().show();
                }else if(infusionStatus.DoingCount>1){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(HandlerActivity.this);
                    dialog.setMessage("还有" + infusionStatus.DoingCount + "组药正在输液!是否确认结束全部输液?")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    handlerGameOver();
                                }
                            }).setNegativeButton("取消",null).create().show();
                }else {
                    handlerGameOver();
                }
            }
            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                UIHepler.showToast(HandlerActivity.this,"判断几组未输失败");
            }
        });
    }

    /**
     * 结束全部输液接口
     */
    private void handlerGameOver() {
        showLoadingDialog("正在结束全部输液...");
        String url = InfoApi.url_end(LocalSetting.CurrentAccount.Id, currentBottle.PeopleInfo.PatId,LocalSetting.CurrentAccount.FullName);
        RestClient.get(url,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                dismissLoadingDialog();
                realeaseAllButtons();
                currentBottle.PeopleInfo.Status = QueueStatusCategory.GAMEOVER.getKey();
                InfoUtils.sendSuccessBroadcast(context);
                UIHepler.showToast(HandlerActivity.this,"结束全部输液成功");
                finish();
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                dismissLoadingDialog();
                realeaseAllButtons();
                UIHepler.showToast(HandlerActivity.this,"结束所有输液失败");
            }
        });
    }
    /**
     * checkBox checkchange事件 为什么没用radioButton  考虑到可能以后会扩展同时执行多项
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean clicked) {
        etSpeed.setEnabled(cbSpeed.isChecked());
        btnArround.setEnabled(cbNormal.isChecked() || cbSpeed.isChecked());

        switch (compoundButton.getId()) {
            case R.id.cbNormal:
                if (clicked) cbSpeed.setChecked(false);
                break;
            case R.id.cbSpeed:
                if (clicked) cbNormal.setChecked(false);
                break;
        }
    }

    private void initBoradCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.SETNO);
        setNoReceiver = new SetNoReceiver();
        this.registerReceiver(setNoReceiver, intentFilter);
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(setNoReceiver);
        super.onDestroy();
    }

    private class SetNoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constant.SETNO)){
//                judgeSeat(intent);
            }

        }
    }

}
