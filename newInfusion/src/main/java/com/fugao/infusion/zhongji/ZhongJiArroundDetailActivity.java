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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseTempleActivity;
import com.fugao.infusion.chuaici.DrugAdapter;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.dao.DataBaseInfo;
import com.fugao.infusion.dao.InfusionDetailDAO;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.DrugDetailModel;
import com.fugao.infusion.model.PatrolModel;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.xunshi.AroundDetailAdapter;
import com.fugao.infusion.xunshi.NewHandlerActivity;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.RestClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastAndroid
 * @Location: com.android.fastinfusion.ui.activity.ArroundActivity
 * @Description: TODO
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/10/11 3:07
 * @Modify-date: 修改日期
 * @Modify-description: TODO 修改说明 巡视详情
 * @Modify-author: 修改人
 * @version: V1.0
 */

public class ZhongJiArroundDetailActivity extends BaseTempleActivity {
    private static final String TAG = "Fast-ArroundActivity";

    @InjectView(R.id.listview_drugs)
    ListView listViewDrugs;
    @InjectView(R.id.listview_arround)
    ListView listViewArrounds;

    @InjectView(R.id.more)
    LinearLayout more;
    @InjectView(R.id.title_text_view)
    TextView title_text_view;

    private String pid;
    private String iid;
    private String bid;
    private String bottleId;


    private BottleModel currentBottle;
    public InfusionDetailDAO infusionDetailDAO;
    private DrugAdapter drugAdapter;
    private AroundDetailAdapter patrolAdapter;
    private List<DrugDetailModel> drugs;
    private List<PatrolModel> patrols;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_arroud);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        currentBottle = new BottleModel();
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(ZhongJiArroundDetailActivity.this));
        currentBottle= infusionDetailDAO.getBottleById(bid);
        if(currentBottle==null){
            Log.d(TAG, "currentBottle==null");
            return;
        }else{
            drugs = currentBottle.DrugDetails == null ? new ArrayList<DrugDetailModel>() : currentBottle.DrugDetails;
            patrols = currentBottle.AboutPatrols == null ? new ArrayList<PatrolModel>() : currentBottle.AboutPatrols;
        }


        drugAdapter = new DrugAdapter(context, drugs);
        patrolAdapter = new AroundDetailAdapter(context, patrols);
        listViewDrugs.setAdapter(drugAdapter);
        listViewArrounds.setAdapter(patrolAdapter);
//
        /**
         * 未加载过药品信息 需要获取药品信息
         */
        if (drugs.size() <= 0) {
            getBottle();
        }
    }

    @Override
    public void initListener() {
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent() ;
                Bundle bundle = new Bundle();
                bundle.putString("pid", pid);
                bundle.putString("iid", iid);
                bundle.putString("bid", bid);
                bundle.putBoolean("liuqq", true);
                LocalSetting.CurrentBottle = currentBottle;
                intent.putExtras(bundle);
//                intent.setClass(ArroundDetailActivity.this, HandlerActivity.class);
                intent.setClass(ZhongJiArroundDetailActivity.this, ZhongJiNewHandlerActivity.class);
                startActivity(intent);
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
        pid = getIntent().getStringExtra("pid");
        iid = getIntent().getStringExtra("iid");
        bid = getIntent().getStringExtra("bid");
    }


    private void getBottle() {
        showLoadingDialog("正在获取巡视信息,请稍后...");
        String url = InfoApi.url_getBottleByBottleId(bid);
        Log.e("------------------",url);
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                BottleModel bottleModel = String2InfusionModel.string2BottleModel(s);
                dismissLoadingDialog();
                if (bottleModel != null) {
                    currentBottle = bottleModel;
                    infusionDetailDAO.update(bottleModel, false);//不更新巡视记录
                    if (bottleModel.DrugDetails != null) {
                        drugs.clear();
                        drugs.addAll(bottleModel.DrugDetails);
                        drugAdapter.notifyDataSetChanged();
                    }
                    if (bottleModel.AboutPatrols != null) {
                        patrols.clear();
                        patrols.addAll(bottleModel.AboutPatrols);
                        patrolAdapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                dismissLoadingDialog();
//                InfoUtils.showWifiConnectdDialogFragament(ArroundDetailActivity.this,error);
//                UIHelper.showToast(context, "获取巡视信息失败: " + error.getToastInfo(context));
            }
        });
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.around_detail2, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.more:
                Intent intent= new Intent() ;
                Bundle bundle = new Bundle();
                bundle.putString("pid", pid);
                bundle.putString("iid", iid);
                bundle.putString("bid", bid);
                bundle.putBoolean("liuqq", true);
                LocalSetting.CurrentBottle = currentBottle;
                intent.putExtras(bundle);
//                intent.setClass(ArroundDetailActivity.this, HandlerActivity.class);
                intent.setClass(ZhongJiArroundDetailActivity.this, NewHandlerActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
