package com.fugao.infusion.seat;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.fugao.infusion.InfusionApplication;
import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseTempleActivity;
import com.fugao.infusion.model.InfusionAreaBean;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.DisplayUtils;
import com.jasonchen.base.utils.JacksonHelper;
import com.jasonchen.base.view.PagerSlidingTabStrip;
import com.jasonchen.base.view.PagerSlidingTabStripAdapter;

import org.codehaus.jackson.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * 区域座位
 */
public class SeatActivity extends BaseTempleActivity {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabs;
    @InjectView(R.id.pager)
    ViewPager mPager;
    private String[] titles;
    private PagerSlidingTabStripAdapter adapter;
    private List<InfusionAreaBean> infusionAreaBeanList;
    private InfusionApplication InfusionApplication;
    private ArrayList<Fragment> fragments;

    @Override
    public void setContentView() {

        setContentView(R.layout.activity_seat);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        InfusionApplication= (InfusionApplication) getApplication();
        infusionAreaBeanList = new ArrayList<InfusionAreaBean>();
        fragments = new ArrayList<Fragment>();
        List<InfusionAreaBean> infoAreaBeanList = InfusionApplication.getInfusionAreaBeanList();
        if(infoAreaBeanList ==null){
            String infusionAreaList = XmlDB.getInstance(context).getKeyString("infusionAreaList", "");
            infoAreaBeanList = JacksonHelper.getObjects(infusionAreaList, new TypeReference<List<InfusionAreaBean>>() {
            });
        }
        List<String> strings=new ArrayList<String>();
        for (int i=0;i<infoAreaBeanList.size();i++){
            InfusionAreaBean infusionAreaBean=infoAreaBeanList.get(i);
            strings.add(infusionAreaBean.AreaName);
            SeatFragment seatFragment=SeatFragment.newInstance(infusionAreaBean.AreaCode);
            fragments.add(seatFragment);
        }
        titles=(String[])(strings.toArray(new String[strings.size()]));
        adapter = new PagerSlidingTabStripAdapter(getSupportFragmentManager(), fragments,
                titles);
        mPager.setAdapter(adapter);
        mTabs.setViewPager(mPager);
        mTabs.setTextSize(DisplayUtils.sp2px(18, displayMetrics.scaledDensity));
        int tabChildSize = fragments.size();
        int titleCount = 0;
        String titleString = "";
        titleCount = titles.length;
        if (titleCount <= 4) {
            int width = windowWidth / tabChildSize;
            mTabs.setTabWidth(width);
        }
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {

    }


}
