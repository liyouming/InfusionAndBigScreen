package com.fugao.infusion.comonPage;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.fugao.infusion.InfusionApplication;
import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.model.InfusionAreaBean;
import com.fugao.infusion.model.RealseSeatModel;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.view.ActionItemBadge;
import com.fugao.infusion.view.Iconify;
import com.fugao.infusion.xunshi.CallQueueActivity;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.BeepManager;
import com.jasonchen.base.utils.Log;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastApps
 * @Location: com.android.fastinfusion.ui.fragment.RealeseSeatFragment
 * @Description: TODO 座位界面
 * @author: 胡乐    hule@fugao.com
 * @date: 2014/11/18 19:33
 * @version: V1.0
 */

public class RealeseSeatFragment extends BaseFragmentV4 implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "Fugao-RealeseSeatFragment";

    private ArrayList<HashMap<String, String>> lstImageItem;
    private String DeptID;
    private List<RealseSeatModel> realseSeatNoList;
    private GridViewSeatAdapter gridviewAdapter;
    private MenuItem refreshItem;
    private MenuItem moreItem;
//    private ActionBar actionBar;
    private RefreshSetNoReceiver refreshSetNoReceiver;
    private Handler handler;
    private BeepManager beepManager;//呼叫声音
    /**
    * 区域集合
    */
    private ArrayList<String> infusionAreaNameList = null;
    private ArrayList<InfusionAreaBean> infusionAreaBeanList = null;
    private InfusionApplication infoApp;
    @InjectView(R.id.gridView)
    GridView gridView;
    @InjectView(R.id.realeseseat_spinner)
    Spinner myspinner;
    @InjectView(R.id.framelayout_call)
    FrameLayout mframeLayout;
    @InjectView(R.id.shuaxin)
    LinearLayout refresh;
    @InjectView(R.id.un_complete_refresh_layout)
    SwipeRefreshLayout mUnCompleteRefreshLayout;
    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_realese_seatno, container, false);
    }

    @Override
    public void initView(View currentView) {
        infoApp = (InfusionApplication) fatherActivity.getApplication();
        DeptID = XmlDB.getInstance(fatherActivity).getKeyString("deptID", "100001");
        realseSeatNoList = new ArrayList<RealseSeatModel>();
        infusionAreaBeanList = new ArrayList<InfusionAreaBean>();
        infusionAreaNameList = new ArrayList<String>();
        setHasOptionsMenu(true);
        initActionBar();
        mUnCompleteRefreshLayout.setOnRefreshListener(this);
        mUnCompleteRefreshLayout.setColorScheme(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
    }

    @Override
    public void initData() {
        beepManager = new BeepManager(fatherActivity);
        getAreaInfoList();
        initBoradCast();
        initHandler();
    }
    private void initHandler(){
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String callString = "";
                if (msg.obj != null) {
                    callString = msg.obj.toString();
                }
                setBadge4ActionBar(msg.what, callString);
            }
        };
    }
    @Override
    public void initListener() {
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(realseSeatNoList.get(position).PeopleInfo !=null){
                    String url = InfoApi.url_ReleaseSeat(DeptID,realseSeatNoList.get(position).PeopleInfo.InfusionId);
                    RestClient.get(url,new BaseAsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, String s) {
                            UIHepler.showToast(fatherActivity,lstImageItem.get(position).get("itemName") + lstImageItem.get(position).get("itemSeatNo") + "被释放");
//                            gridviewAdapter.notifyDataSetChanged();
                            topRefresh();
                        }

                        @Override
                        public void onFailure(int i, Throwable throwable, String s) {
//                            error.makeToast(fatherActivity);
                            UIHepler.showToast(fatherActivity,"释放" + lstImageItem.get(position).get("itemSeatNo") + "失败请重试！");
                        }
                    });
                }else {
                    UIHepler.showToast(fatherActivity,"当前为空座位，不能释放！");
                }
                return false;
            }
        });
        mframeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(fatherActivity,CallQueueActivity.class);
                startActivity(intent);
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topRefresh();
            }
        });
    }

    private void initActionBar() {
//        actionBar =fatherActivity.getActionBar();
    }
    private void initBoradCast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.REFRESHSEATNO);
        refreshSetNoReceiver = new RefreshSetNoReceiver();
        fatherActivity.registerReceiver(refreshSetNoReceiver, intentFilter);
    }
    public void getData(int areId) {
        mUnCompleteRefreshLayout.setRefreshing(true);
        String url = InfoApi.url_GetSeatAndInfo( DeptID, String.valueOf(areId));
        RestClient.get(url,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
               mUnCompleteRefreshLayout.setRefreshing(false);
               List<RealseSeatModel> realseSeatNoListss = String2InfusionModel.string2RealseSeatModel(s);
                lstImageItem = new ArrayList<HashMap<String, String>>();
                realseSeatNoList.clear();
                realseSeatNoList.addAll(realseSeatNoListss);
                if (realseSeatNoListss.size() > 0) {
                    for (i = 0; i < realseSeatNoListss.size(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("itemSeatNo", realseSeatNoListss.get(i).Seat);
                        if (realseSeatNoListss.get(i).PeopleInfo != null) {
                            map.put("itemName", realseSeatNoListss.get(i).PeopleInfo.Name);
                        }
                        lstImageItem.add(map);
                    }
                    gridviewAdapter = new GridViewSeatAdapter(fatherActivity, lstImageItem);
                    gridView.setAdapter(gridviewAdapter);
                    gridviewAdapter.notifyDataSetChanged();
                    setRefreshing(false);
                } else {
                    UIHepler.showToast(fatherActivity, "暂时没有座位可以释放，请稍等！");
                    setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                mUnCompleteRefreshLayout.setRefreshing(false);
                UIHepler.showToast(fatherActivity,"网络请求失败，请刷新！");
            }
        });
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.arround, menu);
        refreshItem = menu.findItem(R.id.action_refresh);
//        moreItem = menu.findItem(R.id.action_more);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                topRefresh();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 加载区域名称
     */
    private void showAreaName() {
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(fatherActivity, android.R.layout.simple_spinner_dropdown_item, infusionAreaNameList);
        //下拉列表
        myspinner.setAdapter(stringArrayAdapter);
        myspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view;
                textView.setTextColor(Color.WHITE);
                XmlDB.getInstance(fatherActivity).saveKey("areaId", infusionAreaBeanList.get(position).Id);
                getData(infusionAreaBeanList.get(position).Id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setRefreshing(boolean refreshing) {
        if (refreshItem == null) return;
        if (refreshing) {
            refreshItem.setActionView(R.layout.actionbar_refresh_progress);
        } else {
            refreshItem.setActionView(null);
        }
    }

    private void getAreaInfoList(){
        List<InfusionAreaBean> infusionAreaBeanList1 = infoApp.getInfusionAreaBeanList();
        checkArea(infusionAreaBeanList1);
        if(infusionAreaBeanList1 !=null  && infusionAreaBeanList1.size()>0){
            infusionAreaBeanList.clear();
            for(InfusionAreaBean each:infusionAreaBeanList1){
                if(each.Type ==2)continue;
                infusionAreaNameList.add(each.AreaName);
                infusionAreaBeanList.add(each);
            }
            showAreaName();
        }else {
            String deptID = XmlDB.getInstance(fatherActivity).getKeyString("deptID", "100001");
            String url = InfoApi.getInfusionAreaByDeptID(deptID);
            RestClient.get(url, new BaseAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, String s) {
                    List<InfusionAreaBean> infusionAreaBeans = String2InfusionModel.string2InfusionAreaBeans(s);
                    checkArea(infusionAreaBeans);
                    if (infusionAreaBeans != null
                            && infusionAreaBeans.size() > 0) {
                        infusionAreaBeanList.clear();
                        for (InfusionAreaBean each : infusionAreaBeans) {
                            if (each.Type == 2) continue;
                            infusionAreaNameList.add(each.AreaName);
                            infusionAreaBeanList.add(each);
                        }
                        showAreaName();

                    } else {
                        UIHepler.showToast(fatherActivity, "没有加载的区域");
                    }
                }

                @Override
                public void onFailure(int i, Throwable throwable, String s) {
//                    error.makeToast(fatherActivity);
//                    UIHelper.showInfoDilalog(fatherActivity,"未能加载获取区域，请刷新！",new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            getAreaInfoList();
//                        }
//                    });
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        topRefresh();
    }

    /**
     * 实现 ActionBar.OnNavigationListener接口
     */
    private class DropDownListenser implements ActionBar.OnNavigationListener {
        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            XmlDB.getInstance(fatherActivity).saveKey("areaId", infusionAreaBeanList.get(itemPosition).Id);
            getData(infusionAreaBeanList.get(itemPosition).Id);
            return false;
        }
    }

    @Override
    public void onResume() {
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        super.onResume();
    }

   public void topRefresh(){
       int areaId;
       if(infusionAreaBeanList !=null) {
           areaId = XmlDB.getInstance(fatherActivity).getKeyIntValue("areaId", infusionAreaBeanList.get(0).Id);
       }else {
           getAreaInfoList();
           areaId = XmlDB.getInstance(fatherActivity).getKeyIntValue("areaId", infusionAreaBeanList.get(0).Id);
       }
       getData(areaId);
   }

    /**
     * 重新分配座位后控制列表刷新
     */
    private class RefreshSetNoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constant.REFRESHSEATNO)){
                topRefresh();
            }

        }
    }

    @Override
    public void onDestroy() {
        fatherActivity.unregisterReceiver(refreshSetNoReceiver);
        super.onDestroy();
    }
    /**
     * 给actionItem添加上标
     *
     * @param count 上标数
     */
    private void setBadge4ActionBar(int count, String callString) {
        try {
            /**
             * 当巡视界面没有呼叫的时候，铃铛图标显示是灰色的
             * 当有呼叫的时候，图标变为红色
             */
            if (count > 0) {
                ActionItemBadge.update(fatherActivity, mframeLayout, Iconify.IconValue.fa_bell_o,
                        ActionItemBadge.BadgeStyle.RED, count);
            } else {
                ActionItemBadge.update(fatherActivity, mframeLayout, Iconify.IconValue.fa_bell_o,
                        ActionItemBadge.BadgeStyle.DARKGREY, count);
            }

            if (count >= 1) {
                String lastCalls = XmlDB.getInstance(fatherActivity).getKeyString("last_call_ids", "0");
                String currentCalls = callString;
                Log.d("tag", "count=" + count);
                if ("0".equals(lastCalls)) {
                    Log.d("后台有数据呼叫=========" + lastCalls + "/" + currentCalls, count + "");
                    setBeep();
                    XmlDB.getInstance(fatherActivity).saveKey("last_call_ids", currentCalls);
                } else {
                    String[] callArrays = currentCalls.split(",");
                    String[] lastCallArrays = lastCalls.split(",");

                    if (lastCallArrays.length != callArrays.length) {
                        Log.d("后台有数据呼叫=========" + lastCallArrays.length + "/" + callArrays.length, count + "");
                        setBeep();
                    } else {
                        if (!Arrays.equals(callArrays, lastCallArrays)) {
                            Log.d("后台有数据呼叫=========" + callArrays + "/" + lastCallArrays, count + "");
                            setBeep();
                        }
                    }
                    XmlDB.getInstance(fatherActivity).saveKey("last_call_ids", currentCalls);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 控制声音和振动方法
     */
    public void setBeep() {
        beepManager.playBeepSoundAndVibrate();
        InfoUtils.Vibrate(fatherActivity, new long[]{10, 1500, 100, 2500}, false);
    }

    /**
     *  区分普通和重急区域的加载
     * @param infusionAreaBeanList1
     */
   private void checkArea(List<InfusionAreaBean> infusionAreaBeanList1){
       String areaName = null;
       if(RoleCategory.SHUYE.getKey() == LocalSetting.RoleIndex){
           for(int i =0; i<infusionAreaBeanList1.size(); i++){
               areaName = infusionAreaBeanList1.get(i).AreaName;
               if(areaName.contains("重症")||areaName.contains("急诊")){
                   infusionAreaBeanList1.remove(i);
                   i--;
               }
           }
       }else if(RoleCategory.ZHONGJI.getKey() == LocalSetting.RoleIndex){
           for(int i =0; i<infusionAreaBeanList1.size(); i++){
               areaName = infusionAreaBeanList1.get(i).AreaName;
               if(!areaName.contains("重症") && !areaName.contains("急诊")){
                   infusionAreaBeanList1.remove(i);
                   i--;
               }
           }
       }
   }

}
