package com.fugao.infusion.xinhua;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseScanTestTempleActivity;
import com.fugao.infusion.chuaici.ChuancCiExecuteSingleActivity;
import com.fugao.infusion.chuaici.DrugAdapter;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.QueueStatusCategory;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.PatrolModel;
import com.fugao.infusion.paiyao.CheckActivity;
import com.fugao.infusion.service.PutService;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.view.ComboBox;
import com.fugao.infusion.view.ProgressGenerator;
import com.fugao.infusion.view.processbutton.iml.ActionProcessButton;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.DateUtils;
import com.jasonchen.base.utils.JacksonHelper;
import com.jasonchen.base.utils.ListViewUtils;
import com.jasonchen.base.utils.ResourceUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/** 新华新的穿刺执行界面，只负责重新穿刺
 * Created by li on 2017/3/22.
 */

public class XHChuanciExecuteSingleActivity extends BaseScanTestTempleActivity {
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
    @InjectView(R.id.title_text_view)
    TextView mTitleTextView;
    @InjectView(R.id.cbBottle)
    CheckBox cbBottle;
    @InjectView(R.id.cbEnd)
    CheckBox cbEnd;
    @InjectView(R.id.cbLiu)
    CheckBox cbLiu;
    @InjectView(R.id.tvDs)
    TextView tvDs;
    @InjectView(R.id.tvUnit)
    TextView tvUnit;
    @InjectView(R.id.tvPc)
    TextView tvPc;
    @InjectView(R.id.tvYf)
    TextView tvYf;
    /**
     * 显示时间
     */
    @InjectView(R.id.tvSj)
    TextView tvSj;

    @InjectView(R.id.tvEnd)
    TextView tvEnd;

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
    @InjectView(R.id.topLayout)
    LinearLayout mTopLayout;
//    @InjectView(R.id.showMessage)
//    TextView mShowMessage;
    @InjectView(R.id.contentLayout)
    ScrollView mContentLayout;
    @InjectView(R.id.handlerContent)
    LinearLayout mHandlerContent;
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

    private String deptID;
    private boolean isUseSeatNo = false;//是否启用绑定座位
    private String[] inputReason;
    private BottleModel currentBottle;
    private List<BottleModel> bottleModels;
    private List<BottleModel> runningBottles;
    private DrugAdapter drugAdapter;
    private ProgressGenerator progressGenerator;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_new_excute_single);
    }

    @Override
    public void initView() {
        InitCombox();
        deptID = XmlDB.getInstance(context).getKeyString("deptID", "1");
        if(deptID.equals("1")){
            deptID="儿科输液";
        }else if(deptID.equals("2")){
            deptID="成人输液";
        }
        isUseSeatNo = XmlDB.getInstance(context).getKeyBooleanValue("isUseSeatNo", false);
        if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
            cbEnd.setVisibility(View.GONE);
            tvEnd.setVisibility(View.GONE);
            divider.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void initData() {
        /**
         * 通过pid或者iid查找该人所有瓶贴
         */
        loadBottle(LocalSetting.CurrentBottle);
    }

    /**
     * 初始化combox
     */
    private void InitCombox() {
        if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex ||RoleCategory.ZHONGJI.getKey() == LocalSetting.RoleIndex) {
            otherReason.setVisibility(View.VISIBLE);
            inputReason = ResourceUtils.getResouce4Arrays(context, R.array.arround_unnormal_reason);
            cboEditReason.setFocusable(false);
            cboEditReason.getEditText().setEnabled(false);
            cboEditReason.getEditText().setTextColor(Color.BLACK);
            cboEditReason.setData(inputReason);
        } else {
            otherReason.setVisibility(View.GONE);
        }
    }

    private void loadBottle(BottleModel bottle) {
        currentBottle = bottle;
        if (currentBottle == null) {
//            btnSeat.setEnabled(false);
        } else {
//            if (StringUtils.StringIsEmpty(bottle.PeopleInfo.SeatNo)) {
//                if(!isUseSeatNo){
//                    btnSeat.setVisibility(View.GONE);
//                } else{
//                    btnSeat.setText("还未分配座位");
//                }
//                btnSeat.setText("还未分配座位");
//            } else {
//                btnSeat.setText(bottle.PeopleInfo.SeatNo);
//            }
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
                            + " "
                            + LocalSetting.bottletotle+"袋"

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

//                Log.d(TAG, "currentBottle.DrugDetails==null");
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
//                initEndAndSeatLayouts(bottle);
                drugAdapter = new DrugAdapter(context, bottle.DrugDetails);
                listView.setAdapter(drugAdapter);
                ListViewUtils.setListViewHeightBasedOnChildren(listView);
                if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
                    if (QueueStatusCategory.WAITING.getKey() == currentBottle.PeopleInfo.Status) {
//                        doCallHer();
                    } else if (QueueStatusCategory.CALLED.getKey() == currentBottle.PeopleInfo.Status) {
//                        doCallHer();
                    } else if (QueueStatusCategory.FINISHED.getKey() == currentBottle.PeopleInfo.Status) {
                        UIHepler.showToast(XHChuanciExecuteSingleActivity.this, "重新穿刺病人"); //不呼叫
                    }
                }
            }

        }
    }
    private void initEndAndSeatLayouts(BottleModel bottle) {
//        runningBottles = new ArrayList<BottleModel>();
//        /**
//         * 如果没有输液中...隐藏 结束上一瓶
//         */
//        if (RoleCategory.SHUYE.getKey() == LocalSetting.RoleIndex) {
//            List<BottleModel> allBottles = infusionDetailDAO.getBottleByPatid(bottle.PeopleInfo.PatId);
//            for (BottleModel bo : allBottles) {
//                if (BottleStatusCategory.INFUSIONG.getKey() == bo.BottleStatus) {
//                    bo.BottleStatus = BottleStatusCategory.HADINFUSE.getKey();
//                    bo.InfusionCore = LocalSetting.CurrentAccount.Id;
//                    bo.InfusionDate = DateUtils.getCurrentDate("yyyyMMdd");
//                    bo.InfusionTime = DateUtils.getCurrentDate("HHmmss");
//                    runningBottles.add(bo);
//                }
//            }
//            cbEnd.setChecked(runningBottles.size() > 0);
//
//        }
    }

    @Override
    public void initListener() {
        mTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
        cbLiu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    LocalSetting.CurrentBottle.LZZ="留";
                }else{
                    LocalSetting.CurrentBottle.LZZ="";
                }
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 执行提交操作，和服务器进行交互
                 */
                if(StringUtils.getString(currentBottle.DiagnoseName).equals("1")){
                    Intent intent=new Intent(XHChuanciExecuteSingleActivity.this, CheckActivity.class);
                    startActivityForResult(intent,9);
                }else {
                    btnOK.setEnabled(false);
                    excute();
                }
            }
        });
    }

    private void excute(){
        progressGenerator = new ProgressGenerator(btnOK);
        progressGenerator.start();
        bottleModels=new ArrayList<BottleModel>();
        currentBottle.BottleStatus = BottleStatusCategory.INFUSIONG.getKey();
        currentBottle.InfusionCore = LocalSetting.CurrentAccount.UserName;
        currentBottle.InfusionName = LocalSetting.CurrentAccount.FullName;
        currentBottle.InfusionDate = DateUtils.getCurrentDate("yyyyMMdd");
        currentBottle.InfusionTime = DateUtils.getCurrentDate("HHmmss");
        currentBottle.PeopleInfo.Status = QueueStatusCategory.FINISHED.getKey();
        currentBottle.CheckCore=LocalSetting.CurrentCheck.UserName;
        currentBottle.CheckName=LocalSetting.CurrentCheck.FullName;
        currentBottle.CheckDate=DateUtils.getCurrentDate("yyyyMMdd");
        currentBottle.CheckTime=DateUtils.getCurrentDate("HHmmss");
        bottleModels.add(currentBottle);
        putData();
    }
    private void putData(){
        String acountId = XmlDB.getInstance(XHChuanciExecuteSingleActivity.this).getKeyString("AcountId","");
        String postData = JacksonHelper.model2String(bottleModels);
        String url = ChuanCiApi.url_updateBottlesAndQueues(acountId,deptID);
        RestClient.put(XHChuanciExecuteSingleActivity.this, url, postData, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                progressGenerator.fail();
                com.jasonchen.base.utils.Log.d("本地数据上传成功");
                setResult(8);
                XHChuanciExecuteSingleActivity.this.finish();
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                progressGenerator.fail();
                com.jasonchen.base.utils.Log.d("本地数据上传失败");
            }
        });

    }
    @Override
    public void initIntent() {

    }

    @Override
    protected void receiverPatientId(String patientId) {

    }

    @Override
    protected void receiverBottleId(String patientId, String bottleId) {

    }
}
