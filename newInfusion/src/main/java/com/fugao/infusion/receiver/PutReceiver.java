package com.fugao.infusion.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fugao.infusion.service.PutService;
import com.fugao.infusion.utils.AlarmManagerUtils;
import com.jasonchen.base.utils.Log;
import com.jasonchen.base.utils.NetWorkUtils;
import com.jasonchen.base.utils.RestClient;


/**
 * 更新本地未上传的数据
 */
public class PutReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("收到定时上传提醒！");
        boolean flag = NetWorkUtils.isNetworkAvalible(context);
        if(!flag){
            Log.e("自动上传任务网络异常");
        }
        AlarmManagerUtils.sendPutBroadcast(context);
        if(!RestClient.BASE_URL.equals("")&& flag){
            intent.setClass(context, PutService.class);
            context.startService(intent);
        }
    }
}