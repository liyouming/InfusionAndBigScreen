package com.fugao.infusion.peiye;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fugao.infusion.LoginActivity;
import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.UIHelper;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.DateUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.InjectView;

/** 新华配液
 * Created by li on 2016/12/20.
 */

public class XinhuaPeiyeFragment extends BaseFragmentV4 {
    @InjectView(R.id.xinhuapeiye_username)
    TextView xinhuapeiye_username;
    @InjectView(R.id.xinhuapeiye_null)
    TextView xinhuapeiye_null;
//    @InjectView(R.id.contentLayout)
//    ScrollView contentLayout;
    @InjectView(R.id.xinhuapeiye_name)
    TextView xinhuapeiye_name;
    @InjectView(R.id.xinhuapeiye_patid)
    TextView xinhuapeiye_patid;
    @InjectView(R.id.xinhuapeiye_lsh)
    TextView xinhuapeiye_lsh;
    @InjectView(R.id.xinhuapeiye_listView)
    ListView xinhuapeiye_listView;
    @InjectView(R.id.xinhua_bingren)
    LinearLayout xinhua_bingren;
    @InjectView(R.id.xinhuapeiye_search)
    ImageView search;
    private String curBottleId;
    private ArrayList<BottleModel> bottleModels;
    private ProgressDialog progressDialog;
    private XinhuaPeiyeAdapter xinhuaPeiyeAdapter;
    private BottleModel bottleModel;
    private String deptcode;
    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_xinhuapeiye_layout, container,false);
    }

    @Override
    public void initView(View currentView) {

    }

    @Override
    public void initData() {
        deptcode= XmlDB.getInstance(fatherActivity).getKeyString("deptID", "1");
        if(deptcode.equals("1")){
            deptcode="儿科输液";
        }else if(deptcode.equals("2")){
            deptcode="成人输液";
        }
        progressDialog = new ProgressDialog(fatherActivity);
        progressDialog.setMessage("加载中...");
        progressDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置点击进度对话框外的区域对话框不消失
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        bottleModels=new ArrayList<BottleModel>();
        xinhuaPeiyeAdapter=new XinhuaPeiyeAdapter(fatherActivity,bottleModels);
        xinhuapeiye_listView.setAdapter(xinhuaPeiyeAdapter);
        xinhuapeiye_username.setText(LocalSetting.CurrentAccount.FullName);
    }

    @Override
    public void initListener() {
        search.setOnClickListener(new View.OnClickListener() {
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
        String url=ChuanCiApi.getBottlesByDateAndLsh(date,lsh,"1_2_3",deptcode);
        progressDialog.show();
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                progressDialog.dismiss();
                bottleModels= String2InfusionModel.string2BottleModels(s);
                if(bottleModels!=null&&bottleModels.size()>0){
                    setView();
                    xinhuaPeiyeAdapter.notify(bottleModels);
                }else {
                    xinhuapeiye_null.setText("没有数据");
                    xinhuapeiye_null.setVisibility(View.VISIBLE);
                    xinhua_bingren.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                progressDialog.dismiss();
                UIHepler.showToast(fatherActivity,"请求失败，请重试");
            }
        });
    }

    private void setView(){
        xinhuapeiye_null.setVisibility(View.GONE);
//        contentLayout.setVisibility(View.VISIBLE);
        xinhua_bingren.setVisibility(View.VISIBLE);
        xinhuapeiye_name.setText(bottleModels.get(0).PeopleInfo.Name);
        xinhuapeiye_patid.setText(bottleModels.get(0).PeopleInfo.PatId);
        xinhuapeiye_lsh.setText(bottleModels.get(0).PeopleInfo.QueueNo);
    }
    public void bottleIdExecute(String bottleId){
        curBottleId=bottleId;
        if(bottleModels!=null&&bottleModels.size()>0){
            boolean ishas=false;
            boolean isNoOver=false;
            for(int i=0;i<bottleModels.size();i++){
                if(bottleModels.get(i).BottleId.equals(curBottleId)){
                    bottleModel=bottleModels.get(i);
                    ishas=true;
                }
            }
            if(ishas){

            }else{
                for(int i=0;i<bottleModels.size();i++){
                    if(bottleModels.get(i).BottleStatus<=2){
//                        bottleModel=bottleModels.get(i);
                        isNoOver=true;
                        break;
                    }
                }
            }
            if(!ishas){
                if(!isNoOver){
                    getBottleData();
                }else {
                    AlertDialog alertDialog=new AlertDialog.Builder(fatherActivity).setMessage("当前人还有未配液瓶贴,是否拉取新病人瓶贴信息?").setTitle
                            ("温馨提醒！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            getBottleData();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    }).create();
                    alertDialog.getWindow().setGravity(Gravity.CENTER);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
            }else{
                if(bottleModel.BottleStatus<=2){
                    bottleIdExecuteBySd(bottleModel);
                }else if(bottleModel.BottleStatus>2){
                    UIHelper.showToast(fatherActivity,"该瓶贴已经配液完成");
                }
            }
        }else{
            getBottleData();
        }
    }
    public void getBottleData(){
//        String url= ChuanCiApi.getBottlesByone(curBottleId);
        String url= ChuanCiApi.getBottlesByone(curBottleId,deptcode);
        progressDialog.show();
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                progressDialog.dismiss();
                bottleModels= String2InfusionModel.string2BottleModels(s);
                if(bottleModels!=null&&bottleModels.size()>0){
                    setView();
                    xinhuaPeiyeAdapter.notify(bottleModels);
                }else {
                    xinhuapeiye_null.setText("没有数据");
                    xinhuapeiye_null.setVisibility(View.VISIBLE);
                    xinhua_bingren.setVisibility(View.GONE);
//                    UIHepler.showToast(fatherActivity,"没有数据");
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                progressDialog.dismiss();
                UIHepler.showToast(fatherActivity,"请求失败，请重试");
            }
        });
    }
    public void bottleIdExecuteBySd(BottleModel bm){
        bottleModel=bm;
        String url= InfoApi.updatePeiyeBybottleId(bottleModel.BottleId,LocalSetting.CurrentAccount.UserName
                ,LocalSetting.CurrentAccount.FullName,deptcode);
        progressDialog.show();
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                progressDialog.dismiss();
                if(s.contains("ok")){
                    Toast.makeText(fatherActivity,"上传成功",Toast.LENGTH_SHORT).show();
                    for(int j=0;j<bottleModels.size();j++){
                        if(bottleModels.get(j).BottleId.equals(bottleModel.BottleId)){
                            bottleModels.get(j).BottleStatus=3;
                        }
                    }
                    xinhuaPeiyeAdapter.notify(bottleModels);
//                    bottleModels.remove(bm);
//                    if(bottleModels.size()==0){
//                        xinhuapeiye_null.setVisibility(View.VISIBLE);
//                        xinhua_bingren.setVisibility(View.GONE);
//                    }else{
//                        xinhuaPeiyeAdapter.notify(bottleModels);
//                    }

                }else if(s.contains("Repeat")){
                    UIHepler.showDilalog(fatherActivity, "此瓶贴已经配液完成");
                    for(int j=0;j<bottleModels.size();j++){
                        if(bottleModels.get(j).BottleId.equals(bottleModel.BottleId)){
                            bottleModels.get(j).BottleStatus=3;
                        }
                    }
                    xinhuaPeiyeAdapter.notify(bottleModels);
                }else if(s.contains("notmatch")){
                    xinhuaPeiyeAdapter.notify(bottleModels);
                    UIHepler.showDilalog(fatherActivity, "不存在的瓶贴");
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                progressDialog.dismiss();
                UIHepler.showDilalog(fatherActivity, "配液失败，请重试");
            }
        });
    }
}
