
package com.fugao.infusion.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

import com.fugao.infusion.base.BaseActivity;
import com.fugao.infusion.bluetooth.bluetooth.BluetoothPrintFormatUtil;
import com.fugao.infusion.bluetooth.bluetooth.BluetoothUtils;
import com.fugao.infusion.bluetooth.bluetooth.ChooseBluetoothDeviceActivity;
import com.fugao.infusion.bluetooth.bluetooth.PrintStyle;
import com.fugao.infusion.bluetooth.bluetooth.PrintStytleModel;
import com.fugao.infusion.bluetooth.service.BlueToothService;
import com.fugao.infusion.bluetooth.service.BluetoothPrintService;
import com.fugao.infusion.constant.BottleStatusCategory;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.DrugDetailModel;
import com.fugao.infusion.model.GroupBottleModel;
import com.fugao.infusion.model.InfusionEventModel;
import com.fugao.infusion.model.PatrolModel;
import com.fugao.infusion.model.QueueModel;
import com.honeywell.devicesettings.DeviceSettings;
import com.jasonchen.base.utils.AppManager;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.DES;
import com.jasonchen.base.utils.DateUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FugaoApps
 * @Location: com.fugao.infusion.util.InfusionUtils
 * @Description: TODO
 * @author: 陈亮    chenliang@fugao.com
 * @date: 2014/8/27 11:10
 * @version: V1.0
 */

public class InfoUtils {

    /**
     * 输液 巡视 专用
     * 根据人来分类 然后输液中和输液完成再分开
     */
    public static ArrayList<GroupBottleModel> toSpecialGroup(List<BottleModel> allInfusionInfoBean) {
        ArrayList<GroupBottleModel> peopleInfusionBeans = new ArrayList<GroupBottleModel>();
        HashMap<String, String> hashPid = new HashMap<String, String>();
        HashMap<String, Integer> hashStatus = new HashMap<String, Integer>();

        for (int i = 0; i < allInfusionInfoBean.size(); i++) {
            BottleModel anAllInfusionInfoBean = allInfusionInfoBean.get(i);

            GroupBottleModel peopleInfusionBean = new GroupBottleModel();
            String patID = StringUtils.getString(anAllInfusionInfoBean.PeopleInfo.PatId);
            String bottleStatus = StringUtils.getString(anAllInfusionInfoBean.BottleStatus + "");
            boolean index = hashPid.containsValue(patID);
            Integer second = hashStatus.get(patID + bottleStatus);
            peopleInfusionBean.PatId = patID;
            peopleInfusionBean.SeatNo = anAllInfusionInfoBean.SeatNo;
            peopleInfusionBean.Name = anAllInfusionInfoBean.PeopleInfo.Name;
            peopleInfusionBean.Sex = anAllInfusionInfoBean.PeopleInfo.Sex;
            peopleInfusionBean.Age = anAllInfusionInfoBean.PeopleInfo.Age;
            peopleInfusionBean.Weight = anAllInfusionInfoBean.PeopleInfo.Weight;
            peopleInfusionBean.DrugAllergy = anAllInfusionInfoBean.PeopleInfo.DrugAllergy;

            if (index) {
                if (second != null) {
                    if (BottleStatusCategory.INFUSIONG.getKey() != anAllInfusionInfoBean.BottleStatus) {

                        for (String each : hashPid.keySet()) {
                            int tem = Integer.parseInt(each.charAt(each.length() - 1) + "");
                            if (anAllInfusionInfoBean.BottleStatus == tem) {
                                for (GroupBottleModel p : peopleInfusionBeans) {
                                    if (p.items.get(0).BottleStatus == tem) {
                                        p.items.add(anAllInfusionInfoBean);
                                    }
                                }
                                break;
                            }
                        }
                    } else {
                        hashPid.put(i + bottleStatus, patID);
                        hashStatus.put(patID + bottleStatus, peopleInfusionBeans.size());
                        List<BottleModel> infusionInfoBeans = new ArrayList<BottleModel>();
                        infusionInfoBeans.add(anAllInfusionInfoBean);
                        peopleInfusionBean.items = infusionInfoBeans;
                        peopleInfusionBeans.add(peopleInfusionBean);
                    }
                } else {
                    hashPid.put(i + bottleStatus, patID);
                    hashStatus.put(patID + bottleStatus, peopleInfusionBeans.size());
                    List<BottleModel> infusionInfoBeans = new ArrayList<BottleModel>();
                    infusionInfoBeans.add(anAllInfusionInfoBean);
                    peopleInfusionBean.items = infusionInfoBeans;
                    peopleInfusionBeans.add(peopleInfusionBean);
                }
            } else {
                hashPid.put(i + bottleStatus, patID);
                hashStatus.put(patID + bottleStatus, peopleInfusionBeans.size());
                List<BottleModel> infusionInfoBeans = new ArrayList<BottleModel>();
                infusionInfoBeans.add(anAllInfusionInfoBean);
                peopleInfusionBean.items = infusionInfoBeans;
                peopleInfusionBeans.add(peopleInfusionBean);
            }
        }

        return peopleInfusionBeans;
    }

    /**
     * 按照人来分类
     *
     * @param bottles 原来以瓶贴为对象的集合
     * @return 人有瓶贴  一对多关系
     */
    public static ArrayList<GroupBottleModel> toGroup(List<BottleModel> bottles) {
        ArrayList<GroupBottleModel> onePerson2ManyBottles = new ArrayList<GroupBottleModel>();
        List<String> pids = new ArrayList<String>();
        GroupBottleModel onePerson = null;
        for (BottleModel bottle : bottles) {
            String patID = StringUtils.getString(bottle.PeopleInfo.PatId);
            if (pids.contains(patID)) {
                /**
                 * 找到那个人 给她瓶贴
                 */
                for (GroupBottleModel onePerson2ManyBottle : onePerson2ManyBottles) {

                    if (onePerson2ManyBottle.PatId.equals(patID)) {
                        onePerson2ManyBottle.items.add(bottle);
                        onePerson2ManyBottle.bottletotle++;
                        break;
                    }
                }
            } else {
                /**
                 * 创建这个人
                 */
                pids.add(patID);
                onePerson = new GroupBottleModel();
                onePerson.PatId = patID;
                onePerson.SeatNo = bottle.PeopleInfo.SeatNo;
                onePerson.Name = bottle.PeopleInfo.Name;
                onePerson.Sex = bottle.PeopleInfo.Sex;
                onePerson.Age = bottle.PeopleInfo.Age;
                onePerson.Weight = bottle.PeopleInfo.Weight;
                onePerson.DrugAllergy = bottle.PeopleInfo.DrugAllergy;
                onePerson.Status = bottle.PeopleInfo.Status;
                onePerson.Lsh = bottle.PeopleInfo.QueueNo;
                onePerson.ObserveFlag = bottle.PeopleInfo.ObserveFlag;
                onePerson.bottletotle=1;
                List<BottleModel> tempItems = new ArrayList<BottleModel>();
                tempItems.add(bottle);
                onePerson.items = tempItems;
                onePerson2ManyBottles.add(onePerson);
            }
        }

        return onePerson2ManyBottles;
    }

    /**
     * 排药获取状态1,2的药品
     * 配液获取2,3的药品
     * 穿刺获取3,4的药品
     * 输液获取3,4,5,6的药品
     *
     * @param roleId 角色
     * @return 状态组合
     */
    public static String getAllStatusGroup(int roleId) {
        if (roleId == RoleCategory.PAIYAO.getKey()) {
            return BottleStatusCategory.WAITINGHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.HADHANDLE.getKey();//"1_2"
        } else if (roleId == RoleCategory.PEIYE.getKey()) {
            return BottleStatusCategory.WAITINGHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.HADHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.WAITINGINFUSE.getKey();//"1_2_3";
        } else if (roleId == RoleCategory.CHUANCI.getKey()) {
            return BottleStatusCategory.WAITINGHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.HADHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.WAITINGINFUSE.getKey()
                    + "_"
                    + BottleStatusCategory.INFUSIONG.getKey();//"1_2_3_4";
        } else if (roleId == RoleCategory.SHUYE.getKey()) {
            return BottleStatusCategory.WAITINGHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.HADHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.WAITINGINFUSE.getKey()
                    + "_"
                    + BottleStatusCategory.INFUSIONG.getKey()
                    + "_"
                    + BottleStatusCategory.HADINFUSE.getKey()
                    + "_"
                    + BottleStatusCategory.CANCEL.getKey(); //"1_2_3_4_5_6";
        } else if (roleId == RoleCategory.ZHONGJI.getKey()) {
            return BottleStatusCategory.WAITINGHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.HADHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.WAITINGINFUSE.getKey()
                    + "_"
                    + BottleStatusCategory.INFUSIONG.getKey()
                    + "_"
                    + BottleStatusCategory.HADINFUSE.getKey()
                    + "_"
                    + BottleStatusCategory.CANCEL.getKey(); //"1_2_3_4_5_6";
        }
        return "";
    }

    /**
     * 根据角色得到改角色未完状态组合
     *
     * @param roleId 角色索引
     * @return 状态组合
     */
    public static String getUndoStatusGroup(int roleId) {
        if (roleId == RoleCategory.PAIYAO.getKey()) {
            return BottleStatusCategory.WAITINGHANDLE.getKey() + "";//1
        } else if (roleId == RoleCategory.PEIYE.getKey()) {
            return BottleStatusCategory.WAITINGHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.HADHANDLE.getKey();//"1_2"
        } else if (roleId == RoleCategory.CHUANCI.getKey()) {
            return BottleStatusCategory.WAITINGHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.HADHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.WAITINGINFUSE.getKey();//"1_2_3";
        } else if (roleId == RoleCategory.SHUYE.getKey()) {
            return BottleStatusCategory.WAITINGHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.HADHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.WAITINGINFUSE.getKey()
                    + "_" + BottleStatusCategory.INFUSIONG.getKey();//"1_2_3_4";
        } else if (roleId == RoleCategory.ZHONGJI.getKey()) {
            return BottleStatusCategory.WAITINGHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.HADHANDLE.getKey()
                    + "_"
                    + BottleStatusCategory.WAITINGINFUSE.getKey()
                    + "_" + BottleStatusCategory.INFUSIONG.getKey();//"1_2_3_4";
        }
        return "";
    }

    /**
     * 根据角色得到改角色已完状态组合
     *
     * @param roleId 角色索引
     * @return 状态组合
     */
    public static String getDoneStatusGroup(int roleId) {
        if (roleId == RoleCategory.PAIYAO.getKey()) {
            return BottleStatusCategory.HADHANDLE.getKey() + "";//"2"
        } else if (roleId == RoleCategory.PEIYE.getKey()) {
            return BottleStatusCategory.WAITINGINFUSE.getKey() + "";//"3"
        } else if (roleId == RoleCategory.CHUANCI.getKey()) {
            return BottleStatusCategory.INFUSIONG.getKey()
                    + "_"
                    +
                    BottleStatusCategory.HADINFUSE.getKey()
                    + "_"
                    + BottleStatusCategory.CANCEL.getKey();//"4_5_6"
        } else if (roleId == RoleCategory.SHUYE.getKey()) {
            return BottleStatusCategory.HADINFUSE.getKey()
                    + "_"
                    + BottleStatusCategory.CANCEL.getKey();//"5_6"
        } else if (roleId == RoleCategory.ZHONGJI.getKey()) {
            return BottleStatusCategory.HADINFUSE.getKey()
                    + "_"
                    + BottleStatusCategory.CANCEL.getKey();//"5_6"
        }
        return "";
    }

    /**
     * 获取当前角色名称
     */
    public static String getCurrentRoleName() {
        if (RoleCategory.PAIYAO.getKey() == LocalSetting.RoleIndex) {
            return "排药";
        }
        if (RoleCategory.PEIYE.getKey() == LocalSetting.RoleIndex) {
            return "配液";
        }
        if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
            return "穿刺";
        }
        if (RoleCategory.SHUYE.getKey() == LocalSetting.RoleIndex) {
            return "巡视/输液";
        }
        if (RoleCategory.ZHONGJI.getKey() == LocalSetting.RoleIndex) {
            return "重急/输液";
        }
        return "";
    }

    /**
     * 执行成功后发送广播。。。控制刷新
     */
    public static void sendSuccessBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction(Constant.RETURNSUCCESS);
        context.sendBroadcast(intent);
    }

    /**
     * 执行成功后发送广播。。。控制刷新
     */
    public static void sendSuccessBroadcastAdapter(Context context) {
        Intent intent = new Intent();
        intent.setAction(Constant.NOTIFYADAPTER);
        context.sendBroadcast(intent);
    }

    /**
     * 设置座位
     *
     * @param context
     */
    public static void sendSuccessBroadcastSetNo(Context context, String seatNo) {
        Intent intent = new Intent();
        intent.setAction(Constant.SETNO);
        intent.putExtra("No", seatNo);
        context.sendBroadcast(intent);
    }


    /**
     * 重新分配座位后发广播控制座位列表刷新
     *
     * @param context
     */
    public static void sendSuccessBroadcastRefreshSeatNo(Context context, String seatNo) {
        Intent intent = new Intent();
        intent.setAction(Constant.REFRESHSEATNO);
        intent.putExtra("No", seatNo);
        context.sendBroadcast(intent);
    }

    public static void sendSuccessBroadcastSetTime(Context context) {
        Intent intent = new Intent();
        intent.setAction(Constant.SETTIMERECIVER);
        context.sendBroadcast(intent);
    }

    /**
     * 外面设置界面如果没有设置蓝牙连接这里就先去设置蓝牙
     */
    public static void initBluetoothSetting(final Context context) {
        if (BlueToothService.getState() != BlueToothService.STATE_CONNECTED) {
            AlertDialog alertDialog = null;
            if (alertDialog == null || !alertDialog.isShowing()) {
                alertDialog = new AlertDialog.Builder(AppManager.getInstance().getTopActivity()).setTitle("提醒！")
                        .setMessage("蓝牙打印机没有连接，是否去设置")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setClass(context, ChooseBluetoothDeviceActivity.class);
                                context.startActivity(intent);
                            }
                        }).create();
                alertDialog.show();
            } else {
                alertDialog.setMessage("蓝牙打印机没有连接，是否去设置");
            }
        }
    }

    /**
     * 监测蓝牙是否存在连接
     * 如果没有最后一个打印机就跳转到蓝牙列表界面选择
     * 如果最后一个打印机存活的话并且拦截蓝牙成功返回true;否则跳转到列表选择
     */
    public static boolean checkBlueTooth(final Context context) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Toast.makeText(context, "当前没有蓝牙设备", Toast.LENGTH_SHORT).show();
        } else {
            if (!Constant.BlueToothAddress.equals(
                    XmlDB.getInstance(context).getKeyString("blutoothAddress", ""))) {

                UIHelper.showInfoDilalog(context, "蓝牙打印机没有连接，是否去设置 ",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setClass(context, ChooseBluetoothDeviceActivity.class);
                                context.startActivity(intent);
                            }
                        });
            } else {
                if (BlueToothService.getState() == BlueToothService.STATE_CONNECTED) {
                    return true;
                } else {
                    sendCmd(context, BluetoothUtils.CMD_CONNTECT_BLUETOOTH, "1", null, null);//连接设备
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 蓝牙打印机打印座位
     * <p>
     * param
     */
    public static void bluetoothPrintSeatNo(Context context, String seatNo, String peopleMessage) {
        Intent intent = new Intent();
        intent.setClass((Activity) context, BluetoothPrintService.class);
        context.startService(intent);
        int state = BlueToothService.getState();
        if (StringUtils.getString(seatNo).contains("号")) {
            seatNo = StringUtils.getString(seatNo).replace("号", "");
        }
        if (3 == state) {
            sendCmd(context, BluetoothUtils.CMD_SEND_DATA, printString(), peopleMessage, setPrintContent(seatNo));
        } else {
            sendCmd(context, BluetoothUtils.CMD_CONNTECT_BLUETOOTH, "1", null, null);
        }
    }

    /**
     * 向蓝牙打印机发送广播数据
     */
    public static void sendCmd(Context context, int command, String value, String peopleMessage,
                               ArrayList<PrintStytleModel> printStytleModels) {
        String blutoothAddress =
                XmlDB.getInstance(context).getKeyString("blutoothAddress", Constant.BlueToothAddress);
        Intent intent = new Intent();//创建Intent对象
        intent.setAction("android.intent.action.cmd");
        intent.putExtra("command", command);
        intent.putExtra("value", value);
        intent.putExtra("peopleMessage", peopleMessage);
        intent.putExtra("address", blutoothAddress);
        Bundle bundle = new Bundle();
        bundle.putSerializable("content", printStytleModels);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);//发送广播
    }

    /**
     * 蓝牙打印的内容
     * 在这里设置打印的字体、内容、以及格式
     */
    private static ArrayList<PrintStytleModel> setPrintContent(String seatNo) {
        ArrayList<PrintStytleModel> printStytleModels = new ArrayList<PrintStytleModel>();
        PrintStytleModel printStytleModel =
                new PrintStytleModel(PrintStyle.TEXT, 1, 2, 15, "\n" + seatNo + "座" + "\n");
        //PrintStytleModel printStytleModel1 = new PrintStytleModel(PrintStyle.QRCODE, 20, 60, "33" +"\n");
        printStytleModels.add(printStytleModel);
        //printStytleModels.add(printStytleModel1);
        return printStytleModels;
    }

    /**
     * 蓝牙打印内容
     */
    private static String printString() {
        StringBuffer sb = new StringBuffer();
        //排版标题
        sb.append(BluetoothPrintFormatUtil.printTitle("药品单\n")).append("日期：2013-06-25\n");
        return sb.toString();
    }

    /**
     * 手机震动效果
     */
    public static void Vibrate(final Activity activity, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

    /**
     * 根据服务器时间和系统时间间隔是否在规定时间内,判断是否需要重新设定时间
     *
     * @param interval    时间间隔，单位分钟
     * @param nowDateTime 服务器时间
     * @return true设定时间
     */
    public static boolean isSetSystemDateTime(int interval, String nowDateTime) {
        boolean flag = false;
        if (StringUtils.StringIsEmpty(StringUtils.getString(nowDateTime))) {
            return flag;
        }
        long intervalTime = interval * 60 * 1000;
        long systemDateTime = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date begin = dateFormat.parse(nowDateTime);
            long serverDateTime = begin.getTime();
            long time = serverDateTime - systemDateTime;
            if (time < 0) {
                time = 0 - time;
            }
            if (time > intervalTime) {
                flag = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 获得刷新的时间
     *
     * @return
     */
    public static String getLasetTime(Context context) {
        String lastTime = XmlDB.getInstance(context).getKeyString("lastTime", "");
        return DateUtils.getHHmmss(lastTime);
    }

    /**
     * tong  自动重连网络
     *
     * @param context
     */
    public static void showWifiConnectdDialogFragament(Context context, String error) {

        if (!StringUtils.StringIsEmpty(error)) {
            if (error.contains("网络异常") || error.contains("网络连接失败")) {
                String ssid = PullXMLTools.parseXml(context,
                        Constant.DEFAUL_CONFIG_XML, "ssid");
                String wifeTypeString = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML,
                        "wife_type");
                /**
                 * 无线网加密类型
                 */
                int wifiType = Integer.parseInt(wifeTypeString);
                DES des = new DES("fugao_moible_wife");
                String readPassword = StringUtils.getString(PullXMLTools.parseXml(context,
                        Constant.DEFAUL_CONFIG_XML, "password"));
                String password = des.decrypt(readPassword);
                ConnectWifiDialgoFragment connectWifiDialgoFragment = ConnectWifiDialgoFragment
                        .newInstance(ssid, password, wifiType);
                if (((BaseActivity) context).getFragmentManager() != null) {
                    try {
                        connectWifiDialgoFragment.show(((BaseActivity) context).getFragmentManager(), "connect");
                    } catch (Exception e) {
                        e.printStackTrace();//此处的show()如果Activity被回收会报错原因因为fragemnt.commit()不能在saveInstance()后执行
                    }
                }

            }
        }

    }

    /**
     * 核对病人信息错误提醒设置
     *
     * @param context
     * @param beepManager
     */
    public static void warningComfirmError(Context context, BeepManager beepManager) {
        /**
         * 扫描核对错误提醒设置
         */
        String userSetting = XmlDB.getInstance(context).getKeyString("userSetting", "打开震动和声音");
        String string = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "comfirmErrorSetting");
        List<String> oriModuldes = Arrays.asList(string.split(","));
        ArrayList<String> moduldes = new ArrayList<String>();
        moduldes.addAll(oriModuldes);
        InfoUtils.Vibrate((Activity) context, new long[]{100, 1500, 1000, 2000}, false);
        if (userSetting.contains(moduldes.get(0))) {
            beepManager.playBeepVibrate();
        }
        if (userSetting.contains(moduldes.get(1))) {
            beepManager.playBeepSound();
        }
        if (userSetting.contains(moduldes.get(2))) {
            InfoUtils.Vibrate((Activity) context, new long[]{100, 1500, 1000, 6000}, false);
            beepManager.playBeepSound();
        }
    }

    /**
     * 针对扫描病人信息和当前瓶贴核对不一致的时候
     * 提交错误日志到服务器
     *
     * @param context
     * @param infusionEventList
     */
    public static void recordConfirmError(final Context context, ArrayList<InfusionEventModel> infusionEventList) {
        String jsonStrig = String2InfusionModel.InfusionEventModel2infusionEventList(infusionEventList);
        RestClient.put(context, InfoApi.POST_RECORDCOMFIRMERROR, jsonStrig, new BaseAsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, String s) {

            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                UIHepler.showToast(context, "核对错误日志添加失败!");
            }
        });
    }

    /**
     * 解析BottleModel
     *
     * @param s
     * @throws JSONException
     */
    public static ArrayList<BottleModel> jsonStringTOModel(String s) throws JSONException {
        ArrayList<BottleModel> bottleModels = new ArrayList<BottleModel>();

        JSONArray jsonArray = new JSONArray(s);
        for (int i = 0; i < jsonArray.length(); i++) {
            BottleModel bottleModel = new BottleModel();
            QueueModel peopleInfo = new QueueModel();
            JSONObject singleBottleModel = jsonArray.getJSONObject(i);
            bottleModel.BottleId = singleBottleModel.getString("BottleId");
            /**
             * 药的结合
             */
            JSONArray drugDetailsArray = null;
            try {
                drugDetailsArray = singleBottleModel.getJSONArray("DrugDetails");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<DrugDetailModel> drugDetails = new ArrayList<DrugDetailModel>();
            bottleModel.DrugDetails = drugDetails;
            for (int j = 0; j < drugDetailsArray.length(); j++) {
                JSONObject drugjsonObject = drugDetailsArray.getJSONObject(j);
                DrugDetailModel drugDetailModel = new DrugDetailModel();
                drugDetailModel.DetailId = drugjsonObject.getString("DetailId");
                drugDetailModel.BottleId = drugjsonObject.getString("BottleId");
                drugDetailModel.GroupId = drugjsonObject.getString("GroupId");
                drugDetailModel.PrescriptionId = drugjsonObject.getInt("PrescriptionId");
                drugDetailModel.ItemId = drugjsonObject.getInt("ItemId");
                drugDetailModel.ItemName = drugjsonObject.getString("ItemName");
                drugDetailModel.Standard = drugjsonObject.getString("Standard");
                drugDetailModel.Amount = (float) drugjsonObject.getDouble("Amount");
                drugDetailModel.Unit = drugjsonObject.getString("Unit");
                drugDetailModel.EveryAmount = (float) drugjsonObject.getDouble("EveryAmount");
                drugDetailModel.AmountUnit = drugjsonObject.getString("AmountUnit");
                drugDetailModel.ReturnFlag = drugjsonObject.getInt("ReturnFlag");
                drugDetailModel.Remark = drugjsonObject.getString("Remark");
                drugDetails.add(drugDetailModel);
            }

            bottleModel.SeatNo = singleBottleModel.getString("SeatNo");
            bottleModel.GroupId = singleBottleModel.getString("GroupId");
            bottleModel.InfusionId = singleBottleModel.getString("InfusionId");
            bottleModel.PrescriptionId = singleBottleModel.getInt("PrescriptionId");
            bottleModel.DoctorId = singleBottleModel.getString("DoctorId");
            bottleModel.DoctorName = singleBottleModel.getString("DoctorName");
            bottleModel.DoctorCore = singleBottleModel.getString("DoctorCore");
            bottleModel.DiagnoseCore = singleBottleModel.getString("DiagnoseCore");
            bottleModel.DiagnoseName = singleBottleModel.getString("DiagnoseName");
            bottleModel.PrescribeDate = singleBottleModel.getString("PrescribeDate");
            bottleModel.PrescribeTime = singleBottleModel.getString("PrescribeTime");
            bottleModel.BottleStatus = singleBottleModel.getInt("BottleStatus");
            bottleModel.Way = singleBottleModel.getString("Way");
            bottleModel.Frequency = singleBottleModel.getString("Frequency");
            bottleModel.TransfusionBulk = singleBottleModel.getInt("TransfusionBulk");
            bottleModel.TransfusionSpeed = singleBottleModel.getString("TransfusionSpeed");
            bottleModel.ExpectTime = singleBottleModel.getInt("ExpectTime");
            bottleModel.SubscribeDate = singleBottleModel.getString("SubscribeDate");
            bottleModel.SubscribeTime = singleBottleModel.getString("SubscribeTime");
            bottleModel.RegistrationDate = singleBottleModel.getString("RegistrationDate");
            bottleModel.RegistrationTime = singleBottleModel.getString("RegistrationTime");
            bottleModel.RegistrationId = singleBottleModel.getInt("RegistrationId");
            bottleModel.RegistrationCore = singleBottleModel.getString("RegistrationCore");
            bottleModel.PillDate = singleBottleModel.getString("PillDate");
            bottleModel.PillTime = singleBottleModel.getString("PillTime");
            bottleModel.PillId = singleBottleModel.getInt("PillId");
            bottleModel.PillName = singleBottleModel.getString("PillName");
            bottleModel.PillCore = singleBottleModel.getString("PillCore");
            bottleModel.LiquorDate = singleBottleModel.getString("LiquorDate");
            bottleModel.LiquorTime = singleBottleModel.getString("LiquorTime");
            bottleModel.LiquorId = singleBottleModel.getInt("LiquorId");
            bottleModel.LiquorCore = singleBottleModel.getString("LiquorCore");
            bottleModel.LiquorName = singleBottleModel.getString("LiquorName");
            bottleModel.InfusionDate = singleBottleModel.getString("InfusionDate");
            bottleModel.InfusionTime = singleBottleModel.getString("InfusionTime");
            bottleModel.InfusionPeopleId = singleBottleModel.getInt("InfusionPeopleId");
            bottleModel.InfusionCore = singleBottleModel.getString("InfusionCore");
            bottleModel.EndDate = singleBottleModel.getString("EndDate");
            bottleModel.EndTime = singleBottleModel.getString("EndTime");
            bottleModel.EndId = singleBottleModel.getInt("EndId");
            bottleModel.EndCore = singleBottleModel.getString("EndCore");
            bottleModel.EndName = singleBottleModel.getString("EndName");
            bottleModel.Remark = singleBottleModel.getString("Remark");
            bottleModel.SpeedUnit = singleBottleModel.getString("SpeedUnit");
            bottleModel.LZZ = singleBottleModel.getString("LZZ");
            bottleModel.GCF = singleBottleModel.getString("GCF");
            bottleModel.SpeedUnit = singleBottleModel.getString("SpeedUnit");
            bottleModel.SpeedUnit = singleBottleModel.getString("SpeedUnit");
            bottleModel.SpeedUnit = singleBottleModel.getString("SpeedUnit");

            /**
             * 人的集合
             */
            JSONObject peopleInfoObject = null;
            try {
                peopleInfoObject = singleBottleModel.getJSONObject("PeopleInfo");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            peopleInfo.InfusionId = peopleInfoObject.getString("InfusionId");
            peopleInfo.Date = peopleInfoObject.getString("Date");
            peopleInfo.DepartmentId = peopleInfoObject.getString("DepartmentId");
            peopleInfo.PatId = peopleInfoObject.getString("PatId");
            peopleInfo.Status = peopleInfoObject.getInt("Status");
            peopleInfo.Name = peopleInfoObject.getString("Name");
            peopleInfo.CardId = peopleInfoObject.getInt("CardId");
            peopleInfo.QueueNo = peopleInfoObject.getString("QueueNo");
            peopleInfo.SeatNo = peopleInfoObject.getString("SeatNo");
            peopleInfo.Age = peopleInfoObject.getString("Age");
            peopleInfo.Sex = peopleInfoObject.getInt("Sex");
            peopleInfo.InsertTime = peopleInfoObject.getString("InsertTime");
            peopleInfo.Ticket = peopleInfoObject.getString("Ticket");
            peopleInfo.Detective = peopleInfoObject.getString("Detective");
            peopleInfo.callCount = peopleInfoObject.getInt("callCount");
            peopleInfo.ObserveFlag = peopleInfoObject.getInt("ObserveFlag");
            peopleInfo.Weight = peopleInfoObject.getString("Weight");
            peopleInfo.DrugAllergy = peopleInfoObject.getString("DrugAllergy");
            peopleInfo.OverCall = peopleInfoObject.getInt("OverCall");
            bottleModel.PeopleInfo = peopleInfo;

            bottleModel.InfusionNo = singleBottleModel.getInt("InfusionNo");
            bottleModel.InvoicingId = singleBottleModel.getInt("InvoicingId");

            /**
             * 巡视集合
             */
            JSONArray aboutPatrolsArray = null;
            try {
                aboutPatrolsArray = singleBottleModel.getJSONArray("AboutPatrols");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<PatrolModel> AboutPatrols = new ArrayList<PatrolModel>();
            if (aboutPatrolsArray != null && aboutPatrolsArray.length() > 0) {
                for (int k = 0; k < aboutPatrolsArray.length(); k++) {
                    JSONObject aboutPatrolsObject = drugDetailsArray.getJSONObject(k);
                    PatrolModel patrolModel = new PatrolModel();
                    patrolModel.Id = aboutPatrolsObject.getString("Id");
                    patrolModel.BottleId = aboutPatrolsObject.getString("BottleId");
                    patrolModel.PatrolerNo = aboutPatrolsObject.getString("PatrolerNo");
                    patrolModel.PatrolerName = aboutPatrolsObject.getString("PatrolerName");
                    patrolModel.PatrolTime = aboutPatrolsObject.getString("PatrolTime");
                    patrolModel.Content = aboutPatrolsObject.getString("Content");
                    patrolModel.TargetContent = aboutPatrolsObject.getString("TargetContent");
                    patrolModel.Expand = aboutPatrolsObject.getString("Expand");
                    patrolModel.Status = aboutPatrolsObject.getInt("Status");
                    patrolModel.AboutNo = aboutPatrolsObject.getString("AboutNo");
                    patrolModel.OperationType = aboutPatrolsObject.getInt("OperationType");
                    patrolModel.Type = aboutPatrolsObject.getInt("Type");
                    patrolModel.DrippingSpeed = aboutPatrolsObject.getString("DrippingSpeed");
                    patrolModel.DrippingSpeedUnit = aboutPatrolsObject.getString("DrippingSpeedUnit");
                    AboutPatrols.add(patrolModel);
                }
                bottleModel.AboutPatrols = AboutPatrols;
            } else {
                bottleModel.AboutPatrols = null;
            }
            bottleModels.add(bottleModel);
        }

        return bottleModels;

    }

    /**
     * 设置PDA的系统时间保持与服务器同步
     *
     * @param activity
     * @param serverTime 2015-07-10 14:03:53
     */
    public static boolean setPDATimeSyncServer(Activity activity, String serverTime) {
        boolean flag = false;
        if (!StringUtils.StringIsEmpty(serverTime)) {
            try {
                String format = DateUtils.changeDateFormat(serverTime, "yyyy-MM-dd HH:mm:ss", "yyyy:MM:dd:HH:mm:ss");
                String[] split = format.split(":");
                int year = Integer.parseInt(split[0]);
                int month = Integer.parseInt(split[1]);
                int day = Integer.parseInt(split[2]);
                int hour = Integer.parseInt(split[3]);
                int minute = Integer.parseInt(split[4]);
                DeviceSettings deviceSettings = new DeviceSettings(activity);
                deviceSettings.setDate(year, month, day);
                Thread.sleep(500);
                deviceSettings.setTime(hour, minute);
                Thread.sleep(500);
                deviceSettings.set24Hour(true);
//                //set data
//                Intent setdata = new Intent("com.honeywell.intent.action.devicesettings");
//                setdata.putExtra("type", "setDate");
//                setdata.putExtra("year", year);
//                setdata.putExtra("month", month);
//                setdata.putExtra("day", day);
//                activity.sendBroadcast(setdata);
//
//                // set time
//                Intent settime = new Intent("com.honeywell.intent.action.devicesettings");
//                settime.putExtra("type", "setTime");
//                settime.putExtra("hourOfDay", hour);
//                settime.putExtra("minute", minute);
//                activity.sendBroadcast(settime);
//
//                //set 24
//                Intent set24hour = new Intent("com.honeywell.intent.action.devicesettings");
//                set24hour.putExtra("type", "set24Hour");
//                set24hour.putExtra("is24Hour", true);
//                activity.sendBroadcast(set24hour);
                flag = true;
                Toast.makeText(activity, "同步的时间为" + year + "-" + month + "-" + day + "-" + hour + "-" + minute, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(activity, "服务器日期格式回传错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "服务器返回时间为空", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

}
