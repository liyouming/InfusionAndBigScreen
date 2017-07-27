/**
 * Copyright © 2014 上海复高计算机科技有限公司. All rights reserved.
 *
 * @Title: WarningToast.java
 * @Prject: FugaoEMR
 * @Package: com.fugao.emr.view
 * @Description: TODO
 * @author: 饶涛  raotao@fugao.com
 * @date: 2014年1月6日 上午10:13:15 
 * @version: V1.0
 */
package com.fugao.infusion.utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fugao.infusion.R;


/**
 * 警告Toast
 *
 * @ClassName: WarningToast
 * @Description: TODO
 * @author: 饶涛 raotao@fugao.com
 * @date: 2014年1月6日 上午10:13:15
 */
public class WarningToast {

    private Toast toast;
    private Activity activity;
    private View currentView;
    private TextView warningTextView;
    private String warningStr;

    public static WarningToast newInstance(Activity activity, String warningStr) {
        WarningToast warningToast = new WarningToast(activity, warningStr);
        warningToast.show();
        return warningToast;

    }

    public WarningToast(Activity activity, String warningStr) {
        this.warningStr = warningStr;
        this.activity = activity;
    }

    public void show() {
        currentView = activity.getLayoutInflater().inflate(
                R.layout.view_toast_ok_layout, null);
        warningTextView = (TextView) currentView
                .findViewById(R.id.warning_textview);
        warningTextView.setText(warningStr);
        toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(currentView);
        toast.show();
    }
}
