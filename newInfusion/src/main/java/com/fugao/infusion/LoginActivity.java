/*
 * Copyright © 2014 上海复高计算机科技有限公司. All rights reserved.
 */

package com.fugao.infusion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.model.NurseAccountModel;
import com.fugao.infusion.setting.RoleChoiceActivity;
import com.fugao.infusion.setting.SyncActivity;
import com.fugao.infusion.utils.InfoUtils;
import com.fugao.infusion.utils.InfusionDateUtils;
import com.fugao.infusion.utils.InfusionHelper;
import com.fugao.infusion.utils.PullXMLTools;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.UIHelper;
import com.fugao.infusion.utils.UpdateDialgoFragment;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.AppUtils;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.DES;
import com.jasonchen.base.utils.FileHelper;
import com.jasonchen.base.utils.NetWorkUtils;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 登陆页面
 *
 * @author findchen 2013-5-24下午3:07:20
 */
public class LoginActivity extends FragmentActivity {
    @InjectView(R.id.title_text_view)
    TextView mTitleTextView;
    @InjectView(R.id.android_advice_name)
    TextView mAndroidAdviceName;
    @InjectView(R.id.login_title_setting_text_view)
    TextView mLoginTitleSettingTextView;
    @InjectView(R.id.usernametext)
    TextView mUsernametext;
    @InjectView(R.id.username)
    EditText mUsername;
    @InjectView(R.id.passwordText)
    TextView mPasswordText;
    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.login_textview)
    TextView mLoginTextview;
    @InjectView(R.id.login)
    LinearLayout mLogin;
    @InjectView(R.id.login_content)
    LinearLayout mLoginContent;
    @InjectView(R.id.login_content_layout)
    ScrollView mLoginContentLayout;
    @InjectView(R.id.app_version_text)
    TextView mAppVersionText;
    @InjectView(R.id.switch_ip)
    LinearLayout mSwitchIp;
    @InjectView(R.id.modifyPassword)
    TextView modifyPassword;
    private TextView login;
    private EditText username;
    private EditText password;
    private Activity currentActivity;
    private ProgressDialog progressDialog;
    public InfusionApplication infusionApplication;


    private TextView passwordText;
    public String currentHostName = "";
    private String currentDate;
    public String currentDivision = "";
    public static Handler loginToMainActivity;
    public String hostNameKey = "nurse_host_name";
    /**
     * 上一次登陆的病区
     */
    public static String lastDivisionCode = "last_division_code";
    /**
     * 病区设置返回值
     */
    private static final int DEFAULT_DIVISION = 314;
    private int reLoginTime = 0;
    private LinearLayout settingLayout;

    /**
     * 用户是否取消登陆
     */
    private boolean isInterruptByUser = false;
    private TextView android_advice_name;

    private NurseAccountModel nurseAccountBean;
    private String port;
    private String ip;
    String oldHostName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        infusionApplication = (InfusionApplication) getApplication();
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("正在登录中...");
        progressDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置点击进度对话框外的区域对话框不消失
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                isInterruptByUser = true;
            }
        });
//        WindowManager.LayoutParams params = progressDialog.getWindow().getAttributes();
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        currentActivity = LoginActivity.this;
        initView();
        initData();
        initSetting();
        loginToMainActivity = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Intent intent = new Intent();
                        intent.setClass(currentActivity, RoleChoiceActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        break;
                    default:
                        break;
                }
            }

        };
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!NetWorkUtils.isNetworkAvalible(currentActivity)) {
                    initOffile();
                } else {
                    reLoginTime = 1;
                    isInterruptByUser = false;
                    Login();
                }

            }
        });

        mLoginTitleSettingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, LoginDialogActivity.class);
                startActivity(intent);
            }
        });
        modifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ModifyPasswordActivity.class));
            }
        });

    }


    /**
     * 判断是否有默认病区，没有则弹出选择框，选择病区
     */
    protected void ishasDivision(boolean isRefrshDicision) {
        currentDivision = XmlDB.getInstance(LoginActivity.this).getKeyString(lastDivisionCode, "");
        loginToMainActivity();
//        if (StringUtils.StringIsEmpty(currentDivision) || isRefrshDicision) {
//
////            Intent intent = new Intent(currentActivity, SettingDefaultDivisionActivity.class);
////            intent.putExtra("default_division", DEFAULT_DIVISION);
////            intent.putParcelableArrayListExtra("departmentBeans", nurseAccountBean.DeptPermits);
//            startActivityForResult(intent, 0);
//        } else {
//            loginToMainActivity();
//        }
    }

    public void initOffile() {
        new AlertDialog.Builder(currentActivity).setTitle("温馨提醒！").setCancelable(false)
                .setMessage("当前无网络").
//                setPositiveButton("离线模式", new OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                        GoneIp();
//                        login.setText("离线登录");
//                        passwordText.setText("离线密码:");
//                        loginByOffline();
//
//                        }
//                        }).

        setPositiveButton("退出", new OnClickListener() {

    @Override
    public void onClick(DialogInterface arg0, int arg1) {
        LoginActivity.this.finish();

    }
}).
                setNegativeButton("去设置", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                        startActivity(wifiSettingsIntent);
                    }
                }).create().show();
    }

    public void initView() {
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        settingLayout = (LinearLayout) findViewById(R.id.login);
        String last_userName = XmlDB.getInstance(LoginActivity.this).getKeyString
                ("last_username", "");
        username.setText(last_userName);
        login = (TextView) findViewById(R.id.login_textview);
        android_advice_name = (TextView) findViewById(R.id.android_advice_name);
    }


    private void loginToMainActivity() {


        currentDate = infusionApplication.currentDate;
        XmlDB.getInstance(LoginActivity.this).saveKey("last_username",
                StringUtils.getString(username.getText().toString()));
        Intent intent = new Intent();
        intent.putExtra("activity", "LoginActivity");
        intent.setClass(LoginActivity.this, SyncActivity.class);
        startActivity(intent);

    }

    private void initData() {
        /**
         * 设置界面填写设备名称。
         */
        String device_name = XmlDB.getInstance(this).getKeyString("android_device_name", "请填写设备名称");
        android_advice_name.setText(device_name);
        String versionString = getResources().getString(R.string.app_version_description);
        String appName = AppUtils.getCurrentVersionName(currentActivity);
        String appFullName = String.format(versionString, appName);
        mAppVersionText.setText(appFullName);

        if (BuildConfig.DEBUG) {
            username.setText("0000");
            password.setText("0000");
        }

    }
    //
    //	/**
    //	 * 检查是否有未上传的内容
    //	 *
    //	 * @Title: checkUploads
    //	 * @return
    //	 * @return: ArrayList<NurseTaskDetailBean>
    //	 */
    //	public ArrayList<NurseTaskDetailBean> checkUploads() {
    //
    //		String divisionCode = XmlDB.getInstance(LoginActivity.this)
    //				.getKeyString("last_division_code", "0");
    //		ArrayList<NurseTaskDetailBean> nurseTaskDetailBeans = nurseTaskDetailDAO
    //				.getNurseTaskByDivisonAndUnUPload(divisionCode);
    //		return nurseTaskDetailBeans;
    //	}

    public void checkUpdate() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            // loginToMainActivity();
        } else if (resultCode == DEFAULT_DIVISION) {
            loginToMainActivity();
        }
    }


    /*
     * 验证登陆条件
     */
    public String validateLogin(String username, String password) {

        String respone = "ok";

        if (StringUtils.StringIsEmpty(username)) {
            respone = "账号不能为空";
        }
        if (StringUtils.StringIsEmpty(password)) {
            respone = "密码不能为空";
        }

//        String[] str = hostname.split(":");
//        if (StringUtils.StringIsEmpty(str[0])) {
//            respone = "IP地址不能为空";
//        }
//        if (StringUtils.StringIsEmpty(str[1])) {
//            respone = "端口号不能为空";
//        }
//
        return respone;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            InfusionHelper.exite(LoginActivity.this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void Login() {
        String usernameString = username.getText().toString();
        String passwordString = password.getText().toString();
        ip = XmlDB.getInstance(LoginActivity.this).getKeyString("ip", "");
        port = XmlDB.getInstance(LoginActivity.this).getKeyString("port", "");
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
            RestClient.BASE_URL = "http://" + currentHostName.trim() + "/";
            if (NetWorkUtils.isNetworkAvalible(currentActivity)) {
                closeKeyboard();
                if (reLoginTime > 1) {
                    progressDialog.setMessage("尝试第" + reLoginTime + "次登录,请寻找信号强的地方");
                } else {
                    progressDialog.setMessage("正在登录中...");
                }

                RestClient.login(InfoApi.login(usernameString,
                        passwordString,true,AppUtils.getCurrentVersion(currentActivity),"移动输液"), new BaseAsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        progressDialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, String content) {
                        if (isInterruptByUser) return;
                        if (statusCode == 200) {
                            progressDialog.dismiss();
                            nurseAccountBean = String2InfusionModel.string2NurseAccount(content);
                            if (nurseAccountBean.Competence == null || nurseAccountBean.Competence.size() == 0) {
                                Toast.makeText(LoginActivity.this, "没有权限，请联系管理员", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            String sycntime=XmlDB.getInstance(LoginActivity.this).getKeyString("syncTimes","2");
                            int minute=Integer.parseInt(sycntime);
                            if(minute<=0){
                                minute=2;
                            }
                            boolean flag = InfusionDateUtils.isSetSystemDateTime(
                                    minute, nurseAccountBean.NowDateTime);
                            if (flag) {
                                boolean syncServer = InfoUtils.setPDATimeSyncServer(LoginActivity.this, nurseAccountBean.NowDateTime);
                                if (syncServer) {
                                    LocalSetting.CurrentAccount = nurseAccountBean;
                                    XmlDB.getInstance(LoginActivity.this).saveKey("AcountId", nurseAccountBean.Id);
                                    oldHostName = XmlDB.getInstance(
                                            LoginActivity.this).getKeyString(
                                            hostNameKey, "");
                                    if(nurseAccountBean.AppInfo!=null){
                                        newVersion();
                                        return;
                                    }
                                    toDivision();
//                                    if (!oldHostName.equals("")
//                                            && oldHostName.equals(currentHostName)) {
//                                        ishasDivision(false);
//                                    } else {
//                                        XmlDB.getInstance(LoginActivity.this)
//                                                .saveKey(hostNameKey,
//                                                        currentHostName);
//                                        ishasDivision(true);
//                                    }
                                }
                                //                                new AlertDialog.Builder(LoginActivity.this)
//                                        .setTitle("本机时间不正确！")
//                                        .setMessage(
//                                                "当前系统时间为:"
//                                                        + nurseAccountBean.NowDateTime
//                                        )
//                                        .setPositiveButton("关闭",
//                                                new OnClickListener() {
//
//                                                    @Override
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int which) {
//                                                        dialog.dismiss();
//                                                    }
//                                                }
//                                        )
//                                        .setPositiveButton(
//                                                "去设置",
//                                                new OnClickListener() {
//
//                                                    @Override
//                                                    public void onClick(
//                                                            DialogInterface dialog,
//                                                            int which) {
//                                                        Intent intent = new Intent(
//                                                                Settings.ACTION_DATE_SETTINGS);
//                                                        startActivityForResult(
//                                                                intent, 10);
//                                                    }
//                                                }
//                                        ).create().show();

                            } else {
                                LocalSetting.CurrentAccount = nurseAccountBean;
                                XmlDB.getInstance(LoginActivity.this).saveKey("AcountId", nurseAccountBean.Id);
                                oldHostName = XmlDB.getInstance(
                                        LoginActivity.this).getKeyString(
                                        hostNameKey, "");
                                if(nurseAccountBean.AppInfo!=null){
                                    newVersion();
                                    return;
                                }
                                toDivision();
//                                AlertDialog builder1 = new AlertDialog.Builder(LoginActivity.this).create();
//                                builder1.getWindow().setGravity(Gravity.CENTER);
//                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                                builder.setTitle("提示！").setMessage("检测到有新版本").setNegativeButton("更新",new OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
////                                        Environment.getExternalStorageDirectory();
////                                        FileHelper.appSDPath="/mnt/sdcard";
//                                        FileHelper.appSDPath="/mnt/internal_sd";
//                                        UpdateDialgoFragment.newInstance(RestClient.BASE_URL + "Update/android/apks/newInfusion-fugao-release-v1.7.apk",
//                                                FileHelper.appSDPath + "/" + "newInfusion-fugao-release-v1.6.apk").show(getFragmentManager(),
//                                                "updateApp");
//                                    }
//                                }).setPositiveButton("残忍拒绝",new OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        if (!oldHostName.equals("")
//                                                && oldHostName.equals(currentHostName)) {
//                                            ishasDivision(false);
//                                        } else {
//                                            XmlDB.getInstance(LoginActivity.this)
//                                                    .saveKey(hostNameKey,
//                                                            currentHostName);
//                                            ishasDivision(true);
//                                        }
//                                    }
//                                }).create();

                            }


                        }
                    }

                    @Override
                    public void onFailure(int code, Throwable error, String content) {
                        Log.e("user", content);
                        if (isInterruptByUser) return;
                        String msg = "";
                        if (content.contains("密码错误")) {
                            msg = "密码错误";
                        } else if (content.contains("密码过期")) {
                            msg = "密码过期,请修改密码";
                        } else if (content.contains("该账户不存在")) {
                            msg = "该账户不存在";
                        } else if (content.contains("账户禁用")) {
                            msg = "账户禁用";
                        } else if (content.contains("账户过期")) {
                            msg = "账户过期";
                        } else {
                            msg = "登录失败，请重试";
                        }
                        progressDialog.dismiss();
                        UIHepler.showToast(LoginActivity.this, msg);


                    }
                }, currentActivity);
            } else {
                Toast.makeText(LoginActivity.this, "无网络连接", Toast.LENGTH_SHORT).show();
                initOffile();
            }
        } else {
            Toast.makeText(LoginActivity.this, validateResult, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 新增自动更新
     */
    private void newVersion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("提示！").setMessage("检测到有新版本").setNegativeButton("更新",new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                                        Environment.getExternalStorageDirectory();
                                        FileHelper.appSDPath="/mnt/sdcard";
//                        FileHelper.appSDPath="/mnt/internal_sd";
                        UpdateDialgoFragment.newInstance(RestClient.BASE_URL + nurseAccountBean.AppInfo.Path,
                                FileHelper.appSDPath + "/" + nurseAccountBean.AppInfo.Name).show(getFragmentManager(),
                                "updateApp");
                    }
                }).setPositiveButton("残忍拒绝",new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toDivision();
                    }
                     }).create();
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 跳转到病区
     */
    private void toDivision(){
        if (!oldHostName.equals("")
                && oldHostName.equals(currentHostName)) {
            ishasDivision(false);
        } else {
            XmlDB.getInstance(LoginActivity.this)
                    .saveKey(hostNameKey,
                            currentHostName);
            ishasDivision(true);
        }
    }
    public void closeKeyboard() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initSetting();
    }

    public void initSetting() {

        ip = XmlDB.getInstance(LoginActivity.this).getKeyString("ip", "");
        port = XmlDB.getInstance(LoginActivity.this).getKeyString("port", "");
        currentHostName = ip + ":" + port;

        String defalutSSID = PullXMLTools.parseXml(this, Constant.DEFAUL_CONFIG_XML, "ssid");
        String ssid = XmlDB.getInstance(this).getKeyString("ssid", defalutSSID);


        String wifeTypeString = PullXMLTools.parseXml(this, Constant.DEFAUL_CONFIG_XML,
                "wife_type");

        String defaultPassword = StringUtils.getString(PullXMLTools.parseXml(this,
                Constant.DEFAUL_CONFIG_XML, "password"));
        DES des = new DES("fugao_moible_wife");
        String password = XmlDB.getInstance(this).getKeyString("password", des.decrypt(defaultPassword));
//
//        BaseConstant.ssid = ssid;
//
//        /**
//         * 无线网加密类型
//         */
//        BaseConstant.wifiType = Integer.parseInt(wifeTypeString);
//        BaseConstant.password = password;

        /**
         * 设置界面填写设备名称。
         */
        String device_name = XmlDB.getInstance(this).getKeyString("android_device_name", "Android");
        android_advice_name.setText(device_name);

    }

}
