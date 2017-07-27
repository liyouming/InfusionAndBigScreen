package com.fugao.newbigscreen.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.fugao.newbigscreen.MainActivity;
import com.fugao.newbigscreen.R;
import com.fugao.newbigscreen.activitys.InjectionWaitingActivity;
import com.fugao.newbigscreen.constant.InfoApi;
import com.fugao.newbigscreen.model.BottleModel;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.view.UIHepler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;


public class PollingService extends Service {
    public static final String ACTION = "com.fugao.newbigscreen.utils.PollingService";
    private Notification mNotification;
    private NotificationManager mManager;
    private AsyncHttpClient client;
    private String departmentId="";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // initNotifiManager();
//        Log.d("pollingService 启动");
        // UIHepler.showToast(this, "pollingService启动");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        new PollingThread().start();

    }

    private void initNotifiManager(String s) {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.mipmap.ic_launcher;
        mNotification = new Notification();
        mNotification.icon = icon;
        mNotification.tickerText = s;
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
    }

    private void showNotification() {
        mNotification.when = System.currentTimeMillis();
        // Navigator to the new activity when click the notification title
        Intent i = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
//                Intent.FLAG_ACTIVITY_NEW_TASK);
//        mNotification.setLatestEventInfo(this,
//                getResources().getString(R.string.app_name),
//                "有新医嘱。", pendingIntent);
        mManager.notify(0, mNotification);
    }

    Handler m_handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==0){
                doGet();
            }
        }
    };
    public void doGet() {
//        final String url="http://192.168.252.1:8011/api/infusion/infusionInfo?DepartmentId=100001&statusgroup=1_2_3";
        String url= "http://192.168.252.1:8011/"+InfoApi.getGetCall(departmentId);
//        RestClient.BASE_URL="http://192.168.252.1:8011/";
        RestClient.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String s = new String(bytes,"UTF-8");
                    s=s.substring(1,s.length()-1);
                    if("".equals(s)){

                    }else {
                        Message message = new Message();
                        message.obj=s;
                        message.what = 4;
                        Intent intent = new Intent();
                        intent.setClassName("com.fugao.newbigscreen","InjectionWaitingActivity");
                        if(intent.resolveActivity(getPackageManager())!=null){
                            InjectionWaitingActivity.mainHandler.sendMessage(message);
                        }
                    }

//                    if(intent.resolveActivity(getPackageManager())!=null){
//                        MainActivity.mainHandler.sendMessage(message);
//                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                String s="";
                Log.d("error1","222");
            }

        });
    }
    class PollingThread extends Thread {

        @Override
        public void run() {
//            String s="[{\"BottleId\":\"231620751\",\"InfusionId\":\"201505290079\",\"InfusionNo\":0,\"InvoicingId\":0,\"PrescriptionId\":23162075,\"DoctorId\":\"0000\",\"DoctorCore\":\"0000\",\"DoctorName\":\"管理员\",\"DiagnoseCore\":null,\"DiagnoseName\":null,\"PrescribeDate\":null,\"PrescribeTime\":null,\"GroupId\":\"2-1\",\"SeatNo\":\"\",\"BottleStatus\":1,\"Way\":\"静滴\",\"Frequency\":\"qd\",\"TransfusionBulk\":0,\"TransfusionSpeed\":\"150ml/h\",\"ExpectTime\":0,\"SubscribeDate\":null,\"SubscribeTime\":null,\"RegistrationDate\":\"20160615\",\"RegistrationTime\":\"155622\",\"RegistrationId\":0,\"RegistrationCore\":\"\",\"PillDate\":null,\"PillTime\":null,\"PillId\":0,\"PillCore\":\"1484\",\"PillName\":\"黄怡蝶\",\"LiquorDate\":null,\"LiquorTime\":null,\"LiquorId\":0,\"LiquorCore\":null,\"LiquorName\":null,\"InfusionDate\":null,\"InfusionTime\":null,\"InfusionPeopleId\":0,\"InfusionCore\":null,\"InfusionName\":null,\"EndDate\":null,\"EndTime\":null,\"EndId\":0,\"EndCore\":null,\"EndName\":null,\"CheckDate\":null,\"CheckTime\":null,\"CheckId\":0,\"CheckCore\":null,\"CheckName\":null,\"Remark\":null,\"SpeedUnit\":null,\"LZZ\":\"\",\"GCF\":\"\",\"DrugDetails\":[{\"DetailId\":\"231620751\",\"BottleId\":\"231620751\",\"GroupId\":\"2-1\",\"PrescriptionId\":23162075,\"ItemId\":\"000697\",\"ItemName\":\"(四川)5%葡萄糖针\",\"Standard\":\"50ml\",\"Amount\":1.0,\"Unit\":\"袋\",\"EveryAmount\":\"50.0000\",\"AmountUnit\":\"ml\",\"Remark\":null,\"ReturnFlag\":0,\"Infusion\":null},{\"DetailId\":\"231620752\",\"BottleId\":\"231620751\",\"GroupId\":\"2-1\",\"PrescriptionId\":23162075,\"ItemId\":\"051711\",\"ItemName\":\"20%甘露醇针(浙江)\",\"Standard\":\"250ml\",\"Amount\":1.0,\"Unit\":\"瓶\",\"EveryAmount\":\"250.0000\",\"AmountUnit\":\"ml\",\"Remark\":null,\"ReturnFlag\":0,\"Infusion\":null}],\"PeopleInfo\":{\"Date\":\"20160615\",\"QueueNo\":\"79\",\"DepartmentId\":\"1110501\",\"InfusionId\":\"201505290079\",\"PatId\":\"0071694544\",\"Name\":\"empi测试\",\"Status\":0,\"CardId\":0,\"CardCore\":null,\"SeatNo\":\"\",\"Age\":\"4月\",\"Sex\":\"1\",\"InsertTime\":\"2016-06-15T15:56:22\",\"UpdateTime\":\"06 15 2016  3:56PM\",\"Ticket\":\"0000008159\",\"Detective\":\"足趾脱位\",\"Weight\":\"10kg\",\"DrugAllergy\":\"不详;\",\"callCount\":0,\"ObserveFlag\":0,\"OverCall\":0},\"AboutPatrols\":null},{\"BottleId\":\"231620752\",\"InfusionId\":\"201505290079\",\"InfusionNo\":0,\"InvoicingId\":0,\"PrescriptionId\":23162075,\"DoctorId\":\"0000\",\"DoctorCore\":\"0000\",\"DoctorName\":\"管理员\",\"DiagnoseCore\":null,\"DiagnoseName\":null,\"PrescribeDate\":null,\"PrescribeTime\":null,\"GroupId\":\"2-2\",\"SeatNo\":\"\",\"BottleStatus\":1,\"Way\":\"静滴\",\"Frequency\":\"qd\",\"TransfusionBulk\":0,\"TransfusionSpeed\":\"\",\"ExpectTime\":0,\"SubscribeDate\":null,\"SubscribeTime\":null,\"RegistrationDate\":\"20160615\",\"RegistrationTime\":\"155622\",\"RegistrationId\":0,\"RegistrationCore\":\"\",\"PillDate\":null,\"PillTime\":null,\"PillId\":0,\"PillCore\":\"1484\",\"PillName\":\"黄怡蝶\",\"LiquorDate\":null,\"LiquorTime\":null,\"LiquorId\":0,\"LiquorCore\":null,\"LiquorName\":null,\"InfusionDate\":null,\"InfusionTime\":null,\"InfusionPeopleId\":0,\"InfusionCore\":null,\"InfusionName\":null,\"EndDate\":null,\"EndTime\":null,\"EndId\":0,\"EndCore\":null,\"EndName\":null,\"CheckDate\":null,\"CheckTime\":null,\"CheckId\":0,\"CheckCore\":null,\"CheckName\":null,\"Remark\":null,\"SpeedUnit\":null,\"LZZ\":\"\",\"GCF\":\"\",\"DrugDetails\":[{\"DetailId\":\"231620753\",\"BottleId\":\"231620752\",\"GroupId\":\"2-2\",\"PrescriptionId\":23162075,\"ItemId\":\"000760\",\"ItemName\":\"0.9%氯化钠针(大冢 塑料安瓿装)\",\"Standard\":\"10ml\",\"Amount\":1.0,\"Unit\":\"支\",\"EveryAmount\":\"10.0000\",\"AmountUnit\":\"ml\",\"Remark\":null,\"ReturnFlag\":0,\"Infusion\":null},{\"DetailId\":\"231620754\",\"BottleId\":\"231620752\",\"GroupId\":\"2-2\",\"PrescriptionId\":23162075,\"ItemId\":\"052119\",\"ItemName\":\"炎琥宁针\",\"Standard\":\"40mg\",\"Amount\":1.0,\"Unit\":\"支\",\"EveryAmount\":\"10.0000\",\"AmountUnit\":\"mg\",\"Remark\":null,\"ReturnFlag\":0,\"Infusion\":null}],\"PeopleInfo\":{\"Date\":\"20160615\",\"QueueNo\":\"79\",\"DepartmentId\":\"1110501\",\"InfusionId\":\"201505290079\",\"PatId\":\"0071694544\",\"Name\":\"empi测试\",\"Status\":0,\"CardId\":0,\"CardCore\":null,\"SeatNo\":\"\",\"Age\":\"4月\",\"Sex\":\"1\",\"InsertTime\":\"2016-06-15T15:56:22\",\"UpdateTime\":\"06 15 2016  3:56PM\",\"Ticket\":\"0000008159\",\"Detective\":\"足趾脱位\",\"Weight\":\"10kg\",\"DrugAllergy\":\"不详;\",\"callCount\":0,\"ObserveFlag\":0,\"OverCall\":0},\"AboutPatrols\":null}]";
            Message message=m_handler.obtainMessage();
            message.arg1=0;
            m_handler.sendMessage(message);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        new PollingThread().interrupt();
        Log.d("ser","pollingService终止");
        UIHepler.showToast(this, "pollingService终止");
    }

}
