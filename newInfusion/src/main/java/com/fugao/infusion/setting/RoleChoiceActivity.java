package com.fugao.infusion.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseTempleActivity;
import com.fugao.infusion.chuaici.ChuanCiActivity;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.constant.RoleCategory;
import com.fugao.infusion.model.MenuListModel;
import com.fugao.infusion.paiyao.CheckActivity;
import com.fugao.infusion.peiye.PeiYeActivity;
import com.fugao.infusion.utils.InfusionHelper;
import com.fugao.infusion.utils.PullXMLTools;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.xinhua.XHXunshiActivity;
import com.fugao.infusion.xunshi.XunshiActivity;
import com.jasonchen.base.utils.ViewUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * TODO 功能选择界面
 */
public class RoleChoiceActivity extends BaseTempleActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final int DEFAULT_DIVISION = 314;
    @InjectView(R.id.fun_paiyao)
    Button mFunPaiyao;
    @InjectView(R.id.fun_peiye)
    Button mFunPeiye;
    @InjectView(R.id.fun_chuanci)
    Button mFunChuanci;
    @InjectView(R.id.fun_shuye)
    Button mFunShuye;
    @InjectView(R.id.zhong_shuye)
    Button mZhongShuye;
    @InjectView(R.id.container)
    LinearLayout mContainer;
    private int flag=1;
    public static Handler finishHandler;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_role_choice);
    }

    @Override
    public void initView() {
        mFunPaiyao.setOnClickListener(this);
        mFunPeiye.setOnClickListener(this);
        mFunChuanci.setOnClickListener(this);
        mFunShuye.setOnClickListener(this);
        mZhongShuye.setOnClickListener(this);
        lauchPeiYeOrNot();

    }

    @Override
    public void initData() {
        List<MenuListModel> menuListModels = LocalSetting.CurrentAccount.Competence.get(0).MenuList;
        if(menuListModels ==null || menuListModels.size() ==0){
            Toast.makeText(RoleChoiceActivity.this,"当前账户没有操作权限",Toast.LENGTH_SHORT).show();
            return;
        }
        String string = PullXMLTools.parseXml(context, Constant.DEFAUL_CONFIG_XML, "hospital");
        for(MenuListModel each :menuListModels){
            String moduleName = each.ModuleName;
            if(string.equals("zzey")){
//                if(each.ModuleName.contains("排药") && !each.ModuleName.contains("重症")){
//                    mFunPaiyao.setVisibility(View.VISIBLE);
//                    continue;
//                }
                if(each.ModuleName.contains("穿刺") && !each.ModuleName.contains("重症")){
                    mFunChuanci.setVisibility(View.VISIBLE);
                    continue;
                }
                if(each.ModuleName.contains("巡回") &&!each.ModuleName.contains("重症")){
                    mFunShuye.setVisibility(View.VISIBLE);
                    continue;
                }
//                if(each.ModuleName.contains("重症")){
//                    mZhongShuye.setVisibility(View.VISIBLE);
//                    continue;
//                }
            }else if(string.equals("xhyy")){
                mFunPeiye.setVisibility(View.VISIBLE);
                if(each.ModuleName.contains("穿刺") && !each.ModuleName.contains("重症")){
                    mFunChuanci.setVisibility(View.VISIBLE);
                    continue;
                }
                if(each.ModuleName.contains("巡回") &&!each.ModuleName.contains("重症")){
                    mFunShuye.setVisibility(View.VISIBLE);
                    continue;
                }
//                if(each.ModuleName.contains("重症")){
//                    mZhongShuye.setVisibility(View.VISIBLE);
//                    continue;
//                }
            }else{
                if(each.ModuleName.contains("排药") && !each.ModuleName.contains("重症")){
                    mFunPaiyao.setVisibility(View.VISIBLE);
                    continue;
                }
                if(each.ModuleName.contains("穿刺") && !each.ModuleName.contains("重症")){
                    mFunChuanci.setVisibility(View.VISIBLE);
                    continue;
                }
                if(each.ModuleName.contains("巡回") &&!each.ModuleName.contains("重症")){
                    mFunShuye.setVisibility(View.VISIBLE);
                    continue;
                }
                if(each.ModuleName.contains("重症")){
                    mZhongShuye.setVisibility(View.VISIBLE);
                    continue;
                }
            }
        }

    }

    @Override
    public void initListener() {
        finishHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                       RoleChoiceActivity.this.finish();
                        break;
            }
        }

    };}

    @Override
    public void initIntent() {

    }

    private void lauchPeiYeOrNot() {
        boolean set_peiye = XmlDB.getInstance(this).getKeyBooleanValue("canPeiye", false);
        if (set_peiye) {
            ViewUtils.setVisible(findViewById(R.id.fun_peiye));
        }
    }

    @Override
    public void onClick(View v) {

        XmlDB xmldb = XmlDB.getInstance(this);
        int appflag = -1;
        switch (v.getId()) {
            case R.id.fun_paiyao:
                appflag = 0;
                break;
            case R.id.fun_peiye:
                appflag = 1;
                break;
            case R.id.fun_chuanci:
                appflag = 2;
                break;
            case R.id.fun_shuye:
                appflag = 3;
                break;
            case R.id.zhong_shuye:
                appflag = 4;
                break;

            default:
                break;
        }
        xmldb.saveKey("appflag", appflag);
        LocalSetting.RoleIndex = appflag;
        Constant.lastUpdateTime = "0";//每次点击进入角色之前先将最近的时间设置为0
        Intent intent = new Intent();
        switch (LocalSetting.RoleIndex) {
            case 0:
                intent.setClass(RoleChoiceActivity.this, CheckActivity.class);
                startActivity(intent);

                break;
            case 1:
                intent.setClass(RoleChoiceActivity.this, PeiYeActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case 2:
//                intent.setClass(RoleChoiceActivity.this, ChoiceChuanCiQuActivity.class);
                intent.setClass(RoleChoiceActivity.this, ChuanCiActivity.class);
                this.finish();
                startActivity(intent);
                break;
            case 3:
//                intent.setClass(RoleChoiceActivity.this, SettingDefaultDivisionActivity.class);
//                intent.setClass(RoleChoiceActivity.this, XunshiActivity.class);
                intent.setClass(RoleChoiceActivity.this, XHXunshiActivity.class);
                intent.putExtra("dept_id", "100001");
                intent.putExtra("userName", LocalSetting.CurrentAccount.UserName);
                this.finish();
                startActivity(intent);
                break;
            case 4:
                intent.setClass(RoleChoiceActivity.this, SettingDefaultDivisionActivity.class);
                intent.putExtra("dept_id", "100001");
                intent.putExtra("userName", LocalSetting.CurrentAccount.UserName);
                this.finish();
                startActivity(intent);
                break;
        }

    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    public boolean openSelectedInfusionArea(String userName, String deptId) {
        Boolean openChoice;
        if (RoleCategory.SHUYE.getKey() == LocalSetting.RoleIndex) {
            openChoice = true;
            Intent intent = new Intent();
            intent.setClass(RoleChoiceActivity.this, SettingDefaultDivisionActivity.class);
            //            intent.putExtra("default_division", DEFAULT_DIVISION);
            intent.putExtra("dept_id", deptId);
            intent.putExtra("userName", userName);
            //            startActivityForResult(intent, 0);
            startActivity(intent);
        } else if (RoleCategory.CHUANCI.getKey() == LocalSetting.RoleIndex) {
            openChoice = true;
            Intent intent = new Intent();
            intent.setClass(RoleChoiceActivity.this, ChoiceChuanCiQuActivity.class);
            startActivity(intent);
        } else {
            openChoice = false;
        }
        return openChoice;
    }
    //    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        super.onActivityResult(requestCode, resultCode, data);
    //         if (resultCode == DEFAULT_DIVISION) {
    //            UIHelper.showWarningDialog(ChooseActivity.this, "选择跳转");
    //             closeActivity();
    //        }
    //    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            InfusionHelper.exite(RoleChoiceActivity.this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.inject(this);
    }
}
