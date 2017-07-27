package com.fugao.infusion.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.fugao.infusion.receiver.PutReceiver;
import com.jasonchen.base.utils.Log;


/**
 * 1.定时任务 ，去定时执行某项数据
 * 2.周期性发送检测程序，
 * 3.发送一次检测程序
 */
public class AlarmManagerUtils {
    /**
     * 定时更新时间
     */
    private static   int UPLOADTIME = 60000;
    /**
     * 定时上传时间
     */
    private static  int PUTTIME = 30000;
    public static AlarmManager am;
    public static AlarmManager getAlarmManager(Context ctx){
        return (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

    }
   /* *//**
     * 指定时间后进行更新赛事信息(有如闹钟的设置)
     * 注意: Receiver记得在manifest.xml中注册
     *
     * @param ctx
     *//*
    public static void sendUpdateBroadcast(Context ctx){
        Log.e("alarm", "启动定时检测更新任务" + UPLOADTIME);
         am = getAlarmManager(ctx);
        // 60秒后将产生广播,触发UpdateReceiver的执行,这个方法才是真正的更新数据的操作主要代码
        Intent i = new Intent(ctx, UpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
        am.set(AlarmManager.RTC, System.currentTimeMillis()+UPLOADTIME, pendingIntent);
    }*/
 /*   *//**
     * 取消定时执行(有如闹钟的取消)
     *
     * @param ctx
     *//*
    public static void cancelUpdateBroadcast(Context ctx){
        Log.e("alarm", "取消定时检测更新任务" + UPLOADTIME);
        AlarmManager am = getAlarmManager(ctx);
        Intent i = new Intent(ctx, UpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
        am.cancel(pendingIntent);
    }*/
/*
    */
/**
     * 定时性的更新程序,
     * @param ctx
     *//*

    public static void sendUpdateBroadcastRepeat(Context ctx){
        Log.e("alarm", "开启定时检测周期性更新任务" + UPLOADTIME);
        Intent intent =new Intent(ctx, UpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);

        //开始时间
        long firstime= SystemClock.elapsedRealtime();

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
       //60秒一个周期，不停的发送广播
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, 10000, pendingIntent);
    }
*/


   /********上传相关********/
    /**
     * 指定时间后进行更新赛事信息(有如闹钟的设置)
     * 注意: Receiver记得在manifest.xml中注册
     *
     * @param ctx
     */
    public static void sendPutBroadcast(Context ctx){
        Log.e("alarm", "启动定时检测上传任务" + PUTTIME);
        am = getAlarmManager(ctx);
        Intent i = new Intent(ctx, PutReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
        am.set(AlarmManager.RTC, System.currentTimeMillis()+PUTTIME, pendingIntent);
    }
    /**
     * 取消定时执行(有如闹钟的取消)
     *
     * @param ctx
     */
    public static void cancelPutBroadcast(Context ctx){
        Log.e("alarm", "取消定时检测上传任务" + PUTTIME);
        AlarmManager am = getAlarmManager(ctx);
        Intent i = new Intent(ctx, PutReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
        am.cancel(pendingIntent);
    }


}
