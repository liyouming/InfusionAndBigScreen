package com.fugao.infusion;

import android.content.Intent;
import android.os.Handler;

import com.fugao.infusion.base.BaseTempleActivity;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoConfig;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.utils.PullXMLTools;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.AppUtils;
import com.jasonchen.base.utils.RestClient;

public class LoadingActivity extends BaseTempleActivity {
    public String currentHostName = "";

    public String hostNameKey = "infusion_host_name";

    public String ip = "ip";

    public String port = "port";
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_loading);
    }

    @Override
    public void initView() {
        inintSettings();
    }

    @Override
    public void initData() {

        int currentVersion = AppUtils.getCurrentVersion(LoadingActivity.this);
        int lastVesrion = XmlDB.getInstance(LoadingActivity.this).getKeyIntValue(Constant.LastVersionKey, 0);

            XmlDB.getInstance(LoadingActivity.this).saveKey(Constant.LastVersionKey, currentVersion);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setClass(LoadingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    LoadingActivity.this.finish();
                }
            }, 1000);

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {

    }


    /**
     * 程序第一次进入的ip和端口号等
     */
    private void inintSettings() {
        Constant.DEFAUL_CONFIG_XML = R.xml.xh_config;
        boolean firstCome = XmlDB.getInstance(this).getKeyBooleanValue("firstCome", true);
        if (firstCome) {
            String mmip = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "ip");
            String mmport = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "port");
            XmlDB.getInstance(context).saveKey("ip", mmip);
            XmlDB.getInstance(context).saveKey("port", mmport);
            XmlDB.getInstance(context).saveKey("firstCome", false);
            currentHostName = mmip + ":" + mmport;
            String canPeiye = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "canPeiye");
            XmlDB.getInstance(context).saveKey("canPeiye", "1".equals(canPeiye));
        } else {
            String ip = XmlDB.getInstance(this).getKeyString("ip", "");
            String port = XmlDB.getInstance(this).getKeyString("port", "");
            currentHostName = ip + ":" +port;
        }
        RestClient.BASE_URL = "http://" + currentHostName + "/";
        RestClient.client.setTimeout(InfoConfig.TIME_OUT) ;

        /**
         * 排药是否启用批量扫描
         */
        String batchScan_paiyao =
                PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "batchScan_paiyao");
        LocalSetting.BatchScanPaiyao = "1".equals(batchScan_paiyao);

        /**
         * 配液是否启用批量扫描
         */
        String batchScan_peiye =
                PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "batchScan_peiye");
        LocalSetting.BatchScanPeiye = "1".equals(batchScan_peiye);
        /**
         * 是否自动结束上一瓶
         */
        String endOthers = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "autoEndOthers");
        LocalSetting.AutoEndOthers = "1".equals(endOthers);

        /**
         * 输液低速是否修改
         */
        String ifCanModifytheSpeed =
                PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "canModifytheSpeed");
        LocalSetting.CanModifytheSpeed = "1".equals(ifCanModifytheSpeed);

        /**
         * 输液低速是否修改
         */
        String callMaxCount =
                PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "callMaxCount");
        try {
            LocalSetting.CallMaxCount = Integer.parseInt(callMaxCount);
        } catch (Exception e) {
        }

        /**
         * 是否自动分配座位
         */
        String autoAllotSeat =
                PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "autoAllotSeat");
        LocalSetting.AutoAllotSeat = "1".equals(autoAllotSeat);
    }
}
