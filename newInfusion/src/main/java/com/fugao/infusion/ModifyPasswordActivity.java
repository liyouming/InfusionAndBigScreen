package com.fugao.infusion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fugao.infusion.base.BaseTempleActivity;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.view.UIHepler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: InfusionApps
 * @Location: com.fugao.infusion.ModifyPassword
 * @Description: TODO
 * @author: 胡乐    hule@fugao.com
 * @date: 2015/3/23 17:57
 * @version: V1.0
 */

public class ModifyPasswordActivity extends BaseTempleActivity {
    private static final String TAG = "Fugao-ModifyPassword";
    @InjectView(R.id.title_text_view)
    TextView mTitleTextView;
    @InjectView(R.id.usernametext)
    TextView mUsernametext;
    @InjectView(R.id.username)
    EditText mUsername;
    @InjectView(R.id.passwordText1)
    TextView mPasswordText1;
    @InjectView(R.id.oldassword)
    EditText mOldassword;
    @InjectView(R.id.passwordText2)
    TextView mPasswordText2;
    @InjectView(R.id.newPassword)
    EditText mNewPassword;
    @InjectView(R.id.login_textview)
    TextView mLoginTextview;
    @InjectView(R.id.excute)
    LinearLayout mExcute;
    @InjectView(R.id.login_content)
    LinearLayout mLoginContent;
    @InjectView(R.id.confirmNewPassword)
    TextView mConfirmNewPassword;

    private String port;
    private String ip;
    private String currentHostName = "";
    private ProgressDialog progressDialog;
    /**
     * 用户是否取消修改密码
     */
    private boolean isInterruptByUser = false;

    @Override
    public void setContentView() {
        setContentView(R.layout.modify_password);
    }

    @Override
    public void initView() {
        progressDialog = new ProgressDialog(ModifyPasswordActivity.this);
        progressDialog.setMessage("正在修改中...");
        progressDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置点击进度对话框外的区域对话框不消失
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                isInterruptByUser = true;
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        mExcute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameStr = mUsername.getText().toString();
                String oldPasswordStr = mOldassword.getText().toString();
                String newPasswordStr = mNewPassword.getText().toString();
                String confirmNewPassword = mConfirmNewPassword.getText().toString();
                validateLogin(userNameStr, oldPasswordStr, newPasswordStr, confirmNewPassword);
            }
        });
        mTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 验证工号 密码
     *
     * @param userNameStr
     * @param oldPasswordStr
     * @param newPasswordStr
     * @return
     */
    private void validateLogin(String userNameStr, String oldPasswordStr, String newPasswordStr,String confirmNewPassword) {
        ip = XmlDB.getInstance(ModifyPasswordActivity.this).getKeyString("ip", "");
        port = XmlDB.getInstance(ModifyPasswordActivity.this).getKeyString("port", "");
        if ("".equals(ip)) {
            UIHepler.showToast(this, "ip地址不能为空！");
            return;
        }
        if ("".equals(port)) {
            UIHepler.showToast(this, "端口号不能为空！");
            return;
        }
        currentHostName = ip + ":" + port;
        RestClient.BASE_URL = "http://" + currentHostName.trim() + "/";
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);//加载动画资源文件
        if (StringUtils.StringIsEmpty(userNameStr)
                || (StringUtils.StringIsEmpty(userNameStr) && StringUtils.StringIsEmpty(oldPasswordStr))
                || (StringUtils.StringIsEmpty(userNameStr) && StringUtils.StringIsEmpty(newPasswordStr))
                || (StringUtils.StringIsEmpty(userNameStr) && StringUtils.StringIsEmpty(newPasswordStr) && StringUtils.StringIsEmpty(newPasswordStr))) {
            mUsername.setText("");
            mUsername.setHintTextColor(Color.RED);
            mUsername.setHint("工号不能为空!");
            mUsername.startAnimation(shake);
            mOldassword.setHint("");
            mNewPassword.setHint("");
            return;
        }
        if (!StringUtils.StringIsEmpty(userNameStr)&&StringUtils.StringIsEmpty(oldPasswordStr)
               ){
            mOldassword.setText("");
            mOldassword.setHintTextColor(Color.RED);
            mOldassword.setHint("原密码不能为空!");
            mOldassword.startAnimation(shake);
            mUsername.setHint("");
            mNewPassword.setHint("");
            return;
        }
        if (StringUtils.StringIsEmpty(newPasswordStr)&&!StringUtils.StringIsEmpty(userNameStr)&&!StringUtils.StringIsEmpty(oldPasswordStr)
                ) {
            mNewPassword.setText("");
            mNewPassword.setHintTextColor(Color.RED);
            mNewPassword.setHint("新密码不能为空!");
            mNewPassword.startAnimation(shake);
            mUsername.setHint("");
            mOldassword.setHint("");
            mConfirmNewPassword.setHint("");
            return;
        }
        RegisterPassword(newPasswordStr);
        if(!TextUtils.equals(newPasswordStr,confirmNewPassword)){
            mConfirmNewPassword.setText("");
            mConfirmNewPassword.setHintTextColor(Color.RED);
            mConfirmNewPassword.setHint("两次输入的密码不一致！");
            return;
        }
        progressDialog.setMessage("正在修改密码...");
        String url = InfoApi.modifyPassword(userNameStr, oldPasswordStr, newPasswordStr);
        RestClient.get(url,new BaseAsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progressDialog.show();
            }

            @Override
            public void onSuccess(int i, String s) {
                progressDialog.dismiss();
                if (s.contains("修改成功")) {
                    Toast.makeText(ModifyPasswordActivity.this, "修改密码成功！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                progressDialog.dismiss();
                if (s.contains("密码错误")) {
                    Toast.makeText(ModifyPasswordActivity.this, "原密码错误！", Toast.LENGTH_SHORT).show();
                } else if (s.contains("账户不存在")) {
                    Toast.makeText(ModifyPasswordActivity.this, "账户不存在！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ModifyPasswordActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 密码规则验证
     * @param newPasswordStr
     */
    private void RegisterPassword(String newPasswordStr) {
        Pattern pattern = Pattern.compile("^\\w+$\n");
        Matcher matcher = pattern.matcher(newPasswordStr);
        if(!matcher.matches()){
            mNewPassword.setHintTextColor(Color.RED);
            mNewPassword.setHint("请输入英文和数字！");
        }
    }

    @Override
    public void initIntent() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
