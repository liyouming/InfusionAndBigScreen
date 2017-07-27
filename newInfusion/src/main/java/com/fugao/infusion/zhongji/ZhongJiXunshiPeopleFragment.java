package com.fugao.infusion.zhongji;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.comonPage.CustomCallActivity;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.QueueStatusCategory;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.dao.InfusionDetailDAO;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.LogOnExceptionScheduledExecutor;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.UIHelper;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.view.ActionItemBadge;
import com.fugao.infusion.view.AnimatedExpandableListView;
import com.fugao.infusion.view.Iconify;
import com.fugao.infusion.view.PopMenu;
import com.fugao.infusion.xunshi.CallQueueActivity;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.BeepManager;
import com.jasonchen.base.utils.Log;
import com.jasonchen.base.utils.ResourceUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;

/**
 * 穿刺列表
 */
public class ZhongJiXunshiPeopleFragment extends BaseFragmentV4 implements SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.title_text_view)
    TextView mTitleTextView;
    @InjectView(R.id.listView)
    AnimatedExpandableListView listView;
    @InjectView(R.id.un_complete_refresh_layout)
    SwipeRefreshLayout mUnCompleteRefreshLayout;
    @InjectView(R.id.more)
    LinearLayout mMore;
    private ZhongjiXunshiActivity activity;
    @InjectView(R.id.framelayout_call)
    FrameLayout mframeLayout;
    @InjectView(R.id.progressContainer)
    LinearLayout progressContainer;

    /**
     * 程序默认进来是 未完成 拼贴列表 0-全部 1-未完成 2-已完成
     */
    private static final int CATOGRY_ALL = 0;
    private static final int CATOGRY_UNDO = 1;
    private static final int CATOGRY_DONE = 2;
    private int POSITION = 1;
    private String[] strings;
    private String[] strsMenu;
    /**
     * 对象初始化
     */
    private static ArrayList<GroupBottleModel> originalGroupBottles;
    private ArrayList<GroupBottleModel> groupBottleModels = new ArrayList<GroupBottleModel>();
    private ZhongJiXunshiGroupBottleAdapter bottleAdapter;
    /**
     * 初始化String
     */
    private String patID;
    private InfusionDetailDAO infusionDetailDAO;
    private PopMenu popMenu;

    private Handler handler;

    private BeepManager beepManager;//呼叫声音

    private ScheduledExecutorService autoRefreshExecutor = null;

    private ArrayList<GroupBottleModel> searchGroupBottles;
    private View currentView;
    private TextView count_id;
    private String count_id_s;
    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView=inflater.inflate(R.layout.fragment_xunshi_people, container, false);
        return currentView;
    }

    @Override
    public void initView(View currentView) {
        activity = (ZhongjiXunshiActivity) fatherActivity;
        mUnCompleteRefreshLayout.setOnRefreshListener(this);
        mUnCompleteRefreshLayout.setColorScheme(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
        count_id= (TextView) currentView.findViewById(R.id.count_id_xunshi);
    }

    @Override
    public void initData() {
        count_id_s=XmlDB.getInstance(fatherActivity).getKeyString("AcountId","");
        count_id.setText(count_id_s);
        beepManager = new BeepManager(activity);
        strings = ResourceUtils.getResouce4Arrays(activity, R.array.statues_arrays);
        infusionDetailDAO = activity.infusionDetailDAO;
        originalGroupBottles = new ArrayList<GroupBottleModel>();
        bottleAdapter = new ZhongJiXunshiGroupBottleAdapter(fatherActivity, originalGroupBottles);
        listView.setAdapter(bottleAdapter);
        getData();
        initHandler();

        //传入字符串数组:呼叫 和 打印座位
        List<String> tempMenu =
                Arrays.asList(fatherActivity.getResources().getStringArray(R.array.longClick_menu));

        ArrayList<String> longClickMenu = new ArrayList<String>();
        longClickMenu.addAll(tempMenu);

        if (RoleCategory.CHUANCI.getKey() != LocalSetting.RoleIndex) {
            if (longClickMenu.contains("打印座位")) longClickMenu.remove("打印座位");
        }

        strsMenu = new String[longClickMenu.size()];
        longClickMenu.toArray(strsMenu);
    }
    private void initHandler(){
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case -1:
                        mUnCompleteRefreshLayout.setRefreshing(true);
                        break;
                    default:
                        String callString = "";
                        if (msg.obj != null) {
                            callString = msg.obj.toString();
                        }
                        setBadge4ActionBar(msg.what, callString);
                        break;
                }
            }
        };
    }
    @Override
    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        /**
         * 询房界面增加长按事件进行打印座位
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                UIHelper.showListDialog(fatherActivity, groupBottleModels.get(i).Name,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int subPosition) {
                                switch (subPosition) {
                                    case 0:
                                        /**
                                         * 自定义呼叫
                                         */
                                        //doCustomCall(position);
                                        LocalSetting.CurrentGroupBottle = groupBottleModels.get(i);
                                        Intent intent = new Intent(fatherActivity, CustomCallActivity.class);
                                        startActivity(intent);
                                        //finish();
                                        break;
                                    case 1:
                                        /**
                                         * 蓝牙打印座位
                                         */
                                        //if (isUserSeatNO) doPrintSeatNo(position);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }, strsMenu //strsMenu  showPopMenu
                );
                return false;
            }
        });

        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopMenu();
            }
        });
        mframeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(activity, CallQueueActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getData() {
        mTitleTextView.setText(InfoUtils.getCurrentRoleName() + "-" + getCurrentCatogryText(POSITION));
        for (int i = 0; i < bottleAdapter.getGroupCount(); i++) {
            listView.collapseGroup(i);
        }
        String tempCatogry = getCurrentCatogry(POSITION);
        String deptId = XmlDB.getInstance(fatherActivity).getKeyString("deptID", "0");//貌似有BGU
        String url = ChuanCiApi.url_getBottlesByStatusGroup(deptId, tempCatogry, "zz");
        android.util.Log.d("=========================正在获取列表", "");
        final long l1 = System.currentTimeMillis();
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                Message msg = Message.obtain();
                msg.what = -1;
                msg.obj = this;
                handler.sendMessage(msg);
            }

            @Override
            public void onSuccess(int i, String s) {
                long l2 = System.currentTimeMillis();
                android.util.Log.d("获取列表成功返回耗时================", "" + (l2 - l1));
                long l3 = System.currentTimeMillis();
                ArrayList<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
                android.util.Log.d("客户端解析耗时耗时================", "" + (l3 - l2));
//                ArrayList<BottleModel> bottleModels1=new ArrayList<BottleModel>();
//                for(BottleModel bottlemodel:bottleModels){
//                    if(bottlemodel.GCF.equals("重症")){
//                        bottleModels1.add(bottlemodel);
//                    }
//                }
                if (bottleModels.size() > 0) {
                    infusionDetailDAO.deleteAllInfo();
                    infusionDetailDAO.saveToInfusionDetail(bottleModels);
                    groupBottleModels.clear();
                    originalGroupBottles = InfoUtils.toGroup(bottleModels);
                    groupBottleModels.addAll(originalGroupBottles);
                    bottleAdapter = new ZhongJiXunshiGroupBottleAdapter(fatherActivity, groupBottleModels);
                    listView.setAdapter(bottleAdapter);
                } else {
                    groupBottleModels.clear();
                    UIHepler.showToast(fatherActivity, "没有数据");
                    bottleAdapter.notifyDataSetChanged();
                }
                UIHepler.showToast(activity, "成功");
                mUnCompleteRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                UIHepler.showToast(activity, "加载失败");
                mUnCompleteRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void redirect2ExecuteSingleByClick(BottleModel bottle) {
        Log.e("输液执行扫描瓶贴操作");
        if (BottleStatusCategory.WAITINGHANDLE.getKey() == bottle.BottleStatus) {
            if(QueueStatusCategory.FINISHED.getKey() ==bottle.PeopleInfo.Status){
                redirect2ExecuteSingleByScan(bottle);
                UIHepler.showToast(activity, "该组药还未排药");
            }else{
                redirectChuanCiByScan(bottle);
                UIHepler.showToast(activity, "该病人没有穿刺，请先穿刺");
            }
        } else if (BottleStatusCategory.HADHANDLE.getKey() == bottle.BottleStatus) {
            if(QueueStatusCategory.FINISHED.getKey() ==bottle.PeopleInfo.Status){
                redirect2ExecuteSingleByScan(bottle);
                UIHepler.showToast(activity, "该组药还未配液");
            }else{
                redirectChuanCiByScan(bottle);
                UIHepler.showToast(activity, "该病人没有穿刺，请先穿刺");
            }
        }else if (BottleStatusCategory.WAITINGINFUSE.getKey() == bottle.BottleStatus) {
            if(QueueStatusCategory.FINISHED.getKey() ==bottle.PeopleInfo.Status){
                redirect2ExecuteSingleByScan(bottle);
            }else{
                redirectChuanCiByScan(bottle);
                UIHepler.showToast(activity, "该病人没有穿刺，请先穿刺");
            }
        }else if (BottleStatusCategory.CANCEL.getKey() == bottle.BottleStatus) {
            UIHepler.showToast(activity, "该瓶贴已经输液结束");
        }else if (BottleStatusCategory.HADINFUSE.getKey() == bottle.BottleStatus) {
            UIHepler.showToast(activity, "该瓶贴已经执行输液完成");
        }else {
            redirect2HandlerActivity(bottle);
        }
    }

    /**
     * 跳转到统一操作入口
     */
    public void redirect2HandlerActivity(BottleModel bottle) {
        LocalSetting.CurrentBottle = bottle;
        Intent intent = new Intent();
//      intent.setClass(activity, HandlerActivity.class);
        intent.setClass(activity, ZhongJiNewHandlerActivity.class);
        startActivity(intent);
    }

    /**
     * 扫描跳转到单次执行界面
     *
     * @param bottle 拼贴对象
     */
    public void redirect2ExecuteSingleByScan(BottleModel bottle) {
        LocalSetting.IsOpenByScan = true;
        LocalSetting.CurrentBottle =null;
        LocalSetting.CurrentBottle = bottle;
        Log.e("扫描后执行跳转执行界面的操作");
        Intent intent = new Intent();
//        intent.setClass(activity, XunshiExecuteSingleActivity.class);
        intent.setClass(activity, ZhongJiNewXunshiExecuteSingleActivity.class);
        startActivity(intent);
    }

    /**
     * 扫描跳转到单次执行界面
     *
     * @param bottle 拼贴对象
     */
    public void redirectChuanCiByScan(BottleModel bottle) {
        LocalSetting.IsOpenByScan = true;
        LocalSetting.CurrentBottle =null;
        LocalSetting.CurrentBottle = bottle;
        Log.e("扫描后执行跳转执行界面的操作");
        Intent intent = new Intent();
//        intent.setClass(activity, XunshiExecuteSingleActivity.class);
        intent.setClass(activity, ZhongjiChuancCiExecuteSingleActivity.class);
        startActivity(intent);
    }

    /**
     * 得到当前所选分类
     */
    private String getCurrentCatogryText(int position) {
        switch (position) {
            case CATOGRY_ALL:
                return "全部";
            case CATOGRY_UNDO:
                return "未完成";
            case CATOGRY_DONE:
                return "已完成";
        }
        return "";
    }

    /**
     * 得到当前所选分类
     */
    private String getCurrentCatogry(int position) {
        switch (position) {
            case CATOGRY_ALL:
                return InfoUtils.getAllStatusGroup(RoleCategory.ZHONGJI.getKey());
            case CATOGRY_UNDO:
                return InfoUtils.getUndoStatusGroup(LocalSetting.RoleIndex);
            case CATOGRY_DONE:
                return InfoUtils.getDoneStatusGroup(LocalSetting.RoleIndex);
        }
        return "";
    }

    @Override
    public void onRefresh() {
        getData();
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
            popMenu = new PopMenu(activity);
            popMenu.addItems(strings);
            popMenu.setOnItemClickListener(new PopMenu.OnItemClickListener() {
                @Override
                public void onItemClick(int index) {
                    String slectValue = strings[index];
                    if ("全部".equals(slectValue)) {
                        POSITION = CATOGRY_ALL;
                        getData();
                    } else if ("未完成".equals(slectValue)) {
                        POSITION = CATOGRY_UNDO;
                        getData();
                    } else if ("已完成".equals(slectValue)) {
                        POSITION = CATOGRY_DONE;
                        getData();
                    }else if("搜索".equals(slectValue)){
                        searchMessageByKeyword();
                    }

                }
            });
        }
        popMenu.showAsDropDown(mMore, -30, 0);
    }
    /**
     * 移除自动作业
     */
    public void removeAutoRefresh() {
        if (autoRefreshExecutor != null && !autoRefreshExecutor.isShutdown()) {
            autoRefreshExecutor.shutdownNow();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        addAutoRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeAutoRefresh();
    }
    /**
     * 增加自动作业
     */
    public void addAutoRefresh() {
        if (autoRefreshExecutor != null && !autoRefreshExecutor.isShutdown()) {
            autoRefreshExecutor.shutdownNow();
        }
        autoRefreshExecutor = new LogOnExceptionScheduledExecutor(1);
        if (RoleCategory.ZHONGJI.getKey() == LocalSetting.RoleIndex) {
            autoRefreshExecutor.scheduleAtFixedRate(new AutoRefreshCallTask(),
                    0, 10000,
                    TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 自动轮训呼叫
     */
    private class AutoRefreshCallTask implements Runnable {
        @Override
        public void run() {
            String areaID = XmlDB.getInstance(activity).getKeyString("areaID", "1");
            String strings = executeSyncHttp(RestClient.BASE_URL + ChuanCiApi.Url_GetCallList(1, areaID));
            Message message = new Message();
            Log.d("tag", "strings:" + strings);
            if ("[]".equals(strings) || "".equals(strings) || null == strings) {
                message.what = 0;
                handler.sendMessage(message);
            } else {
                strings = strings.replace("[", "").replace("]", "");
                if (!StringUtils.StringIsEmpty(strings)) {
                    String[] arrays = strings.split(",");
                    message.what = arrays.length;
                    message.obj = strings;
                    if (strings != null && arrays.length > 0) {
                        handler.sendMessage(message);
                    }
                } else {
                    message.what = 0;
                    handler.sendMessage(message);
                }

            }
        }
    }

    public String executeSyncHttp(String urlString) {
        String result = "";
        URL url = null;
        HttpURLConnection connection = null;
        InputStreamReader in = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            in = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuilder strBuffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
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
                    ActionItemBadge.update(activity, mframeLayout, Iconify.IconValue.fa_bell_o,
                            ActionItemBadge.BadgeStyle.RED, count);
                } else {
                    ActionItemBadge.update(activity, mframeLayout, Iconify.IconValue.fa_bell_o,
                            ActionItemBadge.BadgeStyle.DARKGREY, count);
                }

                if (count >= 1) {
                    String lastCalls = XmlDB.getInstance(activity).getKeyString("last_call_ids", "0");
                    String currentCalls = callString;
                    Log.d("tag", "count=" + count);
                    if ("0".equals(lastCalls)) {
                        Log.d("后台有数据呼叫=========" + lastCalls + "/" + currentCalls, count + "");
                        setBeep();
                        XmlDB.getInstance(activity).saveKey("last_call_ids", currentCalls);
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
                        XmlDB.getInstance(activity).saveKey("last_call_ids", currentCalls);
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
        InfoUtils.Vibrate(activity, new long[]{10, 1500, 100, 2500}, false);
    }

    public void searchMessageByKeyword(){
        LayoutInflater inflater  = LayoutInflater.from(fatherActivity);
        View view = inflater.inflate(R.layout.view_edittext_layout,null);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_edittext);
        LinearLayout view_height = (LinearLayout) view.findViewById(R.id.dialog_view);
        view_height.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,40));
        new AlertDialog.Builder(fatherActivity).setTitle("请输入门诊号或者流水号")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String keyWorld = StringUtils.getStringContainSpecialFlag(editText.getText().toString());
                        if (keyWorld.equals("")) {     //删除关键字到空或者初始状态
                            groupBottleModels.clear();
                            if(originalGroupBottles !=null)//报空指针
                                groupBottleModels.addAll(originalGroupBottles);
                            bottleAdapter.notifyDataSetChanged();
                        } else if (keyWorld.length() <= 4 ||keyWorld.length() == 10) {      //有关键字啦
                            searchGroupBottles = new ArrayList<GroupBottleModel>();
                            for (GroupBottleModel group : originalGroupBottles) {
                                //                        if (group.Name.contains(keyWorld)
                                //                                || group.PatId.contains(keyWorld)
                                //                                || group.Lsh.equals(keyWorld)
                                //                                ) {
                                //                            searchGroupBottles.add(group);
                                //                        }
                                if(keyWorld.length()>5 &&group.PatId.contains(keyWorld)){
                                    searchGroupBottles.add(group);
                                }else if(group.Lsh.equals(keyWorld)){
                                    searchGroupBottles.add(group);
                                }
                            }
                            if (searchGroupBottles.size() <= 0)
                                getSearchBottles(keyWorld);
                            groupBottleModels.clear();
                            groupBottleModels.addAll(searchGroupBottles);
                            bottleAdapter.notifyDataSetChanged();
                            searchGroupBottles.clear();//清空

                        }
                    }
                }).setNegativeButton("取消",null).create().show();
    }
    /**
     * 搜索功能
     * @param keyWorld
     */
    private void getSearchBottles(final String keyWorld) {
        showLoadingDialog("加载信息中");
        String url = InfoApi.url_getBottlesByKeyword(keyWorld);
        RestClient.get(url ,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {
                dismissLoadingDialog();
                List<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
                if (bottleModels.size() > 0) {
                    infusionDetailDAO.deleteBottlesByPatid(keyWorld);
                    infusionDetailDAO.save(bottleModels);
                    ArrayList<GroupBottleModel> groupBottleModels1 = InfoUtils.toGroup(bottleModels);
                    if (searchGroupBottles != null) searchGroupBottles.clear();
                    for (GroupBottleModel groupBottleModel : groupBottleModels1) {
                        searchGroupBottles.add(groupBottleModel);
                    }
                    groupBottleModels.clear();
                    groupBottleModels.addAll(searchGroupBottles);
                    bottleAdapter.notifyDataSetChanged();
                    searchGroupBottles.clear();//清空
                } else {
                    progressContainer.setVisibility(View.VISIBLE);
                    UIHepler.showToast(activity,"未检索到到数据,请重试!");
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                dismissLoadingDialog();
                UIHepler.showToast(activity,"加载失败");
            }
        });

    }
}
