package com.fugao.infusion.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseActivity;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.ResourceUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 扫描核对错误设置
 *
 * @ClassName: SettingDefaultDivisionActivity
 * @Description: TODO 打印模式设置
 * @author: 葛立平 geliping@fugao.com
 * @date: 2014年1月2日 下午4:34:19
 */
public class PrintModeSettingActivity extends BaseActivity {
    /**
     * 区域选择框
     */
    private ListView setting_division_listview;
    /**
     * 设置的选项
     */
    private List<String> scanConfrimSettingList;
    /**
     * 确定按钮
     */
    private Button setting_division_confirm;

    private TextView setting_dialog_title;
    /**
     * 选中的设置
     */
    private String settingContent;
    /**
     * 列表适配器
     */
    private PrintModeSettingAdapter printModeSettingAdapter;

    /**
     * 打印模式的选择  蓝牙打印 网络打印
     */
    private String[] settingList;


    @Override
    protected void setRootView() {
        setContentView(R.layout.setting_confirm);
        setWindow();
    }

    @Override
    protected void initView() {
        setting_dialog_title = (TextView) findViewById(R.id.setting_dialog_title);
        setting_division_listview = (ListView) findViewById(R.id.setting_division_listview);
        setting_division_confirm = (Button) findViewById(R.id.setting_division_confirm);

    }

    /**
     * 设置窗口
     */
    private void setWindow() {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.56); // 高度设置为屏幕的0.9
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.9
        p.y = -10;
        getWindow().setAttributes(p); // 设置生效
    }
    @Override
    protected void initIntent() {

    }
    @Override
    protected void initData() {
        settingList = ResourceUtils.getResouce4Arrays(PrintModeSettingActivity.this, R.array.print_mode);
        scanConfrimSettingList =  Arrays.asList(settingList);
        printModeSettingAdapter = new PrintModeSettingAdapter(
            PrintModeSettingActivity.this, scanConfrimSettingList);
        setting_dialog_title.setText("打印模式设置");
        int position = XmlDB.getInstance(PrintModeSettingActivity.this).getKeyIntValue("pirntPosition", 0);
        printModeSettingAdapter.selectItem(position);
        settingContent = scanConfrimSettingList.get(position);
        setting_division_listview.setAdapter(printModeSettingAdapter);

    }

    @Override
    protected void initListener() {
        setting_division_listview
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        settingContent = scanConfrimSettingList.get(arg2);
                        printModeSettingAdapter.selectItem(arg2);
                        XmlDB.getInstance(PrintModeSettingActivity.this).saveKey("pirntPosition",arg2);
                    }
                });
        setting_division_confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (settingContent != null) {
                    //TODO 保存选择的设置
                    XmlDB.getInstance(PrintModeSettingActivity.this).saveKey("printSetting",settingContent);
                    finish();
                } else {
                    AlertDialog.Builder  alertDialog = new AlertDialog.Builder(PrintModeSettingActivity.this).setTitle("提醒！")
                            .setMessage("请选择打印模式")
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
    }
}
