package com.fugao.infusion.chuaici;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.comonPage.CustomCallActivity;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.dao.DataBaseInfo;
import com.fugao.infusion.dao.InfusionDetailDAO;
import com.fugao.infusion.dao.UploadInfusionDetailDAO;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.model.QueueModel;
import com.fugao.infusion.utils.AlarmManagerUtils;
import com.fugao.infusion.utils.DateUtils;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.PullXMLTools;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.UIHelper;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.view.AnimatedExpandableListView;
import com.fugao.infusion.view.PopMenu;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.ResourceUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.InjectView;

/**
 * 穿刺列表
 */
public class ChuanCiPeopleFragment extends BaseFragmentV4 implements SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.title_text_view)
    TextView mTitleTextView;
    @InjectView(R.id.listView)
    AnimatedExpandableListView listView;
    @InjectView(R.id.un_complete_refresh_layout)
    SwipeRefreshLayout mUnCompleteRefreshLayout;
    @InjectView(R.id.more)
    LinearLayout more;
    @InjectView(R.id.chuanchi_title)
    TextView mChuanchiTitle;
    @InjectView(R.id.progressContainer)
    LinearLayout progressContainer;


    private ChuanCiActivity activity;
    /**
     * handler 相关参数
     */
    private final int AUTOUPDATETIME = 60000;
    private final int AUTOUPPUTTIME = 30000;
    private final int TIMERCHECKDATA = 10000;

    private final int AOTUUPDATE = 0;
    private final int AOTUPUTDATE = 1;
    private final int SETSUBTITLE = 2;
    private final int SAVEDATA = 3;

    /**
     * 显示副标题常量
     */
    private String REFRESHTIME;
    private String UNPUTCOUNT = "0";
    /**
     * 程序默认进来是 未完成 拼贴列表 0-全部 1-未完成 2-已完成
     */
    private static final int CATOGRY_ALL = 0;
    private static final int CATOGRY_UNDO = 1;
    private static final int CATOGRY_DONE = 2;
    private int POSITION = 1;
    /**
     * 对象初始化
     */
    private static ArrayList<GroupBottleModel> originalGroupBottles;
    private ArrayList<GroupBottleModel> groupBottleModels = new ArrayList<GroupBottleModel>();
    private ArrayList<GroupBottleModel> searchGroupBottles;
    private ChuanCiGroupBottleAdapter bottleAdapter;
    private PopMenu popMenu;
    private Timer timerCheckData;
    private Handler handler;
    private AlertDialog alertDialogOverDue;
    /**
     * 初始化String
     */
    private String patID;
    private InfusionDetailDAO infusionDetailDAO;
    private String[] strings;

    private String[] strsMenu; //字符串数 呼叫和打印座位
    private boolean flag = true;
    private String DATEFROMAT = "HH:mm:ss";
    private boolean allowUpdate = true;
    private ArrayList<BottleModel> autoUpbottleModels;
    private UploadInfusionDetailDAO uploadInfusionDetailDAO;
    private String printModeString ="";
    private View currentView;
    private TextView count_id;
    private String count_id_s;


    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView=inflater.inflate(R.layout.fragment_chuan_ci_people, container, false);
        return currentView;
    }

    @Override
    public void initView(View currentView) {
        activity = (ChuanCiActivity) fatherActivity;
        mUnCompleteRefreshLayout.setOnRefreshListener(this);
        mUnCompleteRefreshLayout.setColorScheme(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
        count_id= (TextView) currentView.findViewById(R.id.count_id_chuanci);
    }

    @Override
    public void initData() {
        count_id_s=XmlDB.getInstance(fatherActivity).getKeyString("AcountId","");
        count_id.setText(count_id_s);
        printModeString = XmlDB.getInstance(fatherActivity).getKeyString("printSetting","蓝牙打印");
        REFRESHTIME = DateUtils.getCurrentDate(DATEFROMAT);
        strings = ResourceUtils.getResouce4Arrays(activity, R.array.statues_arrays);
        infusionDetailDAO = activity.infusionDetailDAO;
//        unPutInfusionDetailDAO = new UnPutInfusionDetailDAO(DataBaseInfo.getInstance(fatherActivity));
        uploadInfusionDetailDAO = new UploadInfusionDetailDAO(DataBaseInfo.getInstance(fatherActivity));
        originalGroupBottles = new ArrayList<GroupBottleModel>();
        bottleAdapter = new ChuanCiGroupBottleAdapter(fatherActivity, originalGroupBottles);
        listView.setAdapter(bottleAdapter);
        initHandler();
        getData();
        mTitleTextView.setText("刷新：" + com.jasonchen.base.utils.DateUtils.getCurrentDate(DATEFROMAT) + " ↑0");
        AlarmManagerUtils.sendPutBroadcast(activity);
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

    /**
     * 点击跳转到单次执行界面
     *
     * @param bottle 拼贴对象
     */
    public void redirect2ExecuteSingleByClick(final BottleModel bottle) {
        LocalSetting.IsOpenByScan = true;
        LocalSetting.CurrentBottle =null;
        LocalSetting.CurrentBottle = bottle;
        if(bottle.totle>0){
            LocalSetting.bottletotle=bottle.totle;
        }else{
            for(int i=0;i<groupBottleModels.size();i++){
                if(groupBottleModels.get(i).PatId.equals(bottle.PeopleInfo.PatId)){
                    LocalSetting.bottletotle=groupBottleModels.get(i).bottletotle;
                    break;
                }
            }
        }
        checkedIsOverDue(bottle);
        }
    @Override
    public void initListener() {
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopMenu();
            }
        });

        /**
         * 穿刺界面增加长按事件进行打印座位
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                           long id) {
                String string = PullXMLTools.parseXml(fatherActivity, Constant.DEFAUL_CONFIG_XML, "hospital");
                if (string.equals("zzey")) {

                }else if(string.equals("xhyy")){

                }else{
                    UIHelper.showListDialog(fatherActivity, groupBottleModels.get(position).Name,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int subPosition) {
                                    switch (subPosition) {
                                        case 0:
                                            /**
                                             * 自定义呼叫
                                             */
                                            LocalSetting.CurrentGroupBottle = groupBottleModels.get(position);
                                            Intent intent = new Intent(fatherActivity, CustomCallActivity.class);
                                            startActivity(intent);
                                            //doCustomCall(position);
                                            break;
                                        case 1:
                                            /**
                                             * 蓝牙打印座位
                                             */
                                            if (printModeString.contains("蓝牙打印")) {
                                                doPrintSeatNo(position);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }, strsMenu
                    );
                }


                return false;
            }
        });

    }

    /**
     * 打印座位
     */
    private void doPrintSeatNo(int position) {
        if (groupBottleModels.get(position).ObserveFlag != 1) {
            InfoUtils.checkBlueTooth(fatherActivity);
            if(StringUtils.StringIsEmpty(groupBottleModels.get(position).SeatNo)){
                UIHelper.showToast("还未未分配座位，无法打印");
                return;
            }
            String PeopleContent=groupBottleModels.get(position).Name +":" +groupBottleModels.get(position).PatId ;
            InfoUtils.bluetoothPrintSeatNo(getActivity(),StringUtils.getString(LocalSetting.divZone)+":"+groupBottleModels.get(position).SeatNo,PeopleContent);
        }
    }
    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case AOTUUPDATE:
//                        autoUpdating();
                        break;
                    case SETSUBTITLE:
                        setSubTitle();
                        break;
                    case SAVEDATA:
                        infusionDetailDAO.save(autoUpbottleModels);
                        break;
                    case 10:
                        mUnCompleteRefreshLayout.setRefreshing(true);
                    default:
                        break;
                }
            }
        };
    }
    /**
     * 检查该组药是否已经过期
     *
     * @PARAM BOTTLE
     */
    private void checkedIsOverDue(final BottleModel bottle) {
        Log.e("","检查瓶贴是否过期");
        long zerotime=getTodayZero();
        String date = bottle.RegistrationDate + bottle.RegistrationTime;
        String stringTime = DateUtils.changeDateFormat(StringUtils.getString(date), "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss");
        long starttime = DateUtils.getStandardDateToMills(stringTime);
        long nowTime = System.currentTimeMillis();
        int hours = 0;
        hours = (int) ((nowTime -starttime) / (1000 * 60 *60));
        String string = PullXMLTools.parseXml(fatherActivity, Constant.DEFAUL_CONFIG_XML, "hospital");
//        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date d1=new Date(zerotime);
//        Log.d("111",format.format(d1));
//        Date d2=new Date(starttime);
//        Log.d("222",format.format(d2));
        if(string.equals("zzey")||string.equals("xhyy")){
            if (starttime <= zerotime) {//测试
                TextView textView = new TextView(activity);
                textView.setTextColor(Color.RED);
                textView.setTextSize(22);
                textView.setGravity(Gravity.CENTER);
                textView.setMinHeight(80);
                textView.setText("该组药已过期");
                alertDialogOverDue = new AlertDialog.Builder(activity)
                        .setTitle("提醒！")
                        .setIcon(R.drawable.warning)
                        .setView(textView)
                        .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scanExcuteBottle(bottle);
                            }
                        })
                        .setNegativeButton("弃用",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //带完善接口
                                if(bottle.BottleStatus !=BottleStatusCategory.CANCEL.getKey()){
                                    final List<BottleModel> bottles = new ArrayList<BottleModel>();
                                    bottle.BottleStatus = BottleStatusCategory.CANCEL.getKey();
                                    bottles.add(bottle);
                                    String url = InfoApi.url_updateBottlesAndQueues(LocalSetting.CurrentAccount.UserName);
                                    String jsonStr = String2InfusionModel.bottleModles2String((ArrayList<BottleModel>) bottles);
                                    RestClient.put(fatherActivity,url,jsonStr,new BaseAsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int i, String s) {
                                            /**
                                             * 更新本地信息
                                             */
                                            bottle.BottleStatus = BottleStatusCategory.CANCEL.getKey();
                                            infusionDetailDAO.update(bottle,false);
                                            UIHelper.showToast("执行成功!");
                                        }

                                        @Override
                                        public void onFailure(int i, Throwable throwable, String s) {
                                            UIHelper.showToast(fatherActivity,"弃用该瓶贴失败");
                                        }
                                    });
                                }
                            }
                        }).create();
                alertDialogOverDue.show();
            } else {
                scanExcuteBottle(bottle);
            }
        }else{
            if (hours >= 6) {//测试
                TextView textView = new TextView(activity);
                textView.setTextColor(Color.RED);
                textView.setTextSize(22);
                textView.setGravity(Gravity.CENTER);
                textView.setMinHeight(80);
                textView.setText("该组药已过期");
                alertDialogOverDue = new AlertDialog.Builder(activity)
                        .setTitle("提醒！")
                        .setIcon(R.drawable.warning)
                        .setView(textView)
                        .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scanExcuteBottle(bottle);
                            }
                        })
                        .setNegativeButton("弃用",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //带完善接口
                                if(bottle.BottleStatus !=BottleStatusCategory.CANCEL.getKey()){
                                    final List<BottleModel> bottles = new ArrayList<BottleModel>();
                                    bottle.BottleStatus = BottleStatusCategory.CANCEL.getKey();
                                    bottles.add(bottle);
                                    String url = InfoApi.url_updateBottlesAndQueues(LocalSetting.CurrentAccount.UserName);
                                    String jsonStr = String2InfusionModel.bottleModles2String((ArrayList<BottleModel>) bottles);
                                    RestClient.put(fatherActivity,url,jsonStr,new BaseAsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int i, String s) {
                                            /**
                                             * 更新本地信息
                                             */
                                            bottle.BottleStatus = BottleStatusCategory.CANCEL.getKey();
                                            infusionDetailDAO.update(bottle,false);
                                            UIHelper.showToast("执行成功!");
                                        }

                                        @Override
                                        public void onFailure(int i, Throwable throwable, String s) {
                                            UIHelper.showToast(fatherActivity,"弃用该瓶贴失败");
                                        }
                                    });
                                }
                            }
                        }).create();
                alertDialogOverDue.show();
            } else {
                scanExcuteBottle(bottle);
            }
        }
    }

    /**
     * 得到瓶贴进行跳转到穿刺界面
     * @param bottle
     */
    public void scanExcuteBottle(final BottleModel bottle) {
        Log.e("", "执行扫描瓶贴操作");
        String string = PullXMLTools.parseXml(fatherActivity, Constant.DEFAUL_CONFIG_XML, "hospital");
        if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
            if (BottleStatusCategory.WAITINGHANDLE.getKey() == bottle.BottleStatus) {
                excutePunctureAnyway(bottle);
                if(string.equals("zzey")){

                }else if(string.equals("xhyy")){

                }else{
                    UIHepler.showToast(activity,"该组药还未排药！");
                }
            } else if (BottleStatusCategory.HADHANDLE.getKey() == bottle.BottleStatus) {
                excutePunctureAnyway(bottle);
                if(string.equals("zzey")){

                }else{
                    UIHepler.showToast(activity,"该组药还未配液");
                }
            } else if (BottleStatusCategory.WAITINGINFUSE.getKey() == bottle.BottleStatus) {
                excutePunctureAnyway(bottle);
            } else if (BottleStatusCategory.INFUSIONG.getKey() == bottle.BottleStatus) {
                redirect2ExecuteSingleByScan(bottle);
            } else if (BottleStatusCategory.HADINFUSE.getKey() == bottle.BottleStatus) {
                UIHepler.showToast(activity, "该瓶帖已完成输液");
            } else {
                UIHepler.showToast(activity,"该瓶帖已经输液结束");
            }
        }
    }
    private void excutePunctureAnyway(BottleModel bottle) {
        QueueModel queue = bottle.PeopleInfo;
        if(queue != null){
            redirect2ExecuteSingleByScan(bottle);
        }
    }
    /**
     * 扫描跳转到单次执行界面
     *
     * @param bottle 拼贴对象
     */
    public void redirect2ExecuteSingleByScan(BottleModel bottle) {
        LocalSetting.IsOpenByScan = true;
        LocalSetting.CurrentBottle = bottle;
        Log.e("","扫描后执行跳转执行界面的操作");
        Intent intent = new Intent();
        intent.setClass(activity,ChuancCiExecuteSingleActivity.class);
        startActivity(intent);
    }

    /**
     * 上传前先检查本地数据是否有未上传数据
     */
    private void checkupDataLoading() {
        mChuanchiTitle.setText(InfoUtils.getCurrentRoleName() + "-" + getCurrentCatogryText(POSITION));

        final ArrayList<BottleModel> bottleModels = uploadInfusionDetailDAO.getBottleListAndUnUpload();
        if (bottleModels.size() > 0) {
            String url = ChuanCiApi.url_updateBottlesAndQueues(LocalSetting.CurrentAccount.Id);
            String Json = String2InfusionModel.bottleModles2String(bottleModels);
            RestClient.put(activity,url,Json, new BaseAsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    mUnCompleteRefreshLayout.setRefreshing(true);
                    uploadInfusionDetailDAO.updateIsUploadStatusToUploading((ArrayList)bottleModels);
                    super.onStart();
                }

                @Override
                public void onSuccess(int i, String s) {
//                    infusionDetailDAO.updateDateFromServer((ArrayList) bottleModels);
                    uploadInfusionDetailDAO.updateIsUploadStatus((ArrayList) bottleModels);
                    getData();
                }

                @Override
                public void onFailure(int i, Throwable throwable, String s) {
                    UIHepler.showToast(activity, "获取数据失败,请重试");
                    mUnCompleteRefreshLayout.setRefreshing(false);
                    uploadInfusionDetailDAO.updateIsUploadStatusToUnUpload((ArrayList)bottleModels);
                }
            });
        } else {
            getData();
        }

    }

    private void getData() {
        for (int i = 0; i < bottleAdapter.getGroupCount(); i++) {
            listView.collapseGroup(i);
        }
        String tempCatogry = InfoUtils.getCurrentRoleName() + "-" + getCurrentCatogryText(POSITION);
        mChuanchiTitle.setText(tempCatogry);
        String deptId = XmlDB.getInstance(fatherActivity).getKeyString("deptID", "0");
        String url = ChuanCiApi.url_getBottlesByStatusGroup(deptId, getCurrentCatogry(POSITION));
        final long l1 = System.currentTimeMillis();
        Log.e("", "获取数据," + l1);
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                Message msg = Message.obtain();
                msg.what = 10;
                msg.obj = this;
                handler.sendMessage(msg);
            }

            @Override
            public void onSuccess(int i, String s) {
                long l2 = System.currentTimeMillis();
                REFRESHTIME = DateUtils.getCurrentDate(DATEFROMAT);
                Message msg = Message.obtain();
                msg.what = SETSUBTITLE;
                msg.obj = this;
                handler.sendMessage(msg);
                Log.e("", "访问 - 获取数据成功耗时," + (l2 - l1));
                final ArrayList<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
                Log.e("", "解析耗时" +
                        (System.currentTimeMillis() - l2));
                if (bottleModels.size() > 0) {
                    XmlDB.getInstance(fatherActivity).saveKey(Constant.LASTUPDATETIME, Constant.LASTUPDATETIMEFLAG);
                    long l5 = System.currentTimeMillis();
                    ExecutorService singleThreadService = Executors.newSingleThreadExecutor();
                    singleThreadService.execute(new Runnable() {
                        @Override
                        public void run() {
                            infusionDetailDAO.deleteAllInfo();
                            uploadInfusionDetailDAO.deleteAllInfo();
                            infusionDetailDAO.saveToInfusionDetail(bottleModels);
                        }
                    });
                    groupBottleModels.clear();
                    long l3 = System.currentTimeMillis();
                    Log.e("", "解析成功 - 操作数据库耗时," + (l3 - l2));
                    originalGroupBottles = InfoUtils.toGroup(bottleModels);
                    groupBottleModels.addAll(originalGroupBottles);
                    bottleAdapter = new ChuanCiGroupBottleAdapter(fatherActivity, groupBottleModels);
                    listView.setAdapter(bottleAdapter);
                    long l4 = System.currentTimeMillis();
                    Log.e("", "数据库 - 显示到界面上的时间耗时," + (l4 - l3));
                    if (flag) {
                        startAutotask();
                        flag = false;
                    }
                } else {
                    groupBottleModels.clear();
                    UIHepler.showToast(fatherActivity, "没有数据");
                    bottleAdapter.notifyDataSetChanged();
                }
                mUnCompleteRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                UIHepler.showToast(activity, "获取数据失败");
                mUnCompleteRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        endAutotask();
        XmlDB.getInstance(fatherActivity).saveKey(Constant.LASTUPDATETIME, Constant.LASTUPDATETIMEFLAG);
        AlarmManagerUtils.cancelPutBroadcast(activity);
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
                return InfoUtils.getAllStatusGroup(RoleCategory.CHUANCI.getKey());
            case CATOGRY_UNDO:
                return InfoUtils.getUndoStatusGroup(LocalSetting.RoleIndex);
            case CATOGRY_DONE:
                return InfoUtils.getDoneStatusGroup(LocalSetting.RoleIndex);
        }
        return "";
    }

    @Override
    public void onRefresh() {
        checkupDataLoading();
//        getData();
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
                        checkupDataLoading();
                    } else if ("未完成".equals(slectValue)) {
                        POSITION = CATOGRY_UNDO;
                        checkupDataLoading();
                    } else if ("已完成".equals(slectValue)) {
                        POSITION = CATOGRY_DONE;
                        checkupDataLoading();
                    } else if ("搜索".equals(slectValue)) {
                        searchMessageByKeyword();
                    }

                }
            });
        }
        popMenu.showAsDropDown(more, -30, 0);
    }

    /**
     * 开始定时任务
     */
    private void startAutotask() {
        timerCheckData = new Timer();
        timerCheckData.schedule(new TimerTask() {
            @Override
            public void run() {
                UNPUTCOUNT = uploadInfusionDetailDAO.getUnUploadCount() + "";
//                UNPUTCOUNT = BottleModels.size() + "";
                handler.sendEmptyMessage(SETSUBTITLE);
            }
        }, TIMERCHECKDATA, TIMERCHECKDATA);

    }

    /**
     * 取消定时任务
     */
    private void endAutotask() {
        if(timerCheckData!=null){
            timerCheckData.cancel();
        }
    }

    private void setSubTitle() {
        mTitleTextView.setText("刷新：" + REFRESHTIME + "  ↑" + UNPUTCOUNT);
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
        RestClient.get(url, new BaseAsyncHttpResponseHandler() {
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
                    UIHepler.showToast(activity, "未检索到到数据,请重试!");
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                dismissLoadingDialog();
                UIHepler.showToast(activity, "加载失败");
            }
        });
    }
    public long getTodayZero(){
        Date date = new Date();
        long l = 24*60*60*1000; //每天的毫秒数
        //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        //减8个小时的毫秒值是为了解决时区的问题。
        return (date.getTime() - (date.getTime()%l) - 8* 60 * 60 *1000);
    }
//    /**
//     * 回收native的图片资源
//     */
//    public void cycleResourceBitmap(){
//        bottleAdapter.cyleBitmap();
//    }
}
