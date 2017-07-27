package com.fugao.infusion;

import android.app.Application;
import android.content.Context;

import com.fugao.infusion.model.BottleModel;
import com.fugao.infusion.model.InfusionAreaBean;
import com.fugao.infusion.model.NurseAccountModel;
import com.fugao.infusion.model.QueueModel;
import com.fugao.infusion.model.UserDetailTimelineModel;
import com.fugao.infusion.utils.CrashHandler;
import com.jasonchen.base.utils.DateUtils;
import com.jasonchen.base.utils.FileHelper;
import com.jasonchen.base.utils.Log;
import com.jasonchen.base.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: InfusionApps
 * @Location: com.fugao.infusion.InfusionApplication
 * @Description: TODO
 * @author: 陈亮    chenliang@fugao.com
 * @date: 2014/12/20 13:58
 * @version: V1.0
 */

public class InfusionApplication extends Application {

    /**
     * 上次同步时间
     */
    public String updateTime = "";

    public ArrayList<String> alarmRequestList = new ArrayList<String>();

    /**
     * 当前用户登录信息
     */
    private NurseAccountModel currentAccount = new NurseAccountModel();

    /**
     * 当前瓶签的唯一号
     */
    public String bottleId = "";

    /**
     * 当前病人唯一号
     */
    public String pid = "";

    /**
     * 当前病区编号
     */
    public String currentDivisionCode = "";

    /**
     * 当前日期格式 20130829
     */
    public String currentDate;

    /**
     * 当前时间 包含
     */
    public String executeTime;

    private String currentStatues = "";

    /**
     * 当前登录者的操作ID;
     */
    private String currentAccountId;

    /**
     * 输液当前的项目sdcard 路径
     */
    public String InfusionPath = "";

    /**
     * 上一次扫描的 病人住院号，为了同时执行一个人的多条医嘱时少扫几次腕带
     */
    private String currentScanPatId = "";

    private Context sContext;
    public BottleModel receiveInfo;
    public QueueModel currentQueue = new QueueModel();
    private BottleModel currentInfo;
    private List<UserDetailTimelineModel> timeTwoStatusModels;
    private List<InfusionAreaBean> infusionAreaBeans;
    public NurseAccountModel currentNurseAccountBean = new NurseAccountModel();

    @Override
    public void onCreate() {
        super.onCreate();
        onInit();
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
    }
    public void onInit() {
        sContext = getApplicationContext();
        if (!BuildConfig.DEBUG) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
        }
        init();
        executeTime = DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss");
        this.currentDate = DateUtils.getCurrentDate("yyyyMMdd");
    }
    /**
     * 初始化当前系统的参数 1.系统的sd card 的根目录 2.当前目录下面的文件目录
     *
     * @Title: init
     * @Description: TODO
     * @return: void
     */
    public void init() {
        FileHelper.setAppSDPath("infusion");
        FileHelper.appSDPath="/mnt/internal_sd/infusion";
        InfusionPath=FileHelper.appSDPath;
        FileHelper.creatSDDir("logs");
        Log.setPath(InfusionPath + "/logs/"
                + DateUtils.getCurrentDate("yyyy-MM-dd-HH-mm-ss") + ".txt");
        Log.setTag("infusion");
        Log.setEnabled(false);
        Log.setPolicy(Log.LOG_ERROR_TO_FILE);
    }


    /**
     * 获取系统上下文
     */
    public  Context getContext() {
        return sContext;
    }

    public String getCurrentDate() {
        return this.currentDate;
    }

    public void setCurrentAccount(NurseAccountModel currentAccount) {
        this.currentAccount = currentAccount;
    }

    public NurseAccountModel getCurrentAccount() {
        currentAccount.DeptName = "输液室";
        return currentAccount;
    }

    public String getCurrentDivisionCode() {
        return currentDivisionCode;
    }

    public void setCurrentDivisionCode(String currentDivisionCode) {
        this.currentDivisionCode = currentDivisionCode;
    }

    public String getCurrentStatues() {
        return currentStatues;
    }

    public void setCurrentStatues(String currentStatues) {
        this.currentStatues = currentStatues;
    }

    public String getCurrentAccountId() {
        return currentAccountId;
    }

    public void setCurrentAccountId(String currentAccountId) {
        this.currentAccountId = currentAccountId;
    }

    public String getCurrentScanPatId() {
        return currentScanPatId;
    }

    public void setCurrentScanPatId(String currentScanPatId) {
        this.currentScanPatId = currentScanPatId;
    }

    public void setCurrentInfo(BottleModel info) {
        this.currentInfo = info;
    }

    public BottleModel getCurrentInfo() {
        return this.currentInfo;
    }

    /**
     * 拿到时间轴第二状态的get  set
     * @param timeTwoStatusModels
     */

    public void setTimeTwoStatusModel(List<UserDetailTimelineModel> timeTwoStatusModels){
        this.timeTwoStatusModels = timeTwoStatusModels;
    }

    public List<UserDetailTimelineModel> getTimeTwoStatusModel() {
        return this.timeTwoStatusModels;
    }

    /**
     * 登陆的时候拿到区域，不然哟后穿刺区域分座位和输液/巡视呼叫都要重新请求执行两个url
     * @param infusionAreaBeans
     */
    public void setInfusionAreaBeanList(List<InfusionAreaBean> infusionAreaBeans){
        this.infusionAreaBeans = infusionAreaBeans;
    }

    public List<InfusionAreaBean> getInfusionAreaBeanList() {
        return this.infusionAreaBeans;
    }

    public void setNurseAccount(NurseAccountModel nurseAccountBean) {
        this.currentNurseAccountBean = nurseAccountBean;
        this.currentAccountId = StringUtils.getString(nurseAccountBean.UserName);
    }

}