package com.fugao.infusion.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseActivity;
import com.fugao.infusion.chuaici.ChuanCiActivity;
import com.fugao.infusion.constant.ChuanCiApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.model.InfusionAreaBean;
import com.fugao.infusion.paiyao.PaiYaoActivity;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.xunshi.XunshiActivity;
import com.fugao.infusion.zhongji.ZhongjiXunshiActivity;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 选择默认病区
 *
 * @ClassName: SettingDefaultDivisionActivity
 * @Description: TODO 输液巡视界面选择区域
 * @author: 葛立平 geliping@fugao.com
 * @date: 2014年1月2日 下午4:34:19
 */
public class SettingDefaultDivisionActivity extends BaseActivity {
    /**
     * 区域选择框
     */
    private ListView setting_division_listview;
    /**
     * 区域集合
     */
    private ArrayList<InfusionAreaBean> infusionAreaBeanList = null;
    /**
     * 确定按钮
     */
    private Button setting_division_confirm;
    /**
     * 选中的区域
     */
    private InfusionAreaBean currentInfusionArea;
    /**
     * 列表适配器
     */
    private SettingDefaultDivisionAdapter defaultDivisionAdapter;
    /**
     * 返回值
     */
    private int DEFAULT_DIVISION;
    // 加载progressbar
    private RelativeLayout updateProgressbarLayout;
    private ProgressBar updateProgressbar;
    private Button restartButton;
    private TextView nothingTextView;
    private String deptID;
    private String userName;

    private String currentRole;//当前角色

    @Override
    protected void setRootView() {
        setContentView(R.layout.setting_default_infusion_area);
        setWindow();
    }

    @Override
    protected void initView() {
        setting_division_listview = (ListView) findViewById(R.id.setting_division_listview);
        setting_division_confirm = (Button) findViewById(R.id.setting_division_confirm);
        updateProgressbarLayout = (RelativeLayout) findViewById(R.id
                .specimens_collection_progressbar_layout);
        updateProgressbar = (ProgressBar) findViewById(R.id.update_progressbar);
        restartButton = (Button) findViewById(R.id.update_fail_restart);
        nothingTextView = (TextView) findViewById(R.id.update_nothing);
    }

    /**
     * 设置窗口
     */
    private void setWindow() {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.93); // 高度设置为屏幕的0.9
        p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9
        p.y = -10;
        getWindow().setAttributes(p); // 设置生效
    }

    @Override
    protected void initIntent() {
        Intent intent = getIntent();
        deptID = intent.getStringExtra("dept_id");
        userName = intent.getStringExtra("userName");
        //        DEFAULT_DIVISION = intent.getIntExtra("default_division", 0);
    }

    @Override
    protected void initData() {
        currentRole = LocalSetting.CurrentAccount.Competence.get(0).Role;
        infusionAreaBeanList = new ArrayList<InfusionAreaBean>();
        defaultDivisionAdapter = new SettingDefaultDivisionAdapter(
                SettingDefaultDivisionActivity.this, infusionAreaBeanList);
        setting_division_listview.setAdapter(defaultDivisionAdapter);
        updateData();
    }

    @Override
    protected void initListener() {
        setting_division_listview
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                        SettingDefaultDivisionAdapter.ViewHodler holder = (SettingDefaultDivisionAdapter.ViewHodler) arg1.getTag();
                        //改变checkBox的状态
                        holder.item_cb.toggle();
                        // 将CheckBox的选中状况记录下来
                        SettingDefaultDivisionAdapter.getIsSelected().put(arg2, holder.item_cb.isChecked());
                        currentInfusionArea = infusionAreaBeanList.get(arg2);
                        //                        if (defaultDivisionAdapter != null) {
                        //                            defaultDivisionAdapter.selectItem(arg2);
                        //                        }
                    }
                });
        setting_division_confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (currentInfusionArea != null) {
                    //TODO 保存选择的区域
                    XmlDB.getInstance(SettingDefaultDivisionActivity.this)
                            .saveKey(
                                    userName,
                                    StringUtils
                                            .getString(currentInfusionArea.AreaCode)
                            );
                    XmlDB.getInstance(SettingDefaultDivisionActivity.this)
                            .saveKey(
                                    userName + "name",
                                    StringUtils
                                            .getString(currentInfusionArea.AreaName)
                            );
                    XmlDB.getInstance(SettingDefaultDivisionActivity.this)
                            .saveKey(
                                    "deptID",
                                    StringUtils
                                            .getString(currentInfusionArea.DepartmentId)
                            );
                    HashMap<Integer, Boolean> hashMap = SettingDefaultDivisionAdapter.getIsSelected();
                    ArrayList<String> areaIdList = new ArrayList<String>();
                    Set<Integer> integers = hashMap.keySet();
                    for (Iterator<Integer> iterator = integers.iterator(); iterator.hasNext(); ) {
                        Integer next = iterator.next();
                        Boolean isSelect = hashMap.get(next);
                        if (isSelect) {
                            areaIdList.add(String
                                    .valueOf(infusionAreaBeanList.get(next).Id));
                        }
                    }
                    //                    for(int i=0; i<hashMap.size(); i++){
                    //                        int id = infusionAreaBeanList.get(i).Id;
                    //                        Boolean isSelect = hashMap.get(i);
                    //                        if(isSelect){
                    //                            areaIdList.add(String
                    //                                    .valueOf(infusionAreaBeanList.get(i).Id));
                    //                        }
                    //                    }
                    if (areaIdList.size() == 1) {
                        XmlDB.getInstance(SettingDefaultDivisionActivity.this)
                                .saveKey(
                                        "areaID",
                                        String.valueOf(currentInfusionArea.Id)
                                );
                    } else {
                        String areaGroup;
                        Object[] objects = areaIdList.toArray();
                        StringBuffer str_buff = new StringBuffer();
                        for (int i = 0, len = objects.length; i < len; i++) {
                            str_buff.append(String.valueOf(objects[i]));
                            if (i < len - 1) str_buff.append("_");
                        }
                        areaGroup = str_buff.toString();
                        XmlDB.getInstance(SettingDefaultDivisionActivity.this)
                                .saveKey(
                                        "areaID",
                                        StringUtils
                                                .getString(areaGroup)
                                );
                    }
                    LocalSetting.RoleIndex =
                            XmlDB.getInstance(SettingDefaultDivisionActivity.this).getKeyIntValue("appflag", 0);
                    Intent intent = new Intent();
                    switch (LocalSetting.RoleIndex) {
                        case 1:
                            intent.setClass(SettingDefaultDivisionActivity.this, PaiYaoActivity.class);
                            break;
                        case 2:
                            intent.setClass(SettingDefaultDivisionActivity.this, ChuanCiActivity.class);
                            break;
                        case 3:
                            intent.setClass(SettingDefaultDivisionActivity.this, XunshiActivity.class);
                            break;
                        case 4:
                            intent.setClass(SettingDefaultDivisionActivity.this, ZhongjiXunshiActivity.class);
                            break;
                    }
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingDefaultDivisionActivity.this).setTitle("提醒！")
                            .setMessage("请选择您所在输液区")
                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.create().show();
                }
            }
        });

        restartButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                updateData();
            }
        });
    }

    private void updateData() {
        setProgressbarLayout(0);
        if (StringUtils.StringIsEmpty(deptID)) {
            deptID = XmlDB.getInstance(SettingDefaultDivisionActivity.this).getKeyString("deptID", "100001");
        }

        RestClient.get(ChuanCiApi.getInfusionAreaByDeptID(deptID), new BaseAsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int i, String s) {
                List<InfusionAreaBean> infusionAreaBeans = String2InfusionModel.string2InfusionAreaBeans(s);
                if (infusionAreaBeans != null
                        && infusionAreaBeans.size() > 0) {
                    infusionAreaBeanList.clear();
                    for (InfusionAreaBean each : infusionAreaBeans) {
                        if (each.Type == 1) {
                            if (currentRole.equals("a3")) {
                                if ("重症留观室".equals(each.AreaName) || "急诊留观室".equals(each.AreaName)) {
                                    infusionAreaBeanList.add(each);
                                }
                            } else if (currentRole.equals("a4")) {
                                if (LocalSetting.RoleIndex == 4) {
                                    if ("重症留观室".equals(each.AreaName) || "急诊留观室".equals(each.AreaName)) {
                                        infusionAreaBeanList.add(each);
                                    }
                                }else {
                                    if (!"重症留观室".equals(each.AreaName) && !"急诊留观室".equals(each.AreaName)) {
                                        infusionAreaBeanList.add(each);
                                    }
                                }
                            } else {
                                if (!"重症留观室".equals(each.AreaName) && !"急诊留观室".equals(each.AreaName)) {
                                    infusionAreaBeanList.add(each);
                                }
                            }
                        }
                    }
                    //                    infusionAreaBeanList.addAll(infusionAreaBeans);
                    //                    infusionAreaBeanList = (ArrayList<InfusionAreaBean>) infusionAreaBeans;
                    setProgressbarLayout(3);
                    defaultDivisionAdapter
                            .changeDataSource((ArrayList) infusionAreaBeanList);
                } else if (infusionAreaBeans == null) {
                    setProgressbarLayout(1);
                } else {
                    setProgressbarLayout(2);
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                setProgressbarLayout(1);
            }
        });
    }

    /**
     * @param choice 0正在加载，1加载失败，2无数据，3加载成功，有数据
     * @Title: setProgressbarLayout
     * @Description: TODO
     * @return: void
     */
    public void setProgressbarLayout(int choice) {
        ViewUtils.setGone(setting_division_listview);
        ViewUtils.setGone(updateProgressbarLayout);
        ViewUtils.setGone(updateProgressbar);
        ViewUtils.setGone(restartButton);
        ViewUtils.setGone(nothingTextView);
        if (choice == 0) {// 正在加载
            ViewUtils.setVisible(updateProgressbarLayout);
            ViewUtils.setVisible(updateProgressbar);
        } else if (choice == 1) {// 加载失败
            ViewUtils.setVisible(updateProgressbarLayout);
            ViewUtils.setVisible(restartButton);
        } else if (choice == 2) {// 无数据
            ViewUtils.setVisible(updateProgressbarLayout);
            ViewUtils.setVisible(nothingTextView);
        } else if (choice == 3) {// 加载成功，有数据
            ViewUtils.setVisible(setting_division_listview);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
