package com.fugao.infusion.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.fugao.infusion.BuildConfig;
import com.fugao.infusion.view.CustomDialog;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: InfusionApps
 * @Location: com.fugao.infusion.utils.InfusionHelper
 * @Description: TODO
 * @author: 蒋光锦  jiangguangjin@fugao.com
 * @date: 2014/12/24 18:27
 * @version: V1.0
 */

public class InfusionHelper {
    private static AlertDialog alertDialog;
    /**
     * 弹出确实对话框 有确认和取消 两个按钮
     *
     * @param context 上下午
     * @param msg 提示内容
     * @param okListener 确定 监听事件
     */
    public static void showConfirmDilalog(Context context, String msg,
                                          DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        if (alertDialog == null || !alertDialog.isShowing()) {
            alertDialog = new AlertDialog.Builder(context).setTitle("提醒！")
                    .setMessage(msg)
                    .setPositiveButton("确定", okListener)
                    .setNegativeButton("取消", cancelListener)
                    .create();
            alertDialog.show();
        } else {
            alertDialog.setMessage(msg);
        }
    }
    /**
     * 显示警告知道,点击"知道了"关闭当前dilog
     */
    public static void showWarningDialog(Context context,String msg) {
        if (alertDialog == null || !alertDialog.isShowing()) {
            alertDialog =
                    new AlertDialog.Builder(context).setTitle("提醒！")
                            .setMessage(msg)
                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
            alertDialog.show();
        } else {
            alertDialog.setMessage(msg);
        }
    }
    public static void showDebugToast(Context context,String msg){
        if(BuildConfig.DEBUG){
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 退出程序
     * @param activity
     */
    public static void exite(final Activity activity) {
            CustomDialog.Builder customBuilder = new CustomDialog.Builder(
                    activity);
            customBuilder
                    .setTitle("温馨提示！")
                    .setMessage("确定要退出程序吗？")
                    .setContentView(null)
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                try{
                                activity.finish();
                                ActivityManager activityMgr = (ActivityManager) activity.getSystemService(Context
                                    .ACTIVITY_SERVICE);
                                activityMgr.killBackgroundProcesses(activity.getPackageName());
                                System.exit(0);
                                }catch (Exception e){
                                e.printStackTrace();
                                    System.exit(0);
                                }

                                }
                            }
                    );
            Dialog dialog = customBuilder.create();
            dialog.show();
    }
}
