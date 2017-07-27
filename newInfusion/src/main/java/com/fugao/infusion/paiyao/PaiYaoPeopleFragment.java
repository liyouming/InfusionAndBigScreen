package com.fugao.infusion.paiyao;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseFragmentV4;
import com.fugao.infusion.comonPage.CustomCallActivity;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.dao.InfusionDetailDAO;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.UIHelper;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.view.AnimatedExpandableListView;
import com.fugao.infusion.view.PopMenu;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.ResourceUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.InjectView;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: InfusionApps
 * @Location: com.fugao.infusion.peiye.PaiYaoPeopleFragment
 * @Description: TODO
 * @author: 蒋光锦  jiangguangjin@fugao.com
 * @date: 2014/12/23 16:26
 * @version: V1.0
 */

public class PaiYaoPeopleFragment extends BaseFragmentV4 implements SwipeRefreshLayout.OnRefreshListener{

    @InjectView(R.id.chuanchi_title)
    public TextView mChuanchiTitle;
    @InjectView(R.id.more)
    LinearLayout mMore;
    @InjectView(R.id.listView)
    AnimatedExpandableListView mListView;
    @InjectView(R.id.un_complete_refresh_layout)
    SwipeRefreshLayout mUnCompleteRefreshLayout;
    @InjectView(R.id.progressContainer)
    LinearLayout progressContainer;
    @InjectView(R.id.totitleMemerry)
    TextView mTotitleMemerry;
    private Handler handler;


    private ArrayList<GroupBottleModel> searchGroupBottles;

    private PaiYaoActivity activity;
    private PopMenu popMenu;
    private String[] strings;
    private ArrayList<GroupBottleModel> groupBottleModels;
    private ArrayList<GroupBottleModel> originalGroupBottles;

    private String[] strsMenu;

    /**
     * 程序默认进来是 未完成 拼贴列表 0-全部 1-未完成 2-已完成 3-搜索
     */
    private  final int CATOGRY_ALL = 0;
    private  final int CATOGRY_UNDO = 1;
    private  final int CATOGRY_DONE = 2;
    private  final int CATOGRY_SEARCH=3;






    private int POSITION = 1;
    private PaiYaoGroupBottleAdapter bottleAdapter;
    private InfusionDetailDAO infusionDetailDAO;
    // 视图
    private View currentView;
    private TextView count_id;
    private TextView check_id;
    private String count_id_s;
    private String check_id_s;
    @Override
    public View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView=inflater.inflate(R.layout.fragment_pai_yao_people, container,false);
        return currentView;
    }

    @Override
    public void initView(View currentView) {
        activity = (PaiYaoActivity) fatherActivity;
        mUnCompleteRefreshLayout.setOnRefreshListener(this);
        mUnCompleteRefreshLayout.setColorScheme(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
        count_id= (TextView) currentView.findViewById(R.id.count_id);
        check_id= (TextView) currentView.findViewById(R.id.check_id);
    }

    @Override
    public void initData() {
        count_id_s=XmlDB.getInstance(fatherActivity).getKeyString("AcountId","");
        check_id_s=XmlDB.getInstance(fatherActivity).getKeyString("CheckId","");
        count_id.setText(count_id_s);
        check_id.setText(check_id_s);
        groupBottleModels = new ArrayList<GroupBottleModel>();
        infusionDetailDAO = activity.infusionDetailDAO;
        strings = ResourceUtils.getResouce4Arrays(activity, R.array.statues_arrays);
        bottleAdapter = new PaiYaoGroupBottleAdapter(fatherActivity, groupBottleModels);
        mListView.setAdapter (bottleAdapter);
        initHandler();
//        startAutotask();
        getData();
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
     * 得到数据
     */
    public void getData(){
        String tempCatogry = InfoUtils.getCurrentRoleName() + "-" + getCurrentCatogryText(POSITION);
        mChuanchiTitle.setText(tempCatogry);
        String deptId = XmlDB.getInstance(fatherActivity).getKeyString("deptID", "0");
        final long long3 = System.currentTimeMillis();
        String url = ChuanCiApi.url_getBottlesByStatusGroup(deptId, getCurrentCatogry(POSITION));
        RestClient.get(url,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = this;
                handler.sendMessage(msg);
            }
            @Override
            public void onSuccess(int i, String s) {
                long long4 = System.currentTimeMillis();
                Log.e("服务器请求耗时",(long4-long3)+"毫秒");
                ArrayList<BottleModel> bottleModels = null;
                try {
                    //测试自己解析的Model
                    long long1 = System.currentTimeMillis();
                    bottleModels = InfoUtils.jsonStringTOModel(s);
                    long long2 = System.currentTimeMillis();
                    Log.e("解析转换耗时",(long2-long1)+"毫秒");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                final ArrayList<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
                if(bottleModels!=null&&bottleModels.size()>0){
                    ExecutorService singleThreadService = Executors.newSingleThreadExecutor();
                    final ArrayList<BottleModel> finalBottleModels = bottleModels;
                    singleThreadService.execute(new Runnable() {
                        @Override
                        public void run() {
                            infusionDetailDAO.deleteAllInfo();
                            infusionDetailDAO.saveToInfusionDetail(finalBottleModels);
                        }
                    });
                    groupBottleModels.clear();
                    originalGroupBottles = InfoUtils.toGroup(bottleModels);
                    groupBottleModels.addAll(originalGroupBottles);
                    bottleAdapter = new PaiYaoGroupBottleAdapter(fatherActivity, groupBottleModels);
                    mListView.setAdapter(bottleAdapter);
                }else {
                    groupBottleModels.clear();
                    UIHepler.showToast(fatherActivity, "没有数据");
                    bottleAdapter.notifyDataSetChanged();
                }
                mUnCompleteRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                UIHepler.showToast(activity, "加载失败");
                mUnCompleteRefreshLayout.setRefreshing(false);
            }
        });

    }
    @Override
    public void initListener() {
        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopMenu();
            }
        });
     /**
     *  配药界面增加长按事件进行打印座位
     */

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                           long id) {
                /**
                 * 由于我们执行完了不去刷新列表，点击的时候可能会数组越界，不知道具体的操作只能异常抛出让他刷新列表
                 */
                try {
                    groupBottleModels.get(position);
                }catch ( Exception e){
                    e.printStackTrace();
                    UIHepler.showToast(activity,"请刷新一下排药列表");
                    return true;
                }
                UIHelper.showListDialog(fatherActivity, groupBottleModels.get(position).Name,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int subPosition) {
                                switch (subPosition) {
                                    case 0:
                                        /**
                                         * 自定义呼叫
                                         */
                                        //doCustomCall(position);
                                        LocalSetting.CurrentGroupBottle = groupBottleModels.get(position);
                                        Intent intent = new Intent(fatherActivity,CustomCallActivity.class);
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
                        },strsMenu //strsMenu  showPopMenu
                );

                return false;
            }
        });
    }



    /**
     * 自定义呼叫
     */
    private void doCustomCall(int position) {
        LocalSetting.CurrentGroupBottle = groupBottleModels.get(position);
        //((PaiYaoActivity) fatherActivity).openActivity(CustomCallActivity.class);
        //Intent intent = new Intent();

        //startActivity(new Int);
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
//                        getData();
//                        UIHepler.showToast(activity,"开发中...");
                    }
                }
            });
        }
        popMenu.showAsDropDown(mMore, -30, 0);
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
    /**
     * 点击跳转到单次执行界面
     *
     * @param bottle 拼贴对象
     */
    public void redirect2ExecuteSingleByClick(BottleModel bottle) {
        LocalSetting.CurrentBottle =null;
        LocalSetting.CurrentBottle = bottle;
        if (BottleStatusCategory.WAITINGHANDLE.getKey() == bottle.BottleStatus) {
            Intent intent = new Intent();
            intent.setClass(activity,PaiyaoExecuteBatchActivity.class);
            startActivity(intent);
        } else {
            UIHepler.showToast(activity,"该组药已经排药!");
        }
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
                            if(originalGroupBottles !=null){
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
                            }
                            if (searchGroupBottles.size() <= 0)
                            getSearchBottles(keyWorld);
                            groupBottleModels.clear();
                            groupBottleModels.addAll(searchGroupBottles);
                            bottleAdapter.notifyDataSetChanged();
                            searchGroupBottles.clear();//清空

                        }else {
                            Toast.makeText(fatherActivity,"输入的门诊号格式不对！",Toast.LENGTH_LONG).show();
                        }
                    }
                }).setNegativeButton("取消", null).create().show();
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
                ArrayList<BottleModel> bottleModels = null;
                try {
                    //测试自己解析的Model
                    long long1 = System.currentTimeMillis();
                    bottleModels = InfoUtils.jsonStringTOModel(s);
                    long long2 = System.currentTimeMillis();
                    Log.e("解析转换耗时",(long2-long1)/1000+"秒");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                List<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
                if (bottleModels!= null  &&  bottleModels.size() > 0) {
                    infusionDetailDAO.deleteBottlesByPatid(keyWorld);
                    infusionDetailDAO.save(bottleModels);
                    ArrayList<GroupBottleModel> groupBottleModels1 = InfoUtils.toGroup(bottleModels);
                    if (searchGroupBottles != null)
                        searchGroupBottles.clear();
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

    /**
     * 处理消息
     */
    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        mUnCompleteRefreshLayout.setRefreshing(true);
//                    case 2:
//                        checkedMemerry();
//                        break;
                    default:
                        break;
                }
            }
        };
    }
    /**
     * 开始定时任务
     */
    private void startAutotask() {
        Timer timerCheckData = new Timer();
        timerCheckData.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(2);
            }
        }, 0, 60000);

    }
    /**
     * 检查内存使用情况
     */
    private void checkedMemerry(){
//        int pid = android.os.Process.myPid();
//        ActivityManager activityManager = (ActivityManager) fatherActivity.getSystemService(Context.ACTIVITY_SERVICE);
//        android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(new int[] {pid});
//        Log.d("TAG", "本应用当前使用了" + (int) memoryInfoArray[0].getTotalPrivateDirty() / 1024 + "mb的内存");
//        mTotitleMemerry.setText((float) memoryInfoArray[0].getTotalPrivateDirty() / 1024 + "M");


//        ActivityManager activityManager = (ActivityManager) fatherActivity.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        activityManager.getMemoryInfo(memoryInfo);
//        mTotitleMemerry.setText((int)((memoryInfo.availMem >> 10) / 1024)+"M");


        //应用程序最大可用内存
        int maxMemory = ((int) Runtime.getRuntime().maxMemory())/1024/1024;
        // 应用程序已获得内存
        long totalMemory = ((int) Runtime.getRuntime().totalMemory())/1024/1024;
        // 应用程序已获得内存中未使用内存
        long freeMemory = ((int) Runtime.getRuntime().freeMemory())/1024/1024;
        mTotitleMemerry.setText(maxMemory+" "+totalMemory+" "+ freeMemory+"M");
    }

//    /**
//     * 回收native的图片资源
//     */
//    public void cycleResourceBitmap(){
//        bottleAdapter.cyleBitmap();
//    }
}