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

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fugao.infusion.InfusionApplication;
import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.dao.DataBaseInfo;
import com.fugao.infusion.dao.InfusionDetailDAO;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.UserDetailTimelineModel;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.view.PopMenu;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.ResourceUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastAndroid
 * @Location: com.android.fastinfusion.ui.fragment.InfoAround
 * @Description: TODO 输液巡视
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/9/26 2:14
 * @Modify-date: 修改日期
 * @Modify-description: TODO 修改说明
 * @Modify-author: 修改人
 * @version: V1.0
 */

public class ArroundFragment extends BaseFragmentV4
        implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "Fast-InfoAround";
    @InjectView(R.id.listView)
    ListView mListView;
    @InjectView(R.id.un_complete_refresh_layout)
    SwipeRefreshLayout mUnCompleteRefreshLayout;
    @InjectView(R.id.more)
    LinearLayout mMore;


    private MenuItem refreshItem;
    private ArroundsAdapter arroundsAdapter;
    private List<BottleModel> bottles;
    private List<UserDetailTimelineModel> timeTwoStatusModels;

    private InfusionApplication infoApp;
    private ActionBar actionBar;
    private String depId;
    private InfusionDetailDAO infusionDetailDAO;
    private PopMenu popMenu;
    private String[] strings;
    private String userId ="";

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_around, container, false);
    }

    @Override
    public void initView(View currentView) {
        setHasOptionsMenu(true);
        mUnCompleteRefreshLayout.setOnRefreshListener(this);
        getData();
//        initActionBar();
    }

    @Override
    public void initData() {
        strings = ResourceUtils.getResouce4Arrays(fatherActivity, R.array.around_choose);
        depId = XmlDB.getInstance(fatherActivity).getKeyString("deptID", "100001");
        infoApp = (InfusionApplication) fatherActivity.getApplication();
        timeTwoStatusModels = new ArrayList<UserDetailTimelineModel>();
        infusionDetailDAO = new InfusionDetailDAO(DataBaseInfo.getInstance(fatherActivity));
        bottles = new ArrayList<BottleModel>();
        bottles = infusionDetailDAO.getBottleByBottlsStatus(BottleStatusCategory.INFUSIONG.getKey()+"");
        arroundsAdapter = new ArroundsAdapter(fatherActivity, bottles);
        mListView.setAdapter(arroundsAdapter);
    }

    @Override
    public void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent() ;
                Bundle bundle = new Bundle();
                BottleModel bo = bottles.get(i);
                bundle.putString("bid", bo.BottleId);
                bundle.putString("iid", bo.InfusionId);
                bundle.putString("pid", bo.PeopleInfo.PatId);
                intent.putExtras(bundle);
                intent.setClass(fatherActivity, ArroundDetailActivity.class);
                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                BottleModel bo = bottles.get(position);
                UserDetailTimelineModel timeTwoStatusBean = new UserDetailTimelineModel();
                timeTwoStatusBean.RegistrationCore = bo.RegistrationCore;
                timeTwoStatusBean.RegistrationDate = bo.RegistrationDate;
                timeTwoStatusBean.RegistrationTime = bo.RegistrationTime;
                timeTwoStatusBean.PillDate = bo.PillDate;
                timeTwoStatusBean.PillTime = bo.PillTime;
                timeTwoStatusBean.PillCore = bo.PillCore;
                timeTwoStatusBean.LiquorDate = bo.LiquorDate;
                timeTwoStatusBean.LiquorTime = bo.LiquorTime;
                timeTwoStatusBean.LiquorCore = bo.LiquorCore;
                timeTwoStatusBean.InfusionDate = bo.InfusionDate;
                timeTwoStatusBean.InfusionTime = bo.InfusionTime;
                timeTwoStatusBean.InfusionCore = bo.InfusionCore;
                timeTwoStatusBean.EndDate = bo.EndDate;
                timeTwoStatusBean.EndTime = bo.EndTime;
                timeTwoStatusBean.EndCore = bo.EndCore;
                timeTwoStatusModels.add(timeTwoStatusBean);
                infoApp.setTimeTwoStatusModel(timeTwoStatusModels);
                //待完成
                //((MainTabActivity) fatherActivity).openActivity(InfusionTimelineActivity.class);
                return true;
            }
        });

        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopMenu();
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
            popMenu = new PopMenu(fatherActivity);
            popMenu.addItems(strings);
            popMenu.setOnItemClickListener(new PopMenu.OnItemClickListener() {
                @Override
                public void onItemClick(int index) {
                    String slectValue = strings[index];
                    if ("自己".equals(slectValue)) {
                        userId = LocalSetting.CurrentAccount.UserName;
                        getData();
                    } else if ("全部".equals(slectValue)) {
                        userId = "all";
                        getData();
                    }
                }
            });
        }
        popMenu.showAsDropDown(mMore, -30, 0);
    }

    private void initActionBar() {
        actionBar = fatherActivity.getActionBar();
        actionBar.setTitle("巡视");
    }

    private void getData() {

        mUnCompleteRefreshLayout.setRefreshing(true);
        if(StringUtils.StringIsEmpty(userId)){
            userId = LocalSetting.CurrentAccount.UserName;
        }
        String url = InfoApi.Url_GetAround(depId,userId,LocalSetting.type);
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                List<BottleModel> bottleModels =String2InfusionModel.string2BottleModels(s);
                if (bottleModels.size() > 0) {
                    infusionDetailDAO.deleteAllInfo();
                    infusionDetailDAO.saveToInfusionDetail(bottleModels);
                    bottles.clear();
                    bottles.addAll(bottleModels);
                    arroundsAdapter.notifyDataSetChanged();
                }else{
                    bottles.clear();
                    UIHepler.showToast(fatherActivity, "没有数据");
                    arroundsAdapter.notifyDataSetChanged();
                }
                UIHepler.showToast(fatherActivity, "成功");
                mUnCompleteRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                UIHepler.showToast(fatherActivity, "加载失败");
                mUnCompleteRefreshLayout.setRefreshing(false);
                //待完成
//                error.makeToast(fatherActivity);
//                InfoUtils.showWifiConnectdDialogFragament(fatherActivity, error);
            }
        });
    }

//      private void setRefreshing(boolean refreshing) {
//          mUnCompleteRefreshLayout.setRefreshing(refreshing);
//        if (refreshItem == null)
//            return;
//
//        if (refreshing) {
//          refreshItem.setActionView(R.layout.actionbar_refresh_progress);
//        } else {
//          refreshItem.setActionView(null);
////          mPullToRefreshLayout.setRefreshComplete();
//
//        }
//      }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.arround, menu);
        refreshItem = menu.findItem(R.id.action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                getData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }
}
