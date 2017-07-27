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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.constant.WorkloadStatusCategory;
import com.fugao.infusion.dao.DataBaseInfo;
import com.fugao.infusion.dao.WorkLoadDAO;
import com.jasonchen.base.utils.DisplayUtils;
import com.jasonchen.base.view.PagerSlidingTabStrip;
import com.jasonchen.base.view.PagerSlidingTabStripAdapter;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastAndroid
 * @Location: com.android.fastinfusion.ui.fragment.HomeFragment
 * @Description: TODO 工作量
 * @Author: LoQua.Xee    loquaciouser@gmail.com
 * @Create-date: 2014/9/26 1:21
 * @Modify-date: 修改日期
 * @Modify-description: TODO 修改说明
 * @Modify-author: 修改人
 * @version: V1.0
 */

public class WorkloadFragment extends BaseFragmentV4 {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip viewTabs;
    @InjectView(R.id.pager)
    ViewPager pager;
    private static final String TAG = "Fast-WorkloadFragment";
    private ArrayList<Fragment> fragments;
    private WorkLoadDAO workLoadDAO;
    private PagerSlidingTabStripAdapter adapter;

    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, null);
    }

    @Override
    public void initView(View view) {
        workLoadDAO = new WorkLoadDAO(DataBaseInfo.getInstance(fatherActivity));
        workLoadDAO.deleteAllInfo();
        setHasOptionsMenu(true);
        fragments = new ArrayList<Fragment>();
        fragments.add(WorkloadDetailFragment.newInstance(WorkloadStatusCategory.DAY.getValue())); //一天
        fragments.add(
                WorkloadDetailFragment.newInstance(WorkloadStatusCategory.WEEK.getValue()));   //一周
        fragments.add(WorkloadDetailFragment.newInstance(WorkloadStatusCategory.MONTH.getValue())); //一月
        String[] tabs = getResources().getStringArray(R.array.workload_array);

        adapter = new PagerSlidingTabStripAdapter(getFragmentManager(), fragments, tabs);
        pager.setAdapter(adapter);
        viewTabs.setViewPager(pager);
        pager.setOffscreenPageLimit(3);
        final String[] finalTabs = tabs;

        viewTabs.setTextSize(DisplayUtils.sp2px(18, displayMetrics.scaledDensity));
        int tabChildSize = fragments.size();
        int titleCount = 0;
        titleCount = 6;
        if (titleCount <= 12) {
            int width = phoneWidth / tabChildSize;
            viewTabs.setTabWidth(width);
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.arround, menu);
        //refreshItem = menu.findItem(R.id.action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                //getData();
                WorkloadDetailFragment data =
                        (WorkloadDetailFragment) fragments.get(pager.getCurrentItem());
                data.getData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
