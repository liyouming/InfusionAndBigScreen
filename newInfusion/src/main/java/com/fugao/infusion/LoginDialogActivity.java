/*
 * Copyright © 2014 上海复高计算机科技有限公司. All rights reserved.
 */

package com.fugao.infusion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.model.MenuListModel;
import com.fugao.infusion.model.NurseAccountModel;
import com.fugao.infusion.setting.SettingsActivity;
import com.fugao.infusion.utils.PullXMLTools;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.NetWorkUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.view.UIHepler;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FugaoApps
 * @Location: com.fugao.nurse.activitys.LoginDialogActivity
 * @Description: TODO  进入设置界面需要验证
 * @author: 席强    xiqiang@fugao.com
 * @date: 2014/7/17 11:14
 * @version: V1.0
 */

public class LoginDialogActivity extends Activity {
    private static final String TAG = "Fugao-LoginDialogActivity";
    private ViewSwitcher mViewSwitcher;
    private ImageButton btn_close;
    private Button btn_login;
    private AutoCompleteTextView mAccount;
    private EditText mPwd;
    private AnimationDrawable loadingAnimation;
    private View loginLoading;
    private CheckBox chb_rememberMe;
    private int curLoginType;
    private InputMethodManager imm;

    public final static int LOGIN_OTHER = 0x00;
    public final static int LOGIN_MAIN = 0x01;
    public final static int LOGIN_SETTING = 0x02;

    public String currentHostName = "";
    /**
     * 用户是否取消登陆
     */
    private boolean isInterruptByUser = false;
    private NurseAccountModel nurseAccountBean;


    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Intent intent = new Intent(LoginDialogActivity.this, SettingsActivity.class);
                intent.putExtra("LOGIN", true);
                startActivity(intent);
                finish();
            } else if (msg.what == 0) {
                mViewSwitcher.showPrevious();
                btn_close.setVisibility(View.VISIBLE);
                UIHepler.showToast(LoginDialogActivity.this, "登陆失败");
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_dialog);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        curLoginType = getIntent().getIntExtra("LOGINTYPE", LOGIN_OTHER);

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.logindialog_view_switcher);
        loginLoading = (View) findViewById(R.id.login_loading);
        mAccount = (AutoCompleteTextView) findViewById(R.id.login_account);
        mPwd = (EditText) findViewById(R.id.login_password);
        chb_rememberMe = (CheckBox) findViewById(R.id.login_checkbox_rememberMe);

        btn_close = (ImageButton) findViewById(R.id.login_close_button);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                LoginDialogActivity.this.finish();
            }
        });

        String currentHostName = XmlDB.getInstance(
                LoginDialogActivity.this).getKeyString(
                "nurse_host_name", "");

        RestClient.BASE_URL = "http://" + currentHostName.trim() + "/";

        btn_login = (Button) findViewById(R.id.login_btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //隐藏软键盘
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                String account = mAccount.getText().toString();
                String pwd = mPwd.getText().toString();
                //判断输入
                if (StringUtils.isEmpty(account)) {
                    UIHepler.showToast(v.getContext(), "用户名不能为空!");
                    return;
                }
                if (StringUtils.isEmpty(pwd)) {
                    UIHepler.showToast(v.getContext(), "密码不能为空!");
                    return;
                }

                btn_close.setVisibility(View.GONE);
                loadingAnimation = (AnimationDrawable) loginLoading.getBackground();
                loadingAnimation.start();
                mViewSwitcher.showNext();

                String local_username = PullXMLTools.parseXml(LoginDialogActivity
                                .this,
                        Constant.DEFAUL_CONFIG_XML, "local_username");
                String local_password = PullXMLTools.parseXml(LoginDialogActivity
                                .this,
                        Constant.DEFAUL_CONFIG_XML, "local_password");

                Message msg = new Message();
                if (local_username.equals(account) && local_password.equals(pwd)) {
                    msg.what = 1;//登陆成功
                } else {
                    msg.what = 0;//登陆成功
                }
                handler.sendMessage(msg);
//                LoginToSettings();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onDestroy();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置验证
     */
    public void LoginToSettings() {
        String usernameString = mAccount.getText().toString();
        String passwordString = mPwd.getText().toString();
        String ip = XmlDB.getInstance(LoginDialogActivity.this).getKeyString("ip", "");
        String port = XmlDB.getInstance(LoginDialogActivity.this).getKeyString("port", "");
        if ("".equals(ip)) {
            UIHepler.showToast(this, "ip地址不能为空！");
            return;
        }
        if ("".equals(port)) {
            UIHepler.showToast(this, "端口号不能为空！");
            return;
        }
        currentHostName = ip + ":" + port;
        String validateResult = validateLogin(usernameString, passwordString);
        if ("ok".equals(validateResult)) {
            btn_close.setVisibility(View.GONE);
            loadingAnimation = (AnimationDrawable) loginLoading.getBackground();
            loadingAnimation.start();
            mViewSwitcher.showNext();
            RestClient.BASE_URL = "http://" + currentHostName.trim() + "/";
            if (NetWorkUtils.isNetworkAvalible(LoginDialogActivity.this)) {

                RestClient.login(InfoApi.login(usernameString,
                        passwordString), new BaseAsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, String content) {
                        if (isInterruptByUser) return;
                        if (statusCode == 200) {
                            nurseAccountBean = String2InfusionModel.string2NurseAccount(content);
                            if(nurseAccountBean.Competence ==null || nurseAccountBean.Competence.size()==0){
                                Toast.makeText(LoginDialogActivity.this, "没有权限，请联系管理员", Toast.LENGTH_SHORT).show();
                                return;
                            }else {
                                List<MenuListModel> menuList = nurseAccountBean.Competence.get(0).MenuList;
                                boolean flags = false;
                                for(MenuListModel each: menuList){
                                    if(!StringUtils.isEmpty(each.ModuleName)){
                                        if(each.ModuleName.contains("PDA设置")){
                                            flags = true;
                                            Intent intent = new Intent(LoginDialogActivity.this, SettingsActivity.class);
                                            intent.putExtra("LOGIN", true);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                                if(!flags){
                                    Toast.makeText(LoginDialogActivity.this,"此用户没有权限进入设置界面",Toast.LENGTH_SHORT).show();
                                    mPwd.setText("");
                                    mViewSwitcher.showPrevious();
                                    btn_close.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                    }

                    @Override
                    public void onFailure(int code, Throwable error, String content) {
                        Log.e("user", content);
                        mViewSwitcher.showPrevious();
                        btn_close.setVisibility(View.VISIBLE);
                        if (isInterruptByUser) return;
                        String msg="";
                        if(content.contains("密码错误")){
                            msg = "密码错误";
                        }else if(content.contains("密码过期")){
                            msg = "密码过期,请修改密码";
                        }else if(content.contains("该账户不存在")){
                            msg = "该账户不存在";
                        }else if(content.contains("账户禁用")){
                            msg = "账户禁用";
                        }else if(content.contains("账户过期")){
                            msg = "账户过期";
                        }else {
                            msg = "登录失败，请重试";
                        }
                        UIHepler.showToast(LoginDialogActivity.this,msg);
                    }
                }, LoginDialogActivity.this);
            } else {
                Toast.makeText(LoginDialogActivity.this, "无网络连接", Toast.LENGTH_SHORT).show();
                initOffile();
            }
        } else {
            Toast.makeText(LoginDialogActivity.this, validateResult, Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * 验证登陆条件
     */
    public String validateLogin(String username, String password) {
        String respone = "ok";
        if (StringUtils.isEmpty(username)) {
            respone = "账号不能为空";
        }
        if (StringUtils.isEmpty(password)) {
            respone = "密码不能为空";
        }
        return respone;
    }

    public void initOffile() {
        new AlertDialog.Builder(LoginDialogActivity.this).setTitle("温馨提醒！").setCancelable(false)
                .setMessage("当前无网络").
                setPositiveButton("退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        LoginDialogActivity.this.finish();

                    }
                }).
                setNegativeButton("去设置", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                        startActivity(wifiSettingsIntent);
                    }
                }).create().show();
    }
}
