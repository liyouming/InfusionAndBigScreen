package com.fugao.infusion.setting;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fugao.infusion.InfusionApplication;
import com.fugao.infusion.LoginActivity;
import com.fugao.infusion.R;
import com.fugao.infusion.base.BaseTempleActivity;
import com.fugao.infusion.constant.Constant;
import com.fugao.infusion.constant.InfoApi;
import com.fugao.infusion.model.InfusionAreaBean;
import com.fugao.infusion.utils.String2InfusionModel;
import com.fugao.infusion.utils.XmlDB;
import com.jasonchen.base.utils.BaseAsyncHttpResponseHandler;
import com.jasonchen.base.utils.JacksonHelper;
import com.jasonchen.base.utils.RestClient;
import com.jasonchen.base.utils.ViewUtils;
import com.jasonchen.base.view.UIHepler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Do one thing at a time, and do well!
 *
 * @Prject: FastApps
 * @Location: com.android.fastinfusion.ui.activity.SyncActivity
 * @Description: TODO 同步数据
 * @author: 蒋光锦  jiangguangjin@fugao.com
 * @date: 2014/12/5 16:49
 * @version: V1.0
 */

public class SyncActivity extends BaseTempleActivity {
    private  boolean flag = true;
    private InfusionApplication infoapp;
    /**
     * 准备要访问数据的链接
     */
    public ArrayList<String> isPrepareConnectString;
    /**
     * 进度条
     */
    private ProgressBar updateInfoProgressBar;
    /**
     * 控制按钮
     */
    private Button sync_dialog_re_connect, sync_dialog_close;
    /**
     * 加载显示信息
     */
    private TextView textVersionMsg;
    /**
     * progressbar 当前长度
     */
    private int currentBarLength = 0;
    /**
     * 加载处理Handler
     */
    private Handler updateProgressHandler;

    /**
     * 加载成功标志
     */
    public static final int UPDATE_SUCCESS = 1;
    /**
     * 添加执行的方法名称
     */
    private void setMethodName(){
//        isPrepareConnectString.add(Constant.GETDEPARTMENTS); //科室
//        isPrepareConnectString.add(Constant.GETAREALIST); //区域列表
    }
    private void initHandler(){
        // TODO Auto-generated method stub
        updateProgressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_SUCCESS:
                        currentBarLength++;
                        updateInfoProgressBar.setProgress(currentBarLength);
                        getDataFromServer();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void setContentView() {
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_sync);
    }

    @Override
    public void initView() {
        setTitle("数据加载中!");
        updateInfoProgressBar = (ProgressBar) findViewById(R.id.updateInfoProgressBar);
        sync_dialog_re_connect = (Button) findViewById(R.id.sync_dialog_re_connect);
        sync_dialog_close = (Button) findViewById(R.id.sync_dialog_close);
        textVersionMsg = (TextView) findViewById(R.id.textVersionMsg);
        ViewUtils.setGone(sync_dialog_close);
        ViewUtils.setGone(sync_dialog_re_connect);
        ViewUtils.setVisible(updateInfoProgressBar);
        ViewUtils.setVisible(textVersionMsg);
    }

    @Override
    public void initData() {
        infoapp = (InfusionApplication) getApplication();
        isPrepareConnectString = new ArrayList<String>();
        setMethodName();
        updateInfoProgressBar.setMax(isPrepareConnectString.size());
        updateInfoProgressBar.setProgress(0);
        initHandler();
       getDataFromServer();
    }

    @Override
    public void initListener() {
        sync_dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sync_dialog_re_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reconnect();
            }
        });
//        super.initListener();
    }

    @Override
    public void initIntent() {

    }

    /**
     * 得到科室
     */
    public void getDepartments(){
        textVersionMsg.setText("正在加载数据...");

        RestClient.get(InfoApi.getInfusionAreadeptId(),new BaseAsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int i, String s) {
               ArrayList<InfusionAreaBean> infusionAreaBeans= String2InfusionModel.string2InfusionAreaBeans(s);
                if (infusionAreaBeans != null
                        && infusionAreaBeans.size() > 0) {
                    String infusionAreas = JacksonHelper.model2String(infusionAreaBeans);
                    XmlDB.getInstance(context).saveKey("infusionAreaList", infusionAreas);
                    XmlDB.getInstance(context).saveKey("deptID", infusionAreaBeans.get(0).DepartmentId);
                    requestSuccess(Constant.GETDEPARTMENTS);
                } else {
                    UIHepler.showToast(context,"科室列表为空");
                }
            }

            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                requestFail();
            }
        });

    }

    public void getAreaList(){
//        textVersionMsg.setText("正在加载区域列表");
        textVersionMsg.setText("正在加载数据...");
        String deptID =  XmlDB.getInstance(context).getKeyString("deptID","");
        RestClient.get(InfoApi.getInfusionAreaByDeptID(deptID),new BaseAsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onSuccess(int i, String s) {
                ArrayList<InfusionAreaBean> infusionAreaBeans = String2InfusionModel.string2InfusionAreaBeans(s);

                if (infusionAreaBeans != null
                        && infusionAreaBeans.size() > 0) {
                    String infusionAreas = JacksonHelper.model2String(infusionAreaBeans);
                    XmlDB.getInstance(context).saveKey("infusionAreaList", infusionAreas);
                    infoapp.setInfusionAreaBeanList(infusionAreaBeans);
                    XmlDB.getInstance(context).saveKey("depart", infusionAreaBeans.get(0).DepartmentId);
                    requestSuccess(Constant.GETAREALIST);
                } else {
//                    UIHelper.showToast("区域列表为空");
                    requestFail();
                }
            }
            @Override
            public void onFailure(int i, Throwable throwable, String s) {
                requestFail();
            }
        });

    }
    /**
     * 从服务端获取数据
     *
     * @Title: getDataFromServer
     * @Description: TODO
     * @return: void
     */
    public void getDataFromServer() {
        if (isPrepareConnectString.size() > 0) {
            invokeMethod(isPrepareConnectString.get(0));
        } else {
            finish();
            LoginActivity.loginToMainActivity.sendEmptyMessage(1);
        }
    }
    /**
     * 获取数据失败
     *
     * @Title: requestFail
     * @Description: TODO
     * @return: void
     */
    public void requestFail() {
        ViewUtils.setVisible(sync_dialog_close);
        ViewUtils.setVisible(sync_dialog_re_connect);
        ViewUtils.setGone(updateInfoProgressBar);
        ViewUtils.setVisible(textVersionMsg);
    }
    /**
     * 获取数据成功
     *
     * @param string
     * @Title: requestSuccess
     * @Description: TODO
     * @return: void
     */
    public void requestSuccess(String string) {
        Log.i("sync",string + "--->requestSuccess");
        isPrepareConnectString.remove(string);
        updateProgressHandler.sendEmptyMessage(UPDATE_SUCCESS);
    }
    /**
     * 反射执行方法
     *
     * @param methodName
     * @Title: invokeMethod
     * @Description: TODO
     * @return: void
     */
    public void invokeMethod(String methodName) {
        Class[] cargs = null;
        Object[] inArgs = null;
        Method m;
        try {
            m = SyncActivity.class.getMethod(methodName, cargs);
            m.invoke(this, inArgs);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    /**
     * 重试 获取服务器端的数据
     */
    public void reconnect() {
        updateInfoProgressBar.setProgress(currentBarLength);
        getDataFromServer();
        ViewUtils.setGone(sync_dialog_close);
        ViewUtils.setGone(sync_dialog_re_connect);
        ViewUtils.setVisible(updateInfoProgressBar);
        ViewUtils.setVisible(textVersionMsg);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return false;
    }


}
