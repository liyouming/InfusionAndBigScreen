package com.fugao.infusion.xinhua;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.chuaici.ChuanCiActivity;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.QueueStatusCategory;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.model.PatrolModel;
import com.fugao.infusion.paiyao.CheckActivity;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.UIHelper;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.view.AnimatedExpandableListView;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.DateUtils;
import com.jasonchen.base.utils.JacksonHelper;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/** 新华穿刺
 * Created by li on 2017/3/21.
 */

public class XinHuaChuanCiPeopleFragment extends BaseFragmentV4 {
    @InjectView(R.id.count_id_chuanci)
    TextView count_id_chuanci;
    @InjectView(R.id.xinhua_listView)
    AnimatedExpandableListView listview;
    @InjectView(R.id.xinhua_progressContainer)
    LinearLayout progressContainer;
    @InjectView(R.id.xinhuachuanci_search)
    ImageView xinhuachuanci_search;
    private View currentView;
    private ChuanCiActivity activity;
    private String count_id_s;
    /**
     * 对象初始化
     */
    private static ArrayList<GroupBottleModel> originalGroupBottles;
    private ArrayList<GroupBottleModel> groupBottleModels = new ArrayList<GroupBottleModel>();
    private XHChuanCiGroupBottleAdapter bottleAdapter;
    private ProgressDialog progressDialog;
    private int currentBottleStatus=0;
    private BottleModel currentBottle;
    private List<BottleModel> bottleModels;
    private String currentBottleId;
    private String deptID;
    private int operationtype=0;
    private boolean canExcute=true;
    private boolean iscancle=false;
    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView=inflater.inflate(R.layout.fragment_xinhuachuanci_layout, container, false);
        return currentView;
    }

    @Override
    public void initView(View currentView) {
        activity = (ChuanCiActivity) fatherActivity;
    }

    @Override
    public void initData() {
        progressDialog = new ProgressDialog(fatherActivity);
        progressDialog.setMessage("加载中...");
        progressDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        Constant.CURRENTPATIENT="";
        deptID = XmlDB.getInstance(fatherActivity).getKeyString("deptID", "1");
        if(deptID.equals("1")){
            deptID="儿科输液";
        }else if(deptID.equals("2")){
            deptID="成人输液";
        }
        count_id_s= XmlDB.getInstance(fatherActivity).getKeyString("AcountId","");
//        count_id_chuanci.setText(count_id_s);
        count_id_chuanci.setText(LocalSetting.CurrentAccount.FullName);
        listview.setVisibility(View.GONE);
        progressContainer.setVisibility(View.VISIBLE);
        originalGroupBottles = new ArrayList<GroupBottleModel>();
        bottleAdapter = new XHChuanCiGroupBottleAdapter(fatherActivity, originalGroupBottles);
        listview.setAdapter(bottleAdapter);
        listview.expandGroupWithAnimation(0);
    }
    public void getData(final String patientId){
        if(!StringUtils.StringIsEmpty(Constant.CURRENTPATIENT)&&Constant.CURRENTPATIENT.equals(patientId)&&!iscancle){
//            currentBottle=new BottleModel();
//            for(int i=0;i<originalGroupBottles.get(0).items.size();i++){
//                if(originalGroupBottles.get(0).items.get(i).BottleStatus==4){
//                    currentBottle=originalGroupBottles.get(0).items.get(i);
//                }
//            }
//            PatrolModel patrolDetailBean = new PatrolModel();
//            patrolDetailBean.TargetContent = "穿刺异常";
//            patrolDetailBean.BottleId = currentBottle.BottleId;
//            patrolDetailBean.PatrolerNo = LocalSetting.CurrentAccount.UserName;
//            patrolDetailBean.PatrolerName = LocalSetting.CurrentAccount.FullName;
//            patrolDetailBean.PatrolTime = DateUtils.getStandarCurrentDate();
//            patrolDetailBean.Content = "肿针、针头脱落";
//            patrolDetailBean.Status = -1; //异常巡视
//            patrolDetailBean.Type =2;
//            patrolDetailBean.AboutNo = currentBottle.PeopleInfo.PatId;
//            ArrayList<PatrolModel> updatePatrol = new ArrayList<PatrolModel>();
//            updatePatrol.add(patrolDetailBean);
//            currentBottle.AboutPatrols =new ArrayList<PatrolModel>();
//            currentBottle.AboutPatrols.addAll(updatePatrol);
//            operationtype=2;
//            execute();
            currentBottle=new BottleModel();
            for(int i=0;i<originalGroupBottles.get(0).items.size();i++){
                if(originalGroupBottles.get(0).items.get(i).BottleStatus==4){
                    currentBottle=originalGroupBottles.get(0).items.get(i);
//                    currentBottleStatus=originalGroupBottles.get(0).items.get(i).BottleStatus;
                }
            }
//            LocalSetting.CurrentBottle = currentBottle;
            if(StringUtils.StringIsEmpty(currentBottle.BottleId)){
                UIHepler.showToast(fatherActivity,"此人不在输液中");
            }else {
                LocalSetting.CurrentBottle = currentBottle;
                Intent intent=new Intent(fatherActivity,XHChuanciExecuteSingleActivity.class);
                startActivityForResult(intent,7);
            }
        }else {
            progressDialog.show();
            RestClient.get(ChuanCiApi.Url_GetBottlesByPid(patientId), new BaseAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, String s) {
                    progressDialog.dismiss();
                    iscancle=false;
                    ArrayList<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
                    groupBottleModels=new ArrayList<GroupBottleModel>();
                    if (bottleModels!=null&&bottleModels.size() > 0){
                        progressContainer.setVisibility(View.GONE);
                        listview.setVisibility(View.VISIBLE);
                        Constant.CURRENTPATIENT=patientId;
                        originalGroupBottles = InfoUtils.toGroup(bottleModels);
                        groupBottleModels.addAll(originalGroupBottles);
                        bottleAdapter.changeView(groupBottleModels);
                    }else{
                        listview.setVisibility(View.GONE);
                        progressContainer.setVisibility(View.VISIBLE);
                        Constant.CURRENTPATIENT="";
                    }
                }

                @Override
                public void onFailure(int i, Throwable throwable, String s) {
                    progressDialog.dismiss();
                    iscancle=false;
                    UIHelper.showToast(fatherActivity,"请求失败，请重试");
                    listview.setVisibility(View.GONE);
                    progressContainer.setVisibility(View.VISIBLE);
                    Constant.CURRENTPATIENT="";
                }
            });
        }
    }
    public void redirect2ExecuteBottleId(String bottleId){
        currentBottleId=bottleId;
        currentBottle=new BottleModel();
        currentBottleStatus=0;
        canExcute=true;
        for(int i=0;i<originalGroupBottles.get(0).items.size();i++){
            if(originalGroupBottles.get(0).items.get(i).BottleId.equals(bottleId)){
                currentBottle=originalGroupBottles.get(0).items.get(i);
                currentBottleStatus=originalGroupBottles.get(0).items.get(i).BottleStatus;
            }
            if(originalGroupBottles.get(0).items.get(i).BottleStatus==4&&!originalGroupBottles.get(0).items.get(i).BottleId.equals(bottleId)){
                canExcute=false;
            }
        }

        if(currentBottle!=null&& !StringUtils.StringIsEmpty(currentBottle.BottleId)){
            if(canExcute){
                if(currentBottleStatus==4){
                    AlertDialog alertDialog=new AlertDialog.Builder(fatherActivity).setTitle("温馨提醒！").setCancelable(false)
                            .setMessage("当前瓶贴已穿刺！请选择操作").
                                    setPositiveButton("撤销穿刺", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            String url=ChuanCiApi.cancleChuanCiByBottleId();
                                            String json=String2InfusionModel.bottleModel2String(currentBottle);
                                            progressDialog.show();
                                            RestClient.put(fatherActivity, url, json, new BaseAsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int i, String s) {
                                                    progressDialog.dismiss();
                                                    UIHepler.showToast(fatherActivity,"取消穿刺成功");
                                                    iscancle=true;
                                                    getData(Constant.CURRENTPATIENT);
                                                }

                                                @Override
                                                public void onFailure(int i, Throwable throwable, String s) {
                                                    progressDialog.dismiss();
                                                    UIHepler.showToast(fatherActivity,"取消穿刺失败，请重试!");
                                                }
                                            });

                                        }
                                    }).create();
//                                    setNegativeButton("重新穿刺", new DialogInterface.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(DialogInterface arg0, int arg1) {
//                                            LocalSetting.CurrentBottle = currentBottle;
//                                            Intent intent=new Intent(fatherActivity,XHChuanciExecuteSingleActivity.class);
//                                            startActivityForResult(intent,7);
//                                        }
//                                    }).create();
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.show();
                }else if(currentBottleStatus==2){
                    AlertDialog alertDialog=new AlertDialog.Builder(fatherActivity).setTitle("温馨提醒！").setCancelable(false)
                            .setMessage("改组药还没配液，是否继续").
                                    setPositiveButton("是", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            if(StringUtils.getString(currentBottle.DiagnoseName).equals("1")){
                                                Intent intent=new Intent(fatherActivity, CheckActivity.class);
                                                startActivityForResult(intent,9);
                                            }else {
                                                operationtype=1;
                                                execute();
                                            }
                                        }
                                    }).
                                    setNegativeButton("否", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    }).create();
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.show();
                }else if(currentBottleStatus==3){
                    if(StringUtils.getString(currentBottle.DiagnoseName).equals("1")){
                        Intent intent=new Intent(fatherActivity, CheckActivity.class);
                        startActivityForResult(intent,9);
                    }else {
                        operationtype=1;
                        execute();
                    }
                }else if(currentBottleStatus==1){
                    AlertDialog alertDialog=new AlertDialog.Builder(fatherActivity).setTitle("温馨提醒！").setCancelable(false)
                            .setMessage("改组药还没排药，是否继续").
                                    setPositiveButton("是", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            if(StringUtils.getString(currentBottle.DiagnoseName).equals("1")){
                                                Intent intent=new Intent(fatherActivity, CheckActivity.class);
                                                startActivityForResult(intent,9);
                                            }else {
                                                operationtype=1;
                                                execute();
                                            }
                                        }
                                    }).
                                    setNegativeButton("否", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    }).create();
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.show();
                }else{
                    UIHelper.showToast(fatherActivity,"改组药已结束");
                }
            }else {
                UIHepler.showToast(fatherActivity,"该病人正在输液中");
            }
        }else {
            UIHelper.showToast(fatherActivity,"找不到该瓶贴");
        }

    }
    public void execute(){
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

    /**
     * 上传穿刺数据
     */
    private void putData(){
        String acountId = XmlDB.getInstance(fatherActivity).getKeyString("AcountId","");
        String postData = JacksonHelper.model2String(bottleModels);
        String url = ChuanCiApi.url_updateBottlesAndQueues(acountId,deptID);
        progressDialog.show();
        RestClient.put(fatherActivity, url, postData, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                progressDialog.dismiss();
                for(int j=0;j<originalGroupBottles.get(0).items.size();j++){
                    if(originalGroupBottles.get(0).items.get(j).BottleId.equals(currentBottleId)){
                        BottleModel bottleModel=originalGroupBottles.get(0).items.get(j);
                        bottleModel.BottleStatus = currentBottle.BottleStatus;
                        bottleModel.InfusionCore = currentBottle.InfusionCore;
                        bottleModel.InfusionName = currentBottle.InfusionName;
                        bottleModel.InfusionDate = currentBottle.InfusionDate;
                        bottleModel.InfusionTime = currentBottle.InfusionTime;
                        bottleModel.PeopleInfo.Status = currentBottle.PeopleInfo.Status;
                        bottleModel.CheckCore=currentBottle.CheckCore;
                        bottleModel.CheckName=currentBottle.CheckName;
                        bottleModel.CheckDate=currentBottle.CheckDate;
                        bottleModel.CheckTime=currentBottle.CheckTime;
                    }
                }
                bottleAdapter.changeView(originalGroupBottles);
                if(operationtype==1){
                    UIHepler.showToast(fatherActivity,"穿刺成功");
                }else if(operationtype==2){
                    UIHepler.showDilalog(fatherActivity,"重新穿刺成功");
                }
                com.jasonchen.base.utils.Log.d("本地数据上传成功");
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                progressDialog.dismiss();
//                UIHelper.showToast(fatherActivity,"上传数据失败，请重试");
                if(operationtype==1){
                    UIHepler.showToast(fatherActivity,"穿刺失败");
                }else if(operationtype==2){
                    UIHepler.showDilalog(fatherActivity,"重新穿刺失败");
                }
                com.jasonchen.base.utils.Log.d("本地数据上传失败");
            }
        });
    }
    @Override
    public void initListener() {
        xinhuachuanci_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTextDialog("请输入流水号");
            }
        });
    }
    private void showEditTextDialog(String title){
        LayoutInflater inflater = LayoutInflater.from(fatherActivity);
        View view = inflater.inflate(R.layout.view_edittext_layout, null);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_edittext);
        LinearLayout view_height = (LinearLayout) view.findViewById(R.id.dialog_view);
        view_height.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40));
        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        new AlertDialog.Builder(fatherActivity).setTitle(title)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputText = editText.getText().toString();
                        if (StringUtils.StringIsEmpty(inputText)){
                            editText.setError("不能输入为空，请重新输入！");
                            dialogIsClose(dialog, false);
                        }else {
                            dialogIsClose(dialog, true);
                            handlerManualDialog(dialog, inputText);
                            String date= DateUtils.getCurrentDate("yyyyMMdd");
                            getData(date,inputText);
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
            UIHepler.showToast(fatherActivity, "输入的格式错误，请重新再输入！");
        }
    }
    /**
     * 根据日期、流水号去获取数据
     */
    private void getData(String date,String lsh){
        String url=ChuanCiApi.getBottlesByDateAndLsh(date,lsh,"1_2_3_4_5",deptID);
        progressDialog.show();
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                progressDialog.dismiss();
                ArrayList<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
                groupBottleModels=new ArrayList<GroupBottleModel>();
                if (bottleModels!=null&&bottleModels.size() > 0){
                    progressContainer.setVisibility(View.GONE);
                    listview.setVisibility(View.VISIBLE);
                    originalGroupBottles = InfoUtils.toGroup(bottleModels);
                    Constant.CURRENTPATIENT=originalGroupBottles.get(0).PatId;
                    groupBottleModels.addAll(originalGroupBottles);
                    bottleAdapter.changeView(groupBottleModels);
                }else{
                    listview.setVisibility(View.GONE);
                    progressContainer.setVisibility(View.VISIBLE);
                    Constant.CURRENTPATIENT="";
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                progressDialog.dismiss();
                UIHelper.showToast(fatherActivity,"请求失败，请重试");
                listview.setVisibility(View.GONE);
                progressContainer.setVisibility(View.VISIBLE);
                Constant.CURRENTPATIENT="";
            }
        });
    }
    public void onRefresh(){

    }
    /**
     * 点击跳转到单次执行界面
     *
     * @param bottle 拼贴对象
     */
//    public void redirect2ExecuteSingleByClick(final BottleModel bottle) {
//        LocalSetting.IsOpenByScan = true;
//        LocalSetting.CurrentBottle =null;
//        LocalSetting.CurrentBottle = bottle;
//        if(bottle.totle>0){
//            LocalSetting.bottletotle=bottle.totle;
//        }else{
//            for(int i=0;i<groupBottleModels.size();i++){
//                if(groupBottleModels.get(i).PatId.equals(bottle.PeopleInfo.PatId)){
//                    LocalSetting.bottletotle=groupBottleModels.get(i).bottletotle;
//                    break;
//                }
//            }
//        }
//        checkedIsOverDue(bottle);
//    }
    /**
     * 检查该组药是否已经过期
     *
     * @PARAM BOTTLE
     */
    private void checkedIsOverDue(final BottleModel bottle) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==7&&resultCode==8){
            iscancle=true;
            UIHepler.showToast(fatherActivity,"重新穿刺成功");
            getData(Constant.CURRENTPATIENT);
        }else if(requestCode==9&&resultCode==10){
            operationtype=1;
            execute();
        }
    }
}
