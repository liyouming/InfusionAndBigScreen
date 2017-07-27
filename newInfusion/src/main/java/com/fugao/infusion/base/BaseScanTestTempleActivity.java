package com.fugao.infusion.base;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.utils.BeepManager;
import com.fugao.infusion.utils.QRCodeParse;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.view.SoundManager;
import com.honeywell.decodemanager.DecodeManager;
import com.honeywell.decodemanager.barcode.CommonDefine;
import com.honeywell.decodemanager.barcode.DecodeResult;
import com.jasonchen.base.utils.AppManager;
import com.jasonchen.base.utils.PullXMLTools;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;
import java.io.IOException;
import java.util.HashMap;

import butterknife.ButterKnife;

/**
 * activity 模版类
 * 增加扫描公共类
 *
 * @author findchen_2013 2013年11月5日下午2:39:26
 */
public abstract class BaseScanTestTempleActivity extends FragmentActivity {
    public ActionBar actionBar;

    public DisplayMetrics displayMetrics = new DisplayMetrics();

    public int windowWidth;

    public Context context;
    /**
     * 病人门诊号 patientId
     */
    public String patientCode;

    /**
     * 瓶贴号 bottleId
     */
    public String adviceInfo;

    private ReceivePDAScan receivePDAScan;

    private IntentFilter filter;
    public String ssid;
    public String password;
    public int wifiType;
    private ProgressDialog loadingDialog;

    /**
     * 扫描相关
     */
    private final int SCANKEY = 0x94;
    private final int SCANTIMEOUT = 2000;
    public DecodeManager mDecodeManager;
    private boolean mbKeyDown = true;
    private boolean sCanFlag = false;  //扫描开关
//    private Handler scanResultHandler;
    private boolean isUserSysTem =false;
    private BeepManager beepManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        windowWidth = displayMetrics.widthPixels;
        setContentView();
        ButterKnife.inject(this);
        context = this;
        actionBar = getActionBar();
        loadingDialog = new ProgressDialog(context);
        beepManager = new BeepManager(BaseScanTestTempleActivity.this);
        isUserSysTem = XmlDB.getInstance(BaseScanTestTempleActivity.this).getKeyBooleanValue("isUserSystemScan",false);
        initIntent();
        initView();
        initData();
        initListener();
        initScanBroadCast();
        AppManager.getInstance().addActivity(this);
        /**
         * 是否开启集成扫描
         */
        String startLoadDateString= PullXMLTools.parseXml(this,
                Constant.DEFAUL_CONFIG_XML, "integration_scan").toString();
        sCanFlag = Boolean.parseBoolean(startLoadDateString);
//        if(sCanFlag && isUserSysTem ==false){
//            initHandler();
//        }
    }


    public abstract void setContentView();

    public abstract void initView();

    public abstract void initData();

    public abstract void initListener();

    public abstract void initIntent();


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initScanBroadCast() {
        receivePDAScan = new ReceivePDAScan();
        filter = new IntentFilter();
//        filter.addAction(Constant.RECEIVER_SCAN_RESULT_S200);
//        filter.addAction(Constant.RECEIVER_SCAN_RESULT_MC);
//        filter.addAction(Constant.RECEIVER_SCAN_RESULT_EMH);
//        filter.addAction(Constant.QCOM_PEIYE);
        filter.addAction(Constant.LACH_SIS);
        filter.addAction(Constant.RECEIVER_SCAN_RESULT_HONEYWELL);
        filter.addAction(Constant.getRECEIVER_SCAN_RESULT_ECHART);
        filter.addAction(Constant.SLKJU);
        filter.addAction(Constant.YMH);
        filter.addAction(Constant.ACTION_SOFTSCANTRIGGER);
        filter.addAction(Constant.M_BROADCASTNAME);
        registerReceiver(receivePDAScan, filter);
    }

    public class ReceivePDAScan extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String receiver_action = intent.getAction();
            String result = "";
            if (receiver_action.equals(Constant.RECEIVER_SCAN_RESULT)) {
                result = intent.getStringExtra("result");
            } else if (receiver_action.equals(Constant.RECEIVER_SCAN_RESULT_EMH)) {
                result = intent.getStringExtra("value");
            } else if (receiver_action.equals(Constant.RECEIVER_SCAN_RESULT_MC)) {
                result = intent.getStringExtra("borcode_value");
            } else if (receiver_action.equals(Constant.RECEIVER_SCAN_RESULT_S200)) {
                result = intent.getStringExtra("value");
            } else if (receiver_action.equals(Constant.QCOM_MX50)) {
                result = intent.getStringExtra("scannerdata");
            } else if (receiver_action.equals(Constant.LACH_SIS)) {
                result = intent.getStringExtra("lachesis_barcode_value_notice_broadcast_data_string");
            } else if (receiver_action.equals(Constant.RECEIVER_SCAN_RESULT_HONEYWELL)) {
                result = intent.getStringExtra("barcode_data");
            } else if (receiver_action.equals(Constant.getRECEIVER_SCAN_RESULT_ECHART)) {
                result = intent.getStringExtra("serial");
                beepManager.playBeep();//中标软件需要自己集成扫描声音
            }else if (receiver_action.equals(Constant.SLKJU)) {
                result = intent.getStringExtra("BAR_VALUE");
            }else if (receiver_action.equals(Constant.YMH)) {
                result = intent.getStringExtra("value");
            }else if (receiver_action.equals(Constant.ACTION_SOFTSCANTRIGGER)) {
                result = intent.getStringExtra("com.motorolasolutions.emdk.datawedge.data_string");
            }else if (receiver_action.equals(Constant.M_BROADCASTNAME)) {
                result = intent.getStringExtra("BARCODE");
            }
            executeResult(context, intent, result);
        }
    }

    private void executeResult(Context context, Intent intent, String result) {
        result = StringUtils.getStringContainSpecialFlag(result);
        String prefix = "fgv1|";
        try {
            if (result.contains(prefix)) {
                result = result.replace(prefix, "");
                HashMap<String, String> qrcodeHashMap = QRCodeParse.getCodeTypeAndValue(result);
                if (qrcodeHashMap.get(Constant.QRCODE_PATIENT) != null) {
                    this.patientCode = qrcodeHashMap.get(Constant.QRCODE_PATIENT);
                    receiverPatientId(this.patientCode);
                } else if (qrcodeHashMap.get(Constant.QRCODE_SY_ADVICE) != null) {
                    this.adviceInfo = qrcodeHashMap.get(Constant.QRCODE_SY_ADVICE);
                    receiverBottleId(this.adviceInfo.split("_")[0], this.adviceInfo.split("_")[1]);
                } else {
                    UIHepler.showToast(context, "系统未识别的条形码！");
                }
            } else {
                if (StringUtils.isNumeric(result) && result.length() >= 12) {
                    UIHepler.showToast(context, "这是发票信息！");
                } else {
                    UIHepler.showToast(context, "系统未识别的条形码！");
                }
            }
        } catch (Exception ex) {
            UIHepler.showDilalog(BaseScanTestTempleActivity.this, "条码格式不正确，解析出错");
        }
    }

    /**
     * 扫描腕带操作
     *
     * @param patientId 返回门诊号
     */
    protected abstract void receiverPatientId(String patientId);

    /**
     * 扫描瓶贴操作
     *
     * @param patientId 门诊号
     * @param bottleId  返回瓶贴号
     */
    protected abstract void receiverBottleId(String patientId, String bottleId);

    /**
     * 显示加载进度对话框
     */
    public void showLoadingDialog(String message) {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(context);
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingDialog.setIndeterminate(false);
            loadingDialog.setCancelable(true);
            loadingDialog.setCanceledOnTouchOutside(false);
        }
        loadingDialog.setMessage(message);
        loadingDialog.show();
    }

    /**
     * 关闭对话框
     */
    public void dismissLoadingDialog() {
        if(!this.isFinishing() && loadingDialog !=null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    public void showWifiConnectdDialogFragament() {

        //        Constant.DEFAUL_CONFIG_XML = R.xml.hzx_06_config;
        //        ssid = PullXMLTools.parseXml(context,
        //
        //                Constant.DEFAUL_CONFIG_XML, "ssid");
        //        String wifeTypeString = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML,
        //                "wife_type");
        //        /**
        //         * 无线网加密类型
        //         */
        //        wifiType = Integer.parseInt(wifeTypeString);
        //
        //        DES des = new DES("fugao_moible_wife");
        //        String readPassword = StringUtils.getString(PullXMLTools.parseXml(context,
        //                Constant.DEFAUL_CONFIG_XML, "password"));
        //        password = des.decrypt(readPassword);
        //        ConnectWifiDialgoFragment connectWifiDialgoFragment = ConnectWifiDialgoFragment
        //                .newInstance(ssid, password, wifiType);
        //        connectWifiDialgoFragment.show(getFragmentManager(), "connect");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        SoundManager.cleanup();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receivePDAScan);
        if (mDecodeManager != null) {
            try {
                mDecodeManager.release();
                mDecodeManager = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receivePDAScan, filter);
        if (sCanFlag && isUserSysTem == false) {
            if (mDecodeManager == null) {
                mDecodeManager = new DecodeManager(this, scanResultHandler);
            }
            SoundManager.getInstance();
            SoundManager.initSounds(BaseScanTestTempleActivity.this);
            SoundManager.loadSounds();
        }
    }
   /* */

    private void DoScan() throws Exception {
        try {
            mDecodeManager.doDecode(SCANTIMEOUT);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(!sCanFlag && isUserSysTem ==true){
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                try {
                    if (mbKeyDown) {
                        DoScan();
                        mbKeyDown = false;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return true;
//            case KeyEvent.KEYCODE_BACK:
//                this.finish();
//                return true;
            case KeyEvent.KEYCODE_UNKNOWN:
                if(event.getScanCode() == SCANKEY || event.getScanCode() == 87 || event.getScanCode() == 88) {
                    try {
                        if (mbKeyDown) {
                            DoScan();
                            mbKeyDown = false;
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(!sCanFlag && isUserSysTem ==true){
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                try {
                    mbKeyDown = true;
                    cancleScan();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return true;
//            case KeyEvent.KEYCODE_BACK:
//                this.finish();
//                return true;

            case KeyEvent.KEYCODE_UNKNOWN:
                if(event.getScanCode() == SCANKEY || event.getScanCode() == 87 || event.getScanCode() == 88) {
                    try {
                        mbKeyDown = true;
                        cancleScan();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

        public Handler scanResultHandler = new Handler() {
            public void handleMessage(Message msg) {
                if(!sCanFlag && isUserSysTem ==true){
                    return ;
                }
                switch (msg.what) {
                    case DecodeManager.MESSAGE_DECODER_COMPLETE:
                        DecodeResult decodeResult = (DecodeResult) msg.obj;
                        SoundManager.playSound(1, 1);
                        executeResult(BaseScanTestTempleActivity.this, null, decodeResult.barcodeData);
                        break;
                    case DecodeManager.MESSAGE_DECODER_FAIL:
                        SoundManager.playSound(2, 1);
                        break;
                    case DecodeManager.MESSAGE_DECODER_READY:
                    {
                        try {
                            mDecodeManager.disableSymbology(CommonDefine.SymbologyID.SYM_ALL);
                            mDecodeManager.enableSymbology(17);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;
                    default:
                        super.handleMessage(msg);
                        break;
                }
            }
        };

    private void cancleScan(){
        try {
            mDecodeManager.cancelDecode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**
     * 通过Class跳转界面
     */
    public void openActivity(Class<?> cls) {
        openActivity(cls, null);
    }

    /**
     * 含有Bundle通过Class跳转界面
     */
    public void openActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
//    /**
//     * 跳转动画,从左进如
//     */
//    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}