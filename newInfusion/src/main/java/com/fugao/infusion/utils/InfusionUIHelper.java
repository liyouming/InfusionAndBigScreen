package com.fugao.infusion.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.jasonchen.base.view.UIHepler;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: InfusionApps
 * @Location: com.fugao.infusion.utils.InfusionUIHelper
 * @Description: TODO
 * @author: 陈亮    chenliang@fugao.com
 * @date: 2014/12/20 14:33
 * @version: V1.0
 */

public class InfusionUIHelper extends UIHepler {
    /**
     * 显示警告知道,点击"知道了"关闭当前dilog
     */
    public static void showWarningDialogByCustom(Context context, String msg) {
        TextView textView = new TextView(context);
        textView.setTextColor(Color.RED);
        textView.setTextSize(22);
        textView.setGravity(Gravity.CENTER);
        textView.setMinHeight(80);
        textView.setText(msg);
        if (alertDialog == null || !alertDialog.isShowing()) {
            alertDialog =
                    new AlertDialog.Builder(context).setIcon(R.drawable.warning)
                            .setTitle("提醒！")
                            .setView(textView)
                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
            alertDialog.show();
        } else {
            alertDialog.setView(textView);
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


}
