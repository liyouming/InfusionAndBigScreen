package com.fugao.infusion.chuaici;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fugao.infusion.InfusionApplication;
import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseTempleActivity;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.model.InfusionAreaBean;
import com.fugao.infusion.utils.InfoUtils;
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
* Do one thing at a time, and do well!
*
* @Prject: FastApps
* @Location: com.android.fastinfusion.ui.activity.AreaInfusionEmptyActivity
* @Description: TODO  区域加载座位 如优先区 加号区  留观室。。
* @author: 胡乐    hule@fugao.com
* @date: 2014/11/12 17:24
* @version: V1.0
*/

public class AreaInfusionEmptyActivity extends BaseTempleActivity{
    private static final String TAG = "Fugao-AreaInfusionEmptyActivity";

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip viewTabs;
    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.allot_seat_title)
    TextView allot_seat_title;
    private PagerSlidingTabStripAdapter adapter;
//    private ViewPager pager;
    private ArrayList<Fragment> fragments;
    private InfusionApplication infoApp;
    private  List<InfusionAreaBean> infusionAreaBeanList;
    private String currentRole;//当前角色
    /**
     * 发送广播
     * @param seatNO
     */
    public void sendBoradcastSetNo(String seatNO){
        InfoUtils.sendSuccessBroadcastSetNo(this, seatNO);
        finish();
    }
    @Override
    public void initView() {
        infoApp = (InfusionApplication) getApplication();
        currentRole = LocalSetting.CurrentAccount.Competence.get(0).Role;
        infusionAreaBeanList = new ArrayList<InfusionAreaBean>();
        fragments = new ArrayList<Fragment>();
        List<InfusionAreaBean> infoAreaBeanList = infoApp.getInfusionAreaBeanList();
        if(infoAreaBeanList ==null){
            String infusionAreaList = XmlDB.getInstance(context).getKeyString("infusionAreaList", "");
                infusionAreaBeanList = JacksonHelper.getObjects(infusionAreaList, new TypeReference<List<InfusionAreaBean>>() {
                });
        }

        /***
         * 我先写死 以后登录的时候选择区域保存在数据库然后从数据库中取
         */
//        String[] tabs =new String[]{"一输液区","二输液区", "加号输液区"};
//        String[] areaId = new String[]{"1", "2", "7"};
//        for(int i=0; i<tabs.length; i++){
//            fragments.add(InfusionAreaSeatDetailFragment.newInstance(areaId[i]));
//        }
        for (InfusionAreaBean each : infoAreaBeanList) {
            if (each.Type == 1) {
                if (currentRole.equals("a3")) {
                    if ("重症留观室".equals(each.AreaName) || "急诊留观室".equals(each.AreaName)) {
                        infusionAreaBeanList.add(each);
                    }
                } else if (currentRole.equals("a4")) {
                    if (LocalSetting.RoleIndex == 4) {
                        if ("重症留观室".equals(each.AreaName) || "急诊留观室".equals(each.AreaName)) {
                            infusionAreaBeanList.add(each);
                        }
                    }else {
                        if (!"重症留观室".equals(each.AreaName) && !"急诊留观室".equals(each.AreaName)) {
                            infusionAreaBeanList.add(each);
                        }
                    }
                } else {
                    if (!"重症留观室".equals(each.AreaName) && !"急诊留观室".equals(each.AreaName)) {
                        infusionAreaBeanList.add(each);
                    }
                }
            }
        }
        String[] tabs = new String[infusionAreaBeanList.size()];
        for(int i=0; i<infusionAreaBeanList.size(); i++){
            tabs[i] = infusionAreaBeanList.get(i).AreaName;
            fragments.add(InfusionAreaSeatDetailFragment.newInstance(String.valueOf(infusionAreaBeanList.get(i).Id)));
        }
//        for(int i=infusionAreaBeanList.size()-1; i>=0; i--){
//            tabs[infusionAreaBeanList.size()-1-i] = infusionAreaBeanList.get(i).AreaName;
//            fragments.add(InfusionAreaSeatDetailFragment.newInstance(String.valueOf(infusionAreaBeanList.get(i).Id)));
//        }
        adapter = new PagerSlidingTabStripAdapter(getSupportFragmentManager(), fragments, tabs);
        pager.setAdapter(adapter);
        viewTabs.setViewPager(pager);
        pager.setOffscreenPageLimit(3);
        final String[] finalTabs = tabs;

        viewTabs.setTextSize(DisplayUtils.sp2px(15, displayMetrics.scaledDensity));
        int tabChildSize = fragments.size();
        int titleCount = 0;
        titleCount = 6;
        if (titleCount <= 12&&tabChildSize!=0) {
            int width = new DisplayMetrics().widthPixels / tabChildSize;
            viewTabs.setTabWidth(width);
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        allot_seat_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initIntent() {

    }



    @Override
    public void setContentView() {
        setContentView(R.layout.fragment_tab_seat);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.arround, menu);
        return true;
    }

    /**
     * 取到fragment的座位信息然后回传给穿刺界面
     * @param seatNo
     */
    public void updateSeatNo(String seatNo){
        Intent intent = new Intent();
        intent.putExtra("emptySeatNO",seatNo);
        setResult(101,intent);
        this.finish();
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                InfusionAreaSeatDetailFragment data =
                        (InfusionAreaSeatDetailFragment) fragments.get(pager.getCurrentItem());
                data.getData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
