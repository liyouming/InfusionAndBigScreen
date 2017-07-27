package com.fugao.infusion.peiye;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fugao.infusion.InfusionApplication;
import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.DrugDetailModel;
import com.fugao.infusion.model.NurseAccountModel;
import com.fugao.infusion.view.ProgressGenerator;
import com.fugao.infusion.view.processbutton.iml.ActionProcessButton;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/** 新华配液
 * Created by li on 2016/11/30.
 */

public class NewPeiYePeopleFragment extends BaseFragmentV4{
    @InjectView(R.id.newpeiye_null)
    TextView newpeiye_null;
    @InjectView(R.id.newpeiye_listView)
    ListView newpeiye_listView;
    @InjectView(R.id.btnOK)
    ActionProcessButton btnOK;
    @InjectView(R.id.handlerContent)
    LinearLayout handlerContent;
    @InjectView(R.id.newpeiye_username)
    TextView newpeiye_username;
    private List<DrugDetailModel> drugDetailModels;
    private NewPeiyeAdapter newPeiyeAdapter;
    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_newpeiye_layout, container,false);
    }

    @Override
    public void initView(View currentView) {

    }

    @Override
    public void initData() {
        drugDetailModels=new ArrayList<DrugDetailModel>();
        newPeiyeAdapter=new NewPeiyeAdapter(fatherActivity,drugDetailModels);
        newpeiye_listView.setAdapter(newPeiyeAdapter);
        newpeiye_null.setVisibility(View.VISIBLE);
        newpeiye_listView.setVisibility(View.GONE);
        newpeiye_username.setText(LocalSetting.CurrentAccount.FullName);
    }

    @Override
    public void initListener() {
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 执行提交操作，和服务器进行交互
                 */
                btnOK.setEnabled(false);
                excute();
            }
        });
    }
    private void excute(){
        final ProgressGenerator progressGenerator = new ProgressGenerator(btnOK);
        progressGenerator.start();
        String url= InfoApi.updatePeiyeBybottleId(LocalSetting.CurrentBottle.BottleId,LocalSetting.CurrentAccount.UserName,LocalSetting.CurrentAccount.FullName);
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                if(s.contains("ok")){
                    btnOK.setEnabled(true);
                    progressGenerator.fail();
                    Toast.makeText(fatherActivity,"上传成功",Toast.LENGTH_SHORT).show();
                    newpeiye_null.setVisibility(View.VISIBLE);
                    newpeiye_listView.setVisibility(View.GONE);
                    handlerContent.setVisibility(View.GONE);
                }else if(s.contains("Repeat")){
                    btnOK.setEnabled(true);
                    progressGenerator.fail();
                    UIHepler.showDilalog(fatherActivity, "此瓶贴已经配液完成");
                    newpeiye_null.setVisibility(View.VISIBLE);
                    newpeiye_listView.setVisibility(View.GONE);
                    handlerContent.setVisibility(View.GONE);
                }else if(s.contains("notmatch")){
                    btnOK.setEnabled(true);
                    progressGenerator.fail();
                    UIHepler.showDilalog(fatherActivity, "不存在的瓶贴");
                    newpeiye_null.setVisibility(View.VISIBLE);
                    newpeiye_listView.setVisibility(View.GONE);
                    handlerContent.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                btnOK.setEnabled(true);
                Toast.makeText(fatherActivity,"上传失败",Toast.LENGTH_SHORT).show();
                progressGenerator.fail();
            }
        });
    }
    /**
     * 执行瓶贴并进行判断
     *
     * @param bottle 拼贴对象
     */
    public void redirect2Execute(BottleModel bottle) {
        LocalSetting.CurrentBottle = bottle;
        if (BottleStatusCategory.WAITINGHANDLE.getKey() == bottle.BottleStatus) {
            UIHepler.showToast(fatherActivity,"该组还未排药");
        } else if (BottleStatusCategory.HADHANDLE.getKey() == bottle.BottleStatus) {
            if(bottle.DrugDetails!=null&&bottle.DrugDetails.size()>0){
                newPeiyeAdapter.change(bottle.DrugDetails);
                newpeiye_null.setVisibility(View.GONE);
                newpeiye_listView.setVisibility(View.VISIBLE);
                handlerContent.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(fatherActivity,"没有瓶贴明细",Toast.LENGTH_LONG).show();
            }
        } else {
            UIHepler.showToast(fatherActivity,"该组药已经配液!");
        }
    }
}
