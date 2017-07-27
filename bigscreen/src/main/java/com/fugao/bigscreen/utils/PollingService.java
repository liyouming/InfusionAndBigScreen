package com.fugao.bigscreen.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;

import com.fugao.bigscreen.MainActivity;
import com.fugao.bigscreen.R;
import com.fugao.bigscreen.model.BottleModel;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.Log;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import java.util.ArrayList;


public class PollingService extends Service {
    public static final String ACTION = "com.fugao.nurse.utils.PollingService";
    private Notification mNotification;
    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // initNotifiManager();
        Log.d("pollingService 启动");
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

    int count = 0;

    class PollingThread extends Thread {
        @Override
        public void run() {
//            String divisionCode = XmlDB.getInstance(getApplicationContext())
//                    .getKeyString("last_division_code", "0");
            //			android.util.Log.i("info", NurseApi.getNewAdvicePatientBean(divisionCode,
            //					"noExeFlag"));
//            if (!StringUtils.getString(RestClient.BASE_URL).equals("")) {
            String url="http://192.168.252.1:8011/api/infusion/infusionInfo?DepartmentId=100001&statusgroup=1_2_3";
                RestClient.get(url, new BaseAsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, String s) {
                        final ArrayList<BottleModel> bottleModels = String2InfusionModel.string2BottleModels(s);
                        try {
                            sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.obj=bottleModels;
                        message.what = 4;
                        MainActivity.mainHandler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(int i, Throwable throwable, String s) {

                    }
                });
//            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("pollingService终止");
        UIHepler.showToast(this, "pollingService终止");
    }

}
