package com.fugao.infusion.bluetooth.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.fugao.infusion.bluetooth.bluetooth.BluePrintUtils;
import com.fugao.infusion.bluetooth.bluetooth.BluetoothUtils;
import com.fugao.infusion.bluetooth.bluetooth.BuletoothToastUtils;
import com.fugao.infusion.bluetooth.bluetooth.PrintStyle;
import com.fugao.infusion.bluetooth.bluetooth.PrintStytleModel;
import com.fugao.infusion.bluetooth.print.BarcodeCreater;
import com.fugao.infusion.utils.DateUtils;

import java.util.ArrayList;

/**
 * 蓝牙打印service
 */
public class BluetoothPrintService extends Service {
    private BlueToothService mBTService = null;
    private String tag = "MainActivity";
    private static final int REQUEST_EX = 1;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    private Handler mhandler;
    CommandReceiver cmdReceiver;//继承自BroadcastReceiver对象，用于得到Activity发送过来的命令
    Intent intentStr;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mhandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:// 蓝牙连接状态
                        switch (msg.arg1) {
                            case BlueToothService.STATE_CONNECTED:// 已经连接
                                BuletoothToastUtils.makeText(BluetoothPrintService.this, "已经连接蓝牙", 2000).show();
                                break;
                            case BlueToothService.STATE_CONNECTING:// 正在连接
                                break;
                            case BlueToothService.STATE_LISTEN:
                            case BlueToothService.STATE_NONE:
                                break;
                            case BlueToothService.SUCCESS_CONNECT:
                                BuletoothToastUtils.makeText(BluetoothPrintService.this, "连接蓝牙成功", 2000).show();
//                                ToFinishCurrentActivity();
                                break;
                            case BlueToothService.FAILED_CONNECT:
                                BuletoothToastUtils.makeText(BluetoothPrintService.this, "连接蓝牙失败", 2000).show();
                                break;
                            case BlueToothService.LOSE_CONNECT:
                                switch (msg.arg2) {
                                    case -1:
                                        BuletoothToastUtils.makeText(BluetoothPrintService.this, "蓝牙连接丢失", 2000).show();
                                        break;
                                    case 0:
                                        break;
                                }
                        }
                        break;
                    case MESSAGE_READ:
                        // sendFlag = false;//缓冲区已满
                        break;
                    case MESSAGE_WRITE:// 缓冲区未满
                        // sendFlag = true;
                        break;
                }
            }
        };
        mBTService = new BlueToothService(this, mhandler);// 创建对象的时候必须有一个是Handler类型

        cmdReceiver = new CommandReceiver();
        IntentFilter filter = new IntentFilter();//创建IntentFilter对象
        //注册一个广播，用于接收Activity传送过来的命令，控制Service的行为，如：发送数据，停止服务等
        filter.addAction("android.intent.action.cmd");
        //注册Broadcast Receiver
        registerReceiver(cmdReceiver, filter);
    }

    /**
     * 发送广播当蓝牙连接成功是自动销毁当前蓝牙列表界面
     */
    private void ToFinishCurrentActivity() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.finish");
        this.sendBroadcast(intent);
    }

    //前台Activity调用startService时，该方法自动执行
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(cmdReceiver);//取消注册的CommandReceiver

    }

    //接收Activity传送过来的命令
    private class CommandReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.cmd")) {
                int cmd = intent.getIntExtra("command", -1);//获取Extra信息
                switch (cmd) {
                    case BluetoothUtils.CMD_START_SERVCE:

                        break;
                    case BluetoothUtils.CMD_STOP_SERVICE:
                        //stopService();
                        break;
                    case BluetoothUtils.CMD_SEND_DATA:
                        if (mBTService.getState() == mBTService.STATE_CONNECTED) {
//                            BuletoothToastUtils.makeText(BluetoothPrintService.this, "打印数据", 2000).show();
                            String value = intent.getStringExtra("value");
                            String peopleMessage = intent.getStringExtra("peopleMessage");
                            ArrayList<PrintStytleModel> printStytleModels = (ArrayList<PrintStytleModel>) intent.getSerializableExtra("content");
                            for (PrintStytleModel printStytleModel : printStytleModels) {
                                switch (printStytleModel.type) {
                                    case PrintStyle.TEXT:
                                        String[] peopleInfo= peopleMessage.split(":");
                                        if(peopleInfo.length ==2){
                                            mBTService.SendOrder(BluePrintUtils.getQSOrderSize(1));
                                            mBTService.SendOrder(BluePrintUtils.getQSOrderPaddingRow(8));
                                            mBTService.SendOrder(BluePrintUtils.getQSOrderCharacterSize(17));
                                            mBTService.SendOrder(BluePrintUtils.getQSOrderPaddingLeft(1));
                                            mBTService.SendOrder(BluePrintUtils.allowBoldSize(0));
                                            mBTService.PrintCharacters("姓  名："+"  "+peopleInfo[0]);

                                            mBTService.SendOrder(BluePrintUtils.useNewline());
                                            mBTService.SendOrder(BluePrintUtils.getQSOrderPaddingRow(8));
                                            mBTService.SendOrder(BluePrintUtils.getQSOrderSize(1));
                                            mBTService.SendOrder(BluePrintUtils.getQSOrderCharacterSize(17));
                                            mBTService.SendOrder(BluePrintUtils.getQSOrderPaddingLeft(1));
                                            mBTService.SendOrder(BluePrintUtils.allowBoldSize(0));
                                            mBTService.PrintCharacters("门诊号："+"  "+peopleInfo[1]);
                                        }

//                                        mBTService.SendOrder(BluePrintUtils.useNewline());
//                                        mBTService.SendOrder(BluePrintUtils.getQSOrderSize(1));
//                                        mBTService.SendOrder(BluePrintUtils.getQSOrderCharacterSize(17));
//                                        mBTService.SendOrder(BluePrintUtils.getQSOrderPaddingLeft(1));
//                                        mBTService.SendOrder(BluePrintUtils.allowBoldSize(0));
//                                        mBTService.PrintCharacters("您的座位号是:");

                                        mBTService.SendOrder(BluePrintUtils.useNewline());
                                        mBTService.SendOrder(BluePrintUtils.getQSOrderPaddingRow(25));
                                        mBTService.SendOrder(BluePrintUtils.getQSOrderSize(0));
                                        mBTService.SendOrder(BluePrintUtils.getQSOrderCharacterSize(17));
                                        mBTService.SendOrder(BluePrintUtils.getQSOrderPaddingLeft(1));
                                        mBTService.SendOrder(BluePrintUtils.getQSOrderPositionWay(0));
                                        mBTService.SendOrder(BluePrintUtils.allowBoldSize(0));
                                        String[] print= printStytleModel.content.split(":");
                                        if(print[0].contains("输液区")){
                                            mBTService.PrintCharacters(print[0].replace("输液","").trim()+":"+"  "+print[1].substring(0,print[1].length()-2)+"座");
                                        }else {
                                            if(print[0].contains("急诊")){
                                                mBTService.PrintCharacters(print[0].replace("急诊留观室","急留室").trim()+":"+"  "+print[1].substring(0,print[1].length()-2)+"座");
                                            }else if(print[0].contains("重症")){
                                                mBTService.PrintCharacters(print[0].replace("重症留观室","重留室").trim()+":"+"  "+print[1].substring(0,print[1].length()-2)+"座");
                                            }
                                        }
                                        //mBTService.PrintCharacters(print[0].replace("输液","").trim()+":"+"   "+print[1].substring(0,print[1].length()-2)+"座");

                                        //mBTService.PrintCharacters("\n"+"\r\r"+printStytleM odel.content.substring(0,printStytleModel.content.length()-2).replace("输液","").trim()+"座"+"\n");
                                        //mBTService.PrintCharacters("\n"+print[0].replace("输液","").trim()+":"+"   "+print[1].substring(0,print[1].length()-2)+"座"+"\n");
                                        mBTService.SendOrder(BluePrintUtils.useNewline());
                                        mBTService.SendOrder(BluePrintUtils.getQSOrderPaddingRow(8));
                                        mBTService.SendOrder(BluePrintUtils.getQSOrderCharacterSize(0));
                                        mBTService.SendOrder(BluePrintUtils.getQSOrderPaddingLeft(1));
                                        mBTService.SendOrder(BluePrintUtils.allowBoldSize(0));
                                        mBTService.PrintCharacters("取号日期："+ DateUtils.getStandarCurrentDate());

                                        mBTService.SendOrder(BluePrintUtils.useNewline());
                                        mBTService.SendOrder(BluePrintUtils.getQSOrderPaddingRow(8));
                                        mBTService.SendOrder(BluePrintUtils.getQSOrderCharacterSize(0));
                                        mBTService.SendOrder(BluePrintUtils.getQSOrderPaddingLeft(1));
                                        mBTService.SendOrder(BluePrintUtils.allowBoldSize(0));
                                        mBTService.PrintCharacters("该座位号限本次使用!");

                                        mBTService.SendOrder(BluePrintUtils.printNextPage());//间隙打印机
                                        break;
                                    case PrintStyle.BARCODE:
                                        mBTService.SendOrder(BluePrintUtils.getQSOrderPaddingLeft(20));
                                        Bitmap btMap = BarcodeCreater.encode2dAsBitmap(printStytleModel.content, 160, 120,
                                                2);
                                        mBTService.PrintImageNew(btMap);
                                        break;
                                }

                            }

                        } else {
                            BuletoothToastUtils.makeText(BluetoothPrintService.this, "蓝牙未连接", 2000).show();

                        }

                        break;
                    case BluetoothUtils.CMD_CONNTECT_BLUETOOTH:
                        if (mBTService.IsOpen()) {
                            if (mBTService.getState() == mBTService.STATE_CONNECTING) {

                                BuletoothToastUtils.makeText(BluetoothPrintService.this, "正在连接蓝牙", 2000).show();
                                return;
                            } else if (mBTService.getState() == mBTService.STATE_CONNECTED) {
                                BuletoothToastUtils.makeText(BluetoothPrintService.this, "已经连接蓝牙", 2000).show();

                            } else {
                                String value = intent.getStringExtra("address");
                                //String address = info.substring(info.length() - 17);
                                mBTService.DisConnected();
                                mBTService.ConnectToDevice(value);// 连接蓝牙
                            }
                        } else {
                            mBTService.OpenDevice();
                        }

                        break;
                }
            }
        }
    }

    public void showToast(String message) {

        Toast.makeText(
                BluetoothPrintService.this, message, Toast.LENGTH_SHORT).show();
        Log.e("蓝牙", message);
    }
}