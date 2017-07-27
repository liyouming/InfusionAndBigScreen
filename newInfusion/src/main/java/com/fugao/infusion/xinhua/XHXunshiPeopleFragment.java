package com.fugao.infusion.xinhua;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.QueueStatusCategory;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.paiyao.CheckActivity;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.UIHelper;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.view.AnimatedExpandableListView;
import com.fugao.infusion.xunshi.NewHandlerActivity;
import com.fugao.infusion.xunshi.XunshiActivity;
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

/** 新华新流程巡视
 * Created by li on 2017/3/29.
 */

public class XHXunshiPeopleFragment extends BaseFragmentV4 implements SwipeRefreshLayout.OnRefreshListener{
    @InjectView(R.id.count_id_xunshi)
    TextView count_id_xunshi;
    @InjectView(R.id.xinhua_xunshi_listView)
    AnimatedExpandableListView listView;
    @InjectView(R.id.xinhua_progressContainer)
    LinearLayout progressContainer;
    @InjectView(R.id.xinhuaxunshii_search)
    ImageView xinhuaxunshii_search;
    private View currentView;
    private XHXunshiActivity activity;
    private String count_id_s;
    /**
     * 对象初始化
     */
    private static ArrayList<GroupBottleModel> originalGroupBottles;
    private ArrayList<GroupBottleModel> groupBottleModels = new ArrayList<GroupBottleModel>();
    private XHXunshiGroupBottleAdapter bottleAdapter;
    private ProgressDialog progressDialog;
    private String currentBottleId;
    private BottleModel currentBottle;
    private int currentBottleStatus=0;
    private List<BottleModel> bottleModels;
    private String deptID;
    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView=inflater.inflate(R.layout.fragmeng_xh_xunshi_layout, container, false);
        return currentView;
    }

    @Override
    public void initView(View currentView) {
        activity= (XHXunshiActivity) fatherActivity;
    }

    @Override
    public void initData() {
        progressDialog = new ProgressDialog(fatherActivity);
        progressDialog.setMessage("加载中...");
        progressDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        count_id_s= XmlDB.getInstance(fatherActivity).getKeyString("AcountId","");
        deptID = XmlDB.getInstance(fatherActivity).getKeyString("deptID", "1");
        if(deptID.equals("1")){
            deptID="儿科输液";
        }else if(deptID.equals("2")){
            deptID="成人输液";
        }
//        count_id_xunshi.setText(count_id_s);
        count_id_xunshi.setText(LocalSetting.CurrentAccount.FullName);
        listView.setVisibility(View.GONE);
        progressContainer.setVisibility(View.VISIBLE);
        originalGroupBottles = new ArrayList<GroupBottleModel>();
        bottleAdapter = new XHXunshiGroupBottleAdapter(fatherActivity, originalGroupBottles);
        listView.setAdapter(bottleAdapter);
        listView.setAdapter(bottleAdapter);
        listView.expandGroupWithAnimation(0);
    }

    @Override
    public void initListener() {
        xinhuaxunshii_search.setOnClickListener(new View.OnClickListener() {
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
                    originalGroupBottles = InfoUtils.toGroup(bottleModels);
                    Constant.CURRENTPATIENT=originalGroupBottles.get(0).PatId;
                    groupBottleModels.addAll(originalGroupBottles);
                    bottleAdapter.changeView(groupBottleModels);
                    progressContainer.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }else{
                    listView.setVisibility(View.GONE);
                    progressContainer.setVisibility(View.VISIBLE);
                    Constant.CURRENTPATIENT="";
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                progressDialog.dismiss();
                UIHelper.showToast(fatherActivity,"请求失败，请重试");
                listView.setVisibility(View.GONE);
                progressContainer.setVisibility(View.VISIBLE);
                Constant.CURRENTPATIENT="";
            }
        });
    }
    public void getData(final String patientId){
        progressDialog.show();
        RestClient.get(ChuanCiApi.Url_GetBottlesByPid(patientId), new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                progressDialog.dismiss();
                ArrayList<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
                groupBottleModels=new ArrayList<GroupBottleModel>();
                if (bottleModels!=null&&bottleModels.size() > 0){
                    progressContainer.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    Constant.CURRENTPATIENT=patientId;
                    originalGroupBottles = InfoUtils.toGroup(bottleModels);
                    groupBottleModels.addAll(originalGroupBottles);
                    bottleAdapter.changeView(groupBottleModels);
                }else{
                    listView.setVisibility(View.GONE);
                    progressContainer.setVisibility(View.VISIBLE);
                    Constant.CURRENTPATIENT="";
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                progressDialog.dismiss();
                UIHelper.showToast(fatherActivity,"请求失败，请重试");
                listView.setVisibility(View.GONE);
                progressContainer.setVisibility(View.VISIBLE);
                Constant.CURRENTPATIENT="";
            }
        });
    }
    public void redirect2ExecuteBottleId(String bottleId){
        currentBottleId=bottleId;
        for(int i=0;i<originalGroupBottles.get(0).items.size();i++){
            if(originalGroupBottles.get(0).items.get(i).BottleId.equals(bottleId)){
                currentBottle=originalGroupBottles.get(0).items.get(i);
                currentBottleStatus=originalGroupBottles.get(0).items.get(i).BottleStatus;
            }
        }
        if(currentBottle!=null&& !StringUtils.StringIsEmpty(currentBottle.BottleId)){
            if(currentBottleStatus==1){
                AlertDialog alertDialog=new AlertDialog.Builder(fatherActivity).setTitle("温馨提醒！").setCancelable(false)
                        .setMessage("改组药还没排药，是否继续").
                                setPositiveButton("是", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        if(StringUtils.getString(currentBottle.DiagnoseName).equals("1")){
                                            Intent intent=new Intent(fatherActivity, CheckActivity.class);
                                            startActivityForResult(intent,9);
                                        }else {
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
                execute();
            }else if(currentBottleStatus==4){
                LocalSetting.CurrentBottle = currentBottle;
                Intent intent = new Intent();
                intent.setClass(activity, XHHandlerActivity.class);
                startActivity(intent);
            }else if(currentBottleStatus==5){
                UIHelper.showToast(activity,"该组药已经结束");
            }else if(currentBottleStatus==6){
                UIHelper.showToast(activity,"该组药已经作废");
            }
        }else{
            UIHelper.showToast(fatherActivity,"找不到该瓶贴");
        }
    }
    public void execute(){
        bottleModels=new ArrayList<BottleModel>();
        currentBottle.BottleStatus=BottleStatusCategory.INFUSIONG.getKey();
        currentBottle.InfusionCore=LocalSetting.CurrentAccount.UserName;
        currentBottle.InfusionDate=DateUtils.getCurrentDate("yyyyMMdd");
        currentBottle.InfusionTime=DateUtils.getCurrentDate("HHmmss");
        currentBottle.InfusionName=LocalSetting.CurrentAccount.FullName;
        bottleModels.add(currentBottle);
        putData();
    }

    /**
     * 上传穿刺数据
     */
    private void putData(){
        String jsonString = String2InfusionModel.bottleModles2String((ArrayList<BottleModel>) bottleModels);
        progressDialog.show();
        RestClient.put(activity, InfoApi.url_updateBottleAndfinishRuning(Constant.CURRENTPATIENT), jsonString, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                progressDialog.dismiss();
                getData(Constant.CURRENTPATIENT);
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                UIHelper.showToast(activity,"上传失败，请重试");
            }
        });

    }

    @Override
    public void onRefresh() {
        getData(Constant.CURRENTPATIENT);
    }
}
