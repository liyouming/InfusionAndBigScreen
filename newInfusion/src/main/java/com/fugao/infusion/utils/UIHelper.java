/*
 * Copyright (C) 2014 loQua.Xee <loquaciouser@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fugao.infusion.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fugao.infusion.BuildConfig;
import com.fugao.infusion.R;
import com.fugao.infusion.view.CustomDialog;
import com.jasonchen.base.utils.AppManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastAndroid
 * @Location: com.android.fastlibrary.ui.ViewInject
 * @Description: TODO
 * @author: loQua.Xee    loquaciouser@gmail.com
 * @date: 2014/8/16 14:44
 * @version: V1.0
 */

public class UIHelper {
  private static final String TAG = "Fugao-ViewInject";

  private static AlertDialog alertDialog;

  private static UIHelper instance;

  private UIHelper() {
  }

  /**
   * 单一实例
   */
  public static UIHelper create() {
    if (instance == null) {
      instance = new UIHelper();
    }
    return instance;
  }

  /**
   * 显示一个toast
   */
  public static void showToast(String msg) {
    try {
      showToast(AppManager.getInstance().getTopActivity(), msg);
    } catch (Exception e) {
    }
  }

  /**
   * 长时间显示一个toast
   */
  public static void showLongToast(String msg) {
    try {
      showLongToast(AppManager.getInstance().getTopActivity(), msg);
    } catch (Exception e) {
    }
  }

  /**
   * 长时间显示一个toast
   */
  public static void showLongToast(Context context, String msg) {
    Toast.makeText(AppManager.getInstance().getTopActivity(), msg, Toast.LENGTH_LONG).show();
  }

  /**
   * 显示一个toast
   */
  public static void showToast(Context context, String msg) {
    Toast.makeText(AppManager.getInstance().getTopActivity(), msg, Toast.LENGTH_SHORT).show();
  }
    public static void showDevToast(Context context, String msg) {
        if(BuildConfig.DEBUG) {
            Toast.makeText(AppManager.getInstance().getTopActivity(), msg, Toast.LENGTH_SHORT).show();
        }}

  /**
   * 返回一个退出确认对话框
   *
   * @param context 当前上下文
   */
  public static void getExitDialog(final Context context) {
      CustomDialog.Builder customBuilder = new CustomDialog.Builder(
              AppManager.getInstance().getTopActivity());
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
                              AppManager.getInstance().appExit(AppManager.getInstance().getTopActivity());
                              dialog.dismiss();
                          }
                      }
              );
      Dialog dialog = customBuilder.create();
      dialog.show();

   /* AlertDialog.Builder builder = new AlertDialog.Builder(AppManager.getInstance().getTopActivity());
    builder.setMessage("确定退出吗？");
    builder.setCancelable(true);
    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        AppManager.getInstance().appExit(context);
      }
    });
    builder.create();
    builder.show();
    builder = null;*/
  }

  /**
   * 用于创建PopupWindow封装一些公用属性
   */
  private PopupWindow createWindow(View view, int w, int h, int argb) {
    PopupWindow popupView = new PopupWindow(view, w, h);
    popupView.setFocusable(true);
    popupView.setBackgroundDrawable(new ColorDrawable(argb));
    popupView.setOutsideTouchable(true);
    return popupView;
  }

  /**
   * 返回一个日期对话框
   */
  public void getDateDialog(String title, final TextView textView) {
    final String[] time =getDataTime("yyyy-MM-dd").split("-");
    final int year = Integer.parseInt(time[0]);
    final int month = Integer.parseInt(time[1]);
    final int day = Integer.parseInt(time[2]);
    DatePickerDialog dialog =
        new DatePickerDialog(textView.getContext(), new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            textView.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
          }
        }, year, month - 1, day);
    dialog.setTitle(title);
    dialog.show();
  }
    /**
     * 指定格式返回当前系统时间
     */
    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }
  /**
   * 返回一个等待信息弹窗
   *
   * @param aty 要显示弹出窗的Activity
   * @param msg 弹出窗上要显示的文字
   * @param cancel dialog是否可以被取消
   */
  public static ProgressDialog getProgress(Activity aty, String msg, boolean cancel) {
    // 实例化一个ProgressBarDialog
    ProgressDialog progressDialog = new ProgressDialog(aty);
    progressDialog.setMessage(msg);
    progressDialog.getWindow()
        .setLayout(DensityUtils.getScreenW(aty), DensityUtils.getScreenH(aty));
    progressDialog.setCancelable(cancel);
    // 设置ProgressBarDialog的显示样式
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.show();
    return progressDialog;
  }
  /**
   * 显示警告知道,点击"知道了"关闭当前dilog
   */
  public static void showWarningDialog(String msg) {
    if (alertDialog == null || !alertDialog.isShowing()) {
         alertDialog =
          new AlertDialog.Builder(AppManager.getInstance().getTopActivity()).setTitle("提醒！")
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

  /**
   * 显示警告知道,点击"知道了"关闭当前dilog
   */
  public static void showWarningDialog(Context context, String msg) {
    if (alertDialog == null || !alertDialog.isShowing()) {
        alertDialog = new AlertDialog.Builder(AppManager.getInstance().getTopActivity()).setTitle("提醒！")
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

  public static void showWarningDialog(Context context, String msg,
      DialogInterface.OnClickListener listener) {
    if (alertDialog == null || !alertDialog.isShowing()) {
       alertDialog = new AlertDialog.Builder(AppManager.getInstance().getTopActivity()).setTitle("提醒！")
          .setMessage(msg)
          .setPositiveButton("知道了", listener)
          .create();
      alertDialog.show();
    } else {
      alertDialog.setMessage(msg);
    }
  }

  public static void showInfoDilalog(Context context, String msg) {
    if (alertDialog == null || !alertDialog.isShowing()) {
      alertDialog = new AlertDialog.Builder(AppManager.getInstance().getTopActivity()).setTitle("提醒！")
          .setMessage(msg)
          .setPositiveButton("确定", new DialogInterface.OnClickListener() {

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

  public static void showInfoDilalog(Context context, String msg,
      DialogInterface.OnClickListener listener) {
    if (alertDialog == null || !alertDialog.isShowing()) {
        alertDialog = new AlertDialog.Builder(context).setTitle("提醒！")
          .setMessage(msg)
          .setPositiveButton("确定", listener)
          .create();
      alertDialog.show();
    } else {
      alertDialog.setMessage(msg);
    }
  }

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
       alertDialog = new AlertDialog.Builder(AppManager.getInstance().getTopActivity()).setTitle("提醒！")
          .setMessage(msg)
          .setPositiveButton("确定", okListener)
          .setNegativeButton("取消", cancelListener)
          .create();
      alertDialog.show();
    } else {
      alertDialog.setMessage(msg);
    }
  }

  public static void showConfirmDilalog(Context context, String msg,
      DialogInterface.OnClickListener okListener) {
    if (alertDialog == null || !alertDialog.isShowing()) {
       alertDialog = new AlertDialog.Builder(AppManager.getInstance().getTopActivity()).setTitle("提醒！")
          .setMessage(msg)
          .setPositiveButton("确定", okListener)
          .setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
              dialogInterface.dismiss();
            }
          })
          .create();
      alertDialog.show();
    } else {
      alertDialog.setMessage(msg);
    }
  }

  public static ProgressDialog createProgressDialog(Context context) {
    ProgressDialog progressDialog = new ProgressDialog(context);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.setMessage(context.getResources().getString(R.string.defaultLoadingInfo));
    progressDialog.setIndeterminate(false);
    progressDialog.setCancelable(true);
    progressDialog.setCanceledOnTouchOutside(false);
    return progressDialog;
  }

  public static void showListDialog(Context context, String title,
      DialogInterface.OnClickListener listener, String... ss) {
    new AlertDialog.Builder(context).setTitle(title)
        //.setIcon(null)
        .setItems(ss, listener).create().show();
  }

    /**
     * 显示警告知道,点击"知道了"关闭当前dilog
     */
    public static void showWarningDialogByCustom(Context context, String msg) {
        TextView textView = new TextView(AppManager.getInstance().getTopActivity());
        textView.setTextColor(Color.RED);
        textView.setTextSize(22);
        textView.setGravity(Gravity.CENTER);
        textView.setMinHeight(80);
        textView.setText(msg);
        if (alertDialog == null || !alertDialog.isShowing()) {
            alertDialog =
                    new AlertDialog.Builder(AppManager.getInstance().getTopActivity()).setIcon(R.drawable.warning)
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
     *  弹出确实对话框 有确认和取消 两个按钮
     *  这里没有用showConfirmDilalog原因为会出现弹不出来的情况，偶尔进去会出现alertDalog不为空的情况
     * @param context 上下文
     * @param msg 提示内容
     * @param okListener 确定 监听事件
     */
    public static void showExcuteDilalog(Context context, String msg,
                                                            DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(AppManager.getInstance().getTopActivity()).setTitle("提醒！")
                .setMessage(msg)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", cancelListener)
                .create().show();
    }
}
