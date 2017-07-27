package com.fugao.infusion.paiyao;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseTempleActivity;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.model.NurseAccountModel;
import com.fugao.infusion.setting.RoleChoiceActivity;
import com.fugao.infusion.utils.PullXMLTools;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.view.UIHepler;
import org.apache.commons.lang3.StringUtils;

/**
 * 核对人登录界面
 * Created by 胡绪庆 on 2015/9/9.
 */
public class CheckActivity extends BaseTempleActivity {
    private EditText check_user;
    private EditText check_password;
    private Button login_btn_login;
    private ImageButton btn_close;
    private String user;
    private String password;
    private Activity currentActivity;
    private ProgressDialog progressDialog;
    private NurseAccountModel nurseAccountBean;
    private String hospital;
    @Override
    public void setContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.check_dialog);
    }

    @Override
    public void initView() {
        currentActivity=CheckActivity.this;
        progressDialog=new ProgressDialog(currentActivity);
        progressDialog.setMessage("正在核对中...");
        progressDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置点击进度对话框外的区域对话框不消失
        check_user= (EditText) findViewById(R.id.check_user);
        check_password= (EditText) findViewById(R.id.check_password);
        login_btn_login= (Button) findViewById(R.id.login_btn_login);
        btn_close= (ImageButton) findViewById(R.id.login_close_button);
        hospital= PullXMLTools.parseXml(CheckActivity.this, Constant.DEFAUL_CONFIG_XML, "hospital");
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        login_btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void Login() {
        user = check_user.getText().toString();
        password = check_password.getText().toString();
        //判断输入
        if (StringUtils.isEmpty(user)) {
            UIHepler.showToast(currentActivity, "用户名不能为空!");
            return;
        }
        if (StringUtils.isEmpty(password)) {
            UIHepler.showToast(currentActivity, "密码不能为空!");
            return;
        }
        progressDialog.setMessage("正在核对中...");
        progressDialog.show();
        RestClient.login(InfoApi.login(user,password), new BaseAsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, String s) {
                        progressDialog.dismiss();
                        if(i==200){
                            nurseAccountBean= String2InfusionModel.string2NurseAccount(s);
                            if(hospital.equals("ekyy")){
                                XmlDB.getInstance(currentActivity).saveKey("CheckId",nurseAccountBean.Id);
                                LocalSetting.CurrentCheck = nurseAccountBean;
                                Intent intent=new Intent();
                                intent.setClass(currentActivity,PaiYaoActivity.class);
                                startActivity(intent);
                                currentActivity.finish();
                                if(RoleChoiceActivity.finishHandler!=null){
                                    RoleChoiceActivity.finishHandler.sendEmptyMessage(1);
                                }
                            }else{
                                if(nurseAccountBean.UserName.equals(LocalSetting.CurrentAccount.UserName)){
                                    UIHepler.showToast(CheckActivity.this,"核对人和操作人不可以是同一个人");
                                }else{
                                    LocalSetting.CurrentCheck = nurseAccountBean;
                                    CheckActivity.this.setResult(10);
                                    CheckActivity.this.finish();
                                }
                            }
                        }

                    }

                    @Override
                    public void onFailure(int i, Throwable throwable, String content) {
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
                        progressDialog.dismiss();
                        UIHepler.showToast(currentActivity,msg);
                    }
                },currentActivity
        );

    }
        @Override
    public void initIntent() {

    }
}
