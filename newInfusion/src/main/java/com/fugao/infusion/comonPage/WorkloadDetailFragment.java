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

package com.fugao.infusion.comonPage;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.dao.DataBaseInfo;
import com.fugao.infusion.dao.WorkLoadDAO;
import com.fugao.infusion.model.CounterModel;
import com.fugao.infusion.model.WorkloadModel;
import com.fugao.infusion.paiyao.PaiYaoActivity;
import com.fugao.infusion.utils.String2InfusionModel;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;
import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastAndroid
 * @Location: com.android.fastinfusion.ui.fragment.HomeFragment
 * @Description: TODO
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/9/19 23:34
 * @Modify-date: 修改日期
 * @Modify-description: TODO 修改说明
 * @Modify-author: 修改人
 * @version: V1.0
 */

public class WorkloadDetailFragment extends BaseFragmentV4
    implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,
    PaiYaoActivity.RefreshFragment{

  public static final String EXTRA_CATEGORY = "workload_category";
  private SwipeRefreshLayout mUnCompleteRefreshLayout;
  private ListView mListView;
  private WorkLoadDAO workLoadDAO;

  private MenuItem refreshItem;
  private String catalog;
  private ActionBar actionbar;
  private List<CounterModel> counters;
  private WorkloadAdapter workloadAdapter;

  // 标志位，标志已经初始化完成。
  private boolean isPrepared;

  public static WorkloadDetailFragment newInstance(String catalog) {
    WorkloadDetailFragment fragment = new WorkloadDetailFragment();
    Bundle bundle = new Bundle();
    bundle.putString(EXTRA_CATEGORY, catalog);
    fragment.setArguments(bundle);
    return fragment;
  }

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workload, container, false);
    }

    @Override
    public void initView(View view) {
        mUnCompleteRefreshLayout = (SwipeRefreshLayout) view;
        mListView = (ListView) mUnCompleteRefreshLayout.findViewById(R.id.listView);
        mListView.setEmptyView(mUnCompleteRefreshLayout.findViewById(R.id.nodata));
    mUnCompleteRefreshLayout.setOnRefreshListener(this);
    mUnCompleteRefreshLayout.setColorScheme(android.R.color.holo_red_light,
            android.R.color.holo_green_light,
            android.R.color.holo_blue_bright,
            android.R.color.holo_orange_light);
    mListView.setOnItemClickListener(this);
  }

    @Override
    public void initData() {

        /**
         * 填充各控件的数据
         */
        catalog = getArguments().getString(EXTRA_CATEGORY);
        workLoadDAO = new WorkLoadDAO(DataBaseInfo.getInstance(fatherActivity));
        counters = new ArrayList<CounterModel>();
        workloadAdapter = new WorkloadAdapter(fatherActivity, counters);
        mListView.setAdapter(workloadAdapter);
        loadData();

    }

    @Override
    public void initListener() {

    }


  private List<CounterModel> handler2Counter(WorkloadModel workloadModel) {
    List<CounterModel> counts = new ArrayList<CounterModel>();
    if (workloadModel == null) return counts;
//    counts.add(new CounterModel("登记次数", workloadModel.DjCount));
    counts.add(new CounterModel("排药次数", workloadModel.PaiYaoCount));
    counts.add(new CounterModel("冲配次数", workloadModel.DoseCount));
    counts.add(new CounterModel("穿刺(留)次数", workloadModel.PunctureCount));
    counts.add(new CounterModel("穿刺(钢针)次数",workloadModel.PunctureCountN));
    counts.add(new CounterModel("接瓶次数", workloadModel.DoneCount));
    counts.add(new CounterModel("巡视次数", workloadModel.PatrolCount));
//    counts.add(new CounterModel("处理呼叫次数", workloadModel.CallOver));
//    counts.add(new CounterModel("作废次数", workloadModel.InvalidOver));
    return counts;
  }

  private void loadData() {
    WorkloadModel workloadModel = workLoadDAO.query(catalog);
    if (workloadModel == null) {
      getData();
    } else {
      counters.clear();
      counters.addAll(handler2Counter(workloadModel));
      workloadAdapter.notifyDataSetChanged();
    }
  }

  /**
   * 刷新数据
   */
  public void getData() {

    mUnCompleteRefreshLayout.setRefreshing(true);
    RestClient.get(InfoApi.Url_GetWorkload(LocalSetting.CurrentAccount.UserName, catalog),new BaseAsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, String s) {
            WorkloadModel workloadModel = String2InfusionModel.string2WorkloadModel(s);
            mUnCompleteRefreshLayout.setRefreshing(false);
            workloadModel.Catalog = catalog;
            workLoadDAO.insertWorlLoad(workloadModel);
            counters.clear();
            counters.addAll(handler2Counter(workloadModel));
            workloadAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(int i, Throwable throwable, String s) {
            mUnCompleteRefreshLayout.setRefreshing(false);
            UIHepler.showToast(fatherActivity,"加载失败");
        }
    });
  }

  //@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
  //  super.onCreateOptionsMenu(menu, inflater);
  //  inflater.inflate(R.menu.arround, menu);
//    refreshItem = menu.findItem(R.id.action_refresh);
  //}
  //
  //@Override public boolean onOptionsItemSelected(MenuItem item) {
  //
  //  switch (item.getItemId()) {
  //    case R.id.action_refresh:
  //      getData();
  //      return true;
  //    default:
  //      return super.onOptionsItemSelected(item);
  //  }
  //}


  @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

  }

  @Override public void topRefresh() {
    getData();
  }

    @Override
    public void onRefresh() {
        getData();
    }

}
