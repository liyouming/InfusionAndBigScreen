package com.fugao.infusion.setting;

import android.content.Intent;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseTempleActivity;
import com.fugao.infusion.chuaici.ChuanCiActivity;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.constant.LocalSetting;
import com.fugao.infusion.model.InfusionAreaBean;
import com.fugao.infusion.paiyao.PaiYaoActivity;
import com.fugao.infusion.utils.InfusionUIHelper;
import com.fugao.infusion.utils.XmlDB;
import com.fugao.infusion.xunshi.XunshiActivity;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.JacksonHelper;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.StringUtils;
import com.jasonchen.base.utils.ViewUtils;

import org.codehaus.jackson.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

/**
 *  TODO 进入穿刺界面的选择注射室界面
 */
public class ChoiceChuanCiQuActivity extends BaseTempleActivity {
    private static final String TAG = "Fugao-ChoosePuntrueDept";

    /**
     * 注射室选择框
     */
    private ListView choose_puncture_listview;
    /**
     * 注射室集合
     */
    private List<InfusionAreaBean> infusionAreaBeanList =null;
    /**
     * 确定按钮
     */
    private Button setting_division_confirm;
    /**
     * 选中的注射室
     */
    private InfusionAreaBean currentInfusionArea;
    /**
     * 列表适配器
     */
    private ChoosePunctureAdapter choosePunctureAdapter;

    // 加载progressbar
    private RelativeLayout updateProgressbarLayout;
    private ProgressBar updateProgressbar;
    private Button restartButton;
    private TextView nothingTextView;
    private String deptID;

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
    public void setContentView() {
        setContentView(R.layout.activity_choice_chuan_ci_qu);
        setWindow();
    }

    @Override
    public void initView() {
        choose_puncture_listview = (ListView) findViewById(R.id.punctureListView);
        setting_division_confirm = (Button) findViewById(R.id.setting_division_confirm);
        updateProgressbarLayout = (RelativeLayout) findViewById(R.id
                .specimens_collection_progressbar_layout);
        updateProgressbar = (ProgressBar) findViewById(R.id.update_progressbar);
        restartButton = (Button) findViewById(R.id.update_fail_restart);
        nothingTextView = (TextView) findViewById(R.id.update_nothing);
    }

    @Override
    public void initData() {
        List<InfusionAreaBean> infusionAreaBeans = null;
        String infusionAreaList = XmlDB.getInstance(context).getKeyString("infusionAreaList", null);
            infusionAreaBeans = JacksonHelper.getObjects(StringUtils.getString(infusionAreaList),new TypeReference<List<InfusionAreaBean>>() {});
            deptID = XmlDB.getInstance(context).getKeyString("deptID","100001");
            infusionAreaBeanList = new ArrayList<InfusionAreaBean>();
            infusionAreaBeanList.clear();
            for (InfusionAreaBean each : infusionAreaBeans) {
            if (each.Type == 2) {
                infusionAreaBeanList.add(each);
                }
            }
            choosePunctureAdapter = new ChoosePunctureAdapter(
                    ChoiceChuanCiQuActivity.this, infusionAreaBeanList);
            choose_puncture_listview.setAdapter(choosePunctureAdapter);
            if(infusionAreaBeanList !=null)updateData();
    }

    @Override
    public void initListener() {
        choose_puncture_listview
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
//                        // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
//                        ChoosePunctureAdapter.ViewHodler holder = (ChoosePunctureAdapter.ViewHodler)arg1.getTag();
//                        //改变checkBox的状态
//                        holder.item_cb.toggle();
//                        choosePunctureAdapter.getIsSelected().put(arg2, holder.item_cb.isChecked());
                        currentInfusionArea = infusionAreaBeanList.get(arg2);
                        choosePunctureAdapter.selectItem(arg2);
                    }
                });

        setting_division_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (currentInfusionArea != null) {
                    XmlDB.getInstance(context).saveKey("punctureDeptId",currentInfusionArea.Id);
                    LocalSetting.RoleIndex =
                            XmlDB.getInstance(ChoiceChuanCiQuActivity.this).getKeyIntValue("appflag", 0);
                  //  startActivity(new Intent(ChoiceChuanCiQuActivity.this, MainTabActivity.class));
                   // RoleChoiceActivity.handler.sendEmptyMessage(1);
                    Intent intent=new Intent();
                    switch ( LocalSetting.RoleIndex){
                        case 1:
                            intent.setClass(ChoiceChuanCiQuActivity.this, PaiYaoActivity.class);
                            break;
                        case 2:
                            intent.setClass(ChoiceChuanCiQuActivity.this, ChuanCiActivity.class);
                            break;
                        case 3:
                            intent.setClass(ChoiceChuanCiQuActivity.this, XunshiActivity.class);
                            break;
                    }
                    startActivity(intent);

                    finish();
                } else {
                    InfusionUIHelper.showWarningDialog(context,
                            "请选择您所在穿刺室");
                }
            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                updateData();
            }
        });
    }

    @Override
    public void initIntent() {

    }

    private void updateData() {
        RestClient.get(InfoApi.getInfusionAreaByDeptID(deptID),new BaseAsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                setProgressbarLayout(0);

            }

            @Override
            public void onSuccess(int i, String s) {
                ArrayList<InfusionAreaBean> infusionAreaBeans=new ArrayList<InfusionAreaBean>();
                if (infusionAreaBeans != null
                        && infusionAreaBeans.size() > 0) {
                    infusionAreaBeanList.clear();
                    for (InfusionAreaBean each : infusionAreaBeans) {
                        if (each.Type == 2) {
                            infusionAreaBeanList.add(each);
                        }
                    }
                    setProgressbarLayout(3);
                    choosePunctureAdapter
                            .changeDataSource((ArrayList) infusionAreaBeanList);
                } else if (infusionAreaBeans == null) {
                    setProgressbarLayout(1);
                } else {
                    setProgressbarLayout(3);
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
        ViewUtils.setGone(choose_puncture_listview);
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
            ViewUtils.setVisible(choose_puncture_listview);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
}
